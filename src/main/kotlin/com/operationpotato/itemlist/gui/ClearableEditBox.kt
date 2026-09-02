package com.operationpotato.itemlist.gui

import com.mojang.blaze3d.platform.InputConstants
import com.operationpotato.itemlist.ContainerSearcher
import com.operationpotato.itemlist.SkyBlockItemList.id
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

open class ClearableEditBox(font: Font, width: Int, height: Int, narration: Component) :
	EditBox(font, width, height, narration) {

	var isSearchingInventory: Boolean = false
		private set

	@Suppress("unused") // Used in EditBoxMixin
	fun getSprites() = WidgetSprites(id("clearable_edit_box/text_field"), id("clearable_edit_box/text_field_highlighted"))

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		if (!this.isActive) return false
		if (event.button() == InputConstants.MOUSE_BUTTON_LEFT && doubleClick) {
			isSearchingInventory = !isSearchingInventory
			if (isSearchingInventory) {
				ContainerSearcher.setSearch(value)
			} else {
				ContainerSearcher.setSearch(null)
			}
			return true
		}
		if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT) {
			value = ""
			return true
		}
		return super.mouseClicked(event, doubleClick)
	}
}
