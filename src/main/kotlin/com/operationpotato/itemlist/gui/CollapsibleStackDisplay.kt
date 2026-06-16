package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import com.operationpotato.itemlist.utils.SkyBlockItems
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.util.ARGB
import net.minecraft.util.CommonColors
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.scale
import tech.thatgravyboat.skyblockapi.platform.translate
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import kotlin.math.ceil

class CollapsibleStackDisplay(
	val familyItems: List<SkyBlockItems.Item>,
	mainItem: SkyBlockItems.Item
) : StackDisplay(mainItem.stack, mainItem.category, mainItem.isVanilla) {

	var isExpanded = false
	var hoveredChildIndex = -1
	var filteredFamilyItems = familyItems

	override val hoveredStack: ItemStack
		get() {
			if (isExpanded && hoveredChildIndex in filteredFamilyItems.indices) {
				return filteredFamilyItems[hoveredChildIndex].stack.create()
			}
			return stack
		}

	override fun matchesSearch(searches: List<String>): Boolean {
		val filtered = familyItems.filter { item ->
			val stack = item.stack.create()
			val stackName = stack.cleanName.lowercase()
			val loreLines = stack.getRawLore().map { it.lowercase() }
			searches.any { stackName.contains(it) || loreLines.any { line -> line.contains(it) } }
		}
		filteredFamilyItems = filtered
		return filtered.isNotEmpty()
	}

	private fun getPopOutState(): PopOutState {
		val parentHeight = STACK_SIZE * scale
		val popoutY = y + parentHeight

		val cols = minOf(6, filteredFamilyItems.size)
		val rows = ceil(filteredFamilyItems.size / cols.toDouble()).toInt()
		val expandedWidth = cols * STACK_SIZE * scale
		val expandedHeight = rows * STACK_SIZE * scale

		val screenWidth = McScreen.self?.width ?: Int.MAX_VALUE
		var popoutX = x.toDouble()
		if (popoutX + expandedWidth > screenWidth) {
			popoutX = screenWidth - expandedWidth - 4.0
		}
		popoutX = maxOf(4.0, popoutX)

		return PopOutState(cols, rows, popoutX, popoutY, expandedWidth, expandedHeight)
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		if (!this.active || !this.visible) return false

		val overParent = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
		if (overParent) return true

		if (isExpanded) {
			val (_, _, popoutX, popoutY, expandedWidth, expandedHeight) = getPopOutState()
			return mouseX >= popoutX && mouseX < popoutX + expandedWidth && mouseY >= popoutY && mouseY < popoutY + expandedHeight
		}

		return false
	}

	fun resetState() {
		isExpanded = false
		hoveredChildIndex = -1
	}

	override fun extractWidgetRenderState(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		createStackIfEmpty()

		val overParent = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
		val (cols, _, popoutX, popoutY, expandedWidth, expandedHeight) = getPopOutState()

		if (overParent) {
			isExpanded = true
			hoveredChildIndex = -1
		} else if (isExpanded) {
			val overExpanded = mouseX >= popoutX && mouseX < popoutX + expandedWidth && mouseY >= popoutY && mouseY < popoutY + expandedHeight
			if (overExpanded) {
				val relX = (mouseX - popoutX) / (STACK_SIZE * scale)
				val relY = (mouseY - popoutY) / (STACK_SIZE * scale)
				val index = relY.toInt() * cols + relX.toInt()
				hoveredChildIndex = if (index < filteredFamilyItems.size) index else -1
			} else {
				resetState()
			}
		} else {
			resetState()
		}

		if (!isExpanded) {
			super.extractWidgetRenderState(graphics, mouseX, mouseY, a)
			graphics.pushPop {
				graphics.translate(x, y)
				graphics.scale(scale, scale)
				graphics.text(McFont.self, "+", 10, 10, CommonColors.WHITE)
			}
			return
		}

		extractStack(graphics, stack, x, y, overParent)
		graphics.pushPop {
			graphics.fill(
				popoutX.toInt() - 2,
				popoutY.toInt() - 2,
				popoutX.toInt() + expandedWidth.toInt() + 2,
				popoutY.toInt() + expandedHeight.toInt() + 2,
				BACKGROUND_COLOR,
			)

			filteredFamilyItems.forEachIndexed { index, familyItem ->
				val col = index % cols
				val row = index / cols
				val itemX = popoutX.toInt() + col * (STACK_SIZE * scale).toInt()
				val itemY = popoutY.toInt() + row * (STACK_SIZE * scale).toInt()

				val itemStack = familyItem.stack.create()
				val isChildHovered = hoveredChildIndex == index

				extractStack(graphics, itemStack, itemX, itemY, isChildHovered)

				if (isChildHovered) {
					val tooltipLines = getTooltipLines(itemStack)
					graphics.setComponentTooltipForNextFrame(McClient.gui.font, tooltipLines, mouseX, mouseY)
				}
			}
		}
	}

	override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
		if (event.button() == 0) {
			RecipeScreen.openRecipeForItem(hoveredStack, McScreen.self)
		}
	}

	private data class PopOutState(
		val cols: Int, val rows: Int,
		val popoutX: Double, val popoutY: Float,
		val expandedWidth: Float, val expandedHeight: Float,
	)

	companion object {
		private val BACKGROUND_COLOR = ARGB.multiplyAlpha(CommonColors.BLACK, 0.8f)
	}
}
