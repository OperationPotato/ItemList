package com.operationpotato.itemlist.api.impl

import com.operationpotato.itemlist.api.supportedscreen.ScreenBounds
import com.operationpotato.itemlist.api.supportedscreen.SupportedScreenManager
import com.operationpotato.itemlist.api.supportedscreen.SupportedScreenProvider
import net.minecraft.client.gui.screens.Screen
import org.jetbrains.annotations.ApiStatus
import kotlin.jvm.optionals.getOrNull

@ApiStatus.Internal
class SupportedScreenManagerImpl : SupportedScreenManager {
	private val providers: MutableList<ProviderEntry<*>> = mutableListOf()

	override fun <T : Screen> addProvider(screenClass: Class<T>, provider: SupportedScreenProvider<T>) {
		providers.add(ProviderEntry(provider, screenClass))
	}

	fun getBounds(screen: Screen, width: Int, height: Int): ScreenBounds? {
		for ((provider, screenClass) in providers.reversed()) {
			if (screenClass.isInstance(screen)) {
				@Suppress("UNCHECKED_CAST")
				val bounds = (provider as SupportedScreenProvider<Screen>).getBounds(screen, width, height)
				return bounds.getOrNull()
			}
		}
		return null
	}

	private data class ProviderEntry<T : Screen>(
		val provider: SupportedScreenProvider<T>,
		val screenClass: Class<T>
	)
}
