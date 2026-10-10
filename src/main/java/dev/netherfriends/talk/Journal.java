package dev.netherfriends.talk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.entity.People;
import dev.netherfriends.piglin.GoldRule;
import dev.netherfriends.talk.Bonds.Bond;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * What a player and a resident have shared, kept on the resident, one entry per player: when they met, the days
 * they've talked, the memories the Journal tab lists, the gifts the player has found out about, and how far along
 * the resident's story ({@link Story}) the two of them are. The Journal tab shows it in three pages: Memories,
 * About and Story notes.
 */
public final class Journal {
    private Journal() {}

    public static final int MAX_MEMORIES = 40;

    /** The story steps: not begun, asked for something, brought it, finished. */
    public static final int NOT_BEGUN = 0, ASKED = 1, BROUGHT = 2, FINISHED = 3;

    public record Memory(long day, String text) {
        public static final Codec<Memory> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.LONG.fieldOf("day").forGetter(Memory::day),
                Codec.STRING.fieldOf("text").forGetter(Memory::text)
        ).apply(i, Memory::new));
    }

    /**
     * {@code story} is the id of the story begun ("" until then), {@code chapter} one of the steps above,
     * {@code brought} the day the request was brought, {@code ending} the ending chosen (-1 until then).
     * {@code gifts} holds "loved:minecraft:gold_block" and the like for each gift reaction found out.
     */
    public record Entry(long met, int days, String story, int chapter, long brought, int ending, List<Memory> memories, List<String> gifts) {
        public static final Entry NEW = new Entry(-1, 0, "", NOT_BEGUN, -1, -1, List.of(), List.of());
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.LONG.optionalFieldOf("met", -1L).forGetter(Entry::met),
                Codec.INT.optionalFieldOf("days", 0).forGetter(Entry::days),
                Codec.STRING.optionalFieldOf("story", "").forGetter(Entry::story),
                Codec.INT.optionalFieldOf("chapter", NOT_BEGUN).forGetter(Entry::chapter),
                Codec.LONG.optionalFieldOf("brought", -1L).forGetter(Entry::brought),
                Codec.INT.optionalFieldOf("ending", -1).forGetter(Entry::ending),
                Memory.CODEC.listOf().optionalFieldOf("memories", List.of()).forGetter(Entry::memories),
                Codec.STRING.listOf().optionalFieldOf("gifts", List.of()).forGetter(Entry::gifts)
        ).apply(i, Entry::new));

        /** A new day of talking (the first one is the day they met). */
        public Entry visit(long day) {
            return new Entry(this.met < 0 ? day : this.met, this.days + 1, this.story, this.chapter, this.brought, this.ending, this.memories, this.gifts);
        }

        public Entry remember(long day, String text) {
            List<Memory> more = new ArrayList<>(this.memories);
            more.add(new Memory(day, text));
            while (more.size() > MAX_MEMORIES) more.removeFirst();
            return new Entry(this.met, this.days, this.story, this.chapter, this.brought, this.ending, List.copyOf(more), this.gifts);
        }

        /** Found out how they take this gift ("loved", "liked", "neutral" or "disliked"). */
        public Entry learn(String kind, String item) {
            String fact = kind + ":" + item;
            if (this.gifts.contains(fact)) return this;
            List<String> more = new ArrayList<>(this.gifts);
            more.add(fact);
            return new Entry(this.met, this.days, this.story, this.chapter, this.brought, this.ending, this.memories, List.copyOf(more));
        }

        public Entry chapter(String story, int chapter) {
            return new Entry(this.met, this.days, story, chapter, this.brought, this.ending, this.memories, this.gifts);
        }

        public Entry brought(long day) {
            return new Entry(this.met, this.days, this.story, BROUGHT, day, this.ending, this.memories, this.gifts);
        }

        public Entry ended(int ending) {
            return new Entry(this.met, this.days, this.story, FINISHED, this.brought, ending, this.memories, this.gifts);
        }
    }

    public static final AttachmentType<Map<String, Entry>> JOURNAL = AttachmentRegistry.create(NetherFriends.id("journal"),
            b -> b.initializer(Map::<String, Entry>of).persistent(Codec.unboundedMap(Codec.STRING, Entry.CODEC)));

    /** Loads the class, so the attachment is registered before any world (and its residents) loads. */
    public static void init() {}

    public static Entry get(LivingEntity resident, UUID player) {
        Map<String, Entry> all = resident.getAttached(JOURNAL);
        Entry entry = all == null ? null : all.get(player.toString());
        return entry == null ? Entry.NEW : entry;
    }

    public static void put(LivingEntity resident, UUID player, Entry entry) {
        Map<String, Entry> all = new HashMap<>(resident.getAttachedOrCreate(JOURNAL));
        all.put(player.toString(), entry);
        resident.setAttached(JOURNAL, Map.copyOf(all));
    }

    public static void remember(LivingEntity resident, UUID player, long day, String text) {
        put(resident, player, get(resident, player).remember(day, text));
    }

    // -- the tab -------------------------------------------------------------------------------------------------

    static List<TalkPayload.Choice> choices(LivingEntity resident) {
        return List.of(new TalkPayload.Choice("journal:memories", "Memories", false),
                new TalkPayload.Choice("journal:about", "About " + Folk.name(resident), false),
                new TalkPayload.Choice("journal:notes", "Story notes", false));
    }

    /** One page of the Journal tab: "memories", "about" or "notes". Lines are separated by line breaks. */
    static String page(String page, ServerPlayer player, LivingEntity resident, Bond bond) {
        Entry entry = get(resident, player.getUUID());
        return switch (page) {
            case "about" -> about(resident, bond, entry);
            case "notes" -> notes(resident, bond, entry);
            default -> memories(resident, entry);
        };
    }

    private static String memories(LivingEntity resident, Entry entry) {
        StringBuilder out = new StringBuilder("Memories with ").append(Folk.name(resident)).append('\n');
        if (entry.memories().isEmpty()) return out.append("Nothing yet. Talk with them, bring gifts, hear their story, and this fills up.").toString();
        for (Memory m : entry.memories().reversed()) out.append("Day ").append(m.day() + 1).append(": ").append(m.text()).append('\n');
        return out.toString().strip();
    }

    private static String about(LivingEntity resident, Bond bond, Entry entry) {
        People people = People.of(resident);
        Folk.Profile p = Folk.of(resident);
        boolean child = p.job().equals(Folk.CHILD);
        List<String> lines = new ArrayList<>();
        lines.add(Folk.fullName(p, people));
        String village = Folk.village(p, people);
        String home = village != null ? "of " + village : p.place().equals("bastion") ? "of a Bastion Remnant" : "no village";
        lines.add((people == People.PIGLIN ? "Piglin" : people.plural) + ", " + (child ? "child" : p.job().toLowerCase()) + ", " + home);
        String kin = p.kin();
        lines.add(switch (people) {
            case PIGLIN -> "Clan: " + kin;
            case WITHREN -> child ? "Keeps no one's name yet: a child chooses at their first Vigil." : "Keeps the name of " + kin + ".";
            case MAGMARI -> "Their Mooring's chain: " + kin;
            case WEFT -> "Born in row " + kin + " of the Long Cloth.";
            case GHASTKIN -> kin.isEmpty() ? child ? "Too young to have grieved, or to have a second name." : "Still has their first name." : "Was once called " + kin + ".";
        });
        Dialogue.Bank bank = Dialogue.bank(people);
        lines.add("Friendship: " + bank.levels().get(bond.level()) + " (Lv. " + bond.level() + ", " + bond.points() + " points)");
        if (entry.met() >= 0) lines.add("Met on day " + (entry.met() + 1) + ", talked on " + entry.days() + (entry.days() == 1 ? " day" : " days"));
        lines.add("Topics heard: " + bond.heard().size() + " of " + bank.topics().size());
        Map<String, List<String>> gifts = new LinkedHashMap<>();
        for (String kind : List.of("loved", "liked", "neutral", "disliked")) gifts.put(kind, new ArrayList<>());
        for (String fact : entry.gifts()) {
            int colon = fact.indexOf(':');
            List<String> list = colon < 0 ? null : gifts.get(fact.substring(0, colon));
            if (list != null) list.add(itemName(fact.substring(colon + 1)));
        }
        if (entry.gifts().isEmpty()) lines.add("Gifts: give them things to find out what they love.");
        if (!gifts.get("loved").isEmpty()) lines.add("Loves: " + String.join(", ", gifts.get("loved")));
        if (!gifts.get("liked").isEmpty()) lines.add("Likes: " + String.join(", ", gifts.get("liked")));
        if (!gifts.get("neutral").isEmpty()) lines.add("Doesn't mind: " + String.join(", ", gifts.get("neutral")));
        if (!gifts.get("disliked").isEmpty()) lines.add("Dislikes: " + String.join(", ", gifts.get("disliked")));
        if (people == People.PIGLIN && !child && bond.level() >= GoldRule.TRUST_LEVEL) lines.add("Knows your face: won't attack you without gold.");
        Story.Tale tale = Story.tale(resident, p, entry);
        if (tale != null && entry.chapter() != NOT_BEGUN) lines.add("Story: " + tale.title() + (entry.chapter() == FINISHED ? " (finished)" : ""));
        return String.join("\n", lines);
    }

    private static String notes(LivingEntity resident, Bond bond, Entry entry) {
        People people = People.of(resident);
        Folk.Profile p = Folk.of(resident);
        Story.Tale tale = Story.tale(resident, p, entry);
        if (tale == null) return "Their story\nChildren don't have a story of their own yet. Ask again when they're grown.";
        List<String> levels = Dialogue.bank(people).levels();
        int chapter = entry.chapter();
        List<String> lines = new ArrayList<>();
        lines.add(chapter == NOT_BEGUN ? "Their story (not told yet)" : tale.title());
        String things = Story.things(tale.request().stack());
        String opens = levels.get(Story.OPENS_AT), confides = levels.get(Story.CONFIDES_AT);
        lines.add(step(chapter > NOT_BEGUN, chapter == NOT_BEGUN && bond.level() >= Story.OPENS_AT, "Heard what's on their mind",
                bond.level() < Story.OPENS_AT ? "Hear what's on their mind (when you're their " + opens + ")" : "Hear what's on their mind (Story tab)"));
        lines.add(step(chapter > ASKED, chapter == ASKED, "Brought them " + things,
                chapter == ASKED ? "Bring them " + things + " (hold them, then press Here on the Story tab)" : "Bring them what they need"));
        lines.add(step(chapter > BROUGHT, chapter == BROUGHT, "Heard the rest",
                "Hear the rest (once you're their " + confides + ", on a later day than you brought it)"));
        lines.add(step(chapter == FINISHED, false, "Answered them", "Answer them"));
        if (chapter == FINISHED && entry.ending() >= 0 && entry.ending() < tale.endings().size()) {
            lines.add("You told them: " + tale.endings().get(entry.ending()).ask());
        }
        return String.join("\n", lines);
    }

    private static String step(boolean done, boolean next, String doneText, String todo) {
        return done ? "Done: " + doneText : (next ? "Next: " : "Later: ") + todo;
    }

    private static String itemName(String id) {
        Identifier key = Identifier.tryParse(id);
        return key == null ? id : new ItemStack(BuiltInRegistries.ITEM.getValue(key)).getHoverName().getString();
    }
}
