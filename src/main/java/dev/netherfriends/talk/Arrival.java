package dev.netherfriends.talk;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dev.netherfriends.NetherFriends;
import dev.netherfriends.entity.People;
import dev.netherfriends.piglin.GoldRule;
import dev.netherfriends.world.BuildingPiece;
import dev.netherfriends.world.Names;
import dev.netherfriends.world.NetherVillage;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/**
 * Walking into a village shows its name, with a word on how its people expect you to behave. Shown once per
 * visit: it shows again only after you've been well away.
 */
public final class Arrival {
    private Arrival() {}

    static final TagKey<Structure> VILLAGES = TagKey.create(Registries.STRUCTURE, NetherFriends.id("village"));
    static final int EVERY = 40;
    static final double AWAY_SQR = 112 * 112;
    private static final Map<UUID, BlockPos> SHOWN = new HashMap<>();

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(Arrival::tick);
    }

    private static void tick(MinecraftServer server) {
        if (server.getTickCount() % EVERY != 0) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerLevel level = player.level();
            BlockPos seen = SHOWN.get(player.getUUID());
            if (seen != null && seen.distToCenterSqr(player.position()) > AWAY_SQR) SHOWN.remove(player.getUUID());
            HolderSet<Structure> villages = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).get(VILLAGES).orElse(null);
            if (villages == null) return;
            StructureStart start = level.structureManager().getStructureAt(player.blockPosition(), villages);
            if (!start.isValid() || !(start.getStructure() instanceof NetherVillage village)) continue;
            BlockPos centre = centre(start);
            if (centre == null || centre.equals(SHOWN.get(player.getUUID()))) continue;
            SHOWN.put(player.getUUID(), centre);
            People people = village.people();
            player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 20));
            player.connection.send(new ClientboundSetTitleTextPacket(Component.literal(Names.village(people, centre)).withStyle(colour(people))));
            player.connection.send(new ClientboundSetSubtitleTextPacket(Component.literal(subtitle(people, player))));
        }
    }

    private static BlockPos centre(StructureStart start) {
        for (StructurePiece piece : start.getPieces()) if (piece instanceof BuildingPiece building) return building.village();
        return null;
    }

    private static ChatFormatting colour(People people) {
        return switch (people) {
            case PIGLIN -> ChatFormatting.GOLD;
            case WITHREN -> ChatFormatting.AQUA;
            case MAGMARI -> ChatFormatting.RED;
            case WEFT -> ChatFormatting.DARK_AQUA;
            case GHASTKIN -> ChatFormatting.WHITE;
        };
    }

    /** Who lives here, and the one rule an outsider should know. */
    private static String subtitle(People people, ServerPlayer player) {
        return switch (people) {
            case PIGLIN -> PiglinAi.isWearingSafeArmor(player) || GoldRule.wearsGoldTrim(player)
                    ? "A Piglin Hold. They see your gold." : "A Piglin Hold. Show gold, or be hunted.";
            case WITHREN -> "A Withren Vigil. The dead are kept here.";
            case MAGMARI -> "A Magmari Mooring. Guests are welcome.";
            case WEFT -> "A Weft Loom. Leave the forest standing.";
            case GHASTKIN -> "A Ghastkin Eave. Come quietly.";
        };
    }
}
