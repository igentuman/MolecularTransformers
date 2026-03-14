package igentuman.mt.screen;

import igentuman.mt.Main;
import igentuman.mt.menu.MolecularTransformerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MolecularTransformerScreen extends AbstractContainerScreen<MolecularTransformerMenu> {
    private final ResourceLocation TEXTURE;
    public String identifier;

    public MolecularTransformerScreen(MolecularTransformerMenu pMenu, Inventory pPlayerInventory, Component pComponent, String identifier) {
        super(pMenu, pPlayerInventory, pComponent);
        this.identifier = identifier;
        TEXTURE =  ResourceLocation.fromNamespaceAndPath(Main.MODID, "textures/gui/"+identifier+".png");
        this.imageWidth = 250;
        this.imageHeight = 250;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(TEXTURE, (this.width - 206)/2, (this.height - 196)/2, 0, 0, 205, 196, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBg(guiGraphics, pPartialTick, pMouseX, pMouseY);
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
    }
}