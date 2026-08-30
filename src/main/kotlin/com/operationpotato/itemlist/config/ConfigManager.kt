package com.operationpotato.itemlist.config

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.utils.ItemClickAction
import tech.thatgravyboat.skyblockapi.helpers.McClient
import java.nio.file.Files

object ConfigManager {
	const val CONFIG_VERSION = 3
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
			val rawJson = Files.readString(file)
			val jsonElement = JsonParser.parseString(rawJson)

			if (jsonElement.isJsonObject) {
				val jsonObject = jsonElement.asJsonObject
				val currentVersion = if (jsonObject.has("version")) jsonObject.get("version").asInt else 1

				if (currentVersion < CONFIG_VERSION) {
					migrateConfig(jsonObject, currentVersion)
					jsonObject.addProperty("version", CONFIG_VERSION)

					settings = GSON.fromJson(jsonObject, Settings::class.java)
					save()
					return
				}
			}

			settings = GSON.fromJson(rawJson, Settings::class.java)
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

	private fun migrateConfig(json: JsonObject, fromVersion: Int) {
		var version = fromVersion

		if (version < 2) {
			if (json.has("general")) {
				val general = json.getAsJsonObject("general")
				val clickActions = listOf("leftClickAction", "middleClickAction", "rightClickAction")

				for (action in clickActions) {
					if (general.has(action) && general.get(action).asString == "OPEN_OFFICIAL_WIKI") {
						general.addProperty(action, ItemClickAction.OPEN_INDEPENDENT_WIKI.name)
					}
				}
			}
			version = 2 // Not useful here but when someone migrates from version 1 to 3 this is needed I think
		}

		if (version < 3) {
			if (json.has("mainList")) {
				val mainList = json.getAsJsonObject("mainList")
				if (mainList.has("hideItemsWithoutSearch") && mainList.get("hideItemsWithoutSearch").asBoolean) {
					mainList.addProperty("hideItemsWhen", "SEARCH")
				} else {
					mainList.addProperty("hideItemsWhen", "NEVER")
				}
			}
			@Suppress("AssignedValueIsNeverRead")
			version = 3
		}

	}
}
