package com.operationpotato.itemlist.gui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors

class SpacerTextWidget(x: Int, y: Int, width: Int, message: Component, val font: Font) :
	AbstractWidget(x, y, width, font.lineHeight, message) {
	override fun extractWidgetRenderState(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		graphics.centeredText(font, message, x + (width / 2), y, CommonColors.WHITE)
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}
}
