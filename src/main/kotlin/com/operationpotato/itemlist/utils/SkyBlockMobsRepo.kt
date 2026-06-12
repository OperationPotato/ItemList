package com.operationpotato.itemlist.utils

import com.mojang.authlib.properties.Property
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.TooltipDisplay
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.mobs.Mob
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.RepoItemCache
import tech.thatgravyboat.skyblockapi.platform.ResolvableProfile
import tech.thatgravyboat.skyblockapi.utils.extentions.compoundTag
import tech.thatgravyboat.skyblockapi.utils.extentions.toData
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic
import kotlin.jvm.optionals.getOrNull

object SkyBlockMobsRepo : RepoItemCache<String>("Mobs") {
	val npcSuffixes = listOf("NPC", "Rift NPC")
	private const val ID_KEY = "skyblock-item-list:id"

	private val repo get() = RepoAPI.mobs()

	override fun create(key: String): LazyItemStack? {
		val data = repo.getMob(key) ?: return null

		val stackName = Text.of(data.name) {
			italic = false
			data.type?.let { append(" ($it)") }
		}
		val lore = createLore(data)

		val item = Identifier.parse(data.itemId().lowercase())
			?.let(BuiltInRegistries.ITEM::getValue)
			?.takeUnless { it == Items.AIR }
			?: Items.BARRIER

		val stack = LazyItemStack(item.takeIf { data.texture == null } ?: Items.PLAYER_HEAD) {
			if (data.texture != null) {
				this[DataComponents.PROFILE] = ResolvableProfile { put("textures", Property("textures", data.texture)) }
			}
			this[DataComponents.CUSTOM_NAME] = stackName
			if (lore.isNotEmpty()) {
				this[DataComponents.LORE] = ItemLore(lore)
				this[DataComponents.TOOLTIP_DISPLAY] =
					TooltipDisplay.DEFAULT.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true)
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
