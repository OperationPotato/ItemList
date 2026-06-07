package com.operationpotato.itemlist.api;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface RecipeButtonProvider {
	Optional<AbstractWidget> provide(ItemStack recipeOutput);
}
