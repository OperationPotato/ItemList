package com.operationpotato.itemlist.utils

import com.mojang.authlib.properties.Property
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.RepoItemCache
import tech.thatgravyboat.skyblockapi.platform.ResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.extentions.toTitleCase
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style

object SkyBlockMobsRepo : RepoItemCache<String>("Mobs") {
	val mobSuffixes = listOf("MONSTER", "SC", "MINIBOSS", "BOSS")
	val suffixesToCapitalize = listOf("SC", "NPC")

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
		// TODO: replace barrier with mob item when it's available
		val stack = LazyItemStack(Items.BARRIER.takeIf { data.texture == null } ?: Items.PLAYER_HEAD) {
			if (data.texture != null) {
				this[DataComponents.PROFILE] = ResolvableProfile { put("textures", Property("textures", data.texture)) }
			}
			this[DataComponents.CUSTOM_NAME] = stackName
		}
		return stack
	}
}
