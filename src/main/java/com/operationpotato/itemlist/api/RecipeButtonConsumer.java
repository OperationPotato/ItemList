package com.operationpotato.itemlist.api;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.MouseButtonEvent;

public interface RecipeButtonConsumer {
	void consume(AbstractWidget widget, MouseButtonEvent event);
}
