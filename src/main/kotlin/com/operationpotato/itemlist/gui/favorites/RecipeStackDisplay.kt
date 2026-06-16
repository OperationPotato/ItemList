package com.operationpotato.itemlist.gui.favorites

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.platform.drawFilledBox
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

class RecipeStackDisplay(val recipe: Recipe<*>, lazyStack: LazyItemStack, type: SkyBlockItemCategory) : StackDisplay(lazyStack, type) {
	override fun getTooltipLines(stack: ItemStack): List<Component> {
		val lines = super.getTooltipLines(stack)
		val pinLine = Text.of("[CTRL+Click to pin this recipe]").withColor(TextColor.GRAY).apply { italic = false }
		(lines as ArrayList<Component>).add(pinLine)
		return lines
	}

	override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		// TODO: make this a sprite instead
		graphics.drawFilledBox(x, y, width, height, CommonColors.GREEN)
		super.extractWidgetRenderState(graphics, mouseX, mouseY, a)
	}

	override fun onClick(event: MouseButtonEvent, doubleClick: Boolean) {
		if (event.button() == 0) {
			if (event.hasControlDownWithQuirk()) {
				SkyBlockItemList.favoriteInstance?.setRecipe(recipe)
			} else {
				RecipeScreen.openRecipe(setOf(recipe), McScreen.self)
			}
		}
	}
}
