package igentuman.mt.menu;

import igentuman.mt.setup.ModRegistration;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MolecularTransformerMenu extends AbstractContainerMenu {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    public static final int INVENTORY_START = 2;
    public static final int INVENTORY_END = 38;
    public static final int HOTBAR_START = 38;
    public static final int HOTBAR_END = 47;

    private final ItemStackHandler itemHandler;

    public MolecularTransformerMenu(int containerId, Inventory playerInventory, ItemStackHandler itemHandler, String identifier) {
        super(ModRegistration.getEntry(identifier).menu().get(), containerId);
        this.itemHandler = itemHandler;

        // Input slot
        this.addSlot(new SlotItemHandler(itemHandler, SLOT_INPUT, 58, 65));

        // Output slot
        this.addSlot(new SlotItemHandler(itemHandler, SLOT_OUTPUT, 171, 64));

        // Player inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 30 + j * 21, 115 + i * 21));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 30 + i * 21, 180));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            if (pIndex == SLOT_OUTPUT) {
                if (!this.moveItemStackTo(slotStack, INVENTORY_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, itemstack);
            } else if (pIndex != SLOT_INPUT) {
                if (pIndex >= INVENTORY_START && pIndex < HOTBAR_START) {
                    if (!this.moveItemStackTo(slotStack, HOTBAR_START, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= HOTBAR_START && pIndex < HOTBAR_END) {
                    if (!this.moveItemStackTo(slotStack, INVENTORY_START, HOTBAR_START, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(slotStack, INVENTORY_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}