package dev.netherfriends.world;

import java.util.Set;
import java.util.function.LongFunction;

import dev.netherfriends.entity.People;

import net.minecraft.core.BlockPos;

/**
 * Names in the peoples' own sounds. Everything here is deterministic: the same number (or village centre) always
 * gives the same name, so a village's names can be worked out again at any time without being stored, and the
 * names a village carves into its signs at generation match the names its residents speak of later.
 */
public final class Names {
    private Names() {}

    /**
     * Syllables can land on real words or everyday names ("Later", "Kill", "Eric"). These are thrown back and
     * drawn again, so nobody is called something that reads like a sentence.
     */
    static final Set<String> NOT_NAMES = Set.of(
            "Blob", "Blog", "Bob", "Bod", "Bog", "Bonk", "Bork", "Bosh", "Brad", "Brag", "Brash", "Drub", "Drug", "Drunk", "Glad", "Gob",
            "God", "Gosh", "Grid", "Grub", "Grump", "Mob", "Mod", "Monk", "Mosh", "Ob", "Od", "Rab", "Rad", "Rag", "Ramp", "Rank", "Rash",
            "Skunk", "Snob", "Snog", "Snort", "Throb", "Tramp", "Trash", "Ump", "Boba", "Olga", "Oda", "Moda",
            "Later", "Mater", "Pater", "Baton", "Futon", "Teton", "Puter", "Lamar", "Maria", "Puffa", "Laffa", "Maffa", "Baffa", "Fuffa",
            "Paria", "Lamia",
            "Ilk", "Irk", "Itch", "Kirk", "Nest", "Risk", "Sax", "Vest", "Vetch", "Vex", "Vox", "Rizz", "Tsex", "Ssex", "Ill", "Kill",
            "Killa", "Kiki", "Kira", "Nell", "Nella", "Sara", "Sash", "Till", "Tish", "Vera", "Ira", "Ish", "Rill", "Sall", "Tiki", "Tithe",
            "Alan", "Amos", "Eric", "Erin", "Eros", "Feel", "Lumos", "Odor", "Sarin", "Elan", "Emos", "Fedor", "Orin", "Ilan",
            "Anna", "Ara", "Ariel", "Aura", "Era", "One", "Owen", "Sane", "Sine", "Messi", "Thine", "Sissi", "Mera",
            "Moron", "Moroon", "Karen", "Seven", "Elden", "Goron");

    /** {@code make(n)}, or the next draw that isn't a word or an everyday name. */
    static String draw(long n, LongFunction<String> make) {
        for (int i = 0; i < 16; i++) {
            String name = make.apply(n + i * 0x632BE59BD9B4E019L);
            if (!NOT_NAMES.contains(name)) return name;
        }
        return make.apply(n);
    }

    // -- Withren ----------------------------------------------------------------------------------------------

    static final String[] WITHREN_START = {"Ash", "Os", "Vel", "Kar", "Mor", "Thal", "Sev", "Dra", "Ny", "Ul", "Cin", "Hes", "Gor", "Ves",
            "Iss", "Ruth", "Ael", "Bran", "Corv", "Eld"};
    static final String[] WITHREN_MID = {"", "a", "e", "i", "o", "ae", "y", "ue"};
    static final String[] WITHREN_END = {"rin", "eth", "ith", "on", "ar", "ul", "ys", "eon", "ra", "mir", "oth", "ash", "en", "is", "ane"};

    /** The Three who gave themselves to wake the Wither. Every Vigil's Hall of the Three names them. */
    public static final String[] THE_THREE = {"Ashrin", "Velmir", "Thalys"};
    /** How many names a Vigil's Name Wall carries (13 columns of 3 signs). */
    public static final int WALL_SIZE = 39;

    /** A Withren name: two or three syllables, ash and bone in the mouth. */
    public static String withren(long n) {
        return draw(n, m -> {
            long h = mix(m);
            return pick(WITHREN_START, h) + pick(WITHREN_MID, h >>> 12) + pick(WITHREN_END, h >>> 24);
        });
    }

    /** The {@code index}th name on the Name Wall of the Vigil around {@code village}: the dead its people keep. */
    public static String wallName(BlockPos village, int index) {
        return withren(seed(village) * 31 + Math.floorMod(index, WALL_SIZE));
    }

