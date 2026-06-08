package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.RepoLibUtils.getInputItemStacks
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.Utils.topLeftAlignment
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe

class ShopRecipeWidget(recipe: ShopRecipe) : AbstractRecipeWidget(recipe, 176, 86, "Shop Recipe") {

	init {
		container.addChild(ImageWidget.sprite(176, 86, SkyBlockItemList.id("recipe/shop")))

		addExtra()

		val inputGrid = GridLayout()
		inputGrid.spacing(2)

		recipe.getInputItemStacks().forEachIndexed { index, stack ->
			val element = if (!stack.isEmpty) {
				IngredientDisplay(stack)
			} else {
				SpacerElement(StackDisplay.STACK_SIZE, StackDisplay.STACK_SIZE)
			}
			inputGrid.addChild(element, index / 4, index % 4)
		}

		container.addChild(inputGrid, container.topLeftAlignment(22, 17))

		val outputStack = recipe.result.toItem() ?: ItemStack.EMPTY
		if (!outputStack.isEmpty) {
			container.addChild(IngredientDisplay(outputStack), container.topLeftAlignment(134, 35))
		}

		container.arrangeElements()
	}
}
