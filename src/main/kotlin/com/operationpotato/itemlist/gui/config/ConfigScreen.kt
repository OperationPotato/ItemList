package com.operationpotato.itemlist.gui.config

import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McClient

class ConfigScreen(val parent: Screen?) : Screen(Component.literal("SkyBlock Item List Settings")) {
	override fun isInGameUi(): Boolean = true

	override fun init() {
		val layout = HeaderAndFooterLayout(this)
		layout.visitWidgets(this::addRenderableWidget)
	}

	override fun onClose() {
		McClient.setScreen(parent)
	}
}
