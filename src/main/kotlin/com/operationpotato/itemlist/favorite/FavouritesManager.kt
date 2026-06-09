package com.operationpotato.itemlist.favorite

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import com.operationpotato.itemlist.SkyBlockItemList
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.nio.file.Files

object FavouritesManager {
	private val file = McClient.config.resolve("skyblock-item-list", "favourites.json")

	var favourites = Favourites(emptyList(), emptyList())

	fun load() {
		if (!Files.exists(file)) return
		try {
			val json = JsonParser.parseString(Files.readString(file))
			Favourites.CODEC.parse(JsonOps.INSTANCE, json).result().ifPresent {
				favourites = it
			}
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("Failed to load favourites.", e)
		}
	}

	fun save() {
		try {
			Files.createDirectories(file.parent)
			Favourites.CODEC.encodeStart(JsonOps.INSTANCE, favourites).result().ifPresent {
				Files.writeString(file, it.toString())
			}
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("Failed to save favourites.", e)
		}
	}

	fun addFavouriteItem(id: SkyBlockId) {
		if (!favourites.favouriteItems.contains(id)) {
			favourites = favourites.copy(favouriteItems = favourites.favouriteItems + id)
			save()
		}
	}

	fun removeFavouriteItem(id: SkyBlockId) {
		favourites = favourites.copy(favouriteItems = favourites.favouriteItems.filter { it != id })
		save()
	}

	fun isFavouriteItem(id: SkyBlockId): Boolean = favourites.favouriteItems.contains(id)

	fun addFavouriteRecipe(recipe: Recipe<*>) {
		if (!favourites.favouriteRecipes.contains(recipe)) {
			favourites = favourites.copy(favouriteRecipes = favourites.favouriteRecipes + recipe)
			save()
		}
	}

	fun removeFavouriteRecipe(recipe: Recipe<*>) {
		favourites = favourites.copy(favouriteRecipes = favourites.favouriteRecipes.filter { it != recipe })
		save()
	}

	fun isFavouriteRecipe(recipe: Recipe<*>): Boolean = favourites.favouriteRecipes.contains(recipe)
}
