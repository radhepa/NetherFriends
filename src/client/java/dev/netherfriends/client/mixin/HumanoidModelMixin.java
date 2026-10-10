package dev.netherfriends.client.mixin;

import dev.netherfriends.client.anim.PoseHolder;
import dev.netherfriends.client.anim.Poser;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A Piglin's armour (and anything else drawn from a Piglin's render state on a plain humanoid model) follows its clips too. */
@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    private void netherfriends$animate(HumanoidRenderState state, CallbackInfo ci) {
        if ((Object) this instanceof PiglinModel || !(state instanceof PoseHolder holder)) return;
        Poser.apply(((HumanoidModel<?>) (Object) this).root(), holder.netherfriends$pose());
    }
}
