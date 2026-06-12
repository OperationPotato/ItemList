package com.operationpotato.itemlist.utils

import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockAttributesRepo
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockPetsRepo
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockPotionsRepo
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockRunesRepo
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy

object SkyBlockItems {
	val items by registryBoundLazy { getAllItems() }

	private val attributeNames by registryBoundLazy { getAllAttributeNames() }
	private val enchantNames by registryBoundLazy { getAllEnchantmentNames() }
	private val itemNames by registryBoundLazy { getAllItemNames() }
	private val entityNames by registryBoundLazy { getAllEntityNames() }
	private val petNames by registryBoundLazy { getAllPetNames() }
	private val potionNames by registryBoundLazy { getAllPotionNames() }
	private val runeNames by registryBoundLazy { getAllRuneNames() }

	private val numeralPattern = Regex("[_;]([0-9]+)$")

	private fun sortByIdAndNumber(aItem: Item, bItem: Item): Int {
		val a = aItem.id
		val b = bItem.id
		val aMatch = numeralPattern.find(a)
		val bMatch = numeralPattern.find(b)
		if (aMatch != null && bMatch != null) {
			val aStart = a.substring(0, aMatch.range.first)
			val bStart = b.substring(0, bMatch.range.first)
			val comparison = aStart.compareTo(bStart)
			if (comparison == 0) {
				val aNum = aMatch.groupValues[1].toIntOrNull() ?: return a.compareTo(b)
				val bNum = bMatch.groupValues[1].toIntOrNull() ?: return a.compareTo(b)
				return aNum.compareTo(bNum)
			}
		}
		return a.compareTo(b)
	}

	private fun getAllAttributeNames(): List<String> {
		return RepoAPI.attributes().attributes().keys.toList()
	}

	private fun getAllEnchantmentNames(): List<SkyBlockEnchantmentsRepo.Query> {
		return RepoAPI.enchantments().enchantments().map { (_, v) ->
			v.levels.map { x -> SkyBlockEnchantmentsRepo.Query(id = v.id, level = x.key) }
		}.flatten()
	}

	private fun getAllItemNames(): List<String> {
		return RepoAPI.items().items().keys.toList()
	}

	private fun getAllEntityNames(): List<String> {
		return RepoAPI.mobs().mobs().keys.toList()
	}

	private fun getAllPetNames(): List<SkyBlockPetsRepo.Query> {
		return RepoAPI.pets().pets().map { (k, v) ->
			v.tiers.map { x ->
				val maxLevel = 100 + x.value.variablesOffset
				SkyBlockPetsRepo.Query(id = k, rarity = SkyBlockRarity.fromName(x.key), level = maxLevel)
			}
		}.flatten()
	}

	private fun getAllPotionNames(): List<SkyBlockPotionsRepo.Query> {
		return RepoAPI.potions().potions().map { (_, v) ->
			v.levels.map { x -> SkyBlockPotionsRepo.Query(id = v.id, level = x.key) }
		}.flatten()
	}

	private fun getAllRuneNames(): List<SkyBlockRunesRepo.Query> {
		return RepoAPI.runes().runes().map { (_, v) ->
			v.map { x -> SkyBlockRunesRepo.Query(id = x.id, tier = x.tier) }
		}.flatten()
	}

	private fun getAllItems(): List<Item> {
		if (!RepoAPI.isInitialized()) return listOf()
		val allItems: MutableList<Item> = mutableListOf()

		attributeNames.forEach { key ->
			val stack = SkyBlockAttributesRepo.getLazyItemStack(key) ?: return@forEach
			val isVanilla = RepoAPI.overlays().getAttribute(key)?.vanilla() ?: false
			allItems.add(Item(stack, SkyBlockItemCategory.ATTRIBUTE, key, isVanilla))
		}

		enchantNames.forEach { key ->
			val stack = SkyBlockEnchantmentsRepo.getLazyItemStack(key) ?: return@forEach
			val id = key.id.replace(Regex("^ULTIMATE_"), "")
			val isVanilla = RepoAPI.overlays().getEnchantment(key.id, key.level ?: 1)?.vanilla() ?: false
			allItems.add(Item(stack, SkyBlockItemCategory.ENCHANTMENT, "$id;${key.level}", isVanilla))
		}

		itemNames.forEach { key ->
			val stack = SkyBlockItemsRepo.getLazyItemStack(key) ?: return@forEach
			val isVanilla = RepoAPI.overlays().getItem(key)?.vanilla() ?: false
			allItems.add(Item(stack, SkyBlockItemCategory.ITEM, key, isVanilla))
		}

		entityNames.forEach { key ->
			val stack = SkyBlockMobsRepo.getLazyItemStack(key) ?: return@forEach
			val type = key.substringAfterLast("_")
			val isNpc = SkyBlockMobsRepo.npcSuffixes.any { type == it }
			val category = if (isNpc) SkyBlockItemCategory.NPC else SkyBlockItemCategory.MOB
			val isVanilla = RepoAPI.overlays().getMob(key)?.vanilla() ?: false
			allItems.add(Item(stack, category, key, isVanilla))
		}

		petNames.forEach { key ->
			val stack = SkyBlockPetsRepo.getLazyItemStack(key) ?: return@forEach
			val isVanilla = RepoAPI.overlays().getPet(key.id, key.rarity.name)?.vanilla() ?: false
			allItems.add(Item(stack, SkyBlockItemCategory.PET, "${key.id};${key.rarity.ordinal}", isVanilla))
		}

		potionNames.forEach { key ->
			val stack = SkyBlockPotionsRepo.getLazyItemStack(key) ?: return@forEach
			val id = key.id.replace(Regex("^POTION_"), "")
			val isVanilla = RepoAPI.overlays().getPotion(key.id, key.level ?: 1)?.vanilla() ?: false
			allItems.add(Item(stack, SkyBlockItemCategory.POTION, "$id;${key.level}", isVanilla))
		}

		runeNames.forEach { key ->
			val stack = SkyBlockRunesRepo.getLazyItemStack(key) ?: return@forEach
			val isVanilla = RepoAPI.overlays().getRune(key.id, key.tier ?: 1)?.vanilla() ?: false
			allItems.add(Item(stack, SkyBlockItemCategory.RUNE, "${key.id};${key.id}", isVanilla))
		}

		return allItems.sortedWith(::sortByIdAndNumber)
	}

	data class Item(
		val stack: LazyItemStack,
		val category: SkyBlockItemCategory,
		val id: String,
		val isVanilla: Boolean = false
	)
}
