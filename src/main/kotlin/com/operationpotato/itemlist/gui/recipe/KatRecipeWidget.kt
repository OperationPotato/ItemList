package com.operationpotato.itemlist.gui.recipe

import com.operationpotato.itemlist.SkyBlockItemList
import com.operationpotato.itemlist.gui.PetLevelSlider
import com.operationpotato.itemlist.gui.StackDisplay
import com.operationpotato.itemlist.utils.Currency
import com.operationpotato.itemlist.utils.KatCost
import com.operationpotato.itemlist.utils.RepoLibUtils.getInputItemStacks
import com.operationpotato.itemlist.utils.RepoLibUtils.toItem
import com.operationpotato.itemlist.utils.Utils.formatDuration
import com.operationpotato.itemlist.utils.Utils.topLeftAlignment
import net.minecraft.client.gui.components.ImageWidget
import net.minecraft.client.gui.layouts.GridLayout
import net.minecraft.client.gui.layouts.SpacerElement
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.ItemLore
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.ingredient.PetIngredient
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockPetsRepo
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.italic

class KatRecipeWidget(private val kat: KatRecipe) : AbstractRecipeWidget(kat, 176, 86, "Kat Recipe") {

	private val inputPet = kat.input as? PetIngredient
	private val outputPet = kat.output as? PetIngredient
	private val maxLevel = inputPet?.let(KatCost::maxLevel) ?: KatCost.MAX_LEVEL

	private val inputDisplay = IngredientDisplay(ItemStack.EMPTY)
	private val outputDisplay = IngredientDisplay(ItemStack.EMPTY)
	private val coinsDisplay = IngredientDisplay(ItemStack.EMPTY, false)
	private val levelSlider = PetLevelSlider(maxLevel, KatCost.MIN_LEVEL, ::updateLevel)

	init {
		val hasExtraItems = kat.items.isNotEmpty()
		val texture = if (hasExtraItems) "recipe/kat_extra" else "recipe/kat"
		container.addChild(ImageWidget.sprite(176, 86, SkyBlockItemList.id(texture)))

		addExtra()

		inputDisplay.stack = kat.input.toItem() ?: ItemStack.EMPTY
		outputDisplay.stack = kat.output.toItem() ?: ItemStack.EMPTY

		val (inputX, inputY) = if (hasExtraItems) 20 to 35 else 35 to 35
		container.addChild(inputDisplay, container.topLeftAlignment(inputX, inputY))

		val (coinsX, coinsY) = if (hasExtraItems) 46 to 35 else 61 to 35
		container.addChild(coinsDisplay, container.topLeftAlignment(coinsX, coinsY))

		if (hasExtraItems) {
			val inputGrid = GridLayout()
			inputGrid.spacing(2)

			kat.getInputItemStacks(withCoins = false).forEachIndexed { index, stack ->
				val element = if (!stack.isEmpty) {
					IngredientDisplay(stack)
				} else {
					SpacerElement(StackDisplay.STACK_SIZE, StackDisplay.STACK_SIZE)
				}
				inputGrid.addChild(element, index, 0)
			}

			container.addChild(inputGrid, container.topLeftAlignment(72, 17))
		}

		val timeStack = Items.CLOCK.defaultInstance.apply {
			set(DataComponents.CUSTOM_NAME, Text.of("Kat Duration") {
				color = TextColor.GREEN
				italic = false
			})
			val lore = listOf(Text.of("Time: ${kat.time.formatDuration()}", TextColor.GRAY).apply { italic = false })
			set(DataComponents.LORE, ItemLore(lore, lore))
		}
		val (timeX, timeY) = if (hasExtraItems) 101 to 52 else 90 to 52
		container.addChild(IngredientDisplay(timeStack), container.topLeftAlignment(timeX, timeY))

		val (outputX, outputY) = if (hasExtraItems) 135 to 35 else 124 to 35
		container.addChild(outputDisplay, container.topLeftAlignment(outputX, outputY))

		// sits in the empty strip under the coins slot, clear of the card art and plugin buttons
		container.addChild(levelSlider, container.topLeftAlignment(28, 71))

		if (hasExtraItems) addRequirementsArrow(98, 35) else addRequirementsArrow(87, 35)

		updateLevel(levelSlider.level)

		container.arrangeElements()
	}

	private fun updateLevel(level: Int) {
		coinsDisplay.stack = Currency.Coin.withAmount(KatCost.coins(level, kat.coins))
		inputPet?.let { inputDisplay.stack = petStack(it, level) }

		val outputPet = outputPet
		if (outputPet != null && inputPet != null) {
			val upgradedLevel = KatCost.upgradedLevel(
				level,
				SkyBlockRarity.fromName(inputPet.tier()),
				SkyBlockRarity.fromName(outputPet.tier()),
				KatCost.maxLevel(outputPet),
			)
			outputDisplay.stack = petStack(outputPet, upgradedLevel)
		}
	}

	private fun petStack(pet: PetIngredient, level: Int): ItemStack {
		val query = SkyBlockPetsRepo.Query(id = pet.id(), rarity = SkyBlockRarity.fromName(pet.tier()), level = level)
		return SkyBlockPetsRepo.getLazyItemStack(query)?.create() ?: pet.toItem() ?: ItemStack.EMPTY
	}
}
