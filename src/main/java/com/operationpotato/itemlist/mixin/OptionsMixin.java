package com.operationpotato.itemlist.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import com.operationpotato.itemlist.utils.KeyMappingWithModifiers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Options.class)
public class OptionsMixin {
	@WrapOperation(method = "processOptions", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;getKey(Ljava/lang/String;)Lcom/mojang/blaze3d/platform/InputConstants$Key;"))
	private InputConstants.Key skyblockItemList$loadKeyWithModifier(String name, Operation<InputConstants.Key> original, @Local(name = "keyMapping") KeyMapping keyMapping) {
		if (keyMapping instanceof KeyMappingWithModifiers keyMappingWithModifiers) {
			return keyMappingWithModifiers.load(name);
		}
		return original.call(name);
	}

	@WrapOperation(method = "processOptions", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
	private void skyblockItemList$ignoreSetForKeyWithModifier(KeyMapping instance, InputConstants.Key key, Operation<Void> original, @Local(name = "keyMapping") KeyMapping keyMapping) {
		if (keyMapping instanceof KeyMappingWithModifiers) return;
		original.call(instance, key);
	}
}
