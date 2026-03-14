package igentuman.mt.setup;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

public record ModEntry(
        String name,
        RegistryObject<Block> block,
        RegistryObject<Item> item,
        RegistryObject<BlockEntityType<BlockEntity>> blockEntity,
        RegistryObject<MenuType<AbstractContainerMenu>> menu
) {

    public boolean hasBlock() {
        return block != null;
    }

    public boolean hasItem() {
        return item != null;
    }

    public boolean hasMenu() {
        return menu != null;
    }
}
