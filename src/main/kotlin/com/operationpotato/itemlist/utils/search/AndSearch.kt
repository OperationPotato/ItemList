package com.operationpotato.itemlist.utils.search

data class AndSearch(val conditions: List<Search>) : Search {
	override fun matches(stackName: String, loreLines: List<String>): Boolean {
		return conditions.all { condition -> condition.matches(stackName, loreLines) }
	}
}
