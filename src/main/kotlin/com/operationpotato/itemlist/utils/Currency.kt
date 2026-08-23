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

sealed class Currency(val itemName: MutableComponent) {
	abstract val stack: ItemStack

	sealed class SkullCurrency(
		itemName: MutableComponent,
		val texture: String
	) : Currency(itemName) {
		override val stack: ItemStack by registryBoundLazy {
			createSkull(texture).apply {
				set(DataComponents.CUSTOM_NAME, this@SkullCurrency.itemName.apply { italic = false })
			}
		}
	}

	sealed class ItemCurrency(
		itemName: MutableComponent,
		private val stackSupplier: () -> ItemStack
	) : Currency(itemName) {
		override val stack: ItemStack by registryBoundLazy {
			stackSupplier().apply {
				set(DataComponents.CUSTOM_NAME, this@ItemCurrency.itemName.apply { italic = false })
			}
		}
	}

	data object Coin : SkullCurrency(
		Text.of("Coin", TextColor.GOLD),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZhMDg3ZWI3NmU3Njg3YTgxZTRlZjgxYTdlNjc3MjY0OTk5MGY2MTY3Y2ViMGY3NTBhNGM1ZGViNmM0ZmJhZCJ9fX0"
	)

	data object Bit : SkullCurrency(
		Text.of("Bit", TextColor.AQUA),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGExNDg0ZjVjNTQ1OGEzYWRhNTk0YTUyOGI2NWJmOWE5ZDU3N2UyNWIyNzE3ZGE3ODY0NjFjOWI2NTg4YjQ4In19fQ=="
	)

	data object Pelt : ItemCurrency(
		Text.of("Pelt", TextColor.GREEN),
		{ Items.LEATHER.defaultInstance }
	)

	data object NorthStar : ItemCurrency(
		Text.of("North Star", TextColor.LIGHT_PURPLE),
		{ Items.NETHER_STAR.defaultInstance }
	)

	data object BingoPoint : ItemCurrency(
		Text.of("Bingo Point", TextColor.GREEN),
		{
			//~ if <26.2 'DYE.white().' -> 'WHITE_DYE.'
			Items.DYE.white().defaultInstance
		}
	)

	data object FossilDust : SkullCurrency(
		Text.of("Fossil Dust", TextColor.WHITE),
		"ewogICJ0aW1lc3RhbXAiIDogMTcwODY0NTg0MjYxNywKICAicHJvZmlsZUlkIiA6ICJiYWRkZjIxZTFmNWE0ZGYzOGVjZmNkOTYwY2E0YzA5YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbmRlckJUIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzkzYTFiODMwMzk5YWI0MzJhNTE3OGZkYWYzOTM5YjI0YmYyNWM3MjRhNjZiZTk0NzI5NmM1MDMzNTJiYzM4MGQiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ"
	)

	data object CarnivalToken : SkullCurrency(
		Text.of("Carnival Token", TextColor.GREEN),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ3ZjVlMmU1MjM1YjY3OWY4ZGI5YzQyZTg1OWM4OGFhNzgyN2IxZmI1MTgyYzA3NzAzYzQ1NmU5MTI1Y2Y1ZiJ9fX0="
	)

	data object Gem : SkullCurrency(
		Text.of("Gem", TextColor.GREEN),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmMwZTZkOWUyNDI3MzU0ODE5MThjNWZkMTQ0OThiZDc2MGJiOWY0ZmY2NDMwYWQ0Njk2YjM4ZThhODgzZGE5NyJ9fX0="
	)

	// Garden
	data object Copper : SkullCurrency(
		Text.of("Copper", TextColor.RED),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWMyNjU4MDNkMWNjMmUzMWY3OTUxZmZlY2JlZjUwZTA3OGMzNjYyOWQ1ZDA5MDc4YjkxYmE0ZGNkNDRjYTI5YyJ9fX0="
	)

	data object Kernel : ItemCurrency(
		Text.of("Kernel", TextColor.YELLOW),
		{ Items.PUMPKIN_SEEDS.defaultInstance }
	)

	data object BronzeMedal : SkullCurrency(
		Text.of("Bronze Medal", TextColor.GREEN),
		"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzk3MjVhYWVhZmUyYjZlNTQxMDMzZDlkOTRhNzcxOWJjYjg2ZTU4MDYzNzkyZDhmYmI5NjU2YzA5Y2FjMmU4NSJ9fX0="
	)

