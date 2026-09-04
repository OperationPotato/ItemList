package com.operationpotato.itemlist.utils.search

import kotlin.math.abs

object FuzzySearch {

	fun matches(query: String, input: String): Boolean {
		if (query.isEmpty()) return true
		if (input.contains(query)) return true

		val words = input.split(' ').filter { it.isNotEmpty() }
		if (words.any { containsFuzzy(query, it) }) return true

		val strippedQuery = query.replace(" ", "")
		val strippedInput = input.replace(" ", "")
		return equalsFuzzy(strippedQuery, strippedInput)
	}

	private fun tolerance(length: Int): Int = when {
		length <= 2 -> 0
		length <= 5 -> 1
		else -> 2
	}

	private fun containsFuzzy(query: String, word: String): Boolean {
		val maxDistance = tolerance(query.length)
		if (maxDistance == 0) return false
		if (query.length > word.length + maxDistance) return false

		var previousRow = IntArray(word.length + 1)
		var currentRow = IntArray(word.length + 1)

		for (queryIndex in 1..query.length) {
			currentRow[0] = queryIndex
			var rowMin = currentRow[0]
			for (wordIndex in 1..word.length) {
				val substitutionCost = if (query[queryIndex - 1] == word[wordIndex - 1]) 0 else 1
				currentRow[wordIndex] = minOf(
					previousRow[wordIndex - 1] + substitutionCost,
					previousRow[wordIndex] + 1,
					currentRow[wordIndex - 1] + 1
				)
				if (currentRow[wordIndex] < rowMin) rowMin = currentRow[wordIndex]
			}
			if (rowMin > maxDistance) return false
			val swap = previousRow
			previousRow = currentRow
			currentRow = swap
		}
		return previousRow.min() <= maxDistance
	}

	private fun equalsFuzzy(a: String, b: String): Boolean {
		val maxDistance = tolerance(a.length)
		if (abs(a.length - b.length) > maxDistance) return false

		var previousRow = IntArray(b.length + 1) { it }
		var currentRow = IntArray(b.length + 1)

		for (aIndex in 1..a.length) {
			currentRow[0] = aIndex
			var rowMin = currentRow[0]
			for (bIndex in 1..b.length) {
				val substitutionCost = if (a[aIndex - 1] == b[bIndex - 1]) 0 else 1
				currentRow[bIndex] = minOf(
					previousRow[bIndex - 1] + substitutionCost,
					previousRow[bIndex] + 1,
					currentRow[bIndex - 1] + 1
				)
				if (currentRow[bIndex] < rowMin) rowMin = currentRow[bIndex]
			}
			if (rowMin > maxDistance) return false
			val swap = previousRow
			previousRow = currentRow
			currentRow = swap
		}
		return previousRow[b.length] <= maxDistance
	}
}
