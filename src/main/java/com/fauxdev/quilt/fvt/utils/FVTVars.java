package com.fauxdev.quilt.fvt.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

/**
 * Holder for various utility variables for different features.
 *
 * @author Flourick
 */
public class FVTVars
{
	public boolean settingsShowTooltips;

	public double freecamYaw;
	public double freecamPitch;

	public Vec3d playerVelocity;

	public double freecamX;
	public double freecamY;
	public double freecamZ;

	public double prevFreecamX;
	public double prevFreecamY;
	public double prevFreecamZ;

	public float freecamForwardSpeed;
	public float freecamSideSpeed;
	public float freecamUpSpeed;

	private int toolWarningTextTicksLeft;
	public int toolDurability;
	public ItemStack mainHandToolItemStack;
	public ItemStack offHandToolItemStack;
	public Hand toolHand;

	public FVTVars()
	{
		this.settingsShowTooltips = true;

		this.freecamYaw = 0.0d;
		this.freecamPitch = 0.0d;

		this.playerVelocity = Vec3d.ZERO;

		this.freecamX = 0.0d;
		this.freecamY = 0.0d;
		this.freecamZ = 0.0d;

		this.prevFreecamX = 0.0d;
		this.prevFreecamY = 0.0d;
		this.prevFreecamZ = 0.0d;

		this.freecamForwardSpeed = 0.0f;
		this.freecamSideSpeed = 0.0f;
		this.freecamUpSpeed = 0.0f;

		this.toolWarningTextTicksLeft = 0;
		this.toolDurability = 0;
		this.mainHandToolItemStack = ItemStack.EMPTY;
		this.offHandToolItemStack = ItemStack.EMPTY;
		this.toolHand = Hand.MAIN_HAND;
	}

	public int getToolWarningTextTicksLeft()
	{
		return toolWarningTextTicksLeft;
	}

	public void resetToolWarningTicks()
	{
		toolWarningTextTicksLeft = 40;
	}

	public void tickToolWarningTicks()
	{
		if(toolWarningTextTicksLeft > 0) {
			toolWarningTextTicksLeft -= 1;
		}
	}
}
