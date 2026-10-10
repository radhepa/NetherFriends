package dev.netherexpansion.mixin;

import dev.netherexpansion.entity.Ashbell;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Ghast;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A ghast won't take aim at anyone near an Ashbell: hearing one, it remembers being kin to the Ghastkin. */
@Mixin(Mob.class)
public abstract class MobMixin {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void netherexpansion$ashbellsCalmGhasts(@Nullable LivingEntity target, CallbackInfo ci) {
        if (target != null && (Object) this instanceof Ghast && Ashbell.soothes(target)) ci.cancel();
    }
}
