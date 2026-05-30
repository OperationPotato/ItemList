package com.operationpotato.itemlist

import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.gui.ItemPanel
import com.operationpotato.itemlist.utils.ScaledItemRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.fabricmc.fabric.api.event.Event
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.right

object SkyBlockItemList : ClientModInitializer {
	val latePhase = Identifier.fromNamespaceAndPath("skyblock-item-list", "late")
	var instance: ItemPanel? = null

	override fun onInitializeClient() {
		Keybinds.init()
		ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
		ScreenEvents.AFTER_INIT.register(latePhase, ::addItemListWidget)
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> resetWidget() }

		PictureInPictureRendererRegistry.register { ScaledItemRenderer(it.bufferSource()) }
	}

	fun addItemListWidget(mc: Minecraft, screen: Screen, w: Int, h: Int) {
		if (!LocationAPI.isOnSkyBlock) return
		if (screen is AbstractContainerScreen<*>) {
			if (screen is InventoryScreen && mc.player?.hasInfiniteMaterials() ?: false) return
			val width = w - screen.right
			val itemPanel = instance ?: ItemPanel(0, 0, 0, 0)
			instance = itemPanel

			itemPanel.setPosition(screen.right, 0)
			itemPanel.setSize(width - 2, h)
			itemPanel.updatePosition()
			itemPanel.visible = Settings.enabled
			if (width < 80) itemPanel.visible = false

			Screens.getWidgets(screen).add(itemPanel)
			val mouseScroll = ScreenMouseEvents.allowMouseScroll(screen)
			mouseScroll.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
			mouseScroll.register(latePhase) { _, x, y, scrollX, scrollY ->
				itemPanel.mouseScrolled(x, y, scrollX, scrollY)
			}
			val keyPress = ScreenKeyboardEvents.allowKeyPress(screen)
			keyPress.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
			keyPress.register(latePhase) { screen, event ->
				val bl = itemPanel.onScreenKeyPress(screen, event)
				if (!bl) return@register false
				if (Keybinds.hideOverlay.matches(event)) {
					itemPanel.visible = !itemPanel.visible
					return@register false
				}
				true
			}
			val beforeExtract = ScreenEvents.beforeExtract(screen)
			beforeExtract.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
			beforeExtract.register(latePhase) { _, _, _, _, _ ->
				if (!itemPanel.visible) return@register
				PluginManager.refreshExclusionZones()
			}

			ScreenEvents.remove(screen).register {
				itemPanel.removed()
			}
		}
	}

	fun resetWidget() {
		instance = null
	}
}
