package com.operationpotato.itemlist

import com.mojang.logging.LogUtils
import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.ItemPanel
import com.operationpotato.itemlist.gui.favorites.FavoritesPanel
import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import com.operationpotato.itemlist.utils.ScaledItemRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
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
import net.minecraft.client.input.KeyEvent
import net.minecraft.resources.Identifier
import org.slf4j.Logger
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.right
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

object SkyBlockItemList : ClientModInitializer {
	val latePhase = id("late")
	val logger: Logger = LogUtils.getLogger()
	var instance: ItemPanel? = null
	var favoriteInstance: FavoritesPanel? = null

	override fun onInitializeClient() {
		ConfigManager.load()
		FavoritesManager.load()
		Keybinds.init()
		ScreenEvents.AFTER_INIT.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
		ScreenEvents.AFTER_INIT.register(latePhase, ::addItemListWidget)
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> resetWidget() }
		ClientLifecycleEvents.CLIENT_STOPPING.register {
			ConfigManager.save()
			FavoritesManager.save()
		}

		PictureInPictureRendererRegistry.register { ScaledItemRenderer(it.bufferSource()) }
	}

	fun addItemListWidget(mc: Minecraft, screen: Screen, w: Int, h: Int) {
		if (!LocationAPI.isOnSkyBlock && !McClient.isDev) return
		if (screen is AbstractContainerScreen<*> || screen is RecipeScreen) {
			if (screen is InventoryScreen && mc.player?.hasInfiniteMaterials() ?: false) return
			val screenRight = when (screen) {
				is AbstractContainerScreen<*> -> screen.right
				is RecipeScreen -> screen.getRight()
				else -> w
			}

			val availableWidth = w - screenRight
			val panelWidth = (availableWidth * ConfigManager.get().general.maxWidth).toInt()

			val itemPanel = instance ?: ItemPanel(0, 0, 0, 0)
			instance = itemPanel

			itemPanel.setPosition(w - panelWidth, 0)
			itemPanel.setSize(panelWidth - 2, h)
			itemPanel.updatePosition()
			itemPanel.visible = ConfigManager.get().general.enabled
			if (panelWidth < 80) itemPanel.visible = false

			Screens.getWidgets(screen).add(itemPanel)

			val favPanel = favoriteInstance ?: FavoritesPanel(0, 0, 0, 0)
			favoriteInstance = favPanel

			favPanel.setPosition(0, 0)
			favPanel.setSize(panelWidth - 2, h)
			favPanel.updatePosition()
			favPanel.visible = ConfigManager.get().general.enabled && ConfigManager.get().favoritesList.enableFavorites
			if (panelWidth < 80) favPanel.visible = false

			Screens.getWidgets(screen).add(favPanel)

			var modName: String? = PluginManager.onScreenOpened(screen)
			if (modName != null) {
				itemPanel.visible = false
				favPanel.visible = false
			}

			val mouseScroll = ScreenMouseEvents.allowMouseScroll(screen)
			mouseScroll.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
			mouseScroll.register(latePhase) { _, x, y, scrollX, scrollY ->
				if (favPanel.mouseScrolled(x, y, scrollX, scrollY)) false
				else !itemPanel.mouseScrolled(x, y, scrollX, scrollY)
			}
			ScreenMouseEvents.allowMouseClick(screen).register { _, _ ->
				itemPanel.focused = null
				favPanel.focused = null
				true
			}
			val keyPress = ScreenKeyboardEvents.allowKeyPress(screen)
			keyPress.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
			keyPress.register(latePhase) { screen, event ->
				if (event.hasControlDownWithQuirk() && Keybinds.hideOverlay.matches(event)) {
					if (modName != null) {
						Text.of("Item list was previously hidden by $modName.").withColor(TextColor.YELLOW).send()
						modName = null
					}
					itemPanel.visible = !itemPanel.visible
					favPanel.visible = itemPanel.visible && ConfigManager.get().favoritesList.enableFavorites
					ConfigManager.get().general.enabled = itemPanel.visible
					return@register false
				}
				if (!favPanel.onScreenKeyPress(screen, event)) return@register false
				if (!itemPanel.onScreenKeyPress(screen, event)) return@register false
				return@register !handleScreenRecipeLookup(screen, event)
			}
			val beforeExtract = ScreenEvents.beforeExtract(screen)
			beforeExtract.addPhaseOrdering(Event.DEFAULT_PHASE, latePhase)
			beforeExtract.register(latePhase) { _, _, _, _, _ ->
				if (!itemPanel.visible) return@register
				PluginManager.refreshExclusionZones()
			}

			ScreenEvents.remove(screen).register {
				PluginManager.onScreenClosed()
				if (modName == null) ConfigManager.get().general.enabled = itemPanel.visible
				favPanel.removed()
				itemPanel.removed()
			}
		}
	}

	fun handleScreenRecipeLookup(screen: Screen, event: KeyEvent): Boolean {
		val item = PluginManager.getHoveredItem(screen) ?: return false
		return Keybinds.handleKeybind(item, event)
	}

	fun resetWidget() {
		instance = null
		favoriteInstance = null
	}

	fun id(path: String): Identifier = Identifier.fromNamespaceAndPath("skyblock-item-list", path)
}
