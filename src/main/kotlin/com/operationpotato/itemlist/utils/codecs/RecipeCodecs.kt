package com.operationpotato.itemlist.utils.codecs

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.Decoder
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.Encoder
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.repolib.api.recipes.CraftingRecipe
import tech.thatgravyboat.repolib.api.recipes.ForgeRecipe
import tech.thatgravyboat.repolib.api.recipes.KatRecipe
import tech.thatgravyboat.repolib.api.recipes.Recipe
import tech.thatgravyboat.repolib.api.recipes.ShopRecipe
import tech.thatgravyboat.repolib.api.recipes.ingredient.CraftingIngredient
import tech.thatgravyboat.repolib.api.recipes.ingredient.EmptyIngredient

object RecipeCodecs {
	private data class CraftingFormat(val keys: List<CraftingIngredient>, val pattern: List<Int>, val result: CraftingIngredient)

	private val RAW_CRAFTING_FORMAT: MapCodec<CraftingFormat> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			IngredientCodecs.CRAFTING_INGREDIENT.listOf().fieldOf("keys").forGetter(CraftingFormat::keys),
			Codec.INT.listOf().fieldOf("pattern").forGetter(CraftingFormat::pattern),
			IngredientCodecs.CRAFTING_INGREDIENT.fieldOf("result").forGetter(CraftingFormat::result)
		).apply(instance, ::CraftingFormat)
	}

	val CRAFTING: MapCodec<CraftingRecipe> = RAW_CRAFTING_FORMAT.xmap(
		{ raw ->
			val resolvedInputs = raw.pattern.map { index ->
				if (index == -1) EmptyIngredient.INSTANCE else raw.keys[index]
			}
			CraftingRecipe(resolvedInputs, raw.result)
		},
		{ recipe ->
			val keys = recipe.inputs.distinct().filter { it != EmptyIngredient.INSTANCE }
			val pattern = recipe.inputs.map { if (it == EmptyIngredient.INSTANCE) -1 else keys.indexOf(it) }
			CraftingFormat(keys, pattern, recipe.result)
		}
	)

	val FORGE: MapCodec<ForgeRecipe> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			IngredientCodecs.CRAFTING_INGREDIENT.listOf().fieldOf("inputs").forGetter(ForgeRecipe::inputs),
			Codec.INT.optionalFieldOf("coins", 0).forGetter(ForgeRecipe::coins),
			Codec.INT.optionalFieldOf("time", 0).forGetter(ForgeRecipe::time),
			IngredientCodecs.CRAFTING_INGREDIENT.fieldOf("result").forGetter(ForgeRecipe::result)
		).apply(instance, ::ForgeRecipe)
	}

	val KAT: MapCodec<KatRecipe> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			IngredientCodecs.CRAFTING_INGREDIENT.fieldOf("input").forGetter(KatRecipe::input),
			IngredientCodecs.CRAFTING_INGREDIENT.listOf().fieldOf("items").forGetter(KatRecipe::items),
			Codec.INT.optionalFieldOf("coins", 0).forGetter(KatRecipe::coins),
			Codec.INT.optionalFieldOf("time", 0).forGetter(KatRecipe::time),
			IngredientCodecs.CRAFTING_INGREDIENT.fieldOf("output").forGetter(KatRecipe::output)
		).apply(instance, ::KatRecipe)
	}

	val SHOP: MapCodec<ShopRecipe> = RecordCodecBuilder.mapCodec { instance ->
		instance.group(
			IngredientCodecs.CRAFTING_INGREDIENT.listOf().fieldOf("inputs").forGetter(ShopRecipe::inputs),
			IngredientCodecs.CRAFTING_INGREDIENT.fieldOf("result").forGetter(ShopRecipe::result)
		).apply(instance, ::ShopRecipe)
	}

	val DISPATCH: Codec<Recipe<*>> = Codec.STRING.dispatch(
		"type",
		{ it.type().type }
	) { typeStr ->
		val codec: MapCodec<out Recipe<*>> = when (typeStr) {
			"crafting" -> CRAFTING
			"forge" -> FORGE
			"kat" -> KAT
			"shop" -> SHOP
			else -> throw IllegalArgumentException("Unknown recipe type: $typeStr")
		}
		codec
	}

	val RECIPE: Codec<Recipe<*>> = Codec.of(
		DISPATCH as Encoder<Recipe<*>>,
		object : Decoder<Recipe<*>> {
			override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<Recipe<*>, T>> {
				return DISPATCH.decode(ops, input)
			}
		}
	)
}
