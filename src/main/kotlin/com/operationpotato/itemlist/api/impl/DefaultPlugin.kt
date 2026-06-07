package com.operationpotato.itemlist.api.impl

import com.google.common.collect.Ordering
import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.Plugin
import com.operationpotato.itemlist.api.RecipeButtonManager
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
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
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
		manager.addProvider { stack ->
			Optional.of(
				Button.builder(Text.of("+")) {
					// TODO: add favouriting here or smth
					println("meowing at ${stack.cleanName}")
				}.apply {
					tooltip(Tooltip.create(Text.of("Favourite")))
					size(10, 10)
				}.build()
			)
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
