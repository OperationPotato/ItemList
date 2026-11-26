package com.operationpotato.itemlist.utils

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.recipes.ingredient.PetIngredient
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import kotlin.math.roundToInt

/**
 * Kat's listed price is the price before any discount, and every level the pet already has
 * takes 0.3% off it (level 1 included). An upgraded pet keeps its experience, so its level
 * shifts by the rarity offset.
 */
object KatCost {
	const val MIN_LEVEL = 1
	const val MAX_LEVEL = 100
	private const val DISCOUNT_PER_LEVEL = 0.003

	// A pet keeps its experience through an upgrade, and every rarity reads the same exp ladder
	// shifted by these offsets, so its level drops by the offset difference.
	private fun offset(rarity: SkyBlockRarity): Int = when (rarity) {
		SkyBlockRarity.COMMON -> 0
		SkyBlockRarity.UNCOMMON -> 6
		SkyBlockRarity.RARE -> 11
		SkyBlockRarity.EPIC -> 16
		else -> 20
	}

	fun maxLevel(pet: PetIngredient): Int {
		if (!RepoAPI.isInitialized()) return MAX_LEVEL
		val bonus = RepoAPI.pets().getPet(pet.id())?.tiers()?.get(pet.tier())?.variablesOffset ?: 0
		return MAX_LEVEL + bonus
	}

	fun upgradedLevel(level: Int, from: SkyBlockRarity, to: SkyBlockRarity, maxLevel: Int): Int {
		return (level + offset(from) - offset(to)).coerceIn(MIN_LEVEL, maxLevel)
	}

	fun coins(level: Int, listedCoins: Int): Int {
		val discount = DISCOUNT_PER_LEVEL * level.coerceIn(0, MAX_LEVEL)
		return (listedCoins * (1.0 - discount)).roundToInt()
	}
}
