package dev.netherfriends.talk;

import java.util.ArrayList;
import java.util.List;

import dev.netherfriends.NetherFriends;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Server to client, for the ferry: {@code booked} (close the conversation window: the barge is at the dock),
 * {@code cross} (the screen darkens for the crossing, showing {@code lines} one after another) and {@code clear}
 * (the crossing is over: the screen lightens on the far side).
 */
public record FerryPayload(String phase, List<String> lines) implements CustomPacketPayload {
    public static final String BOOKED = "booked", CROSS = "cross", CLEAR = "clear";

    public static final Type<FerryPayload> TYPE = new Type<>(NetherFriends.id("ferry"));
    public static final StreamCodec<FriendlyByteBuf, FerryPayload> CODEC = StreamCodec.of(
            (buf, v) -> {
                buf.writeUtf(v.phase, 16);
                buf.writeVarInt(v.lines.size());
                for (String line : v.lines) buf.writeUtf(line, 512);
            },
            buf -> {
                String phase = buf.readUtf(16);
                int n = buf.readVarInt();
                if (n < 0 || n > 8) throw new IllegalArgumentException("Invalid ferry line count " + n);
                List<String> lines = new ArrayList<>();
                for (int i = 0; i < n; i++) lines.add(buf.readUtf(512));
                return new FerryPayload(phase, List.copyOf(lines));
            });

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
