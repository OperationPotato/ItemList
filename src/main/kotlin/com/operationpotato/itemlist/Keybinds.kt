package com.operationpotato.itemlist

import com.moulberry.lattice.annotation.LatticeOption
import com.moulberry.lattice.annotation.widget.LatticeWidgetKeybind
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo.getMobId
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

	@LatticeOption(title = "Hide Overlay", description = "Hides the Item List and Favorites List.\nRequires holding Ctrl/Cmd!")
	@LatticeWidgetKeybind
	val hideOverlay: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.hideOverlay",
			GLFW.GLFW_KEY_O,
			category
		)
	)

	@LatticeOption(title = "View Recipe", description = "Shows the recipe of the hovered item, if there are any.")
	@LatticeWidgetKeybind
	val viewRecipe: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.viewRecipe",
			GLFW.GLFW_KEY_R,
			category,
		)
	)


	@LatticeOption(title = "View Usage", description = "Shows the uses of the hovered item, if there are any.")
	@LatticeWidgetKeybind
	val viewUsage: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.viewUsage",
			GLFW.GLFW_KEY_U,
			category,
		)
	)

	@LatticeOption(title = "Return to Previous Recipe", description = "While in a recipe screen, pressing this allows you to go back to the previous recipe screen.")
	@LatticeWidgetKeybind
	val previousRecipe: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.reopenPreviousRecipe",
			GLFW.GLFW_KEY_BACKSPACE,
			category,
		)
	)

	@LatticeOption(title = "Favorite Item", description = "Adds the hovered item or recipe to your Favorites List.")
	@LatticeWidgetKeybind
	val favoriteItem: KeyMapping = registerKeyMapping(
		KeyMapping(
			"key.skyblock-item-list.favoriteItem",
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
		} else if (favoriteItem.matches(keyEvent)) {
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

	fun init() {}
}
