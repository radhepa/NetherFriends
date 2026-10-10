package dev.netherfriends.mixin;

import java.util.Optional;

import dev.netherfriends.piglin.GoldRule;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBruteAi;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Brutes respect gold like every other Piglin, except in a Bastion's treasure vault. */
@Mixin(PiglinBruteAi.class)
abstract class PiglinBruteAiMixin {
    @Inject(method = "findNearestValidAttackTarget", at = @At("RETURN"), cancellable = true)
    private static void netherfriends$respectGold(ServerLevel level, AbstractPiglin body,
                                                     CallbackInfoReturnable<Optional<? extends LivingEntity>> cir) {
        if (cir.getReturnValue().orElse(null) instanceof Player player && GoldRule.bruteSpares(level, body, player)) {
            cir.setReturnValue(body.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS));
        }
    }
}
