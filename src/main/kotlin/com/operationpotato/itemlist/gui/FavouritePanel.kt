package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.Settings
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.right

class FavouritePanel(x: Int, y: Int, width: Int, height: Int) :
	AbstractContainerWidget(x, y, width, height, Component.empty(), defaultSettings(0)) {
	val listWidget = FavouriteListWidget(width - AbstractItemList.PADDING, height - 20)

	fun updatePosition() {
		listWidget.setPosition(x, y + 10)
		listWidget.setSize(width - 2, height - 20)
		listWidget.positioningCallback = {
			McClient.runOrNextTick {}
		}
		listWidget.itemSize = Settings.itemSize
		listWidget.scaleChildren()
		listWidget.updatePositionsAsync()
	}

	override fun children(): List<GuiEventListener> = listOf(listWidget)

	override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
		return listWidget.mouseScrolled(x, y, scrollX, scrollY)
	}

	override fun keyPressed(event: KeyEvent): Boolean {
		if (!this.visible) return false
		return listWidget.keyPressed(event)
	}

	fun updateWidth() {
		val screen = McScreen.self
		if (screen !is AbstractContainerScreen<*>) return
		x = 0
		width = screen.width - screen.right
		updatePosition()
	}

	fun onScreenKeyPress(screen: Screen, event: KeyEvent): Boolean {
		if (!this.visible) return true
		if (event.isEscape) return true
		return !keyPressed(event)
	}

	override fun contentHeight(): Int = height

	override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		listWidget.extractRenderState(graphics, mouseX, mouseY, a)
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}
}
