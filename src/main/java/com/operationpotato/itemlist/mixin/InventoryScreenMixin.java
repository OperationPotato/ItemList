package com.operationpotato.itemlist.mixin;

import com.operationpotato.itemlist.SkyBlockItemList;
import com.operationpotato.itemlist.gui.FavoritePanel;
import com.operationpotato.itemlist.gui.ItemPanel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {
	@Inject(method = "onRecipeBookButtonClick", at = @At("HEAD"))
	public void skyblockItemList$onButtonClick(CallbackInfo ci) {
		ItemPanel itemPanel = SkyBlockItemList.INSTANCE.getInstance();
		if (itemPanel != null) itemPanel.updateWidth();
		FavoritePanel favoritesPanel = SkyBlockItemList.INSTANCE.getFavoriteInstance();
		if (favoritesPanel != null) favoritesPanel.updateWidth();
	}
}
