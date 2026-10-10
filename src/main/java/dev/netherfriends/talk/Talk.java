package dev.netherfriends.talk;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import dev.netherfriends.entity.Ghastkin;
import dev.netherfriends.entity.Magmari;
import dev.netherfriends.entity.People;
import dev.netherfriends.entity.Weft;
import dev.netherfriends.piglin.GoldRule;
import dev.netherfriends.talk.Bonds.Bond;
import dev.netherfriends.talk.Dialogue.Bank;
import dev.netherfriends.talk.Dialogue.Ctx;
import dev.netherfriends.talk.Dialogue.Line;
import dev.netherfriends.talk.Dialogue.Reply;
import dev.netherfriends.talk.Dialogue.Topic;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jspecify.annotations.Nullable;

/**
 * Conversations, run on the server. Right-click a resident (a Piglin only while you wear gold, a Ghastkin only
 * while you sneak) and the server picks their greeting and the topics you can raise from their people's dialogue
 * bank ({@link Dialogue}), sends it to your window ({@link TalkPayload}) and answers what you press. While you
 * talk they stand still and face you. Friendship ({@link Bonds}) grows as you talk, listen and give gifts. The
 * window has three tabs: Talk (topics and small talk), Story ({@link Story}) and Journal ({@link Journal}).
 */
public final class Talk {
    private Talk() {}

    static final int MAX_TOPICS = 6;
    static final double REACH_SQR = 8 * 8;
    static final int IDLE_TICKS = 20 * 180;
    static final int GIFT_LOVED = 4, GIFT_LIKED = 2, GIFT_NEUTRAL = 1, GIFT_DISLIKED = -1;
    static final String TAB_TALK = "talk", TAB_STORY = "story", TAB_JOURNAL = "journal";

    /** One player's conversation with one resident. */
    static final class Session {
        final UUID resident;
        final int entityId;
        final RandomSource random = RandomSource.create();
        final Set<String> asked = new HashSet<>();
        final Deque<String> recent = new ArrayDeque<>();
        final long order = RandomSource.create().nextLong();
        List<Reply> pending = List.of();
        boolean refused;
        int idle;
        /** The tab the window shows, the page of the story being told, and the last line said on the Talk tab. */
        String tab = TAB_TALK;
        int page;
        String talkLine = "";

        Session(LivingEntity resident) {
            this.resident = resident.getUUID();
            this.entityId = resident.getId();
        }

        void said(String text) {
            this.recent.addLast(text);
            while (this.recent.size() > 12) this.recent.removeFirst();
        }
    }

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final Map<UUID, UUID> LISTENING = new HashMap<>();

    public static void init() {
        Folk.init();
        Bonds.init();
        Journal.init();
        PayloadTypeRegistry.clientboundPlay().register(TalkPayload.TYPE, TalkPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(TalkPayload.Action.TYPE, TalkPayload.Action.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TalkPayload.Action.TYPE, (payload, context) -> act(context.player(), payload));
        ServerTickEvents.END_SERVER_TICK.register(Talk::tick);
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> end(handler.player));
        UseEntityCallback.EVENT.register(Talk::piglinInteract);
    }

    /** The player this resident is talking to, if any (a Ghastkin doesn't drift away from them). */
    public static @Nullable UUID listeningTo(Entity resident) {
        return LISTENING.get(resident.getUUID());
    }

    // -- starting ------------------------------------------------------------------------------------------------

    /**
     * Piglins are vanilla mobs: their interaction is caught here. Holding gold still barters, as in vanilla. They
     * talk to you while you wear gold, or without it once you're their friend (GoldRule).
     */
    private static InteractionResult piglinInteract(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hit) {
        if (!(entity instanceof AbstractPiglin piglin) || hand != InteractionHand.MAIN_HAND || player.isSpectator() || !piglin.isAlive()) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getMainHandItem();
        if (held.is(Items.NAME_TAG) || held.getItem() instanceof SpawnEggItem) return InteractionResult.PASS;
        if (piglin instanceof Piglin p && !p.isBaby() && held.is(PiglinAi.BARTERING_ITEM)) return InteractionResult.PASS;
        if (!GoldRule.showsGold(piglin, player)) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (player instanceof ServerPlayer sp) open(sp, piglin);
        return InteractionResult.SUCCESS_SERVER;
    }

