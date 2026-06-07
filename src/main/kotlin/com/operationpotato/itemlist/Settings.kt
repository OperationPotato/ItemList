package com.operationpotato.itemlist

import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.SkyBlockItemCategory

object Settings {
	var enabled: Boolean = false
	var itemSize: Int = StackDisplay.STACK_SIZE
	var nonPixelatedItemScale = true // Currently a bit laggy when actively scaling, else runs fine
	var lastSearch: String = ""
	var lastFilter: SkyBlockItemCategory = SkyBlockItemCategory.ALL
	var outsideSkyBlock: Boolean = true // Set to false when actual config, I like test in singleplayer
}
