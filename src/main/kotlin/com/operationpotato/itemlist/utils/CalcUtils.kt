package com.operationpotato.itemlist.utils

import com.notkamui.keval.Keval
import com.operationpotato.itemlist.config.ConfigManager
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.LowestBinAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString

object CalcUtils {

	val defaultConstants: Map<String, Double> = mapOf(
		"st" to 64.0,
		"k" to 1_000.0,
		"m" to 1_000_000.0,
		"b" to 1_000_000_000.0,
		"t" to 1_000_000_000_000.0,
	)

	private val customResolvers: Map<String, (String) -> Double> = mapOf(
		"bz" to { id -> BazaarAPI.getProduct(id)?.buyPrice ?: throw Exception("Unknown item $id") },
		"lb" to { id -> LowestBinAPI.getLowestPrice(id)?.toDouble() ?: throw Exception("Unknown item $id") },
		"npc" to { id -> ItemData.getNpcSellPrice(id)?.toDouble() ?: throw Exception("Unknown item $id") },
	)

	private val resolverRegex = Regex("\\b([a-zA-Z_]+)\\(([^)]+)\\)")

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

			caseInsensitiveConstant("purse") { CurrencyAPI.purse }
			defaultConstants.forEach { (k, v) -> caseInsensitiveConstant(k) { v } }
			ConfigManager.get().calculator.customConstants.forEach { (k, v) -> caseInsensitiveConstant(k) { v } }
		}


	fun calculateExpression(text: String): Pair<String, Boolean> {
		var expression = text.removePrefix("=")

		val result = runCatching {
			expression = resolverRegex.replace(expression) { matchResult ->
				val name = matchResult.groupValues[1].trim().lowercase()
				val arg = matchResult.groupValues[2].trim().uppercase()
				customResolvers[name]?.invoke(arg)?.toString() ?: matchResult.value
			}

			calc.eval(expression).toFormattedString()
		}

		return result.fold(
			onSuccess = { "= $it" to true },
			onFailure = { "ERR: ${it.message}" to false }
		)
	}

	fun String.isExpression(): Boolean {
		if (ConfigManager.get().calculator.requiresEquals) return this.startsWith('=')
		return this.any { it in "+-*/^()" }
	}
}
