package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Checkbox
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text

class ConfigureFilterScreen(parent: Screen?) : AbstractPopupScreen(Component.literal("Configure Filter"), parent) {

	override fun createLayout(): Layout {
		val allFilters = SkyBlockItemCategory.entries.filter {
			it != SkyBlockItemCategory.ALL && it != SkyBlockItemCategory.CUSTOM
		}
		val enabledFilters = ConfigManager.get().mainList.customFilters

		val layout = LinearLayout.vertical().spacing(4)
		layout.addChild(StringWidget(Text.of("Custom Filter"), McFont.self))
		layout.addChild(SpacerElement.height(8))

		val mainLayout = layout.addChild(LinearLayout.vertical()).spacing(4)
		for (filter in allFilters) {
			val checkBox = Checkbox.builder(filter.asComponent(), McFont.self)
				.selected(enabledFilters.contains(filter))
				.onValueChange { _, bool ->
					if (!bool) enabledFilters.remove(filter)
					else enabledFilters.add(filter)
				}.build()
			mainLayout.addChild(checkBox)
		}
		mainLayout.arrangeElements()

		layout.addChild(SpacerElement.height(8))
		val doneButton = Button.builder(CommonComponents.GUI_DONE) { _ -> onClose() }
			.width(mainLayout.width).build()
		layout.addChild(doneButton)

		layout.visitWidgets(::addRenderableWidget)
		layout.arrangeElements()
		FrameLayout.centerInRectangle(layout, this.rectangle)

		return mainLayout
	}
}
