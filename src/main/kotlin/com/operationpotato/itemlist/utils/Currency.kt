package com.operationpotato.itemlist.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.skyblockapi.utils.extentions.createSkull
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

sealed class Currency(
	val itemName: MutableComponent,
	val texture: String
) {
	open val stack: ItemStack by registryBoundLazy {
		createSkull(texture).apply {
			val name = this@Currency.itemName.apply { italic = false }
			set(DataComponents.CUSTOM_NAME, name)
		}
	}

	data object Coin : Currency(
		Text.of("Coins", TextColor.GOLD),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZhMDg3ZWI3NmU3Njg3YTgxZTRlZjgxYTdlNjc3MjY0OTk5MGY2MTY3Y2ViMGY3NTBhNGM1ZGViNmM0ZmJhZCJ9fX0"
	)

	data object Copper : Currency(
		Text.of("Copper", TextColor.RED),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWMyNjU4MDNkMWNjMmUzMWY3OTUxZmZlY2JlZjUwZTA3OGMzNjYyOWQ1ZDA5MDc4YjkxYmE0ZGNkNDRjYTI5YyJ9fX0="
	)

	data class Unknown(val id: String) : Currency(Text.of("Unknown Currency: $id", TextColor.RED), "") {
		override val stack: ItemStack by registryBoundLazy {
			Items.BARRIER.defaultInstance.apply {
				set(DataComponents.CUSTOM_NAME, this@Unknown.itemName.apply { italic = false })
			}
		}
	}

	fun withAmount(amount: Number): ItemStack = stack.copy().apply {
		val lore = listOf(Text.of("Amount: ${amount.toFormattedString()}", TextColor.GRAY))
		set(DataComponents.LORE, ItemLore(lore, lore))
	}

	companion object {
		fun getCurrencyById(id: String): Currency = when (id.lowercase()) {
			"coin" -> Coin
			"copper" -> Copper
			else -> Unknown(id)
		}
	}
}
