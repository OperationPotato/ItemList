package com.operationpotato.itemlist.gui.loottable

import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.gui.SpacerTextWidget
import com.operationpotato.itemlist.gui.recipe.IngredientDisplay
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
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.jvm.optionals.getOrNull

class LootTableScreen(val parent: Screen?, val widgets: List<MobLootWidget>, val pageIndex: Int = 0) :
	Screen(Text.of("Loot Tables")) {
	private var visibleWidgets: List<MobLootWidget> = listOf()
	private var pageAmount: Int = 0

	private val prevPageButton: Button = PageButton(0, 0, false, { _ ->
		goBackward()
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)

	private val nextPageButton: Button = PageButton(0, 0, true, { _ ->
		goForward()
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)

	private var topLayout: LinearLayout = LinearLayout.horizontal()

	override fun init() {
		super.init()
		val pages = mutableListOf(mutableListOf<MobLootWidget>())
		val allowedSize = this@LootTableScreen.height / 5 * 4

		widgets.forEach { widget ->
			if (pages.last().sumOf { it.height + 5 } + widget.height > allowedSize) {
				pages.add(mutableListOf(widget))
			} else {
				pages.last().add(widget)
			}
		}

		pageAmount = pages.size - 1
		val safePageIndex = pageIndex.coerceIn(0, pageAmount)
		visibleWidgets = pages[safePageIndex]

		topLayout = LinearLayout.horizontal()

		val layout = LinearLayout.vertical().spacing(5).apply {
			if (pageAmount == 0) return@apply
			topLayout.addChild(prevPageButton) { it.alignHorizontallyLeft() }
			//@formatter:off
			topLayout.addChild(SpacerTextWidget(
				getMaxWidth() - 46,
				Text.of("${safePageIndex + 1} / ${pages.size}"),
				font
			))
			//@formatter:on
			topLayout.addChild(nextPageButton) { it.alignHorizontallyRight() }
			topLayout.arrangeElements()
			addChild(topLayout)
		}

		layout.apply {
			visibleWidgets.forEach(::addChild)
			arrangeElements()
			FrameLayout.centerInRectangle(this, this@LootTableScreen.rectangle)
		}.visitWidgets(this::addRenderableWidget)
	}

	override fun onClose() {
		if (parent is LootTableScreen) {
			parent.onClose()
		} else {
			McClient.setScreen(parent)
		}
	}

	override fun isInGameUi() = true

	fun getLeft(): Int = visibleWidgets.minOf { it.x }
	fun getRight(): Int = visibleWidgets.maxOf { it.right }
	fun getMaxWidth(): Int = visibleWidgets.maxOf { it.width }

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
		if (child is AbstractContainerWidget) child = child.getChildAt(mousePos.first, mousePos.second).getOrNull()

		var stack: ItemStack? = null
		if (child is MobLootWidget) {
			if (child.keyPressed(event)) return true
			child.visitWidgets {
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

	private fun goForward() {
		McClient.setScreen(LootTableScreen(parent, widgets, if (pageIndex != pageAmount) pageIndex + 1 else 0))
	}

	private fun goBackward() {
		McClient.setScreen(LootTableScreen(parent, widgets, if (pageIndex != 0) pageIndex - 1 else pageAmount))
	}
}
