package dev.netherexpansion;

import dev.netherexpansion.entity.NetherEntities;
import dev.netherexpansion.entity.Weft;
import dev.netherexpansion.talk.Arrival;
import dev.netherexpansion.talk.Talk;
import dev.netherexpansion.world.NetherStructures;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

/**
 * Nether Expansion: the Nether's five peoples and their villages. Piglins keep their vanilla look but follow
 * the gold rule in full (see {@link dev.netherexpansion.piglin.GoldRule}); the Withren, Magmari, Weft and
 * Ghastkin are new. Every people lives in its own villages and none of them get along.
 */
public final class NetherExpansion implements ModInitializer {
    public static final String MOD_ID = "netherexpansion";
    static final TagKey<Block> WARPED_STEMS = TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace("warped_stems"));

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        NetherEntities.init();
        NetherStructures.init();
        Talk.init();
        Arrival.init();
        // cut the warped forest near a Loom and the Weft come for you
        PlayerBlockBreakEvents.AFTER.register((level, player, pos, state, blockEntity) -> {
            if (!(level instanceof ServerLevel server)) return;
            boolean forest = state.is(WARPED_STEMS) || state.is(Blocks.WARPED_WART_BLOCK) || state.is(Blocks.SHROOMLIGHT)
                    || state.is(Blocks.WARPED_PLANKS) || state.is(Blocks.TWISTING_VINES) || state.is(Blocks.TWISTING_VINES_PLANT);
            if (!forest) return;
            for (Weft weft : server.getEntitiesOfClass(Weft.class, new AABB(pos).inflate(24))) {
                if (!weft.hasHome() || weft.getHomePosition().distSqr(pos) < 48 * 48) weft.holdGrudge(player);
            }
        });
    }
}