    /** Open a conversation: the greeting (or a refusal), the topics, and today's friendship point. */
    public static void open(ServerPlayer player, LivingEntity resident) {
        People people = People.of(resident);
        if (people == null) return;
        Folk.Profile profile = Folk.of(resident);
        if (resident instanceof Ghastkin && !player.isShiftKeyDown()) {
            player.sendOverlayMessage(Component.literal(Folk.name(resident) + " drifts back. Come closer quietly (sneak) to talk."));
            return;
        }
        end(player);
        Session s = new Session(resident);
        SESSIONS.put(player.getUUID(), s);
        LISTENING.put(resident.getUUID(), player.getUUID());
        Bank bank = Dialogue.bank(people);
        Bond bond = Bonds.get(resident, player.getUUID());
        String state = refusal(resident, player);
        Ctx ctx = ctx(resident, profile, player, bond, state);
        if (state != null) {
            s.refused = true;
            send(player, resident, s, line(bank.refuse(), ctx, s, resident), "", List.of(), true, true);
            return;
        }
        Line greet = ctx.first() ? firstMeeting(bank.greet(), ctx, s) : null;
        if (greet == null) greet = Dialogue.pick(bank.greet(), ctx, s.random, Set.copyOf(s.recent));
        String text = say(greet, ctx, s, resident);
        String status = "";
        long day = day(player);
        Journal.Entry entry = Journal.get(resident, player.getUUID());
        if (ctx.first()) entry = entry.remember(day, "You met " + Folk.fullName(profile, people) + whereabouts(profile, people) + ".");
        if (bond.talkDay() != day) {
            bond = bond.talked(day).add(1);
            status = "Talked today: +1";
            entry = entry.visit(day);
        }
        Journal.put(resident, player.getUUID(), entry);
        s.talkLine = text;
        finish(player, resident, s, bond, text, status, true);
    }

    private static String whereabouts(Folk.Profile profile, People people) {
        String village = Folk.village(profile, people);
        if (village != null) return " in " + village;
        return profile.place().equals("bastion") ? " in a Bastion Remnant" : " out in the wild";
    }

    /** Why they won't talk, or null: a Weft whose forest you cut, or anyone fighting you. */
    private static @Nullable String refusal(LivingEntity resident, Player player) {
        if (resident instanceof Weft weft && weft.hasGrudgeAgainst(player)) return "grudge";
        if (resident instanceof Mob mob && mob.getTarget() == player) return "angry";
        if (resident instanceof AbstractPiglin piglin
                && piglin.getBrain().getMemory(MemoryModuleType.ANGRY_AT).filter(player.getUUID()::equals).isPresent()) return "angry";
        if (resident.getLastHurtByMob() == player && resident.tickCount - resident.getLastHurtByMobTimestamp() < 20 * 30) return "angry";
        return null;
    }

    private static @Nullable Line firstMeeting(List<Line> greet, Ctx ctx, Session s) {
        List<Line> first = greet.stream().filter(l -> Boolean.TRUE.equals(l.when().first())).toList();
        return Dialogue.pick(first, ctx, s.random, Set.copyOf(s.recent));
    }

    // -- answering -----------------------------------------------------------------------------------------------

