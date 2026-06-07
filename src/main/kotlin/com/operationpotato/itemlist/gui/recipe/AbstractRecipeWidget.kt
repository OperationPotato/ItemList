package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.api.impl.PluginManager
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.MouseButtonEvent
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

abstract class AbstractRecipeWidget(val recipe: Recipe<*>, width: Int, height: Int, val title: String? = null) :
	AbstractWidget(0, 0, width, height, Text.of(title ?: "Recipe Widget")) {

	protected val container = FrameLayout(0, 0, width, height)

	protected fun addExtra() {
		title?.let {
			container.addChild(
				StringWidget(Text.of(it, TextColor.GRAY), McFont.self),
				container.newChildLayoutSettings()
					.alignHorizontallyCenter()
					.alignVerticallyTop()
					.paddingTop(5)
			)
		}

		val buttons = PluginManager.getRecipeButtons(recipe)
		val verticalButtons = LinearLayout.vertical().spacing(5).apply {
			buttons.forEach { button -> addChild(button) }
		}
		container.addChild(
			verticalButtons,
			container.newChildLayoutSettings().alignVerticallyBottom().alignHorizontallyRight()
				.paddingBottom(5).paddingRight(5)
		)
	}

	override fun setX(x: Int) {
		super.setX(x)
		container.x = x
		container.arrangeElements()
	}

	override fun setY(y: Int) {
		super.setY(y)
		container.y = y
		container.arrangeElements()
	}

	override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		container.visitWidgets { widget ->
			widget.extractRenderState(graphics, mouseX, mouseY, a)
		}
	}

	override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
		var handled = false
		container.visitWidgets { widget ->
			if (widget.isHovered) {
				widget.onClick(event, doubleClick)
				handled = true
			}
		}

		if (!handled) {
			super.onClick(event, doubleClick)
		}
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}
}
