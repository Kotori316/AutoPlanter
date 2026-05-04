package com.kotori316.auto_planter.forge;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.packet.OnReceiveWithLevel;
import com.kotori316.auto_planter.planter.PlanterMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PacketHandler {
    private static final int PROTOCOL = 1;
    private static final SimpleChannel CHANNEL =
        ChannelBuilder.named(Identifier.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, "main"))
            .networkProtocolVersion(PROTOCOL)
            .acceptedVersions(Channel.VersionTest.exact(PROTOCOL))
            .simpleChannel()
            .play()
            .bidirectional()
            // PlanterMessage
            .addMain(PlanterMessage.class, PlanterMessage.STREAM_CODEC, PacketHandler::onReceive)
            // END
            .build();

    public static void init() {
    }

    public static void sendToClientPlayer(@NotNull CustomPacketPayload message, @NotNull ServerPlayer player) {
        if (player.level().getServer() instanceof GameTestServer) {
            // sending message to test server will cause NPE
            return;
        }
        CHANNEL.send(message, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToClientNear(@NotNull CustomPacketPayload message, @NotNull BlockPos pos, @NotNull ResourceKey<Level> dim, double range) {
        CHANNEL.send(message, PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(
            pos.getX(), pos.getY(), pos.getZ(), range, dim
        )));
    }

    private static void onReceive(OnReceiveWithLevel message, CustomPayloadEvent.Context context) {
        var player = context.isServerSide() ? getServerPlayer(context) : getClientPlayer(context);
        if (player == null) return;
        message.onReceive(player.level(), player);
    }

    @Nullable
    private static Player getServerPlayer(CustomPayloadEvent.Context context) {
        return context.getSender();
    }

    @Nullable
    private static Player getClientPlayer(CustomPayloadEvent.Context context) {
        var player = context.getSender();
        if (player != null) return player;
        return Minecraft.getInstance().player;
    }
}
