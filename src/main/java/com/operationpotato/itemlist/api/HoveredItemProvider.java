package com.operationpotato.itemlist.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public interface HoveredItemProvider {
	@Nullable ItemStack provide(Screen screen);
}
