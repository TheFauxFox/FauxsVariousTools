package com.fauxdev.quilt.fvt.settings;

import com.fauxdev.quilt.fvt.FVT;
import com.fauxdev.quilt.fvt.utils.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * This mods settings screen.
 *
 * @author Flourick
 */
public class FVTSettingsScreen extends Screen
{
	private final Screen parent;
	private FVTButtonListWidget list;

	// getter for ModMenu
	public static Screen getNewScreen(Screen parent)
	{
		return new FVTSettingsScreen(parent);
	}

	public FVTSettingsScreen(Screen parent)
	{
		super(Text.translatable("fvt.options_title"));
		this.parent = parent;
	}

	protected void init()
	{
		this.list = new FVTButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addCategoryEntry("fvt.mod_category.render");
		this.list.addOptionEntry(FVT.OPTIONS.fullbright, FVT.OPTIONS.entityOutline);
		this.list.addCategoryEntry("fvt.mod_category.tools");
		this.list.addOptionEntry(FVT.OPTIONS.toolWarning, FVT.OPTIONS.toolWarningPosition);
		this.list.addOptionEntry(FVT.OPTIONS.toolWarningScale);
		this.list.addOptionEntry(FVT.OPTIONS.noToolBreaking);
		this.list.addCategoryEntry("fvt.mod_category.misc");
		this.list.addOptionEntry(FVT.OPTIONS.freecam);
		this.addSelectableChild(this.list);

		// DEFAULTS button at the top left corner
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("fvt.options.defaults"), (buttonWidget) -> {
					FVT.OPTIONS.reset();
					this.client.setScreen(getNewScreen(parent));
				})
				.dimensions(6, 6, 55, 20)
				.tooltip(Tooltip.of(Text.translatable("fvt.options.defaults.tooltip").formatted(Formatting.YELLOW)))
				.build()
		);

		// TOOLTIP (?/-) button at the top right corner
		this.addDrawableChild(
			ButtonWidget.builder(Text.literal("-"), (buttonWidget) -> {
					FVT.VARS.settingsShowTooltips = !FVT.VARS.settingsShowTooltips;

					if(FVT.VARS.settingsShowTooltips) {
						buttonWidget.setMessage(Text.literal("-"));
						buttonWidget.setTooltip(Tooltip.of(Text.translatable("fvt.options.tooltips.hide")));
					}
					else {
						buttonWidget.setMessage(Text.literal("?"));
						buttonWidget.setTooltip(Tooltip.of(Text.translatable("fvt.options.tooltips.show")));
					}
				})
				.dimensions(this.width - 26, 6, 20, 20)
				.tooltip(Tooltip.of(Text.translatable("fvt.options.tooltips.show")))
				.build()
		);

		// DONE button at the bottom
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.DONE, (buttonWidget) -> {
					FVT.OPTIONS.write();
					this.client.setScreen(parent);
				})
				.dimensions(this.width / 2 - 100, this.list.getBottom() + ((this.height - this.list.getBottom() - 20) / 2), 200, 20)
				.build()
		);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(context);
		this.list.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, Color.WHITE.getPacked());

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void removed()
	{
		FVT.OPTIONS.write();
	}

	@Override
	public void close()
	{
		this.client.setScreen(parent);
	}
}
