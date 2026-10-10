package dev.netherfriends.mixin;

import dev.netherfriends.piglin.GoldRule;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.PiglinSpecificSensor;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The sensor picks the nearest player showing no gold for a Piglin to attack. A Piglin's friends are left out of
 * that pick (GoldRule.trusts): it looks past them to the next gold-less stranger, if any.
 */
@Mixin(PiglinSpecificSensor.class)
abstract class PiglinSpecificSensorMixin {
    @Inject(method = "doTick", at = @At("TAIL"))
    private void netherfriends$spareFriends(ServerLevel level, LivingEntity body, CallbackInfo ci) {
        if (!(body instanceof AbstractPiglin piglin)) return;
        Brain<?> brain = piglin.getBrain();
        if (!(brain.getMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD).orElse(null) instanceof Player friend)
                || !GoldRule.trusts(piglin, friend)) {
            return;
        }
        brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES)
                .orElse(NearestVisibleLivingEntities.empty())
                .findClosest(e -> e instanceof Player p && !PiglinAi.isWearingSafeArmor(p) && body.canAttack(p) && !GoldRule.trusts(piglin, p))
                .map(Player.class::cast));
    }
}
