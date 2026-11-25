package com.operationpotato.itemlist.utils

import com.operationpotato.itemlist.utils.SkyBlockMobsRepo.getMobId
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient

object AbiphoneCallnames {

	fun getCallname(stack: ItemStack): String? {
		if (!RepoAPI.isInitialized()) return null
		val mobId = stack.getMobId() ?: return null
		return RepoAPI.overlays().getMob(mobId)?.abiphoneCallnames?.firstOrNull()
	}

	fun call(stack: ItemStack): Boolean {
		val callname = getCallname(stack) ?: return false
		McClient.sendCommand("call $callname")
		return true
	}
}
