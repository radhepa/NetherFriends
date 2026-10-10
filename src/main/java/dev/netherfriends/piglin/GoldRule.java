package dev.netherfriends.piglin;

import java.util.Optional;
import java.util.UUID;

import dev.netherfriends.talk.Bonds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
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
 * vault, where nobody's gold is good enough. A Piglin whose Ingot-friend you are (friendship level 3) knows your
 * face and treats you as showing gold even with none on: it won't attack you for it, and when other Piglins pick a
 * fight with you it stays out. Hit that Piglin itself, or rob a chest in front of it, and it fights like any other.
 */
public final class GoldRule {
    private GoldRule() {}

    /** The friendship level (each people's level 3; for Piglins, Ingot-friend) at which a Piglin no longer needs to see gold. */
    public static final int TRUST_LEVEL = 3;

    /** This Piglin knows the player well enough that it doesn't need to see gold on them. */
    public static boolean trusts(AbstractPiglin piglin, Player player) {
        return Bonds.level(piglin, player.getUUID()) >= TRUST_LEVEL;
    }

    /** The player counts as showing gold to this Piglin: they wear it, or they're its friend and don't need to. */
    public static boolean showsGold(AbstractPiglin piglin, Player player) {
        return PiglinAi.isWearingSafeArmor(player) || trusts(piglin, player);
    }

    /** Armour trimmed with gold counts as showing gold. */
    public static boolean wearsGoldTrim(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlotGroup.ARMOR) {
            ItemStack stack = entity.getItemBySlot(slot);
            ArmorTrim trim = stack.get(DataComponents.TRIM);
            if (trim != null && trim.material().is(TrimMaterials.GOLD)) return true;
        }
        return false;
    }

    /** True when a Brute should leave this player alone: they show it gold (or are its friend), haven't angered it, and aren't in the vault. */
    public static boolean bruteSpares(ServerLevel level, AbstractPiglin brute, Player player) {
        if (!showsGold(brute, player)) return false;
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
