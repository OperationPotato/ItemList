package com.operationpotato.itemlist.gui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import org.lwjgl.glfw.GLFW

class FilterableEditBox(
	font: Font,
	width: Int,
	height: Int,
	narration: Component,
	val filterButton: FilterButton,
	val acceptCalcResult: Runnable,
) : ClearableEditBox(font, width, height, narration) {

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		if (filterButton.mouseClicked(event, doubleClick)) return true
		return super.mouseClicked(event, doubleClick)
	}

	override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		super.extractWidgetRenderState(graphics, mouseX, mouseY, a)
		filterButton.extractRenderState(graphics, mouseX, mouseY, a)
	}

	override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
		if (filterButton.isMouseOver(x, y)) {
			return filterButton.mouseScrolled(x, y, scrollX, scrollY)
		}
		return super.mouseScrolled(x, y, scrollX, scrollY)
	}

	override fun keyPressed(event: KeyEvent): Boolean {
		if (active && isFocused && event.key == GLFW.GLFW_KEY_ENTER) {
			acceptCalcResult.run()
			return true
		}
		return super.keyPressed(event)
	}

	override fun getInnerWidth(): Int {
		return super.getInnerWidth() - 16
	}
}
