package dev.netherfriends.world;

import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Keeps the big natural features out of the chunks a village stands on: giant fungi, basalt columns and
 * pillars, lava deltas, lava springs, fire, and vines from floor and ceiling. Without this a crimson forest
 * grows straight through a Hold. Each people builds its own version (the Weft's fungus trees, the Ghastkin's
 * basalt pillars), so a village reads as a clearing in its biome.
 */
public final class VillageClearing {
    private VillageClearing() {}

    static final Set<String> KEPT_OUT = Set.of(
            "crimson_fungus", "warped_fungus", "delta", "small_basalt_columns", "large_basalt_columns", "basalt_pillar",
            "spring_lava_overworld", "spring_nether_open", "spring_nether_closed", "spring_lava_nether", "spring_lava_frozen",
            "patch_fire", "patch_soul_fire", "twisting_vines", "weeping_vines");

    public static boolean keptOut(Holder<Feature> feature) {
        return feature.unwrapKey().map(key -> key.identifier().getNamespace().equals("minecraft")
                && KEPT_OUT.contains(key.identifier().getPath())).orElse(false);
    }

    /** Whether the chunk at {@code pos} is part of one of this mod's villages. */
    public static boolean inVillage(WorldGenLevel level, BlockPos pos) {
        for (Structure structure : level.getChunk(pos).getAllReferences().keySet()) {
            if (structure instanceof NetherVillage) return true;
        }
        return false;
    }
}
