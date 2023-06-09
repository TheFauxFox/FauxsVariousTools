package com.fauxdev.quilt.fvt.utils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

/**
 * ClickableWidget render without tooltip accessor.
 *
 * @author Flourick
 */
public interface IClickableWidget
{
	public void FVT_renderWithoutTooltip(DrawContext context, int mouseX, int mouseY, float delta);
}
