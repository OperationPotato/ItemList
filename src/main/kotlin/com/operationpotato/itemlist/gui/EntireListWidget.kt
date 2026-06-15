package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.utils.SearchUtils
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import com.operationpotato.itemlist.utils.SkyBlockItems
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy

class EntireListWidget(width: Int, height: Int) : AbstractItemList(width, height) {
	var visibleChildren: List<StackDisplay> = listOf()
	var currentFilter: SkyBlockItemCategory = SkyBlockItemCategory.ALL
	var currentSearch: String = ""

	fun filterChildren(category: SkyBlockItemCategory) {
		currentFilter = category
		visibleChildren = when (currentFilter) {
			SkyBlockItemCategory.ALL -> children
			SkyBlockItemCategory.CUSTOM -> children.filter { it.type in ConfigManager.get().mainList.customFilters }
			else -> children.filter { it.type == currentFilter }
		}
		if (ConfigManager.get().mainList.hideVanillaItems) {
			visibleChildren = visibleChildren.filterNot { it.isVanilla }
		}
	}

	fun searchChildren(search: String) {
		val lower = search.lowercase()
		if (search.isEmpty() || SearchUtils.isDistinctSearch(currentSearch, lower) || visibleChildren.isEmpty()) {
			filterChildren(currentFilter)
		}
		currentSearch = lower
		if (lower.isEmpty()) return
		val searchFilters = SearchUtils.transformSearch(lower)
		visibleChildren = visibleChildren.filter { it.matchesSearch(searchFilters) }
	}

	override fun getItems(): List<StackDisplay> = visibleChildren
	override fun getAllItems(): List<StackDisplay> = children

	companion object {
		private val children: List<StackDisplay> by registryBoundLazy { getItems() }

		fun getItems(): List<StackDisplay> {
			val displays: MutableList<StackDisplay> = mutableListOf()

			SkyBlockItems.items.forEach { item ->
				val display = StackDisplay(item.stack, item.category, item.isVanilla)
				displays.add(display)
			}

			return displays
		}
	}
}
