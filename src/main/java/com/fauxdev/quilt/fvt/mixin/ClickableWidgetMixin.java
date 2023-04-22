package com.fauxdev.quilt.fvt.mixin;

import com.fauxdev.quilt.fvt.utils.IClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Adds a method to render ClickableWidgets without a tooltip
 *
 * @author Flourick
 */
@Mixin(ClickableWidget.class)
abstract class ClickableWidgetMixin implements IClickableWidget
{
	@Shadow
	private int x;
	@Shadow
	private int y;

	@Shadow
	protected int width;
	@Shadow
	protected int height;

	@Shadow
	protected boolean hovered;
	@Shadow
	public boolean visible;

	@Shadow
	public abstract void render(MatrixStack matrices, int mouseX, int mouseY, float delta);

	@Override
	public void FVT_renderWithoutTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		if(!this.visible) {
			return;
		}

		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		this.render(matrices, mouseX, mouseY, delta);
	}
}
