package com.operationpotato.itemlist.config

import com.google.gson.GsonBuilder
import com.operationpotato.itemlist.SkyBlockItemList
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.nio.file.Files

object ConfigManager {
	const val CONFIG_VERSION = 1
	private val file = McClient.config.resolve("skyblock-item-list", "config.json")
	private var settings: Settings = Settings()
	private val GSON = GsonBuilder().setPrettyPrinting().create()

	fun get() = settings

	fun load() {
		if (!Files.exists(file)) {
			save()
			return
		}

		try {
			settings = GSON.fromJson(Files.readString(file), Settings::class.java)
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("[SkyBlock Item List] Failed to load config!", e)
		}
	}

	fun save() {
		try {
			Files.createDirectories(file.parent)
			val json = GSON.toJson(settings, Settings::class.java)
			Files.writeString(file, json)
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("[SkyBlock Item List] Failed to save config!", e)
		}
	}
}
