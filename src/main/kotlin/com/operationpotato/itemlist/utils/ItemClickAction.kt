package com.operationpotato.itemlist.utils

import com.operationpotato.itemlist.gui.recipe.RecipeScreen
import com.operationpotato.itemlist.utils.SkyBlockMobsRepo.getMobId
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.IdOverlaysAPI
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.DELIMITER
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Suppress("unused")
enum class ItemClickAction(val action: (ItemStack) -> Unit) {
	OPEN_RECIPE({ stack -> RecipeScreen.openRecipeForItem(stack, McScreen.self) }),
	OPEN_USAGE({ stack -> RecipeScreen.openUsageForItem(stack, McScreen.self) }),
	OPEN_INDEPENDENT_WIKI({ handleWiki(it, "independent") { overlay -> overlay.independent } }),
	OPEN_OFFICIAL_WIKI({ handleWiki(it, "official") { overlay -> overlay.official } }),
	NOTHING({ _ -> }),
	;

	private val formattedName = toFormattedName()
	override fun toString() = formattedName
}

private fun handleWiki(stack: ItemStack, type: String, getter: (IdOverlaysAPI.WikiData) -> String?) {
	val link = getWikiOverlay(stack)?.let(getter)
	if (link != null) {
		McClient.openUri(link)
	} else {
		Text.of("No $type wiki link found for ") {
			color = TextColor.RED
			append(stack.cleanName, TextColor.LIGHT_PURPLE)
			append("!")
		}.send()
	}
}

private fun getWikiOverlay(stack: ItemStack): IdOverlaysAPI.WikiData? {
	if (!RepoAPI.isInitialized()) return null
	val api = RepoAPI.overlays()

	val id = stack.getSkyBlockId()
	if (id == null) {
		val mobId = stack.getMobId() ?: return null
		return api.getMob(mobId)?.wiki
	}

	val cleanId = id.cleanId
	val base = cleanId.substringBeforeLast(DELIMITER)
	val suffix = cleanId.substringAfterLast(DELIMITER)

	return when {
		id.isItem -> api.getItem(cleanId)
		id.isAttribute -> api.getAttribute(cleanId)
		id.isPet -> api.getPet(base, suffix)
		id.isPotion -> api.getPotion(base, suffix.toIntValue())
		id.isEnchantment -> api.getEnchantment(base, suffix.toIntValue())
		id.isRune -> api.getRune(base, suffix.toIntValue())
		else -> null
	}?.wiki
}
