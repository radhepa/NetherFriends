package dev.netherfriends.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.anim.Clip;
import dev.netherfriends.anim.Cue;
import dev.netherfriends.anim.Library;
import dev.netherfriends.anim.Pack;
import dev.netherfriends.client.anim.Life;
import dev.netherfriends.client.TalkScreen;
import dev.netherfriends.entity.Ashbell;
import dev.netherfriends.entity.Barge;
import dev.netherfriends.entity.Cairnback;
import dev.netherfriends.entity.NetherEntities;
import dev.netherfriends.entity.Silkgrub;
import dev.netherfriends.entity.People;
import dev.netherfriends.piglin.GoldRule;
import dev.netherfriends.talk.Bonds;
import dev.netherfriends.talk.Ferry;
import dev.netherfriends.talk.Folk;
import dev.netherfriends.talk.Journal;
import dev.netherfriends.talk.Story;
import dev.netherfriends.talk.Talk;
import dev.netherfriends.talk.TalkPayload;
import dev.netherfriends.world.BuildingPiece;
import dev.netherfriends.world.Sea;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotOptions;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

/**
 * Drives the real game. "Peoples": every look of every people in a lit pen, photographed. "Villages": each kind
 * of village is found by natural world generation in a fresh Nether (as /locate would), visited, checked for
 * residents (each with a name and a job) and photographed from a few sides. "Talk": a conversation with each people,
 * through the real window: greeting, a topic, a gift and a level-up. "Story": every people's stories load, and one
 * resident's story is played through on the Story tab, then read back on the Journal tab's three pages.
 * "Ferry": a Mooring's Ferrier takes the player across the lava sea to another Mooring (with -PnetherFilm, filmed
 * frame by frame into ferry-frames/ for tools/film_ferry.py). "Creatures": the Cairnback, Silkgrub and Ashbell,
 * photographed, then each one's use tried for real (relighting, shearing, calming a ghast). "Animations": every
 * people's pack loads with fifty clips or more; a row of each people acts out every clip in turn (photographed
 * mid-motion), and a reaction cue from the server plays. "Loot": every loot table of the mod rolled hundreds of times
 * (chest counts vary, potions are real potions, the Cairnback's bone meal comes half the time), so we know 26.3
 * reads them. Screenshots land in build/run/clientGameTest/screenshots.
 *   gradlew runClientGameTest -PtestHeap=3g [-PnetherTests=Villages]
 */
@SuppressWarnings("UnstableApiUsage")
public class NetherClientTest implements FabricClientGameTest {
    static final String[] VILLAGES = {"hold", "vigil", "mooring", "loom", "eave"};

    static void check(boolean ok, String what) {
        if (!ok) throw new AssertionError(what);
    }

    static boolean wanted(String test) {
        String only = System.getProperty("netherfriends.tests");
        return only == null || only.isBlank() || List.of(only.split(",")).contains(test);
    }

    static void shoot(ClientGameTestContext ctx, String name) {
        ctx.runOnClient(client -> client.gui.toastManager().clear());
        ctx.takeScreenshot(name);
    }

    @Override
    public void runTest(ClientGameTestContext ctx) {
        ctx.getInput().resizeWindow(1600, 900);
        // a normal world with structures on: the villages must come from real Nether generation
        String seed = System.getProperty("netherfriends.seed", "7");
        try (TestSingleplayerContext world = ctx.worldBuilder().adjustSettings(ui -> {
            var normal = ui.getSettings().worldgenLoadContext().lookupOrThrow(Registries.WORLD_PRESET).getOrThrow(WorldPresets.NORMAL);
            ui.setWorldType(new WorldCreationUiState.WorldTypeEntry(normal));
            ui.setGenerateStructures(true);
            ui.setSeed(seed);
        }).create()) {
            world.getConnection().waitForChunksRender();
            ctx.runOnClient(client -> {
                if (!client.gui.hud.isHidden()) client.gui.hud.toggle();
            });
            TestServerContext server = world.getServer();
            for (String cmd : List.of("gamemode creative @a", "gamerule advance_time false", "gamerule advance_weather false",
                    "gamerule spawn_mobs false", "time set 6000", "weather clear", "effect give @a night_vision infinite 0 true")) {
                server.runCommand(cmd);
            }
            if (wanted("Peoples")) peoples(ctx, world, server);
            if (wanted("Talk")) talk(ctx, world, server);
            if (wanted("Story")) story(ctx, world, server);
            if (wanted("Ferry")) ferry(ctx, world, server);
            if (wanted("Creatures")) creatures(ctx, world, server);
            if (wanted("Animations")) animations(ctx, world, server);
            if (wanted("Villages")) villages(ctx, world, server);
            if (wanted("Loot")) loot(server);
        }
    }

    // -- the peoples, every look -------------------------------------------------------------------------------

    record Sitter(String type, String nbt) {}

    void peoples(ClientGameTestContext ctx, TestSingleplayerContext world, TestServerContext server) {
        List<Sitter> row = List.of(
                new Sitter("withren", "Role:0"), new Sitter("withren", "Role:1"), new Sitter("withren", "Role:2"), new Sitter("withren", "Age:-24000"),
                new Sitter("magmari", "Gender:0"), new Sitter("magmari", "Gender:1"), new Sitter("magmari", "Role:1"), new Sitter("magmari", "Age:-24000"),
                new Sitter("magmari", "Gender:0,Cold:1b,Warmth:0"), new Sitter("magmari", "Gender:1,Cold:1b,Warmth:0"), new Sitter("magmari", "Age:-24000,Cold:1b,Warmth:0"),
                new Sitter("weft", "Gender:0"), new Sitter("weft", "Gender:1"), new Sitter("weft", "Age:-24000"),
                new Sitter("ghastkin", "Gender:0"), new Sitter("ghastkin", "Gender:1"), new Sitter("ghastkin", "Age:-24000"));
        int perRow = 6;
        // a pen of blackstone in the open air, a nether-brick wall behind
        server.runCommand("tp @a 0 150 -10 0 10");
        world.getConnection().waitForChunksRender();
        ctx.waitTicks(20);
        for (String cmd : List.of("fill -16 149 -8 16 149 16 blackstone", "fill -16 150 -8 16 162 16 air",
                "fill -16 150 16 16 155 16 nether_bricks", "kill @e[type=!player]")) {
            server.runCommand(cmd);
        }
        ctx.waitTicks(20);
        for (int i = 0; i < row.size(); i++) {
            Sitter s = row.get(i);
            double x = (i % perRow - (perRow - 1) / 2.0) * 3.0, z = 1 + (i / perRow) * 4.0;
            server.runCommand(String.format("summon netherfriends:%s %.1f 150 %.1f {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f],%s}",
                    s.type, x, z, s.nbt));
        }
        world.getConnection().waitForClientboundEntityUpdates(NetherEntities.GHASTKIN);
        ctx.waitTicks(40);
        for (NetherEntities.Entry e : NetherEntities.all()) {
            long n = row.stream().filter(s -> s.type.equals(e.id())).count();
            int found = server.computeOnServer(srv -> srv.overworld().getEntities(e.type(), new AABB(-20, 146, -10, 20, 164, 20), x -> true).size());
            check(found == n, e.id() + ": expected " + n + ", found " + found);
        }
        shoot(ctx, "nether-01-peoples");
        server.runCommand("tp @a 0 152 -7 0 18");
        ctx.waitTicks(10);
        shoot(ctx, "nether-02-peoples-high");
        for (int r = 0; r < 3; r++) {
            server.runCommand(String.format("tp @a 0 150.5 %.1f 0 6", -4.5 + r * 4.0));
            ctx.waitTicks(10);
            shoot(ctx, "nether-0" + (3 + r) + "-peoples-row" + (r + 1));
        }
        server.runCommand("kill @e[type=!player]");
    }

