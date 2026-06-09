package com.operationpotato.itemlist.favorite

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.operationpotato.itemlist.utils.codecs.RecipeCodecs
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

data class Favourites(
	var favouriteItems: List<SkyBlockId> = emptyList(),
	var favouriteRecipes: List<Recipe<*>> = emptyList(),
) {
	companion object {
		val CODEC: Codec<Favourites> = RecordCodecBuilder.create { instance ->
			instance.group(
				SkyBlockId.CODEC.listOf().optionalFieldOf("favouriteItems", emptyList()).forGetter(Favourites::favouriteItems),
				RecipeCodecs.RECIPE.listOf().optionalFieldOf("favouriteRecipes", emptyList()).forGetter(Favourites::favouriteRecipes),
			).apply(instance, ::Favourites)
		}
	}
}
