package com.operationpotato.itemlist.utils.search

data class TextSearch(val text: String, val fuzzy: Boolean = false) : Search {
	private val words = text.split(' ').filter { it.isNotEmpty() }

	override fun matches(input: String): Boolean = matches(text, input)

	/**
	 * Matches when every word of the query appears somewhere in [input] in any order,
	 * so "ferocity sword" finds swords with lore including "ferocity".
	 */
	fun matchesAllWords(input: String): Boolean {
		if (words.size < 2) return false
		return words.all { matches(it, input) }
	}

	private fun matches(query: String, input: String): Boolean {
		if (fuzzy) return FuzzySearch.matches(query, input)
		return input.contains(query)
	}

	override fun contains(otherSearch: Search): Boolean {
		if (otherSearch !is TextSearch) return false
		if (fuzzy != otherSearch.fuzzy) return false
		return otherSearch.text.startsWith(text)
	}
}
