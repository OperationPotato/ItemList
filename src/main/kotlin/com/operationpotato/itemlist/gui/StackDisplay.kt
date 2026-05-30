package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.Settings
import com.operationpotato.itemlist.utils.ScaledItemRenderer
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.scale
import tech.thatgravyboat.skyblockapi.platform.translate
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import kotlin.math.roundToInt

class StackDisplay(val lazyStack: LazyItemStack, val type: SkyBlockItemCategory) :
	AbstractWidget(0, 0, STACK_SIZE, STACK_SIZE, Component.empty()) {

	var stack: ItemStack = ItemStack.EMPTY
	var scale: Float = 1f

	val stackName: String = stack.cleanName.lowercase()
	val loreLines: List<String> by lazy { stack.getRawLore().map { it.lowercase() } }

	fun getTooltipLines(): List<Component> {
		val tooltipStyle = if (McClient.options.advancedItemTooltips) {
			TooltipFlag.Default.ADVANCED
		} else {
			TooltipFlag.Default.NORMAL
		}
		return stack.getTooltipLines(Item.TooltipContext.of(McLevel.self), McPlayer.self, tooltipStyle)
	}

	override fun extractWidgetRenderState(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		if (stack.isEmpty) stack = lazyStack.create()
		graphics.pushPop {
			graphics.translate(x, y)
			graphics.scale(scale, scale)
			if (isHovered) graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, HIGHLIGHT_BACK, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
			)
			if (scale > 1f && Settings.nonPixelatedItemScale) {
				ScaledItemRenderer.extract(graphics, stack, 0, 0)
			} else {
				graphics.fakeItem(stack, 0, 0)
			}
			if (isHovered) graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, HIGHLIGHT_FRONT, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
			)
		}

		if (isHovered) {
			graphics.setComponentTooltipForNextFrame(McClient.gui.font, getTooltipLines(), mouseX, mouseY)
		}
	}

	fun scale(scale: Float) {
		val scaledSize = (STACK_SIZE * scale).roundToInt()
		setSize(scaledSize, scaledSize)
		this.scale = scale
	}

	fun matchesSearch(searches: List<String>): Boolean {
		if (stack.isEmpty) stack = lazyStack.create()
		return searches.any { stackName.contains(it) || loreLines.any { line -> line.contains(it) } }
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}

	companion object {
		const val STACK_SIZE = 16
		const val HIGHLIGHT_SIZE = 24

		private val HIGHLIGHT_BACK: Identifier = Identifier.withDefaultNamespace("container/slot_highlight_back")
		private val HIGHLIGHT_FRONT: Identifier = Identifier.withDefaultNamespace("container/slot_highlight_front")
	}
}
