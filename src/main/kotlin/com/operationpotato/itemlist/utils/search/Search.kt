package com.operationpotato.itemlist.utils.search

interface Search {
	fun matches(input: String): Boolean
	fun contains(otherSearch: Search): Boolean
}

