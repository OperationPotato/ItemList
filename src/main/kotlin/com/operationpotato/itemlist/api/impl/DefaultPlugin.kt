package com.operationpotato.itemlist.api.impl

import com.google.common.collect.Ordering
import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.api.ExcludedScreensManager
import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.HoveredItemManager
import com.operationpotato.itemlist.api.Plugin
import com.operationpotato.itemlist.api.RecipeButtonManager
import com.operationpotato.itemlist.favorites.FavoritesManager
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
import net.minecraft.client.gui.screens.inventory.EffectsInInventory
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.renderer.Rect2i
import net.minecraft.world.effect.MobEffectInstance
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.getHoveredSlot
import tech.thatgravyboat.skyblockapi.utils.extentions.right
import tech.thatgravyboat.skyblockapi.utils.extentions.top
import tech.thatgravyboat.skyblockapi.utils.text.Text
import java.util.*

@ApiStatus.Internal
class DefaultPlugin : Plugin {
	override fun registerExclusionZones(exclusionZoneManager: ExclusionZoneManager) {
		exclusionZoneManager.addProvider(InventoryScreen::class.java, ::provide)
		exclusionZoneManager.addProvider(CreativeModeInventoryScreen::class.java, ::provide)
	}

	override fun registerRecipeButtons(manager: RecipeButtonManager) {
		manager.addProvider { recipeObj, stack ->
			val id = stack.getSkyBlockId()?.skyblockId
			if ((recipeObj as? Recipe<*>)?.type() != Recipe.Type.CRAFTING || id == null) {
				return@addProvider Optional.empty()
			}
			Optional.of(
				Button.builder(Text.of("o")) { button ->
					if (button.active) McClient.sendCommand("viewrecipe $id")
				}.apply {
					tooltip(Tooltip.create(Text.of("Show SkyBlock Craft")))
					size(10, 10)
				}.build()
			)
		}

		manager.addProvider { recipeObj, _ ->
			val recipe = recipeObj as? Recipe<*> ?: return@addProvider Optional.empty()
			val isFav = FavoritesManager.isFavoriteRecipe(recipe)
			val favText = Text.of("+")
			val unfavText = Text.of("-")
			val favTooltip = Tooltip.create(Text.of("Favorite Recipe"))
			val unfavTooltip = Tooltip.create(Text.of("Unfavorite Recipe"))

			Optional.of(
				Button.builder(if (isFav) unfavText else favText) {
					if (FavoritesManager.isFavoriteRecipe(recipe)) {
						FavoritesManager.removeFavoriteRecipe(recipe)
						it.message = favText
						it.setTooltip(favTooltip)
					} else {
						FavoritesManager.addFavoriteRecipe(recipe)
						it.message = unfavText
						it.setTooltip(unfavTooltip)
					}
				}.apply {
					tooltip(if (isFav) unfavTooltip else favTooltip)
					size(10, 10)
				}.build()
			)
		}

		manager.addProvider { recipeObj, _ ->
			val pinnedRecipe = FavoritesManager.favorites.pinnedRecipe
			if (pinnedRecipe.isEmpty || recipeObj != pinnedRecipe.get()) return@addProvider Optional.empty()
			Optional.of(
				Button.builder(Text.of("x")) {
					SkyBlockItemList.favoriteInstance?.removeRecipe()
				}.apply {
					tooltip(Tooltip.create(Text.of("Unpin Recipe")))
					size(10, 10)
				}.build()
			)
		}
	}

	override fun registerHoveredItems(hoveredItemManager: HoveredItemManager) {
		hoveredItemManager.addProvider { screen ->
			if (screen is AbstractContainerScreen<*>) return@addProvider screen.getHoveredSlot()?.item
			return@addProvider null
		}
	}

	override fun registerExcludedScreens(excludedScreensManager: ExcludedScreensManager) {
		if (!McClient.isDev) return
		excludedScreensManager.addProvider(AbstractContainerScreen::class.java) { screen ->
			if (screen.title.string == "Hide Item List") return@addProvider Optional.of("Excluded Screens Test")
			return@addProvider Optional.empty()
		}
	}

	fun provide(screen: AbstractContainerScreen<*>): List<Rect2i> {
		val player = McPlayer.self ?: return listOf()
		if (!screen.showsActiveEffects()) return listOf()
		if (player.activeEffects.isEmpty()) return listOf()
		val effectsInInventory = getEffectsInInventory(screen) ?: return listOf()

		val playerEffects = Ordering.natural<MobEffectInstance>().sortedCopy(player.activeEffects)

		val x = screen.right + 2
		val availableWidth = screen.width - (x)
		val maxWidth = if (availableWidth >= 120) 120 else 32
		var y = screen.top
		val step = if (player.activeEffects.size > 5) 132 / (player.activeEffects.size) else 33

		val zones = mutableListOf<Rect2i>()
		playerEffects.forEach { effect ->
			val width = if (maxWidth == 32) maxWidth else getEffectSize(effectsInInventory, effect)
			zones.add(Rect2i(x, y, width, 32))
			y += step
		}
		return zones
	}

	fun getEffectSize(effectsInInventory: EffectsInInventory, effect: MobEffectInstance): Int {
		return 32 + McFont.self.width(effectsInInventory.getEffectName(effect))
	}

	fun getEffectsInInventory(screen: Screen): EffectsInInventory? {
		if (screen is CreativeModeInventoryScreen) {
			return screen.effects
		} else if (screen is InventoryScreen) {
			return screen.effects
		}
		return null
	}
}
