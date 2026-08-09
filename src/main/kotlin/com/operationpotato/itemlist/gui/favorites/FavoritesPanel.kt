package com.operationpotato.itemlist.gui.favorites

import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.AbstractItemList
import com.operationpotato.itemlist.gui.AbstractItemPanel
import com.operationpotato.itemlist.gui.AbstractPagedListScreen
import com.operationpotato.itemlist.gui.recipe.AbstractRecipeWidget
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.input.KeyEvent
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.right
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import java.util.*

class FavoritesPanel(x: Int, y: Int, width: Int, height: Int) : AbstractItemPanel(x, y, width, height) {
	val listWidget = FavoritesListWidget(width - AbstractItemList.PADDING, height)
	var recipeWidget: AbstractRecipeWidget? = null

	init {
		listWidget.itemSize = ConfigManager.get().favoritesList.favoritesItemSize
		val pinnedRecipe = FavoritesManager.favorites.pinnedRecipe
		setRecipe(pinnedRecipe)
	}

	override fun updatePosition() {
		val recipe = recipeWidget
		if (recipe != null && width != 0) {
			if (width > recipe.width) {
				recipe.setPosition(x + (width - recipe.width) / 2, 5)
			} else if (width > 3 * recipe.width / 4) {
				recipe.setPosition(x + (width - recipe.width) + 4, 5)
			} else {
				Text.of("Your screen is too small to pin this recipe!").withColor(TextColor.RED).send()
				removeRecipe()
				return
			}
		}
		val listWidgetY = recipeWidget?.bottom ?: y
		listWidget.setPosition(x, listWidgetY)
		listWidget.setSize(width - 2, height - listWidgetY - 20)
		listWidget.scaleChildren()
		listWidget.updatePositionsAsync()
	}

	override fun children(): List<GuiEventListener> = listOfNotNull(listWidget, recipeWidget)
	override fun getListWidget(): AbstractItemList = listWidget

	override fun keyPressed(event: KeyEvent): Boolean {
		if (!this.visible) return false
		recipeWidget?.let {
			if (it.isHovered && it.keyPressed(event)) return true
		}
		return listWidget.keyPressed(event)
	}

	override fun updateWidth() {
		val screen = McScreen.self ?: return
		val customRightBound = PluginManager.getScreenRightBound(screen, screen.width, screen.height)

		val rightBound = when {
			customRightBound != null -> customRightBound
			screen is AbstractContainerScreen<*> -> screen.right
			screen is AbstractPagedListScreen<*> -> screen.getRight()
			else -> return
		}

		val availableWidth = screen.width - rightBound
		val panelWidth = (availableWidth * ConfigManager.get().general.maxWidth).toInt()
		x = 0
		width = panelWidth - 2
		updatePosition()
	}

	override fun removed() {
		ConfigManager.get().favoritesList.favoritesItemSize = listWidget.itemSize
	}

	fun setRecipe(recipe: Optional<Recipe<*>>) {
		FavoritesManager.favorites.pinnedRecipe = recipe
		recipeWidget =
			if (recipe != recipeWidget?.recipe && !recipe.isEmpty) AbstractPagedListScreen.getWidgetForRecipe(recipe.get()) else null
		updatePosition()
	}

	fun setRecipe(recipe: Recipe<*>) = setRecipe(Optional.ofNullable(recipe))

	fun removeRecipe() {
		FavoritesManager.favorites.pinnedRecipe = Optional.empty()
		recipeWidget = null
		updatePosition()
	}

	override fun extractWidgetRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		recipeWidget?.extractRenderState(graphics, mouseX, mouseY, a)
		listWidget.extractRenderState(graphics, mouseX, mouseY, a)
	}
}
