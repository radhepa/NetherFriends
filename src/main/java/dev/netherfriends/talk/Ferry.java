package dev.netherfriends.talk;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.entity.Barge;
import dev.netherfriends.entity.Magmari;
import dev.netherfriends.entity.NetherEntities;
import dev.netherfriends.entity.People;
import dev.netherfriends.talk.Bonds.Bond;
import dev.netherfriends.talk.Dialogue.Ctx;
import dev.netherfriends.talk.Dialogue.Line;
import dev.netherfriends.talk.Talk.Session;
import dev.netherfriends.world.BuildingPiece;
import dev.netherfriends.world.Names;
import dev.netherfriends.world.Sea;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * The Magmari ferry. Ask a Mooring's Ferrier (the Ferry button) and they offer the other Moorings within reach,
 * for a fare in warped fungus (striders eat the fare); a close friend rides free. Book a crossing and a strider
 * barge ties up at the ferry dock: climb aboard (right-click it) and the Ferrier wades out beside it into open
 * lava, then lets the strider go on alone. The crossing itself passes as a dark screen with a few lines of the
 * sea; the barge comes in to the far Mooring's dock and sets you down on its lander, then turns for home.
 * The far shore's chunks are loaded while you cast off, so you only ever arrive somewhere ready.
 */
public final class Ferry {
    private Ferry() {}

    public static final String JOB = "Ferrier";
    static final int SEARCH_BLOCKS = 1600, MAX_DESTINATIONS = 4, CANDIDATES = 12;
    static final int OUT_REACH = 44, IN_REACH = 36;
    /** The far dock's chunks kept loading (ticket radius), and how far round its berth must be loaded and lit to arrive. */
    static final int FAR_TICKET = 4, FAR_READY = 3;
    /** Friendship level at which a Ferrier takes you for nothing. */
    public static final int FREE_AT = 4;
    static final int WAIT_TICKS = 20 * 120, BLACKOUT = 25, MIN_DARK = 110, SETTLE = 60, GIVE_UP_LOADING = 20 * 20;
    static final String TAB = "ferry";

    public record Destination(String name, BlockPos village, Sea.Dock dock, int distance, String direction) {}

    /** Where a Ferrier's barges leave from: their Mooring's centre and its dock. */
    record Home(BlockPos village, Sea.Dock dock) {}

    enum Stage { WAITING, OUT, CROSSING, IN, ASHORE }

    /** One player's crossing, from booking to the barge sailing off home. */
    static final class Voyage {
        final UUID player, ferrier;
        final ServerLevel level;
        final Home from;
        final Destination to;
        final int fare;
        final List<Vec3> out;
        final Map<String, String> said;
        Barge barge;
        Stage stage = Stage.WAITING;
        int ticks;
        boolean wading = true, moved;
        int movedAt;
        List<Vec3> in = List.of();

        Voyage(ServerPlayer player, LivingEntity ferrier, Home from, Destination to, int fare, List<Vec3> out, Map<String, String> said, Barge barge) {
            this.player = player.getUUID();
            this.ferrier = ferrier.getUUID();
            this.level = (ServerLevel) ferrier.level();
            this.from = from;
            this.to = to;
            this.fare = fare;
            this.out = out;
            this.said = said;
            this.barge = barge;
        }
    }

    private static final Map<UUID, Voyage> VOYAGES = new HashMap<>();
    private static final Map<BlockPos, List<Destination>> ROUTES = new HashMap<>();
    private static final Set<BlockPos> SEARCHING = new HashSet<>();
    private static @Nullable Lines lines;

