package com.operationpotato.itemlist.gui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class TooltipWidget(tooltip: Component, width: Int, height: Int) :
	AbstractWidget(0, 0, width, height, Component.empty()) {
	init {
		this.setTooltip(Tooltip.create(tooltip))
	}

	override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {}
	override fun updateWidgetNarration(output: NarrationElementOutput) {}
}
