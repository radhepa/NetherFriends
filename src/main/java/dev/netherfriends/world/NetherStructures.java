package dev.netherfriends.world;

import com.mojang.serialization.MapCodec;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.world.village.EaveVillage;
import dev.netherfriends.world.village.HoldVillage;
import dev.netherfriends.world.village.LoomVillage;
import dev.netherfriends.world.village.MooringVillage;
import dev.netherfriends.world.village.VigilVillage;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

/**
 * The five kinds of village and the one piece type they're all built from. Where and how often each
 * appears is data: data/netherfriends/worldgen/structure and structure_set (data packs can change it).
 */
public final class NetherStructures {
    private NetherStructures() {}

    public static final StructurePieceType BUILDING_PIECE = Registry.register(BuiltInRegistries.STRUCTURE_PIECE,
            NetherFriends.id("building"), (StructurePieceType.ContextlessType) BuildingPiece::new);

    public static final StructureType<HoldVillage> HOLD = type("hold", HoldVillage.CODEC);
    public static final StructureType<VigilVillage> VIGIL = type("vigil", VigilVillage.CODEC);
    public static final StructureType<MooringVillage> MOORING = type("mooring", MooringVillage.CODEC);
    public static final StructureType<LoomVillage> LOOM = type("loom", LoomVillage.CODEC);
    public static final StructureType<EaveVillage> EAVE = type("eave", EaveVillage.CODEC);

    private static <S extends Structure> StructureType<S> type(String id, MapCodec<S> codec) {
        return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, NetherFriends.id(id), () -> codec);
    }

    public static void init() {
        Ways.register();
        HoldVillage.register();
        VigilVillage.register();
        MooringVillage.register();
        LoomVillage.register();
        EaveVillage.register();
    }
}