    public static void init() {
        PayloadTypeRegistry.clientboundPlay().register(FerryPayload.TYPE, FerryPayload.CODEC);
        ServerTickEvents.END_SERVER_TICK.register(Ferry::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> abandon(handler.player));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (UUID id : List.copyOf(VOYAGES.keySet())) {
                ServerPlayer player = server.getPlayerList().getPlayer(id);
                if (player != null) abandon(player);
            }
            VOYAGES.clear();
            ROUTES.clear();
            SEARCHING.clear();
        });
        Barge.onBoard = Ferry::board;
    }

    /** A grown Magmari Ferrier: the only resident with a Ferry button. */
    public static boolean ferries(LivingEntity resident) {
        return resident instanceof Magmari && !resident.isBaby() && Folk.of(resident).job().equals(JOB);
    }

    /** For tests: the dock a Ferrier's barges leave from, or null. */
    public static Sea.@Nullable Dock dockOf(LivingEntity ferrier) {
        Home home = home((ServerLevel) ferrier.level(), ferrier);
        return home == null ? null : home.dock();
    }

    /** For tests: where this player's crossing ends, or null. */
    public static @Nullable BlockPos landingFor(UUID player) {
        Voyage v = VOYAGES.get(player);
        return v == null ? null : v.to.dock().landing();
    }

    /** For tests: the crossing this player is on, as "stage", or null. */
    public static @Nullable String stage(UUID player) {
        Voyage v = VOYAGES.get(player);
        return v == null ? null : v.stage.name();
    }

    // -- in the conversation window ------------------------------------------------------------------------------

    /** The Ferry button: where they can take you, and what it costs. */
    static void offer(ServerPlayer player, LivingEntity ferrier, Session s, Bond bond, Ctx ctx) {
        s.tab = TAB;
        ServerLevel level = (ServerLevel) ferrier.level();
        Lines l = lines();
        if (VOYAGES.containsKey(player.getUUID())) {
            Talk.send(player, ferrier, s, say(l.busy, ctx, s, ferrier), "", List.of(back()), false, false);
            return;
        }
        Home home = home(level, ferrier);
        if (home == null) {
            Talk.send(player, ferrier, s, say(l.wild, ctx, s, ferrier), "", List.of(back()), false, false);
            return;
        }
        List<Destination> routes = ROUTES.get(home.village());
        if (routes != null) {
            show(player, ferrier, s, bond, ctx, routes);
            return;
        }
        Talk.send(player, ferrier, s, say(l.searching, ctx, s, ferrier), "Working out where the barges can go...", List.of(), false, false);
        if (!SEARCHING.add(home.village())) return;
        search(level, home).whenCompleteAsync((found, error) -> {
            SEARCHING.remove(home.village());
            List<Destination> result = found == null ? List.of() : found;
            if (error != null) NetherFriends.LOGGER.warn("Couldn't chart the ferry routes from {}", home.village(), error);
            ROUTES.put(home.village(), result);
            if (Talk.session(player.getUUID()) == s && s.tab.equals(TAB) && !player.isRemoved() && ferrier.isAlive()) {
                Bond now = Bonds.get(ferrier, player.getUUID());
                show(player, ferrier, s, now, Talk.ctx(ferrier, Folk.of(ferrier), player, now, null), result);
            }
        }, level.getServer());
    }

    private static void show(ServerPlayer player, LivingEntity ferrier, Session s, Bond bond, Ctx ctx, List<Destination> routes) {
        Lines l = lines();
        if (routes.isEmpty()) {
            Talk.send(player, ferrier, s, say(l.none, ctx, s, ferrier), "", List.of(back()), false, false);
            return;
        }
        boolean free = bond.level() >= FREE_AT;
        String strider = strider(ferrier);
        String text = extra(say(free ? l.free : l.offer, ctx, s, ferrier), strider, "");
        List<TalkPayload.Choice> choices = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            Destination d = routes.get(i);
            int fare = fare(d, bond);
            choices.add(new TalkPayload.Choice("ferry:" + i, capital(d.name()) + ", " + d.distance() + " " + abbreviate(d.direction())
                    + (fare > 0 ? ": " + fare + " fungus" : ""), false));
        }
        choices.add(back());
        String status = free ? "You ride free: you're their " + Dialogue.bank(People.MAGMARI).levels().get(bond.level()) + "."
                : "Fares in warped fungus. You have " + count(player) + ".";
        Talk.send(player, ferrier, s, text, status, choices, false, false);
    }

    /** A choice from the Ferry list: pay and book the crossing; the barge comes to the dock. */
    static void book(ServerPlayer player, LivingEntity ferrier, Session s, Bond bond, Ctx ctx, int index) {
        ServerLevel level = (ServerLevel) ferrier.level();
        Lines l = lines();
        Home home = home(level, ferrier);
        List<Destination> routes = home == null ? null : ROUTES.get(home.village());
        if (home == null || routes == null || index < 0 || index >= routes.size() || VOYAGES.containsKey(player.getUUID())) return;
        Destination to = routes.get(index);
        int fare = fare(to, bond), have = count(player);
        if (have < fare) {
            Talk.send(player, ferrier, s, say(l.poor, ctx, s, ferrier), "The fare is " + fare + " warped fungus. You have " + have + ".",
                    List.of(back()), false, false);
            return;
        }
        BlockPos berth = home.dock().berth();
        List<Vec3> out = Sea.wayOut(level, berth, to.dock().berth().getX() - berth.getX(), to.dock().berth().getZ() - berth.getZ(), OUT_REACH);
        if (out.size() < 2) {
            Talk.send(player, ferrier, s, say(l.blocked, ctx, s, ferrier), "", List.of(back()), false, false);
            return;
        }
        Barge barge = NetherEntities.BARGE.create(level, EntitySpawnReason.EVENT);
        if (barge == null) return;
        take(player, fare);
        barge.snapTo(out.get(0).x, out.get(0).y, out.get(0).z, heading(out.get(0), out.get(1)), 0.0F);
        barge.bookFor(player.getUUID());
        level.addFreshEntity(barge);
        // what's said on the way, filled in now while the Ferrier is to hand
        String strider = strider(ferrier);
        Map<String, String> said = new HashMap<>();
        for (Map.Entry<String, List<Line>> pool : Map.of("booked", l.booked, "castoff", l.castoff, "hopoff", l.hopoff, "arrive", l.arrive,
                "late", l.late, "stay", l.stay).entrySet()) {
            said.put(pool.getKey(), extra(say(pool.getValue(), ctx, s, ferrier), strider, to.name()));
        }
        // the crossing: two lines of open sea, then land in sight
        List<String> crossing = new ArrayList<>();
        for (int i = 0; i < 2; i++) crossing.add(extra(say(l.crossing, ctx, s, ferrier), strider, to.name()));
        crossing.add(extra(say(l.landfall, ctx, s, ferrier), strider, to.name()));
        said.put("crossing", String.join("\n", crossing));
        Voyage v = new Voyage(player, ferrier, home, to, fare, out, said, barge);
        VOYAGES.put(player.getUUID(), v);
        ticket(v);
        if (ferrier instanceof Magmari m) m.getNavigation().moveTo(home.dock().ferrier().getX() + 0.5, home.dock().ferrier().getY(), home.dock().ferrier().getZ() + 0.5, 1.0);
        Talk.end(player);
        ServerPlayNetworking.send(player, new FerryPayload(FerryPayload.BOOKED, List.of()));
        player.sendOverlayMessage(Component.literal(Folk.name(ferrier) + ": " + said.get("booked")));
    }

    /** The yaw that faces from a toward b. */
    static float heading(Vec3 a, Vec3 b) {
        return (float) (Mth.atan2(b.z - a.z, b.x - a.x) * Mth.RAD_TO_DEG) - 90.0F;
    }

    private static TalkPayload.Choice back() {
        return new TalkPayload.Choice("tab:talk", "Not today.", false);
    }

    private static String say(List<Line> pool, Ctx ctx, Session s, LivingEntity ferrier) {
        return Talk.line(pool, ctx, s, ferrier);
    }

    /** Fill in the ferry's own placeholders, {strider} and {destination} (capitalised where a sentence starts). */
    private static String extra(String text, String strider, String destination) {
        return Dialogue.fill(text, Map.of("strider", strider, "destination", destination));
    }

    private static String capital(String text) {
        return text.isEmpty() ? text : Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    // -- the crossing --------------------------------------------------------------------------------------------

    /** Right-clicking the barge: the player who booked it climbs aboard and it casts off. */
    private static void board(Barge barge, ServerPlayer player) {
        Voyage v = VOYAGES.get(player.getUUID());
        if (v == null || v.barge != barge || v.stage != Stage.WAITING) {
            if (!barge.bookedFor(player)) player.sendOverlayMessage(Component.literal("This barge is waiting for someone else."));
            return;
        }
        if (!player.startRiding(barge, true, true)) {
            NetherFriends.LOGGER.warn("{} couldn't board the barge at {}", player.getName().getString(), barge.blockPosition());
            return;
        }
        player.forceSetRotation(barge.getYRot(), false, 25.0F, false);
        player.clearFire();
        // the Ferrier's magma-cream paste: the sea's heat can't touch you while it lasts
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * 120, 0, true, false));
        v.stage = Stage.OUT;
        v.ticks = 0;
        barge.follow(v.out.subList(1, v.out.size()));
        LivingEntity ferrier = ferrier(v);
        player.sendOverlayMessage(Component.literal((ferrier != null ? Folk.name(ferrier) + ": " : "") + v.said.get("castoff")));
    }

    private static void tick(MinecraftServer server) {
        if (VOYAGES.isEmpty() || !server.tickRateManager().runsNormally()) return;
        for (Voyage v : List.copyOf(VOYAGES.values())) {
            ServerPlayer player = server.getPlayerList().getPlayer(v.player);
            if (player == null || !player.isAlive() || player.level() != v.level) {
                end(v, player, true);
                continue;
            }
            v.ticks++;
            if (v.stage.ordinal() < Stage.IN.ordinal() && v.ticks % 200 == 0) ticket(v);
            switch (v.stage) {
                case WAITING -> waiting(v, player);
                case OUT -> out(v, player);
                case CROSSING -> crossing(v, player);
                case IN -> in(v, player);
                case ASHORE -> {
                    if (v.barge.isRemoved() || v.barge.arrived() || v.ticks > 300) end(v, player, false);
                }
            }
        }
    }

    private static void waiting(Voyage v, ServerPlayer player) {
        boolean gone = v.barge.isRemoved() || player.distanceToSqr(v.barge) > 160 * 160;
        if (gone || v.ticks > WAIT_TICKS) {
            NetherFriends.LOGGER.info("A ferry barge left without its passenger ({})", gone ? "barge gone or player far" : "waited too long");
            refund(player, v.fare);
            LivingEntity ferrier = ferrier(v);
            player.sendOverlayMessage(Component.literal((ferrier != null ? Folk.name(ferrier) + ": " : "") + v.said.get("late")));
            end(v, player, false);
            return;
        }
        if (v.ticks % 100 == 60 && player.distanceToSqr(v.barge) < 48 * 48) {
            player.sendOverlayMessage(Component.literal("The barge is waiting at the ferry dock: right-click it to climb aboard."));
        }
    }

    private static void out(Voyage v, ServerPlayer player) {
        if (!aboard(v, player)) return;
        LivingEntity ferrier = ferrier(v);
        if (v.wading && ferrier != null) {
            if (v.barge.progress() >= 0.55F || ferrier.distanceToSqr(v.barge) > 32 * 32) {
                letGo(v, ferrier);
                player.sendOverlayMessage(Component.literal(Folk.name(ferrier) + ": " + v.said.get("hopoff")));
            } else {
                // wading beside the strider, a hand on its harness
                float yaw = v.barge.getYRot() * Mth.DEG_TO_RAD;
                double fx = -Mth.sin(yaw), fz = Mth.cos(yaw);
                double x = v.barge.getX() + fx * 2.2 - fz * 2.1, z = v.barge.getZ() + fz * 2.2 + fx * 2.1;
                if (ferrier instanceof Magmari m) m.getNavigation().stop();
                ferrier.snapTo(x, Sea.surface(v.level) + 1.0, z, v.barge.getYRot(), 0.0F);
                ferrier.setYHeadRot(v.barge.getYRot());
                ferrier.setYBodyRot(v.barge.getYRot());
            }
        }
        if (v.barge.arrived()) {
            if (v.wading && ferrier != null) letGo(v, ferrier);
            v.wading = false;
            v.stage = Stage.CROSSING;
            v.ticks = 0;
            ServerPlayNetworking.send(player, new FerryPayload(FerryPayload.CROSS, List.of(v.said.get("crossing").split("\n"))));
        }
    }

    private static void crossing(Voyage v, ServerPlayer player) {
        if (!v.moved) {
            if (v.ticks < BLACKOUT) {
                aboard(v, player);
                return;
            }
            // the far shore loaded (and lit, so it isn't dark when the screen lifts) all round the berth
            ChunkPos far = ChunkPos.containing(v.to.dock().berth());
            boolean ready = true;
            for (int dx = -FAR_READY; dx <= FAR_READY && ready; dx++) {
                for (int dz = -FAR_READY; dz <= FAR_READY && ready; dz++) {
                    var chunk = v.level.getChunkSource().getChunkNow(far.x() + dx, far.z() + dz);
                    ready = chunk != null && (Math.abs(dx) > 2 || Math.abs(dz) > 2 || chunk.isLightCorrect());
                }
            }
            if (!ready && v.ticks < GIVE_UP_LOADING) {
                aboard(v, player);
                return;
            }
            if (!ready) {
                for (int dx = -FAR_READY; dx <= FAR_READY; dx++) for (int dz = -FAR_READY; dz <= FAR_READY; dz++) v.level.getChunk(far.x() + dx, far.z() + dz);
            }
            arrive(v, player);
            return;
        }
        aboard(v, player);
        // stay dark a few seconds after the move, so the far shore is drawn before the screen lifts
        if (v.ticks >= Math.max(MIN_DARK, v.movedAt + SETTLE)) {
            ServerPlayNetworking.send(player, new FerryPayload(FerryPayload.CLEAR, List.of()));
            v.stage = Stage.IN;
            v.ticks = 0;
            v.barge.follow(v.in.subList(1, v.in.size()));
        }
    }

    /** In the dark: move the player across the sea to a fresh barge off the far dock. */
    private static void arrive(Voyage v, ServerPlayer player) {
        BlockPos berth = v.to.dock().berth(), home = v.from.dock().berth();
        List<Vec3> in = new ArrayList<>(Sea.wayOut(v.level, berth, home.getX() - berth.getX(), home.getZ() - berth.getZ(), IN_REACH));
        if (in.size() < 2) {
            NetherFriends.LOGGER.info("No way in to {} from the sea: berth {} open {}, chunk {}, lava at {}: {}", v.to.name(), berth.toShortString(),
                    Sea.open(v.level, berth.getX(), berth.getZ()), v.level.hasChunk(berth.getX() >> 4, berth.getZ() >> 4), Sea.surface(v.level),
                    v.level.getBlockState(berth.atY(Sea.surface(v.level))));
        }
        if (in.size() < 2) {
            // a sea too tight to chart: come straight in from the open side
            double y = Sea.floatY(v.level);
            BlockPos off = berth.relative(v.to.dock().seaward(), 10);
            in = new ArrayList<>(List.of(new Vec3(berth.getX() + 0.5, y, berth.getZ() + 0.5), new Vec3(off.getX() + 0.5, y, off.getZ() + 0.5)));
        }
        java.util.Collections.reverse(in);
        player.stopRiding();
        v.barge.discard();
        Barge barge = NetherEntities.BARGE.create(v.level, EntitySpawnReason.EVENT);
        if (barge == null) {
            end(v, player, true);
            return;
        }
        Vec3 start = in.getFirst();
        float yaw = heading(start, in.get(1));
        barge.snapTo(start.x, start.y, start.z, yaw, 0.0F);
        barge.bookFor(player.getUUID());
        v.level.addFreshEntity(barge);
        player.teleportTo(v.level, start.x, start.y + 0.5, start.z, Set.of(), yaw, player.getXRot(), false);
        player.startRiding(barge, true, true);
        player.forceSetRotation(yaw, false, 25.0F, false);
        NetherFriends.LOGGER.debug("Ferry crossing to {}: {} waypoints out, {} in", v.to.name(), v.out.size(), in.size());
        v.barge = barge;
        v.in = in;
        v.moved = true;
        v.movedAt = v.ticks;
    }

    private static void in(Voyage v, ServerPlayer player) {
        if (!aboard(v, player)) return;
        if (!v.barge.arrived()) return;
        player.stopRiding();
        BlockPos landing = v.to.dock().landing();
        float face = (float) (Mth.atan2(v.to.village().getZ() - landing.getZ(), v.to.village().getX() - landing.getX()) * Mth.RAD_TO_DEG) - 90.0F;
        player.teleportTo(v.level, landing.getX() + 0.5, landing.getY(), landing.getZ() + 0.5, Set.of(), face, 0.0F, false);
        player.clearFire();
        player.sendOverlayMessage(Component.literal(v.said.get("arrive")));
        List<Vec3> back = new ArrayList<>(v.in);
        java.util.Collections.reverse(back);
        v.barge.bookFor(null);
        v.barge.follow(back);
        v.stage = Stage.ASHORE;
        v.ticks = 0;
    }

    /** Keep the player on the barge while it's on the open sea; false when the voyage had to end. */
    private static boolean aboard(Voyage v, ServerPlayer player) {
        if (v.barge.isRemoved()) {
            end(v, player, true);
            return false;
        }
        if (player.getVehicle() != v.barge) {
            player.startRiding(v.barge, true, true);
            if (v.ticks % 40 == 1) player.sendOverlayMessage(Component.literal(v.said.get("stay")));
        }
        return true;
    }

    private static void letGo(Voyage v, LivingEntity ferrier) {
        v.wading = false;
        if (ferrier instanceof Magmari m) {
            BlockPos spot = v.from.dock().ferrier();
            m.getNavigation().moveTo(spot.getX() + 0.5, spot.getY(), spot.getZ() + 0.5, 1.0);
        }
    }

    /** End a voyage. {@code strand}: it ended at sea (a logout, a lost barge), so set the player down on a dock. */
    private static void end(Voyage v, @Nullable ServerPlayer player, boolean strand) {
        VOYAGES.remove(v.player);
        if (strand) NetherFriends.LOGGER.info("A ferry crossing ended at sea ({}, tick {}): barge {}", v.stage, v.ticks, v.barge.isRemoved() ? "gone" : "afloat");
        LivingEntity ferrier = ferrier(v);
        if (v.wading && ferrier != null) letGo(v, ferrier);
        if (player != null && strand) {
            if (v.stage == Stage.WAITING) refund(player, v.fare);
            if (v.stage != Stage.WAITING && v.stage != Stage.ASHORE && player.level() == v.level) {
                player.stopRiding();
                BlockPos safe = (v.stage == Stage.OUT || v.stage == Stage.CROSSING && !v.moved ? v.from.dock() : v.to.dock()).landing();
                player.teleportTo(v.level, safe.getX() + 0.5, safe.getY(), safe.getZ() + 0.5, Set.of(), player.getYRot(), player.getXRot(), false);
                ServerPlayNetworking.send(player, new FerryPayload(FerryPayload.CLEAR, List.of()));
            }
        }
        if (!v.barge.isRemoved()) v.barge.discard();
    }

    private static void abandon(ServerPlayer player) {
        Voyage v = VOYAGES.get(player.getUUID());
        if (v != null) end(v, player, true);
    }

    private static @Nullable LivingEntity ferrier(Voyage v) {
        return v.level.getEntity(v.ferrier) instanceof LivingEntity e && e.isAlive() ? e : null;
    }

    /** Keep the far dock's chunks loading, so they're ready when the crossing ends. */
    private static void ticket(Voyage v) {
        v.level.getChunkSource().addTicketWithRadius(TicketType.PORTAL, ChunkPos.containing(v.to.dock().berth()), FAR_TICKET);
    }

    // -- fares ---------------------------------------------------------------------------------------------------

    static int fare(Destination d, Bond bond) {
        return bond.level() >= FREE_AT ? 0 : Math.clamp(Math.round(d.distance() / 160.0F), 2, 8);
    }

    static int count(ServerPlayer player) {
        Inventory inv = player.getInventory();
        int n = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) if (inv.getItem(i).is(Items.WARPED_FUNGUS)) n += inv.getItem(i).getCount();
        return n;
    }

    private static void take(ServerPlayer player, int fare) {
        if (player.hasInfiniteMaterials()) return;
        Inventory inv = player.getInventory();
        for (int i = 0; i < inv.getContainerSize() && fare > 0; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.is(Items.WARPED_FUNGUS)) continue;
            int used = Math.min(fare, stack.getCount());
            stack.shrink(used);
            fare -= used;
        }
    }

    private static void refund(ServerPlayer player, int fare) {
        if (fare <= 0 || player.hasInfiniteMaterials()) return;
        ItemStack back = new ItemStack(Items.WARPED_FUNGUS, fare);
        if (!player.getInventory().add(back) && !back.isEmpty()) player.spawnAtLocation(player.level(), back);
    }

    /** Each Mooring's barge strider has a name; the same Ferrier always has the same one. */
    static String strider(LivingEntity ferrier) {
        List<String> names = lines().striders;
        return names.get(Math.floorMod(ferrier.getUUID().hashCode(), names.size()));
    }

    // -- where the barges go ---------------------------------------------------------------------------------------

    static @Nullable Structure mooring(ServerLevel level) {
        return level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(NetherFriends.id("mooring"));
    }

    /** The Ferrier's own Mooring and its dock, or null for a Ferrier with no Mooring (or one built without a dock). */
    static @Nullable Home home(ServerLevel level, LivingEntity ferrier) {
        Optional<BlockPos> village = Folk.of(ferrier).village();
        Structure structure = mooring(level);
        if (village.isEmpty() || structure == null) return null;
        StructureStart start = level.structureManager().getStructureAt(village.get(), structure);
        Sea.Dock dock = dock(start);
        return dock == null ? null : new Home(village.get(), dock);
    }

    private static Sea.@Nullable Dock dock(@Nullable StructureStart start) {
        if (start == null || !start.isValid()) return null;
        for (StructurePiece piece : start.getPieces()) {
            if (piece instanceof BuildingPiece b && b.kind().equals("mooring/dock")) return Sea.Dock.of(b.origin(), b.rotation());
        }
        return null;
    }

    /**
     * The other Moorings within reach, nearest first. Each Mooring's set has one possible spot per region; the
     * nearest few spots are generated just far enough to know whether a Mooring really stands there (structure
     * starts only, off the main thread), and those with a dock become destinations.
     */
    static CompletableFuture<List<Destination>> search(ServerLevel level, Home home) {
        Structure structure = mooring(level);
        StructureSet set = level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET).getValue(NetherFriends.id("mooring"));
        if (structure == null || set == null || !(set.placement() instanceof RandomSpreadStructurePlacement spread)) {
            return CompletableFuture.completedFuture(List.of());
        }
        ChunkPos here = ChunkPos.containing(home.village());
        int spacing = spread.spacing(), reach = SEARCH_BLOCKS / 16;
        List<ChunkPos> spots = new ArrayList<>();
        for (int rx = Math.floorDiv(here.x() - reach, spacing); rx <= Math.floorDiv(here.x() + reach, spacing); rx++) {
            for (int rz = Math.floorDiv(here.z() - reach, spacing); rz <= Math.floorDiv(here.z() + reach, spacing); rz++) {
                ChunkPos spot = spread.getPotentialStructureChunk(level.getSeed(), rx * spacing, rz * spacing);
                if (Math.abs(spot.x() - here.x()) <= 4 && Math.abs(spot.z() - here.z()) <= 4) continue;
                if (blocks(here, spot) <= SEARCH_BLOCKS) spots.add(spot);
            }
        }
        spots.sort(Comparator.comparingDouble(spot -> blocks(here, spot)));
        List<ChunkPos> nearest = spots.subList(0, Math.min(CANDIDATES, spots.size()));
        List<CompletableFuture<ChunkResult<ChunkAccess>>> futures = new ArrayList<>();
        for (ChunkPos spot : nearest) futures.add(level.getChunkSource().getChunkFuture(spot.x(), spot.z(), ChunkStatus.STRUCTURE_STARTS, true));
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApplyAsync(ignored -> {
            List<Destination> found = new ArrayList<>();
            for (CompletableFuture<ChunkResult<ChunkAccess>> f : futures) {
                ChunkAccess chunk = f.join().orElse(null);
                if (chunk == null) continue;
                StructureStart start = chunk.getStartForStructure(structure);
                Sea.Dock dock = dock(start);
                if (dock == null) continue;
                BlockPos village = null;
                for (StructurePiece piece : start.getPieces()) if (piece instanceof BuildingPiece b) village = b.village();
                if (village == null || village.equals(home.village())) continue;
                int dx = village.getX() - home.village().getX(), dz = village.getZ() - home.village().getZ();
                found.add(new Destination(label(Names.village(People.MAGMARI, village)), village, dock, (int) Math.round(Math.sqrt(dx * dx + dz * dz)),
                        compass(dx, dz)));
            }
            found.sort(Comparator.comparingInt(Destination::distance));
            return List.copyOf(found.subList(0, Math.min(MAX_DESTINATIONS, found.size())));
        }, level.getServer());
    }

    private static double blocks(ChunkPos a, ChunkPos b) {
        double dx = (a.x() - b.x()) * 16.0, dz = (a.z() - b.z()) * 16.0;
        return Math.sqrt(dx * dx + dz * dz);
    }

    /** "the Mooring of the Long Chain" is spoken of as "the Long Chain". */
    static String label(String village) {
        return village.startsWith("the Mooring of ") ? village.substring("the Mooring of ".length()) : village;
    }

    /** "north-east" as "NE". */
    static String abbreviate(String direction) {
        StringBuilder out = new StringBuilder();
        for (String part : direction.split("-")) out.append(Character.toUpperCase(part.charAt(0)));
        return out.toString();
    }

    static String compass(int dx, int dz) {
        String[] names = {"south", "south-west", "west", "north-west", "north", "north-east", "east", "south-east"};
        double yaw = Math.toDegrees(Math.atan2(dz, dx)) - 90.0;
        return names[Math.floorMod((int) Math.round(yaw / 45.0), 8)];
    }

    // -- what's said ---------------------------------------------------------------------------------------------

    record Lines(List<String> striders, List<Line> offer, List<Line> free, List<Line> searching, List<Line> none, List<Line> wild,
                 List<Line> busy, List<Line> blocked, List<Line> poor, List<Line> booked, List<Line> castoff, List<Line> hopoff,
                 List<Line> crossing, List<Line> landfall, List<Line> arrive, List<Line> late, List<Line> stay) {}

    static synchronized Lines lines() {
        if (lines == null) lines = load();
        return lines;
    }

    private static Lines load() {
        String path = "/data/netherfriends/ferry/magmari.json";
        try (InputStream in = Ferry.class.getResourceAsStream(path)) {
            if (in == null) throw new IllegalStateException("missing " + path);
            JsonObject root = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
            List<String> striders = new ArrayList<>();
            for (JsonElement e : root.getAsJsonArray("striders")) striders.add(e.getAsString());
            return new Lines(List.copyOf(striders), pool(root, "offer"), pool(root, "free"), pool(root, "searching"), pool(root, "none"),
                    pool(root, "wild"), pool(root, "busy"), pool(root, "blocked"), pool(root, "poor"), pool(root, "booked"), pool(root, "castoff"),
                    pool(root, "hopoff"), pool(root, "crossing"), pool(root, "landfall"), pool(root, "arrive"), pool(root, "late"), pool(root, "stay"));
        } catch (Exception e) {
            throw new IllegalStateException("Could not read the ferry lines (" + path + ")", e);
        }
    }

    private static List<Line> pool(JsonObject root, String key) {
        return Dialogue.lines(root.getAsJsonArray(key));
    }

}
