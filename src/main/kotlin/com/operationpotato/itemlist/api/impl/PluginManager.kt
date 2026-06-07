package com.operationpotato.itemlist.api.impl

import com.operationpotato.itemlist.api.ExclusionZone
import com.operationpotato.itemlist.api.Plugin
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.input.KeyEvent
import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.helpers.McScreen

@ApiStatus.Internal
object PluginManager {
	private val plugins: List<Plugin> =
		FabricLoader.getInstance().getEntrypoints("skyblock-item-list", Plugin::class.java)
	private val exclusionZoneManager = ExclusionZoneManagerImpl()
	private val hoveredItemManager = HoveredItemManagerImpl()
	private val recipeButtonsManager = RecipeButtonsManagerImpl()

	init {
		registerPlugins()
	}

	fun registerPlugins() {
		for (plugin in plugins) {
			plugin.registerExclusionZones(exclusionZoneManager)
			plugin.registerHoveredItems(hoveredItemManager)
			plugin.registerRecipeButtons(recipeButtonsManager)
		}
	}

	fun refreshExclusionZones() {
		exclusionZoneManager.calculateExclusionZones()
	}

	fun getExclusionZones(): List<ExclusionZone> {
		return exclusionZoneManager.getExclusionZones()
	}

	fun didExclusionZonesChange(): Boolean {
		return exclusionZoneManager.getHasChanged()
	}

	fun provideHoveredItem(stack: ItemStack, keyEvent: KeyEvent): Boolean {
		return hoveredItemManager.provideHoveredItem(McScreen.self!!, stack, keyEvent)
	}

	fun getRecipeButtons(recipe: Recipe<*>): List<AbstractWidget> {
		return recipeButtonsManager.getButtons(recipe)
	}
}
