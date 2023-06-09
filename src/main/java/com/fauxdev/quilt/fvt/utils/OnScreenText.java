package com.fauxdev.quilt.fvt.utils;


import com.fauxdev.quilt.fvt.FVT;
import com.fauxdev.quilt.fvt.settings.FVTOptions;
import net.minecraft.client.gui.DrawContext;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

/**
 * Holder for static functions that draw text on the ingame HUD.
 *
 * @author Flourick
 */
public class OnScreenText
{
	public static void drawToolWarningText(DrawContext context)
	{
		// last half a second fade-out
		int alpha = MathHelper.clamp(MathHelper.ceil(25.5f * FVT.VARS.getToolWarningTextTicksLeft()), 0, 255);

		int y;
		if(FVT.OPTIONS.toolWarningPosition.getValue() == FVTOptions.ToolWarningPosition.TOP) {
			y = (int)((-(FVT.MC.getWindow().getScaledHeight() / 2 * 1/FVT.OPTIONS.toolWarningScale.getValue())) + (2/FVT.OPTIONS.toolWarningScale.getValue()));
		}
		else {
			y = (int)(((FVT.MC.getWindow().getScaledHeight() / 2 * 1/FVT.OPTIONS.toolWarningScale.getValue())) - (FVT.MC.textRenderer.fontHeight + 60/FVT.OPTIONS.toolWarningScale.getValue()));
		}

		final String ToolWarningText = FVT.VARS.toolHand.equals(Hand.MAIN_HAND) ? Text.translatable("fvt.tool_warning.text.main_hand", FVT.VARS.toolDurability).getString() : Text.translatable("fvt.tool_warning.text.offhand", FVT.VARS.toolDurability).getString();
		context.drawTextWithShadow(FVT.MC.textRenderer, ToolWarningText, (int) -(FVT.MC.textRenderer.getWidth(ToolWarningText) / 2), y, new Color(alpha, 255, 0, 0).getPacked());
	}

	// FUNCTIONS TO GET VARIOUS VALUES TO HUD

	private static int getBlockLightLevel()
	{
		return FVT.MC.world.getChunkManager().getLightingProvider().get(LightType.BLOCK).getLightLevel(FVT.OPTIONS.freecam.getValue() ? new BlockPos((int)FVT.MC.gameRenderer.getCamera().getPos().x, (int)FVT.MC.gameRenderer.getCamera().getPos().y, (int)FVT.MC.gameRenderer.getCamera().getPos().z)  : FVT.MC.getCameraEntity().getBlockPos());
	}

	private static String getFacingDirection()
	{
		return StringUtils.capitalize(Direction.fromRotation(FVT.MC.gameRenderer.getCamera().getYaw()).asString());
	}

	private static double getCurrentX()
	{
		return FVT.OPTIONS.freecam.getValue() ? FVT.MC.gameRenderer.getCamera().getPos().x : FVT.MC.player.getX();
	}

	private static double getCurrentY()
	{
		return FVT.OPTIONS.freecam.getValue() ? FVT.MC.gameRenderer.getCamera().getPos().y : FVT.MC.player.getY();
	}

	private static double getCurrentZ()
	{
		return FVT.OPTIONS.freecam.getValue() ? FVT.MC.gameRenderer.getCamera().getPos().z : FVT.MC.player.getZ();
	}
}
