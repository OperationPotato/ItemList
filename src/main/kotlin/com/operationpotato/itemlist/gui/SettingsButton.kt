package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.SkyBlockItemList
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.text.Text

class SettingsButton(x: Int, y: Int, onPress: OnPress) : Button(x, y, 16, 16, Text.of("Settings Button"), onPress, DEFAULT_NARRATION) {
	override fun extractContents(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		this.extractDefaultSprite(graphics)
		graphics.blitSprite(RenderPipelines.GUI_TEXTURED, COG, x, y, width, height)
	}

	companion object {
		val COG: Identifier = SkyBlockItemList.id("cog")
	}
}
