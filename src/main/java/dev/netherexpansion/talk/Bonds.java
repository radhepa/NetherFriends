package dev.netherexpansion.talk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.netherexpansion.NetherExpansion;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.world.entity.LivingEntity;

/**
 * Friendship, kept on the resident, one bond per player. Six levels (0 to 5); each people names its own in its
 * dialogue file. {@code best} is the highest level ever reached, so a level's reward is only ever given once.
 */
public final class Bonds {
    private Bonds() {}

    /** The points at which each level starts. */
    public static final int[] THRESHOLDS = {0, 3, 8, 15, 25, 40};
    public static final int MAX_LEVEL = THRESHOLDS.length - 1;

    public record Bond(int points, int best, long talkDay, long giftDay, List<String> heard) {
        public static final Bond NEW = new Bond(0, 0, -1, -1, List.of());
        public static final Codec<Bond> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.optionalFieldOf("points", 0).forGetter(Bond::points),
                Codec.INT.optionalFieldOf("best", 0).forGetter(Bond::best),
                Codec.LONG.optionalFieldOf("talk_day", -1L).forGetter(Bond::talkDay),
                Codec.LONG.optionalFieldOf("gift_day", -1L).forGetter(Bond::giftDay),
                Codec.STRING.listOf().optionalFieldOf("heard", List.of()).forGetter(Bond::heard)
        ).apply(i, Bond::new));

        public int level() {
            return levelFor(this.points);
        }

        public Bond add(int delta) {
            return new Bond(Math.max(0, this.points + delta), this.best, this.talkDay, this.giftDay, this.heard);
        }

        public Bond talked(long day) {
            return new Bond(this.points, this.best, day, this.giftDay, this.heard);
        }

        public Bond gifted(long day) {
            return new Bond(this.points, this.best, this.talkDay, day, this.heard);
        }

        public Bond hear(String topic) {
            if (this.heard.contains(topic)) return this;
            List<String> more = new java.util.ArrayList<>(this.heard);
            more.add(topic);
            return new Bond(this.points, this.best, this.talkDay, this.giftDay, List.copyOf(more));
        }

        public Bond reached(int level) {
            return new Bond(this.points, Math.max(this.best, level), this.talkDay, this.giftDay, this.heard);
        }
    }

    public static final AttachmentType<Map<String, Bond>> BONDS = AttachmentRegistry.create(NetherExpansion.id("bonds"),
            b -> b.initializer(Map::<String, Bond>of).persistent(Codec.unboundedMap(Codec.STRING, Bond.CODEC)));

    public static int levelFor(int points) {
        int level = 0;
        while (level < MAX_LEVEL && points >= THRESHOLDS[level + 1]) level++;
        return level;
    }

    public static Bond get(LivingEntity resident, UUID player) {
        return resident.getAttachedOrCreate(BONDS).getOrDefault(player.toString(), Bond.NEW);
    }

    public static void put(LivingEntity resident, UUID player, Bond bond) {
        Map<String, Bond> all = new HashMap<>(resident.getAttachedOrCreate(BONDS));
        all.put(player.toString(), bond);
        resident.setAttached(BONDS, Map.copyOf(all));
    }
}
