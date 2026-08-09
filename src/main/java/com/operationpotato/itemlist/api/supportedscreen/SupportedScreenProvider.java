package com.operationpotato.itemlist.api.supportedscreen;

import net.minecraft.client.gui.screens.Screen;

public interface SupportedScreenProvider<T extends Screen> {
	int getRightBound(T screen, int screenWidth, int screenHeight);
}
