package com.operationpotato.itemlist.gui.loottable

import com.operationpotato.itemlist.Keybinds
import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.gui.SpacerTextWidget
import com.operationpotato.itemlist.gui.recipe.IngredientDisplay
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.AbstractWidget.playButtonClickSound
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.PageButton
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.util.CommonColors
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
	private var container: FrameLayout? = null

	override fun init() {
		super.init()
		val pages = mutableListOf(mutableListOf<MobLootWidget>())
		val allowedSize = this@LootTableScreen.height / 5 * 4 - 30

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

		val layout = LinearLayout.vertical().spacing(5)

		if (pageAmount != 0) {
			topLayout = LinearLayout.horizontal()
			topLayout.addChild(prevPageButton) { it.alignHorizontallyLeft() }
			//@formatter:off
			topLayout.addChild(SpacerTextWidget(
				176 - 46,
				Text.of("${safePageIndex + 1} / ${pages.size}"),
				font
			))
			//@formatter:on
			topLayout.addChild(nextPageButton) { it.alignHorizontallyRight() }
			topLayout.arrangeElements()
			layout.addChild(topLayout)
		}

		val mob = visibleWidgets.firstOrNull()?.mob
		if (mob != null) {
			val innerLayout = LinearLayout.vertical().spacing(5).apply {
				addChild(
					StringWidget(Text.of(mob.name, CommonColors.DARK_GRAY).apply { withoutShadow() }, font),
					this.newCellSettings().alignHorizontallyCenter().paddingTop(8).paddingBottom(2)
				)
				visibleWidgets.forEach { addChild(it) }
			}
			innerLayout.arrangeElements()

			val containerWidth = 176 + 12
			val containerHeight = innerLayout.height + 12

			val container = FrameLayout(0, 0, containerWidth, containerHeight)
			container.addChild(ImageWidget.sprite(containerWidth, containerHeight, SkyBlockItemList.id("blank")))
			container.addChild(
				innerLayout,
				container.newChildLayoutSettings().alignHorizontallyCenter().alignVerticallyTop().paddingTop(2)
			)
			container.arrangeElements()

			this.container = container
			layout.addChild(container)
		}

		layout.arrangeElements()
		FrameLayout.centerInRectangle(layout, this@LootTableScreen.rectangle)
		layout.visitWidgets(this::addRenderableWidget)
	}

	override fun onClose() {
		if (parent is LootTableScreen) {
			parent.onClose()
		} else {
			McClient.setScreen(parent)
		}
	}

	override fun isInGameUi() = true

	fun getLeft(): Int = container?.x ?: visibleWidgets.minOf { it.x }
	fun getRight(): Int = container?.x?.plus(container?.width ?: 0) ?: visibleWidgets.maxOf { it.right }
	fun getMaxWidth(): Int = container?.width ?: visibleWidgets.maxOf { it.width }

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
