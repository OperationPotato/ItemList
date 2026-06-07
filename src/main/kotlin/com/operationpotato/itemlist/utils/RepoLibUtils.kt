package com.operationpotato.itemlist.utils

import net.minecraft.world.item.ItemStack
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
			is PotionIngredient -> SkyBlockId.potion(this.id(), this.level())
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
			is SkyBlockId -> id.toItem().apply { if (withStackSize) this.count = count }
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
}
