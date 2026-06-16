package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import com.operationpotato.itemlist.utils.ScaledItemRenderer
import com.operationpotato.itemlist.utils.SkyBlockItems
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.util.ARGB
import net.minecraft.util.CommonColors
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.scale
import tech.thatgravyboat.skyblockapi.platform.translate
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import kotlin.math.ceil

class CollapsibleStackDisplay(
	val familyItems: List<SkyBlockItems.Item>,
	val mainItem: SkyBlockItems.Item
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
		val filterd = familyItems.filter { item ->
			val stack = item.stack.create()
			val stackName = stack.cleanName.lowercase()
			val loreLines = stack.getRawLore().map { it.lowercase() }
			searches.any { stackName.contains(it) || loreLines.any { line -> line.contains(it) } }
		}
		filteredFamilyItems = filterd
		return filterd.isNotEmpty()
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		if (!this.active || !this.visible) return false

		val overParent = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
		if (overParent) return true

		if (isExpanded) {
			val parentHeight = STACK_SIZE * scale
			val popoutY = y + parentHeight
			val cols = minOf(6, filteredFamilyItems.size)
			val rows = ceil(filteredFamilyItems.size / cols.toDouble())
			val expandedWidth = cols * STACK_SIZE * scale
			val expandedHeight = rows * STACK_SIZE * scale

			val screenWidth = McScreen.self?.width ?: Int.MAX_VALUE
			var popoutX = x.toDouble()
			if (popoutX + expandedWidth > screenWidth) {
				popoutX = screenWidth - expandedWidth - 4.0
			}
			popoutX = maxOf(4.0, popoutX)

			return mouseX >= popoutX && mouseX < popoutX + expandedWidth && mouseY >= popoutY && mouseY < popoutY + expandedHeight
		}

		return false
	}

	override fun extractWidgetRenderState(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		createStackIfEmpty()

		val overParent = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
		val parentHeight = STACK_SIZE * scale
		val popoutY = (y + parentHeight).toInt()

		val cols = minOf(6, filteredFamilyItems.size)
		val rows = ceil(filteredFamilyItems.size / cols.toDouble())
		val expandedWidth = cols * STACK_SIZE * scale
		val expandedHeight = rows * STACK_SIZE * scale

		val screenWidth = McScreen.self?.width ?: Int.MAX_VALUE
		var popoutX = x.toDouble()
		if (popoutX + expandedWidth > screenWidth) {
			popoutX = screenWidth - expandedWidth - 4.0
		}
		popoutX = maxOf(4.0, popoutX)

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
				isExpanded = false
				hoveredChildIndex = -1
			}
		} else {
			isExpanded = false
			hoveredChildIndex = -1
		}

		if (!isExpanded) {
			super.extractWidgetRenderState(graphics, mouseX, mouseY, a)

			graphics.pushPop {
				graphics.translate(x, y)
				graphics.scale(scale, scale)
				graphics.text(McFont.self, "+", 12, 12, CommonColors.WHITE)
			}
			return
		}

		graphics.pushPop {
			graphics.translate(x, y)
			graphics.scale(scale, scale)
			if (overParent) graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, HIGHLIGHT_BACK, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
			)
			if (scale > 1f && ConfigManager.get().general.nonPixelatedItemScale) {
				ScaledItemRenderer.extract(graphics, stack, 0, 0)
			} else {
				graphics.fakeItem(stack, 0, 0)
			}
			if (overParent) graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, HIGHLIGHT_FRONT, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
			)
		}

		graphics.pushPop {
			graphics.fill(
				popoutX.toInt() - 2,
				popoutY - 2,
				popoutX.toInt() + expandedWidth.toInt() + 2,
				popoutY + expandedHeight.toInt() + 2,
				BACKGROUND_COLOR,
			)

			filteredFamilyItems.forEachIndexed { index, familyItem ->
				val col = index % cols
				val row = index / cols
				val itemX = popoutX.toInt() + col * (STACK_SIZE * scale).toInt()
				val itemY = popoutY + row * (STACK_SIZE * scale).toInt()

				val itemStack = familyItem.stack.create()

				val isChildHovered = hoveredChildIndex == index

				graphics.pushPop {
					graphics.translate(itemX, itemY)
					graphics.scale(scale, scale)
					if (isChildHovered) graphics.blitSprite(
						RenderPipelines.GUI_TEXTURED, HIGHLIGHT_BACK, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
					)
					if (scale > 1f && ConfigManager.get().general.nonPixelatedItemScale) {
						ScaledItemRenderer.extract(graphics, itemStack, 0, 0)
					} else {
						graphics.fakeItem(itemStack, 0, 0)
					}
					if (isChildHovered) graphics.blitSprite(
						RenderPipelines.GUI_TEXTURED, HIGHLIGHT_FRONT, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
					)
				}

				if (isChildHovered) {
					val tooltipStyle = if (McClient.options.advancedItemTooltips) {
						TooltipFlag.Default.ADVANCED
					} else {
						TooltipFlag.Default.NORMAL
					}
					val tooltipLines =
						itemStack.getTooltipLines(Item.TooltipContext.of(McLevel.self), McPlayer.self, tooltipStyle)
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

	companion object {
		private val BACKGROUND_COLOR = ARGB.multiplyAlpha(CommonColors.BLACK, 0.8f)
	}
}
