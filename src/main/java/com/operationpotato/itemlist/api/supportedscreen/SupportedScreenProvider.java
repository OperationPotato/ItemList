package com.operationpotato.itemlist.api.supportedscreen;

import net.minecraft.client.gui.screens.Screen;

import java.util.OptionalInt;

public interface SupportedScreenProvider<T extends Screen> {
	OptionalInt getRightBound(T screen, int screenWidth, int screenHeight);
}
