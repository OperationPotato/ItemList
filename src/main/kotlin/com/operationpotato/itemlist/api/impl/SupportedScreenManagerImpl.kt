package com.operationpotato.itemlist.api.impl

import com.operationpotato.itemlist.api.supportedscreen.SupportedScreenManager
import com.operationpotato.itemlist.api.supportedscreen.SupportedScreenProvider
import net.minecraft.client.gui.screens.Screen
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class SupportedScreenManagerImpl : SupportedScreenManager {
	private val providers: MutableList<ProviderEntry<*>> = mutableListOf()

	override fun <T : Screen> addProvider(screenClass: Class<T>, provider: SupportedScreenProvider<T>) {
		providers.add(ProviderEntry(provider, screenClass))
	}

	fun getRightBound(screen: Screen, width: Int, height: Int): Int? {
		for ((provider, screenClass) in providers) {
			if (screenClass.isInstance(screen)) {
				@Suppress("UNCHECKED_CAST")
				val rightBound = (provider as SupportedScreenProvider<Screen>).getRightBound(screen, width, height)
				if (rightBound.isEmpty) return null
				return rightBound.asInt
			}
		}
		return null
	}

	private data class ProviderEntry<T : Screen>(
		val provider: SupportedScreenProvider<T>,
		val screenClass: Class<T>
	)
}
