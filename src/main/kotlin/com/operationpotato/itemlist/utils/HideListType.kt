package com.operationpotato.itemlist.utils

enum class HideListType(val formattedName: String) {
	SEARCH("Not Searching"),
	FILTER("Not Filtering"),
	NEVER("Never"),
	;

	override fun toString() = formattedName
}