    static final String[] VIGIL = {"Greylantern", "Ashfall", "Bonefire", "Lowmourn", "Stillash", "Soulwater", "Hollowbell", "Quietcairn",
            "Duskwart", "Palestone", "Emberless", "Ninecandle", "Coldhearth", "Longwatch"};

    // -- Piglins ----------------------------------------------------------------------------------------------

    static final String[] PIGLIN_START = {"Gru", "Bra", "Sno", "Kro", "Dru", "Thro", "Zu", "Mo", "Ra", "Hru", "Gno", "Blo", "Sku", "Go", "Gla",
            "Tra", "Bo", "Gri", "U", "O"};
    static final String[] PIGLIN_MALE = {"kk", "rr", "g", "gg", "sh", "nk", "rt", "mp", "rk", "lg", "b", "d", "gor", "rug"};
    static final String[] PIGLIN_FEMALE = {"kka", "rra", "gga", "sha", "nka", "rta", "mpa", "rka", "lga", "ba", "da", "zza", "gna", "ssa"};
    static final String[] CLAN_FIRST = {"Red", "Gild", "Ash", "Iron", "Crook", "Bright", "Black", "Split", "Broad", "Ember", "Hog", "Gold",
            "Bent", "Loud", "Rust", "Ingot"};
    static final String[] CLAN_SECOND = {"Tusk", "Snout", "Bristle", "Hide", "Maw", "Hoof", "Ear", "Tooth", "Fist", "Gut", "Back", "Jaw"};

    public static String piglin(long n, boolean female) {
        return draw(n, m -> {
            long h = mix(m);
            return pick(PIGLIN_START, h) + pick(female ? PIGLIN_FEMALE : PIGLIN_MALE, h >>> 16);
        });
    }

    /** A Piglin clan ("Red-Tusk"): one per longhouse. */
    public static String clan(long n) {
        long h = mix(n ^ 0x5DEECE66DL);
        return pick(CLAN_FIRST, h) + "-" + pick(CLAN_SECOND, h >>> 16);
    }

    // -- Magmari ----------------------------------------------------------------------------------------------

    static final String[] MAGMARI_START = {"Te", "Sco", "La", "Pu", "Ci", "Ig", "Fu", "Ob", "Kra", "Tu", "Ma", "Ba", "Vul", "Kal", "Sin", "Pa"};
    static final String[] MAGMARI_MALE = {"phar", "rik", "har", "mik", "nder", "nim", "mar", "sid", "ton", "rok", "gor", "salt", "kan", "dar", "ter", "hoe"};
    static final String[] MAGMARI_FEMALE = {"phra", "ria", "hara", "mia", "ndra", "nea", "mara", "sida", "tona", "ffa", "gora", "salta", "kana",
            "dera", "tera", "hoa"};
    static final String[] CHAIN_ADJ = {"Long", "Slow", "Bright", "Deep", "Red", "Old", "Ninth", "Low", "Warm", "Iron", "Singing", "Sunken",
            "Heavy", "Patient"};
    static final String[] CHAIN_NOUN = {"Chain", "Glow", "Link", "Tide", "Ember", "Hook", "Wake", "Drift", "Crust", "Anchor", "Swell", "Coil"};

    public static String magmari(long n, boolean female) {
        return draw(n, m -> {
            long h = mix(m);
            return pick(MAGMARI_START, h) + pick(female ? MAGMARI_FEMALE : MAGMARI_MALE, h >>> 16);
        });
    }

    /** A Mooring's chain ("the Long Chain"), which all its people carry in their names. */
    public static String chain(long n) {
        long h = mix(n ^ 0x2545F4914F6CDD1DL);
        return "the " + pick(CHAIN_ADJ, h) + " " + pick(CHAIN_NOUN, h >>> 16);
    }

    // -- Weft -------------------------------------------------------------------------------------------------

