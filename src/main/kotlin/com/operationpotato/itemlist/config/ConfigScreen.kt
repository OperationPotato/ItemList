package com.operationpotato.itemlist.config

import com.moulberry.lattice.Lattice
import com.moulberry.lattice.element.LatticeElements
import com.operationpotato.itemlist.SkyBlockItemList
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object ConfigScreen {
	fun createLatticeElements(): LatticeElements {
		val title = Component.literal("SkyBlock Item List Settings")
		val elements = LatticeElements.fromAnnotations(title, ConfigManager.get())
		return elements
	}

	fun createScreen(parent: Screen?): Screen {
		SkyBlockItemList.instance = null
		SkyBlockItemList.favoriteInstance = null
		val elements = createLatticeElements()
		val screen = Lattice.createConfigScreen(elements, null, parent)
		return screen
	}
}
