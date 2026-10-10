package dev.netherfriends.talk;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.netherfriends.entity.People;
import dev.netherfriends.talk.Bonds.Bond;
import dev.netherfriends.talk.Dialogue.Ctx;
import dev.netherfriends.talk.Dialogue.Line;
import dev.netherfriends.talk.Talk.Session;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

/**
 * Each grown resident's own story, told on the conversation window's Story tab in four steps (docs/STORIES.md):
 * hear what's on their mind, bring what they ask for, hear the rest (from level 3, on a later day) and answer
 * them. The stories live in {@code data/netherfriends/stories/<people>.json}; which one a resident tells is picked
 * from their job's stories by who they are, and kept in the {@link Journal} once begun.
 */
public final class Story {
    private Story() {}

    /** The friendship level at which a resident tells you their story, and the one at which they tell you the rest. */
    public static final int OPENS_AT = 2, CONFIDES_AT = 3;
    public static final int BROUGHT_POINTS = 5, ENDING_POINTS = 5;

    public record Request(ItemStack stack, String ask, String thanks) {}

    public record Ending(String ask, String text, String memory) {}

    public record Tale(String id, Set<String> jobs, String title, List<String> intro, Request request, List<String> confide,
                       List<Ending> endings, ItemStack reward, List<String> after) {}

    public record Book(List<Line> early, List<Line> child, List<Line> waiting, List<Line> ready, List<Tale> tales) {}

    /** What the Story tab shows: their line, the status under it, and what you can say. */
    record View(String text, String status, List<TalkPayload.Choice> choices) {}

    private static final Map<People, Book> BOOKS = new EnumMap<>(People.class);

    public static synchronized Book book(People people) {
        return BOOKS.computeIfAbsent(people, Story::load);
    }

    /** The resident's story for this player: the one they began together, or the one the resident's job and self pick. */
    public static @Nullable Tale tale(LivingEntity resident, Folk.Profile profile, Journal.Entry entry) {
        Book book = book(People.of(resident));
        if (!entry.story().isEmpty()) {
            for (Tale t : book.tales()) if (t.id().equals(entry.story())) return t;
        }
        if (profile.job().equals(Folk.CHILD)) return null;
        List<Tale> theirs = book.tales().stream().filter(t -> t.jobs().contains(profile.job())).toList();
        if (theirs.isEmpty()) theirs = book.tales();
        if (theirs.isEmpty()) return null;
        return theirs.get(Math.floorMod(resident.getUUID().hashCode() * 31 + 7, theirs.size()));
    }

    /** "Magma Block x8". */
    public static String things(ItemStack stack) {
        return stack.getHoverName().getString() + (stack.getCount() > 1 ? " x" + stack.getCount() : "");
    }

    /** They're ready to tell you the rest: you brought what they asked for, on an earlier day, and you're close enough. */
    static boolean ready(Journal.Entry entry, Bond bond, long day) {
        return entry.chapter() == Journal.BROUGHT && bond.level() >= CONFIDES_AT && day > entry.brought();
    }

    // -- the tab -------------------------------------------------------------------------------------------------

