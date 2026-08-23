package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.utils.Utils.bottom
import com.operationpotato.itemlist.utils.Utils.right
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.ARGB
import net.minecraft.util.CommonColors

class ExclusionZoneDebugWidget : AbstractWidget(0, 0, 0, 0, Component.empty()) {
	val color = ARGB.multiplyAlpha(CommonColors.RED, 0.5f)

	override fun extractWidgetRenderState(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		PluginManager.getExclusionZones().forEach {
			val area = it.area
			graphics.fill(area.x, area.y, area.right, area.bottom, color)
		}
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}
}
