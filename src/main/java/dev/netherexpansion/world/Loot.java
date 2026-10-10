package dev.netherexpansion.world;

import dev.netherexpansion.NetherExpansion;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

/** The villages' chest loot tables (data/netherexpansion/loot_table/chests/). */
public final class Loot {
    private Loot() {}

    public static final ResourceKey<LootTable> HOLD_HOUSE = chest("hold_house");
    public static final ResourceKey<LootTable> HOLD_HOARD = chest("hold_hoard");
    public static final ResourceKey<LootTable> HOLD_FORGE = chest("hold_forge");
    public static final ResourceKey<LootTable> VIGIL_HOUSE = chest("vigil_house");
    public static final ResourceKey<LootTable> VIGIL_BREWERY = chest("vigil_brewery");
    public static final ResourceKey<LootTable> VIGIL_HALL = chest("vigil_hall");
    public static final ResourceKey<LootTable> MOORING_HOUSE = chest("mooring_house");
    public static final ResourceKey<LootTable> MOORING_DOCK = chest("mooring_dock");
    public static final ResourceKey<LootTable> LOOM_HOUSE = chest("loom_house");
    public static final ResourceKey<LootTable> LOOM_DYES = chest("loom_dyes");
    public static final ResourceKey<LootTable> EAVE_HOUSE = chest("eave_house");
    public static final ResourceKey<LootTable> EAVE_TEARS = chest("eave_tears");

    private static ResourceKey<LootTable> chest(String name) {
        return ResourceKey.create(Registries.LOOT_TABLE, NetherExpansion.id("chests/" + name));
    }
}