    /** The Story tab as it stands, at the session's page. */
    static View view(ServerPlayer player, LivingEntity resident, Session s, Bond bond, Ctx ctx) {
        People people = People.of(resident);
        Book book = book(people);
        Folk.Profile profile = Folk.of(resident);
        Journal.Entry entry = Journal.get(resident, player.getUUID());
        Tale tale = tale(resident, profile, entry);
        List<String> levels = Dialogue.bank(people).levels();
        if (tale == null) {
            return new View(Talk.line(book.child(), ctx, s, resident), "Children don't have a story of their own yet.", List.of());
        }
        String step = tale.title() + ", ";
        switch (entry.chapter()) {
            case Journal.NOT_BEGUN -> {
                if (bond.level() < OPENS_AT) {
                    return new View(Talk.line(book.early(), ctx, s, resident), "They'll tell you their story when you're their " + levels.get(OPENS_AT) + ".", List.of());
                }
                int page = Math.clamp(s.page, 0, tale.intro().size() - 1);
                List<TalkPayload.Choice> choices = page < tale.intro().size() - 1
                        ? List.of(choice("story:next", "Go on."))
                        : List.of(choice("story:begin", "I'll help."), choice("tab:talk", "Not now."));
                return new View(Talk.fill(tale.intro().get(page), ctx, resident), step + "1 of 4: what's on their mind", choices);
            }
            case Journal.ASKED -> {
                ItemStack want = tale.request().stack();
                return new View(Talk.fill(tale.request().ask(), ctx, resident), step + "2 of 4: hold " + things(want) + " in your main hand",
                        List.of(choice("story:bring", "Here: " + things(want))));
            }
            case Journal.BROUGHT -> {
                if (!ready(entry, bond, Talk.day(player))) {
                    String when = bond.level() < CONFIDES_AT ? "once you're their " + levels.get(CONFIDES_AT) : "on another day";
                    return new View(Talk.line(book.waiting(), ctx, s, resident), step + "3 of 4: they'll tell you the rest " + when + ".", List.of());
                }
                if (s.page <= 0) {
                    return new View(Talk.line(book.ready(), ctx, s, resident), step + "3 of 4: the rest of it", List.of(choice("story:next", "I'm listening.")));
                }
                int page = Math.min(s.page, tale.confide().size()) - 1;
                List<TalkPayload.Choice> choices = new ArrayList<>();
                if (page < tale.confide().size() - 1) choices.add(choice("story:next", "Go on."));
                else for (int i = 0; i < tale.endings().size(); i++) choices.add(choice("story:end:" + i, Talk.fill(tale.endings().get(i).ask(), ctx, resident)));
                return new View(Talk.fill(tale.confide().get(page), ctx, resident), page < tale.confide().size() - 1 ? step + "3 of 4: the rest of it"
                        : step + "4 of 4: what do you tell them?", choices);
            }
            default -> {
                String after = tale.after().get(s.random.nextInt(tale.after().size()));
                return new View(Talk.fill(after, ctx, resident), tale.title() + ": finished", List.of());
            }
        }
    }

    /**
     * A press on the Story tab. Returns the bond (with any points earned) and sets the session's page; the caller
     * shows {@link #view} afterwards, or {@code said} when the step has a line of its own (thanks, an ending).
     */
    record Outcome(Bond bond, @Nullable String said, String status) {}

