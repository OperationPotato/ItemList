package com.operationpotato.itemlist.utils

import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.renderer.Rect2i
import java.text.NumberFormat

object Utils {

	val Rect2i.right: Int
		get() = this.x + this.width

	val Rect2i.bottom: Int
		get() = this.y + this.height

	fun Rect2i.overlaps(other: Rect2i): Boolean {
		return this.x < other.right && this.right > other.x
			&& this.y < other.bottom && this.bottom > other.y
	}

	fun FrameLayout.topLeftAlignment(x: Int = 0, y: Int = 0): LayoutSettings {
		return this.newChildLayoutSettings()
			.alignHorizontallyLeft()
			.alignVerticallyTop()
			.paddingLeft(x)
			.paddingTop(y)
	}

	fun Int.formatDuration(): String {
		if (this <= 0) return "0s"

		val days = this / 86400
		val hours = (this % 86400) / 3600
		val minutes = (this % 3600) / 60
		val seconds = this % 60

		return buildList {
			if (days > 0) add("${days}d")
			if (hours > 0) add("${hours}h")
			if (minutes > 0) add("${minutes}m")
			if (seconds > 0) add("${seconds}s")
		}.joinToString(", ")
	}

	/**
	 * Like .toFormattedString() just with way more possible decimal points.
	 */
	fun Number.toPreciseString(): String = NumberFormat.getNumberInstance().apply {
		// From testing 7 was the least weird, where no 0.004000001 etc happens
		maximumFractionDigits = 7
	}.format(this)
}
