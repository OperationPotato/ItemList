package com.operationpotato.itemlist.gui.favorites

import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.AbstractItemList
import com.operationpotato.itemlist.gui.AbstractItemPanel
import com.operationpotato.itemlist.gui.recipe.AbstractRecipeWidget
import com.operationpotato.itemlist.gui.recipe.RecipeScreen
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
import java.util.Optional

class FavoritesPanel(x: Int, y: Int, width: Int, height: Int) : AbstractItemPanel(x, y, width, height) {
	val listWidget = FavoritesListWidget(width - AbstractItemList.PADDING, height)
	var recipeWidget: AbstractRecipeWidget? = null

	init {
		listWidget.itemSize = ConfigManager.get().favoritesItemSize
		val pinnedRecipe = FavoritesManager.favorites.pinnedRecipe
		setRecipe(pinnedRecipe)
	}

	override fun updatePosition() {
		val recipe = recipeWidget
		if (recipe != null && width != 0) {
			recipe.setPosition((width - recipe.width) / 2, 5)
			if (recipe.width > width) {
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
		val screen = McScreen.self
		if (screen !is AbstractContainerScreen<*>) return
		val availableWidth = screen.width - screen.right
		val panelWidth = (availableWidth * ConfigManager.get().maxWidth).toInt()
		x = 0
		width = panelWidth - 2
		updatePosition()
	}

	override fun removed() {
		ConfigManager.get().favoritesItemSize = listWidget.itemSize
	}

	fun setRecipe(recipe: Optional<Recipe<*>>) {
		FavoritesManager.favorites.pinnedRecipe = recipe
		recipeWidget =
			if (recipe != recipeWidget?.recipe && !recipe.isEmpty) RecipeScreen.getWidgetForRecipe(recipe.get()) else null
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
