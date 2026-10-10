package dev.netherfriends.anim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.jupiter.api.Test;

/** The bundled animation packs load, and the clip maths behaves. */
class PackTest {
    @Test
    void everyPeopleHasFiftyClipsAndSomethingForEveryMoment() {
        Library library = Library.bundled();
        for (String species : Library.SPECIES) {
            Pack pack = library.pack(species);
            assertNotNull(pack, species + " has no animation pack");
            assertTrue(pack.clips().size() >= 50, species + " has only " + pack.clips().size() + " clips");
            Set<String> ids = new HashSet<>();
            for (Clip clip : pack.clips()) assertTrue(ids.add(clip.id()), "duplicate " + clip.id());
            for (String trigger : Pack.TRIGGERS) {
                Clip grownUp = library.pick(species, trigger, Set.of("adult"), new Random(1));
                assertNotNull(grownUp, species + ": nothing for a grown-up on " + trigger);
                assertFalse(pack.upper().isEmpty(), species + " has no waist");
            }
        }
    }

    @Test
    void tracksAreSmoothAndNeverOvershoot() {
        Track t = new Track(new float[] {0, 1, 2}, new float[] {0, 0, 0, 10, -4, 0, 0, 0, 0});
        float[] out = new float[3];
        t.sample(1, out, 0);
        assertEquals(10, out[0], 1e-4);
        assertEquals(-4, out[1], 1e-4);
        for (float s = 0; s <= 2; s += 0.05F) {
            t.sample(s, out, 0);
            assertTrue(out[0] >= -1e-4 && out[0] <= 10 + 1e-4, "overshoot at " + s + ": " + out[0]);
        }
        t.sample(5, out, 0);
        assertEquals(0, out[0], 1e-6);
    }

    @Test
    void parsesAClipAndMirrorsItsSides() {
        Pack pack = Pack.parse("""
                {"format": 1, "species": "weft", "waist": {"hip": 8.5, "upper": ["body", "head"]}, "clips": [
                  {"id": "wave", "trigger": "greet", "length": 2, "require": ["adult|child", "job:weaver"], "boost": {"social": 2},
                   "tracks": {"right_lower_forearm.rot": [[0, 0, 0, 0], [1, -40, 10, 5], [2, 0, 0, 0]], "root.pos": [[0, 0, 0, 0], [1, 0, -1, 0], [2, 0, 0, 0]]}}
                ]}""");
        Clip clip = pack.clips().getFirst();
        assertEquals("weft:wave", clip.id());
        assertEquals("left_lower_forearm", clip.mirrored()[0]);
        assertTrue(clip.eligible(Set.of("adult", "job:weaver")));
        assertFalse(clip.eligible(Set.of("adult", "job:dyer")));
        assertEquals(2, clip.weightFor(Set.of("social")), 1e-6);
        assertEquals(8.5, pack.hip(), 1e-6);
    }

    @Test
    void rejectsMistakes() {
        assertThrows(IllegalArgumentException.class, () -> Pack.parse("""
                {"format": 1, "species": "weft", "clips": [{"id": "x", "trigger": "dance", "length": 1, "tracks": {"head.rot": [[0, 0, 0, 0]]}}]}"""));
        assertThrows(IllegalArgumentException.class, () -> Pack.parse("""
                {"format": 1, "species": "weft", "clips": [{"id": "x", "length": 1, "tracks": {"waist.pos": [[0, 0, 0, 0]]}}]}"""));
        assertThrows(IllegalArgumentException.class, () -> Pack.parse("""
                {"format": 1, "species": "weft", "clips": [{"id": "x", "length": 1, "tracks": {"head.rot": [[0, 0, 0, 0], [2, 1, 0, 0]]}}]}"""));
    }
}
