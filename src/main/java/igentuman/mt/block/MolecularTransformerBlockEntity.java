package igentuman.mt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MolecularTransformerBlockEntity extends BlockEntity {
    private final EnergyStorage energyStorage;
    private final ItemStackHandler itemHandler;
    private final LazyOptional<EnergyStorage> energyCapability;
    private final LazyOptional<InvWrapper> itemCapability;
    private final int outputCount; // 1 for mk1, 2 for mk2

    public MolecularTransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState, int maxEnergy, int outputCount) {
        super(type, pos, blockState);
        this.outputCount = outputCount;
        
        // Create energy storage - only receives energy, cannot extract
        this.energyStorage = new EnergyStorage(maxEnergy, 100, 0, 0) {
            @Override
            public boolean canExtract() {
                return false;
            }
        };
        
        // Create inventory: 1 input slot, outputCount output slots
        this.itemHandler = new ItemStackHandler(1 + outputCount);
        
        this.energyCapability = LazyOptional.of(() -> this.energyStorage);
        this.itemCapability = LazyOptional.of(() -> new InvWrapper(this.itemHandler));
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyCapability.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemCapability.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.energyCapability.invalidate();
        this.itemCapability.invalidate();
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.contains("Energy", Tag.TAG_INT)) {
            this.energyStorage.deserializeNBT(tag.getCompound("Energy"));
        }
        if (tag.contains("Items", Tag.TAG_COMPOUND)) {
            this.itemHandler.deserializeNBT(tag.getCompound("Items"));
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", this.energyStorage.serializeNBT());
        tag.put("Items", this.itemHandler.serializeNBT());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        this.load(pkt.getTag());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public void tick() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        // Pull from top
        pullFromTop();
        
        // Push to bottom
        pushToBottom();
        
        // TODO: Implement recipe processing logic
    }

    private void pullFromTop() {
        if (this.itemHandler.getStackInSlot(0).getCount() >= this.itemHandler.getSlotLimit(0)) {
            return; // Input slot is full
        }

        BlockPos abovePos = this.worldPosition.above();
        BlockEntity aboveEntity = this.level.getBlockEntity(abovePos);

        if (aboveEntity == null) {
            return;
        }

        LazyOptional<net.minecraftforge.items.IItemHandler> aboveHandler = aboveEntity.getCapability(
                ForgeCapabilities.ITEM_HANDLER, Direction.DOWN);

        aboveHandler.ifPresent(handler -> {
            // Try to extract from the block above
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack extracted = handler.extractItem(i, 1, false);
                if (!extracted.isEmpty()) {
                    ItemStack remaining = this.itemHandler.insertItem(0, extracted, false);
                    if (!remaining.isEmpty()) {
                        // Couldn't fit all items, put it back
                        handler.insertItem(i, remaining, false);
                    }
                    return;
                }
            }
        });
    }

    private void pushToBottom() {
        // Find first non-empty output slot
        int firstOutputSlot = 1;
        ItemStack itemToPush = ItemStack.EMPTY;
        
        for (int i = firstOutputSlot; i < this.itemHandler.getSlots(); i++) {
            ItemStack stack = this.itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                itemToPush = stack.copy();
                itemToPush.setCount(1);
                break;
            }
        }

        if (itemToPush.isEmpty()) {
            return; // No items to push
        }

        BlockPos belowPos = this.worldPosition.below();
        BlockEntity belowEntity = this.level.getBlockEntity(belowPos);

        if (belowEntity == null) {
            return;
        }

        LazyOptional<net.minecraftforge.items.IItemHandler> belowHandler = belowEntity.getCapability(
                ForgeCapabilities.ITEM_HANDLER, Direction.UP);

        belowHandler.ifPresent(handler -> {
            // Try to insert into the block below
            ItemStack remaining = ItemHandlerHelper.insertItem(handler, itemToPush, false);
            if (remaining.isEmpty()) {
                // Successfully pushed, remove from our inventory
                for (int i = firstOutputSlot; i < this.itemHandler.getSlots(); i++) {
                    ItemStack stack = this.itemHandler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        stack.shrink(1);
                        return;
                    }
                }
            }
        });
    }
}