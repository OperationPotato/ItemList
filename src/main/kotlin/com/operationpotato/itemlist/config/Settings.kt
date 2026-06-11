package com.operationpotato.itemlist.config

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.SkyBlockItemCategory

data class Settings(
	// General
	var enabled: Boolean = false,
	var nonPixelatedItemScale: Boolean = true, // Currently a bit laggy when actively scaling, else runs fine

	// Main List
	var itemSize: Int = StackDisplay.STACK_SIZE,
	var lastSearch: String = "",
	var lastFilter: SkyBlockItemCategory = SkyBlockItemCategory.CUSTOM,
	var customFilters: MutableList<SkyBlockItemCategory> = SkyBlockItemCategory.NON_ENTITIES.toMutableList(),
	var hideItemsWithoutSearch: Boolean = true,
	var hideVanillaItems: Boolean = true,

	// Favorites
	var enableFavorites: Boolean = true,
	var favoritesItemSize: Int = StackDisplay.STACK_SIZE,

	// Calculator
	var requiresEquals: Boolean = false, // maybe switch to true by default
	var customConstants: Map<String, Double> = mutableMapOf(),
) {
	companion object {
		val CODEC: Codec<Settings> = RecordCodecBuilder.create { instance ->
			instance.group(
				Codec.BOOL.fieldOf("enabled").forGetter(Settings::enabled),
				Codec.BOOL.fieldOf("nonPixelatedItemScaling").forGetter(Settings::nonPixelatedItemScale),
				Codec.INT.fieldOf("itemSize").forGetter(Settings::itemSize),
				Codec.STRING.fieldOf("lastSearch").forGetter(Settings::lastSearch),
				SkyBlockItemCategory.CODEC.fieldOf("lastFilter").forGetter(Settings::lastFilter),
				SkyBlockItemCategory.CODEC.listOf().fieldOf("customFilters").forGetter(Settings::customFilters),
				Codec.BOOL.fieldOf("hideItemsWithoutSearch").forGetter(Settings::hideItemsWithoutSearch),
				Codec.BOOL.optionalFieldOf("hideVanillaItems", false).forGetter(Settings::hideVanillaItems),
				Codec.BOOL.fieldOf("enableFavorites").forGetter(Settings::enableFavorites),
				Codec.INT.fieldOf("favoritesItemSize").forGetter(Settings::favoritesItemSize),
				Codec.BOOL.fieldOf("requiresEquals").forGetter(Settings::requiresEquals),
				Codec.unboundedMap(Codec.STRING, Codec.DOUBLE).fieldOf("customConstants")
					.forGetter(Settings::customConstants),
			).apply(instance, ::Settings)
		}
	}
}
