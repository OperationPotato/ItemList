package com.operationpotato.itemlist.gui.recipe

import com.mojang.blaze3d.platform.InputConstants
import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.AbiphoneCallnames
import com.operationpotato.itemlist.utils.RepoLibUtils.getInputItemStacks
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo
import com.operationpotato.itemlist.utils.Utils.topLeftAlignment
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.network.chat.Component
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
		val npcItem = SkyBlockMobsRepo.getItemStackOrDefault(recipe.npc)
		val callTooltip = if (AbiphoneCallnames.getCallname(npcItem) != null) {
			listOf(Component.empty(), Component.literal("Click to call this NPC!").withStyle(ChatFormatting.BLUE))
		} else {
			emptyList()
		}

		val npcDisplay = IngredientDisplay(
			npcItem,
			clickOverride = if (callTooltip.isNotEmpty()) {
				{ event ->
					event.button() == InputConstants.MOUSE_BUTTON_LEFT && AbiphoneCallnames.call(npcItem) }
			} else {
				null
			},
			extraTooltip = callTooltip,
		)
		container.addChild(npcDisplay, container.topLeftAlignment(103, 16))

		addRequirementsArrow(100, 35)

		container.arrangeElements()
	}
}
