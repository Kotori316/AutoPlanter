package com.kotori316.auto_planter.planter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public abstract class PlanterTile extends BlockEntity implements MenuProvider {
    protected final SimpleContainer container;
    private final PlanterBlock.PlanterBlockType blockType;

    protected PlanterTile(BlockPos pos, BlockState state, PlanterBlock.PlanterBlockType blockType) {
        super(blockType.entityType.get(), pos, state);
        this.blockType = blockType;
        this.container = new PlanterInventory(blockType.storageSize, this::onInventoryOpen, this::onInventoryClose, this::onInventoryUpdate);
    }

    public void plantSapling() {
        if (level != null && !level.isClientSide()) {
            BlockPos upPos = getBlockPos().above();
            BlockState state = level.getBlockState(upPos);
            if (level.getFluidState(upPos).isEmpty()) { // Water removes sapling immediately.
                for (ItemStack maybeSapling : this.container.getItems()) {
                    if (isPlantable(maybeSapling, getBlockState().getValue(PlanterBlock.TRIGGERED))) {
                        DirectionalPlaceContext context = new DirectionalPlaceContext(level, upPos, Direction.DOWN, maybeSapling, Direction.UP);
                        if (state.canBeReplaced(context)) {
                            ((BlockItem) maybeSapling.getItem()).place(context);
                            setChanged();
                        }
                    }
                }
            }
        }
    }

    @NotNull
    public final PlanterBlock.PlanterBlockType blockType() {
        return this.blockType;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, this.container.getItems());
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.container.getItems());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        // Invalidate handler
    }

    public static boolean isPlantable(ItemStack stack, boolean triggered) {
        if (stack.isEmpty()) return false;
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            if (stack.is(ItemTags.SAPLINGS)) {
                return true;
            }
            if (triggered) {
                // Seed and crops
                return ((BlockItem) item).getBlock() instanceof CropBlock;
            }
        }
        return false;
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public abstract PlanterContainer<?> createMenu(int id, Inventory inv, Player p);

    protected void onInventoryOpen(ServerPlayer player) {
    }

    protected void onInventoryClose() {
        plantSapling();
    }

    protected void onInventoryUpdate() {
        this.setChanged();
    }

    private static class PlanterInventory extends SimpleContainer {
        private final Consumer<ServerPlayer> onOpen;
        private final Runnable onClose;
        private final Runnable onUpdate;

        private PlanterInventory(int size, Consumer<ServerPlayer> onOpen, Runnable onClose, Runnable onUpdate) {
            super(size);
            this.onOpen = onOpen;
            this.onClose = onClose;
            this.onUpdate = onUpdate;
        }

        @Override
        public void startOpen(ContainerUser containerUser) {
            super.startOpen(containerUser);
            if (containerUser instanceof ServerPlayer player) {
                onOpen.accept(player);
            }
        }

        @Override
        public void stopOpen(ContainerUser containerUser) {
            super.stopOpen(containerUser);
            onClose.run();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return isPlantable(itemStack, true);
        }

        @Override
        public void setChanged() {
            super.setChanged();
            onUpdate.run();
        }
    }
}
