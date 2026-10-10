package dev.netherfriends.client;

import java.util.ArrayList;
import java.util.List;

import dev.netherfriends.entity.Magmari;
import dev.netherfriends.entity.People;
import dev.netherfriends.talk.TalkPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The conversation window: Village Friends' layout in netherrack. The resident stays in view in the world; their
 * portrait and name ribbon sit on a dialogue box at the bottom, where their line types out in their people's
 * voice; what you can say floats as numbered bubbles on the right; a dock along the bottom has Talk, Gift, Barter
 * (Piglins), Ferry (a Mooring's Ferrier) and Goodbye; and a card in the corner shows your friendship in their people's own tokens, with three
 * tabs under it: Talk, Story (their own story, step by step) and Journal (what you've shared, shown at once in a
 * taller box you can scroll).
 */
public final class TalkScreen extends Screen {
    // netherrack: light ink on dark stone (Village Friends is dark ink on paper, so every colour is new here)
    static final int INK = 0xFFFBEEE2, MUTED = 0xFFDCB0A2, PAPER = 0xFF6A2B29, PAPER_SHADE = 0xFF55211F, EDGE = 0xFF260D0C,
            EDGE_LIGHT = 0xFFB35D53, GOLD = 0xFFF2C14E, GOLD_BRIGHT = 0xFFFFE08A, BUBBLE = 0xFF7B3431, BUBBLE_HOVER = 0xFF923F3A,
            BUBBLE_OFF = 0xFF4A2220, METER_EMPTY = 0xFF44201D, WASH = 0xD8000000 | (PAPER & 0xFFFFFF);
    static final Identifier NETHERRACK = Identifier.withDefaultNamespace("textures/block/netherrack.png");
    /** Name ribbon colour per people: gold, soul fire, ember, warped teal, delta smoke. */
    static final int[] RIBBONS = {0xFFC98F22, 0xFF2F9AA8, 0xFFD0561E, 0xFF23877F, 0xFF7D8AA3};
    private static final float CHARS_PER_SECOND = 36F;
    private static final int SPACE_KEY = 32, KEY_1 = 49; // key codes (the same in GLFW and SDL)
    private static final int LINE = 10;
    private static final String[][] TABS = {{"talk", "Talk"}, {"story", "Story"}, {"journal", "Journal"}};

    private TalkPayload data;
    private final People people;
    private final List<Button> actions = new ArrayList<>();
    private boolean waiting, closing;
    private int waitTicks, ticks, missingTicks;
    private int boxX, boxY, boxW, boxH, textX, textW, portraitX, portraitY, portraitW, portraitH, cardX, cardY, cardW, cardH;
    private int dialogueScroll;
    // typing
    private List<String> typeLines = List.of();
    private int[] lineStarts = new int[0];
    private String typeKey = "", typeFlat = "";
    private int shown, sinceBlip;
    private float typeBudget, typePause;
    private long lastFrameNanos;
    private boolean popPending = true;
    private int levelUpAt = -1000, statusAt = -1000;

    public TalkScreen(TalkPayload data) {
        super(Component.literal(data.name()));
        this.data = data;
        this.people = People.values()[Math.floorMod(data.people(), People.values().length)];
        if (data.levelUp()) this.levelUpAt = 0;
        if (!data.status().isEmpty()) this.statusAt = 0;
    }

    /** What the window is showing now (for tests). */
    public TalkPayload payload() {
        return this.data;
    }

    public boolean matches(TalkPayload next) {
        return this.data.residentId().equals(next.residentId());
    }

    public void update(TalkPayload next) {
        if (!this.data.line().equals(next.line()) || next.opening() || !this.data.tab().equals(next.tab())) {
            this.dialogueScroll = 0;
            this.restartTyping();
        }
        if (next.levelUp()) this.levelUpAt = this.ticks;
        if (!next.status().isEmpty() && !next.status().equals(this.data.status())) this.statusAt = this.ticks;
        this.data = next;
        this.waiting = false;
        this.waitTicks = 0;
        this.rebuildWidgets();
        this.triggerImmediateNarration(false);
    }

    // -- the voice -----------------------------------------------------------------------------------------------

    /** Piglins grunt, Withren hum low and hollow, Magmari crackle (lower when cold), Weft click, Ghastkin fall soft. */
    private Holder<SoundEvent> voice() {
        return switch (this.people) {
            case PIGLIN -> SoundEvents.NOTE_BLOCK_BASS;
            case WITHREN -> SoundEvents.NOTE_BLOCK_FLUTE;
            case MAGMARI -> SoundEvents.NOTE_BLOCK_GUITAR;
            case WEFT -> SoundEvents.NOTE_BLOCK_HAT;
            case GHASTKIN -> SoundEvents.NOTE_BLOCK_CHIME;
        };
    }

    private float pitch(char c) {
        LivingEntity speaker = this.speaker();
        boolean child = speaker != null && speaker.isBaby();
        float base = switch (this.people) {
            case PIGLIN -> 0.75F;
            case WITHREN -> 0.55F;
            case MAGMARI -> speaker instanceof Magmari m && m.isCold() ? 0.6F : 0.8F;
            case WEFT -> 1.45F;
            // soft notes falling through the line
            case GHASTKIN -> 1.35F - 0.35F * this.shown / Math.max(1F, this.typeFlat.length());
        };
        return (child ? base * 1.35F : base) + Character.toLowerCase(c) % 7 * 0.03F;
    }

    private void restartTyping() {
        this.shown = 0;
        this.typeBudget = 0;
        this.typePause = 0;
        this.sinceBlip = 0;
        this.popPending = true;
    }

    private boolean typing() {
        return this.shown < this.typeFlat.length();
    }

    private void finishLine() {
        if (this.typing()) {
            this.shown = this.typeFlat.length();
            this.play(this.voice(), 1.2F, 0.3F);
        }
    }

    private void play(Holder<SoundEvent> sound, float pitch, float volume) {
        if (this.minecraft != null) this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), pitch, volume));
    }

    private void layoutTyping(int wrapWidth) {
        String key = this.data.line() + '|' + wrapWidth;
        if (key.equals(this.typeKey)) return;
        this.typeKey = key;
        this.typeLines = this.font.splitIgnoringLanguage(Component.literal(this.data.line()), wrapWidth).stream().map(t -> t.getString()).toList();
        this.lineStarts = new int[this.typeLines.size()];
        int total = 0;
        for (int i = 0; i < this.lineStarts.length; i++) {
            this.lineStarts[i] = total;
            total += this.typeLines.get(i).length();
        }
        this.typeFlat = String.join("", this.typeLines);
        this.shown = Math.min(this.shown, total);
    }

    /** A Journal page is your own notes: it appears at once, without their voice. */
    private boolean instant() {
        return this.data.tab().equals("journal");
    }

    private void advanceTyping() {
        if (this.instant()) {
            this.shown = this.typeFlat.length();
            this.popPending = false;
            this.lastFrameNanos = 0;
            return;
        }
        long now = System.nanoTime();
        float dt = this.lastFrameNanos == 0 ? 0F : Math.min(0.1F, (now - this.lastFrameNanos) / 1.0E9F);
        this.lastFrameNanos = now;
        if (this.popPending) {
            this.popPending = false;
            this.play(this.voice(), this.pitch('a') * 1.25F, 0.35F);
        }
        if (!this.typing()) return;
        this.typeBudget += dt;
        float cost = 1F / CHARS_PER_SECOND;
        while (this.typeBudget > 0 && this.typing()) {
            if (this.typePause > 0) {
                float used = Math.min(this.typePause, this.typeBudget);
                this.typePause -= used;
                this.typeBudget -= used;
                continue;
            }
            if (this.typeBudget < cost) break;
            this.typeBudget -= cost;
            char c = this.typeFlat.charAt(this.shown++);
            if (!Character.isWhitespace(c) && c != '*' && ++this.sinceBlip >= 2) {
                this.sinceBlip = 0;
                this.play(this.voice(), this.pitch(c), 0.28F);
            }
            if (".!?".indexOf(c) >= 0) this.typePause = 0.18F;
            else if (",;:".indexOf(c) >= 0) this.typePause = 0.08F;
        }
    }

    // -- layout --------------------------------------------------------------------------------------------------

    @Override
    protected void init() {
        this.actions.clear();
        int dockH = 20, bottom = this.height - 8 - dockH - 5;
        this.boxW = Math.min(430, this.width - 16);
        this.boxX = (this.width - this.boxW) / 2;
        this.portraitW = 64;
        this.textX = this.boxX + this.portraitW + 18;
        this.textW = this.boxX + this.boxW - 12 - this.textX;
        int count = this.data.choices().size(), row = 18;
        this.boxH = this.instant() ? 114 : 64;
        int columns = count * row > bottom - this.boxH - 30 ? 2 : 1;
        int rows = (count + columns - 1) / columns;
        int replyW = columns == 1 ? Math.clamp(this.width * 2 / 5, 130, 196) : Math.clamp((this.boxW - 12) / 2 - 2, 100, 170);
        this.boxY = bottom - this.boxH;
        this.portraitH = Math.min(this.boxH + 30, 118);
        this.portraitX = this.boxX + 7;
        this.portraitY = this.boxY + this.boxH - 7 - this.portraitH;
        this.cardX = 8;
        this.cardY = 8;
        this.cardW = Math.min(176, this.width / 2 - 12);
        this.cardH = 66;
        // the tabs, along the bottom of the friendship card
        int tabW = (this.cardW - 12 - 6) / TABS.length;
        for (int i = 0; i < TABS.length; i++) {
            String id = TABS[i][0];
            TabButton tab = new TabButton(this.cardX + 6 + i * (tabW + 3), this.cardY + this.cardH - 19, tabW, 13, TABS[i][1], id.equals(this.data.tab()),
                    pressed -> this.send("tab:" + id));
            tab.active = !this.waiting && !this.data.refused();
            tab.setTooltip(Tooltip.create(Component.literal(switch (id) {
                case "story" -> "Story: their own story, one step at a time";
                case "journal" -> "Journal: what you've shared, and what you know about them";
                default -> "Talk: topics and small talk";
            })));
            this.actions.add(tab);
            this.addRenderableWidget(tab);
        }
        // replies float above the box on the right, hugging the screen's edge so the resident stays in view
        int replyRight = Math.max(this.boxX + this.boxW, this.width - 10), replyBottom = this.boxY - 18;
        for (int i = 0; i < count; i++) {
            TalkPayload.Choice choice = this.data.choices().get(i);
            int column = columns == 1 ? 0 : i % 2, r = columns == 1 ? i : i / 2;
            int x = replyRight - (columns - column) * (replyW + 4) + 4, y = replyBottom - (rows - r) * row;
            ReplyButton button = new ReplyButton(x, y, replyW, row - 3, i + 1, choice.label(), choice.fresh(), pressed -> this.send(choice.id()));
            button.active = !this.waiting;
            if (choice.fresh()) button.setTooltip(Tooltip.create(Component.literal("Something you haven't heard about yet")));
            this.actions.add(button);
            this.addRenderableWidget(button);
        }
        // the dock: Talk, Gift, Barter (Piglins), Goodbye
        ItemStack held = this.minecraft.player == null ? ItemStack.EMPTY : this.minecraft.player.getMainHandItem();
        boolean piglin = this.people == People.PIGLIN;
        List<String> ids = new ArrayList<>(List.of("chatter", "gift"));
        if (piglin) ids.add("barter");
        if (this.data.canFerry()) ids.add("ferry");
        ids.add("bye");
        boolean wide = this.width >= 520;
        int each = wide ? 70 : 22, gap = 4, total = ids.size() * each + (ids.size() - 1) * gap + 8;
        int x = (this.width - total) / 2, y = this.height - 8 - dockH;
        for (String id : ids) {
            if (id.equals("bye")) x += 8;
            ItemStack icon = switch (id) {
                case "gift" -> held.isEmpty() ? new ItemStack(Items.CRIMSON_FUNGUS) : held.copyWithCount(1);
                case "barter" -> new ItemStack(Items.GOLD_INGOT);
                case "ferry" -> new ItemStack(Items.WARPED_FUNGUS_ON_A_STICK);
                case "bye" -> new ItemStack(Items.CRIMSON_DOOR);
                default -> ItemStack.EMPTY;
            };
            String label = switch (id) {
                case "chatter" -> "Talk";
                case "gift" -> "Gift";
                case "barter" -> "Barter";
                case "ferry" -> "Ferry";
                default -> "Goodbye";
            };
            DockButton button = new DockButton(x, y, each, dockH, label, icon, id.equals("chatter"), wide, pressed -> {
                if (id.equals("bye")) this.onClose();
                else this.send(id);
            });
            button.setTooltip(Tooltip.create(Component.literal(this.tooltip(id, held))));
            if (this.data.refused()) button.active = id.equals("bye");
            else if (id.equals("gift")) button.active = !this.waiting && this.data.canGift();
            else if (id.equals("barter")) button.active = !this.waiting && this.data.canBarter();
            else if (!id.equals("bye")) button.active = !this.waiting;
            if (!id.equals("bye")) this.actions.add(button);
            this.addRenderableWidget(button);
            x += each + gap;
        }
    }

    private String tooltip(String id, ItemStack held) {
        return switch (id) {
            case "chatter" -> "Talk: small talk and news";
            case "gift" -> !this.data.canGift() ? "Gift: one a day, and you've given one today"
                    : "Gift: offer " + (held.isEmpty() ? "the item in your main hand (you're holding nothing)" : "one " + held.getHoverName().getString());
            case "barter" -> this.data.canBarter() ? "Barter: hand over the gold ingot you're holding" : "Barter: hold a gold ingot to barter";
            case "ferry" -> "Ferry: a strider barge across the lava sea to another Mooring";
            default -> "Goodbye (Esc)";
        };
    }

    private void send(String action) {
        if (this.waiting || !ClientPlayNetworking.canSend(TalkPayload.Action.TYPE)) return;
        ClientPlayNetworking.send(new TalkPayload.Action(this.data.entityId(), this.data.residentId(), action));
        if (action.equals("barter")) {
            this.closing = true;
            super.onClose();
            return;
        }
        this.waiting = true;
        this.waitTicks = 0;
        this.actions.forEach(button -> button.active = false);
    }

    /** Leaving says goodbye, so the resident can answer with a farewell over the hotbar. */
    @Override
    public void onClose() {
        if (!this.closing && ClientPlayNetworking.canSend(TalkPayload.Action.TYPE)) {
            ClientPlayNetworking.send(new TalkPayload.Action(this.data.entityId(), this.data.residentId(), "bye"));
        }
        this.closing = true;
        super.onClose();
    }

    // -- input ---------------------------------------------------------------------------------------------------

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == SPACE_KEY && this.typing()) {
            this.finishLine();
            return true;
        }
        int index = event.key() - KEY_1;
        if (index >= 0 && index < this.data.choices().size() && !this.waiting) {
            this.send(this.data.choices().get(index).id());
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.typing() && event.x() >= this.boxX && event.x() < this.boxX + this.boxW && event.y() >= this.boxY && event.y() < this.boxY + this.boxH) {
            this.finishLine();
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
        if (x >= this.boxX && x < this.boxX + this.boxW && y >= this.boxY && y < this.boxY + this.boxH) {
            this.dialogueScroll = Math.clamp(this.dialogueScroll - (int) Math.signum(vertical), 0, Math.max(0, this.typeLines.size() - this.visibleLines()));
            return true;
        }
        return super.mouseScrolled(x, y, horizontal, vertical);
    }

    private LivingEntity speaker() {
        return this.minecraft != null && this.minecraft.level != null && this.minecraft.level.getEntity(this.data.entityId()) instanceof LivingEntity e
                && e.getUUID().equals(this.data.residentId()) ? e : null;
    }

    @Override
    public void tick() {
        this.ticks++;
        if (this.minecraft.player == null || this.minecraft.level == null) {
            this.onClose();
            return;
        }
        LivingEntity resident = this.speaker();
        // on a busy server the window can open a moment before the resident reaches this client: wait a little
        if (resident == null) {
            if (++this.missingTicks > 40) this.onClose();
            return;
        }
        this.missingTicks = 0;
        if (!resident.isAlive() || this.minecraft.player.distanceToSqr(resident) > 64 || !this.minecraft.player.isAlive()) {
            this.onClose();
            return;
        }
        if (this.waiting && ++this.waitTicks > 100) {
            this.waiting = false;
            this.rebuildWidgets();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean isInGameUi() {
        return true;
    }

    @Override
    public Component getNarrationMessage() {
        return Component.literal(this.data.name() + ", " + this.data.subtitle() + ". " + this.data.levelName() + ". " + this.data.line() + " " + this.data.status());
    }

    // -- drawing -------------------------------------------------------------------------------------------------

    /** No blur: the resident stays in view in the world; a soft shade lifts the text. */
    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        g.fillGradient(0, this.height / 2, this.width, this.height, 0x00000000, 0x77150605);
    }

    private int visibleLines() {
        return Math.max(1, (this.boxH - 24) / LINE);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        float now = this.ticks + delta;
        this.drawCard(g, mouseX, mouseY);
        panel(g, this.boxX, this.boxY, this.boxW, this.boxH);
        frame(g, this.portraitX - 2, this.portraitY - 2, this.portraitW + 4, this.portraitH + 4);
        g.fillGradient(this.portraitX, this.portraitY, this.portraitX + this.portraitW, this.portraitY + this.portraitH, 0xFF3A1412, 0xFF8A3A2C);
        LivingEntity resident = this.speaker();
        if (resident != null) this.drawPortrait(g, resident, mouseX, mouseY);
        this.drawRibbon(g, mouseX, mouseY);
        // the line
        this.layoutTyping(this.textW);
        this.advanceTyping();
        int visible = this.visibleLines();
        if (this.typing()) {
            int lastLine = 0;
            for (int i = 0; i < this.lineStarts.length; i++) if (this.lineStarts[i] < this.shown) lastLine = i;
            this.dialogueScroll = Math.max(this.dialogueScroll, lastLine - visible + 1);
        }
        this.dialogueScroll = Math.clamp(this.dialogueScroll, 0, Math.max(0, this.typeLines.size() - visible));
        int top = this.boxY + 9;
        g.enableScissor(this.textX, top - 1, this.textX + this.textW, top + visible * LINE);
        for (int line = this.dialogueScroll, y = top; line < Math.min(this.typeLines.size(), this.dialogueScroll + visible); line++, y += LINE) {
            int chars = Math.min(this.typeLines.get(line).length(), this.shown - this.lineStarts[line]);
            if (chars <= 0) break;
            g.text(this.font, this.typeLines.get(line).substring(0, chars), this.textX, y, this.instant() && line == 0 ? GOLD : INK, true);
        }
        g.disableScissor();
        if (this.typeLines.size() > visible) this.scrollbar(g, top, visible);
        // the status along the bottom of the box, and a bouncing arrow once the line is done
        int statusY = this.boxY + this.boxH - 13;
        g.fill(this.textX, statusY - 3, this.textX + this.textW, statusY - 2, PAPER_SHADE);
        String status = this.waiting ? "..." : this.data.refused() ? "They won't talk to you now." : this.data.status();
        if (status.isEmpty() && this.typeLines.size() > visible) status = "Scroll for more";
        int statusColor = now - this.statusAt < 40 ? GOLD_BRIGHT : GOLD;
        g.text(this.font, this.font.plainSubstrByWidth(status, this.textW - 12), this.textX, statusY, statusColor, false);
        if (mouseX >= this.textX && mouseX < this.textX + this.textW && mouseY >= statusY - 2 && mouseY < statusY + 9 && this.font.width(status) > this.textW - 12) {
            g.setTooltipForNextFrame(Component.literal(status), mouseX, mouseY);
        }
        if (!this.typing() && !this.waiting) {
            int bounce = (int) (Math.abs(Mth.sin(now * .25F)) * 2);
            int ax = this.boxX + this.boxW - 14, ay = statusY + 1 + bounce;
            for (int i = 0; i < 4; i++) g.fill(ax + i, ay + i, ax + 7 - i, ay + i + 1, EDGE_LIGHT);
        }
        super.extractRenderState(g, mouseX, mouseY, delta);
        this.drawLevelUp(g, now);
    }

    /** The resident, scaled to fit whatever their height (a Magmari is half again as tall as a Weft child). */
    private void drawPortrait(GuiGraphicsExtractor g, LivingEntity resident, int mouseX, int mouseY) {
        float height = Math.max(1.0F, resident.getBbHeight());
        int size = (int) (Math.min(this.portraitW * 1.12, this.portraitH * 0.7) * 1.95F / height);
        // their name floats over their head in the world; not in their own portrait
        Component name = resident.getCustomName();
        resident.setCustomName(null);
        try {
            InventoryScreen.extractEntityInInventoryFollowsMouse(g, this.portraitX, this.portraitY, this.portraitX + this.portraitW,
                    this.portraitY + this.portraitH, size, 0.36F, this.portraitX + this.portraitW / 2F - 10, this.portraitY + this.portraitH / 2F, resident);
        } finally {
            resident.setCustomName(name);
        }
    }

    private void drawCard(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        panel(g, this.cardX, this.cardY, this.cardW, this.cardH);
        String badge = "Lv. " + this.data.level();
        int badgeW = this.font.width(badge) + 10;
        pill(g, this.cardX + 6, this.cardY + 6, badgeW, 12, RIBBONS[this.people.ordinal()]);
        g.text(this.font, badge, this.cardX + 11, this.cardY + 8, 0xFFFFFFFF, true);
        g.text(this.font, this.font.plainSubstrByWidth(this.data.levelName(), this.cardW - badgeW - 18), this.cardX + badgeW + 11, this.cardY + 8, INK, true);
        // five tokens, one per level; the next one fills as you get closer
        int floor = this.data.floor(), next = Math.max(floor + 1, this.data.next());
        float progress = this.data.level() >= 5 ? 0 : Math.clamp((this.data.points() - floor) / (float) (next - floor), 0, 1);
        int hx = this.cardX + 7, hy = this.cardY + 22;
        for (int i = 0; i < 5; i++) meter(g, this.people, hx + i * 11, hy, i < this.data.level() ? 1 : i == this.data.level() ? progress : 0);
        String goal = this.data.level() >= 5 ? "As close as an outsider can be" : (next - this.data.points()) + " more to the next level";
        g.text(this.font, this.font.plainSubstrByWidth(goal, this.cardW - 12), this.cardX + 6, this.cardY + 35, MUTED, false);
        if (mouseX >= this.cardX && mouseX < this.cardX + this.cardW && mouseY >= this.cardY && mouseY < this.cardY + this.cardH - 20) {
            List<Component> lines = new ArrayList<>();
            lines.add(Component.literal("Friendship Lv. " + this.data.level() + ": " + this.data.levelName()));
            lines.add(Component.literal(this.data.points() + " points"));
            lines.add(Component.literal("Talk once a day, hear their stories, answer kindly, bring gifts they like."));
            g.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
    }

    private void drawRibbon(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        int color = RIBBONS[this.people.ordinal()];
        String name = this.font.plainSubstrByWidth(this.data.name(), this.boxW - this.portraitW - 40);
        int w = this.font.width(name) + 14, x = this.textX - 4, y = this.boxY - 12;
        g.fill(x + 1, y + 1, x + w + 1, y + 14, 0x55000000);
        g.fill(x, y, x + w, y + 13, color);
        g.fill(x, y + 11, x + w, y + 13, darker(color));
        g.fill(x - 1, y + 1, x, y + 12, darker(color));
        g.fill(x + w, y + 1, x + w + 1, y + 12, darker(color));
        g.text(this.font, name, x + 7, y + 3, 0xFFFFFFFF, true);
        int room = this.boxX + this.boxW - (x + w + 6) - 6;
        if (room > 40) {
            // on a dark strip of its own, so it reads over whatever is behind it in the world
            String subtitle = this.font.plainSubstrByWidth(this.data.subtitle(), room);
            g.fill(x + w + 2, y + 1, x + w + 10 + this.font.width(subtitle), y + 12, 0xC0260D0C);
            g.text(this.font, subtitle, x + w + 6, y + 3, MUTED, true);
        }
        if (mouseX >= x && mouseX < this.boxX + this.boxW && mouseY >= y && mouseY < y + 13) {
            g.setTooltipForNextFrame(Component.literal(this.data.name() + "\n" + this.data.subtitle()), mouseX, mouseY);
        }
    }

    /** A banner that drops in when your friendship reaches a new level. */
    private void drawLevelUp(GuiGraphicsExtractor g, float now) {
        float age = now - this.levelUpAt;
        if (age < 0 || age > 90) return;
        float drop = age < 8 ? 1 - (float) Math.pow(1 - age / 8, 3) : age > 80 ? 1 - (age - 80) / 10 : 1;
        String title = "Friendship: " + this.data.levelName() + "!";
        String sub = this.data.name() + " thinks better of you.";
        int w = Math.max(this.font.width(title), this.font.width(sub)) + 40, h = 30, x = (this.width - w) / 2, y = (int) (-h + (h + 10) * drop);
        panel(g, x, y, w, h);
        g.centeredText(this.font, title, this.width / 2, y + 6, GOLD);
        g.centeredText(this.font, sub, this.width / 2, y + 17, INK);
        for (int i = 0; i < 6; i++) {
            float a = age * .12F + i * 1.047F;
            int sx = (int) (this.width / 2 + Mth.cos(a) * (w / 2F + 6)), sy = (int) (y + h / 2 + Mth.sin(a) * (h / 2F + 5));
            meter(g, this.people, sx - 3, sy - 3, 1);
        }
    }

    private void scrollbar(GuiGraphicsExtractor g, int top, int visible) {
        int trackX = this.boxX + this.boxW - 6, trackH = visible * LINE;
        g.fill(trackX, top, trackX + 2, top + trackH, PAPER_SHADE);
        int thumb = Math.max(6, trackH * visible / this.typeLines.size());
        int offset = (trackH - thumb) * this.dialogueScroll / Math.max(1, this.typeLines.size() - visible);
        g.fill(trackX, top + offset, trackX + 2, top + offset + thumb, EDGE_LIGHT);
    }

    // -- shared drawing helpers ----------------------------------------------------------------------------------

    /** A netherrack panel: nether-brick edge, the stone itself under a dark wash, a lit bevel along the top. */
    static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x + 1, y + h, x + w - 1, y + h + 2, 0x66000000);
        g.fill(x + 1, y, x + w - 1, y + h, EDGE);
        g.fill(x, y + 1, x + w, y + h - 1, EDGE);
        int ix = x + 2, iy = y + 2, iw = w - 4, ih = h - 4;
        for (int ty = 0; ty < ih; ty += 16) {
            for (int tx = 0; tx < iw; tx += 16) {
                int tw = Math.min(16, iw - tx), th = Math.min(16, ih - ty);
                g.blit(RenderPipelines.GUI_TEXTURED, NETHERRACK, ix + tx, iy + ty, 0, 0, tw, th, tw, th, 16, 16);
            }
        }
        g.fill(ix, iy, ix + iw, iy + ih, WASH);
        g.fill(x + 1, y + 1, x + w - 1, y + 2, EDGE_LIGHT);
        g.fill(x + 2, y + h - 2, x + w - 2, y + h - 1, PAPER_SHADE);
    }

    static void frame(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        g.fill(x + 1, y + h, x + w + 1, y + h + 2, 0x66000000);
        g.fill(x, y, x + w, y + h, EDGE);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, EDGE_LIGHT);
    }

    static void pill(GuiGraphicsExtractor g, int x, int y, int w, int h, int color) {
        g.fill(x + 1, y, x + w - 1, y + h, color);
        g.fill(x, y + 1, x + w, y + h - 1, color);
        g.fill(x + 1, y + h - 2, x + w - 1, y + h - 1, darker(color));
    }

    static int darker(int color) {
        int r = (color >> 16 & 255) * 3 / 4, gr = (color >> 8 & 255) * 3 / 4, b = (color & 255) * 3 / 4;
        return 0xFF000000 | r << 16 | gr << 8 | b;
    }

    /**
     * A 7-pixel friendship token in the people's own currency, filled from the left by {@code fill} (0 to 1):
     * a gold nugget, a soul flame, an ember, a spool of thread, a tear.
     */
    static void meter(GuiGraphicsExtractor g, People people, int x, int y, float fill) {
        int[] masks;
        int main, light, dark;
        switch (people) {
            case PIGLIN -> { masks = new int[]{0b0011000, 0b0111100, 0b1111110, 0b1111111, 0b0111110, 0b0011100}; main = 0xFFF2C14E; light = 0xFFFFF0A0; dark = 0xFFB07D1A; }
            case WITHREN -> { masks = new int[]{0b0001000, 0b0011000, 0b0011100, 0b0111110, 0b0111110, 0b0011100}; main = 0xFF4FD6E8; light = 0xFFC2F8FF; dark = 0xFF1E8C9C; }
            case MAGMARI -> { masks = new int[]{0b0001000, 0b0011100, 0b0111110, 0b1111111, 0b0111110, 0b0011100, 0b0001000}; main = 0xFFFF7A2A; light = 0xFFFFD27A; dark = 0xFFB33A12; }
            case WEFT -> { masks = new int[]{0b1111111, 0b0111110, 0b0111110, 0b0111110, 0b0111110, 0b1111111}; main = 0xFF35C2B0; light = 0xFFFF9A3C; dark = 0xFF6B4A2B; }
            default -> { masks = new int[]{0b0001000, 0b0011100, 0b0011100, 0b0111110, 0b0111110, 0b0011100}; main = 0xFFAEE3FF; light = 0xFFFFFFFF; dark = 0xFF6FA8CC; }
        }
        int cut = Math.round(Math.clamp(fill, 0, 1) * 7);
        for (int row = 0; row < masks.length; row++) {
            for (int col = 0; col < 7; col++) {
                if ((masks[row] & (1 << (6 - col))) == 0) continue;
                int color;
                if (col >= cut) color = METER_EMPTY;
                else if (people == People.WEFT) color = row == 0 || row == masks.length - 1 ? dark : row == 2 ? light : main;
                else color = row <= 1 && col < 4 ? light : row >= masks.length - 2 && col > 3 ? dark : main;
                g.fill(x + col, y + row, x + col + 1, y + row + 1, color);
            }
        }
    }

    /** A tab under the friendship card: gold when it's the one shown. */
    static final class TabButton extends Button {
        private final boolean selected;

        TabButton(int x, int y, int w, int h, String label, boolean selected, OnPress action) {
            super(x, y, w, h, Component.literal(label), action, DEFAULT_NARRATION);
            this.selected = selected;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
            boolean hover = this.active && this.isHoveredOrFocused();
            int x = this.getX(), y = this.getY(), w = this.getWidth(), h = this.getHeight();
            int fill = this.selected ? GOLD : !this.active ? BUBBLE_OFF : hover ? BUBBLE_HOVER : BUBBLE;
            g.fill(x + 1, y, x + w - 1, y + h, EDGE);
            g.fill(x, y + 1, x + w, y + h - 1, EDGE);
            g.fill(x + 1, y + 1, x + w - 1, y + h - 1, fill);
            if (!this.selected) g.fill(x + 1, y + 1, x + w - 1, y + 2, this.active ? EDGE_LIGHT : BUBBLE_OFF);
            var font = Minecraft.getInstance().font;
            int ink = this.selected ? EDGE : this.active ? INK : MUTED;
            String label = font.plainSubstrByWidth(this.getMessage().getString(), w - 4);
            g.text(font, label, x + (w - font.width(label)) / 2, y + (h - 8) / 2 + 1, ink, false);
        }
    }

    /** A numbered reply bubble; a gold dot marks a topic you haven't heard about yet. */
    static final class ReplyButton extends Button {
        private final int number;
        private final boolean fresh;

        ReplyButton(int x, int y, int w, int h, int number, String label, boolean fresh, OnPress action) {
            super(x, y, w, h, Component.literal(label), action, DEFAULT_NARRATION);
            this.number = number;
            this.fresh = fresh;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
            boolean hover = this.active && this.isHoveredOrFocused();
            int x = this.getX() + (hover ? -2 : 0), y = this.getY(), w = this.getWidth(), h = this.getHeight();
            int fill = !this.active ? BUBBLE_OFF : hover ? BUBBLE_HOVER : BUBBLE;
            g.fill(x + 2, y + h, x + w - 2, y + h + 1, 0x55000000);
            g.fill(x + 2, y, x + w - 2, y + h, EDGE);
            g.fill(x + 1, y + 1, x + w - 1, y + h - 1, EDGE);
            g.fill(x + 2, y + 1, x + w - 2, y + h - 1, fill);
            g.fill(x + 1, y + 2, x + w - 1, y + h - 2, fill);
            g.fill(x + 2, y + 1, x + w - 2, y + 2, this.active ? EDGE_LIGHT : BUBBLE_OFF);
            int badge = this.active ? hover ? GOLD_BRIGHT : GOLD : 0xFF7A5A40;
            g.fill(x + 3, y + 2, x + 12, y + h - 2, badge);
            var font = Minecraft.getInstance().font;
            g.centeredText(font, String.valueOf(this.number), x + 8, y + (h - 8) / 2, EDGE);
            String label = font.plainSubstrByWidth(this.getMessage().getString(), w - (this.fresh ? 26 : 20));
            g.text(font, label, x + 16, y + (h - 8) / 2, this.active ? INK : MUTED, false);
            if (this.fresh) g.fill(x + w - 7, y + h / 2 - 1, x + w - 4, y + h / 2 + 2, GOLD);
        }
    }

    /** A dock button with an icon (and its name, on wide screens). Talk gets a little speech bubble. */
    static final class DockButton extends Button {
        private final ItemStack icon;
        private final boolean talk, labeled;

        DockButton(int x, int y, int w, int h, String label, ItemStack icon, boolean talk, boolean labeled, OnPress action) {
            super(x, y, w, h, Component.literal(label), action, DEFAULT_NARRATION);
            this.icon = icon;
            this.talk = talk;
            this.labeled = labeled;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
            boolean hover = this.active && this.isHoveredOrFocused();
            int x = this.getX(), y = this.getY() - (hover ? 1 : 0), w = this.getWidth(), h = this.getHeight();
            int fill = !this.active ? BUBBLE_OFF : hover ? BUBBLE_HOVER : BUBBLE;
            g.fill(x + 2, y + h, x + w - 2, y + h + 2, 0x55000000);
            g.fill(x + 2, y, x + w - 2, y + h, EDGE);
            g.fill(x, y + 2, x + w, y + h - 2, EDGE);
            g.fill(x + 1, y + 1, x + w - 1, y + h - 1, EDGE);
            g.fill(x + 2, y + 1, x + w - 2, y + h - 1, fill);
            g.fill(x + 1, y + 2, x + w - 1, y + h - 2, fill);
            int iconX = this.labeled ? x + 3 : x + (w - 16) / 2, iconY = y + (h - 16) / 2;
            if (this.talk) {
                // a speech bubble
                g.fill(iconX + 2, iconY + 3, iconX + 14, iconY + 11, INK);
                g.fill(iconX + 3, iconY + 2, iconX + 13, iconY + 12, INK);
                g.fill(iconX + 4, iconY + 12, iconX + 6, iconY + 14, INK);
                for (int i = 0; i < 3; i++) g.fill(iconX + 5 + i * 3, iconY + 6, iconX + 7 + i * 3, iconY + 8, BUBBLE);
            } else if (!this.icon.isEmpty()) {
                g.item(this.icon, iconX, iconY);
            }
            var font = Minecraft.getInstance().font;
            if (this.labeled) g.text(font, font.plainSubstrByWidth(this.getMessage().getString(), w - 22), x + 20, y + (h - 8) / 2, this.active ? INK : MUTED, false);
            if (!this.active) g.fill(x + 2, y + 2, x + w - 2, y + h - 2, 0x66260D0C);
        }
    }
}
