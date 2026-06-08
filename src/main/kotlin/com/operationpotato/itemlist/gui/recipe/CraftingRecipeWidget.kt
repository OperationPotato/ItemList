package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.Utils.topLeftAlignment
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe

class CraftingRecipeWidget(recipe: CraftingRecipe) : AbstractRecipeWidget(recipe, 176, 86, "Crafting Recipe") {

	init {
		container.addChild(ImageWidget.sprite(176, 86, SkyBlockItemList.id("recipe/crafting")))

		addExtra()

		val grid = GridLayout()
		grid.spacing(2)

		recipe.inputs.forEachIndexed { index, ingredient ->
			val stack = ingredient.toItem() ?: ItemStack.EMPTY
			val element = if (!stack.isEmpty) {
				IngredientDisplay(stack)
			} else {
				SpacerElement(IngredientDisplay.STACK_SIZE, IngredientDisplay.STACK_SIZE)
			}
			grid.addChild(element, index / 3, index % 3)
		}

		grid.addChild(SpacerElement(38, 0), 1, 3)

		val outputStack = recipe.result.toItem() ?: ItemStack.EMPTY
		if (!outputStack.isEmpty) {
			grid.addChild(IngredientDisplay(outputStack), 1, 4)
		}

		container.addChild(grid, container.topLeftAlignment(30, 17))

		container.arrangeElements()
	}
}
