package dev.netherfriends.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import dev.netherfriends.talk.FerryPayload;
import dev.netherfriends.NetherFriends;
import java.util.List;

import dev.netherfriends.client.ResidentRenderer.Look;
import dev.netherfriends.entity.Ashbell;
import dev.netherfriends.entity.Cairnback;
import dev.netherfriends.entity.Ghastkin;
import dev.netherfriends.entity.Magmari;
import dev.netherfriends.entity.NetherEntities;
import dev.netherfriends.entity.Silkgrub;
import dev.netherfriends.entity.Weft;
import dev.netherfriends.entity.Withren;

import dev.netherfriends.talk.TalkPayload;
import dev.netherfriends.anim.Cue;
import dev.netherfriends.client.anim.Life;
import dev.netherfriends.entity.Resident;
import dev.netherfriends.talk.Folk;
import java.util.Locale;
import java.util.Set;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;

/** Client wiring: a model layer per geometry file (and its borrowed layer), and a renderer per people and creature. */
public final class NetherFriendsClient implements ClientModInitializer {
    static final List<String> GEOMETRY = List.of(
            "withren", "withren_child", "withren_remembrancer", "withren_ashwarden",
            "magmari", "magmari_female", "magmari_ferrier", "magmari_child",
            "weft", "weft_female", "weft_child",
            "ghastkin", "ghastkin_female", "ghastkin_child",
            "cairnback", "silkgrub", "ashbell", "barge");

    @Override
    public void onInitializeClient() {
        // the conversation window opens on a greeting and updates in place as the conversation goes on
        ClientPlayNetworking.registerGlobalReceiver(TalkPayload.TYPE, (payload, context) -> {
            var client = context.client();
            if (client.gui.screen() instanceof TalkScreen screen && screen.matches(payload)) screen.update(payload);
            else if (payload.opening() && client.player != null) client.gui.setScreen(new TalkScreen(payload));
        });
        // the ferry: the conversation window closes once a crossing is booked; the crossing darkens the screen
        ClientPlayNetworking.registerGlobalReceiver(FerryPayload.TYPE, (payload, context) -> {
            var client = context.client();
            switch (payload.phase()) {
                case FerryPayload.BOOKED -> {
                    if (client.gui.screen() instanceof TalkScreen screen) screen.onClose();
                }
                case FerryPayload.CROSS -> CrossingOverlay.start(payload.lines());
                default -> CrossingOverlay.clear();
            }
        });
        HudElementRegistry.addLast(NetherFriends.id("crossing"), CrossingOverlay::extract);
        animations();
        EntityRendererRegistry.register(NetherEntities.BARGE, BargeRenderer::new);
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

    /**
     * The peoples' animation packs (docs/ANIMATIONS.md): who plays them, when they can't (fighting, climbing,
     * frightened, a Piglin admiring gold or dancing), and the tags that choose clips (job, gender, a cold Magmari).
     */
    static void animations() {
        Life.register(EntityTypes.PIGLIN, new Life.Kind("piglin", e -> e instanceof AbstractPiglin p && !p.isAggressive()
                && !p.isConverting() && p.getArmPose() == PiglinArmPose.DEFAULT, NetherFriendsClient::tags));
        Life.register(EntityTypes.PIGLIN_BRUTE, new Life.Kind("piglin", e -> e instanceof AbstractPiglin p && !p.isAggressive()
                && !p.isConverting(), (e, tags) -> {
            tags(e, tags);
            tags.add("brute");
        }));
        Life.register(NetherEntities.WITHREN, new Life.Kind("withren", e -> !((Mob) e).isAggressive(), NetherFriendsClient::tags));
        Life.register(NetherEntities.MAGMARI, new Life.Kind("magmari", e -> !((Mob) e).isAggressive(), (e, tags) -> {
            tags(e, tags);
            if (((Magmari) e).isCold()) tags.add("cold");
        }));
        Life.register(NetherEntities.WEFT, new Life.Kind("weft", e -> !((Mob) e).isAggressive() && !((Weft) e).isClimbing(), NetherFriendsClient::tags));
        Life.register(NetherEntities.GHASTKIN, new Life.Kind("ghastkin", e -> !((Mob) e).isAggressive() && !((Ghastkin) e).isFrightened(),
                NetherFriendsClient::tags));
        // in a conversation: 2 while their line types out, 1 while you choose what to say
        Life.conversation = e -> Minecraft.getInstance().gui.screen() instanceof TalkScreen t && t.payload().entityId() == e.getId()
                ? (t.speaking() ? 2 : 1) : 0;
        ClientTickEvents.END_CLIENT_TICK.register(Life::tickAll);
        ClientPlayNetworking.registerGlobalReceiver(Cue.TYPE, (cue, context) -> Life.cue(cue.entityId(), cue.trigger()));
    }

    /** Tags every people shares: their job ("job:lava_angler") and, where they have one, their gender. */
    static void tags(LivingEntity e, Set<String> tags) {
        String job = e.getAttached(Folk.JOB);
        if (job != null && !job.isEmpty()) tags.add("job:" + job.toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_'));
        if (e instanceof Resident r && r.people().gendered) tags.add(r.isFemale() ? "female" : "male");
    }
}
