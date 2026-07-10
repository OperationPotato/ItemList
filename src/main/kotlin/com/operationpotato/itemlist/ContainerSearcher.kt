package com.operationpotato.itemlist

import com.operationpotato.itemlist.utils.SearchUtils
import tech.thatgravyboat.skyblockapi.api.item.replaceVisually
import tech.thatgravyboat.skyblockapi.helpers.McScreen

object ContainerSearcher {

	fun shouldSearch(): Boolean = SkyBlockItemList.instance?.searchBox?.isSearchingInventory == true

	fun setSearch(search: String?) {
		val searches = search?.lowercase()?.let { SearchUtils.transformSearch(it) }

		McScreen.asMenu?.menu?.items?.forEach { item ->
			val color = when {
				searches == null -> 0
				SearchUtils.matchesSearch(item, searches) -> 0
				else -> 0xAA555555.toInt()
			}
			item.replaceVisually {
				copyFrom(item)
				foregroundColor = color
			}
		}
	}

}
