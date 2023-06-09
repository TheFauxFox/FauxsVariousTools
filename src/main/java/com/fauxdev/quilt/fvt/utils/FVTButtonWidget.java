package com.fauxdev.quilt.fvt.utils;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import com.fauxdev.quilt.fvt.FVT;

/**
 * Updated version of vanilla's ButtonWidget that fixes custom button sizes and adds coloring option.
 *
 * @author Flourick
 */
public class FVTButtonWidget extends ButtonWidget
{
	private Color messageColor;
	private Color buttonColor;

	public FVTButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress)
	{
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.messageColor = Color.WHITE;
		this.buttonColor = Color.WHITE;
	}

	public FVTButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, Color buttonColor, Color messageColor)
	{
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.messageColor = messageColor;
		this.buttonColor = buttonColor;
	}


	private int getTextureY() {
		int i = 1;
		if (!this.active) {
			i = 0;
		} else if (this.isHovered()) {
			i = 2;
		}

		return 46 + i * 20;
	}

	// YEP, had to make it my own so not only custom width is supported but also custom height
	@Override
	public void renderButton(DrawContext context, int mouseX, int mouseY, float delta)
	{
		int textureOffset = getTextureY();

		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(buttonColor.getNormRed(), buttonColor.getNormGreen(), buttonColor.getNormBlue(), buttonColor.getNormAlpha());
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();

		// upper-left
		context.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY() , 0  , 46 + textureOffset * 20 , this.width / 2, this.height / 2);
		// upper-right
		context.drawTexture(WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY() , 200 - this.width / 2, 46 + textureOffset * 20 , this.width / 2, this.height / 2);
		// lower-left
		context.drawTexture(WIDGETS_TEXTURE, this.getX(), this.getY() + this.height / 2, 0, 66 - this.height / 2 + textureOffset * 20, this.width / 2, this.height / 2);
		// lower-right
		context.drawTexture(WIDGETS_TEXTURE, this.getX() + this.width / 2, this.getY() + this.height / 2, 200 - this.width / 2, 66 - this.height / 2 + textureOffset * 20, this.width / 2, this.height / 2);

		// title
		context.drawCenteredTextWithShadow(FVT.MC.textRenderer, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, messageColor.getPacked());
	}

	public Color getMessageColor()
	{
		return messageColor;
	}

	public void setMessageColor(Color messageColor)
	{
		this.messageColor = messageColor;
	}

	public Color getButtonColor()
	{
		return buttonColor;
	}

	public void setButtonColor(Color buttonColor)
	{
		this.buttonColor = buttonColor;
	}
}
