package dev.netherfriends.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import dev.netherfriends.entity.Barge;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

/**
 * Draws a strider barge: the raft from its geometry (basalt slabs, chain rails, bench, bow lantern, stern brazier,
 * with the lantern and the brazier's magma glowing), and ahead of it on the tow chains its strider, which is the
 * game's own strider drawn by the game's own renderer (the barge keeps one on the client, Barge.tower).
 */
public class BargeRenderer extends EntityRenderer<Barge, BargeRenderer.State> {
    public static final class State extends EntityRenderState {
        float yRot;
        @Nullable EntityRenderer<Strider, EntityRenderState> towRenderer;
        @Nullable EntityRenderState tow;
    }

    private final Model<State> raft;
    private final Identifier texture = Geometry.texture("barge");
    private final RenderType glow = RenderTypes.eyes(Geometry.texture("barge_glow"));

    public BargeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.raft = new Model<>(context.bakeLayer(Geometry.layer("barge")), RenderTypes::entityCutout) {};
        this.shadowRadius = 1.6F;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void extractRenderState(Barge barge, State state, float partialTicks) {
        super.extractRenderState(barge, state, partialTicks);
        state.yRot = Mth.rotLerp(partialTicks, barge.yRotO, barge.getYRot());
        Strider tower = barge.tower();
        if (tower == null) {
            state.tow = null;
            return;
        }
        state.towRenderer = (EntityRenderer<Strider, EntityRenderState>) this.entityRenderDispatcher.getRenderer(tower);
        state.tow = state.towRenderer.createRenderState(tower, partialTicks);
    }

    @Override
    protected AABB getBoundingBoxForCulling(Barge barge, float partialTicks) {
        return barge.getBoundingBox().inflate(Barge.TOW + 1.5, 2.0, Barge.TOW + 1.5);
    }

    @Override
    public void submit(State state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
        pose.pushPose();
        pose.rotateDegrees(Axis.YP, 180.0F - state.yRot);
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.translate(0.0F, -1.501F, 0.0F);
        collector.submitModel(this.raft, state, pose, this.raft.renderType(this.texture), state.lightCoords, OverlayTexture.NO_OVERLAY,
                state.outlineColor);
        collector.order(1).submitModel(this.raft, state, pose, this.glow, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
        pose.popPose();
        if (state.tow != null && state.towRenderer != null) {
            pose.pushPose();
            pose.translate(state.tow.x - state.x, state.tow.y - state.y, state.tow.z - state.z);
            state.towRenderer.submit(state.tow, pose, collector, camera);
            pose.popPose();
        }
        super.submit(state, pose, collector, camera);
    }
}
