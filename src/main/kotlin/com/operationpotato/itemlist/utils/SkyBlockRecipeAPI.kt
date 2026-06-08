package com.operationpotato.itemlist.utils

import com.operationpotato.itemlist.utils.RepoLibUtils.toSkyBlockId
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

object SkyBlockRecipeAPI {

	private val recipesByOutput: Map<SkyBlockId, List<Recipe<*>>> by lazy {
		val typesToSearch = listOf(
			Recipe.Type.CRAFTING,
			Recipe.Type.FORGE,
			Recipe.Type.KAT,
			Recipe.Type.SHOP
		)

		val grouped = mutableMapOf<SkyBlockId, MutableList<Recipe<*>>>()

		typesToSearch.flatMap { type ->
			RepoAPI.recipes().getRecipes(type)
		}.forEach { recipe ->
			val outputId = getRecipeOutputId(recipe)
			if (outputId != null) {
				grouped.getOrPut(outputId) { mutableListOf() }.add(recipe)
			}
		}

		grouped
	}

	fun getRecipesForId(id: SkyBlockId): List<Recipe<*>> {
		return recipesByOutput[id] ?: emptyList()
	}

	private fun getRecipeOutputId(recipe: Recipe<*>): SkyBlockId? {
		return when (recipe) {
			is CraftingRecipe -> recipe.result()
			is ForgeRecipe -> recipe.result()
			is ShopRecipe -> recipe.result()
			is KatRecipe -> recipe.output()
			else -> null
		}?.toSkyBlockId()
	}
}
