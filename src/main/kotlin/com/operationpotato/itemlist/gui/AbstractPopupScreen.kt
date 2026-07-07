package com.operationpotato.itemlist.gui

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.helpers.McClient

abstract class AbstractPopupScreen(title: Component, val parent: Screen?) : Screen(title) {
	abstract fun createLayout(): Layout

	override fun init() {
		val imageWidget = ImageWidget.sprite(0, 0, BACKGROUND)
		addRenderableWidget(imageWidget)

		val layout = createLayout()

		imageWidget.setRectangle(
			layout.width + 20, layout.height + 20,
			layout.x - 10, layout.y - 10
		)
	}

	override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		parent?.extractRenderState(graphics, -1, -1, a)
		graphics.fill(0, 0, width, height, BACKGROUND_COLOR)
		super.extractBackground(graphics, mouseX, mouseY, a)
	}

	override fun resize(width: Int, height: Int) {
		parent?.resize(width, height)
		super.resize(width, height)
	}

	override fun onClose() {
		McClient.setScreen(parent)
	}

	companion object {
		val BACKGROUND: Identifier = Identifier.withDefaultNamespace("popup/background")
		val BACKGROUND_COLOR = ARGB.multiplyAlpha(CommonColors.BLACK, 0.5f)
	}
}
