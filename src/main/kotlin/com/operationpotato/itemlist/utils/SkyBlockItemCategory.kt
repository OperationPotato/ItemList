package com.operationpotato.itemlist.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

enum class SkyBlockItemCategory(val formattedName: String) {
	ATTRIBUTE("Attributes"),
	ENCHANTMENT("Enchants"),
	ITEM("Items"),
	MOB("Mobs"),
	NPC("NPCs"),
	PET("Pets"),
	POTION("Potions"),
	RUNE("Runes"),
	ALL("All"),
	CUSTOM("Custom"),
	;

	fun asComponent(): MutableComponent {
		return Component.literal(this.formattedName)
	}

	companion object {
		val NON_ENTITIES = entries.filter { it != MOB && it != NPC }
	}
}
