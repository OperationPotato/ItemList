package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.gui.AbstractPagedListScreen
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text

class RecipeScreen(parent: Screen?, recipes: List<AbstractRecipeWidget>, pageIndex: Int = 0) :
	AbstractPagedListScreen<AbstractRecipeWidget>(parent, Text.of("Recipe Screen"), recipes, pageIndex) {

	override fun getTopLayoutWidth(): Int = getMaxWidth()

	override fun buildContent(layout: LinearLayout, visibleItems: List<AbstractRecipeWidget>) {
		visibleItems.forEach(layout::addChild)
	}

	override fun createScreen(newPageIndex: Int): Screen = RecipeScreen(parent, items, newPageIndex)

	override fun handleChildKeyPress(child: AbstractWidget, event: KeyEvent): Boolean {
		return child is AbstractRecipeWidget && child.keyPressed(event)
	}

	override fun handleChildMouseClick(child: AbstractWidget, event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		return child is AbstractRecipeWidget && child.mouseClicked(event, doubleClick)
	}

	override fun getHoveredStack(child: AbstractWidget): ItemStack? {
		var stack: ItemStack? = null
		if (child is AbstractRecipeWidget) {
			child.visitItems {
				if (it is IngredientDisplay && it.isHovered) stack = it.stack
			}
		}
		return stack
	}
}
