package dev.netherfriends.talk;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.netherfriends.entity.People;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * The dialogue bank: one file per people in {@code data/netherfriends/talk/}, read once from the mod jar.
 * See docs/DIALOGUE.md for the format and {@code tools/check_talk.py} for the checks it must pass.
 */
public final class Dialogue {
    private Dialogue() {}

    /** Everything a line's conditions can ask about the resident and the player talking to them. */
    public record Ctx(int level, boolean child, boolean female, String job, boolean cold, boolean first, Player player,
                      String place, @Nullable String state) {}

    public record When(int minLevel, int maxLevel, @Nullable Boolean child, @Nullable Boolean female, Set<String> jobs,
                       @Nullable Boolean cold, @Nullable Boolean first, @Nullable Item carrying, @Nullable Item holding,
                       @Nullable String place, @Nullable String state, @Nullable Boolean gold, int specificity) {
        static final When ALWAYS = new When(0, 5, null, null, Set.of(), null, null, null, null, null, null, null, 0);

        public boolean test(Ctx c) {
            if (c.level < this.minLevel || c.level > this.maxLevel) return false;
            if (this.child != null && this.child != c.child) return false;
            if (this.female != null && this.female != c.female) return false;
            if (!this.jobs.isEmpty() && !this.jobs.contains(c.job)) return false;
            if (this.cold != null && this.cold != c.cold) return false;
            if (this.first != null && this.first != c.first) return false;
            if (this.carrying != null && !c.player.getInventory().contains(s -> s.is(this.carrying))) return false;
            if (this.holding != null && !c.player.getMainHandItem().is(this.holding)) return false;
            if (this.place != null && !this.place.equals(c.place)) return false;
            if (this.gold != null && this.gold != PiglinAi.isWearingSafeArmor(c.player)) return false;
            return this.state == null || this.state.equals(c.state);
        }
    }

    public record Reply(String ask, String text, int points) {}

    public record Line(String text, When when, List<Reply> replies, ItemStack reward) {}

    public record Topic(String id, String ask, When when, List<Line> lines) {}

    public record Bank(List<String> levels, List<Line> greet, List<Line> chatter, List<Topic> topics, List<Line> levelup,
                       List<Line> farewell, List<Line> refuse, List<Predicate<ItemStack>> loved, List<Predicate<ItemStack>> liked,
                       List<Predicate<ItemStack>> disliked, Map<String, List<Line>> giftLines) {}

    private static final Map<People, Bank> BANKS = new EnumMap<>(People.class);

    public static synchronized Bank bank(People people) {
        return BANKS.computeIfAbsent(people, Dialogue::load);
    }

    // -- choosing ----------------------------------------------------------------------------------------------

    /**
     * One line from {@code lines} whose conditions hold, or null. More specific lines are likelier (a line for this
     * job beats a general one), and lines said recently are skipped while there are others.
     */
    public static @Nullable Line pick(List<Line> lines, Ctx ctx, RandomSource random, Set<String> recent) {
        List<Line> open = new ArrayList<>();
        for (Line line : lines) if (line.when.test(ctx)) open.add(line);
        if (open.isEmpty()) return null;
        List<Line> fresh = open.stream().filter(l -> !recent.contains(l.text)).toList();
        if (!fresh.isEmpty()) open = fresh;
        int total = 0;
        for (Line line : open) total += weight(line);
        int roll = random.nextInt(total);
        for (Line line : open) {
            roll -= weight(line);
            if (roll < 0) return line;
        }
        return open.getLast();
    }

    private static int weight(Line line) {
        return 1 + 2 * line.when.specificity;
    }

    public static boolean open(Topic topic, Ctx ctx) {
        if (!topic.when.test(ctx)) return false;
        for (Line line : topic.lines) if (line.when.test(ctx)) return true;
        return false;
    }

    /**
     * Fill in {name}, {village} and the rest from {@code values}. Unknown placeholders are left as they are. A value
     * that starts a sentence ("home", "a neighbour") gets a capital letter.
     */
    public static String fill(String text, Map<String, String> values) {
        StringBuilder out = new StringBuilder(text.length() + 16);
        int i = 0;
        while (i < text.length()) {
            int open = text.indexOf('{', i);
            if (open < 0) break;
            int close = text.indexOf('}', open);
            if (close < 0) break;
            out.append(text, i, open);
            String value = values.get(text.substring(open + 1, close));
            if (value == null) value = text.substring(open, close + 1);
            else if (!value.isEmpty() && startsSentence(out)) value = Character.toUpperCase(value.charAt(0)) + value.substring(1);
            out.append(value);
            i = close + 1;
        }
        out.append(text, i, text.length());
        return out.toString();
    }

    /** True when the text so far ends where a sentence begins (not after an ellipsis, which trails off mid-thought). */
    private static boolean startsSentence(CharSequence before) {
        int i = before.length() - 1;
        while (i >= 0 && " \"'*".indexOf(before.charAt(i)) >= 0) i--;
        if (i < 0) return true;
        char c = before.charAt(i);
        return (c == '.' || c == '!' || c == '?') && !(c == '.' && i > 0 && before.charAt(i - 1) == '.');
    }