    private static void act(ServerPlayer player, TalkPayload.Action action) {
        Session s = SESSIONS.get(player.getUUID());
        if (s == null || s.entityId != action.entityId() || !s.resident.equals(action.residentId())) return;
        if (!(player.level().getEntity(s.resident) instanceof LivingEntity resident) || !resident.isAlive()
                || player.distanceToSqr(resident) > REACH_SQR) {
            end(player);
            return;
        }
        s.idle = 0;
        String a = action.action();
        if (a.equals("bye")) {
            farewell(player, resident, s);
            return;
        }
        if (s.refused) return;
        People people = People.of(resident);
        Bank bank = Dialogue.bank(people);
        Folk.Profile profile = Folk.of(resident);
        Bond bond = Bonds.get(resident, player.getUUID());
        Ctx ctx = ctx(resident, profile, player, bond, null);
        String text;
        String status = "";
        if (a.startsWith("tab:") || a.startsWith("story:") || a.startsWith("journal:")) {
            tabs(player, resident, s, bond, ctx, a);
            return;
        }
        s.tab = TAB_TALK;
        if (a.startsWith("topic:")) {
            String id = a.substring(6);
            Optional<Topic> topic = bank.topics().stream().filter(t -> t.id().equals(id) && Dialogue.open(t, ctx)).findFirst();
            if (topic.isEmpty()) return;
            Line line = Dialogue.pick(topic.get().lines(), ctx, s.random, Set.copyOf(s.recent));
            text = say(line, ctx, s, resident);
            s.asked.add(id);
            if (!bond.heard().contains(id)) {
                bond = bond.hear(id).add(1);
                status = "Something new: +1";
            }
        } else if (a.startsWith("reply:")) {
            int n;
            try {
                n = Integer.parseInt(a.substring(6));
            } catch (NumberFormatException e) {
                return;
            }
            if (n < 0 || n >= s.pending.size()) return;
            Reply reply = s.pending.get(n);
            s.pending = List.of();
            text = fill(reply.text(), ctx, resident);
            s.said(reply.text());
            if (reply.points() != 0) {
                bond = bond.add(reply.points());
                status = reply.points() > 0 ? "They liked that: +" + reply.points() : "That stung: " + reply.points();
            }
        } else if (a.equals("chatter")) {
            text = say(Dialogue.pick(bank.chatter(), ctx, s.random, Set.copyOf(s.recent)), ctx, s, resident);
        } else if (a.equals("gift")) {
            ItemStack held = player.getMainHandItem();
            long day = day(player);
            String kind;
            int points = 0;
            if (held.isEmpty()) kind = "empty";
            else if (bond.giftDay() == day) kind = "again";
            else if (bank.loved().stream().anyMatch(p -> p.test(held))) { kind = "loved"; points = GIFT_LOVED; }
            else if (bank.liked().stream().anyMatch(p -> p.test(held))) { kind = "liked"; points = GIFT_LIKED; }
            else if (bank.disliked().stream().anyMatch(p -> p.test(held))) { kind = "disliked"; points = GIFT_DISLIKED; }
            else { kind = "neutral"; points = GIFT_NEUTRAL; }
            // the line is chosen while the gift is still in hand: some lines name what you're holding
            text = say(Dialogue.pick(bank.giftLines().getOrDefault(kind, List.of()), ctx, s.random, Set.copyOf(s.recent)), ctx, s, resident);
            if (!kind.equals("empty") && !kind.equals("again")) {
                String gift = held.getHoverName().getString();
                Journal.put(resident, player.getUUID(), Journal.get(resident, player.getUUID()).learn(kind, Story.itemId(held))
                        .remember(day, "Gift: " + gift + ". " + Folk.name(resident) + switch (kind) {
                            case "loved" -> " loved it.";
                            case "liked" -> " liked it.";
                            case "disliked" -> " didn't like it.";
                            default -> " took it.";
                        }));
                if (!player.hasInfiniteMaterials()) held.shrink(1);
                bond = bond.gifted(day).add(points);
                status = "Gave " + gift + ": " + (points > 0 ? "+" : "") + points;
            }
            s.pending = List.of();
        } else if (a.equals("barter")) {
            end(player);
            if (resident instanceof Piglin piglin && !piglin.isBaby() && player.getMainHandItem().is(PiglinAi.BARTERING_ITEM)) {
                piglin.mobInteract(player, InteractionHand.MAIN_HAND);
            }
            return;
        } else {
            return;
        }
        s.talkLine = text;
        finish(player, resident, s, bond, text, status, false);
    }

