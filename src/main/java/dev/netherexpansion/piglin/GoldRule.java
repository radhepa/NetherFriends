package dev.netherexpansion.piglin;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * The Piglins' gold rule, as this mod recodes it: show gold and a Piglin will talk to you; show none and it
 * tries to kill you. Gold means a piece of golden armour (vanilla's piglin_safe_armor) or any armour with a
 * gold trim. Brutes, who in vanilla attack everyone, now respect gold too, except in a Bastion's treasure
 * vault, where nobody's gold is good enough.
 */
public final class GoldRule {
    private GoldRule() {}

    /** Armour trimmed with gold counts as showing gold. */
    public static boolean wearsGoldTrim(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
            ItemStack stack = entity.getItemBySlot(slot);
            ArmorTrim trim = stack.get(DataComponents.TRIM);
            if (trim != null && trim.material().is(TrimMaterials.GOLD)) return true;
        }
        return false;
    }

    /** True when a Brute should leave this player alone: they show gold, haven't angered it, and aren't in the vault. */
    public static boolean bruteSpares(ServerLevel level, AbstractPiglin brute, Player player) {
        if (!net.minecraft.world.entity.monster.piglin.PiglinAi.isWearingSafeArmor(player)) return false;
        Optional<UUID> angryAt = brute.getBrain().getMemory(MemoryModuleType.ANGRY_AT);
        if (angryAt.isPresent() && angryAt.get().equals(player.getUUID())) return false;
        return !inTreasureVault(level, player.blockPosition()) && !inTreasureVault(level, brute.blockPosition());
    }

    /** Inside the treasure room of a Bastion Remnant. */
    public static boolean inTreasureVault(ServerLevel level, BlockPos pos) {
        StructureStart start = level.structureManager().getStructureWithPieceAt(pos,
                holder -> holder.is(BuiltinStructures.BASTION_REMNANT));
        if (!start.isValid()) return false;
        for (StructurePiece piece : start.getPieces()) {
            if (piece instanceof PoolElementStructurePiece pool && piece.getBoundingBox().isInside(pos)
                    && pool.getElement().toString().contains("bastion/treasure")) {
                return true;
            }
        }
        return false;
    }
}
