package com.operationpotato.itemlist.api;

import com.operationpotato.itemlist.api.supportedscreen.SupportedScreenManager;
import org.jetbrains.annotations.ApiStatus;

public interface Plugin {
	/**
	 * Exclusion zones are areas of the screen where items from the list will not be placed.<br>
	 * Use this to prevent overlapping with your widgets.<br>
	 * To completely hide the overlay on a screen, see {@link Plugin#registerExcludedScreens}.
	 */
	default void registerExclusionZones(ExclusionZoneManager exclusionZoneManager) {}

	/**
	 * Allows hiding the list on certain screens.
	 */
	default void registerExcludedScreens(ExcludedScreensManager excludedScreensManager) {}

	/**
	 * Allows receiving item {@link net.minecraft.client.input.KeyEvent KeyEvent}s on items.
	 * Allows providing items from custom slots for item list keybinds to work.
	 */
	default void registerHoveredItems(HoveredItemManager hoveredItemManager) {}

	/**
	 * Allows adding custom buttons on recipes.
	 */
	default void registerRecipeButtons(RecipeButtonManager manager) {}

	/**
	 * Allows rendering the item list on custom screens with its boundary.
	 */
	@ApiStatus.Experimental
	default void registerSupportedScreens(SupportedScreenManager manager) {}
}
