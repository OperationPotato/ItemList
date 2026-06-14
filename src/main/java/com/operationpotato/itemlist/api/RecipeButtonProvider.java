package com.operationpotato.itemlist.api;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public interface RecipeButtonProvider {
	/**
	 * Works best with <a href="https://github.com/SkyblockAPI/Repo-Lib">Repo-Lib</a> implemented.
	 *
	 * @param recipe The recipe object is Recipe<*> from Repo-Lib, you can cast it to that to access the contents.
	 */
	Optional<AbstractWidget> provide(Object recipe, ItemStack recipeOutput);

	/**
	 * Override this to provide multiple widgets.
	 * Defaults to wrapping the single widget from {@link #provide}.
	 */
	default Collection<AbstractWidget> provideMultiple(Object recipe, ItemStack recipeOutput) {
		Optional<AbstractWidget> optional = provide(recipe, recipeOutput);
		return optional.<Collection<AbstractWidget>>map(Collections::singletonList).orElse(Collections.emptyList());
	}
}
