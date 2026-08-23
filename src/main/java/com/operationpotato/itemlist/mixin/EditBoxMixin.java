package com.operationpotato.itemlist.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.operationpotato.itemlist.gui.ClearableEditBox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EditBox.class)
public class EditBoxMixin {

	@WrapOperation(
		method = "extractWidgetRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/WidgetSprites;get(ZZ)Lnet/minecraft/resources/Identifier;"
		)
	)
	public Identifier skyblockItemList$replaceSprites(WidgetSprites instance, boolean enabled, boolean focused, Operation<Identifier> original) {
		return original.call(instance, enabled, focused);
	}

	@WrapOperation(
		method = "extractWidgetRenderState",
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Math;min(II)I",
			ordinal = 0
		)
	)
	public int skyblockItemList$fixHighlightWidth(int a, int b, Operation<Integer> original) {
		return original.call(a, b);
	}

	@Mixin(ClearableEditBox.class)
	static abstract class ClearableEditBoxMisin extends EditBoxMixin {
		@Shadow
		public abstract WidgetSprites getSprites();

		@Shadow
		private boolean isSearchingInventory;

		@Override
		public Identifier skyblockItemList$replaceSprites(WidgetSprites instance, boolean enabled, boolean focused, Operation<Identifier> original) {
			if (isSearchingInventory) {
				return original.call(getSprites(), enabled, focused);
			} else {
				return original.call(instance, enabled, focused);
			}
		}

		@Override
		public int skyblockItemList$fixHighlightWidth(int a, int b, Operation<Integer> original) {
			var instance = (ClearableEditBox) (Object) this;
			return original.call(a, instance.getX() + instance.getInnerWidth());
		}
	}
}
