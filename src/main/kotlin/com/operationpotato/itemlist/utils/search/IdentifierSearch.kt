package com.operationpotato.itemlist.utils.search

class IdentifierSearch(itemId: String) : Search {
	val id = "id:${itemId.uppercase()}"

	override fun matches(input: String): Boolean {
		return input.startsWith(id)
	}

	override fun contains(otherSearch: Search): Boolean {
		if (otherSearch !is IdentifierSearch) return false
		return otherSearch.id.startsWith(id)
	}
}
