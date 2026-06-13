package com.operationpotato.itemlist.utils

import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
import net.minecraft.util.FormattedCharSequence

// TODO: Better search filtering
object SearchUtils {
	fun transformSearch(raw: String): List<String> {
		return raw.split("|").map { it.trim() }
	}

	fun isDistinctSearch(a: String, b: String): Boolean {
		val aFilter = transformSearch(a)
		val bFilter = transformSearch(b)
		if (bFilter.size > aFilter.size) return true
		aFilter.forEachIndexed { index, aSearch ->
			val bSearch = bFilter[index]
			if (!bSearch.startsWith(aSearch)) return true
		}
		return false
	}

	fun highlightSearch(text: String, offset: Int): FormattedCharSequence {
		return { visitor ->
			var color = false
			for (i in text.indices) {
				val codePoint = text.codePointAt(i)
				var style = Style.EMPTY
				when(codePoint) {
					'@'.code -> if (i == 0) color = true
					' '.code -> color = false
					'|'.code -> style = style.withColor(CommonColors.SOFT_YELLOW)
				}
				if (color) {
					style = style.withColor(CommonColors.COSMOS_PINK)
				}
				visitor.accept(i, style, codePoint)
			}
			true
		}
	}
}
