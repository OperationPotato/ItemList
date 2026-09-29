package com.operationpotato.itemlist.utils

enum class ItemListSide(val formattedName: String) {
	LEFT("Left"),
	RIGHT("Right"),
	;

	override fun toString() = formattedName
}
