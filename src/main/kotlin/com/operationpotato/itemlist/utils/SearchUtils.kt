package com.operationpotato.itemlist.utils

import com.operationpotato.itemlist.utils.search.AndSearch
import com.operationpotato.itemlist.utils.search.OrSearch
import com.operationpotato.itemlist.utils.search.Search
import com.operationpotato.itemlist.utils.search.TermSearch
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore

// TODO: Better search filtering
object SearchUtils {
	fun transformSearch(raw: String): Search {
		val groups = raw.split("|").map { group ->
			group.split("&").map { term -> TermSearch(term.trim()) }
		}
		val conditions = groups.map { terms -> if (terms.size == 1) terms.first() else AndSearch(terms) }
		return if (conditions.size == 1) conditions.first() else OrSearch(conditions)
	}

	fun isDistinctSearch(a: String, b: String): Boolean {
		return isDistinctSearch(transformSearch(a), transformSearch(b))
	}

	private fun isDistinctSearch(a: Search, b: Search): Boolean {
		return when {
			a is OrSearch && b is OrSearch -> isDistinctSearch(a.conditions, b.conditions)
			a is AndSearch && b is AndSearch -> isDistinctSearch(a.conditions, b.conditions)
			a is TermSearch && b is TermSearch -> !b.term.startsWith(a.term)
			else -> true
		}
	}

	private fun isDistinctSearch(a: List<Search>, b: List<Search>): Boolean {
		if (a.size != b.size) return true
		a.forEachIndexed { index, aSearch ->
			if (isDistinctSearch(aSearch, b[index])) return true
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
					'|'.code, '&'.code -> style = style.withColor(CommonColors.SOFT_YELLOW)
				}
				if (color) {
					style = style.withColor(CommonColors.COSMOS_PINK)
				}
				visitor.accept(i, style, codePoint)
			}
			true
		}
	}

	fun matchesSearch(stack: ItemStack, search: Search): Boolean {
		val stackName = stack.cleanName.lowercase()
		val loreLines = stack.getRawLore().map { it.lowercase() }
		return search.matches(stackName, loreLines)
	}
}
