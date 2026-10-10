package dev.netherexpansion.talk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.netherexpansion.NetherExpansion;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Server to client: one conversation window's worth of state. {@code people} is the {@link
 * dev.netherexpansion.entity.People} ordinal (it picks the window's meter icon and voice); {@code level} is the 0-5
 * friendship level, {@code floor} and {@code next} the points where it and the next level start.
 * {@code refused} means they won't talk: the window offers only Goodbye.
 */
public record TalkPayload(int entityId, UUID residentId, int people, String name, String subtitle, String levelName, int level,
                          int points, int floor, int next, String line, String status, List<Choice> choices, boolean opening,
                          boolean levelUp, boolean canGift, boolean canBarter, boolean refused) implements CustomPacketPayload {
    public static final int MAX_CHOICES = 8;

    /** Something the player can say. {@code fresh} marks a topic they haven't heard about yet. */
    public record Choice(String id, String label, boolean fresh) {}

    public static final Type<TalkPayload> TYPE = new Type<>(NetherExpansion.id("talk"));
    public static final StreamCodec<FriendlyByteBuf, TalkPayload> CODEC = StreamCodec.of(
            (buf, v) -> {
                buf.writeVarInt(v.entityId);
                buf.writeUUID(v.residentId);
                buf.writeVarInt(v.people);
                buf.writeUtf(v.name, 128);
                buf.writeUtf(v.subtitle, 256);
                buf.writeUtf(v.levelName, 64);
                buf.writeVarInt(v.level);
                buf.writeVarInt(v.points);
                buf.writeVarInt(v.floor);
                buf.writeVarInt(v.next);
                buf.writeUtf(v.line, 2048);
                buf.writeUtf(v.status, 256);
                buf.writeVarInt(v.choices.size());
                for (Choice c : v.choices) {
                    buf.writeUtf(c.id, 64);
                    buf.writeUtf(c.label, 64);
                    buf.writeBoolean(c.fresh);
                }
                buf.writeBoolean(v.opening);
                buf.writeBoolean(v.levelUp);
                buf.writeBoolean(v.canGift);
                buf.writeBoolean(v.canBarter);
                buf.writeBoolean(v.refused);
            },
            buf -> {
                int entity = buf.readVarInt();
                UUID id = buf.readUUID();
                int people = buf.readVarInt();
                String name = buf.readUtf(128), subtitle = buf.readUtf(256), levelName = buf.readUtf(64);
                int level = buf.readVarInt(), points = buf.readVarInt(), floor = buf.readVarInt(), next = buf.readVarInt();
                String line = buf.readUtf(2048), status = buf.readUtf(256);
                int count = buf.readVarInt();
                if (count < 0 || count > MAX_CHOICES) throw new IllegalArgumentException("Invalid choice count " + count);
                List<Choice> choices = new ArrayList<>();
                for (int i = 0; i < count; i++) choices.add(new Choice(buf.readUtf(64), buf.readUtf(64), buf.readBoolean()));
                return new TalkPayload(entity, id, people, name, subtitle, levelName, level, points, floor, next, line, status,
                        List.copyOf(choices), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    /** Client to server: what the player pressed. "topic:&lt;id&gt;", "reply:&lt;n&gt;", "chatter", "gift", "barter" or "bye". */
    public record Action(int entityId, UUID residentId, String action) implements CustomPacketPayload {
        public static final Type<Action> TYPE = new Type<>(NetherExpansion.id("talk_action"));
        public static final StreamCodec<FriendlyByteBuf, Action> CODEC = StreamCodec.of(
                (buf, v) -> {
                    buf.writeVarInt(v.entityId);
                    buf.writeUUID(v.residentId);
                    buf.writeUtf(v.action, 80);
                },
                buf -> new Action(buf.readVarInt(), buf.readUUID(), buf.readUtf(80)));

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
