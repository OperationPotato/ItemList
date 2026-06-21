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
		Text.of("Coin", TextColor.GOLD),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZhMDg3ZWI3NmU3Njg3YTgxZTRlZjgxYTdlNjc3MjY0OTk5MGY2MTY3Y2ViMGY3NTBhNGM1ZGViNmM0ZmJhZCJ9fX0"
	)

	data object Bit : Currency(
		Text.of("Bit", TextColor.AQUA),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGExNDg0ZjVjNTQ1OGEzYWRhNTk0YTUyOGI2NWJmOWE5ZDU3N2UyNWIyNzE3ZGE3ODY0NjFjOWI2NTg4YjQ4In19fQ=="
	)

	data object Pelt : Currency(Text.of("Pelt", TextColor.GREEN), "") {
		override val stack: ItemStack by registryBoundLazy {
			Items.LEATHER.defaultInstance.apply {
				set(DataComponents.CUSTOM_NAME, this@Pelt.itemName.apply { italic = false })
			}
		}
	}

	data object NorthStar : Currency(Text.of("North Star", TextColor.LIGHT_PURPLE), "") {
		override val stack: ItemStack by registryBoundLazy {
			Items.NETHER_STAR.defaultInstance.apply {
				set(DataComponents.CUSTOM_NAME, this@NorthStar.itemName.apply { italic = false })
			}
		}
	}

	data object BingoPoint : Currency(Text.of("Bingo Point", TextColor.GREEN), "") {
		override val stack: ItemStack by registryBoundLazy {
			//~ if <26.2 'DYE.white().' -> 'WHITE_DYE.'
			Items.DYE.white().defaultInstance.apply {
				set(DataComponents.CUSTOM_NAME, this@BingoPoint.itemName.apply { italic = false })
			}
		}
	}

	data object FossilDust : Currency(
		Text.of("Fossil Dust", TextColor.WHITE),
		"ewogICJ0aW1lc3RhbXAiIDogMTcwODY0NTg0MjYxNywKICAicHJvZmlsZUlkIiA6ICJiYWRkZjIxZTFmNWE0ZGYzOGVjZmNkOTYwY2E0YzA5YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbmRlckJUIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkzYTFiODMwMzk5YWI0MzJhNTE3OGZkYWYzOTM5YjI0YmYyNWM3MjRhNjZiZTk0NzI5NmM1MDMzNTJiYzM4MGQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ"
	)

	data object CarnivalToken : Currency(
		Text.of("Carnival Token", TextColor.GREEN),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ3ZjVlMmU1MjM1YjY3OWY4ZGI5YzQyZTg1OWM4OGFhNzgyN2IxZmI1MTgyYzA3NzAzYzQ1NmU5MTI1Y2Y1ZiJ9fX0="
	)

	data object Gem : Currency(
		Text.of("Gem", TextColor.GREEN),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmMwZTZkOWUyNDI3MzU0ODE5MThjNWZkMTQ0OThiZDc2MGJiOWY0ZmY2NDMwYWQ0Njk2YjM4ZThhODgzZGE5NyJ9fX0="
	)

	// Garden
	data object Copper : Currency(
		Text.of("Copper", TextColor.RED),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWMyNjU4MDNkMWNjMmUzMWY3OTUxZmZlY2JlZjUwZTA3OGMzNjYyOWQ1ZDA5MDc4YjkxYmE0ZGNkNDRjYTI5YyJ9fX0="
	)

	data object Kernel : Currency(Text.of("Kernel", TextColor.YELLOW), "") {
		override val stack: ItemStack by registryBoundLazy {
			Items.PUMPKIN_SEEDS.defaultInstance.apply {
				set(DataComponents.CUSTOM_NAME, this@Kernel.itemName.apply { italic = false })
			}
		}
	}

	data object BronzeMedal : Currency(
		Text.of("Bronze Medal", TextColor.GREEN),
		"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk3MjVhYWVhZmUyYjZlNTQxMDMzZDlkOTRhNzcxOWJjYjg2ZTU4MDYzNzkyZDhmYmI5NjU2YzA5Y2FjMmU4NSJ9fX0="
	)

	data object SilverMedal : Currency(
		Text.of("Silver Medal", TextColor.GREEN),
		"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA1NGU5MTJmOTE1OTBmNDA3Njc3MWMyNTRmYTYzZTJiOWU2YzM1MjU1ZDU5YmM3OGRmNWQ2MWZjZDUwNjE3YyJ9fX0="
	)

	data object GoldMedal : Currency(
		Text.of("Gold Medal", TextColor.GOLD),
		"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBiNGIwM2I2NjMwYjk0MzIwMGE1OTA0NTg0ZjEzZjRhYTI3ZDk4NWI4YzZiMmIzNGFhNmJjYzFiNDk1ZDIzZiJ9fX0="
	)

	data object Pest : Currency(
		Text.of("Pest", TextColor.DARK_AQUA),
		"ewogICJ0aW1lc3RhbXAiIDogMTcyMzE3OTc4OTkzNCwKICAicHJvZmlsZUlkIiA6ICJlMjc5NjliODYyNWY0NDg1YjkyNmM5NTBhMDljMWMwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLRVZJTktFTE9LRSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MGExZTgzNmJmMTk2OGIyZWFhNDgzNzIyN2ExOTIwNGYxNzI5NWQ4NzBlZTllNzU0YmQ2YjZkNjBkZGJlZDNjIgogICAgfQogIH0KfQ",
	)

	// Rift
	data object Mote : Currency(Text.of("Mote", TextColor.GREEN), "") {
		override val stack: ItemStack by registryBoundLazy {
			Items.LILAC.defaultInstance.apply {
				set(DataComponents.CUSTOM_NAME, this@Mote.itemName.apply { italic = false })
			}
		}
	}

	data object Spider : Currency(
		Text.of("Spider", TextColor.PINK),
		"ewogICJ0aW1lc3RhbXAiIDogMTY1MDU1NjEzMTkxNywKICAicHJvZmlsZUlkIiA6ICI0ODI5MmJkMjI1OTc0YzUwOTZiMTZhNjEyOGFmMzY3NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLVVJPVE9ZVEIyOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZmRmNjJkNGUwM2NhNTk0YzhjZDIxZGQxNzUzMjdmMWNmNzdjNGJjMDU3YTA5NTk2MDNkODNhNjhiYTI3MDA4IgogICAgfQogIH0KfQ",
	)

	data object Fly : Currency(
		Text.of("Fly", TextColor.PINK),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTMwYWMxZjljNjQ5Yzk5Y2Q2MGU0YmZhNTMzNmNjMTg1MGYyNzNlYWI5ZjViMGI3OTQwZDRkNGQ3ZGM4MjVkYyJ9fX0"
	)

	data object Silverfish : Currency(
		Text.of("Silverfish", TextColor.PINK),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5MWRhYjgzOTFhZjVmZGE1NGFjZDJjMGIxOGZiZDgxOWI4NjVlMWE4ZjFkNjIzODEzZmE3NjFlOTI0NTQwIn19fQ"
	)

	data class Unknown(val id: String) : Currency(Text.of("Unknown Currency: $id", TextColor.RED), "") {
		override val stack: ItemStack by registryBoundLazy {
			Items.BARRIER.defaultInstance.apply {
				set(DataComponents.CUSTOM_NAME, this@Unknown.itemName.apply { italic = false })
			}
		}
	}

	fun withAmount(amount: Number): ItemStack = stack.copy().apply {
		val lore = listOf(Text.of("Amount: ${amount.toFormattedString()}", TextColor.GRAY).apply { italic = false })
		set(DataComponents.LORE, ItemLore(lore, lore))
	}

	companion object {
		fun getCurrencyById(id: String): Currency = when (id.lowercase()) {
			"coin" -> Coin
			"bit" -> Bit
			"copper" -> Copper
			"pelt" -> Pelt
			"kernel" -> Kernel
			"north_star" -> NorthStar
			"gold_medal" -> GoldMedal
			"bingo_point" -> BingoPoint
			"fossil_dust" -> FossilDust
			"bronze_medal" -> BronzeMedal
			"silver_medal" -> SilverMedal
			"carnival_token", "carnival_point" -> CarnivalToken
			"gem" -> Gem
			"pest" -> Pest
			"mote" -> Mote
			"spider" -> Spider
			"fly" -> Fly
			"silverfish" -> Silverfish
			else -> Unknown(id)
		}
	}
}