	data object SilverMedal : SkullCurrency(
		Text.of("Silver Medal", TextColor.GREEN),
		"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTA1NGU5MTJmOTE1OTBmNDA3Njc3MWMyNTRmYTYzZTJiOWU2YzM1MjU1ZDU5YmM3OGRmNWQ2MWZjZDUwNjE3YyJ9fX0="
	)

	data object GoldMedal : SkullCurrency(
		Text.of("Gold Medal", TextColor.GOLD),
		"e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBiNGIwM2I2NjMwYjk0MzIwMGE1OTA0NTg0ZjEzZjRhYTI3ZDk4NWI4YzZiMmIzNGFhNmJjYzFiNDk1ZDIzZiJ9fX0="
	)

	data object Pest : SkullCurrency(
		Text.of("Pest", TextColor.DARK_AQUA),
		"ewogICJ0aW1lc3RhbXAiIDogMTcyMzE3OTc4OTkzNCwKICAicHJvZmlsZUlkIiA6ICJlMjc5NjliODYyNWY0NDg1YjkyNmM5NTBhMDljMWMwMSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLRVZJTktFTE9LRSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MGExZTgzNmJmMTk2OGIyZWFhNDgzNzIyN2ExOTIwNGYxNzI5NWQ4NzBlZTllNzU0YmQ2YjZkNjBkZGJlZDNjIgogICAgfQogIH0KfQ",
	)

	// Rift
	data object Mote : ItemCurrency(
		Text.of("Mote", TextColor.GREEN),
		{ Items.LILAC.defaultInstance }
	)

	data object Spider : SkullCurrency(
		Text.of("Spider", TextColor.PINK),
		"ewogICJ0aW1lc3RhbXAiIDogMTY1MDU1NjEzMTkxNywKICAicHJvZmlsZUlkIiA6ICI0ODI5MmJkMjI1OTc0YzUwOTZiMTZhNjEyOGFmMzY3NSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLVVJPVE9ZVEIyOCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZmRmNjJkNGUwM2NhNTk0YzhjZDIxZGQxNzUzMjdmMWNmNzdjNGJjMDU3YTA5NTk2MDNkODNhNjhiYTI3MDA4IgogICAgfQogIH0KfQ",
	)

	data object Fly : SkullCurrency(
		Text.of("Fly", TextColor.PINK),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTMwYWMxZjljNjQ5Yzk5Y2Q2MGU0YmZhNTMzNmNjMTg1MGYyNzNlYWI5ZjViMGI3OTQwZDRkNGQ3ZGM4MjVkYyJ9fX0"
	)

	data object Silverfish : SkullCurrency(
		Text.of("Silverfish", TextColor.PINK),
		"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGE5MWRhYjgzOTFhZjVmZGE1NGFjZDJjMGIxOGZiZDgxOWI4NjVlMWE4ZjFkNjIzODEzZmE3NjFlOTI0NTQwIn19fQ"
	)

	// Powder & Whisper
	data object Mithril : ItemCurrency(
		Text.of("Mithril", TextColor.GREEN),
		{
			//~ if <26.2 'DYE.lime().' -> 'LIME_DYE.'
			Items.DYE.lime().defaultInstance
		}
	)

	data object Gemstone : ItemCurrency(
		Text.of("Gemstone", TextColor.PINK),
		{
			//~ if <26.2 'DYE.magenta().' -> 'MAGENTA_DYE.'
			Items.DYE.magenta().defaultInstance
		}
	)

	data object Glacite : ItemCurrency(
		Text.of("Glacite", TextColor.AQUA),
		{
			//~ if <26.2 'DYE.lightBlue().' -> 'LIGHT_BLUE_DYE.'
			Items.DYE.lightBlue().defaultInstance
		}
	)

	data object Forest : ItemCurrency(
		Text.of("Forest", TextColor.DARK_GREEN),
		{
			//~ if <26.2 'DYE.cyan().' -> 'CYAN_DYE.'
			Items.DYE.cyan().defaultInstance
		}
	)

	data class Unknown(val id: String) : ItemCurrency(
		Text.of("Unknown Currency: $id", TextColor.RED),
		{ Items.BARRIER.defaultInstance }
	)

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
			"mithril", "mithril_powder", "powder_mithril" -> Mithril
			"gemstone", "gemstone_powder", "powder_gemstone" -> Gemstone
			"glacite", "glacite_powder", "powder_glacite" -> Glacite
			"forest", "forest_whispers", "whispers_forest" -> Forest
			else -> Unknown(id)
		}
	}

}
