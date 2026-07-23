package com.operationpotato.itemlist

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.commands.CommandBuildContext
import org.spongepowered.asm.mixin.MixinEnvironment
import tech.thatgravyboat.skyblockapi.utils.text.Text

object DebugCommands {
	fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, context: CommandBuildContext) {
		val node = dispatcher.register(
			literal("sbil")
				.then(literal("auditMixins").executes { ctx ->
					ctx.source.sendFeedback(Text.of("Running mixin audit..."))
					MixinEnvironment.getCurrentEnvironment().audit()
					ctx.source.sendFeedback(Text.of("Done"))
					Command.SINGLE_SUCCESS
				})
				.then(literal("reset").executes { ctx ->
					SkyBlockItemList.resetWidget()
					ctx.source.sendFeedback(Text.of("Reset widgets!"))
					Command.SINGLE_SUCCESS
				})
				.then(literal("toggleExclusionZoneDebug").executes { ctx ->
					SkyBlockItemList.enableExclusionZoneDebug = !SkyBlockItemList.enableExclusionZoneDebug
					val mode = if (SkyBlockItemList.enableExclusionZoneDebug) "enabled" else "disabled"
					ctx.source.sendFeedback(Text.of("Exclusion zone visualization is now $mode"))
					Command.SINGLE_SUCCESS
				})
		)
		dispatcher.register(literal("skyblock-item-list").redirect(node))
	}
}
