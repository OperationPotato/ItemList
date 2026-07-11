package com.operationpotato.itemlist.gui.loottable

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.AbstractPagedListScreen
import com.operationpotato.itemlist.gui.recipe.IngredientDisplay
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.layouts.FrameLayout
import net.minecraft.client.gui.layouts.LinearLayout
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.KeyEvent
import net.minecraft.util.CommonColors
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.text.Text

class LootTableScreen(parent: Screen?, widgets: List<MobLootWidget>, pageIndex: Int = 0) :
	AbstractPagedListScreen<MobLootWidget>(parent, Text.of("Loot Tables"), widgets, pageIndex) {

	private var container: FrameLayout? = null

	override val contentAllowedSize: Int get() = super.contentAllowedSize - 30

	override fun getTopLayoutWidth(): Int = 176

	override fun buildContent(layout: LinearLayout, visibleItems: List<MobLootWidget>) {
		val mob = visibleItems.firstOrNull()?.mob
		if (mob != null) {
			val innerLayout = LinearLayout.vertical().spacing(5).apply {
				addChild(
					StringWidget(Text.of(mob.name, CommonColors.DARK_GRAY).apply { withoutShadow() }, font),
					this.newCellSettings().alignHorizontallyCenter().paddingTop(8).paddingBottom(2)
				)
				visibleItems.forEach { addChild(it) }
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
	}

	override fun createScreen(newPageIndex: Int): Screen = LootTableScreen(parent, items, newPageIndex)

	override fun handleChildKeyPress(child: AbstractWidget, event: KeyEvent): Boolean {
		return child is MobLootWidget && child.keyPressed(event)
	}

	override fun getHoveredStack(child: AbstractWidget): ItemStack? {
		var stack: ItemStack? = null
		if (child is MobLootWidget) {
			child.visitWidgets {
				if (it is IngredientDisplay && it.isHovered) stack = it.stack
			}
		}
		return stack
	}

	override fun getLeft(): Int = container?.x ?: super.getLeft()
	override fun getRight(): Int = container?.x?.plus(container?.width ?: 0) ?: super.getRight()
	override fun getMaxWidth(): Int = container?.width ?: super.getMaxWidth()
}
