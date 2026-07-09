package com.operationpotato.itemlist.gui.config

import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.gui.AbstractPopupScreen
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

class CustomConstantsScreen(parent: Screen?) : AbstractPopupScreen(Text.of("Custom Constants"), parent) {

	private class Constant(var key: String, var value: String)

	private val constants = ConfigManager.get().calculator.customConstants.map {
		Constant(it.key, it.value.toString())
	}.toMutableList()

	override fun createLayout(): Layout {
		val layout = LinearLayout.vertical().spacing(4)
		layout.addChild(StringWidget(Text.of("Customize Constants"), McFont.self))
		layout.addChild(SpacerElement.height(8))

		val mainLayout = layout.addChild(LinearLayout.horizontal()).spacing(6)
		val nameColumn = mainLayout.addChild(LinearLayout.vertical()).spacing(4)
		val valuesColumn = mainLayout.addChild(LinearLayout.vertical()).spacing(4)
		val deleteColumn = mainLayout.addChild(LinearLayout.vertical()).spacing(4)

		for ((index, constant) in constants.withIndex()) {
			nameColumn.addChild(EditBox(McFont.self, 100, 20, Text.of("Key")).apply {
				value = constant.key
				setResponder { constant.key = it }
			})

			valuesColumn.addChild(EditBox(McFont.self, 100, 20, Text.of("Value")).apply {
				value = constant.value
				setResponder { text ->
					constant.value = text
					setTextColor(if (text.toDoubleOrNull() != null) CommonColors.WHITE else CommonColors.SOFT_RED)
				}
			})

			deleteColumn.addChild(Button.builder(Text.of("X", TextColor.RED)) {
				constants.removeAt(index)
				rebuildWidgets()
			}.size(20, 20).build())
		}

		layout.addChild(SpacerElement.height(8))

		val fullWidth = 232

		layout.addChild(Button.builder(Text.of("+ Add Constant")) {
			constants.add(Constant("new_constant", "0.0"))
			rebuildWidgets()
		}.width(fullWidth).build())

		layout.addChild(Button.builder(CommonComponents.GUI_DONE) {
			save()
			onClose()
		}.width(fullWidth).build())

		layout.arrangeElements()
		FrameLayout.centerInRectangle(layout, this.rectangle)
		layout.visitWidgets(::addRenderableWidget)

		return layout
	}

	private fun save() {
		val constants = ConfigManager.get().calculator.customConstants as MutableMap<String, Double>
		constants.clear()

		for (entry in this.constants) {
			if (entry.key.isNotBlank()) {
				constants[entry.key] = entry.value.toDoubleOrNull() ?: 0.0
			}
		}

		ConfigManager.save()
	}
}
