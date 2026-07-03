package com.operationpotato.itemlist.api.impl

import net.minecraft.client.renderer.Rect2i
import org.jetbrains.annotations.ApiStatus

@ApiStatus.Internal
data class ExclusionZone(
	val area: Rect2i
) {
	companion object {
		fun create(x: Int, y: Int, w: Int, h: Int): ExclusionZone {
			return ExclusionZone(Rect2i(x, y, w, h))
		}
	}
}
