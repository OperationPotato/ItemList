package com.operationpotato.itemlist.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.mobs.drop.AttributeDrop
import tech.thatgravyboat.repolib.api.mobs.drop.EnchantmentDrop
import tech.thatgravyboat.repolib.api.mobs.drop.ItemDrop
import tech.thatgravyboat.repolib.api.mobs.drop.MobDrop
import tech.thatgravyboat.repolib.api.mobs.drop.PetDrop
import tech.thatgravyboat.repolib.api.mobs.drop.PotionDrop
import tech.thatgravyboat.repolib.api.mobs.drop.RuneDrop
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe
import tech.thatgravyboat.repolib.api.recipes.ingredient.AttributeIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.CraftingIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.CurrencyIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.EmptyIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.EnchantmentIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.ItemIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.PetIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.PotionIngredient
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.getLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.strikethrough
import kotlin.apply

object RepoLibUtils {

	fun Recipe<*>.result(): CraftingIngredient? {
		return when (this) {
			is ShopRecipe -> this.result()
			is KatRecipe -> this.output()
			is CraftingRecipe -> this.result()
			is ForgeRecipe -> this.result()
			else -> null
		}
	}

	fun CraftingIngredient.toSkyBlockId(): SkyBlockId? {
		if (this is EmptyIngredient) return null

		return when (this) {
			is ItemIngredient -> SkyBlockId.item(this.id())
			is PetIngredient -> SkyBlockId.pet(this.id(), this.tier())
			is EnchantmentIngredient -> SkyBlockId.enchantment(this.id(), this.level())
			is AttributeIngredient -> SkyBlockId.attribute(this.id())
			is PotionIngredient -> SkyBlockId.potion(this.id().removePrefix("POTION_"), this.level())
			else -> null
		}
	}

	fun CraftingIngredient.toItem(withStackSize: Boolean = true): ItemStack? {
		if (this is EmptyIngredient) return null

		val (id, count) = when (this) {
			is ItemIngredient -> SkyBlockId.item(this.id()) to this.count
			is PetIngredient -> SkyBlockId.pet(this.id(), this.tier()) to this.count
			is EnchantmentIngredient -> SkyBlockId.enchantment(this.id(), this.level()) to this.count
			is AttributeIngredient -> SkyBlockId.attribute(this.id()) to this.count
			is PotionIngredient -> SkyBlockId.potion(this.id(), this.level()) to this.count
			is CurrencyIngredient -> Currency.getCurrencyById(this.currency) to this.count
			else -> null to 0
		}

		return when (id) {
			is SkyBlockId -> id.toItem().apply {
				if (withStackSize) {
					val new = copy()
					new.count = count
					return new
				}
			}

			is Currency -> id.withAmount(count)
			else -> null
		}
	}

	fun Recipe<*>.getInputItemStacks(withCoins: Boolean = true): List<ItemStack> {
		val (inputs, coins) = when (this) {
			is ForgeRecipe -> this.inputs to this.coins
			is KatRecipe -> this.items to this.coins
			is ShopRecipe -> this.inputs to 0
			else -> return emptyList()
		}
		val stacks = inputs.map { it.toItem() ?: ItemStack.EMPTY }.toMutableList()
		if (coins > 0 && withCoins) {
			stacks.add(Currency.Coin.withAmount(coins))
		}
		return stacks
	}

	fun MobDrop.toItemStack(): ItemStack {
		val drop = this
		val id = when (this) {
			is ItemDrop -> SkyBlockId.item(this.id)
			is PetDrop -> SkyBlockId.pet(this.id, this.tier)
			is EnchantmentDrop -> SkyBlockId.enchantment(this.id, this.level)
			is PotionDrop -> SkyBlockId.potion(this.id, this.level)
			is RuneDrop -> SkyBlockId.rune(this.id, this.tier)
			is AttributeDrop -> SkyBlockId.attribute(this.id)
			else -> null
		}

		val stack = id?.toItem() ?: Items.BARRIER.defaultInstance.apply {
			set(DataComponents.CUSTOM_NAME, Text.of("Unknown Drop (${drop.type()})", TextColor.RED).apply {
				italic = false }
			)
		}

		val copy = stack.copy()

		val newLoreList = buildList {
			addAll(copy.getLore())
			add(Text.of("           ") {
				strikethrough = true
			})

			val chance = Text.of("Chance: ${drop.chance()}%") {
				color = TextColor.GRAY
				italic = false
				drop.condition()?.let { append(" $it") }
			}
			add(chance)
			val amount = Text.of("Amount: ") {
				color = TextColor.GRAY
				italic = false
				if (drop.minAmount() == drop.maxAmount()) {
					append(drop.minAmount().toFormattedString())
				} else {
					append("${drop.minAmount().toFormattedString()} - ${drop.maxAmount().toFormattedString()}")
				}
			}
			add(amount)

			drop.extraLore().forEach {
				add(Text.of(it, TextColor.DARK_GRAY).apply { italic = false })
			}
		}

		copy.set(DataComponents.LORE, ItemLore(newLoreList, newLoreList))
		return copy
	}
}