    /** The Story and Journal tabs, and back to Talk. */
    private static void tabs(ServerPlayer player, LivingEntity resident, Session s, Bond bond, Ctx ctx, String a) {
        if (a.equals("tab:" + TAB_STORY)) {
            s.tab = TAB_STORY;
            s.page = 0;
            Story.View view = Story.view(player, resident, s, bond, ctx);
            send(player, resident, s, view.text(), view.status(), view.choices(), false, false);
        } else if (a.equals("tab:" + TAB_JOURNAL) || a.startsWith("journal:")) {
            s.tab = TAB_JOURNAL;
            String page = a.startsWith("journal:") ? a.substring(8) : "memories";
            send(player, resident, s, Journal.page(page, player, resident, bond), "", Journal.choices(resident), false, false);
        } else if (a.equals("tab:" + TAB_TALK)) {
            s.tab = TAB_TALK;
            Bank bank = Dialogue.bank(People.of(resident));
            send(player, resident, s, s.talkLine.isEmpty() ? "..." : s.talkLine, "", choices(s, bank, ctx, bond), false, false);
        } else if (a.startsWith("story:")) {
            s.tab = TAB_STORY;
            Story.Outcome outcome = Story.act(player, resident, s, bond, ctx, a.substring(6));
            if (outcome == null) return;
            Bond after = outcome.bond();
            Story.View view = Story.view(player, resident, s, after, ctx(resident, Folk.of(resident), player, after, null));
            finish(player, resident, s, after, outcome.said() != null ? outcome.said() : view.text(),
                    outcome.status().isEmpty() ? view.status() : outcome.status(), false, view.choices());
        }
    }

    /** Save the bond, play any level-ups (with their gifts) and send the window its new state. */
    private static void finish(ServerPlayer player, LivingEntity resident, Session s, Bond bond, String text, String status, boolean opening) {
        finish(player, resident, s, bond, text, status, opening, null);
    }

    /** Save the bond, play any level-ups (with their rewards, and a Journal memory each) and send the window. */
    private static void finish(ServerPlayer player, LivingEntity resident, Session s, Bond bond, String text, String status, boolean opening,
                               @Nullable List<TalkPayload.Choice> choices) {
        People people = People.of(resident);
        Bank bank = Dialogue.bank(people);
        Folk.Profile profile = Folk.of(resident);
        boolean levelUp = false;
        int level = bond.level();
        if (level > bond.best()) {
            for (int n = bond.best() + 1; n <= level; n++) {
                Ctx at = ctx(resident, profile, player, bond, null, n);
                Line up = Dialogue.pick(bank.levelup().stream().filter(l -> l.when().minLevel() == at.level()).toList(), at, s.random, Set.of());
                if (up == null) continue;
                text = text + " " + fill(up.text(), at, resident);
                if (!up.reward().isEmpty()) {
                    ItemStack gift = up.reward().copy();
                    status = "Friendship: " + bank.levels().get(n) + "! They gave you " + gift.getCount() + " " + gift.getHoverName().getString();
                    if (!player.getInventory().add(gift) && !gift.isEmpty()) player.spawnAtLocation(player.level(), gift);
                } else {
                    status = "Friendship: " + bank.levels().get(n) + "!";
                }
                String memory = "You became " + Folk.name(resident) + "'s " + bank.levels().get(n) + ".";
                if (people == People.PIGLIN && n == GoldRule.TRUST_LEVEL && !resident.isBaby()) memory += " Gold or no gold, they won't attack you now.";
                Journal.remember(resident, player.getUUID(), day(player), memory);
            }
            bond = bond.reached(level);
            levelUp = true;
        }
        Bonds.put(resident, player.getUUID(), bond);
        Ctx ctx = ctx(resident, profile, player, bond, null);
        if (choices == null) choices = s.tab.equals(TAB_JOURNAL) ? Journal.choices(resident) : choices(s, bank, ctx, bond);
        send(player, resident, s, text, status, choices, false, levelUp, opening);
    }

