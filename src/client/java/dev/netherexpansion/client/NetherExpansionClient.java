package dev.netherexpansion.client;

import java.util.List;

import dev.netherexpansion.client.ResidentRenderer.Look;
import dev.netherexpansion.entity.Ashbell;
import dev.netherexpansion.entity.Cairnback;
import dev.netherexpansion.entity.Ghastkin;
import dev.netherexpansion.entity.Magmari;
import dev.netherexpansion.entity.NetherEntities;
import dev.netherexpansion.entity.Silkgrub;
import dev.netherexpansion.entity.Weft;
import dev.netherexpansion.entity.Withren;

import dev.netherexpansion.talk.TalkPayload;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

/** Client wiring: a model layer per geometry file (and its borrowed layer), and a renderer per people and creature. */
public final class NetherExpansionClient implements ClientModInitializer {
    static final List<String> GEOMETRY = List.of(
            "withren", "withren_child", "withren_remembrancer", "withren_ashwarden",
            "magmari", "magmari_female", "magmari_ferrier", "magmari_child",
            "weft", "weft_female", "weft_child",
            "ghastkin", "ghastkin_female", "ghastkin_child",
            "cairnback", "silkgrub", "ashbell");

    @Override
    public void onInitializeClient() {
        // the conversation window opens on a greeting and updates in place as the conversation goes on
        ClientPlayNetworking.registerGlobalReceiver(TalkPayload.TYPE, (payload, context) -> {
            var client = context.client();
            if (client.gui.screen() instanceof TalkScreen screen && screen.matches(payload)) screen.update(payload);
            else if (payload.opening() && client.player != null) client.gui.setScreen(new TalkScreen(payload));
        });
        for (String id : GEOMETRY) {
            ModelLayerRegistry.registerModelLayer(Geometry.layer(id), () -> Geometry.load(id));
            if (Geometry.borrowed(id) != null) {
                ModelLayerRegistry.registerModelLayer(Geometry.borrowedLayer(id), () -> Geometry.loadBorrowed(id));
            }
        }

        // Withren: ordinary, Remembrancer, Ashwarden, and the child (classic half-size body under the full-size Wither head)
        EntityRendererRegistry.register(NetherEntities.WITHREN, ctx -> new ResidentRenderer<Withren>(ctx, List.of(
                new Look("withren", "withren"), new Look("withren_remembrancer", "withren_remembrancer"),
                new Look("withren_ashwarden", "withren_ashwarden"), new Look("withren_child", "withren")), 0.5F,
                w -> w.isBaby() ? 3 : w.role() == Withren.REMEMBRANCER ? 1 : w.role() == Withren.ASHWARDEN ? 2 : 0,
                (w, s) -> {}));

        // Magmari: male, female, ferrier, child, each warm or cold (the ferrier has no cold texture)
        EntityRendererRegistry.register(NetherEntities.MAGMARI, ctx -> new ResidentRenderer<Magmari>(ctx, List.of(
                new Look("magmari", "magmari"), new Look("magmari", "magmari_cold"),
                new Look("magmari_female", "magmari_female"), new Look("magmari_female", "magmari_female_cold"),
                new Look("magmari_ferrier", "magmari_ferrier"), new Look("magmari_ferrier", "magmari_ferrier"),
                new Look("magmari_child", "magmari_child"), new Look("magmari_child", "magmari_child_cold")), 0.7F,
                m -> (m.isBaby() ? 6 : m.role() == Magmari.FERRIER ? 4 : m.isFemale() ? 2 : 0) + (m.isCold() ? 1 : 0),
                (m, s) -> s.cold = m.isCold()));

        EntityRendererRegistry.register(NetherEntities.WEFT, ctx -> new ResidentRenderer<Weft>(ctx, List.of(
                new Look("weft", "weft"), new Look("weft_female", "weft_female"), new Look("weft_child", "weft_child")), 0.55F,
                w -> w.isBaby() ? 2 : w.isFemale() ? 1 : 0, (w, s) -> s.climbing = w.isClimbing()));

        EntityRendererRegistry.register(NetherEntities.GHASTKIN, ctx -> new ResidentRenderer<Ghastkin>(ctx, List.of(
                new Look("ghastkin", "ghastkin"), new Look("ghastkin_female", "ghastkin_female"), new Look("ghastkin_child", "ghastkin_child")), 0.35F,
                g -> g.isBaby() ? 2 : g.isFemale() ? 1 : 0, (g, s) -> s.frightened = g.isFrightened()));

        // the creatures: the Withren's Cairnback, the Weft's Silkgrub, the Ghastkin's see-through Ashbell
        EntityRendererRegistry.register(NetherEntities.CAIRNBACK, ctx -> new CreatureRenderer<Cairnback>(ctx, "cairnback", 0.7F, false, (c, s) -> {}));
        EntityRendererRegistry.register(NetherEntities.SILKGRUB, ctx -> new CreatureRenderer<Silkgrub>(ctx, "silkgrub", 0.4F, false, (g, s) -> {
            s.silk = g.silk();
            s.climbing = g.isClimbing();
        }));
        EntityRendererRegistry.register(NetherEntities.ASHBELL, ctx -> new CreatureRenderer<Ashbell>(ctx, "ashbell", 0.3F, true, (a, s) -> {}));
    }
}
