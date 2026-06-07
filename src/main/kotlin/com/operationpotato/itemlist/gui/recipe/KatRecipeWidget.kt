package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.Currency
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
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

// TODO: maybe some ugly slider to add the pet level and adjust the coins (however that formula works) & output level
class KatRecipeWidget(recipe: KatRecipe) : AbstractRecipeWidget(recipe, 176, 86, "Kat Recipe") {

	init {
		val hasExtraItems = recipe.items.isNotEmpty()
		val texture = if (hasExtraItems) "recipe/kat_extra" else "recipe/kat"
		container.addChild(ImageWidget.sprite(176, 86, SkyBlockItemList.id(texture)))

		addExtra()

		val (inputX, inputY) = if (hasExtraItems) 20 to 35 else 35 to 35
		container.addChild(
			StackDisplay(recipe.input.toItem() ?: ItemStack.EMPTY, null),
			container.topLeftAlignment(inputX, inputY)
		)

		val (coinsX, coinsY) = if (hasExtraItems) 46 to 35 else 61 to 35
		container.addChild(
			StackDisplay(Currency.Coin.withAmount(recipe.coins), null),
			container.topLeftAlignment(coinsX, coinsY)
		)

		if (hasExtraItems) {
			val inputGrid = GridLayout()
			inputGrid.spacing(2)

			recipe.getInputItemStacks(withCoins = false).forEachIndexed { index, stack ->
				val element = if (!stack.isEmpty) {
					StackDisplay(stack, null, true)
				} else {
					SpacerElement(StackDisplay.STACK_SIZE, StackDisplay.STACK_SIZE)
				}
				inputGrid.addChild(element, index, 0)
			}

			container.addChild(inputGrid, container.topLeftAlignment(72, 17))
		}

		val timeStack = Items.CLOCK.defaultInstance.apply {
			set(DataComponents.CUSTOM_NAME, Text.of("Kat Duration") {
				color = TextColor.GREEN
				italic = false
			})
			val lore = listOf(Text.of("Time: ${recipe.time.formatDuration()}", TextColor.GRAY))
			set(DataComponents.LORE, ItemLore(lore, lore))
		}
		val (timeX, timeY) = if (hasExtraItems) 101 to 52 else 90 to 52
		container.addChild(StackDisplay(timeStack, null), container.topLeftAlignment(timeX, timeY))

		val outputStack = recipe.output.toItem() ?: ItemStack.EMPTY
		val (outputX, outputY) = if (hasExtraItems) 135 to 35 else 124 to 35
		if (!outputStack.isEmpty) {
			container.addChild(StackDisplay(outputStack, null, true), container.topLeftAlignment(outputX, outputY))
		}

		container.arrangeElements()
	}
}
