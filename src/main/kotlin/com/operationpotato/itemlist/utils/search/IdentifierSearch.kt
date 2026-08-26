package com.operationpotato.itemlist.utils.search

class IdentifierSearch(val itemId: String) : Search {
	override fun matches(input: String): Boolean {
		return input.startsWith(itemId, ignoreCase = true)
	}

	override fun contains(otherSearch: Search): Boolean {
		if (otherSearch !is IdentifierSearch) return false
		return otherSearch.itemId.startsWith(itemId, ignoreCase = true)
	}
}