    private static void farewell(ServerPlayer player, LivingEntity resident, Session s) {
        end(player);
        if (s.refused) return;
        People people = People.of(resident);
        Folk.Profile profile = Folk.of(resident);
        Ctx ctx = ctx(resident, profile, player, Bonds.get(resident, player.getUUID()), null);
        Line line = Dialogue.pick(Dialogue.bank(people).farewell(), ctx, s.random, Set.copyOf(s.recent));
        if (line != null) player.sendOverlayMessage(Component.literal(Folk.name(resident) + ": " + fill(line.text(), ctx, resident)));
    }

    public static void end(Player player) {
        Session s = SESSIONS.remove(player.getUUID());
        if (s != null) LISTENING.remove(s.resident);
    }

    // -- the topics on offer -------------------------------------------------------------------------------------

    private static List<TalkPayload.Choice> choices(Session s, Bank bank, Ctx ctx, Bond bond) {
        List<TalkPayload.Choice> out = new ArrayList<>();
        if (!s.pending.isEmpty()) {
            for (int i = 0; i < s.pending.size(); i++) out.add(new TalkPayload.Choice("reply:" + i, s.pending.get(i).ask(), false));
            return out;
        }
        List<Topic> open = bank.topics().stream().filter(t -> Dialogue.open(t, ctx)).toList();
        List<Topic> left = open.stream().filter(t -> !s.asked.contains(t.id())).toList();
        if (left.isEmpty()) {
            s.asked.clear();
            left = open;
        }
        // topics they haven't heard first, then the rest, in an order that holds for the whole conversation
        List<Topic> sorted = new ArrayList<>(left);
        sorted.sort(Comparator.comparing((Topic t) -> bond.heard().contains(t.id()))
                .thenComparingLong(t -> dev.netherfriends.world.Names.mix(t.id().hashCode() ^ s.order)));
        for (Topic t : sorted.subList(0, Math.min(MAX_TOPICS, sorted.size()))) {
            out.add(new TalkPayload.Choice("topic:" + t.id(), t.ask(), !bond.heard().contains(t.id())));
        }
        return out;
    }

    // -- lines ---------------------------------------------------------------------------------------------------

    static String line(List<Line> lines, Ctx ctx, Session s, LivingEntity resident) {
        return say(Dialogue.pick(lines, ctx, s.random, Set.copyOf(s.recent)), ctx, s, resident);
    }

    /** Fill a picked line in, remember it (so it isn't said again soon) and keep its replies on offer. */
    private static String say(@Nullable Line line, Ctx ctx, Session s, LivingEntity resident) {
        if (line == null) {
            s.pending = List.of();
            return "...";
        }
        s.said(line.text());
        s.pending = line.replies();
        return fill(line.text(), ctx, resident);
    }

    static String fill(String text, Ctx ctx, LivingEntity resident) {
        People people = People.of(resident);
        Folk.Profile p = Folk.of(resident);
        Map<String, String> values = new HashMap<>();
        values.put("name", Folk.name(resident));
        values.put("epithet", Folk.epithet(p, people, ctx.child()));
        values.put("job", p.job().toLowerCase());
        values.put("player", ctx.player().getName().getString());
        String village = Folk.village(p, people);
        values.put("village", village != null ? village : "home");
        if (text.contains("{neighbour}")) {
            List<String> neighbours = Folk.neighbours(resident);
            values.put("neighbour", neighbours.isEmpty() ? "a neighbour" : neighbours.get(resident.getRandom().nextInt(neighbours.size())));
        }
        String kin = p.kin();
        switch (people) {
            case PIGLIN -> values.put("clan", kin);
            case WITHREN -> values.put("kept", kin.isEmpty() ? "no one" : kin);
            case MAGMARI -> values.put("chain", kin);
            case WEFT -> values.put("row", kin);
            case GHASTKIN -> values.put("former", kin);
        }
        return Dialogue.fill(text, values);
    }

