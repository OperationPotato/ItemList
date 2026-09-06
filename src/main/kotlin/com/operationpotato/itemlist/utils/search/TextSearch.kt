package com.operationpotato.itemlist.utils.search

data class TextSearch(val text: String, val fuzzy: Boolean = false) : Search {
	override fun matches(input: String): Boolean {
		if (fuzzy) return FuzzySearch.matches(text, input)
		return input.contains(text)
	}

	override fun contains(otherSearch: Search): Boolean {
		if (otherSearch !is TextSearch) return false
		if (fuzzy != otherSearch.fuzzy) return false
		return otherSearch.text.startsWith(text)
	}
}
