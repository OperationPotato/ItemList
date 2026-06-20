package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.SkyBlockItemList.logger
import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.SpacerTextWidget
import com.operationpotato.itemlist.utils.SkyBlockRecipeAPI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.PageButton
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
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.jvm.optionals.getOrNull

class RecipeScreen(val parent: Screen?, val recipes: List<AbstractRecipeWidget>, val pageIndex: Int = 0) : Screen(Text.of("Recipe Screen")) {

	var pageAmount: Int = 0

	val prevPageButton: Button = PageButton(0, 0, false, { _ ->
		goBackward()
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)
	val nextPageButton: Button = PageButton(0, 0, true, { _ ->
		goForward()
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)
	var topLayout: LinearLayout = LinearLayout.horizontal()

	override fun init() {
		super.init()

		val pages = mutableListOf(mutableListOf<AbstractRecipeWidget>())
		val allowedSize = this@RecipeScreen.height / 5 * 4

		recipes.forEach { recipe ->
			if (pages.last().sumOf { it.height + 5 } + recipe.height > allowedSize) {
				pages.add(mutableListOf(recipe))
			} else {
				pages.last().add(recipe)
			}
		}

		pageAmount = pages.size - 1
		val pageIndex = pageIndex.coerceIn(0, pageAmount)

		topLayout = LinearLayout.horizontal()
		val layout = LinearLayout.vertical().spacing(5).apply {
			topLayout.addChild(prevPageButton) { it.alignHorizontallyLeft() }
			topLayout.addChild(SpacerTextWidget(
				0,
				0,
				pages[pageIndex].maxBy { it.width }.width - 46,
				Text.of("${pageIndex + 1} / ${pages.size}"),
				font
			))
			topLayout.addChild(nextPageButton) { it.alignHorizontallyRight() }
			topLayout.arrangeElements()
			addChild(topLayout)

			pages[pageIndex].forEach(::addChild)
		}

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

	override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
		if (super.mouseScrolled(x, y, scrollX, scrollY)) return true
		if (x >= topLayout.x && x <= topLayout.x + topLayout.width) {
			if (scrollY < 0) goForward() else goBackward()
			return true
		}
		return false
	}

	fun goForward() {
		McClient.setScreen(RecipeScreen(parent, recipes, if (pageIndex != pageAmount) pageIndex + 1 else 0))
	}

	fun goBackward() {
		McClient.setScreen(RecipeScreen(parent, recipes, if (pageIndex != 0) pageIndex - 1 else pageAmount))
	}

	companion object {
		fun openRecipeForItem(stack: ItemStack, parent: Screen? = null) {
			val targetId = stack.getSkyBlockId() ?: return

			val matchingRecipes = SkyBlockRecipeAPI.getRecipesForId(targetId)
			if (matchingRecipes.isNotEmpty()) {
				openRecipe(matchingRecipes, parent)
			} else {
				Text.of("No recipes found for ") {
					color = TextColor.RED
					append(stack.cleanName, TextColor.LIGHT_PURPLE)
					append("!")
				}.send()
			}
		}

		fun openUsageForItem(stack: ItemStack, parent: Screen? = null) {
			val targetId = stack.getSkyBlockId() ?: return

			val matchingRecipes = SkyBlockRecipeAPI.getUsagesForId(targetId)
			if (matchingRecipes.isNotEmpty()) {
				openRecipe(matchingRecipes, parent)
			} else {
				Text.of("No usages found for ") {
					color = TextColor.RED
					append(stack.cleanName, TextColor.LIGHT_PURPLE)
					append("!")
				}.send()
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
			val widgets = recipes
				.sortedByDescending { FavoritesManager.isFavoriteRecipe(it) }
				.mapNotNull { getWidgetForRecipe(it) }
				.takeUnless { it.isEmpty() } ?: return

			McClient.setScreen(RecipeScreen(parent, widgets))
		}
	}
}
