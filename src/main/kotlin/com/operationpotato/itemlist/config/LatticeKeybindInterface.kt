package com.operationpotato.itemlist.config

import com.mojang.blaze3d.platform.InputConstants
import com.moulberry.lattice.keybind.KeybindInterface
import com.moulberry.lattice.keybind.LatticeInputType
import com.operationpotato.itemlist.utils.KeyMappingWithModifiers
import net.minecraft.client.KeyMapping
import net.minecraft.network.chat.Component

class LatticeKeybindInterface(val keyMapping: KeyMapping) : KeybindInterface {
	override fun getKeyMessage(): Component = keyMapping.translatedKeyMessage

	override fun setKey(
		type: LatticeInputType,
		value: Int,
		shiftMod: Boolean,
		ctrlMod: Boolean,
		altMod: Boolean,
		superMod: Boolean
	) {
		val value = when (type) {
			//~ if >26.2 'KEYSYM' -> 'KEYBOARD'
			LatticeInputType.KEYSYM -> InputConstants.Type.KEYBOARD.getOrCreate(value)
			//? if <=26.2
			/*LatticeInputType.SCANCODE -> InputConstants.Type.SCANCODE.getOrCreate(value)*/
			LatticeInputType.MOUSE -> InputConstants.Type.MOUSE.getOrCreate(value)
			//? if >26.2
			else -> return
		}
		keyMapping.setKey(value)
		if (keyMapping is KeyMappingWithModifiers) {
			keyMapping.setModifiers(ctrlMod, shiftMod, altMod, superMod)
		}
	}

	override fun setUnbound() {
		keyMapping.setKey(InputConstants.UNKNOWN)
	}

	override fun getConflicts(): Collection<Component> = listOf()
}
