package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList.logger
import com.operationpotato.itemlist.utils.SkyBlockRecipeAPI
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text

class RecipeScreen(val parent: Screen?, val recipes: List<AbstractRecipeWidget>) : Screen(Text.of("Recipe Screen")) {

	override fun init() {
		super.init()

		LinearLayout.vertical().spacing(10).apply {
			recipes.forEach { addChild(it) }
			arrangeElements()
			FrameLayout.centerInRectangle(this, this@RecipeScreen.rectangle)
		}.visitWidgets(this::addRenderableWidget)
	}

	override fun onClose() {
		parent?.let { McClient.setScreen(parent) } ?: super.onClose()
	}

	override fun isInGameUi() = true

	companion object {
		fun openRecipeForItem(stack: ItemStack, parent: Screen? = null) {
			val targetId = stack.getSkyBlockId() ?: return

			val matchingRecipes = SkyBlockRecipeAPI.getRecipesForId(targetId)
			if (matchingRecipes.isNotEmpty()) {
				openRecipe(matchingRecipes, parent)
			}
		}

		fun openRecipe(recipes: List<Recipe<*>>, parent: Screen? = null) {
			val widgets = recipes.mapNotNull {
				when (it) {
					is CraftingRecipe -> CraftingRecipeWidget(it)
					is ForgeRecipe -> ForgeRecipeWidget(it)
					is KatRecipe -> KatRecipeWidget(it)
					is ShopRecipe -> ShopRecipeWidget(it)
					else -> {
						logger.warn("Unknown recipe ${it::class.simpleName}")
						null
					}
				}
			}.takeUnless { it.isEmpty() } ?: return

			McClient.setScreenAsync { RecipeScreen(parent, widgets) }
		}
	}
}
