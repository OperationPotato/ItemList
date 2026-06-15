package com.operationpotato.itemlist.gui.favorites

import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.AbstractItemList
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.RepoLibUtils.result
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo
import com.operationpotato.itemlist.utils.Utils.toLazy
import net.minecraft.client.input.KeyEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import kotlin.jvm.optionals.getOrNull

class FavoritesListWidget(width: Int, height: Int) : AbstractItemList(width, height) {
	var children: List<StackDisplay> = emptyList()
	override var alwaysShowPageText = false

	init {
		updateChildren()
		this.positioningCallback = {
			if (shouldShowPageText() != addedPageTextPadding) {
				layout.y += (PADDING + McFont.height) / 2 * (if (addedPageTextPadding) -1 else 1)
			}
		}
	}

	fun updateChildren() {
		if (!ConfigManager.get().favoritesList.enableFavorites) {
			children = emptyList()
			return
		}

		val displays = mutableListOf<StackDisplay>()

		FavoritesManager.favorites.favoriteItems.forEach { id ->
			val stack = id.toItem()
			if (!stack.isEmpty) {
				displays.add(StackDisplay(stack.toLazy(), SkyBlockItemCategory.ALL))
			}
		}

		FavoritesManager.favorites.favoriteRecipes.forEach { recipe ->
			val result = recipe.result()?.toItem()
			if (result != null && !result.isEmpty) {
				displays.add(RecipeStackDisplay(recipe, result.toLazy(), SkyBlockItemCategory.ALL))
			}
		}

		FavoritesManager.favorites.favoriteMobs.forEach {
			val stack = SkyBlockMobsRepo.getItemStack(it)
			if (stack != null && !stack.isEmpty) {
				displays.add(StackDisplay(stack.toLazy(), SkyBlockItemCategory.ALL))
			}
		}

		children = displays
		scaleChildren()
		this.visible = children.isNotEmpty()
	}

	override fun keyPressed(event: KeyEvent): Boolean {
		val mousePos = McClient.mouse
		val child = getChildAt(mousePos.first, mousePos.second).getOrNull()
		if (child is RecipeStackDisplay && Keybinds.favoriteItem.matches(event)) {
			FavoritesManager.removeFavoriteRecipe(child.recipe)
			return true
		}
		return super.keyPressed(event)
	}

	override fun getItems(): List<StackDisplay> = children
	override fun getAllItems(): List<StackDisplay> = getItems()
}
