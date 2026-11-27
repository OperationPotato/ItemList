package com.operationpotato.itemlist.utils

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Unlike registryBoundLazy, this is also invalidated when the repo reloads, and it won't cache a result
 * built before the repo was ready (e.g. an empty item list)
 */
class ReloadableLazy<T : Any>(
	private val cacheable: (T) -> Boolean = { true },
	private val factory: () -> T,
) : Lazy<T> {
	@Volatile
	private var cached: T? = null
	private val generation = AtomicInteger()

	init {
		instances += this
	}

	override val value: T
		get() {
			cached?.let { return it }
			return synchronized(this) {
				cached?.let { return@synchronized it }
				val startGeneration = generation.get()
				val built = factory()
				// Don't cache if invalidated while building or if the result isn't final yet
				if (cacheable(built) && startGeneration == generation.get()) cached = built
				built
			}
		}

	override fun isInitialized(): Boolean = cached != null

	fun invalidate() {
		generation.incrementAndGet()
		cached = null
	}

	companion object {
		private val instances = CopyOnWriteArrayList<ReloadableLazy<*>>()

		fun invalidateAll() = instances.forEach { it.invalidate() }
	}
}

fun <T : Any> reloadableLazy(cacheable: (T) -> Boolean = { true }, factory: () -> T): Lazy<T> =
	ReloadableLazy(cacheable, factory)
