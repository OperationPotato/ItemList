package com.operationpotato.itemlist.gui.recipe

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.translate

class IngredientDisplay(val stack: ItemStack, val showStackSize: Boolean = true) :
	AbstractWidget(0, 0, STACK_SIZE, STACK_SIZE, Component.empty()) {

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
		if (stack.isEmpty) return

		graphics.pushPop {
			graphics.translate(x, y)
			if (isHovered) graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, HIGHLIGHT_BACK, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
			)

			graphics.fakeItem(stack, 0, 0)
			if (showStackSize) graphics.itemDecorations(McFont.self, stack, 0, 0)

			if (isHovered) graphics.blitSprite(
				RenderPipelines.GUI_TEXTURED, HIGHLIGHT_FRONT, -4, -4, HIGHLIGHT_SIZE, HIGHLIGHT_SIZE
			)
		}

		if (isHovered) {
			graphics.setComponentTooltipForNextFrame(McClient.gui.font, getTooltipLines(), mouseX, mouseY)
		}
	}

	override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
		if (event.button() == 0) {
			RecipeScreen.openRecipeForItem(stack, McScreen.self)
		}
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}

	companion object {
		const val STACK_SIZE = 16
		const val HIGHLIGHT_SIZE = 24

		private val HIGHLIGHT_BACK: Identifier = Identifier.withDefaultNamespace("container/slot_highlight_back")
		private val HIGHLIGHT_FRONT: Identifier = Identifier.withDefaultNamespace("container/slot_highlight_front")
	}
}
