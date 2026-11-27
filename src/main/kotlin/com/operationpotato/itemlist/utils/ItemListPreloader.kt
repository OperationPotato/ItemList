package com.operationpotato.itemlist.utils

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.repo.RepoEvent

/**
 * Keeps the cached item list in sync with the registries and the SkyBlock repo.
 */
object ItemListPreloader {

	fun init() {
		// Registries are replaced on reconnect, so everything built from them must be dropped.
		ClientConfigurationConnectionEvents.COMPLETE.register { _, _ -> ReloadableLazy.invalidateAll() }

		SkyBlockAPI.eventBus.register<RepoEvent.Reload> { ReloadableLazy.invalidateAll() }
	}
}
