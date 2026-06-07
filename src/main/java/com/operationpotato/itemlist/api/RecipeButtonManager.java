package com.operationpotato.itemlist.api;

import org.jetbrains.annotations.ApiStatus;

public interface RecipeButtonManager {
	@ApiStatus.Experimental
	void addProvider(RecipeButtonProvider provider);

	void addConsumer(RecipeButtonConsumer consumer);
}
