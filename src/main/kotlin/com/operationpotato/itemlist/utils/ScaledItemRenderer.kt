package com.operationpotato.itemlist.utils

import com.mojang.blaze3d.platform.Lighting
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.textures.FilterMode
import com.mojang.blaze3d.textures.GpuTextureView
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.fabric.api.client.rendering.v1.PictureInPictureRendererRegistry
import net.fabricmc.fabric.impl.client.rendering.PictureInPictureRendererRegistryImpl
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.navigation.ScreenRectangle
import net.minecraft.client.gui.render.TextureSetup
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.item.TrackingItemStackRenderState
import net.minecraft.client.renderer.state.gui.BlitRenderState
import net.minecraft.client.renderer.state.gui.GuiItemRenderState
import net.minecraft.client.renderer.state.gui.GuiRenderState
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.LightCoordsUtil
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.Matrix3x2f
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import java.util.*

// Taken with Permission from Meowdding-Lib and modified a bit ~J10a1n15
// https://github.com/meowdding/meowdding-lib/blob/master/src/main/kotlin/me/owdding/lib/displays/item/ItemStateRenderer.kt
class ScaledItemRenderer(buffer: MultiBufferSource.BufferSource) :
	PictureInPictureRenderer<ScaledItemRenderer.State>(buffer) {

	private var textureView: GpuTextureView? = null
	private var lastState: State? = null

	override fun textureIsReadyToBlit(state: State): Boolean {
		return this.lastState != null && this.lastState == state
	}

	override fun renderToTexture(state: State, stack: PoseStack) {
		this.lastState = state
		this.textureView = RenderSystem.outputColorTextureOverride

		stack.scale(1f, -1f, -1f)
		val item = state.state

		McClient.self.gameRenderer.lighting.setupFor(
			if (item.itemStackRenderState().usesBlockLight()) Lighting.Entry.ITEMS_3D else Lighting.Entry.ITEMS_FLAT
		)

		val featureRenderer = McClient.self.gameRenderer.featureRenderDispatcher
		item.itemStackRenderState()
			.submit(stack, featureRenderer.submitNodeStorage, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0)
		featureRenderer.renderAllFeatures()
	}

	override fun getTranslateY(height: Int, ignored: Int): Float {
		return height / 2f
	}

	override fun blitTexture(state: State, gui: GuiRenderState) {
		gui.addBlitToCurrentLayer(
			BlitRenderState(
				RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
				TextureSetup.singleTexture(
					this.textureView!!,
					RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR),
				),
				state.pose(), state.x0(), state.y0(), state.x0() + 16, state.y0() + 16,
				0.0F, 1.0F, 1.0F, 0.0F, -1,
				state.scissorArea(),
				null,
			),
		)
	}

	override fun getRenderStateClass(): Class<State> = State::class.java
	override fun getTextureLabel(): String = "skyblock_item_list_item_state"

	data class State(val state: GuiItemRenderState) : PictureInPictureRenderState {
		override fun scale(): Float = maxOf(state.pose().m00(), state.pose().m11()) * 16f
		override fun x0(): Int = state.x()
		override fun x1(): Int = state.x() + scale().toInt()
		override fun y0(): Int = state.y()
		override fun y1(): Int = state.y() + scale().toInt()
		override fun scissorArea(): ScreenRectangle? = state.scissorArea()
		override fun bounds(): ScreenRectangle? = state.bounds()
		override fun pose(): Matrix3x2f = state.pose()

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is State) return false
			if (other.state.itemStackRenderState().modelIdentity != state.itemStackRenderState().modelIdentity) return false
			if (other.state.pose().m00() != state.pose().m00()) return false
			if (other.state.pose().m11() != state.pose().m11()) return false
			return true
		}

		override fun hashCode(): Int {
			return Objects.hash(
				state.itemStackRenderState().modelIdentity,
				state.pose().m00(),
				state.pose().m11(),
			)
		}
	}

	companion object {
		fun extract(
			graphics: GuiGraphicsExtractor,
			item: ItemStack,
			x: Int, y: Int,
		) {
			if (item.isEmpty || !McLevel.hasLevel) return

			val state = TrackingItemStackRenderState()
			McClient.self.itemModelResolver.updateForTopItem(
				state,
				item,
				ItemDisplayContext.GUI,
				McLevel.self,
				McPlayer.self,
				0
			)

			graphics.guiRenderState.addPicturesInPictureState(
				State(
					GuiItemRenderState(
						Matrix3x2f(graphics.pose()),
						state,
						x, y,
						graphics.scissorStack.peek(),
					),
				),
			)
		}
	}
}