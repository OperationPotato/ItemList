package com.operationpotato.itemlist.utils

import com.notkamui.keval.Keval
import com.notkamui.keval.KevalNumbers
import com.operationpotato.itemlist.Settings
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString

object CalcUtils {

	val defaultConstants: Map<String, Double> = mapOf(
		"st" to 64.toDouble(),
		"k" to 1_000.toDouble(),
		"m" to 1_000_000.toDouble(),
		"b" to 1_000_000_000.toDouble(),
		"t" to 1_000_000_000_000.toDouble(),
	)

	val calc
		get() = Keval.create(KevalNumbers.real) {
			includeDefault()

			fun caseInsensitiveConstant(name: String, amount: () -> Double) {
				constant {
					this.name = name.lowercase()
					this.value = amount()
				}
				constant {
					this.name = name.uppercase()
					this.value = amount()
				}
			}

			caseInsensitiveConstant("purse") { CurrencyAPI.purse }
			defaultConstants.forEach { (k, v) -> caseInsensitiveConstant(k) { v } }
			Settings.customConstants.forEach { (k, v) -> caseInsensitiveConstant(k) { v } }
		}


	fun calculateExpression(text: String): String {
		val expression = text.let { if (it.startsWith('=')) it.substring(1) else it }
		val result = runCatching {
			calc.eval(expression).toFormattedString()
		}

		val exception = result.exceptionOrNull()
		if (exception != null) {
			return "ERR: ${exception.message}"
		}
		return "= ${result.getOrNull()}"
	}

	fun String.isExpression(): Boolean {
		if (Settings.requiresEquals) return this.startsWith('=')
		return this.any { it in "+-*/^()" }
	}
}
