package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.utils.SearchUtils
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import com.operationpotato.itemlist.utils.SkyBlockItems
import tech.thatgravyboat.repolib.api.RepoAPI
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
		visibleChildren.filterIsInstance<CollapsibleStackDisplay>().forEach {
			it.filteredFamilyItems = it.familyItems
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
		private val groupedChildren: List<StackDisplay> by registryBoundLazy { generateItems(true) }
		private val normalChildren: List<StackDisplay> by registryBoundLazy { generateItems(false) }

		val children: List<StackDisplay>
			get() = if (ConfigManager.get().mainList.groupFamilies) groupedChildren else normalChildren

		private fun generateItems(groupFamilies: Boolean): List<StackDisplay> {
			val displays: MutableList<StackDisplay> = mutableListOf()

			if (RepoAPI.isInitialized() && groupFamilies) {
				val itemsById = SkyBlockItems.items.associateBy { it.repoId.uppercase() }
				val processed = mutableSetOf<String>()

				SkyBlockItems.items.forEach { item ->
					val upperId = item.repoId.uppercase()
					if (processed.contains(upperId)) return@forEach

					val family = RepoAPI.parents().getFamily(item.repoId)
					val familyIds = listOf(family.mainParent) + family.allChildren
					val familyItems = familyIds.distinct().mapNotNull { itemsById[it.uppercase()] }

					if (familyItems.size > 1) {
						val mainItem = familyItems.firstOrNull { it.repoId.equals(family.mainParent, ignoreCase = true) }
							?: familyItems.first()
						displays.add(CollapsibleStackDisplay(familyItems, mainItem))
						processed.addAll(familyItems.map { it.repoId.uppercase() })
						return@forEach
					}

					displays.add(StackDisplay(item.stack, item.category, item.isVanilla))
					processed.add(upperId)
				}
			} else {
				SkyBlockItems.items.forEach { item ->
					val display = StackDisplay(item.stack, item.category, item.isVanilla)
					displays.add(display)
				}
			}

			return displays
		}
	}
}
