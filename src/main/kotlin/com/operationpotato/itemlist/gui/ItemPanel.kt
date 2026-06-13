package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.config.ConfigManager
import com.operationpotato.itemlist.utils.CalcUtils
import com.operationpotato.itemlist.utils.CalcUtils.isExpression
import com.operationpotato.itemlist.utils.ComponentUtils
import com.operationpotato.itemlist.utils.SearchUtils
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import com.operationpotato.itemlist.utils.ThreadUtils
import com.operationpotato.itemlist.utils.ThreadUtils.cancelAndSubmit
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.gui.screens.inventory.PageButton
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.right
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import java.util.concurrent.Future

class ItemPanel(x: Int, y: Int, width: Int, height: Int) : AbstractItemPanel(x, y, width, height) {
	val itemListWidget = EntireListWidget(width - AbstractItemList.PADDING, height - 20)

	val prevPageButton: Button = PageButton(0, 0, false, { _ ->
		itemListWidget.scrollPage(false)
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)
	val nextPageButton: Button = PageButton(0, 0, true, { _ ->
		itemListWidget.scrollPage(true)
		playButtonClickSound(Minecraft.getInstance().soundManager)
	}, false)
	var topLayout: LinearLayout = LinearLayout.horizontal()

	val filterButton: CycleButton<SkyBlockItemCategory> =
		CycleButton.builder(SkyBlockItemCategory::asComponent, SkyBlockItemCategory.ALL)
			.withValues(SkyBlockItemCategory.entries)
			.withTooltip(::createFilterTooltip)
			.create(Component.empty(), ::onFilterButtonClick)
	val searchBox: EditBox = EditBox(
		McFont.self, 0, 16, Component.empty()
	)
	var bottomLayout: LinearLayout = LinearLayout.horizontal()

	val children: List<AbstractWidget> = listOf(nextPageButton, prevPageButton, filterButton, searchBox, itemListWidget)

	var filterFuture: Future<*>? = null
	var searchFuture: Future<*>? = null

	private var calculatorResult: Pair<String, Boolean> = "" to false
	private var calculatorResultColor: Int = 0

	init {
		filterButton.value = ConfigManager.get().lastFilter
		filterButton.message = Component.literal("F")
		searchBox.value = ConfigManager.get().lastSearch
		searchBox.addFormatter(SearchUtils::highlightSearch)
		searchBox.setHint(Component.literal("Search or Calculate..."))
		searchBox.setResponder { text ->
			val isExpression = text.isExpression()
			updateListVisibility(text, isExpression)
			if (isExpression) calculateAsync(text)
			else searchAsync(text)
		}
		searchBox.setMaxLength(999)
		updateListVisibility(searchBox.value, false)

		if (ConfigManager.get().lastFilter != SkyBlockItemCategory.ALL)
			itemListWidget.currentFilter = filterButton.value
		if (ConfigManager.get().lastSearch.isNotEmpty())
			itemListWidget.currentSearch = searchBox.value
	}

	fun updateListVisibility(search: String, isExpression: Boolean) {
		itemListWidget.visible = !ConfigManager.get().hideItemsWithoutSearch || (search.isNotEmpty() && !isExpression)
		// top bar layout might not be initialized yet, so:
		prevPageButton.visible = itemListWidget.visible
		nextPageButton.visible = itemListWidget.visible
	}

	override fun updatePosition() {
		positionTopBar()
		positionBottomBar()

		itemListWidget.setPosition(x, y)
		itemListWidget.setSize(width - 2, height - 20)
		itemListWidget.positioningCallback = {
			McClient.runOrNextTick { positionTopBar() }
			McClient.runOrNextTick { updateSearchResult() }
		}
		itemListWidget.itemSize = ConfigManager.get().itemSize
		itemListWidget.scaleChildren()
		itemListWidget.updatePositionsAsync()
	}

	fun positionTopBar() {
		val leftPadding = AbstractItemList.PADDING + itemListWidget.horizontalPadding
		val spacerWidth = itemListWidget.width - 2 * prevPageButton.width - leftPadding
		topLayout = LinearLayout.horizontal()
		topLayout.setPosition(itemListWidget.x + leftPadding, y + 5)
		topLayout.addChild(prevPageButton) { it.alignHorizontallyLeft() }
		topLayout.addChild(SpacerElement.width(spacerWidth))
		topLayout.addChild(nextPageButton) { it.alignHorizontallyRight() }
		topLayout.arrangeElements()
	}

	fun positionBottomBar() {
		filterButton.setSize(16, 16)
		searchBox.width = width - 26 - filterButton.width

		bottomLayout = LinearLayout.horizontal()
		bottomLayout.defaultCellSetting().paddingRight(4)
		bottomLayout.setPosition(x + 20 + itemListWidget.horizontalPadding, y + height - 20)
		bottomLayout.addChild(searchBox)
		bottomLayout.addChild(filterButton)
		bottomLayout.arrangeElements()
	}

	fun createFilterTooltip(category: SkyBlockItemCategory): Tooltip {
		val options = ComponentUtils.getCycleEnumOptions(category)
		var line = Component.empty().append(category.asComponent().withStyle(ChatFormatting.GREEN))
		line = line.append(ComponentUtils.joinComponents(options))
		return Tooltip.create(line, null)
	}

	fun onFilterButtonClick(btn: CycleButton<SkyBlockItemCategory>, category: SkyBlockItemCategory) {
		ConfigManager.get().lastFilter = category
		val color = if (category == SkyBlockItemCategory.ALL) {
			ChatFormatting.WHITE
		} else {
			ChatFormatting.GREEN
		}
		btn.message = Component.literal("F").withStyle(color)
		filterAsync(category)
	}

	fun filterAsync(category: SkyBlockItemCategory) {
		this.filterFuture = ThreadUtils.SORTING_EXECUTOR.cancelAndSubmit(filterFuture) {
			itemListWidget.filterChildren(category)
			itemListWidget.searchChildren(ConfigManager.get().lastSearch)
			itemListWidget.switchPage(0)
			itemListWidget.updatePositionsAsync()
		}
	}

	fun calculateAsync(text: String) {
		searchBox.setTextColor(CommonColors.TEXT_GRAY)
		this.searchFuture = ThreadUtils.SORTING_EXECUTOR.cancelAndSubmit(searchFuture) {
			calculatorResult = CalcUtils.calculateExpression(text)
			calculatorResultColor = if (calculatorResult.second) TextColor.YELLOW else TextColor.RED
		}
	}

	fun searchAsync(text: String) {
		ConfigManager.get().lastSearch = text
		calculatorResult = "" to false
		this.searchFuture = ThreadUtils.SORTING_EXECUTOR.cancelAndSubmit(searchFuture) {
			itemListWidget.searchChildren(text)
			itemListWidget.switchPage(0)
			itemListWidget.updatePositionsAsync()
		}
	}

	fun updateSearchResult() {
		if (itemListWidget.visibleChildren.isEmpty() && calculatorResult.first.isEmpty()) {
			searchBox.setTextColor(CommonColors.SOFT_RED)
		} else {
			searchBox.setTextColor(CommonColors.TEXT_GRAY)
		}
	}

	override fun removed() {
		ConfigManager.get().itemSize = itemListWidget.itemSize
	}

	override fun children(): List<GuiEventListener> = children
	override fun getListWidget(): AbstractItemList = itemListWidget

	override fun keyPressed(event: KeyEvent): Boolean {
		if (!this.visible) return false
		if (this.searchBox.isFocused) {
			this.searchBox.keyPressed(event)
			return true
		}
		return itemListWidget.keyPressed(event)
	}

	override fun updateWidth() {
		val screen = McScreen.self
		if (screen !is AbstractContainerScreen<*>) return
		val availableWidth = screen.width - screen.right
		val panelWidth = (availableWidth * ConfigManager.get().maxWidth).toInt()
		x = screen.width - panelWidth
		width = panelWidth - 2
		updatePosition()
	}

	override fun extractWidgetRenderState(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		children.forEach { it.extractRenderState(graphics, mouseX, mouseY, a) }

		if (calculatorResult.first.isNotEmpty()) {
			val message = Text.of(calculatorResult.first, calculatorResultColor)
			val textX = searchBox.x + searchBox.width - McFont.self.width(message) - 6
			val textY = if (calculatorResult.second) searchBox.y + (searchBox.height - McFont.height) / 2 + 1
			else searchBox.y - 5 - McFont.height / 2
			graphics.text(McFont.self, message, textX, textY, CommonColors.WHITE)
		}
	}
}
