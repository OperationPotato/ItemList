package com.operationpotato.itemlist.utils.search

data class OrSearch(val conditions: List<Search>) : Search {
	override fun matches(stackName: String, loreLines: List<String>): Boolean {
		return conditions.any { condition -> condition.matches(stackName, loreLines) }
	}
}
