package com.operationpotato.itemlist.config

import com.moulberry.lattice.WidgetFunction
import com.moulberry.lattice.annotation.LatticeCategory
import com.moulberry.lattice.annotation.LatticeOption
import com.moulberry.lattice.annotation.constraint.LatticeFloatRange
import com.moulberry.lattice.annotation.constraint.LatticeIntRange
import com.moulberry.lattice.annotation.widget.LatticeWidgetButton
import com.moulberry.lattice.annotation.widget.LatticeWidgetCustom
import com.moulberry.lattice.annotation.widget.LatticeWidgetDropdown
import com.moulberry.lattice.annotation.widget.LatticeWidgetKeybind
import com.moulberry.lattice.annotation.widget.LatticeWidgetSlider
import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.gui.config.ConfigureFilterScreen
import com.operationpotato.itemlist.gui.config.CustomConstantsScreen
import com.operationpotato.itemlist.utils.HideListType
import com.operationpotato.itemlist.utils.ItemClickAction
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import java.util.function.Consumer
import java.util.function.Supplier

class Settings {
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
	@Transient
	val keybinds = KeybindSettings()

	class GeneralSettings {
		@LatticeOption(title = "Enabled", translate = false)
		@LatticeWidgetButton
		var enabled: Boolean = true

		@LatticeOption(
			title = "Enable Non-Pixelated Item Scaling",
			description = "Uses a custom item renderer when scaling above 100%." +
				"\nThis results in better looking items, at the cost of some performance.",
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
		var maxWidth: Float = 1f

		@LatticeOption(
			title = "Left Click Action",
			description = "What happens when you left click an Item"
		)
		@LatticeWidgetDropdown
		var leftClickAction: ItemClickAction = ItemClickAction.OPEN_RECIPE

		@LatticeOption(
			title = "Middle Click Action",
			description = "What happens when you middle click an Item"
		)
		@LatticeWidgetDropdown
		var middleClickAction: ItemClickAction = ItemClickAction.NOTHING

		@LatticeOption(
			title = "Right Click Action",
			description = "What happens when you right click an Item"
		)
		@LatticeWidgetDropdown
		var rightClickAction: ItemClickAction = ItemClickAction.OPEN_INDEPENDENT_WIKI

		@LatticeOption(
			title = "Show Outside SkyBlock",
			description = "Items will show up with missing textures if you do not have the SkyBlock Resource Pack enabled." +
				"\nTo get textures, you can use \"/sbapi pack stable\" to apply Hypixel's Pack."
		)
		@LatticeWidgetButton
		var showOutsideSkyBlock: Boolean = false
	}

	class MainListSettings {
		@LatticeOption(
			title = "Item Size",
			description = "This can be changed in-game by holding Control/Command and scrolling on the Item List."
		)
		@LatticeIntRange(min = 8, max = 48, clampMin = 8, clampMax = 48)
		@LatticeWidgetSlider
		var itemSize: Int = StackDisplay.STACK_SIZE

		@LatticeOption(
			title = "Center Search Bar",
			description = "Whether the search bar should be centered horizontally on screen"
				+ " or centered within the item panel."
		)
		@LatticeWidgetButton
		var centeredSearchBar: Boolean = false

		@LatticeOption(
			title = "Hide List When",
			description = "Whether to hide the list automatically based on one of two conditions.\nThis is recommended to improve performance."
		)
		@LatticeWidgetDropdown
		var hideListWhen: HideListType = HideListType.SEARCH

		@LatticeOption(
			title = "Hide Vanilla Items",
		)
		@LatticeWidgetButton
		var hideVanillaItems: Boolean = false

		@LatticeOption(
			title = "Group Families",
			description = "Group similar items to a parent item"
		)
		@LatticeWidgetButton
		var groupFamilies: Boolean = false

		@LatticeOption(
			title = "Prioritize Prefix Matches",
			description = "Show items starting with the search query first, followed by items containing the query in the middle."
		)
		@LatticeWidgetButton
		var prioritizePrefixMatches: Boolean = true

		@Suppress("unused", "PropertyName")
		@LatticeOption(
			title = "Configure Custom Filter",
			description = "Set what types of items to include in your custom filter."
		)
		@LatticeWidgetCustom(function = "openFilterScreen")
		@Transient
		var _customFilterButton: Int = 0

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
			description = "This can be changed in-game by holding Control/Command and scrolling on the Favorites List."
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

		@LatticeOption(
			title = "Replace Calculation on Enter",
			description = "Whether pressing the Enter key should replace the calculation with the result."
				+ "\nOnly works if the calculation is successful.",
		)
		@LatticeWidgetButton
		var replaceWithEnter: Boolean = true

		@Suppress("unused", "PropertyName")
		@LatticeOption(
			title = "Configure Custom Constants",
			description = "Add custom constants which can be used in the calculator."
		)
		@LatticeWidgetCustom(function = "openCustomConstantsScreen")
		@Transient
		var _customFilterButton: Int = 0

		var customConstants: Map<String, Double> = mutableMapOf()
	}

	@Suppress("unused")
	class KeybindSettings {
		@LatticeOption(
			title = "Hide Overlay",
			description = "Hides the Item List and Favorites List."
		)
		@LatticeWidgetKeybind(allowModifiers = true)
		@Transient
		val hideOverlay = LatticeKeybindInterface(Keybinds.hideOverlay)

		@LatticeOption(title = "View Recipe", description = "Shows the recipes of the hovered item, if there are any.")
		@LatticeWidgetKeybind(allowModifiers = true)
		@Transient
		val viewRecipe = LatticeKeybindInterface(Keybinds.viewRecipe)


		@LatticeOption(title = "View Usage", description = "Shows the uses of the hovered item, if there are any.")
		@LatticeWidgetKeybind(allowModifiers = true)
		@Transient
		val viewUsage = LatticeKeybindInterface(Keybinds.viewUsage)

		@LatticeOption(
			title = "Return to Previous Recipe",
			description = "While in a recipe screen, pressing this allows you to go back to the previous recipe screen."
		)
		@LatticeWidgetKeybind(allowModifiers = true)
		@Transient
		val previousRecipe = LatticeKeybindInterface(Keybinds.previousRecipe)

		@LatticeOption(title = "Favorite Item", description = "Adds the hovered item or recipe to your Favorites List.")
		@LatticeWidgetKeybind(allowModifiers = true)
		@Transient
		val favoriteItem = LatticeKeybindInterface(Keybinds.favoriteItem)

		@LatticeOption(title = "Jump to Search", description = "Focuses the Search Bar when the overlay is open.")
		@LatticeWidgetKeybind(allowModifiers = true)
		@Transient
		val focusSearch = LatticeKeybindInterface(Keybinds.focusSearch)
	}

	companion object {
		@Suppress("unused") // used in MainListSettings annotation
		@JvmStatic
		fun openFilterScreen(supplier: Supplier<Int>, consumer: Consumer<Int>): WidgetFunction {
			return WidgetFunction.runnableButton {
				McClient.setScreen(ConfigureFilterScreen(McScreen.self))
			}
		}

		@Suppress("unused") // used in CalculatorSettings annotation
		@JvmStatic
		fun openCustomConstantsScreen(supplier: Supplier<Int>, consumer: Consumer<Int>): WidgetFunction {
			return WidgetFunction.runnableButton {
				McClient.setScreen(CustomConstantsScreen(McScreen.self))
			}
		}
	}
}
