package com.operationpotato.itemlist.utils

import com.operationpotato.itemlist.utils.RepoLibUtils.toSkyBlockId
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy

object SkyBlockRecipeAPI {

	val validRecipes = listOf(
		Recipe.Type.CRAFTING,
		Recipe.Type.FORGE,
		Recipe.Type.KAT,
		Recipe.Type.SHOP
	)

	private val recipesByOutput: Map<SkyBlockId, Set<Recipe<*>>> by registryBoundLazy {
		val grouped = mutableMapOf<SkyBlockId, MutableSet<Recipe<*>>>()

		validRecipes.flatMap { type ->
			RepoAPI.recipes().getRecipes(type)
		}.forEach { recipe ->
			val outputId = getRecipeOutputId(recipe)
			if (outputId != null) {
				grouped.getOrPut(outputId) { mutableSetOf() }.add(recipe)
			}
		}

		grouped
	}

	private val recipesByInput: Map<SkyBlockId, Set<Recipe<*>>> by registryBoundLazy {
		val grouped = mutableMapOf<SkyBlockId, MutableSet<Recipe<*>>>()

		validRecipes.flatMap { type ->
			RepoAPI.recipes().getRecipes(type)
		}.forEach { recipe ->
			val inputIds = getRecipeInputIds(recipe)
			inputIds?.forEach {
				grouped.getOrPut(it) { mutableSetOf() }.add(recipe)
			}
		}

		grouped
	}

	private val shopRecipesByNpc: Map<String, Set<ShopRecipe>> by registryBoundLazy {
		RepoAPI.recipes().getRecipes(Recipe.Type.SHOP)
			.filterIsInstance<ShopRecipe>()
			.groupBy { it.npc }
			.mapValues { it.value.toSet() }
	}

	fun getRecipesForId(id: SkyBlockId): Set<Recipe<*>> {
		return recipesByOutput[id] ?: emptySet()
	}

	fun getUsagesForId(id: SkyBlockId): Set<Recipe<*>> {
		return recipesByInput[id] ?: emptySet()
	}

	fun getShopRecipesForNpc(npc: String): Set<ShopRecipe> {
		return shopRecipesByNpc[npc] ?: emptySet()
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

	private fun getRecipeInputIds(recipe: Recipe<*>): List<SkyBlockId>? {
		val items = when (recipe) {
			is CraftingRecipe -> recipe.inputs
			is ForgeRecipe -> recipe.inputs
			is ShopRecipe -> recipe.inputs
			is KatRecipe -> {
				val list = mutableListOf(recipe.input)
				list.addAll(recipe.items)
				list
			}

			else -> null
		}
		return items?.mapNotNull { it.toSkyBlockId() }
	}
}