    // -- loading -----------------------------------------------------------------------------------------------

    private static Bank load(People people) {
        String path = "/data/netherfriends/talk/" + people.name().toLowerCase() + ".json";
        try (InputStream in = Dialogue.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("missing " + path);
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            List<String> levels = new ArrayList<>();
            for (JsonElement e : root.getAsJsonArray("levels")) levels.add(e.getAsString());
            List<Topic> topics = new ArrayList<>();
            for (JsonElement e : root.getAsJsonArray("topics")) {
                JsonObject t = e.getAsJsonObject();
                topics.add(new Topic(t.get("id").getAsString(), t.get("ask").getAsString(), when(t.get("when")), lines(t.getAsJsonArray("lines"))));
            }
            JsonObject gift = root.getAsJsonObject("gift");
            Map<String, List<Line>> giftLines = new HashMap<>();
            for (Map.Entry<String, JsonElement> e : gift.getAsJsonObject("lines").entrySet()) giftLines.put(e.getKey(), lines(e.getValue().getAsJsonArray()));
            return new Bank(List.copyOf(levels), lines(root.getAsJsonArray("greet")), lines(root.getAsJsonArray("chatter")), List.copyOf(topics),
                    lines(root.getAsJsonArray("levelup")), lines(root.getAsJsonArray("farewell")), lines(root.getAsJsonArray("refuse")),
                    items(gift.getAsJsonArray("loved")), items(gift.getAsJsonArray("liked")), items(gift.getAsJsonArray("disliked")), giftLines);
        } catch (Exception e) {
            throw new IllegalStateException("Could not read the " + people.plural + "' dialogue (" + path + ")", e);
        }
    }

    static List<Line> lines(@Nullable JsonArray array) {
        List<Line> out = new ArrayList<>();
        if (array == null) return out;
        for (JsonElement e : array) {
            if (e.isJsonPrimitive()) {
                out.add(new Line(e.getAsString(), When.ALWAYS, List.of(), ItemStack.EMPTY));
                continue;
            }
            JsonObject o = e.getAsJsonObject();
            List<Reply> replies = new ArrayList<>();
            if (o.has("replies")) {
                for (JsonElement r : o.getAsJsonArray("replies")) {
                    JsonObject ro = r.getAsJsonObject();
                    replies.add(new Reply(ro.get("ask").getAsString(), ro.get("text").getAsString(), ro.has("points") ? ro.get("points").getAsInt() : 0));
                }
            }
            ItemStack reward = ItemStack.EMPTY;
            if (o.has("reward")) {
                JsonObject r = o.getAsJsonObject("reward");
                reward = new ItemStack(item(r.get("item").getAsString()), r.has("count") ? r.get("count").getAsInt() : 1);
            }
            out.add(new Line(o.get("text").getAsString(), when(o.get("when")), List.copyOf(replies), reward));
        }
        return List.copyOf(out);
    }

    private static When when(@Nullable JsonElement element) {
        if (element == null || !element.isJsonObject()) return When.ALWAYS;
        JsonObject o = element.getAsJsonObject();
        int min = 0, max = 5;
        if (o.has("level")) {
            JsonArray l = o.getAsJsonArray("level");
            min = l.get(0).getAsInt();
            max = l.get(1).getAsInt();
        }
        Set<String> jobs = new HashSet<>();
        if (o.has("job")) {
            JsonElement j = o.get("job");
            if (j.isJsonArray()) for (JsonElement x : j.getAsJsonArray()) jobs.add(x.getAsString());
            else jobs.add(j.getAsString());
        }
        return new When(min, max, bool(o, "child"), o.has("gender") ? o.get("gender").getAsString().equals("female") : null, Set.copyOf(jobs),
                bool(o, "cold"), bool(o, "first"), o.has("carrying") ? item(o.get("carrying").getAsString()) : null,
                o.has("holding") ? item(o.get("holding").getAsString()) : null, o.has("place") ? o.get("place").getAsString() : null,
                o.has("state") ? o.get("state").getAsString() : null, bool(o, "gold"), o.size());
    }

    private static @Nullable Boolean bool(JsonObject o, String key) {
        return o.has(key) ? o.get(key).getAsBoolean() : null;
    }

    static Item item(String id) {
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(id));
    }

    private static List<Predicate<ItemStack>> items(@Nullable JsonArray array) {
        List<Predicate<ItemStack>> out = new ArrayList<>();
        if (array == null) return out;
        for (JsonElement e : array) {
            String id = e.getAsString();
            if (id.startsWith("#")) {
                TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(id.substring(1)));
                out.add(stack -> stack.is(tag));
            } else {
                Item item = item(id);
                out.add(stack -> stack.is(item));
            }
        }
        return List.copyOf(out);
    }
}
