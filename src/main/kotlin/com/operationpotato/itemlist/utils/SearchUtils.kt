package com.operationpotato.itemlist.utils

import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
import net.minecraft.util.FormattedCharSequence
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore

// TODO: Better search filtering
object SearchUtils {
	fun transformSearch(raw: String): List<List<String>> {
		return raw.split("|").map { group -> group.split("&").map { it.trim() } }
	}

	fun isDistinctSearch(a: String, b: String): Boolean {
		val aFilter = transformSearch(a)
		val bFilter = transformSearch(b)
		if (bFilter.size != aFilter.size) return true
		aFilter.forEachIndexed { index, aGroup ->
			val bGroup = bFilter[index]
			if (bGroup.size != aGroup.size) return true
			aGroup.forEachIndexed { i, aSearch ->
				if (!bGroup[i].startsWith(aSearch)) return true
			}
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
					'&'.code -> style = style.withColor(CommonColors.SOFT_YELLOW)
				}
				if (color) {
					style = style.withColor(CommonColors.COSMOS_PINK)
				}
				visitor.accept(i, style, codePoint)
			}
			true
		}
	}

	fun matches(stackName: String, loreLines: List<String>, groups: List<List<String>>): Boolean {
		return groups.any { group ->
			group.all { search -> stackName.contains(search) || loreLines.any { line -> line.contains(search) } }
		}
	}

	fun matchesSearch(stack: ItemStack, groups: List<List<String>>): Boolean {
		val stackName = stack.cleanName.lowercase()
		val loreLines = stack.getRawLore().map { it.lowercase() }
		return matches(stackName, loreLines, groups)
	}
}
