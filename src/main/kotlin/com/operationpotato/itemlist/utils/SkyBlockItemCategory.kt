package com.operationpotato.itemlist.utils

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.StringRepresentable

enum class SkyBlockItemCategory(val formattedName: String) : StringRepresentable {
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

	override fun getSerializedName(): String = name
	fun asComponent(): MutableComponent {
		return Component.literal(this.formattedName)
	}

	companion object {
		val NON_ENTITIES = entries.filter { it != MOB && it != NPC }
	}
}
