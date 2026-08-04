package com.operationpotato.itemlist.utils

import com.mojang.blaze3d.platform.InputConstants
import com.operationpotato.itemlist.Keybinds
import net.minecraft.client.KeyMapping
import net.minecraft.client.input.InputQuirks
import net.minecraft.client.input.InputWithModifiers
import net.minecraft.client.input.KeyEvent
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.network.chat.Component

class KeyMappingWithModifiers(
	name: String, keysym: Int, category: Category,
	val defaultCtrl: Boolean = false, val defaultShift: Boolean = false, val defaultAlt: Boolean = false
) : KeyMapping(name, keysym, category) {
	var requiresCtrl: Boolean = defaultCtrl
	var requiresShift: Boolean = defaultShift
	var requiresAlt: Boolean = defaultAlt

	override fun getTranslatedKeyMessage(): Component {
		var prefix = ""
		if (requiresCtrl) prefix += if (InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY) "Cmd+" else "Ctrl+"
		if (requiresAlt) prefix += "Alt+"
		if (requiresShift) prefix += "Alt+"
		return Component.literal(prefix).append(super.translatedKeyMessage)
	}

	override fun setKey(key: InputConstants.Key) {
		requiresCtrl = false
		requiresAlt = false
		requiresShift = false
		super.setKey(key)
	}

	fun setModifiers(ctrl: Boolean, shift: Boolean, alt: Boolean) {
		requiresCtrl = ctrl
		requiresShift = shift
		requiresAlt = alt
	}

	fun resetModifiers() {
		setModifiers(defaultCtrl, defaultShift, defaultAlt)
	}

	override fun matches(event: KeyEvent): Boolean {
		return matchesModifier(event) && super.matches(event)
	}

	override fun matchesMouse(event: MouseButtonEvent): Boolean {
		return matchesModifier(event) && super.matchesMouse(event)
	}

	fun matchesModifier(event: InputWithModifiers): Boolean {
		if (requiresCtrl && !event.hasControlDownWithQuirk()) return false
		if (requiresAlt && !event.hasAltDown()) return false
		if (requiresShift && !event.hasShiftDown()) return false
		return true
	}

	override fun saveString(): String {

		return "ctrl=$requiresCtrl;shift=$requiresShift;alt=$requiresAlt;key=${super.saveString()}"
	}

	fun load(encodedText: String): InputConstants.Key {
		// migrate existing
		if (encodedText.startsWith("key.")) {
			setKey(InputConstants.getKey(encodedText))
			if (this == Keybinds.hideOverlay) requiresCtrl = true
			return this.key
		}

		// parse encoded
		val fields = encodedText.split(";", limit = 4)
		for (field in fields) {
			val (fieldKey, value) = field.split("=", limit = 2)
			when {
				fieldKey.startsWith("ctrl") -> requiresCtrl = value.toBoolean()
				fieldKey.startsWith("shift") -> requiresShift = value.toBoolean()
				fieldKey.startsWith("alt") -> requiresAlt = value.toBoolean()
				fieldKey.startsWith("key") -> {
					super.setKey(InputConstants.getKey(value))
				}
			}
		}

		return this.key
	}
}
