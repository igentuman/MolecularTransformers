package igentuman.mt.setup;

import igentuman.mt.Main;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.function.BiFunction;

public class ModRegistration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Main.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Main.MODID);

    public static final HashMap<String, ModEntry> ENTRIES = new HashMap<>();

    /**
     * Create a new registration builder
     */
    public static RegistrationBuilder builder(String name) {
        return new RegistrationBuilder(name);
    }

    /**
     * Get a registered entry by name
     */
    public static ModEntry getEntry(String name) {
        return ENTRIES.get(name);
    }

    /**
     * Get all registered entries
     */
    public static HashMap<String, ModEntry> getEntries() {
        return new HashMap<>(ENTRIES);
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        MENUS.register(modEventBus);
        ModEntries.init();
    }

    public static class RegistrationBuilder {
        private final String name;
        private Supplier<? extends Block> blockSupplier;
        private Supplier<? extends Item> itemSupplier;
        private java.util.function.Function<Block, Supplier<? extends BlockEntityType<?>>> entitySupplierFactory;
        private MenuType<?> menuType;

        private RegistrationBuilder(String name) {
            this.name = name;
        }

        /**
         * Register a block
         */
        public <B extends Block> RegistrationBuilder block(Supplier<B> blockSupplier) {
            this.blockSupplier = blockSupplier;
            return this;
        }

        /**
         * Register an item (by default, a BlockItem is created from the block)
         */
        public RegistrationBuilder item(Supplier<? extends Item> itemSupplier) {
            this.itemSupplier = itemSupplier;
            return this;
        }

        /**
         * Register a block entity
         */
        public <E extends BlockEntity> RegistrationBuilder blockEntity(BiFunction<net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState, E> entityConstructor) {
            this.entitySupplierFactory = block -> () -> BlockEntityType.Builder.of(
                    (pos, state) -> entityConstructor.apply(pos, state),
                    block
            ).build(null);
            return this;
        }

        /**
         * Register a menu type
         */
        public RegistrationBuilder menu(MenuType<?> menuType) {
            this.menuType = menuType;
            return this;
        }

        /**
         * Build and register all configured components
         */
        public ModEntry build() {
            RegistryObject<Block> block = null;
            RegistryObject<Item> item = null;
            RegistryObject<BlockEntityType<BlockEntity>> entity = null;
            RegistryObject<MenuType<AbstractContainerMenu>> menu = null;

            // Register block if supplied
            if (blockSupplier != null) {
                block = BLOCKS.register(name, blockSupplier);
                final RegistryObject<Block> finalBlock = block;

                // Register item - use provided supplier or create BlockItem
                if (itemSupplier != null) {
                    item = ITEMS.register(name, itemSupplier);
                } else {
                    item = ITEMS.register(name, () -> new BlockItem(finalBlock.get(), new Item.Properties()));
                }
            }

            // Register block entity if supplied
            if (entitySupplierFactory != null && block != null) {
                final RegistryObject<Block> finalBlock = block;
                @SuppressWarnings("unchecked")
                RegistryObject<BlockEntityType<BlockEntity>> entityCast =
                        (RegistryObject<BlockEntityType<BlockEntity>>) (RegistryObject<?>) 
                        BLOCK_ENTITIES.register(name, () -> entitySupplierFactory.apply(finalBlock.get()).get());
                entity = entityCast;
            }

            // Register menu if supplied
            if (menuType != null) {
                @SuppressWarnings("unchecked")
                RegistryObject<MenuType<AbstractContainerMenu>> menuCast =
                        (RegistryObject<MenuType<AbstractContainerMenu>>) (RegistryObject<?>) 
                        MENUS.register(name, () -> menuType);
                menu = menuCast;
            }

            ModEntry entry = new ModEntry(name, block, item, entity, menu);
            ENTRIES.put(name, entry);
            return entry;
        }
    }
}
