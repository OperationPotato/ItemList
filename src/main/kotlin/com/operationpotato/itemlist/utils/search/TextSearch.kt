package com.operationpotato.itemlist.utils.search

data class TextSearch(val text: String) : Search {
	override fun contains(otherSearch: Search): Boolean {
		if (otherSearch !is TextSearch) return false
		return text.startsWith(otherSearch.text)
	}

	override fun matches(input: String): Boolean {
		return input.contains(text)
	}
}
