package dev.netherexpansion.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.ToIntFunction;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.netherexpansion.entity.Resident;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

/**
 * Draws one people. A people has several looks (male, female, child, a role, a cold texture); each look is
 * a model from its geometry plus a texture, a glow texture when something on it glows, and the borrowed
 * vanilla cubes (the Withren's Wither head) when it has any. The entity's state picks the look.
 */
public class ResidentRenderer<T extends Resident> extends MobRenderer<T, ResidentRenderState, ResidentModel> {
    /** A model and the texture drawn on it. */
    public record Look(String geometry, String texture) {}

    record Baked(ResidentModel model, Identifier texture, @Nullable RenderType glow, @Nullable ResidentModel borrowed,
                 @Nullable Identifier borrowedTexture) {}

    private final List<Baked> looks = new ArrayList<>();
    private final ToIntFunction<T> pick;
    private final BiConsumer<T, ResidentRenderState> extract;

    public ResidentRenderer(EntityRendererProvider.Context context, List<Look> looks, float shadow, ToIntFunction<T> pick,
                            BiConsumer<T, ResidentRenderState> extract) {
        super(context, new ResidentModel(context.bakeLayer(Geometry.layer(looks.getFirst().geometry()))), shadow);
        Map<String, ResidentModel> models = new HashMap<>();
        Map<String, ResidentModel> borrowedModels = new HashMap<>();
        for (Look look : looks) {
            ResidentModel model = models.computeIfAbsent(look.geometry(), g -> new ResidentModel(context.bakeLayer(Geometry.layer(g))));
            String borrowed = Geometry.borrowed(look.geometry());
            ResidentModel borrowedModel = borrowed == null ? null
                    : borrowedModels.computeIfAbsent(look.geometry(), g -> new ResidentModel(context.bakeLayer(Geometry.borrowedLayer(g))));
            Identifier borrowedTexture = borrowed == null ? null : Identifier.withDefaultNamespace("textures/" + borrowed + ".png");
            RenderType glow = Geometry.hasGlow(look.texture()) ? RenderTypes.eyes(Geometry.texture(look.texture() + "_glow")) : null;
            this.looks.add(new Baked(model, Geometry.texture(look.texture()), glow, borrowedModel, borrowedTexture));
        }
        this.pick = pick;
        this.extract = extract;
        this.addLayer(new BorrowedLayer());
        this.addLayer(new GlowLayer());
    }

    private Baked look(ResidentRenderState state) {
        return this.looks.get(Math.floorMod(state.look, this.looks.size()));
    }

    @Override
    public void submit(ResidentRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        this.model = this.look(state).model();
        super.submit(state, poseStack, collector, camera);
    }

    @Override
    public Identifier getTextureLocation(ResidentRenderState state) {
        return this.look(state).texture();
    }

    @Override
    public ResidentRenderState createRenderState() {
        return new ResidentRenderState();
    }

    @Override
    public void extractRenderState(T entity, ResidentRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.look = this.pick.applyAsInt(entity);
        state.swing = entity.getSwingAnimation(partialTicks);
        this.extract.accept(entity, state);
    }

    /** The cubes drawn with a vanilla texture (the Withren's Wither head). */
    class BorrowedLayer extends RenderLayer<ResidentRenderState, ResidentModel> {
        BorrowedLayer() {
            super(ResidentRenderer.this);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, ResidentRenderState state, float yRot, float xRot) {
            Baked look = ResidentRenderer.this.look(state);
            if (look.borrowed() != null) {
                coloredCutoutModelCopyLayerRender(look.borrowed(), look.borrowedTexture(), poseStack, collector, light, state, -1, 0);
            }
        }
    }

    /** Glowing seams, eyes, lanterns and tears, drawn at full brightness. */
    class GlowLayer extends RenderLayer<ResidentRenderState, ResidentModel> {
        GlowLayer() {
            super(ResidentRenderer.this);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector collector, int light, ResidentRenderState state, float yRot, float xRot) {
            Baked look = ResidentRenderer.this.look(state);
            if (look.glow() != null && !state.isInvisible) {
                collector.order(1).submitModel(this.getParentModel(), state, poseStack, look.glow(), light, OverlayTexture.NO_OVERLAY, state.outlineColor);
            }
        }
    }
}
