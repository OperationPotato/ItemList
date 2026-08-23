package com.operationpotato.itemlist.gui

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.utils.ComponentUtils
import com.operationpotato.itemlist.utils.SkyBlockItemCategory
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.CycleButton
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import net.minecraft.util.CommonColors
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import java.util.function.Consumer

class FilterButton(default: SkyBlockItemCategory, val consumer: Consumer<SkyBlockItemCategory>) :
	CycleButton<SkyBlockItemCategory>(
		0, 0, 16, 16,
		Component.empty(), Component.empty(),
		0,
		default,
		{ default },
		ValueSupplier(),
		SkyBlockItemCategory::asComponent,
		{ btn -> btn.value.asComponent() },
		::onValueChanged,
		::createFilterTooltip,
		DisplayState.VALUE,
		{ _, _ -> null },
	) {


	override fun shouldTakeFocusAfterInteraction(): Boolean = false

	override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
		graphics.blitSprite(
			RenderPipelines.GUI_TEXTURED,
			FILTER,
			x,
			y,
			width,
			height,
			if (message.style.color == null) CommonColors.WHITE else ARGB.opaque(message.color)
		)
	}

	override fun mouseClicked(event: MouseButtonEvent, doubleClick: Boolean): Boolean {
		if (!isActive || !isMouseOver(event.x, event.y)) return false
		if (event.button() == 0) onClick(event, doubleClick)
		else if (event.button() == 1) {
			value = when (value) {
				SkyBlockItemCategory.ALL -> SkyBlockItemCategory.CUSTOM
				SkyBlockItemCategory.CUSTOM -> SkyBlockItemCategory.ALL
				else -> SkyBlockItemCategory.ALL
			}
			onValueChanged(this, value)
		}
		this.playDownSound(Minecraft.getInstance().soundManager)
		return true
	}

	companion object {
		val FILTER: Identifier = SkyBlockItemList.id("filter")

		fun onValueChanged(btn: CycleButton<SkyBlockItemCategory>, category: SkyBlockItemCategory) {
			val color = when (category) {
				SkyBlockItemCategory.ALL -> CommonColors.WHITE
				SkyBlockItemCategory.CUSTOM -> CommonColors.SOFT_YELLOW
				else -> CommonColors.GREEN
			}
			btn.message = Component.literal("F").withColor(color)
			if (btn is FilterButton) btn.consumer.accept(category)
		}

		fun createFilterTooltip(category: SkyBlockItemCategory): Tooltip {
			val options = ComponentUtils.getCycleEnumOptions(category)
			var line = Component.empty().append(category.asComponent().withStyle(ChatFormatting.GREEN))
			line = line.append(ComponentUtils.joinComponents(options))
			return Tooltip.create(line, null)
		}
	}

	class ValueSupplier : ValueListSupplier<SkyBlockItemCategory> {
		override fun getSelectedList(): List<SkyBlockItemCategory> = SkyBlockItemCategory.entries
		override fun getDefaultList(): List<SkyBlockItemCategory> = SkyBlockItemCategory.entries
	}
}
