package com.operationpotato.itemlist.config

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import com.operationpotato.itemlist.SkyBlockItemList
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.json.Json.toPrettyString
import java.nio.file.Files

object ConfigManager {
	private val file = McClient.config.resolve("skyblock-item-list", "config.json")
	private var settings: Settings = Settings()

	fun get() = settings

	fun load() {
		if (!Files.exists(file)) {
			save()
			return
		}

		try {
			val json = JsonParser.parseString(Files.readString(file))
//			settings = Settings.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("[SkyBlock Item List] Failed to load config!", e)
		}
	}

	fun save() {
		try {
			Files.createDirectories(file.parent)
//			Settings.CODEC.encodeStart(JsonOps.INSTANCE, get()).result().ifPresent {
//				Files.writeString(file, it.toPrettyString())
//			}
		} catch (e: Exception) {
			SkyBlockItemList.logger.error("[SkyBlock Item List] Failed to save config!", e)
		}
	}
}
