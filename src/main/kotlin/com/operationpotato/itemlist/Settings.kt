package com.operationpotato.itemlist

import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.SkyBlockItemCategory

object Settings {
	// General
	var enabled: Boolean = false
	var nonPixelatedItemScale = true // Currently a bit laggy when actively scaling, else runs fine

	// Main List
	var itemSize: Int = StackDisplay.STACK_SIZE
	var lastSearch: String = ""
	var lastFilter: SkyBlockItemCategory = SkyBlockItemCategory.ALL

	// Favorites
	var enableFavorites: Boolean = true
	var favoritesItemSize: Int = StackDisplay.STACK_SIZE
}
