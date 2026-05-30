package com.operationpotato.itemlist

import com.operationpotato.itemlist.utils.SkyBlockItemCategory

object Settings {
	var enabled: Boolean = false
	var scale: Float = 1.0f
	var nonPixelatedItemScale = true // Currently a bit laggy when actively scaling, else runs fine
	var lastSearch: String = ""
	var lastFilter: SkyBlockItemCategory = SkyBlockItemCategory.ALL
}
