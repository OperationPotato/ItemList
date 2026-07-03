package com.operationpotato.itemlist.api.impl

import com.operationpotato.itemlist.api.ExcludedScreenProvider
import com.operationpotato.itemlist.api.ExcludedScreensManager
import net.minecraft.client.gui.screens.Screen
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
class ExcludedScreensManagerImpl : ExcludedScreensManager {
	private val providers: MutableList<ProviderEntry<*>> = mutableListOf()

	override fun <T : Screen> addProvider(screenClass: Class<T>, provider: ExcludedScreenProvider<T>) {
		providers.add(ProviderEntry(provider, screenClass))
	}

	fun checkScreen(screen: Screen): String? {
		providers.forEach { (provider, screenClass) ->
			if (screenClass.isInstance(screen)) {
				@Suppress("UNCHECKED_CAST")
				val res = (provider as ExcludedScreenProvider<Screen>).shouldDisable(screen)
				if (res.isPresent) return res.get()
			}
		}
		return null
	}

	private data class ProviderEntry<T : Screen>(
		val provider: ExcludedScreenProvider<T>,
		val screenClass: Class<T>
	)
}
