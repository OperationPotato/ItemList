package com.operationpotato.itemlist.mixin;
//? if >=26.2 {
import com.moulberry.lattice.LatticeConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@SuppressWarnings("UnstableApiUsage")
@Mixin(LatticeConfigScreen.class)
public class LatticeConfigScreenMixin {
	@Shadow
	@Final
	private @Nullable Screen closeTo;

	/**
	 * @author alex
	 * @reason Fix onClose not working on 26.2
	 */
	@Overwrite
	public void onClose() {
		Minecraft minecraft = Minecraft.getInstance();
		if (this.closeTo != null) {
			minecraft.gui.setScreen(this.closeTo);
		} else {
			minecraft.gui.setScreen(null);
		}
	}
}
//? }