    static @Nullable Outcome act(ServerPlayer player, LivingEntity resident, Session s, Bond bond, Ctx ctx, String action) {
        Folk.Profile profile = Folk.of(resident);
        Journal.Entry entry = Journal.get(resident, player.getUUID());
        Tale tale = tale(resident, profile, entry);
        if (tale == null) return null;
        long day = Talk.day(player);
        String name = Folk.name(resident);
        switch (action) {
            case "next" -> {
                s.page++;
                return new Outcome(bond, null, "");
            }
            case "begin" -> {
                if (entry.chapter() != Journal.NOT_BEGUN || bond.level() < OPENS_AT) return null;
                Journal.put(resident, player.getUUID(), entry.chapter(tale.id(), Journal.ASKED)
                        .remember(day, name + " told you what's on their mind: " + tale.title() + "."));
                s.page = 0;
                return new Outcome(bond, null, "");
            }
            case "bring" -> {
                if (entry.chapter() != Journal.ASKED) return null;
                ItemStack want = tale.request().stack();
                ItemStack held = player.getMainHandItem();
                if (!held.is(want.getItem()) || held.getCount() < want.getCount()) {
                    return new Outcome(bond, Talk.fill(tale.request().ask(), ctx, resident), "Hold " + things(want) + " in your main hand, then press Here.");
                }
                if (!player.hasInfiniteMaterials()) held.shrink(want.getCount());
                Journal.put(resident, player.getUUID(), entry.brought(day).remember(day, "You brought " + name + " " + things(want) + " for " + tale.title() + "."));
                s.page = 0;
                return new Outcome(bond.add(BROUGHT_POINTS), Talk.fill(tale.request().thanks(), ctx, resident),
                        "Brought " + things(want) + ": +" + BROUGHT_POINTS);
            }
            default -> {
                if (!action.startsWith("end:") || !ready(entry, bond, day) || s.page < tale.confide().size()) return null;
                int n;
                try {
                    n = Integer.parseInt(action.substring(4));
                } catch (NumberFormatException e) {
                    return null;
                }
                if (n < 0 || n >= tale.endings().size()) return null;
                Ending ending = tale.endings().get(n);
                Journal.put(resident, player.getUUID(), entry.ended(n).remember(day, Talk.fill(ending.memory(), ctx, resident)));
                ItemStack gift = tale.reward().copy();
                String reward = things(gift);
                if (!player.getInventory().add(gift) && !gift.isEmpty()) player.spawnAtLocation(player.level(), gift);
                s.page = 0;
                return new Outcome(bond.add(ENDING_POINTS), Talk.fill(ending.text(), ctx, resident),
                        tale.title() + ", finished. They gave you " + reward + ": +" + ENDING_POINTS);
            }
        }
    }

    private static TalkPayload.Choice choice(String id, String label) {
        return new TalkPayload.Choice(id, label, false);
    }

    // -- loading -------------------------------------------------------------------------------------------------

    private static Book load(People people) {
        String path = "/data/netherfriends/stories/" + people.name().toLowerCase() + ".json";
        try (InputStream in = Story.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("missing " + path);
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            List<Tale> tales = new ArrayList<>();
            for (JsonElement e : root.getAsJsonArray("stories")) {
                JsonObject t = e.getAsJsonObject();
                Set<String> jobs = new HashSet<>();
                for (JsonElement j : t.getAsJsonArray("jobs")) jobs.add(j.getAsString());
                JsonObject r = t.getAsJsonObject("request");
                List<Ending> endings = new ArrayList<>();
                for (JsonElement x : t.getAsJsonArray("endings")) {
                    JsonObject o = x.getAsJsonObject();
                    endings.add(new Ending(o.get("ask").getAsString(), o.get("text").getAsString(), o.get("memory").getAsString()));
                }
                tales.add(new Tale(t.get("id").getAsString(), Set.copyOf(jobs), t.get("title").getAsString(), strings(t.getAsJsonArray("intro")),
                        new Request(stack(r), r.get("ask").getAsString(), r.get("thanks").getAsString()), strings(t.getAsJsonArray("confide")),
                        List.copyOf(endings), stack(t.getAsJsonObject("reward")), strings(t.getAsJsonArray("after"))));
            }
            return new Book(Dialogue.lines(root.getAsJsonArray("early")), Dialogue.lines(root.getAsJsonArray("child")),
                    Dialogue.lines(root.getAsJsonArray("waiting")), Dialogue.lines(root.getAsJsonArray("ready")), List.copyOf(tales));
        } catch (Exception e) {
            throw new IllegalStateException("Could not read the " + people.plural + "' stories (" + path + ")", e);
        }
    }

    private static List<String> strings(JsonArray array) {
        List<String> out = new ArrayList<>();
        for (JsonElement e : array) out.add(e.getAsString());
        return List.copyOf(out);
    }

    private static ItemStack stack(JsonObject o) {
        return new ItemStack(Dialogue.item(o.get("item").getAsString()), o.has("count") ? o.get("count").getAsInt() : 1);
    }

    /** For tests: every story id of a people. */
    public static List<String> ids(People people) {
        return book(people).tales().stream().map(Tale::id).toList();
    }

    static String itemId(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }
}
