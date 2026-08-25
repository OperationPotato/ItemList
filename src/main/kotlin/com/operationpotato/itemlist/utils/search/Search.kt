package com.operationpotato.itemlist.utils.search

interface Search {
	fun matches(stackName: String, loreLines: List<String>): Boolean
}
