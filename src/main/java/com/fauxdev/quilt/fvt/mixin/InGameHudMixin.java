package com.fauxdev.quilt.fvt.mixin;


import com.fauxdev.quilt.fvt.utils.OnScreenText;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

import com.fauxdev.quilt.fvt.FVT;

/**
 * FEATURES: Tool Breaking Warning, HUD Info, Mount Hunger, Crosshair, No Vignette, No Spyglass Overlay, Hotbar Autohide
 *
 * @author Flourick
 */
@Mixin(InGameHud.class)
abstract class InGameHudMixin extends DrawableHelper
{
	@Final
	@Shadow
	private MinecraftClient client;

	@Shadow
	abstract LivingEntity getRiddenEntity();

	@Shadow
	abstract int getHeartCount(LivingEntity entity);

	@Shadow
	abstract int getHeartRows(int heartCount);

	@Shadow
	abstract PlayerEntity getCameraPlayer();

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo info)
	{
		FVT.VARS.tickToolWarningTicks();
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(MatrixStack matrixStack, float f, CallbackInfo info)
	{
		// renders on screen text only if not in debug or hud is hidden or if options don't say so
		if(this.client.options.debugEnabled || this.client.options.hudHidden) {
			return;
		}

		if(FVT.VARS.getToolWarningTextTicksLeft() > 0) {
			matrixStack.push();
			matrixStack.translate((this.client.getWindow().getScaledWidth() / 2.0d), (this.client.getWindow().getScaledHeight() / 2.0d), 0);
			matrixStack.scale(FVT.OPTIONS.toolWarningScale.get().floatValue(), FVT.OPTIONS.toolWarningScale.get().floatValue(), 1.0f);
			OnScreenText.drawToolWarningText(matrixStack);
			matrixStack.pop();
		}
	}

	@Redirect(method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartRows(I)I", ordinal = 0))
	private int hijackGetHeartRows(InGameHud igHud, int heartCount)
	{
		// super rare thing but the air bubbles would overlap mount health if shown (ex. popping out of water and straight onto a horse), so yeah this fixes that
		if(this.getCameraPlayer() != null && this.getHeartCount(this.getRiddenEntity()) != 0 && FVT.MC.interactionManager.hasStatusBars()) {
			return this.getHeartRows(heartCount) + 1;
		}
		else {
			return this.getHeartRows(heartCount);
		}
	}
}
