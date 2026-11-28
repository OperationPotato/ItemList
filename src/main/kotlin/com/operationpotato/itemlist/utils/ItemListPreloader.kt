package com.operationpotato.itemlist.utils

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.EntireListWidget
import com.operationpotato.itemlist.utils.ThreadUtils.cancelAndSubmit
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.location.SkyBlockLocationEvent
import tech.thatgravyboat.skyblockapi.api.events.repo.RepoEvent
import java.util.concurrent.Future

object ItemListPreloader {
	private var future: Future<*>? = null

	fun init() {
		// Registries are replaced on reconnect, so everything built from them must be dropped
		ClientConfigurationConnectionEvents.COMPLETE.register { _, _ -> ReloadableLazy.invalidateAll() }

		SkyBlockAPI.eventBus.register<SkyBlockLocationEvent.Join> { preload() }
		SkyBlockAPI.eventBus.register<RepoEvent.Reload> {
			ReloadableLazy.invalidateAll()
			preload()
		}
	}

	fun preload() {
		if (!RepoAPI.isInitialized()) return
		if (!SkyBlockItemList.isEnabled()) return
		future = ThreadUtils.SORTING_EXECUTOR.cancelAndSubmit(future) {
			try {
				val start = System.nanoTime()
				val children = EntireListWidget.children
				// Create the stacks too so the first search doesn't have to
				children.forEach { it.warmUp() }
				SkyBlockItemList.logger.info(
					"Preloaded {} item list entries in {} ms",
					children.size, (System.nanoTime() - start) / 1_000_000,
				)
			} catch (e: Exception) {
				SkyBlockItemList.logger.warn("Failed to preload item list", e)
			}
		}
	}
}
