package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.favorite.FavouritesManager
import com.operationpotato.itemlist.utils.RepoLibUtils.result
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import com.operationpotato.itemlist.utils.Utils.toLazy

class FavouriteListWidget(width: Int, height: Int) : AbstractItemList(width, height) {

	override fun getItems(): List<StackDisplay> {
		val displays = mutableListOf<StackDisplay>()

		FavouritesManager.favourites.favouriteItems.forEach { id ->
			val stack = id.toItem()
			if (!stack.isEmpty) {
				displays.add(StackDisplay(stack.toLazy(), SkyBlockItemCategory.ALL))
			}
		}

		FavouritesManager.favourites.favouriteRecipes.forEach { recipe ->
			val result = recipe.result()?.toItem()
			if (result != null && !result.isEmpty) {
				displays.add(StackDisplay(result.toLazy(), SkyBlockItemCategory.ALL))
			}
		}

		return displays
	}

	override fun getAllItems(): List<StackDisplay> = getItems()
}
