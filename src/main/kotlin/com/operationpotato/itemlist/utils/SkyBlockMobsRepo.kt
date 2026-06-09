package com.operationpotato.itemlist.utils

import com.mojang.authlib.properties.Property
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.RepoItemCache
import tech.thatgravyboat.skyblockapi.platform.ResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.extentions.compoundTag
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style
import kotlin.jvm.optionals.getOrNull

object SkyBlockMobsRepo : RepoItemCache<String>("Mobs") {
	val mobSuffixes = listOf("MONSTER", "SC", "MINIBOSS", "BOSS")
	val suffixesToCapitalize = listOf("SC", "NPC")
	private const val ID_KEY = "skyblock-item-list:id"

	private val repo get() = RepoAPI.mobs()

	override fun create(key: String): LazyItemStack? {
		val data = repo.getMob(key) ?: return null
		val type = key.substringAfterLast("_")
		val shouldTitleCase = !suffixesToCapitalize.any { type == it }
		var suffix = if (shouldTitleCase) type.toTitleCase() else type
		// TODO: remove this when SkyBlockAPI gives the NEU mob type directly
		if (data.island == "rift") suffix = "Rift $suffix"

		val stackName = Component.literal("${data.name} ($suffix)")
			.style { withItalic(false) }
		val lore = createLore(data)

		// TODO: replace barrier with mob item when it's available
		val stack = LazyItemStack(Items.BARRIER.takeIf { data.texture == null } ?: Items.PLAYER_HEAD) {
			if (data.texture != null) {
				this[DataComponents.PROFILE] = ResolvableProfile { put("textures", Property("textures", data.texture)) }
			}
			this[DataComponents.CUSTOM_NAME] = stackName
			if (lore.isNotEmpty()) {
				this[DataComponents.LORE] = ItemLore(lore)
			}
			// Set fake id for favoriting & links
			this[DataComponents.CUSTOM_DATA] = compoundTag {
				putString(ID_KEY, key)
			}.toData()
		}
		return stack
	}

	private fun createLore(mob: Mob): List<Component> {
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
