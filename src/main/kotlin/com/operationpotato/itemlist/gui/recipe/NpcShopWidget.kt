package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.utils.ItemClickAction
import com.operationpotato.itemlist.utils.RepoLibUtils.toDisplayStack
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo
import com.operationpotato.itemlist.utils.SkyBlockRecipeAPI
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
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.math.max

class NpcShopWidget private constructor(npc: String, results: List<ItemStack>, width: Int) :
	AbstractWidget(0, 0, width, calculateHeight(results.size), Text.of("Sold By")) {

	constructor(npc: String, width: Int = 176) : this(npc, getResults(npc), width)

	private val container = FrameLayout(0, 0, width, height)

	init {
		container.addChild(ImageWidget.sprite(width, height, SkyBlockItemList.id("recipe/items")))

		val title = SkyBlockMobsRepo.get(npc)?.name ?: npc
		container.addChild(
			StringWidget(Text.of(title, CommonColors.DARK_GRAY).apply { withoutShadow() }, McFont.self),
			container.newChildLayoutSettings()
				.alignHorizontallyCenter()
				.alignVerticallyTop()
				.paddingTop(5)
		)

		val grid = GridLayout().apply { spacing(2) }
		results.forEachIndexed { index, stack ->
			grid.addChild(IngredientDisplay(stack), index / 7, index % 7)
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
		return info.button() in ItemClickAction.validButtons
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
		private fun getResults(npc: String): List<ItemStack> =
			SkyBlockRecipeAPI.getShopRecipesForNpc(npc).mapNotNull { it.toDisplayStack() }

		private fun calculateHeight(count: Int): Int {
			val rows = max(1, (count + 6) / 7)
			return 32 + (rows * 18)
		}
	}
}
