package com.operationpotato.itemlist

import com.operationpotato.itemlist.favorite.FavouritesManager
import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping
import net.minecraft.client.KeyMapping
import net.minecraft.client.input.KeyEvent
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import org.lwjgl.glfw.GLFW
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McScreen

object Keybinds {
	val category: KeyMapping.Category = KeyMapping.Category.register(
		Identifier.fromNamespaceAndPath("skyblock-item-list", "main")
	)

	val hideOverlay: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.hideOverlay",
			GLFW.GLFW_KEY_O,
			category
		)
	)

	val viewRecipe: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.viewRecipe",
			GLFW.GLFW_KEY_R,
			category,
		)
	)

	val viewUsage: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.viewUsage",
			GLFW.GLFW_KEY_U,
			category,
		)
	)

	val previousRecipe: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.reopenPreviousRecipe",
			GLFW.GLFW_KEY_BACKSPACE,
			category,
		)
	)

	val favouriteItem: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.favouriteItem",
			GLFW.GLFW_KEY_F,
			category,
		)
	)

	fun handleKeybind(itemStack: ItemStack, keyEvent: KeyEvent): Boolean {
		if (viewRecipe.matches(keyEvent)) {
			RecipeScreen.openRecipeForItem(itemStack, McScreen.self)
			return true
		} else if (viewUsage.matches(keyEvent)) {
			RecipeScreen.openUsageForItem(itemStack, McScreen.self)
			return true
		} else if (favouriteItem.matches(keyEvent)) {
		val targetId = itemStack.getSkyBlockId() ?: return true
		if (FavouritesManager.isFavouriteItem(targetId)) {
			FavouritesManager.removeFavouriteItem(targetId)
		} else {
			FavouritesManager.addFavouriteItem(targetId)
		}
		SkyBlockItemList.favouriteInstance?.listWidget?.updatePositionsAsync()
		return true
	}
		return false
	}

	fun init() {}
}
