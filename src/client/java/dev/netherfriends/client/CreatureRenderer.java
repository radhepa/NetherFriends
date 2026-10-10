package dev.netherfriends.client;

import java.util.function.BiConsumer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;
import org.jspecify.annotations.Nullable;

/**
 * Draws a creature: its model from its geometry, its texture, and its glow texture at full brightness (the
 * Cairnback's soul fire, the Silkgrub's spots, the Ashbell's heart and beads). A see-through creature (the Ashbell)
 * is drawn translucent.
 */
public class CreatureRenderer<T extends Mob> extends MobRenderer<T, CreatureRenderState, CreatureModel> {
    private final Identifier texture;
    private final @Nullable RenderType glow;
    private final BiConsumer<T, CreatureRenderState> extract;

    public CreatureRenderer(EntityRendererProvider.Context context, String id, float shadow, boolean translucent,
                            BiConsumer<T, CreatureRenderState> extract) {
        super(context, new CreatureModel(context.bakeLayer(Geometry.layer(id)),
                translucent ? id2 -> RenderTypes.entityTranslucent(id2) : id2 -> RenderTypes.entityCutout(id2)), shadow);
        this.texture = Geometry.texture(id);
        this.glow = Geometry.hasGlow(id) ? RenderTypes.eyes(Geometry.texture(id + "_glow")) : null;
        this.extract = extract;
        this.addLayer(new GlowLayer());
    }

    @Override
    public Identifier getTextureLocation(CreatureRenderState state) {
        return this.texture;
    }

    /** The young are the grown model at half size (26.3 doesn't scale babies for us: vanilla draws them with their own models). */
    @Override
    protected void scale(CreatureRenderState state, PoseStack poseStack) {
        super.scale(state, poseStack);
        if (state.isBaby) poseStack.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    public CreatureRenderState createRenderState() {
        return new CreatureRenderState();
    }

    @Override
    public void extractRenderState(T entity, CreatureRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        this.extract.accept(entity, state);
    }

    class GlowLayer extends RenderLayer<CreatureRenderState, CreatureModel> {
        GlowLayer() {
            super(CreatureRenderer.this);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, CreatureRenderState state, float yRot, float xRot) {
            if (CreatureRenderer.this.glow != null && !state.isInvisible) {
                collector.order(1).submitModel(this.getParentModel(), state, poseStack, CreatureRenderer.this.glow, light,
                        OverlayTexture.NO_OVERLAY, state.outlineColor);
            }
        }
    }
}
