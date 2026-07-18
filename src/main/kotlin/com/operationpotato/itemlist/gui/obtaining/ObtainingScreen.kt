package com.operationpotato.itemlist.gui.obtaining

import com.operationpotato.itemlist.gui.AbstractPagedListScreen
import com.operationpotato.itemlist.gui.recipe.AbstractRecipeWidget
import com.operationpotato.itemlist.gui.recipe.IngredientDisplay
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text

class ObtainingScreen(parent: Screen?, widgets: List<AbstractWidget>, pageIndex: Int = 0) :
	AbstractPagedListScreen<AbstractWidget>(parent, Text.of("How to Obtain"), widgets, pageIndex) {

	override fun getTopLayoutWidth(): Int = getMaxWidth()

	override fun buildContent(layout: LinearLayout, visibleItems: List<AbstractWidget>) {
		visibleItems.forEach(layout::addChild)
	}

	override fun createScreen(newPageIndex: Int): Screen = ObtainingScreen(parent, items, newPageIndex)

	override fun handleChildKeyPress(child: AbstractWidget, event: KeyEvent): Boolean {
		return (child is AbstractRecipeWidget && child.keyPressed(event)) ||
			(child is MobDropWidget && child.keyPressed(event))
	}

	override fun getHoveredStack(child: AbstractWidget): ItemStack? {
		var stack: ItemStack? = null
		if (child is AbstractRecipeWidget) {
			child.visitItems { if (it is IngredientDisplay && it.isHovered) stack = it.stack }
		}
		return stack
	}
}
