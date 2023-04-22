package com.fauxdev.quilt.fvt.mixin;


import com.fauxdev.quilt.fvt.FVT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.GameRenderer;

/**
 * FEATURES: Freecam, Attack Through
 *
 * @author Flourick
 */
@Mixin(GameRenderer.class)
abstract class GameRendererMixin
{
	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	private void removeHandRendering(CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.get()) {
			info.cancel();
		}
	}
}
