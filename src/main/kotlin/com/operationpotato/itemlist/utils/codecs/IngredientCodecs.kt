package com.operationpotato.itemlist.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Encoder
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.repolib.api.recipes.ingredient.AttributeIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.CraftingIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.CurrencyIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.EnchantmentIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.ItemIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.PetIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.PotionIngredient

object IngredientCodecs {
	val PET: MapCodec<PetIngredient> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			Codec.STRING.fieldOf("pet").forGetter(PetIngredient::id),
			Codec.STRING.fieldOf("tier").forGetter(PetIngredient::tier),
			Codec.INT.optionalFieldOf("count", 1).forGetter(PetIngredient::count)
		).apply(instance, ::PetIngredient)
	}

	val ITEM: MapCodec<ItemIngredient> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			Codec.STRING.fieldOf("id").forGetter(ItemIngredient::id),
			Codec.INT.optionalFieldOf("count", 1).forGetter(ItemIngredient::count)
		).apply(instance, ::ItemIngredient)
	}

	val ENCHANTMENT: MapCodec<EnchantmentIngredient> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			Codec.STRING.fieldOf("id").forGetter(EnchantmentIngredient::id),
			Codec.INT.fieldOf("level").forGetter(EnchantmentIngredient::level),
			Codec.INT.optionalFieldOf("count", 1).forGetter(EnchantmentIngredient::count)
		).apply(instance, ::EnchantmentIngredient)
	}

	val ATTRIBUTE: MapCodec<AttributeIngredient> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			Codec.STRING.fieldOf("id").forGetter(AttributeIngredient::id),
			Codec.INT.optionalFieldOf("count", 1).forGetter(AttributeIngredient::count)
		).apply(instance, ::AttributeIngredient)
	}

	val CURRENCY: MapCodec<CurrencyIngredient> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			Codec.STRING.fieldOf("currency").forGetter(CurrencyIngredient::currency),
			Codec.INT.optionalFieldOf("count", 1).forGetter(CurrencyIngredient::count)
		).apply(instance, ::CurrencyIngredient)
	}

	val POTION: MapCodec<PotionIngredient> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			Codec.STRING.fieldOf("id").forGetter(PotionIngredient::id),
			Codec.INT.fieldOf("level").forGetter(PotionIngredient::level),
			Codec.INT.optionalFieldOf("count", 1).forGetter(PotionIngredient::count)
		).apply(instance, ::PotionIngredient)
	}

	val DISPATCH: Codec<CraftingIngredient> = Codec.STRING.dispatch(
		"type",
		CraftingIngredient::type
	) { type ->
		val codec: MapCodec<out CraftingIngredient> = when (type) {
			"pet" -> PET
			"item" -> ITEM
			"enchantment" -> ENCHANTMENT
			"attribute" -> ATTRIBUTE
			"currency" -> CURRENCY
			"potion" -> POTION
			else -> throw IllegalArgumentException("Unknown ingredient type: $type")
		}
		codec
	}

	val CRAFTING_INGREDIENT: Codec<CraftingIngredient> = Codec.of(
		DISPATCH as Encoder<CraftingIngredient>,
		object : Decoder<CraftingIngredient> {
			override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<CraftingIngredient, T>> {
				val typeElement = ops.get(input, "type").result()
				if (typeElement.isEmpty) {
					return ITEM.codec().decode(ops, input).map { Pair.of(it.first as CraftingIngredient, it.second) }
				}
				return DISPATCH.decode(ops, input)
			}
		}
	)
}
