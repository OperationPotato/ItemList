package com.operationpotato.itemlist.api.supportedscreen;

import net.minecraft.client.gui.screens.Screen;

public interface SupportedScreenManager {
	<T extends Screen> void addProvider(Class<T> screenClass, SupportedScreenProvider<T> provider);
}
