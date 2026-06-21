package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.api.impl.PluginManager
import com.operationpotato.itemlist.utils.Utils.overlaps
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.Layout
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.layouts.LayoutSettings
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.client.renderer.Rect2i
import java.util.function.Consumer

class PaginatedGridLayout(private var x: Int, private var y: Int) : Layout {
	var activePage = 0
	var pages = 0

	var excludedAreas: List<Pair<Int, Int>> = listOf()
	var maxCols: Int = 0
	var maxRows: Int = 0
	var itemSize: Int = 0

	private val gridLayouts = mutableListOf<MarkedGridLayout>()

	fun calcExcludedAreas(startX: Int, startY: Int, maxCols: Int, maxRows: Int, itemSize: Int): List<Pair<Int, Int>> {
		val rectangle = Rect2i(x, y, x + maxCols * itemSize, y + maxRows * itemSize)
		val activeExclusionZones = PluginManager.getExclusionZones().filter { zone ->
			rectangle.overlaps(zone.area)
		}
		if (activeExclusionZones.isEmpty()) return listOf()

		val excludedAreas = mutableListOf<Pair<Int, Int>>()
		for (col in 0..maxCols) {
			for (row in 0..maxRows) {
				val x = startX + (col * itemSize)
				val y = startY + (row * itemSize)
				val gridRect = Rect2i(x, y, itemSize, itemSize)
				for (zone in activeExclusionZones) {
					if (zone.area.overlaps(gridRect)) {
						excludedAreas.add(Pair(col, row))
						break
					}
				}
			}
		}

		return excludedAreas
	}

	fun addChildren(children: List<LayoutElement>, maxCols: Int, maxRows: Int, itemSize: Int) {
		this.maxCols = maxCols
		this.maxRows = maxRows
		this.itemSize = itemSize
		var page = 0
		var layout = MarkedGridLayout(x, y)
		var col = 0
		var row = 0
		excludedAreas = calcExcludedAreas(x, y, maxCols, maxRows, itemSize)
		val maxArea = (maxCols + 1) * (maxRows + 1)
		if (maxArea - excludedAreas.size <= 0) return
		val iterator = children.iterator()
		while (iterator.hasNext()) {
			if (excludedAreas.contains(Pair(col, row))) {
				layout.addChild(SpacerElement(itemSize, itemSize), row, col)
			} else {
				layout.addChild(iterator.next(), row, col)
			}
			col += 1
			if (col >= maxCols) {
				col = 0
				row += 1
			}
			if (row >= maxRows) {
				row = 0
				col = 0
				gridLayouts.add(layout)
				layout = MarkedGridLayout(x, y)
				page += 1
			}
		}
		gridLayouts.add(layout)
		activePage = 0
		pages = gridLayouts.size
	}

	fun compareExcludedAreas(): Boolean {
		val newZones = calcExcludedAreas(x, y, maxCols, maxRows, itemSize)
		if (newZones.size != excludedAreas.size) return true
		for (pair in newZones) {
			if (!excludedAreas.contains(pair)) return true
		}
		return false
	}

	fun switchPage(page: Int) {
		activePage = page.coerceIn(0, gridLayouts.size)
		arrangeElements()
	}

	fun visitPageWidgets(consumer: Consumer<AbstractWidget>) {
		getPageLayout(activePage)?.visitWidgets(consumer)
	}

	fun getPageWidgets(): List<AbstractWidget> {
		return getPageLayout(activePage)?.children ?: listOf()
	}

	override fun arrangeElements() {
		val layout = getPageLayout(activePage) ?: return
		if (layout.hasBeenArranged) return
		layout.arrangeElements()
	}

	fun getPageLayout(page: Int): MarkedGridLayout? {
		if (gridLayouts.isEmpty() || page >= gridLayouts.size || page < 0) return null
		return gridLayouts[page]
	}

	override fun visitChildren(layoutElementVisitor: Consumer<LayoutElement>) {}

	override fun setX(x: Int) {
		this.x = x
		gridLayouts.forEach { gridLayout ->
			gridLayout.x = x
			gridLayout.hasBeenArranged = false
		}
	}

	override fun setY(y: Int) {
		this.y = y
		gridLayouts.forEach { gridLayout ->
			gridLayout.y = y
			gridLayout.hasBeenArranged = false
		}
	}

	override fun getX(): Int {
		return x
	}

	override fun getY(): Int {
		return y
	}

	override fun getWidth(): Int {
		return getPageLayout(activePage)?.width ?: -1
	}

	override fun getHeight(): Int {
		return getPageLayout(activePage)?.height ?: -1
	}

	//? if >=26.2 {
	override fun removeChildren() {
		gridLayouts.forEach { it.removeChildren() }
	}
	//? }

	class MarkedGridLayout(x: Int, y: Int) : GridLayout(x, y) {
		val children: List<AbstractWidget> by lazy { getWidgets() }
		var hasBeenArranged: Boolean = false

		fun getWidgets(): List<AbstractWidget> {
			val newChildren = mutableListOf<AbstractWidget>()
			this.visitWidgets(newChildren::add)
			return newChildren
		}

		override fun arrangeElements() {
			super.arrangeElements()
			hasBeenArranged = true
		}

		override fun <T : LayoutElement> addChild(
			child: T,
			row: Int,
			column: Int,
			rows: Int,
			columns: Int,
			cellSettings: LayoutSettings
		): T {
			hasBeenArranged = false
			return super.addChild(child, row, column, rows, columns, cellSettings)
		}
	}
}