    private static Ctx ctx(LivingEntity resident, Folk.Profile profile, Player player, Bond bond, @Nullable String state) {
        return ctx(resident, profile, player, bond, state, bond.level());
    }

    private static Ctx ctx(LivingEntity resident, Folk.Profile profile, Player player, Bond bond, @Nullable String state, int level) {
        return new Ctx(level, resident.isBaby(), Folk.female(resident), profile.job(), resident instanceof Magmari m && m.isCold(),
                bond.talkDay() < 0, player, profile.place(), state);
    }

    static long day(Player player) {
        return player.level().getGameTime() / 24000L;
    }

    // -- the window ----------------------------------------------------------------------------------------------

    private static void send(ServerPlayer player, LivingEntity resident, Session s, String text, String status, List<TalkPayload.Choice> choices,
                             boolean refused, boolean opening) {
        send(player, resident, s, text, status, choices, refused, false, opening);
    }

    private static void send(ServerPlayer player, LivingEntity resident, Session s, String text, String status, List<TalkPayload.Choice> choices,
                             boolean refused, boolean levelUp, boolean opening) {
        People people = People.of(resident);
        Folk.Profile p = Folk.of(resident);
        Bank bank = Dialogue.bank(people);
        Bond bond = Bonds.get(resident, player.getUUID());
        int level = bond.level();
        List<String> bits = new ArrayList<>();
        String epithet = Folk.epithet(p, people, resident.isBaby());
        if (!epithet.isEmpty()) bits.add(epithet);
        bits.add(p.job().equals(Folk.CHILD) ? "child" : p.job());
        String village = Folk.village(p, people);
        if (village != null) bits.add(village);
        else if (p.place().equals("bastion")) bits.add("Bastion Remnant");
        boolean canBarter = resident instanceof Piglin piglin && !piglin.isBaby() && player.getMainHandItem().is(PiglinAi.BARTERING_ITEM);
        boolean canGift = bond.giftDay() != day(player);
        int next = level >= Bonds.MAX_LEVEL ? Bonds.THRESHOLDS[Bonds.MAX_LEVEL] : Bonds.THRESHOLDS[level + 1];
        ServerPlayNetworking.send(player, new TalkPayload(resident.getId(), resident.getUUID(), people.ordinal(), Folk.name(resident),
                String.join(" · ", bits), bank.levels().get(level), level, bond.points(), Bonds.THRESHOLDS[level], next, text, status,
                choices.subList(0, Math.min(choices.size(), TalkPayload.MAX_CHOICES)), opening, levelUp, canGift, canBarter, refused, s.tab));
    }

    // -- while talking -------------------------------------------------------------------------------------------

    /** Residents in a conversation stand still and face whoever they're talking to; conversations left idle end. */
    private static void tick(MinecraftServer server) {
        if (SESSIONS.isEmpty()) return;
        for (UUID id : List.copyOf(SESSIONS.keySet())) {
            ServerPlayer player = server.getPlayerList().getPlayer(id);
            Session s = SESSIONS.get(id);
            if (player == null) {
                SESSIONS.remove(id);
                LISTENING.remove(s.resident);
                continue;
            }
            ServerLevel level = player.level();
            if (!(level.getEntity(s.resident) instanceof LivingEntity resident) || !resident.isAlive()
                    || player.distanceToSqr(resident) > REACH_SQR * 1.5 || ++s.idle > IDLE_TICKS) {
                end(player);
                continue;
            }
            if (resident instanceof Mob mob) {
                mob.getNavigation().stop();
                mob.getLookControl().setLookAt(player, 30.0F, 30.0F);
                if (mob instanceof AbstractPiglin piglin) {
                    piglin.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                    piglin.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(player, true));
                }
            }
        }
    }
}
