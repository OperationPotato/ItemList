package com.operationpotato.itemlist.utils.search

data class TermSearch(val term: String) : Search {
	override fun matches(stackName: String, loreLines: List<String>): Boolean {
		return stackName.contains(term) || loreLines.any { line -> line.contains(term) }
	}
}
