package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.utils.ThreadUtils
import com.operationpotato.itemlist.utils.ThreadUtils.cancelAndSubmit
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractContainerWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import java.util.concurrent.Future

abstract class AbstractItemList(width: Int, height: Int) :
	AbstractContainerWidget(
		0, 0, width, height, Component.empty(), defaultSettings(8)
	) {

	val itemListHeight: Int
		get() = height - 2 * PADDING - 2 * McFont.height

	var layout: PaginatedGridLayout = PaginatedGridLayout(0, 0)
	var itemSize: Int = StackDisplay.STACK_SIZE
	var itemCount: Int = 0
	var scrollAmountWidget: TemporalTextWidget? = null

	var visibleCols: Int = 0
	var visibleRows: Int = 0
	var horizontalPadding: Int = 4
	var currentPage: Int = 1
	var maxPages: Int = 1

	var sortingFuture: Future<*>? = null
	var positioningCallback: Runnable? = null

	abstract fun getItems(): List<StackDisplay>
	abstract fun getAllItems(): List<StackDisplay>

	fun updatePositionsAsync() {
		sortingFuture = ThreadUtils.SORTING_EXECUTOR.cancelAndSubmit(sortingFuture, ::updatePositions)
	}

	fun switchPage(page: Int) {
		layout.switchPage(page)
		currentPage = layout.activePage
	}

	// Off-Thread
	fun updatePositions() {
		val previouslyVisibleCols = visibleCols
		val previouslyVisibleRows = visibleRows
		val prevItemCount = itemCount
		val adjustedWidth = width - PADDING

		visibleCols = Math.ceilDiv(adjustedWidth, itemSize)
		horizontalPadding = (adjustedWidth - visibleCols * itemSize) / 2
		visibleRows = Math.ceilDiv(itemListHeight, itemSize)
		itemCount = getItems().size
		if ((PluginManager.didExclusionZonesChange() && layout.compareExcludedAreas()) ||
			visibleCols != previouslyVisibleCols || visibleRows != previouslyVisibleRows ||
			itemCount != prevItemCount
		) {
			positionDisplays(visibleCols, visibleRows, itemSize)
			layout.switchPage(currentPage - 1)
			positioningCallback?.run()
		}
	}

	// Off-Thread
	fun positionDisplays(maxCols: Int, maxRows: Int, scaledSize: Int) {
		val newLayout = PaginatedGridLayout(x + PADDING + horizontalPadding, y + PADDING + McFont.height / 2)
		newLayout.addChildren(getItems(), maxCols, maxRows, scaledSize)
		layout = newLayout
		maxPages = layout.pages
		currentPage = if (maxPages == 0) 0 else currentPage.coerceIn(1, maxPages)
	}

	fun scaleChildren() = getAllItems().forEach { it.scale(itemSize) }

	override fun contentHeight(): Int {
		return height
	}

	override fun children(): List<GuiEventListener> = layout.getPageWidgets()

	fun scrollPage(scrollDown: Boolean) {
		if (scrollDown) {
			currentPage += 1
			if (currentPage > maxPages) {
				currentPage -= maxPages
			}
		} else {
			currentPage -= 1
			if (currentPage < 1) {
				currentPage += maxPages
			}
		}
		currentPage = currentPage.coerceIn(1, maxPages)
		layout.switchPage(currentPage - 1)
	}

	private val minScale = 0.5f
	private val maxScale = 3f
	fun scrollItemSize(scrollY: Double) {
		itemSize += if (scrollY > 0) 1 else -1
		itemSize = itemSize.coerceIn(
			(minScale * StackDisplay.STACK_SIZE).toInt(),
			(maxScale * StackDisplay.STACK_SIZE).toInt()
		)
		scrollAmountWidget = TemporalTextWidget(
			x + width / 2, itemListHeight / 2, 5f,
			Component.literal("${(itemSize / StackDisplay.STACK_SIZE.toFloat() * 100).toInt()}%"), McFont.self
		)
		scaleChildren()
		updatePositionsAsync()
	}

	override fun mouseScrolled(
		x: Double,
		y: Double,
		scrollX: Double,
		scrollY: Double
	): Boolean {
		if (!visible) return false
		if (!this.isMouseOver(x, y)) return false
		if (McClient.self.hasControlDown()) {
			scrollItemSize(scrollY)
		} else {
			scrollPage(scrollY < 0)
		}
		return true
	}

	override fun extractWidgetRenderState(
		graphics: GuiGraphicsExtractor,
		mouseX: Int,
		mouseY: Int,
		a: Float
	) {
		if (PluginManager.didExclusionZonesChange()) updatePositionsAsync()
		graphics.centeredText(
			McFont.self, Component.literal("${currentPage}/${maxPages}"),
			x + width / 2, y + McFont.height, CommonColors.WHITE
		)
		graphics.enableScissor(x, y, x + width - horizontalPadding, y + height)
		layout.visitPageWidgets {
			it.extractRenderState(graphics, mouseX, mouseY, a)
		}
		scrollAmountWidget?.extractRenderState(graphics, mouseX, mouseY, a)
		if (scrollAmountWidget?.expired() == true) scrollAmountWidget = null
		graphics.disableScissor()
	}

	override fun updateWidgetNarration(output: NarrationElementOutput) {}

	companion object {
		const val PADDING = StackDisplay.STACK_SIZE
	}
}
