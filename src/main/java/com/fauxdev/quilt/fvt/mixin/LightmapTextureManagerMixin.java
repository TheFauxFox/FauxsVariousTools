package com.fauxdev.quilt.fvt.mixin;


import com.fauxdev.quilt.fvt.FVT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.LightmapTextureManager;

/**
 * FEATURES: Fullbright
 *
 * @author Flourick
 */
@Mixin(LightmapTextureManager.class)
abstract class LightmapTextureManagerMixin
{
	@Inject(method = "getBrightness", at = @At("HEAD"), cancellable = true)
	private static void onGetBrightness(CallbackInfoReturnable<Float> info)
	{
		if(FVT.OPTIONS.fullbright.getValue()) {
			info.setReturnValue(1f);
		}
	}
}
