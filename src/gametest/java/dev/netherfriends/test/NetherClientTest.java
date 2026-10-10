package dev.netherfriends.test;

import java.util.ArrayList;
import java.util.List;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.client.TalkScreen;
import dev.netherfriends.entity.Ashbell;
import dev.netherfriends.entity.Cairnback;
import dev.netherfriends.entity.NetherEntities;
import dev.netherfriends.entity.Silkgrub;
import dev.netherfriends.entity.People;
import dev.netherfriends.piglin.GoldRule;
import dev.netherfriends.talk.Bonds;
import dev.netherfriends.talk.Folk;
import dev.netherfriends.talk.Journal;
import dev.netherfriends.talk.Story;
import dev.netherfriends.talk.Talk;
import dev.netherfriends.talk.TalkPayload;
import dev.netherfriends.world.BuildingPiece;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestServerContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
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
 * "Creatures": the Cairnback, Silkgrub and Ashbell,
 * photographed, then each one's use tried for real (relighting, shearing, calming a ghast). Screenshots land in build/run/clientGameTest/screenshots.
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
            if (wanted("Creatures")) creatures(ctx, world, server);
            if (wanted("Villages")) villages(ctx, world, server);
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
        return srv.overworld().getEntitiesOfClass(Piglin.class, new AABB(-8, 145, -8, 8, 160, 8), e -> e.entityTags().contains(tag)).getFirst();
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
}
