package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList.logger
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.AbstractPagedListScreen
import com.operationpotato.itemlist.gui.loottable.LootTableScreen
import com.operationpotato.itemlist.gui.loottable.MobLootWidget
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo.getMobId
import com.operationpotato.itemlist.utils.SkyBlockRecipeAPI
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

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
