package com.operationpotato.itemlist

import com.operationpotato.itemlist.utils.SearchUtils
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.item.replaceVisually
import tech.thatgravyboat.skyblockapi.helpers.McScreen

object ContainerSearcher {

	init {
		SkyBlockAPI.eventBus.register<InventoryChangeEvent> {
			if (shouldSearch()) {
				// Instead of rechecking every item, just recheck the updated item
				setItemSearch(SkyBlockItemList.instance?.searchBox?.value, it.item)
			}
		}
	}

	fun shouldSearch(): Boolean = SkyBlockItemList.instance?.searchBox?.isSearchingInventory == true

	fun setSearch(search: String?) {
		McScreen.asMenu?.menu?.items?.forEach { item -> setItemSearch(search, item) }
	}

	private fun setItemSearch(search: String?, item: ItemStack) {
		val searches = search?.lowercase()?.let { SearchUtils.transformSearch(it) }
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
