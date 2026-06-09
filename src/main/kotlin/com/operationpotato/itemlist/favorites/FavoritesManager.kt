package com.operationpotato.itemlist.favorites

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import com.operationpotato.itemlist.SkyBlockItemList
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.nio.file.Files

object FavoritesManager {
	private val file = McClient.config.resolve("skyblock-item-list", "favorites.json")

	var favorites = Favorites(emptyList(), emptyList())

	fun load() {
		if (!Files.exists(file)) return
		try {
			val json = JsonParser.parseString(Files.readString(file))
			Favorites.CODEC.parse(JsonOps.INSTANCE, json).result().ifPresent {
				favorites = it
			}
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("Failed to load favorites.", e)
		}
	}

	fun save() {
		updateFavorites()
		try {
			Files.createDirectories(file.parent)
			Favorites.CODEC.encodeStart(JsonOps.INSTANCE, favorites).result().ifPresent {
				Files.writeString(file, it.toString())
			}
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("Failed to save favorites.", e)
		}
	}

	fun updateFavorites() {
		SkyBlockItemList.favoriteInstance?.listWidget?.updateChildren()
		SkyBlockItemList.favoriteInstance?.listWidget?.updatePositionsAsync()
	}

	fun addFavoriteItem(id: SkyBlockId) {
		if (!favorites.favoriteItems.contains(id)) {
			favorites = favorites.copy(favoriteItems = favorites.favoriteItems + id)
			save()
		}
	}

	fun removeFavoriteItem(id: SkyBlockId) {
		favorites = favorites.copy(favoriteItems = favorites.favoriteItems.filter { it != id })
		save()
	}

	fun isFavoriteItem(id: SkyBlockId): Boolean = favorites.favoriteItems.contains(id)

	fun addFavoriteRecipe(recipe: Recipe<*>) {
		if (!favorites.favoriteRecipes.contains(recipe)) {
			favorites = favorites.copy(favoriteRecipes = favorites.favoriteRecipes + recipe)
			save()
		}
	}

	fun removeFavoriteRecipe(recipe: Recipe<*>) {
		favorites = favorites.copy(favoriteRecipes = favorites.favoriteRecipes.filter { it != recipe })
		save()
	}

	fun isFavoriteRecipe(recipe: Recipe<*>): Boolean = favorites.favoriteRecipes.contains(recipe)

	fun addFavoriteMob(mob: String) {
		if (!favorites.favoriteMobs.contains(mob)) {
			favorites = favorites.copy(favoriteMobs = favorites.favoriteMobs + mob)
			save()
		}
	}

	fun removeFavoriteMob(mob: String) {
		favorites = favorites.copy(favoriteMobs = favorites.favoriteMobs.filter { it != mob })
		save()
	}

	fun isFavoriteMob(mob: String): Boolean = favorites.favoriteMobs.contains(mob)
}
