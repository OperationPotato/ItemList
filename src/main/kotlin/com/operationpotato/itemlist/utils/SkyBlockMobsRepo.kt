package com.operationpotato.itemlist.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.repolib.api.mobs.drop.ItemDrop
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.RepoItemCache
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.strikethrough
import kotlin.jvm.optionals.getOrNull

object SkyBlockMobsRepo : RepoItemCache<String>("sbil:Mobs") {
	val npcSuffixes = listOf("NPC", "Rift NPC")
	private const val ID_KEY = "skyblock-item-list:id"

	private val repo get() = RepoAPI.mobs()

	val mobsByDropId: Map<SkyBlockId, Set<Mob>> by registryBoundLazy {
		val grouped = mutableMapOf<SkyBlockId, MutableSet<Mob>>()
		if (RepoAPI.isInitialized()) {
			RepoAPI.mobs().mobs().values.forEach { mob ->
				mob.lootTables.forEach { table ->
					table.drops.forEach { drop ->
						if (drop is ItemDrop) {
							val id = SkyBlockId.item(drop.id)
							grouped.getOrPut(id) { mutableSetOf() }.add(mob)
						}
					}
				}
			}
		}
		grouped
	}

	fun getMobsForId(id: SkyBlockId): Set<Mob> = mobsByDropId[id] ?: emptySet()

	fun get(key: String): Mob? = repo.getMob(key)

	override fun create(key: String): LazyItemStack? {
		val data = get(key) ?: return null
		val realStack =
			tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockMobsRepo.getLazyItemStack(key) ?: return null

		val stack = realStack.withComponents {
			// Set fake id for favorites & links
			val customData = realStack[DataComponents.CUSTOM_DATA]?.copyTag() ?: CompoundTag()
			customData.putString(ID_KEY, key)
			this[DataComponents.CUSTOM_DATA] = customData.toData()

			// Add pos to lore
			val loreLines = realStack[DataComponents.LORE]?.lines?.toMutableList() ?: mutableListOf()
			if (loreLines.isNotEmpty() && loreLines.all { it.string.isBlank() }) loreLines.clear()
			createExtraLore(data).let {
				if (it.isEmpty()) return@let
				if (loreLines.isNotEmpty()) {
					loreLines.add(Text.of("           ") {
						strikethrough = true
						color = TextColor.DARK_GRAY
					})
				}
				loreLines.addAll(it)
			}
			this[DataComponents.LORE] = ItemLore(loreLines)
		}

		return stack
	}

	private fun createExtraLore(mob: Mob): List<Component> {
		val island = SkyBlockIsland.getById(mob.island ?: "")?.displayName ?: return listOf()
		val pos = mob.position

		val style = Style.EMPTY.withItalic(false).withColor(TextColor.GRAY)
		val lineEnding = if (pos == null) "." else ""

		return listOfNotNull(
			Text.of("Located in ").append(Text.of(island).withColor(TextColor.GOLD)).append(lineEnding)
				.withStyle(style),
			if (pos == null) null else
				Text.of("at ").append(Text.of("${pos.x}, ${pos.y}, ${pos.z}").withColor(TextColor.WHITE)).append(".")
					.withStyle(style)
		)
	}

	fun ItemStack.getMobId(): String? = this.get(DataComponents.CUSTOM_DATA)?.copyTag()?.getString(ID_KEY)?.getOrNull()
}
