package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.SkyBlockItemList.logger
import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.utils.SkyBlockRecipeAPI
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.ScrollableLayout
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.jvm.optionals.getOrNull

class RecipeScreen(val parent: Screen?, val recipes: List<AbstractRecipeWidget>) : Screen(Text.of("Recipe Screen")) {
	var isOversized = false

	override fun init() {
		super.init()

		var layout: Layout = LinearLayout.vertical().spacing(10).apply { recipes.forEach { addChild(it) } }
		isOversized = recipes.sumOf { it.height + 10 } > height
		if (isOversized) layout = ScrollableLayout(McClient.self, layout, height)
		layout.apply {
			arrangeElements()
			FrameLayout.centerInRectangle(this, this@RecipeScreen.rectangle)
		}.visitWidgets(this::addRenderableWidget)
	}

	override fun onClose() {
		if (parent is RecipeScreen) {
			parent.onClose()
		} else {
			McClient.setScreen(parent)
		}
	}

	override fun isInGameUi() = true

	fun getRight(): Int {
		var right = 0
		recipes.forEach {
			if (it.right > right) right = it.right
		}
		if (isOversized) right += 10
		return right
	}

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		if (Keybinds.previousRecipe.matchesMouse(event)) {
			McClient.setScreen(parent)
			return true
		}
		return super.mouseClicked(event, doubleClick)
	}

	override fun keyPressed(event: KeyEvent): Boolean {
		if (Keybinds.previousRecipe.matches(event)) {
			McClient.setScreen(parent)
			return true
		} else if (this.minecraft.options.keyInventory.matches(event)) {
			this.onClose()
			return true
		}
		val mousePos = McClient.mouse
		var child = getChildAt(mousePos.first, mousePos.second).getOrNull()
		// get the real child from the scrollable layout container
		if (child is AbstractContainerWidget) child = child.getChildAt(mousePos.first, mousePos.second).getOrNull()
		var stack: ItemStack? = null
		if (child is AbstractRecipeWidget) {
			if (child.keyPressed(event)) return true
			child.visitItems {
				if (it is IngredientDisplay && it.isHovered) stack = it.stack
			}
		}
		if (stack != null) {
			if (PluginManager.provideHoveredItem(stack, event)) return true
			if (Keybinds.handleKeybind(stack, event)) return true
		}
		return super.keyPressed(event)
	}

	companion object {
		fun openRecipeForItem(stack: ItemStack, parent: Screen? = null) {
			val targetId = stack.getSkyBlockId() ?: return

			val matchingRecipes = SkyBlockRecipeAPI.getRecipesForId(targetId)
			if (matchingRecipes.isNotEmpty()) {
				openRecipe(matchingRecipes, parent)
			}
		}

		fun openUsageForItem(stack: ItemStack, parent: Screen? = null) {
			val targetId = stack.getSkyBlockId() ?: return

			val matchingRecipes = SkyBlockRecipeAPI.getUsagesForId(targetId)
			if (matchingRecipes.isNotEmpty()) {
				openRecipe(matchingRecipes, parent)
			}
		}

		fun getWidgetForRecipe(it: Recipe<*>): AbstractRecipeWidget? = when (it) {
			is CraftingRecipe -> CraftingRecipeWidget(it)
			is ForgeRecipe -> ForgeRecipeWidget(it)
			is KatRecipe -> KatRecipeWidget(it)
			is ShopRecipe -> ShopRecipeWidget(it)
			else -> {
				logger.warn("[SkyBlock Item List] Unknown recipe ${it::class.simpleName}")
				null
			}
		}

		fun openRecipe(recipes: Set<Recipe<*>>, parent: Screen? = null) {
			val widgets = recipes.mapNotNull {
				getWidgetForRecipe(it)
			}.takeUnless { it.isEmpty() } ?: return

			McClient.setScreen(RecipeScreen(parent, widgets))
		}
	}
}
