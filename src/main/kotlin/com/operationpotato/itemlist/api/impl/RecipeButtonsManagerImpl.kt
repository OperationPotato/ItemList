package com.operationpotato.itemlist.api.impl

import com.operationpotato.itemlist.api.RecipeButtonConsumer
import com.operationpotato.itemlist.api.RecipeButtonManager
import com.operationpotato.itemlist.api.RecipeButtonProvider
import com.operationpotato.itemlist.utils.RepoLibUtils.result
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.input.MouseButtonEvent
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.repolib.api.recipes.Recipe

@ApiStatus.Internal
class RecipeButtonsManagerImpl : RecipeButtonManager {
	private val providers = mutableListOf<RecipeButtonProvider>()
	private val consumers = mutableListOf<RecipeButtonConsumer>()

	override fun addProvider(provider: RecipeButtonProvider) {
		providers.add(provider)
	}

	override fun addConsumer(consumer: RecipeButtonConsumer) {
		consumers.add(consumer)
	}

	fun getButtons(recipe: Recipe<*>): List<AbstractWidget> {
		val stack = recipe.result()?.toItem() ?: return emptyList()
		return providers.flatMap { it.provideMultiple(recipe, stack) }
	}

	fun onButtonClick(widget: AbstractWidget, event: MouseButtonEvent) {
		consumers.forEach { it.consume(widget, event) }
	}
}
