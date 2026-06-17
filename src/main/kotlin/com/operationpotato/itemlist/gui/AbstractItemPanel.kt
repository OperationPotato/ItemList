package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.api.impl.PluginManager
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient.mouse

abstract class AbstractItemPanel(
	x: Int, y: Int, width: Int, height: Int,
) : AbstractContainerWidget(x, y, width, height, Component.empty(), defaultSettings(0)) {

	abstract fun getListWidget(): AbstractItemList
	abstract fun updatePosition()
	abstract fun updateWidth()
	abstract fun removed()

	fun onScreenKeyPress(screen: Screen, event: KeyEvent): Boolean {
		if (!this.visible) return true
		if (event.isEscape) return true
		return !keyPressed(event)
	}

	final override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
		if (!visible) return false
		return getListWidget().mouseScrolled(x, y, scrollX, scrollY)
	}

	override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
		if (!super.isMouseOver(mouseX, mouseY)) return false

		// If the mouse is inside any active exclusion zone, ignore mouse input
		return !PluginManager.isInAnyExclusionZone(mouseX.toInt(), mouseY.toInt())
	}

	override fun contentHeight(): Int = height
	override fun updateWidgetNarration(output: NarrationElementOutput) {}
}
