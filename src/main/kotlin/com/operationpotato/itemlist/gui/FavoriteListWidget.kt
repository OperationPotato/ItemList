package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.favorite.FavoritesManager
import com.operationpotato.itemlist.utils.RepoLibUtils.result
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import com.operationpotato.itemlist.utils.Utils.toLazy

class FavoriteListWidget(width: Int, height: Int) : AbstractItemList(width, height) {

	override fun getItems(): List<StackDisplay> {
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
				displays.add(StackDisplay(result.toLazy(), SkyBlockItemCategory.ALL))
			}
		}

		return displays
	}

	override fun getAllItems(): List<StackDisplay> = getItems()
}
