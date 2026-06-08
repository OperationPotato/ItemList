package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.RepoLibUtils.getInputItemStacks
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.Utils.formatDuration
import com.operationpotato.itemlist.utils.Utils.topLeftAlignment
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

class ForgeRecipeWidget(recipe: ForgeRecipe) : AbstractRecipeWidget(recipe, 176, 86, "Forge Recipe") {

	init {
		// TODO: switch to a 3x3 input field
		container.addChild(ImageWidget.sprite(176, 86, SkyBlockItemList.id("recipe/forge")))

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

		val timeStack = Items.CLOCK.defaultInstance.apply {
			set(DataComponents.CUSTOM_NAME, Text.of("Forge Duration") {
				color = TextColor.GREEN
				italic = false
			})
			val lore = listOf(Text.of("Time: ${recipe.time.formatDuration()}", TextColor.GRAY))
			set(DataComponents.LORE, ItemLore(lore, lore))
		}
		container.addChild(IngredientDisplay(timeStack), container.topLeftAlignment(103, 52))

		val outputStack = recipe.result.toItem() ?: ItemStack.EMPTY
		if (!outputStack.isEmpty) {
			container.addChild(IngredientDisplay(outputStack), container.topLeftAlignment(134, 35))
		}

		container.arrangeElements()
	}
}
