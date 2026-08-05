package com.operationpotato.itemlist.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.InputConstants;
import com.operationpotato.itemlist.utils.KeyMappingWithModifiers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyBindsScreen.class)
public class KeyBindsScreenMixin {
	@WrapOperation(method = "lambda$addFooter$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;setKey(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
	private static void skyblockItemList$fixResetForKeyMappingWithModifier(KeyMapping instance, InputConstants.Key key, Operation<Void> original) {
		original.call(instance, key);
		if (instance instanceof KeyMappingWithModifiers keyMappingWithModifiers)
			keyMappingWithModifiers.resetModifiers();
	}
}
