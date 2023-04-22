package com.fauxdev.quilt.fvt.settings;

import com.fauxdev.quilt.fvt.FVT;
import com.fauxdev.quilt.fvt.utils.ISimpleOption;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.option.Option;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.math.MathHelper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FVTOptions {

	private final File file;
	private final Map<String, Option<?>> mods;

	/* Mod setup */
	public final Option<Boolean> noToolBreaking, toolWarning, entityOutline, fullbright, freecam;
	public final Option<Double> toolWarningScale;
	public final Option<ToolWarningPosition> toolWarningPosition;

	public FVTOptions() {
		this.file = new File(FVT.MC.runDirectory, "config/FauxVT.properties");
		this.mods = new HashMap<>();

		noToolBreaking = Option.ofBoolean(
			"fvt.noToolBreaking.name",
			tooltip("fvt.noToolBreaking.tooltip", true),
			true
		);
		mods.put("noToolBreaking", noToolBreaking);

		toolWarning = Option.ofBoolean(
			"fvt.tool_warning.name",
			tooltip("fvt.tool_warning.tooltip", true),
			true
		);
		mods.put("tool_warning", toolWarning);

		toolWarningScale = new Option<>(
			"fvt.tool_warning.scale.name",
			tooltip("fvt.tool_warning.scale.tooltip", 1.5),
			FVTOptions::getPercentValueText,
			new Option.IntRangeValueSet(0, 80).withModifier(value -> (double)value / 20.0, value -> (int)(value * 20.0)),
			Codec.doubleRange(0.0, 4.0), 1.5, value -> {}
		);
		mods.put("toolWarningScale", toolWarningScale);

		toolWarningPosition = new Option<>(
			"fvt.tool_warning.position.name",
			tooltip("fvt.tool_warning.position.tooltip", ToolWarningPosition.BOTTOM),
			Option.optionEnumText(),
			new Option.EnumValueSet<>(Arrays.asList(ToolWarningPosition.values()),
			Codec.INT.xmap(ToolWarningPosition::byId, ToolWarningPosition::getId)), ToolWarningPosition.BOTTOM, value -> {}
		);
		mods.put("tool_warning.position", toolWarningPosition);

		entityOutline = Option.ofBoolean(
			"fvt.entity_outline.name",
			tooltip("fvt.entity_outline.tooltip", false),
			false
		);
		mods.put("entityOutline", entityOutline);

		fullbright = Option.ofBoolean(
			"fvt.fullbright.name",
			tooltip("fvt.fullbright.tooltip", false),
			false
		);
		mods.put("fullbright", fullbright);

		freecam = Option.ofBoolean(
			"fvt.freecam.name",
			tooltip("fvt.freecam.tooltip", false),
			false
		);

		init();
	}

	private static <T> Option.TooltipSupplier<T> tooltip(String key, T defaultValue)
	{
		return value -> {
			List<Text> lines = new ArrayList<>();
			lines.add(Text.translatable(key));

			if(defaultValue instanceof Double) {
				// double is mostly used with percent so should be fine, just leaving this in case I forgot and rage why it shows percent even tho it should not
				lines.add(Text.translatable("fvt.mod.default", (int)((double)defaultValue * 100.0)).append("%").formatted(Formatting.GRAY));
			}
			else if(defaultValue instanceof Boolean) {
				lines.add(Text.translatable("fvt.mod.default", (boolean)defaultValue ? ScreenTexts.ON : ScreenTexts.OFF).formatted(Formatting.GRAY));
			}
			else if(defaultValue instanceof OptionEnum) {
				lines.add(Text.translatable("fvt.mod.default", ((OptionEnum) defaultValue).getTranslationKey()).formatted(Formatting.GRAY));
			}
			else {
				lines.add(Text.translatable("fvt.mod.default", defaultValue).formatted(Formatting.GRAY));
			}

			return Tooltip.create(Texts.join(lines, Text.of("\n")));
		};
	}

	public static Text getValueText(Text prefix, int value)
	{
		return Text.translatable("options.generic_value", prefix, value);
	}

	public static Text getValueText(Text prefix, double value)
	{
		return Text.translatable("options.generic_value", prefix, value);
	}

	private static Text getPercentValueText(Text prefix, double value)
	{
		if(value == 0.0) {
			return ScreenTexts.composeToggleText(prefix, false);
		}

		return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
	}

	public enum ToolWarningPosition implements OptionEnum
	{
		TOP(0, "fvt.tool_warning.position.top"),
		BOTTOM(1, "fvt.tool_warning.position.bottom");

		private static final ToolWarningPosition[] VALUES = (ToolWarningPosition[])Arrays.stream(ToolWarningPosition.values()).sorted(Comparator.comparingInt(ToolWarningPosition::getId)).toArray(ToolWarningPosition[]::new);;
		private final String translationKey;
		private final int id;

		ToolWarningPosition(int id, String translationKey)
		{
			this.id = id;
			this.translationKey = translationKey;
		}

		public String toString()
		{
			return Integer.toString(getId());
		}

		public int getId()
		{
			return this.id;
		}

		public String getTranslationKey()
		{
			return this.translationKey;
		}

		public static ToolWarningPosition byId(int id)
		{
			return VALUES[MathHelper.floorMod(id, VALUES.length)];
		}
	}

	@SuppressWarnings({"unchecked"})
	private <T> void resetFeature(Option<T> mod)
	{
		((ISimpleOption<T>)(Object) mod).FVT_setValueToDefault();
	}

	public void reset()
	{
		for(Option<?> mod : mods.values()) {
			resetFeature(mod);
		}
	}

	public void write()
	{
		try(PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));) {
			printWriter.println("# FVT configuration. Do not edit here unless you know what you're doing!");
			printWriter.println("# Last save: " + DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy").format(LocalDateTime.now()));

			for(Map.Entry<String, Option<?>> mod : mods.entrySet()) {
				printWriter.println(mod.getKey() + "=" + mod.getValue().get());
			}
		}
		catch(Exception e) {
			FVT.LOGGER.error("Failed to write to 'FauxVT.properties': {}", e.toString());
		}
	}

	private <T> void parseFeatureLine(Option<T> option, String value)
	{
		DataResult<T> dataResult = option.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseString(value));
		dataResult.error().ifPresent(partialResult -> FVT.LOGGER.warn("Skipping bad config option (" + value + "): " + partialResult.message()));
		dataResult.result().ifPresent(option::set);
	}

	private void read()
	{
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
			bufferedReader.lines().forEach((line) -> {
				if(line.startsWith("#")) {
					// skips comments
					return;
				}

				String[] v = line.split("=");

				if(v.length != 2) {
					FVT.LOGGER.warn("Skipping bad config option line!");
					return;
				}

				String key = v[0];
				String value = v[1];

				Option<?> option = mods.get(key);

				if(option == null || value.isEmpty()) {
					FVT.LOGGER.warn("Skipping bad config option (" + value + ")" + " for " + key);
				}
				else {
					parseFeatureLine(option, value);
				}
			});
		}
		catch(IOException e) {
			FVT.LOGGER.error("Failed to read from 'FauxVT.properties': {}", e.toString());
		}
	}

	private void init() {
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			write();
		}
		else {
			read();
		}
	}

}