    // -- animations ---------------------------------------------------------------------------------------------------

    /** A people's row for the animation photos: five grown-ups (every look) and a child, last. */
    record Row(String people, String type, List<String> nbt) {}

    static final List<Row> ROWS = List.of(
            new Row("piglin", "minecraft:piglin", List.of("IsImmuneToZombification:1b", "IsImmuneToZombification:1b",
                    "IsImmuneToZombification:1b", "IsImmuneToZombification:1b", "IsImmuneToZombification:1b", "IsImmuneToZombification:1b,IsBaby:1b")),
            new Row("withren", "netherfriends:withren", List.of("Role:0", "Role:1", "Role:2", "Role:0", "Role:1", "Age:-24000")),
            new Row("magmari", "netherfriends:magmari", List.of("Gender:0", "Gender:1", "Role:1", "Gender:0", "Gender:1", "Age:-24000")),
            new Row("weft", "netherfriends:weft", List.of("Gender:0", "Gender:1", "Gender:0", "Gender:1", "Gender:0", "Age:-24000")),
            new Row("ghastkin", "netherfriends:ghastkin", List.of("Gender:0", "Gender:1", "Gender:0", "Gender:1", "Gender:0", "Age:-24000")));

    static List<LivingEntity> rowOnClient(net.minecraft.client.Minecraft client, String people) {
        return client.level.getEntitiesOfClass(LivingEntity.class, new AABB(-12, 146, -4, 12, 156, 4),
                        e -> Life.kind(e) != null && Life.kind(e).species().equals(people)).stream()
                .sorted(java.util.Comparator.comparingDouble(Entity::getX)).toList();
    }

    void animations(ClientGameTestContext ctx, TestSingleplayerContext world, TestServerContext server) {
        ctx.runOnClient(client -> {
            Library library = Library.current();
            for (String people : Library.SPECIES) {
                Pack pack = library.pack(people);
                check(pack != null, people + " has no animation pack");
                check(pack.clips().size() >= 50, people + " has only " + pack.clips().size() + " clips");
                for (String trigger : Pack.TRIGGERS) {
                    check(!library.clips(people, trigger).isEmpty(), people + " has no clip for " + trigger);
                }
            }
        });
        for (String cmd : List.of("kill @e[type=!player]", "gamemode creative @a", "tp @a 0 150.5 -8 0 8",
                "fill -12 149 -12 12 149 6 blackstone", "fill -12 150 -12 12 158 6 air", "fill -12 150 6 12 156 6 nether_bricks")) {
            server.runCommand(cmd);
        }
        world.getConnection().waitForChunksRender();
        ctx.waitTicks(20);
        // the floor goes in once the chunks are there, and the player is put back on it
        for (String cmd : List.of("fill -12 149 -12 12 149 6 blackstone", "fill -12 150 -12 12 158 6 air",
                "fill -12 150 6 12 156 6 nether_bricks", "tp @a 0 150 -8 0 8")) {
            server.runCommand(cmd);
        }
        ctx.runOnClient(client -> {
            if (!client.gui.hud.isHidden()) client.gui.hud.toggle();
        });
        ctx.waitTicks(10);
        List<String> report = new ArrayList<>();
        for (Row row : ROWS) {
            server.runCommand("kill @e[type=!player]");
            for (int i = 0; i < row.nbt().size(); i++) {
                server.runCommand(String.format("summon %s %.1f 150 0.5 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f],%s}",
                        row.type(), (i - (row.nbt().size() - 1) / 2.0) * 2.2, row.nbt().get(i)));
            }
            ctx.waitFor(client -> rowOnClient(client, row.people()).size() == row.nbt().size(), 200);
            ctx.waitTicks(10);
            List<Clip> clips = Library.current().pack(row.people()).clips();
            List<Clip> young = clips.stream().filter(c -> c.require().stream().anyMatch(g -> g.equals(Set.of("child")))).toList();
            List<Clip> grown = clips.stream().filter(c -> !young.contains(c)).toList();
            int grownSlots = row.nbt().size() - 1;
            int shots = Math.max((grown.size() + grownSlots - 1) / grownSlots, young.size());
            for (int shot = 0; shot < shots; shot++) {
                int first = shot * grownSlots, n = shot;
                List<String> cast = ctx.computeOnClient(client -> {
                    List<LivingEntity> folk = rowOnClient(client, row.people());
                    List<String> playing = new ArrayList<>();
                    for (int i = 0; i < folk.size(); i++) {
                        LivingEntity e = folk.get(i);
                        Clip clip = i == folk.size() - 1 ? (n < young.size() ? young.get(n) : grown.get(n % grown.size()))
                                : first + i < grown.size() ? grown.get(first + i) : null;
                        if (clip == null) continue;
                        check(Life.of(e).play(e, clip.id()), row.people() + ": could not play " + clip.id());
                        playing.add(e.getId() + "=" + clip.id().substring(clip.id().indexOf(':') + 1));
                    }
                    return playing;
                });
                ctx.waitTicks(18);
                ctx.runOnClient(client -> {
                    for (LivingEntity e : rowOnClient(client, row.people())) {
                        String id = cast.stream().filter(c -> c.startsWith(e.getId() + "=")).findFirst().orElse(null);
                        if (id == null) continue;
                        var now = Life.of(e).activity();
                        check(now != null && now.id().endsWith(":" + id.substring(id.indexOf('=') + 1)),
                                row.people() + ": " + id + " stopped before its photo");
                    }
                });
                String name = String.format("nether-anim-%s-%02d", row.people(), shot + 1);
                shoot(ctx, name);
                report.add(name + ": " + String.join(", ", cast));
            }
            // a reaction from the server: everyone watching sees it
            server.runOnServer(srv -> {
                ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
                LivingEntity first = srv.overworld().getEntitiesOfClass(LivingEntity.class, new AABB(-12, 146, -4, 12, 156, 4),
                        e -> People.of(e) != null).getFirst();
                Cue.send(first, "happy", player);
            });
            ctx.waitFor(client -> rowOnClient(client, row.people()).stream().anyMatch(e -> Life.of(e).reaction() != null
                    && Life.of(e).describe().endsWith("/happy")), 100);
            report.add(row.people() + ": " + clips.size() + " clips, happy cue played");
        }
        server.runCommand("kill @e[type=!player]");
        System.out.println("[netherfriends] animations:" + System.lineSeparator() + String.join(System.lineSeparator(), report));
    }

