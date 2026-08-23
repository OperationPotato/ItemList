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
	var requiresCtrl: Boolean = !InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY && defaultCtrl
	var requiresShift: Boolean = defaultShift
	var requiresAlt: Boolean = defaultAlt
	var requiresSuper: Boolean = InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY && defaultCtrl

	override fun getTranslatedKeyMessage(): Component {
		var prefix = ""
		if (requiresCtrl) prefix += "Ctrl+"
		if (requiresSuper) prefix += if (InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY) "Cmd+" else "Super+"
		if (requiresAlt) prefix += "Alt+"
		if (requiresShift) prefix += "Shift+"
		return Component.literal(prefix).append(super.translatedKeyMessage)
	}

	override fun setKey(key: InputConstants.Key) {
		requiresCtrl = false
		requiresShift = false
		requiresAlt = false
		requiresSuper = false
		super.setKey(key)
	}

	fun setModifiers(ctrl: Boolean, shift: Boolean, alt: Boolean, `super`: Boolean) {
		requiresCtrl = ctrl
		requiresShift = shift
		requiresAlt = alt
		requiresSuper = `super`
	}

	fun resetModifiers() {
		setModifiers(
			!InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY && defaultCtrl,
			defaultShift, defaultAlt,
			InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY && defaultCtrl
		)
	}

	override fun matches(event: KeyEvent): Boolean {
		return matchesModifier(event) && super.matches(event)
	}

	override fun matchesMouse(event: MouseButtonEvent): Boolean {
		return matchesModifier(event) && super.matchesMouse(event)
	}

	fun matchesModifier(event: InputWithModifiers): Boolean {
		if (requiresCtrl && !event.hasControlDown()) return false
		if (requiresAlt && !event.hasAltDown()) return false
		if (requiresShift && !event.hasShiftDown()) return false
		if (requiresSuper && (event.modifiers() and 8) == 0) return false
		return true
	}

	override fun saveString(): String {
		return "v=$VERSION;ctrl=$requiresCtrl;shift=$requiresShift;alt=$requiresAlt;super=$requiresSuper;key=${super.saveString()}"
	}

	fun load(encodedText: String): InputConstants.Key {
		// migrate existing
		if (encodedText.startsWith("key.")) {
			setKey(InputConstants.getKey(encodedText))
			if (this == Keybinds.hideOverlay) {
				if (InputQuirks.REPLACE_CTRL_KEY_WITH_CMD_KEY) requiresSuper = true
				else requiresCtrl = true
			}
			return this.key
		}

		// parse encoded
		val fields = encodedText.split(";", limit = 6)
		for (field in fields) {
			val (fieldKey, value) = field.split("=", limit = 2)
			when {
				fieldKey.startsWith("ctrl") -> requiresCtrl = value.toBoolean()
				fieldKey.startsWith("shift") -> requiresShift = value.toBoolean()
				fieldKey.startsWith("alt") -> requiresAlt = value.toBoolean()
				fieldKey.startsWith("super") -> requiresSuper = value.toBoolean()
				fieldKey.startsWith("key") -> {
					super.setKey(InputConstants.getKey(value))
				}
			}
		}

		return this.key
	}

	companion object {
		const val VERSION = 1
	}
}
