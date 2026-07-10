package com.operationpotato.itemlist.gui.loottable

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.recipe.IngredientDisplay
import com.operationpotato.itemlist.utils.RepoLibUtils.toItemStack
import com.operationpotato.itemlist.utils.Utils.topLeftAlignment
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.input.MouseButtonInfo
import net.minecraft.util.CommonColors
import tech.thatgravyboat.repolib.api.mobs.LootTable
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.math.max

class MobLootWidget(val mob: Mob, lootTable: LootTable, width: Int = 176) :
	AbstractWidget(0, 0, width, calculateHeight(lootTable), Text.of(lootTable.name)) {
	private val container = FrameLayout(0, 0, width, height)

	init {
		container.addChild(ImageWidget.sprite(width, height, SkyBlockItemList.id("recipe/items")))

		val title = if (lootTable.mobLevel == 0) lootTable.name
		else "[Lvl ${lootTable.mobLevel}] ${lootTable.name}"
		container.addChild(
			StringWidget(Text.of(title, CommonColors.DARK_GRAY).apply { withoutShadow() }, McFont.self),
			container.newChildLayoutSettings()
				.alignHorizontallyCenter()
				.alignVerticallyTop()
				.paddingTop(5)
		)

		val grid = GridLayout().apply { spacing(2) }
		var index = 0

		lootTable.drops.forEach { drop ->
			val stack = drop.toItemStack()
			if (!stack.isEmpty) {
				grid.addChild(IngredientDisplay(stack), index / 7, index % 7)
				index++
			}
		}

		container.addChild(grid, container.topLeftAlignment(26, 17))
		container.arrangeElements()
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
		container.visitWidgets { widget -> widget.extractRenderState(graphics, mouseX, mouseY, a) }
	}

	override fun isValidClickButton(info: MouseButtonInfo): Boolean {
		return info.button() == 0 || info.button() == 1
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

	override fun keyPressed(event: KeyEvent): Boolean {
		var handled = false
		container.visitWidgets { widget ->
			if (widget.isHovered && widget.keyPressed(event)) {
				handled = true
			}
		}
		return handled
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}

	companion object {
		private fun calculateHeight(lootTable: LootTable): Int {
			val validDropsCount = lootTable.drops.count { !it.toItemStack().isEmpty }
			val rows = max(1, (validDropsCount + 6) / 7)
			return 32 + (rows * 18)
		}
	}
}