    static final String[] WEFT_START = {"Ve", "Ti", "Sse", "Ki", "I", "Ne", "Zae", "Ska", "Thi", "Qe", "Ri", "Xe", "Sa", "Il", "Vo", "Tse"};
    static final String[] WEFT_MALE = {"sk", "k", "th", "tch", "ssk", "x", "kt", "st", "rk", "zz"};
    static final String[] WEFT_FEMALE = {"sse", "ll", "the", "ssa", "lla", "sh", "ra", "wen", "ssi", "ki"};
    static final String[] LOOM = {"Highwarp", "Ninefold", "Tealthread", "Hanging", "Longcloth", "Quietshuttle", "Sixeye", "Lanternweave",
            "Deepcap", "Brightknot", "Slowspool", "Stillweft"};
    /** This year's row of the Long Cloth; the sixty black rows of the Wither are 1190 to 1249. */
    public static final int THIS_ROW = 1312;

    public static String weft(long n, boolean female) {
        return draw(n, m -> {
            long h = mix(m);
            return pick(WEFT_START, h) + pick(female ? WEFT_FEMALE : WEFT_MALE, h >>> 16);
        });
    }

    static final String[] ONES = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    static final String[] TEENS = {"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    static final String[] TENS = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

    /** A row number the way the Weft say it, like a year: 1287 is "Twelve-Eighty-Seven", 1304 "Thirteen-Oh-Four". */
    public static String row(int n) {
        int hi = n / 100, lo = n % 100;
        String head = hi >= 10 && hi < 20 ? TEENS[hi - 10] : String.valueOf(hi);
        if (lo == 0) return head + "-Hundred";
        if (lo < 10) return head + "-Oh-" + ONES[lo];
        if (lo < 20) return head + "-" + TEENS[lo - 10];
        return head + "-" + TENS[lo / 10] + (lo % 10 == 0 ? "" : "-" + ONES[lo % 10]);
    }

    // -- Ghastkin ---------------------------------------------------------------------------------------------

    static final String[] GHASTKIN_START = {"Si", "O", "A", "Lu", "I", "We", "E", "Au", "Me", "Ny", "Fe", "Ilo", "Sa", "Thi", "Ve"};
    static final String[] GHASTKIN_MALE = {"el", "rin", "len", "mos", "ric", "lan", "wyn", "ros", "dor", "lis"};
    static final String[] GHASTKIN_FEMALE = {"el", "riel", "ne", "ssa", "nna", "lia", "ra", "wen", "lune", "ssi"};
    static final String[] EAVE = {"Hushwater", "Stillsmoke", "Greymourn", "Lowlight", "Softash", "Paletear", "Quietdrift", "Lastlantern",
            "Slowfall", "Thinveil", "Saltsmoke", "Mistcradle"};

    public static String ghastkin(long n, boolean female) {
        return draw(n, m -> {
            long h = mix(m);
            return pick(GHASTKIN_START, h) + pick(female ? GHASTKIN_FEMALE : GHASTKIN_MALE, h >>> 16);
        });
    }

    // -- villages ---------------------------------------------------------------------------------------------

    /** A village's name, from its centre: "Gildmaw Hold", "Ashfall Vigil", "the Mooring of the Slow Glow"... */
    public static String village(People people, BlockPos centre) {
        long h = seed(centre);
        return switch (people) {
            case PIGLIN -> pick(CLAN_FIRST, h) + pick(CLAN_SECOND, h >>> 16).toLowerCase() + " Hold";
            case WITHREN -> pick(VIGIL, h) + " Vigil";
            case MAGMARI -> "the Mooring of " + chain(h);
            case WEFT -> pick(LOOM, h) + " Loom";
            case GHASTKIN -> pick(EAVE, h) + " Eave";
        };
    }

    /** What its own people call a kind of village. */
    public static String kindOf(People people) {
        return switch (people) {
            case PIGLIN -> "Hold";
            case WITHREN -> "Vigil";
            case MAGMARI -> "Mooring";
            case WEFT -> "Loom";
            case GHASTKIN -> "Eave";
        };
    }

    /** A number for a place: the seed every name tied to it grows from. */
    public static long seed(BlockPos pos) {
        return mix(pos.asLong());
    }

    static String pick(String[] options, long h) {
        return options[(int) Math.floorMod(h, (long) options.length)];
    }

    public static long mix(long n) {
        long h = n * 0x9E3779B97F4A7C15L;
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        return h ^ (h >>> 31);
    }
}
