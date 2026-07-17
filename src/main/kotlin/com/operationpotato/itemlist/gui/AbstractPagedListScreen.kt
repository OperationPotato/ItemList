package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.SkyBlockItemList.logger
import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.favorites.FavoritesManager
import com.operationpotato.itemlist.gui.loottable.LootTableScreen
import com.operationpotato.itemlist.gui.loottable.MobLootWidget
import com.operationpotato.itemlist.gui.recipe.AbstractRecipeWidget
import com.operationpotato.itemlist.gui.recipe.CraftingRecipeWidget
import com.operationpotato.itemlist.gui.recipe.ForgeRecipeWidget
import com.operationpotato.itemlist.gui.recipe.KatRecipeWidget
import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import com.operationpotato.itemlist.gui.recipe.ShopRecipeWidget
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo.getMobId
import com.operationpotato.itemlist.utils.SkyBlockRecipeAPI
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.PageButton
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component
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

abstract class AbstractPagedListScreen<T : AbstractWidget>(
	val parent: Screen?,
	title: Component,
	val items: List<T>,
	val pageIndex: Int = 0
) : Screen(title) {

	protected var visibleItems: List<T> = listOf()
	protected var pageAmount: Int = 0

	protected val prevPageButton: Button = PageButton(0, 0, false, { _ ->
		goBackward()
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)

	protected val nextPageButton: Button = PageButton(0, 0, true, { _ ->
		goForward()
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)

	protected var topLayout: LinearLayout = LinearLayout.horizontal()

	protected open val contentAllowedSize: Int
		get() = height / 5 * 4

	override fun init() {
		super.init()

		val pages = mutableListOf(mutableListOf<T>())

		items.forEach { item ->
			if (pages.last().sumOf { it.height + 5 } + item.height > contentAllowedSize) {
				pages.add(mutableListOf(item))
			} else {
				pages.last().add(item)
			}
		}

		pageAmount = pages.size - 1
		val safePageIndex = pageIndex.coerceIn(0, pageAmount)
		visibleItems = pages[safePageIndex]

		val layout = LinearLayout.vertical().spacing(5)

		if (pageAmount != 0) {
			topLayout = LinearLayout.horizontal()
			topLayout.addChild(prevPageButton) { it.alignHorizontallyLeft() }
			//@formatter:off
			topLayout.addChild(SpacerTextWidget(
				getTopLayoutWidth() - 46,
				Text.of("${safePageIndex + 1} / ${pages.size}"),
				font
			))
			//@formatter:on
			topLayout.addChild(nextPageButton) { it.alignHorizontallyRight() }
			topLayout.arrangeElements()
			layout.addChild(topLayout)
		}

		buildContent(layout, visibleItems)

		layout.arrangeElements()
		FrameLayout.centerInRectangle(layout, this.rectangle)
		layout.visitWidgets(this::addRenderableWidget)
	}

	abstract fun getTopLayoutWidth(): Int
	abstract fun buildContent(layout: LinearLayout, visibleItems: List<T>)
	abstract fun createScreen(newPageIndex: Int): Screen
	abstract fun handleChildKeyPress(child: AbstractWidget, event: KeyEvent): Boolean
	abstract fun handleChildMouseClick(child: AbstractWidget, event: MouseButtonEvent, doubleClick: Boolean): Boolean
	abstract fun getHoveredStack(child: AbstractWidget): ItemStack?

	override fun onClose() {
		if (parent is AbstractPagedListScreen<*>) {
			parent.onClose()
		} else {
			McClient.setScreen(parent)
		}
	}

	override fun isInGameUi() = true

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		if (Keybinds.previousRecipe.matchesMouse(event)) {
			McClient.setScreen(parent)
			return true
		}

		var child = getChildAt(event.x, event.y).getOrNull()
		if (child is AbstractContainerWidget) child = child.getChildAt(event.x, event.y).getOrNull()

		var stack: ItemStack? = null
		if (child is AbstractWidget) {
			if (handleChildMouseClick(child, event, doubleClick)) return true
			stack = getHoveredStack(child)
		}

		if (stack != null) {
			if (PluginManager.provideHoveredItem(stack, event)) return true
			if (Keybinds.handleKeybind(stack, event)) return true
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
		if (child is AbstractContainerWidget) child = child.getChildAt(mousePos.first, mousePos.second).getOrNull()

		var stack: ItemStack? = null
		if (child is AbstractWidget) {
			if (handleChildKeyPress(child, event)) return true
			stack = getHoveredStack(child)
		}

		if (stack != null) {
			if (PluginManager.provideHoveredItem(stack, event)) return true
			if (Keybinds.handleKeybind(stack, event)) return true
		}

		return super.keyPressed(event)
	}

	override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
		if (super.mouseScrolled(x, y, scrollX, scrollY)) return true
		if (pageAmount != 0 && x >= topLayout.x && x <= topLayout.x + topLayout.width) {
			if (scrollY < 0) goForward() else goBackward()
			return true
		}
		return false
	}

	protected fun goForward() {
		McClient.setScreen(createScreen(if (pageIndex != pageAmount) pageIndex + 1 else 0))
	}

	protected fun goBackward() {
		McClient.setScreen(createScreen(if (pageIndex != 0) pageIndex - 1 else pageAmount))
	}

	open fun getLeft(): Int = visibleItems.minOf { it.x }
	open fun getRight(): Int = visibleItems.maxOf { it.right }
	open fun getMaxWidth(): Int = visibleItems.maxOf { it.width }

	companion object {
		fun handleMob(stack: ItemStack, parent: Screen? = null): Boolean {
			val mobId = stack.getMobId()
			if (mobId != null) {
				val mob = SkyBlockMobsRepo.get(mobId)
				if (mob != null && mob.lootTables.isNotEmpty()) {
					val widgets = mob.lootTables.map { MobLootWidget(mob, it) }
					McClient.setScreen(LootTableScreen(parent, widgets))
				} else {
					Text.of("No loot tables found for ") {
						color = TextColor.RED
						append(stack.cleanName, TextColor.LIGHT_PURPLE)
						append("!")
					}.send()
				}
				return true
			}
			return false
		}

		fun openRecipeForItem(stack: ItemStack, parent: Screen? = null) {
			if (handleMob(stack, parent)) return
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
			if (handleMob(stack, parent)) return
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
