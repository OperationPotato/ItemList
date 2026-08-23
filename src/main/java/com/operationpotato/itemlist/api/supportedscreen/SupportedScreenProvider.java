package com.operationpotato.itemlist.api.supportedscreen;

import net.minecraft.client.gui.screens.Screen;

import java.util.Optional;

public interface SupportedScreenProvider<T extends Screen> {
	Optional<ScreenBounds> getBounds(T screen, int screenWidth, int screenHeight);
}
