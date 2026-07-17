package com.operationpotato.itemlist.api;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.world.item.ItemStack;

public interface HoveredItemInputConsumer {
	boolean consume(Screen screen, ItemStack stack, InputWithModifiers event);
}
