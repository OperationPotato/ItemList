package com.operationpotato.itemlist.api;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Optional;

public interface MultipleRecipeButtonProvider extends RecipeButtonProvider {

	@Override
	default Optional<AbstractWidget> provide(Object recipe, ItemStack recipeOutput) {
		return provideMultiple(recipe, recipeOutput).stream().findFirst();
	}

	@Override
	Collection<AbstractWidget> provideMultiple(Object recipe, ItemStack recipeOutput);
}
