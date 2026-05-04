package com.kotori316.auto_planter.planter;

import com.kotori316.auto_planter.AutoPlanterCommon;
import com.kotori316.auto_planter.packet.OnReceiveWithLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlanterMessage implements CustomPacketPayload, OnReceiveWithLevel {
    public static final Identifier NAME = Identifier.fromNamespaceAndPath(AutoPlanterCommon.AUTO_PLANTER, "planter_message");
    private static final Logger LOGGER = LoggerFactory.getLogger(PlanterMessage.class);
    public static final CustomPacketPayload.Type<PlanterMessage> TYPE = new Type<>(NAME);
    public static final StreamCodec<RegistryFriendlyByteBuf, PlanterMessage> STREAM_CODEC = CustomPacketPayload.codec(
        PlanterMessage::write, PlanterMessage::new
    );

    private final BlockPos pos;
    private final ResourceKey<Level> dim;
    private final CompoundTag tag;

    public PlanterMessage(BlockPos pos, ResourceKey<Level> dim, CompoundTag tag) {
        this.pos = pos;
        this.dim = dim;
        this.tag = tag;
    }

    private PlanterMessage(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.dim = buf.readResourceKey(Registries.DIMENSION);
        this.tag = buf.readNbt();
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceKey(dim);
        buf.writeNbt(tag);
    }

    @Override
    public void onReceive(Level level, Player player) {
        if (!level.dimension().equals(dim)) {
            return;
        }
        var entity = level.getBlockEntity(pos);
        if (entity instanceof PlanterTile planterTile) {
            try (var reporter = new ProblemReporter.ScopedCollector(entity.problemPath(), LOGGER)) {
                var input = TagValueInput.create(reporter, level.registryAccess(), tag);
                planterTile.loadAdditional(input);
            }
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