    // -- talking ------------------------------------------------------------------------------------------------------

    record Speaker(String type, String nbt, String label, boolean sneak) {}

    static final int KEY_SPACE = 32, KEY_1 = 49;

    static LivingEntity speakerNear(net.minecraft.server.MinecraftServer srv) {
        return srv.overworld().getEntitiesOfClass(LivingEntity.class, new AABB(-4, 148, -4, 4, 154, 4), e -> People.of(e) != null).getFirst();
    }

    /**
     * Leave the conversation as Escape or Goodbye does. (26.3 knows Escape by its scancode, which the test's
     * simulated key press doesn't carry, so this calls the window's close directly.)
     */
    static void goodbye(ClientGameTestContext ctx) {
        ctx.runOnClient(client -> client.gui.screen().onClose());
    }

    void talk(ClientGameTestContext ctx, TestSingleplayerContext world, TestServerContext server) {
        server.runCommand("tp @a 0 150 -10 0 10");
        world.getConnection().waitForChunksRender();
        ctx.waitTicks(20);
        for (String cmd : List.of("fill -8 149 -8 8 149 8 blackstone", "fill -8 150 -8 8 158 8 air", "fill -8 150 8 8 155 8 nether_bricks",
                "kill @e[type=!player]", "gamemode survival @a", "clear @a", "item replace entity @a armor.head with golden_helmet")) {
            server.runCommand(cmd);
        }
        List<Speaker> speakers = List.of(
                new Speaker("minecraft:piglin", "IsImmuneToZombification:1b", "piglin", false),
                new Speaker("netherfriends:withren", "Role:1", "withren", false),
                new Speaker("netherfriends:magmari", "Gender:1", "magmari", false),
                new Speaker("netherfriends:weft", "Gender:0", "weft", false),
                new Speaker("netherfriends:ghastkin", "Gender:1", "ghastkin", true),
                new Speaker("netherfriends:withren", "Age:-24000", "withren-child", false));
        List<String> report = new ArrayList<>();
        for (Speaker sp : speakers) {
            server.runCommand("kill @e[type=!player]");
            server.runCommand("tp @a 0 150 -3 0 0");
            server.runCommand("summon " + sp.type + " 0 150 0.5 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f]," + sp.nbt + "}");
            ctx.waitTicks(10);
            ctx.waitFor(client -> client.level.getEntitiesOfClass(LivingEntity.class, new AABB(-4, 148, -4, 4, 154, 4), e -> People.of(e) != null).size() == 1, 200);
            // open as a right-click would (a Ghastkin only while you sneak)
            String opened = server.computeOnServer(srv -> {
                ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
                LivingEntity resident = speakerNear(srv);
                player.setShiftKeyDown(sp.sneak);
                Talk.open(player, resident);
                player.setShiftKeyDown(false);
                Folk.Profile p = Folk.of(resident);
                return Folk.fullName(p, People.of(resident)) + " (" + p.job() + ")";
            });
            ctx.waitForScreen(TalkScreen.class);
            ctx.waitTicks(10);
            ctx.getInput().pressKey(KEY_SPACE);
            ctx.waitTicks(5);
            TalkPayload greeting = ctx.computeOnClient(client -> ((TalkScreen) client.gui.screen()).payload());
            check(!greeting.refused(), sp.label + " refused to talk");
            check(greeting.choices().size() >= 3, sp.label + " offers only " + greeting.choices().size() + " topics");
            check(!greeting.name().isBlank() && !greeting.line().isBlank(), sp.label + " has no name or no greeting");
            shoot(ctx, "nether-talk-" + sp.label + "-1-greeting");
            // ask the first topic
            ctx.getInput().pressKey(KEY_1);
            ctx.waitFor(client -> client.gui.screen() instanceof TalkScreen t && !t.payload().line().equals(greeting.line()), 100);
            ctx.waitTicks(5);
            ctx.getInput().pressKey(KEY_SPACE);
            ctx.waitTicks(5);
            TalkPayload topic = ctx.computeOnClient(client -> ((TalkScreen) client.gui.screen()).payload());
            check(topic.points() > greeting.points(), sp.label + ": a new topic gave no friendship");
            shoot(ctx, "nether-talk-" + sp.label + "-2-topic");
            report.add(sp.label + ": " + opened + " says '" + greeting.line() + "' / asked '" + greeting.choices().getFirst().label() + "': '"
                    + topic.line() + "'");
            goodbye(ctx);
            ctx.waitFor(client -> client.gui.screen() == null, 40);
        }
        // a gift and a level-up, with a Magmari: a lava bucket is loved, and the bond is close to level 4
        server.runCommand("kill @e[type=!player]");
        server.runCommand("summon netherfriends:magmari 0 150 0.5 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f],Gender:0}");
        server.runCommand("item replace entity @a weapon.mainhand with lava_bucket");
        ctx.waitTicks(10);
        ctx.waitFor(client -> client.level.getEntitiesOfClass(LivingEntity.class, new AABB(-4, 148, -4, 4, 154, 4), e -> People.of(e) != null).size() == 1, 200);
        server.runOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            LivingEntity resident = speakerNear(srv);
            Bonds.put(resident, player.getUUID(), new Bonds.Bond(21, 3, srv.overworld().getGameTime() / 24000L, -1, List.of()));
            Talk.open(player, resident);
        });
        ctx.waitForScreen(TalkScreen.class);
        ctx.waitTicks(5);
        TalkPayload before = ctx.computeOnClient(client -> client.gui.screen() instanceof TalkScreen t ? t.payload() : null);
        check(before != null, "the Magmari's window closed by itself");
        ctx.runOnClient(client -> {
            TalkPayload p = before;
            ClientPlayNetworking.send(new TalkPayload.Action(p.entityId(), p.residentId(), "gift"));
        });
        ctx.waitFor(client -> client.gui.screen() instanceof TalkScreen t && t.payload().levelUp(), 100);
        ctx.waitTicks(12);
        TalkPayload gifted = ctx.computeOnClient(client -> ((TalkScreen) client.gui.screen()).payload());
        check(gifted.level() == 4, "a loved gift should reach level 4, got " + gifted.level() + " (" + gifted.points() + " points)");
        shoot(ctx, "nether-talk-gift-levelup");
        report.add("gift: '" + gifted.line() + "' / " + gifted.status());
        goodbye(ctx);
        ctx.waitFor(client -> client.gui.screen() == null, 40);
        friendRule(ctx, server, report);
        server.runCommand("kill @e[type=!player]");
        server.runCommand("gamemode creative @a");
        server.runCommand("clear @a");
        System.out.println("[netherfriends] talk:" + System.lineSeparator() + String.join(System.lineSeparator(), report));
    }

    /**
     * Gold off: a Piglin whose Ingot-friend you are leaves you be (and stays out of it when another Piglin calls for
     * help against you); a stranger attacks. Hit the friend itself and it fights back.
     */
    void friendRule(ClientGameTestContext ctx, TestServerContext server, List<String> report) {
        for (String cmd : List.of("kill @e[type=!player]", "clear @a", "effect give @a resistance 60 4 true", "tp @a 0 150 -3 0 0",
                "summon piglin -2 150 1 {PersistenceRequired:1b,IsImmuneToZombification:1b,Tags:[\"friend\"]}",
                "summon piglin 2 150 1 {PersistenceRequired:1b,IsImmuneToZombification:1b,Tags:[\"stranger\"]}")) {
            server.runCommand(cmd);
        }
        server.runOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            Bonds.put(tagged(srv, "friend"), player.getUUID(),
                    new Bonds.Bond(Bonds.THRESHOLDS[GoldRule.TRUST_LEVEL], GoldRule.TRUST_LEVEL, -1, -1, List.of()));
        });
        ctx.waitTicks(80);
        String noGold = server.computeOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            check(GoldRule.showsGold(tagged(srv, "friend"), player), "an Ingot-friend Piglin should talk without gold");
            check(!GoldRule.showsGold(tagged(srv, "stranger"), player), "a stranger Piglin should want gold");
            String targets = "friend -> " + targetName(tagged(srv, "friend")) + ", stranger -> " + targetName(tagged(srv, "stranger"));
            check(target(tagged(srv, "friend")) != player, "the friend Piglin attacked the gold-less player: " + targets);
            check(target(tagged(srv, "stranger")) == player, "the stranger Piglin ignored the gold-less player: " + targets);
            // the player hits the stranger, which calls the others in
            Piglin stranger = tagged(srv, "stranger");
            stranger.hurtServer(srv.overworld(), player.damageSources().playerAttack(player), 1.0F);
            return targets;
        });
        ctx.waitTicks(40);
        String rallied = server.computeOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            Piglin friend = tagged(srv, "friend");
            check(target(friend) != player, "the friend Piglin joined the stranger's fight: friend -> " + targetName(friend));
            friend.hurtServer(srv.overworld(), player.damageSources().playerAttack(player), 1.0F);
            return "friend -> " + targetName(friend);
        });
        ctx.waitTicks(40);
        String hit = server.computeOnServer(srv -> {
            Piglin friend = tagged(srv, "friend");
            check(target(friend) == srv.getPlayerList().getPlayers().getFirst(), "a friend Piglin you hit should fight back");
            return "friend -> " + targetName(friend);
        });
        report.add("friend rule, gold off: " + noGold + " / after hitting the stranger: " + rallied + " / after hitting the friend: " + hit);
    }

    static Piglin tagged(net.minecraft.server.MinecraftServer srv, String tag) {
        return srv.overworld().getEntitiesOfClass(Piglin.class, new AABB(-40, 100, -40, 40, 170, 40), e -> e.entityTags().contains(tag)).getFirst();
    }

    static @org.jspecify.annotations.Nullable LivingEntity target(Piglin piglin) {
        return piglin.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }

    static String targetName(Piglin piglin) {
        LivingEntity target = target(piglin);
        return target == null ? "nobody" : target.getName().getString();
    }

    // -- a story, and the journal ---------------------------------------------------------------------------------

    /** Press something in the open window (as its button would) and wait for the answer. */
    static TalkPayload press(ClientGameTestContext ctx, String action) {
        TalkPayload before = ctx.computeOnClient(client -> ((TalkScreen) client.gui.screen()).payload());
        ctx.runOnClient(client -> ClientPlayNetworking.send(new TalkPayload.Action(before.entityId(), before.residentId(), action)));
        ctx.waitFor(client -> client.gui.screen() instanceof TalkScreen t && t.payload() != before, 100);
        ctx.waitTicks(3);
        return ctx.computeOnClient(client -> ((TalkScreen) client.gui.screen()).payload());
    }

    static boolean offers(TalkPayload p, String id) {
        return p.choices().stream().anyMatch(c -> c.id().equals(id));
    }

    /** Let the line finish typing (as Space does) and photograph the window. */
    static void shootLine(ClientGameTestContext ctx, String name) {
        ctx.getInput().pressKey(KEY_SPACE);
        ctx.waitTicks(4);
        shoot(ctx, name);
    }

    void story(ClientGameTestContext ctx, TestSingleplayerContext world, TestServerContext server) {
        // every people's stories load, and every job has one
        String loaded = server.computeOnServer(srv -> {
            List<String> counts = new ArrayList<>();
            for (People people : People.values()) {
                Story.Book book = Story.book(people);
                check(!book.tales().isEmpty() && !book.child().isEmpty() && !book.early().isEmpty(), people.plural + " have no stories");
                for (String job : Folk.JOBS.get(people)) {
                    check(book.tales().stream().anyMatch(t -> t.jobs().contains(job)), people.plural + ": no story for a " + job);
                }
                // what they ask for is held in one hand, so it has to fit in one stack (and so does the reward)
                for (Story.Tale t : book.tales()) {
                    for (ItemStack st : List.of(t.request().stack(), t.reward())) {
                        check(!st.isEmpty() && st.getCount() <= st.getMaxStackSize(), people.plural + " story " + t.id() + ": " + Story.things(st)
                                + " doesn't fit in one stack (" + st.getMaxStackSize() + ")");
                    }
                }
                counts.add(people.plural + " " + book.tales().size());
            }
            return String.join(", ", counts);
        });
        server.runCommand("tp @a 0 150 -10 0 10");
        world.getConnection().waitForChunksRender();
        ctx.waitTicks(20);
        for (String cmd : List.of("fill -8 149 -8 8 149 8 blackstone", "fill -8 150 -8 8 158 8 air", "fill -8 150 8 8 155 8 nether_bricks",
                "kill @e[type=!player]", "gamemode survival @a", "clear @a", "effect give @a resistance 600 4 true", "tp @a 0 150 -3 0 0",
                "summon netherfriends:magmari 0 150 0.5 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f],Gender:1}")) {
            server.runCommand(cmd);
        }
        ctx.waitTicks(10);
        ctx.waitFor(client -> client.level.getEntitiesOfClass(LivingEntity.class, new AABB(-4, 148, -4, 4, 154, 4), e -> People.of(e) != null).size() == 1, 200);
        // a friend of level 2: the story opens
        String title = server.computeOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            LivingEntity resident = speakerNear(srv);
            Bonds.put(resident, player.getUUID(), new Bonds.Bond(Bonds.THRESHOLDS[Story.OPENS_AT], Story.OPENS_AT, -1, -1, List.of()));
            Talk.open(player, resident);
            Story.Tale tale = Story.tale(resident, Folk.of(resident), Journal.get(resident, player.getUUID()));
            return tale.title() + " (" + Folk.of(resident).job() + ")";
        });
        ctx.waitForScreen(TalkScreen.class);
        ctx.waitTicks(5);
        TalkPayload p = press(ctx, "tab:story");
        check(p.tab().equals("story") && !p.choices().isEmpty(), "the Story tab offers nothing: " + p.line());
        shootLine(ctx, "nether-story-1-intro");
        for (int i = 0; i < 4 && offers(p, "story:next"); i++) p = press(ctx, "story:next");
        check(offers(p, "story:begin"), "no way to begin the story: " + p.choices());
        p = press(ctx, "story:begin");
        check(offers(p, "story:bring"), "the story asked for nothing: " + p.line());
        shootLine(ctx, "nether-story-2-request");
        // bring what they asked for
        server.runOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            LivingEntity resident = speakerNear(srv);
            Story.Tale tale = Story.tale(resident, Folk.of(resident), Journal.get(resident, player.getUUID()));
            player.setItemInHand(InteractionHand.MAIN_HAND, tale.request().stack().copy());
        });
        int before = p.points();
        p = press(ctx, "story:bring");
        check(p.points() == before + Story.BROUGHT_POINTS, "bringing it should give " + Story.BROUGHT_POINTS + " points: " + before + " -> " + p.points());
        shootLine(ctx, "nether-story-3-brought");
        // later: close enough, and another day
        server.runOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            LivingEntity resident = speakerNear(srv);
            long day = srv.overworld().getGameTime() / 24000L;
            Bonds.put(resident, player.getUUID(), new Bonds.Bond(Bonds.THRESHOLDS[Story.CONFIDES_AT], Story.CONFIDES_AT, day, -1, List.of()));
            Journal.put(resident, player.getUUID(), Journal.get(resident, player.getUUID()).brought(day - 1));
        });
        p = press(ctx, "tab:story");
        check(offers(p, "story:next"), "they should be ready to tell the rest: " + p.line() + " / " + p.status());
        for (int i = 0; i < 5 && offers(p, "story:next"); i++) p = press(ctx, "story:next");
        check(offers(p, "story:end:0") && offers(p, "story:end:1"), "no endings to choose: " + p.choices());
        shootLine(ctx, "nether-story-4-confide");
        p = press(ctx, "story:end:0");
        check(p.status().contains("finished"), "the story didn't finish: " + p.status());
        shootLine(ctx, "nether-story-5-ending");
        String reward = server.computeOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            LivingEntity resident = speakerNear(srv);
            Journal.Entry entry = Journal.get(resident, player.getUUID());
            check(entry.chapter() == Journal.FINISHED && entry.ending() == 0, "the Journal doesn't have the story finished");
            Story.Tale tale = Story.tale(resident, Folk.of(resident), entry);
            check(player.getInventory().contains(st -> st.is(tale.reward().getItem())), "no reward in the inventory");
            return Story.things(tale.reward());
        });
        // the Journal
        p = press(ctx, "tab:journal");
        check(p.tab().equals("journal") && p.line().startsWith("Memories with") && p.line().contains("You met"), "the Journal's memories: " + p.line());
        shoot(ctx, "nether-journal-1-memories");
        p = press(ctx, "journal:about");
        check(p.line().contains("Friendship:"), "the About page: " + p.line());
        shoot(ctx, "nether-journal-2-about");
        p = press(ctx, "journal:notes");
        check(p.line().contains("Done: Answered them"), "the story notes: " + p.line());
        shoot(ctx, "nether-journal-3-notes");
        String notes = p.line();
        p = press(ctx, "tab:talk");
        check(p.tab().equals("talk") && !p.choices().isEmpty(), "back on the Talk tab there are no topics");
        goodbye(ctx);
        ctx.waitFor(client -> client.gui.screen() == null, 40);
        // a child has no story yet
        server.runCommand("kill @e[type=!player]");
        server.runCommand("summon netherfriends:withren 0 150 0.5 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f],Age:-24000}");
        ctx.waitTicks(10);
        ctx.waitFor(client -> client.level.getEntitiesOfClass(LivingEntity.class, new AABB(-4, 148, -4, 4, 154, 4), e -> People.of(e) != null).size() == 1, 200);
        server.runOnServer(srv -> Talk.open(srv.getPlayerList().getPlayers().getFirst(), speakerNear(srv)));
        ctx.waitForScreen(TalkScreen.class);
        ctx.waitTicks(5);
        p = press(ctx, "tab:story");
        check(p.status().startsWith("Children"), "a child's Story tab: " + p.status());
        shootLine(ctx, "nether-story-child");
        goodbye(ctx);
        ctx.waitFor(client -> client.gui.screen() == null, 40);
        server.runCommand("kill @e[type=!player]");
        server.runCommand("gamemode creative @a");
        server.runCommand("clear @a");
        System.out.println("[netherfriends] story: " + loaded + System.lineSeparator() + "played: " + title + ", reward " + reward
                + System.lineSeparator() + notes);
    }

    // -- the ferry ---------------------------------------------------------------------------------------------------

    void ferry(ClientGameTestContext ctx, TestSingleplayerContext world, TestServerContext server) {
        BlockPos centre = server.computeOnServer(srv -> locate(srv.getLevel(Level.NETHER), "mooring"));
        check(centre != null, "no Mooring to sail from");
        server.runCommand(String.format("execute in minecraft:the_nether run tp @a %d %d %d 0 20", centre.getX(), centre.getY() + 3, centre.getZ()));
        world.getConnection().waitForChunksRender();
        ctx.waitTicks(40);
        for (String cmd : List.of("gamemode survival @a", "clear @a", "give @a warped_fungus 16", "effect give @a resistance 600 4 true",
                "effect give @a fire_resistance 600 0 true", "effect give @a saturation 600 0 true")) {
            server.runCommand(cmd);
        }
        // the Ferrier, on their dock, and the player beside them
        String ferrierName = server.computeOnServer(srv -> {
            ServerLevel nether = srv.getLevel(Level.NETHER);
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            LivingEntity ferrier = nether.getEntitiesOfClass(LivingEntity.class, new AABB(centre).inflate(64), Ferry::ferries).stream().findFirst().orElse(null);
            check(ferrier != null, "no Ferrier at the Mooring");
            Sea.Dock dock = Ferry.dockOf(ferrier);
            check(dock != null, "the Ferrier's Mooring has no dock");
            BlockPos at = dock.landing();
            ferrier.teleportTo(nether, at.getX() + 0.5, at.getY(), at.getZ() + 0.5, Set.of(), 0.0F, 0.0F, false);
            BlockPos stand = at.relative(dock.seaward().getOpposite());
            float face = dock.seaward().toYRot();
            player.teleportTo(nether, stand.getX() + 0.5, stand.getY(), stand.getZ() + 0.5, Set.of(), face, 15.0F, false);
            Talk.open(player, ferrier);
            return Folk.name(ferrier);
        });
        ctx.waitForScreen(TalkScreen.class);
        ctx.waitTicks(5);
        TalkPayload p = ctx.computeOnClient(client -> ((TalkScreen) client.gui.screen()).payload());
        check(p.canFerry(), "the Ferrier offers no Ferry button");
        press(ctx, "ferry");
        ctx.waitFor(client -> client.gui.screen() instanceof TalkScreen t && offers(t.payload(), "ferry:0"), 1200);
        p = ctx.computeOnClient(client -> ((TalkScreen) client.gui.screen()).payload());
        String offered = p.choices().stream().map(TalkPayload.Choice::label).toList().toString();
        ctx.getInput().pressKey(KEY_SPACE);
        ctx.waitTicks(4);
        shoot(ctx, "nether-ferry-1-offer");
        // book the first crossing: the window closes, the fare is paid, the barge comes to the dock
        TalkPayload booking = p;
        ctx.runOnClient(client -> ClientPlayNetworking.send(new TalkPayload.Action(booking.entityId(), booking.residentId(), "ferry:0")));
        ctx.waitFor(client -> client.gui.screen() == null, 100);
        int fungus = server.computeOnServer(srv -> srv.getPlayerList().getPlayers().getFirst().getInventory().countItem(Items.WARPED_FUNGUS));
        check(fungus < 16, "no fare was taken");
        // filmed (-PnetherFilm): the game's own view from behind the player. The game runs slowed while it's filmed,
        // so each frame (however long it takes to save) covers a tick or two; every frame's game tick is noted, and
        // the film is timed by those (tools/film_ferry.py)
        if (FILM) {
            ctx.getInput().resizeWindow(960, 540);
            server.runOnServer(srv -> srv.tickRateManager().setTickRate(SLOW_TICKS));
        }
        ctx.runOnClient(client -> {
            client.gui.hud.getChat().clearMessages(false);
            if (client.gui.hud.isHidden()) client.gui.hud.toggle();
            client.options.setCameraType(net.minecraft.client.CameraType.THIRD_PERSON_BACK);
        });
        ctx.waitTicks(5);
        List<String> log = new ArrayList<>();
        int frame = 0;
        for (int i = 0; i < 25; i++, frame++) frame(ctx, server, frame, "WAITING", log);
        server.runOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            ServerLevel nether = srv.getLevel(Level.NETHER);
            Barge barge = nether.getEntitiesOfClass(Barge.class, player.getBoundingBox().inflate(16), b -> b.bookedFor(player)).stream().findFirst().orElse(null);
            check(barge != null, "no barge waiting at the dock");
            barge.interact(player, InteractionHand.MAIN_HAND, barge.position());
        });
        List<String> stages = new ArrayList<>();
        String stage = "OUT";
        int ashoreFrames = 0;
        for (int i = 0; i < 3000 && ashoreFrames < 60; i++, frame++) {
            if (stage.equals("OUT") || stage.equals("IN")) {
                // a camera looking well down over the player's head, so the strider shows ahead of the raft
                ctx.runOnClient(client -> client.player.setXRot(38.0F));
            }
            frame(ctx, server, frame, stage, log);
            String now = server.computeOnServer(srv -> Ferry.stage(srv.getPlayerList().getPlayers().getFirst().getUUID()));
            stage = now == null ? "DONE" : now;
            if (stages.isEmpty() || !stages.getLast().equals(stage)) stages.add(stage);
            if (stage.equals("ASHORE") || stage.equals("DONE")) ashoreFrames++;
            if (stage.equals("DONE") && !stages.contains("IN")) break;      // it ended before landing: fail now
        }
        if (FILM) {
            server.runOnServer(srv -> srv.tickRateManager().setTickRate(20.0F));
            try {
                java.nio.file.Files.write(java.nio.file.Path.of("ferry-frames", "frames.txt"), log);
            } catch (java.io.IOException e) {
                throw new java.io.UncheckedIOException(e);
            }
        }
        check(stages.contains("CROSSING") && stages.contains("IN"), "the crossing never happened: " + stages);
        check(stage.equals("ASHORE") || stage.equals("DONE"), "the barge never landed: " + stages);
        String landed = server.computeOnServer(srv -> {
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            check(player.getVehicle() == null, "still aboard after landing");
            check(!player.isInLava(), "set down in the lava");
            BlockPos at = player.blockPosition();
            return at.toShortString() + (player.distanceToSqr(centre.getX(), centre.getY(), centre.getZ()) > 200 * 200 ? " (far from where we set out)" : "");
        });
        shoot(ctx, "nether-ferry-2-ashore");
        if (FILM) ctx.getInput().resizeWindow(1600, 900);
        ctx.runOnClient(client -> {
            client.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);
            if (!client.gui.hud.isHidden()) client.gui.hud.toggle();
        });
        server.runCommand("gamemode creative @a");
        server.runCommand("clear @a");
        System.out.println("[netherfriends] ferry: " + ferrierName + " offered " + offered + "; fare paid, " + fungus + " fungus left; stages "
                + stages + "; landed at " + landed + "; " + frame + " frames");
    }

    /** -PnetherFilm: film the ferry crossing. */
    static final boolean FILM = Boolean.getBoolean("netherfriends.film");
    /** The game's speed while the ferry is filmed (normal is 20), so frames come a tick or two apart. */
    static final float SLOW_TICKS = 2.5F;

    /** One tick of the crossing; when filming, one frame of it (ferry-frames/frame-NNNN.png) with its stage and game tick. */
    static void frame(ClientGameTestContext ctx, TestServerContext server, int frame, String stage, List<String> log) {
        ctx.waitTicks(1);
        if (!FILM) return;
        ctx.takeScreenshot(TestScreenshotOptions.of(String.format("frame-%04d", frame)).disableCounterPrefix()
                .withDestinationDir(java.nio.file.Path.of("ferry-frames")));
        long tick = server.computeOnServer(srv -> srv.overworld().getGameTime());
        log.add(frame + " " + stage + " " + tick);
    }

    // -- the creatures ----------------------------------------------------------------------------------------------

    void creatures(ClientGameTestContext ctx, TestSingleplayerContext world, TestServerContext server) {
        server.runCommand("tp @a 0 150 -10 0 10");
        world.getConnection().waitForChunksRender();
        ctx.waitTicks(20);
        for (String cmd : List.of("fill -10 149 -8 10 149 12 blackstone", "fill -10 150 -8 10 160 12 air", "fill -10 150 12 10 156 12 nether_bricks",
                "kill @e[type=!player]", "gamemode survival @a", "clear @a")) {
            server.runCommand(cmd);
        }
        // a lineup in one row: grown and young of each, the Ashbell hanging in the air
        server.runCommand("summon netherfriends:cairnback -5 150 3 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f]}");
        server.runCommand("summon netherfriends:cairnback -2.5 150 3 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f],Age:-24000}");
        server.runCommand("summon netherfriends:silkgrub 0.5 150 3 {NoAI:1b,PersistenceRequired:1b,Rotation:[150f,0f]}");
        server.runCommand("summon netherfriends:silkgrub 2.5 150 3 {NoAI:1b,PersistenceRequired:1b,Rotation:[150f,0f],Age:-24000}");
        server.runCommand("summon netherfriends:ashbell 5 151.5 3 {NoAI:1b,PersistenceRequired:1b,Rotation:[180f,0f]}");
        world.getConnection().waitForClientboundEntityUpdates(NetherEntities.ASHBELL);
        ctx.waitTicks(40);
        String sizes = ctx.computeOnClient(client -> {
            List<String> out = new ArrayList<>();
            for (LivingEntity e : client.level.getEntitiesOfClass(LivingEntity.class, new AABB(-10, 148, -8, 10, 160, 12), e -> !(e instanceof net.minecraft.world.entity.player.Player))) {
                out.add(e.getType().getDescription().getString() + (e.isBaby() ? " young" : "") + " scale " + e.getScale() + " at x " + e.getX());
            }
            return String.join("; ", out);
        });
        System.out.println("[netherfriends] creature sizes: " + sizes);
        server.runCommand("tp @a 0 152.5 -5 0 15");
        ctx.waitTicks(20);
        shoot(ctx, "nether-creatures-1-lineup");
        server.runCommand("tp @a 1.5 151 -0.5 0 20");
        ctx.waitTicks(15);
        shoot(ctx, "nether-creatures-2-close");
        List<String> report = new ArrayList<>();
        String result = server.computeOnServer(srv -> {
            var level = srv.overworld();
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            AABB pen = new AABB(-10, 148, -8, 10, 160, 12);
            List<String> out = new ArrayList<>();
            // a torch held to a grown Cairnback comes away a soul torch
            Cairnback cairnback = level.getEntitiesOfClass(Cairnback.class, pen, c -> !c.isBaby()).getFirst();
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TORCH, 3));
            player.interactOn(cairnback, InteractionHand.MAIN_HAND, cairnback.position());
            int soul = player.getInventory().countItem(Items.SOUL_TORCH);
            check(soul == 1 && player.getMainHandItem().getCount() == 2, "a Cairnback relit " + soul + " torches");
            out.add("cairnback: 1 torch relit as a soul torch");
            // shears take a grown Silkgrub's silk as string; warped roots grow it back
            Silkgrub grub = level.getEntitiesOfClass(Silkgrub.class, pen, g -> !g.isBaby()).getFirst();
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.SHEARS));
            player.interactOn(grub, InteractionHand.MAIN_HAND, grub.position());
            int string = level.getEntitiesOfClass(ItemEntity.class, pen, i -> i.getItem().is(Items.STRING)).stream().mapToInt(i -> i.getItem().getCount()).sum();
            check(string >= 2 && grub.silk() < 0.01F, "shearing gave " + string + " string, silk " + grub.silk());
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.WARPED_ROOTS));
            player.interactOn(grub, InteractionHand.MAIN_HAND, grub.position());
            check(grub.silk() > 0.99F, "warped roots didn't grow the silk back");
            out.add("silkgrub: sheared for " + string + " string, regrown with warped roots");
            // a ghast won't take aim at anyone near an Ashbell, and will again once it's gone
            Ghast ghast = EntityTypes.GHAST.create(level, net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            ghast.snapTo(0, 158, 8, 0, 0);
            ghast.setNoAi(true);
            level.addFreshEntity(ghast);
            ghast.setTarget(player);
            check(ghast.getTarget() == null, "a ghast took aim beside an Ashbell");
            level.getEntitiesOfClass(Ashbell.class, pen, a -> true).forEach(a -> a.discard());
            ghast.setTarget(player);
            check(ghast.getTarget() == player, "a ghast wouldn't take aim even with no Ashbell near");
            ghast.discard();
            out.add("ashbell: a ghast held its fire while one was near");
            return String.join(System.lineSeparator(), out);
        });
        report.add(result);
        ctx.waitTicks(10);
        shoot(ctx, "nether-creatures-3-sheared");
        server.runCommand("kill @e[type=!player]");
        server.runCommand("gamemode creative @a");
        server.runCommand("clear @a");
        System.out.println("[netherfriends] creatures:" + System.lineSeparator() + String.join(System.lineSeparator(), report));
    }

    // -- the villages ----------------------------------------------------------------------------------------------

    void villages(ClientGameTestContext ctx, TestSingleplayerContext world, TestServerContext server) {
        server.runCommand("gamemode spectator @a");
        List<String> report = new ArrayList<>();
        String only = System.getProperty("netherfriends.villages", "");
        for (String village : only.isBlank() ? VILLAGES : only.split(",")) {
            BlockPos centre = server.computeOnServer(srv -> locate(srv.getLevel(Level.NETHER), village));
            check(centre != null, "no " + village + " generated within reach of the Nether's origin");
            report.add(village + " at " + centre.toShortString());
            // visit: load the village from its centre, then photograph it from three sides with a clear view
            server.runCommand(String.format("execute in minecraft:the_nether run tp @a %d %d %d 0 20", centre.getX(), centre.getY() + 3, centre.getZ()));
            world.getConnection().waitForChunksRender();
            ctx.waitTicks(40);
            List<double[]> views = server.computeOnServer(srv -> clearViews(srv.getLevel(Level.NETHER), centre));
            for (int v = 0; v < views.size(); v++) {
                double[] cam = views.get(v);
                double dx = centre.getX() + 0.5 - cam[0], dy = centre.getY() + 3 - cam[1], dz = centre.getZ() + 0.5 - cam[2];
                float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
                float pitch = (float) Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx * dx + dz * dz)));
                server.runCommand(String.format("execute in minecraft:the_nether run tp @a %.1f %.1f %.1f %.1f %.1f", cam[0], cam[1], cam[2], yaw, pitch));
                world.getConnection().waitForChunksRender();
                ctx.waitTicks(20);
                shoot(ctx, "nether-village-" + village + "-" + (v + 1));
            }
            People people = switch (village) {
                case "hold" -> People.PIGLIN;
                case "vigil" -> People.WITHREN;
                case "mooring" -> People.MAGMARI;
                case "loom" -> People.WEFT;
                default -> People.GHASTKIN;
            };
            int residents = server.computeOnServer(srv -> {
                ServerLevel nether = srv.getLevel(Level.NETHER);
                List<Entity> all = nether.getEntities((Entity) null, new AABB(centre).inflate(56, 40, 56), e -> People.of(e) == people);
                return all.size();
            });
            report.add("  " + residents + " " + people.plural);
            check(residents >= 4, village + " has only " + residents + " " + people.plural);
            // every resident the village spawned has a name, a job and a home
            List<String> folk = server.computeOnServer(srv -> {
                ServerLevel nether = srv.getLevel(Level.NETHER);
                List<String> out = new ArrayList<>();
                for (Entity e : nether.getEntities((Entity) null, new AABB(centre).inflate(56, 40, 56), e -> People.of(e) == people)) {
                    Folk.Profile p = e.getAttached(Folk.PROFILE);
                    if (p == null) {
                        out.add("!" + e);
                        continue;
                    }
                    out.add(Folk.fullName(p, people) + ", " + Folk.epithet(p, people, ((LivingEntity) e).isBaby()) + " (" + p.job() + ")"
                            + (p.village().map(centre::equals).orElse(false) ? "" : " !village"));
                }
                return out;
            });
            for (String f : folk) check(!f.startsWith("!") && !f.endsWith("!village"), village + ": resident without a proper profile: " + f);
            // the creature its people keep
            var beast = switch (village) {
                case "vigil" -> NetherEntities.CAIRNBACK;
                case "loom" -> NetherEntities.SILKGRUB;
                case "eave" -> NetherEntities.ASHBELL;
                default -> null;
            };
            if (beast != null) {
                int beasts = server.computeOnServer(srv -> srv.getLevel(Level.NETHER).getEntities(beast, new AABB(centre).inflate(56, 40, 56), e -> true).size());
                report.add("  " + beasts + " " + beast.getDescription().getString());
                check(beasts >= 1, village + " keeps no " + beast.getDescription().getString());
            }
            report.add("  " + dev.netherfriends.world.Names.village(people, centre) + ": " + String.join("; ", folk.subList(0, Math.min(6, folk.size()))));
        }
        System.out.println("[netherfriends] villages:\n" + String.join("\n", report));
    }

    /**
     * Up to three camera spots round the centre, from different sides, 16-32 blocks out and 6-18 up, each standing in
     * air with an unbroken line of sight to the centre (so the photos aren't of the inside of a hill).
     */
    static List<double[]> clearViews(ServerLevel level, BlockPos centre) {
        List<double[]> views = new ArrayList<>();
        Vec3 target = Vec3.atCenterOf(centre.above(3));
        for (int sector = 0; sector < 3; sector++) {
            search:
            for (int step = 0; step < 8; step++) {
                double angle = Math.toRadians(sector * 120 + 35 + (step % 2 == 0 ? step : -step) * 12);
                for (int dist : new int[]{30, 24, 18}) {
                    for (int h : new int[]{14, 18, 10, 6}) {
                        Vec3 cam = new Vec3(centre.getX() + 0.5 + Math.cos(angle) * dist, centre.getY() + h, centre.getZ() + 0.5 + Math.sin(angle) * dist);
                        if (!level.getBlockState(BlockPos.containing(cam)).isAir()) continue;
                        HitResult hit = level.clip(new ClipContext(cam, target, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
                        if (hit.getType() == HitResult.Type.MISS || hit.getLocation().distanceTo(target) < 4) {
                            views.add(new double[]{cam.x, cam.y, cam.z});
                            break search;
                        }
                    }
                }
            }
        }
        if (views.isEmpty()) views.add(new double[]{centre.getX() + 0.5, centre.getY() + 3, centre.getZ() - 6.5});
        return views;
    }

    /** Find the nearest village of this kind as /locate does, and return its centre (from its pieces). */
    static BlockPos locate(ServerLevel level, String village) {
        ResourceKey<Structure> key = ResourceKey.create(Registries.STRUCTURE, NetherFriends.id(village));
        Holder<Structure> holder = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getOrThrow(key);
        BlockPos found = level.findNearestMapStructure(HolderSet.direct(holder), BlockPos.ZERO, 150, false);
        if (found == null) return null;
        ChunkAccess chunk = level.getChunk(found.getX() >> 4, found.getZ() >> 4, ChunkStatus.STRUCTURE_STARTS);
        StructureStart start = chunk.getStartForStructure(holder.value());
        if (start == null || !start.isValid()) return null;
        for (StructurePiece piece : start.getPieces()) {
            if (piece instanceof BuildingPiece building) return building.village();
        }
        return null;
    }

    // -- every loot table, rolled ---------------------------------------------------------------------------------

    static final int ROLLS = 300;

    /** Per item: in how many rolls it came, its biggest single stack and how many in all. */
    static Map<Item, int[]> roll(LootTable table, LootParams params) {
        Map<Item, int[]> out = new LinkedHashMap<>();
        for (int i = 0; i < ROLLS; i++) {
            Map<Item, Integer> got = new HashMap<>();
            for (ItemStack stack : table.getRandomItems(params)) {
                if (stack.isEmpty()) continue;
                var potion = stack.get(DataComponents.POTION_CONTENTS);
                check(!stack.is(Items.POTION) || potion != null && potion.potion().isPresent(), "a chest potion is a real potion, not " + stack);
                got.merge(stack.getItem(), stack.getCount(), Integer::sum);
                int[] t = out.computeIfAbsent(stack.getItem(), k -> new int[3]);
                t[1] = Math.max(t[1], stack.getCount());
            }
            got.forEach((item, n) -> { int[] t = out.get(item); t[0]++; t[2] += n; });
        }
        return out;
    }

    void loot(TestServerContext server) {
        server.runOnServer(srv -> {
            ServerLevel level = srv.overworld();
            ServerPlayer player = srv.getPlayerList().getPlayers().getFirst();
            Vec3 origin = player.position();
            var tables = srv.reloadableRegistries().lookup().lookupOrThrow(Registries.LOOT_TABLE).listElementIds()
                    .filter(k -> k.identifier().getNamespace().equals(NetherFriends.MOD_ID)).sorted((a, b) -> a.identifier().compareTo(b.identifier())).toList();
            check(tables.size() == 15, "15 Nether Friends loot tables load, found " + tables.size());
            for (var key : tables) {
                String path = key.identifier().getPath();
                boolean chest = path.startsWith("chests/");
                var params = new LootParams.Builder(level).withParameter(LootContextParams.ORIGIN, origin);
                if (!chest) {
                    Entity mob = BuiltInRegistries.ENTITY_TYPE.getOptional(key.identifier().withPath(path.substring("entities/".length())))
                            .orElseThrow().create(level, EntitySpawnReason.COMMAND);
                    mob.setPos(origin);
                    params.withParameter(LootContextParams.THIS_ENTITY, mob).withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().playerAttack(player))
                            .withParameter(LootContextParams.ATTACKING_ENTITY, player).withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player);
                }
                var got = roll(srv.reloadableRegistries().getLootTable(key), params.create(chest ? LootContextParamSets.CHEST : LootContextParamSets.ENTITY));
                StringBuilder shown = new StringBuilder();
                got.forEach((item, t) -> shown.append(BuiltInRegistries.ITEM.getKey(item).getPath()).append(" in ").append(t[0]).append(" up to ").append(t[1]).append(", "));
                System.out.println("[netherfriends test] " + path + ": " + shown);
                // Before 26.3's keys every stack was a single item, and every creature drop came every time.
                if (chest) check(got.values().stream().anyMatch(t -> t[1] >= 2), path + " stack sizes follow set_count");
                else check(got.values().stream().anyMatch(t -> t[1] >= 2 || t[0] < ROLLS), path + " drop counts follow set_count");
                if (path.equals("entities/cairnback")) {
                    int[] meal = got.get(Items.BONE_MEAL);
                    check(meal != null && meal[0] > ROLLS * 3 / 10 && meal[0] < ROLLS * 7 / 10, "the Cairnback's bone meal comes about half the time");
                }
            }
        });
    }
}
