package igentuman.mt.setup;

import igentuman.mt.block.MolecularTransformerBlock;
import igentuman.mt.block.MolecularTransformerBlockEntity;
import igentuman.mt.menu.MolecularTransformerMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.ItemStackHandler;

public class ModEntries {

    public static void init() {

    }

    public static final ModEntry TRANSFORMER_MK1 = ModRegistration.builder("molecular_transformer_mk1")
            .block(() -> new MolecularTransformerBlock("molecular_transformer_mk1"))
            .blockEntity((pos, state) -> new MolecularTransformerBlockEntity(pos, state, "molecular_transformer_mk1"))
            .menu(new MenuType<>((containerId, playerInventory) ->
                    new MolecularTransformerMenu(containerId, playerInventory, new ItemStackHandler(2), "molecular_transformer_mk1"),
                    FeatureFlags.VANILLA_SET))
            .build();

    public static final ModEntry TRANSFORMER_MK2 = ModRegistration.builder("molecular_transformer_mk2")
            .block(() -> new MolecularTransformerBlock("molecular_transformer_mk2"))
            .blockEntity((pos, state) -> new MolecularTransformerBlockEntity(pos, state, "molecular_transformer_mk2"))
            .menu(new MenuType<>((containerId, playerInventory) ->
                    new MolecularTransformerMenu(containerId, playerInventory, new ItemStackHandler(3), "molecular_transformer_mk2"),
                    FeatureFlags.VANILLA_SET))
            .build();
}
