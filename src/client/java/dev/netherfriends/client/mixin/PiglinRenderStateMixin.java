package dev.netherfriends.client.mixin;

import dev.netherfriends.client.anim.Pose;
import dev.netherfriends.client.anim.PoseHolder;

import net.minecraft.client.renderer.entity.state.PiglinRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/** Piglins carry the clips they're acting out from the director to the model. */
@Mixin(PiglinRenderState.class)
public class PiglinRenderStateMixin implements PoseHolder {
    @Unique
    private final Pose netherfriends$pose = new Pose();

    @Override
    public Pose netherfriends$pose() {
        return this.netherfriends$pose;
    }
}
