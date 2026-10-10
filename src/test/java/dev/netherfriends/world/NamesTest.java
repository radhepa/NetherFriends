package dev.netherfriends.world;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dev.netherfriends.entity.People;
import dev.netherfriends.talk.Dialogue;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

/** Names are worked out again whenever they're needed, so they must never drift. */
class NamesTest {
    @Test
    void rowsAreSaidLikeYears() {
        assertEquals("Thirteen-Twelve", Names.row(1312));
        assertEquals("Twelve-Eighty-Seven", Names.row(1287));
        assertEquals("Thirteen-Oh-Four", Names.row(1304));
        assertEquals("Twelve-Hundred", Names.row(1200));
        assertEquals("Eleven-Ninety", Names.row(1190));
        assertEquals("Twelve-Fifteen", Names.row(1215));
    }

    @Test
    void aVillageAlwaysHasTheSameName() {
        BlockPos centre = new BlockPos(-328, 46, 456);
        for (People people : People.values()) {
            assertEquals(Names.village(people, centre), Names.village(people, centre.immutable()));
            assertNotEquals(Names.village(people, centre), Names.village(people, centre.east(16)), people + " names repeat next door");
        }
        assertTrue(Names.village(People.PIGLIN, centre).endsWith(" Hold"));
        assertTrue(Names.village(People.MAGMARI, centre).startsWith("the Mooring of the "));
    }

    @Test
    void theNameWallHoldsDistinctNames() {
        BlockPos centre = new BlockPos(200, 60, 8);
        Set<String> names = new HashSet<>();
        for (int i = 0; i < Names.WALL_SIZE; i++) names.add(Names.wallName(centre, i));
        assertTrue(names.size() > Names.WALL_SIZE * 3 / 4, "too many repeats on the wall: " + names.size());
        assertEquals(Names.wallName(centre, 5), Names.wallName(centre, 5 + Names.WALL_SIZE));
    }

    @Test
    void namesSoundLikeTheirPeople() {
        for (long n = 0; n < 200; n++) {
            assertTrue(Names.piglin(n, true).matches("[A-Z][a-z]+"), Names.piglin(n, true));
            assertTrue(Names.clan(n).matches("[A-Z][a-z]+-[A-Z][a-z]+"), Names.clan(n));
            assertTrue(Names.chain(n).startsWith("the "), Names.chain(n));
            assertTrue(Names.ghastkin(n, false).length() >= 2);
        }
    }

    @Test
    void nobodyIsCalledAWord() {
        for (long n = 0; n < 5000; n++) {
            for (boolean female : new boolean[]{false, true}) {
                for (String name : new String[]{Names.piglin(n, female), Names.magmari(n, female), Names.weft(n, female), Names.ghastkin(n, female), Names.withren(n)}) {
                    assertTrue(!Names.NOT_NAMES.contains(name), name);
                }
            }
        }
    }

    @Test
    void placeholdersFillAndStartSentences() {
        Map<String, String> v = Map.of("name", "Grukka", "neighbour", "a neighbour", "village", "the Mooring of the Long Chain");
        assertEquals("Grukka here.", Dialogue.fill("{name} here.", v));
        assertEquals("Ask a neighbour. A neighbour knows.", Dialogue.fill("Ask {neighbour}. {neighbour} knows.", v));
        assertEquals("Welcome to the Mooring of the Long Chain.", Dialogue.fill("Welcome to {village}.", v));
        assertEquals("I was... a neighbour, once.", Dialogue.fill("I was... {neighbour}, once.", v));
        assertEquals("{unknown} stays", Dialogue.fill("{unknown} stays", v));
    }
}
