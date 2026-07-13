package com.operationpotato.itemlist.compat

import net.minecraft.world.item.ItemStack
import java.util.function.BiConsumer

object IconographicCompat {

	var withItemCallback: ((item: ItemStack, runnable: () -> Unit) -> Unit) = { _, runnable -> runnable() }

	fun withItem(item: ItemStack, runnable: () -> Unit) {
		withItemCallback(item, runnable)
	}

	fun setupItemCompat(consumer: BiConsumer<ItemStack, Runnable>) {
		this.withItemCallback = { item, runnable -> consumer.accept(item) { runnable() } }
	}

}
