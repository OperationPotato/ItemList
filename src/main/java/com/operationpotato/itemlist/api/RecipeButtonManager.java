package com.operationpotato.itemlist.api;

public interface RecipeButtonManager {
	void addProvider(RecipeButtonProvider provider);

	default void addMultiProvider(MultipleRecipeButtonProvider provider) {
		addProvider(provider);
	}

	void addConsumer(RecipeButtonConsumer consumer);
}
