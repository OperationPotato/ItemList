package com.operationpotato.itemlist

import tech.thatgravyboat.skyblockapi.api.item.replaceVisually
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName

object ContainerSearcher {

	fun shouldSearch(): Boolean = SkyBlockItemList.instance?.searchBox?.isSearchingInventory == true

	fun setSearch(search: String?) {
		McScreen.asMenu?.menu?.items?.forEach { item ->
			val color = when {
				search == null -> 0
				item.cleanName.contains(search, ignoreCase = true) -> 0
				else -> 0xAA555555.toInt()
			}
			item.replaceVisually {
				copyFrom(item)
				foregroundColor = color
			}
		}
	}

}
