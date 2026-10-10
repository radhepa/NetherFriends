package dev.netherfriends.mixin;

import dev.netherfriends.piglin.GoldRule;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Armour with a gold trim shows gold as well as golden armour does. */
@Mixin(PiglinAi.class)
abstract class PiglinAiMixin {
    @Inject(method = "isWearingSafeArmor", at = @At("RETURN"), cancellable = true)
    private static void netherfriends$goldTrim(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && GoldRule.wearsGoldTrim(entity)) cir.setReturnValue(true);
    }
}
