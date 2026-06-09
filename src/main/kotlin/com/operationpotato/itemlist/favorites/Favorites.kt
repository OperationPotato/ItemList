package com.operationpotato.itemlist.favorites

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.operationpotato.itemlist.utils.codecs.RecipeCodecs
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

data class Favorites(
	var favoriteItems: List<SkyBlockId> = emptyList(),
	var favoriteRecipes: List<Recipe<*>> = emptyList(),
	var favoriteMobs: List<String> = emptyList()
) {
	companion object {
		val CODEC: Codec<Favorites> = RecordCodecBuilder.create { instance ->
			instance.group(
				SkyBlockId.CODEC.listOf().optionalFieldOf("favoriteItems", emptyList()).forGetter(Favorites::favoriteItems),
				RecipeCodecs.RECIPE.listOf().optionalFieldOf("favoriteRecipes", emptyList()).forGetter(Favorites::favoriteRecipes),
				Codec.STRING.listOf().optionalFieldOf("favoriteMobs", emptyList()).forGetter(Favorites::favoriteMobs),
			).apply(instance, ::Favorites)
		}
	}
}
