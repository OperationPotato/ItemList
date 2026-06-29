package com.operationpotato.itemlist.utils

import com.notkamui.keval.Keval
import com.operationpotato.itemlist.config.ConfigManager
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.LowestBinAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString

object CalcUtils {

	private val defaultConstants: Map<String, Double> = mapOf(
		"st" to 64.0,
		"k" to 1_000.0,
		"m" to 1_000_000.0,
		"b" to 1_000_000_000.0,
		"t" to 1_000_000_000_000.0,
	)

	private val extraConstants: Map<String, () -> Double> = mapOf(
		"purse" to { CurrencyAPI.purse }
	)

	private val customResolvers: Map<String, (String) -> Double> = mapOf(
		"bz" to { id -> BazaarAPI.getProduct(id)?.buyPrice ?: throw UnknownItemException(id) },
		"lb" to { id -> LowestBinAPI.getLowestPrice(id)?.toDouble() ?: throw UnknownItemException(id) },
		"npc" to { id -> ItemData.getNpcSellPrice(id)?.toDouble() ?: throw UnknownItemException(id) },
	)

	private val resolverRegex = Regex("\\b([a-zA-Z_]+)\\(([^)]+)\\)")

	private val allConstants
		get(): Map<String, Double> =
			defaultConstants + extraConstants.mapValues { it.value() } + ConfigManager.get().calculator.customConstants

	val calc
		get() = Keval.create {
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

			allConstants.forEach { (k, v) -> caseInsensitiveConstant(k) { v } }
		}

	private fun evaluateOrThrow(text: String): Double {
		var expression = text.removePrefix("=")

		expression = resolverRegex.replace(expression) { matchResult ->
			val name = matchResult.groupValues[1].trim().lowercase()
			val arg = matchResult.groupValues[2].trim().uppercase()
			val result = customResolvers[name]?.invoke(arg) ?: return@replace matchResult.value
			"%.2f".format(result)
		}

		expression = expression.replace(Regex("(?i)((?:\\d+)?\\.?\\d*)(\\w+)")) { match ->
			val num = match.groupValues[1].toDoubleOrNull() ?: 1.0
			val constName = match.groupValues[2].lowercase()
			val constValue = allConstants[constName]
			if (constValue != null) {
				(num * constValue).toString()
			} else {
				match.value
			}
		}

		return calc.eval(expression)
	}

	fun calculateExpression(text: String): Pair<String, Boolean> {
		val isExplicit = text.startsWith("=")
		val result = runCatching { evaluateOrThrow(text) }

		return result.fold(
			onSuccess = { "= ${it.toFormattedString()}" to true },
			onFailure = {
				// Only display the error if it's the custom item warning or if equals sign
				if (it is UnknownItemException || isExplicit) {
					"ERR: ${it.message}" to false
				} else {
					"" to false
				}
			}
		)
	}

	fun String.isExpression(): Boolean {
		val equals = this.startsWith('=')
		if (ConfigManager.get().calculator.requiresEquals) return equals
		if (equals) return true

		return runCatching { evaluateOrThrow(this) }.fold(
			onSuccess = { true },
			onFailure = { it is UnknownItemException }
		)
	}

	data class UnknownItemException(val id: String) : Exception("Unknown item $id")
}
