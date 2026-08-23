package com.operationpotato.itemlist.api.impl

import com.operationpotato.itemlist.api.ExclusionZoneManager
import com.operationpotato.itemlist.api.ExclusionZoneProvider
import net.minecraft.client.gui.screens.Screen
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import java.util.*

@ApiStatus.Internal
class ExclusionZoneManagerImpl : ExclusionZoneManager {
	private val providers: MutableList<ProviderEntry<*>> = mutableListOf()
	private var currentScreenProviders: List<ProviderEntry<*>> = emptyList()

	private var exclusionZones: MutableList<ExclusionZone> = mutableListOf()
	private var previous: List<ExclusionZone> = listOf()

	private var previousHash = previous.hashCode()
	private var hasChanged: Boolean = false

	override fun <T : Screen> addProvider(screenClass: Class<T>, provider: ExclusionZoneProvider<T>) {
		providers.add(ProviderEntry(provider, screenClass))
	}

	fun onScreenOpened(screen: Screen) {
		currentScreenProviders = providers.filter { it.screenClass.isInstance(screen) }
	}

	fun onScreenClosed() {
		currentScreenProviders = emptyList()
	}

	fun getExclusionZones(): List<ExclusionZone> {
		return exclusionZones
	}

	fun calculateExclusionZones() {
		clearExclusionZones()
		val currentScreen = McScreen.self ?: return
		currentScreenProviders.forEach { (provider, screenClass) ->
			if (screenClass.isInstance(currentScreen)) {
				@Suppress("UNCHECKED_CAST") // should be safe! (famous last words)
				val zones = (provider as ExclusionZoneProvider<Screen>).provide(currentScreen)
				zones.forEach { exclusionZones.add(ExclusionZone(it)) }
			}
		}
		hasChanged = previousHash != hash(exclusionZones)
	}

	fun hash(list: List<ExclusionZone>): Int {
		var hash = 17
		list.forEach { zone ->
			val rect = zone.area
			val zoneHash = Objects.hash(rect.x, rect.y, rect.width, rect.height)
			hash = 31 * hash + zoneHash
		}
		return hash
	}

	fun clearExclusionZones() {
		previous = exclusionZones
		previousHash = hash(previous)
		exclusionZones = mutableListOf()
	}

	fun getHasChanged(): Boolean {
		return hasChanged
	}

	private data class ProviderEntry<T : Screen>(
		val provider: ExclusionZoneProvider<T>,
		val screenClass: Class<T>
	)
}
