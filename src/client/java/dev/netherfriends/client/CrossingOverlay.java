package dev.netherfriends.client;

import java.util.List;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

/**
 * The ferry's crossing: the screen sinks into a deep ember dark while a few lines of the sea come up one after
 * another, then lifts again on the far side. Drawn over the HUD; it lasts as long as the server says the crossing
 * does ({@code cross} to {@code clear}).
 */
public final class CrossingOverlay {
    private CrossingOverlay() {}

    private static final long FADE_IN = 900, FADE_OUT = 1400, FIRST_LINE = 1100, LINE_GAP = 1500, LINE_FADE = 600;
    private static List<String> lines = List.of();
    private static long started = -1, cleared = -1;

    public static void start(List<String> crossing) {
        lines = crossing;
        started = Util.getMillis();
        cleared = -1;
    }

    public static void clear() {
        if (started >= 0 && cleared < 0) cleared = Util.getMillis();
    }

    /** For tests: how dark it is now, 0 to 1. */
    public static float darkness() {
        if (started < 0) return 0;
        long now = Util.getMillis();
        float in = Mth.clamp((now - started) / (float) FADE_IN, 0, 1);
        float out = cleared < 0 ? 1 : 1 - Mth.clamp((now - cleared) / (float) FADE_OUT, 0, 1);
        return Math.min(in, out);
    }

    public static void extract(GuiGraphicsExtractor g, DeltaTracker delta) {
        if (started < 0) return;
        float dark = darkness();
        if (cleared >= 0 && dark <= 0) {
            started = -1;
            return;
        }
        int w = g.guiWidth(), h = g.guiHeight();
        int a = (int) (dark * 255) << 24;
        g.fill(0, 0, w, h, a | 0x140605);
        // a low glow of lava along the bottom, as if the sea were just out of sight
        g.fillGradient(0, h * 2 / 3, w, h, 0x005A1A08, (int) (dark * 150) << 24 | 0x8A2E0C);
        Font font = Minecraft.getInstance().font;
        long since = Util.getMillis() - started;
        int y = h / 2 - lines.size() * 14;
        for (int i = 0; i < lines.size(); i++) {
            long shownFor = since - FIRST_LINE - i * LINE_GAP;
            if (shownFor < 0) break;
            float alpha = Mth.clamp(shownFor / (float) LINE_FADE, 0, 1) * dark;
            if (alpha < 0.05F) continue;
            int color = ((int) (alpha * 255) << 24) | (i == lines.size() - 1 ? 0xF2C14E : 0xFBEEE2);
            for (FormattedCharSequence part : font.split(Component.literal(lines.get(i)), Math.min(320, w - 40))) {
                g.centeredText(font, part, w / 2, y, color);
                y += 10;
            }
            y += 8;
        }
    }
}
