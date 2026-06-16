package com.operationpotato.itemlist.config

import com.moulberry.lattice.annotation.LatticeCategory
import com.moulberry.lattice.annotation.LatticeOption
import com.moulberry.lattice.annotation.constraint.LatticeFloatRange
import com.moulberry.lattice.annotation.constraint.LatticeIntRange
import com.moulberry.lattice.annotation.widget.LatticeWidgetButton
import com.moulberry.lattice.annotation.widget.LatticeWidgetKeybind
import com.moulberry.lattice.annotation.widget.LatticeWidgetSlider
import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.SkyBlockItemCategory

class Settings {
	@Suppress("unused")
	val version = ConfigManager.CONFIG_VERSION

	@LatticeCategory(name = "General Settings")
	val general = GeneralSettings()

	@LatticeCategory(name = "Main Item Panel")
	val mainList = MainListSettings()

	@LatticeCategory(name = "Favorites Item Panel")
	val favoritesList = FavoritesListSettings()

	@LatticeCategory(name = "Calculator")
	val calculator = CalculatorSettings()

	@LatticeCategory(name = "Keybinds")
	@Suppress("unused")
	@Transient
	val keybinds = KeybindSettings()

	class GeneralSettings {
		@LatticeOption(title = "Enabled", translate = false)
		@LatticeWidgetButton
		var enabled: Boolean = true

		@LatticeOption(
			title = "Enable Non-Pixelated Item Scaling",
			description = "Uses a custom item renderer when scaling above 100%.\nThis results in better looking items, at the cost of some performance.",
			translate = false
		)
		@LatticeWidgetButton
		var nonPixelatedItemScale: Boolean = true // Currently a bit laggy when actively scaling, else runs fine

		@LatticeOption(
			title = "Max Width",
			description = "How much of the available width that should be used up by the item list."
		)
		@LatticeFloatRange(min = 0.25f, max = 1f, clampMin = 0.25f, clampMax = 1f)
		@LatticeWidgetSlider
		var maxWidth: Float = 1f // Percentage 0..1
	}

	class MainListSettings {
		@LatticeOption(
			title = "Item Size",
			description = "This can be changed in-game by holding Control/Cmd and scrolling on the Item List."
		)
		@LatticeIntRange(min = 8, max = 48, clampMin = 8, clampMax = 48)
		@LatticeWidgetSlider
		var itemSize: Int = StackDisplay.STACK_SIZE

		@LatticeOption(
			title = "Hide Items Without Search",
			description = "Hides items when there is no active search.\nThis is recommended to improve performance."
		)
		@LatticeWidgetButton
		var hideItemsWithoutSearch: Boolean = true

		@LatticeOption(
			title = "Hide Vanilla Items",
		)
		@LatticeWidgetButton
		var hideVanillaItems: Boolean = false

		var customFilters: MutableList<SkyBlockItemCategory> = SkyBlockItemCategory.NON_ENTITIES.toMutableList()

		var lastSearch: String = ""
		var lastFilter: SkyBlockItemCategory = SkyBlockItemCategory.CUSTOM
	}

	class FavoritesListSettings {
		@LatticeOption(
			title = "Enable Favorites"
		)
		@LatticeWidgetButton
		var enableFavorites: Boolean = true

		@LatticeOption(
			title = "Favorites Item Size",
			description = "This can be changed in-game by holding Control/Cmd and scrolling on the Favorites List."
		)
		@LatticeIntRange(min = 8, max = 48, clampMin = 8, clampMax = 48)
		@LatticeWidgetSlider
		var favoritesItemSize: Int = StackDisplay.STACK_SIZE
	}

	class CalculatorSettings {
		@LatticeOption(
			title = "Require Equals Sign",
			description = "Whether an equals sign at the beginning of your search should be required for calculation."
		)
		@LatticeWidgetButton
		var requiresEquals: Boolean = false // maybe switch to true by default

		var customConstants: Map<String, Double> = mutableMapOf()
	}

	@Suppress("unused")
	class KeybindSettings {
		@LatticeOption(
			title = "Hide Overlay",
			description = "Hides the Item List and Favorites List.\nRequires holding Ctrl/Cmd!"
		)
		@LatticeWidgetKeybind
		@Transient
		val hideOverlay = Keybinds.hideOverlay

		@LatticeOption(title = "View Recipe", description = "Shows the recipes of the hovered item, if there are any.")
		@LatticeWidgetKeybind
		@Transient
		val viewRecipe = Keybinds.viewRecipe


		@LatticeOption(title = "View Usage", description = "Shows the uses of the hovered item, if there are any.")
		@LatticeWidgetKeybind
		@Transient
		val viewUsage = Keybinds.viewUsage

		@LatticeOption(
			title = "Return to Previous Recipe",
			description = "While in a recipe screen, pressing this allows you to go back to the previous recipe screen."
		)
		@LatticeWidgetKeybind
		@Transient
		val previousRecipe = Keybinds.previousRecipe

		@LatticeOption(title = "Favorite Item", description = "Adds the hovered item or recipe to your Favorites List.")
		@LatticeWidgetKeybind
		@Transient
		val favoriteItem = Keybinds.favoriteItem
	}
}
