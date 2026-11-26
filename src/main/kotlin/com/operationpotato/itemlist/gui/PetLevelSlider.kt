package com.operationpotato.itemlist.gui

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.math.roundToInt

/**
 * Compact level picker used by recipe screens: drag the handle for big jumps,
 * scroll over it for single levels.
 */
class PetLevelSlider(
	maxLevel: Int,
	level: Int,
	private val onChange: (Int) -> Unit,
) : AbstractWidget(0, 0, WIDTH, HEIGHT, Component.empty()) {

	private val levels = maxLevel.coerceAtLeast(1)
	private var dragging = false

	var level = level.coerceIn(1, levels)
		private set

	override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		val active = dragging || isHovered
		val trackY = y + (height - TRACK_HEIGHT) / 2
		graphics.fill(x, trackY, x + TRACK_WIDTH, trackY + TRACK_HEIGHT, if (active) ACTIVE_COLOR else TRACK_COLOR)

		val progress = (level - 1).toDouble() / (levels - 1)
		val handleX = x + (progress * (TRACK_WIDTH - HANDLE_WIDTH)).roundToInt()
		graphics.fill(handleX, y, handleX + HANDLE_WIDTH, y + height, if (active) ACTIVE_COLOR else HANDLE_COLOR)

		graphics.centeredText(
			McFont.self,
			Text.of("Lvl $level"),
			x + TRACK_WIDTH + LABEL_GAP + LABEL_WIDTH / 2,
			y + (height - McFont.self.lineHeight) / 2,
			if (active) ACTIVE_COLOR else LABEL_COLOR,
		)
	}

	override fun isValidClickButton(info: MouseButtonInfo): Boolean {
		return info.button() == InputConstants.MOUSE_BUTTON_LEFT
	}

	override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
		if (!isValidClickButton(event.buttonInfo())) return
		if (event.x > x + TRACK_WIDTH) return
		dragging = true
		setLevelFromMouse(event.x)
	}

	override fun mouseDragged(event: MouseButtonEvent, dragX: Double, dragY: Double): Boolean {
		if (!dragging) return false
		setLevelFromMouse(event.x)
		return true
	}

	override fun mouseReleased(event: MouseButtonEvent): Boolean {
		if (!dragging) return false
		dragging = false
		return true
	}

	override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
		if (scrollY == 0.0 || !isMouseOver(x, y)) return false
		updateLevel(level + if (scrollY > 0) 1 else -1)
		return true
	}

	private fun setLevelFromMouse(mouseX: Double) {
		val progress = ((mouseX - x) / (TRACK_WIDTH - HANDLE_WIDTH)).coerceIn(0.0, 1.0)
		updateLevel((progress * (levels - 1)).roundToInt() + 1)
	}

	private fun updateLevel(newLevel: Int) {
		val clamped = newLevel.coerceIn(1, levels)
		if (clamped == level) return
		level = clamped
		onChange(clamped)
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}

	companion object {
		const val WIDTH = 110
		const val HEIGHT = 12

		private const val TRACK_WIDTH = 68
		private const val TRACK_HEIGHT = 2
		private const val HANDLE_WIDTH = 4
		private const val LABEL_GAP = 4
		private const val LABEL_WIDTH = 38

		private const val TRACK_COLOR = CommonColors.GRAY
		private const val HANDLE_COLOR = CommonColors.DARK_GRAY
		private const val LABEL_COLOR = CommonColors.DARK_GRAY
		private const val ACTIVE_COLOR = CommonColors.BLUE
	}
}
