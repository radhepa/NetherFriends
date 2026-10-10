package dev.netherfriends.client.mixin;

import dev.netherfriends.client.anim.PoseHolder;
import dev.netherfriends.client.anim.Poser;

import net.minecraft.client.model.monster.piglin.PiglinModel;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Adds the Piglin's clips after vanilla has posed it (look, gait, ears). */
@Mixin(PiglinModel.class)
public abstract class PiglinModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/PiglinRenderState;)V", at = @At("TAIL"))
    private void netherfriends$animate(PiglinRenderState state, CallbackInfo ci) {
        Poser.apply(((PiglinModel) (Object) this).root(), ((PoseHolder) state).netherfriends$pose());
    }
}
