package com.operationpotato.itemlist

import com.mojang.blaze3d.platform.InputConstants
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.AbstractPagedListScreen
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo.getMobId
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping
import net.minecraft.client.KeyMapping
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McScreen

object Keybinds {
	val category: KeyMapping.Category = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath("skyblock-item-list", "main")
	)

	val hideOverlay: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.hideOverlay",
			InputConstants.KEY_O,
			category
		)
	)

	val viewRecipe: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.viewRecipe",
			InputConstants.KEY_R,
			category,
		)
	)


	val viewUsage: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.viewUsage",
			InputConstants.KEY_U,
			category,
		)
	)

	val previousRecipe: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.reopenPreviousRecipe",
			InputConstants.KEY_BACKSPACE,
			category,
		)
	)

	val favoriteItem: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.favoriteItem",
			InputConstants.KEY_F,
			category,
		)
	)

	fun handleKeybind(itemStack: ItemStack, input: InputWithModifiers): Boolean {
		if (viewRecipe.matches(input)) {
			AbstractPagedListScreen.openRecipeForItem(itemStack, McScreen.self)
			return true
		} else if (viewUsage.matches(input)) {
			AbstractPagedListScreen.openUsageForItem(itemStack, McScreen.self)
			return true
		} else if (favoriteItem.matches(input)) {
			itemStack.getSkyBlockId()?.let {
				if (FavoritesManager.isFavoriteItem(it)) {
					FavoritesManager.removeFavoriteItem(it)
				} else {
					FavoritesManager.addFavoriteItem(it)
				}
				return true
			} ?: itemStack.getMobId()?.let {
				if (FavoritesManager.isFavoriteMob(it)) {
					FavoritesManager.removeFavoriteMob(it)
				} else {
					FavoritesManager.addFavoriteMob(it)
				}
				return true
			}
			return false
		}
		return false
	}

	fun KeyMapping.matches(input: InputWithModifiers): Boolean {
		return when (input) {
			is KeyEvent -> this.matches(input)
			is MouseButtonEvent -> this.matchesMouse(input)
			else -> false
		}
	}

	@Suppress("EmptyMethod")
	fun init() {}
}
