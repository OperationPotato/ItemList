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
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import kotlin.math.ceil

class CollapsibleStackDisplay(
	val familyItems: List<SkyBlockItems.Item>,
	val mainItem: SkyBlockItems.Item
) : StackDisplay(mainItem.stack, mainItem.category, mainItem.isVanilla) {

	var isExpanded = false
	var hoveredChildIndex = -1

	override val hoveredStack: ItemStack
		get() {
			if (isExpanded && hoveredChildIndex in familyItems.indices) {
				return familyItems[hoveredChildIndex].stack.create()
			}
			return stack
		}

	override fun matchesSearch(searches: List<String>): Boolean {
		return familyItems.any { item ->
			val stack = item.stack.create()
			val stackName = stack.cleanName.lowercase()
			val loreLines = stack.getRawLore().map { it.lowercase() }
			searches.any { stackName.contains(it) || loreLines.any { line -> line.contains(it) } }
		}
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		if (!this.active || !this.visible) return false

		val overParent = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
		if (overParent) return true

		if (isExpanded) {
			val cols = minOf(6, familyItems.size)
			val rows = ceil(familyItems.size / cols.toDouble())
			val expandedWidth = cols * STACK_SIZE * scale
			val expandedHeight = rows * STACK_SIZE * scale

			return mouseX >= x && mouseX < x + expandedWidth && mouseY >= y && mouseY < y + expandedHeight
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
		if (overParent) {
			isExpanded = true
			hoveredChildIndex = familyItems.indexOf(mainItem).takeIf { it >= 0 } ?: 0
		} else if (isExpanded) {
			val cols = minOf(6, familyItems.size)
			val rows = ceil(familyItems.size / cols.toDouble())
			val expandedWidth = cols * STACK_SIZE * scale
			val expandedHeight = rows * STACK_SIZE * scale

			val overExpanded = mouseX >= x && mouseX < x + expandedWidth && mouseY >= y && mouseY < y + expandedHeight
			if (overExpanded) {
				val relX = (mouseX - x) / (STACK_SIZE * scale)
				val relY = (mouseY - y) / (STACK_SIZE * scale)
				val index = relY.toInt() * cols + relX.toInt()
				hoveredChildIndex = if (index < familyItems.size) index else -1
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

		val cols = minOf(6, familyItems.size)
		val rows = ceil(familyItems.size / cols.toDouble())
		val expandedWidth = cols * STACK_SIZE * scale
		val expandedHeight = rows * STACK_SIZE * scale

		graphics.pushPop {
			graphics.fill(
				x - 2,
				y - 2,
				x + expandedWidth.toInt() + 2,
				y + expandedHeight.toInt() + 2,
				BACKGROUND_COLOR,
			)

			familyItems.forEachIndexed { index, familyItem ->
				val col = index % cols
				val row = index / cols
				val itemX = x + col * (STACK_SIZE * scale).toInt()
				val itemY = y + row * (STACK_SIZE * scale).toInt()

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
