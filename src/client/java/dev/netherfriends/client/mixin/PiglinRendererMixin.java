package dev.netherfriends.client.mixin;

import dev.netherfriends.client.anim.Life;
import dev.netherfriends.client.anim.PoseHolder;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.PiglinRenderer;
import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** A Piglin's clips for this frame, from its director. The vanilla model and skin are untouched: only the pose moves. */
@Mixin(PiglinRenderer.class)
public class PiglinRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/piglin/AbstractPiglin;Lnet/minecraft/client/renderer/entity/state/PiglinRenderState;F)V",
            at = @At("TAIL"))
    private void netherfriends$pose(AbstractPiglin piglin, PiglinRenderState state, float partialTicks, CallbackInfo ci) {
        var pose = ((PoseHolder) state).netherfriends$pose();
        Life.fill(piglin, pose, partialTicks);
        pose.holdingRight = state.rightArmPose != HumanoidModel.ArmPose.EMPTY;
        pose.holdingLeft = state.leftArmPose != HumanoidModel.ArmPose.EMPTY;
    }
}
