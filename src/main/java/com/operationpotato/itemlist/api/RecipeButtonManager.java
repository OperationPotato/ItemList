package com.operationpotato.itemlist.api;

import org.jetbrains.annotations.ApiStatus;

public interface RecipeButtonManager {
	@ApiStatus.Experimental
	void addProvider(RecipeButtonProvider provider);

	@ApiStatus.Experimental
	default void addMultiProvider(MultipleRecipeButtonProvider provider) {
		addProvider(provider);
	}

	void addConsumer(RecipeButtonConsumer consumer);
}
