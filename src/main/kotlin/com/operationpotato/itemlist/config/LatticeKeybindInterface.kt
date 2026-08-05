package com.operationpotato.itemlist.config

import com.mojang.blaze3d.platform.InputConstants
import com.moulberry.lattice.keybind.KeybindInterface
import com.moulberry.lattice.keybind.LatticeInputType
import com.operationpotato.itemlist.utils.KeyMappingWithModifiers
import net.minecraft.client.KeyMapping
import net.minecraft.client.input.InputQuirks
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
			LatticeInputType.KEYSYM -> InputConstants.Type.KEYSYM.getOrCreate(value)
			LatticeInputType.SCANCODE -> InputConstants.Type.SCANCODE.getOrCreate(value)
			LatticeInputType.MOUSE -> InputConstants.Type.MOUSE.getOrCreate(value)
		}
		keyMapping.setKey(value)
		if (keyMapping is KeyMappingWithModifiers) {
			val ctrlDown = if (InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY) {
				superMod || ctrlMod
			} else ctrlMod
			keyMapping.setModifiers(ctrlDown, shiftMod, altMod)
		}
	}

	override fun setUnbound() {
		keyMapping.setKey(InputConstants.UNKNOWN)
	}

	override fun getConflicts(): Collection<Component> = listOf()
}
