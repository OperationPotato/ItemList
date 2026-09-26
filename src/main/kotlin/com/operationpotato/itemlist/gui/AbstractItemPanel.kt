package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.utils.ItemListSide
import net.minecraft.client.gui.ComponentPath
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.navigation.FocusNavigationEvent
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McScreen

abstract class AbstractItemPanel(
	x: Int, y: Int, width: Int, height: Int,
) : AbstractContainerWidget(x, y, width, height, Component.empty(), defaultSettings(0)) {

	protected var preferRightSide: Boolean = true

	abstract fun getListWidget(): AbstractItemList
	abstract fun updatePosition()
	abstract fun removed()

	fun updateWidth() {
		val screen = McScreen.self ?: return
		val bounds = PluginManager.getScreenBounds(screen, screen.width, screen.height) ?: return
		val isRightSide = ConfigManager.get().general.listSide == ItemListSide.RIGHT

		val availableWidth = if (isRightSide == preferRightSide) screen.width - bounds.right else bounds.left
		val panelWidth = (availableWidth * ConfigManager.get().general.maxWidth).toInt()
		x = if (isRightSide == preferRightSide) screen.width - panelWidth else 0
		width = panelWidth - 2
		updatePosition()
	}

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
		return children().any { it.isMouseOver(mouseX, mouseY) }
	}

	override fun contentHeight(): Int = height
	override fun updateWidgetNarration(output: NarrationElementOutput) {}
	override fun nextFocusPath(navigationEvent: FocusNavigationEvent): ComponentPath? = null
}
