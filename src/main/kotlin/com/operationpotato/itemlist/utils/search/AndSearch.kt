package com.operationpotato.itemlist.utils.search

data class AndSearch(val conditions: List<Search>) : Search {
	override fun matches(input: String): Boolean {
		return conditions.all { it.matches(input) }
	}

	override fun contains(otherSearch: Search): Boolean {
		if (otherSearch !is AndSearch) return false
		if (conditions.size != otherSearch.conditions.size) return false
		conditions.forEachIndexed { index, condition ->
			val otherCondition = otherSearch.conditions[index]
			val bl = condition.contains(otherCondition)
			if (!bl) return false
		}
		return true
	}
}
