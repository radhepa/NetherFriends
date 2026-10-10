package dev.netherfriends.mixin;

import dev.netherfriends.piglin.GoldRule;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Armour with a gold trim shows gold as well as golden armour does. And when Piglins call each other into a fight
 * with a player, a Piglin that is that player's friend stays out of it.
 */
@Mixin(PiglinAi.class)
abstract class PiglinAiMixin {
    @Inject(method = "isWearingSafeArmor", at = @At("RETURN"), cancellable = true)
    private static void netherfriends$goldTrim(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && GoldRule.wearsGoldTrim(entity)) cir.setReturnValue(true);
    }

    /** Only called when a hurt Piglin rallies the others (hitting this Piglin itself still angers it). */
    @Inject(method = "setAngerTargetIfCloserThanCurrent", at = @At("HEAD"), cancellable = true)
    private static void netherfriends$friendsStayOut(ServerLevel level, AbstractPiglin piglin, LivingEntity target, CallbackInfo ci) {
        if (target instanceof Player player && GoldRule.trusts(piglin, player)) ci.cancel();
    }
}
