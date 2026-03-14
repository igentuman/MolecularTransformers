package igentuman.mt;

import com.mojang.logging.LogUtils;
import igentuman.mt.menu.MolecularTransformerMenu;
import igentuman.mt.screen.MolecularTransformerScreen;
import igentuman.mt.setup.ModEntry;
import igentuman.mt.setup.ModRegistration;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.client.gui.screens.MenuScreens;
import org.slf4j.Logger;

import static igentuman.mt.setup.ModRegistration.ENTRIES;

@Mod(Main.MODID)
public class Main
{
    public static final String MODID = "molecular_transformer";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Main(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);
        ModRegistration.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            for(ModEntry entry: ENTRIES.values()) {
                if(entry.hasBlock()) {
                    event.accept(entry.item());
                }
            }
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            for(ModEntry entry: ENTRIES.values()) {
                if(!entry.hasBlock() && entry.hasItem()) {
                    event.accept(entry.item());
                }
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            event.enqueueWork(() -> {
                for(ModEntry entry: ENTRIES.values()) {
                    if(entry.hasMenu()) {
                        @SuppressWarnings("unchecked")
                        MenuType<MolecularTransformerMenu> entryMenu = (MenuType<MolecularTransformerMenu>) (Object) entry.menu().get();
                        String identifier = entry.name();
                        MenuScreens.<MolecularTransformerMenu, MolecularTransformerScreen>register(entryMenu,
                                (menu, inventory, component) ->
                                        new MolecularTransformerScreen(menu, inventory, component, identifier));
                    }
                }
            });
        }
    }
}
