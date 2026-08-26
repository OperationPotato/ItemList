package com.operationpotato.itemlist.utils

import com.operationpotato.itemlist.utils.search.IdentifierSearch
import com.operationpotato.itemlist.utils.search.Search
import com.operationpotato.itemlist.utils.search.TextSearch
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
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId

// TODO: Better search filtering
object SearchUtils {
	fun transformSearch(raw: String): List<Search> {
		return raw.split("|").map {
			if (it.startsWith("#")) IdentifierSearch(it.trim().substring(1))
			else TextSearch(it.trim())
		}

	fun transformSearch(raw: String): Search {
		val groups = raw.split("|").map { group ->
			group.split("&").map { term -> TermSearch(term.trim()) }
		}
		val conditions = groups.map { terms -> if (terms.size == 1) terms.first() else AndSearch(terms) }
		return if (conditions.size == 1) conditions.first() else OrSearch(conditions)
	}

	fun isDistinctSearch(a: String, b: String): Boolean {
		val aFilter = transformSearch(a)
		val bFilter = transformSearch(b)
		if (bFilter.size != aFilter.size) return true
		aFilter.forEachIndexed { index, aSearch ->
			val bSearch = bFilter[index]
			if (!aSearch.contains(bSearch)) return true
		}
		return false
	}

	fun highlightSearch(text: String, offset: Int): FormattedCharSequence {
		return { visitor ->
			var color: Int? = null // persists until the next space
			for (i in text.indices) {
				val codePoint = text.codePointAt(i)
				var style = Style.EMPTY // for this codepoint only
				when (codePoint) {
					'@'.code -> if (i == 0) color = CommonColors.COSMOS_PINK
					' '.code -> color = null
					'|'.code, '&'.code -> style = style.withColor(CommonColors.SOFT_YELLOW)
					'#'.code -> color = CommonColors.GREEN
				}
				if (color != null) {
					style = style.withColor(color)
				}
				visitor.accept(i, style, codePoint)
			}
			true
		}
	}

	private fun evaluateSearch(search: Search, stackName: String, combinedLore: String, skyblockId: String?) =
		when (search) {
			is IdentifierSearch -> if (skyblockId == null) false else search.matches(skyblockId)
			is TextSearch -> search.matches(stackName) || search.matches(combinedLore)
			else -> false
		}

	fun matches(stackName: String, loreLines: List<String>, skyblockId: String?, searches: List<Search>): Boolean {
		val combinedLore = loreLines.joinToString(" ")
		return searches.any {
			return@any evaluateSearch(it, stackName, combinedLore, skyblockId)
		}
	}

	fun matchesSearch(stack: ItemStack, searches: List<Search>): Boolean {
		if (stack.isEmpty) return false
		val stackName = stack.cleanName.lowercase()
		val loreLines = stack.getRawLore().map { it.lowercase() }
		val skyblockId = stack.getSkyBlockId()
		return matches(stackName, loreLines, skyblockId, searches)
	}
}
