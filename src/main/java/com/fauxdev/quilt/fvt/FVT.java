package com.fauxdev.quilt.fvt;

import com.fauxdev.quilt.fvt.settings.FVTOptions;
import com.fauxdev.quilt.fvt.settings.FVTSettingsScreen;
import com.fauxdev.quilt.fvt.utils.FVTVars;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// We're only dealing with EntityOutline, Fullbright, Freecam, ToolBreakWarning (for now)

public class FVT implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("FVT");

	public static FVT INSTANCE;
	public static MinecraftClient MC;
	public static FVTOptions OPTIONS;
	public static final FVTVars VARS = new FVTVars();

	private KeyBinding toolBreakingOverrideKeybind;

	@Override
	public void onInitializeClient() {
		LOGGER.info("FVT Loading");

		INSTANCE = this;
		MC = MinecraftClient.getInstance();
		OPTIONS = new FVTOptions();
		registerKeybinds();
		registerCallbacks();

		LOGGER.info("FVT Loaded");
	}

	public boolean isToolBreakingOverriden() {
		return toolBreakingOverrideKeybind.isPressed();
	}

	private void handleFeatureKeybindPress(KeyBinding keybind, SimpleOption<Boolean> option, String key)
	{
		while(keybind.wasPressed()) {
			option.setValue(!option.getValue());

			if(option.getValue()) {
				FVT.MC.inGameHud.getChatHud().addMessage(Text.translatable("fvt.chat_messages_prefix", Text.translatable("fvt.mod.enabled", Text.translatable(key))));
			}
			else {
				FVT.MC.inGameHud.getChatHud().addMessage(Text.translatable("fvt.chat_messages_prefix", Text.translatable("fvt.mod.disabled", Text.translatable(key))));
			}
		}
	}

	private void registerKeybinds()
	{
		KeyBinding openSettingsMenuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.options.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		toolBreakingOverrideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.tool_breaking_override.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "FVT"));
		KeyBinding fullbrightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.fullbright.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding freecamKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.freecam.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding entityOutlineKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.entity_outline.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));

		ClientTickEvents.END_WORLD_TICK.register(client ->
		{
			while(openSettingsMenuKeybind.wasPressed()) {
				FVT.MC.setScreen(new FVTSettingsScreen(FVT.MC.currentScreen));
			}

			handleFeatureKeybindPress(fullbrightKeybind, FVT.OPTIONS.fullbright, "fvt.fullbright.name");
			handleFeatureKeybindPress(entityOutlineKeybind, FVT.OPTIONS.entityOutline, "fvt.entity_outline.name");
			handleFeatureKeybindPress(freecamKeybind, FVT.OPTIONS.freecam, "fvt.freecam.name");
		});
	}

	private void registerCallbacks()
	{
		ClientTickEvents.START_WORLD_TICK.register(client ->
		{
			if(FVT.MC.player == null && FVT.OPTIONS.freecam.getValue()) {
				// disables freecam if leaving a world
				FVT.OPTIONS.freecam.setValue(false);
			}
		});

		ClientTickEvents.END_WORLD_TICK.register(clientWorld ->
		{
			if(FVT.OPTIONS.toolWarning.getValue() && MC.player != null) {
				ItemStack mainHandItem = FVT.MC.player.getStackInHand(Hand.MAIN_HAND);
				ItemStack offHandItem = FVT.MC.player.getStackInHand(Hand.OFF_HAND);

				int mainHandDurability = mainHandItem.getMaxDamage() - mainHandItem.getDamage();;
				int offHandDurability = offHandItem.getMaxDamage() - offHandItem.getDamage();

				if(mainHandItem.isDamaged() && mainHandItem != FVT.VARS.mainHandToolItemStack) {
					if(MathHelper.floor(mainHandItem.getMaxDamage() * 0.9f) < mainHandItem.getDamage() + 1 && mainHandDurability < 13) {
						FVT.VARS.toolDurability = mainHandDurability;
						FVT.VARS.toolHand = Hand.MAIN_HAND;
						FVT.VARS.resetToolWarningTicks();
					}
				}

				if(offHandItem.isDamaged() && offHandItem != FVT.VARS.offHandToolItemStack) {
					if(MathHelper.floor(offHandItem.getMaxDamage() * 0.9f) < offHandItem.getDamage() + 1 && offHandDurability < 13) {
						if(mainHandDurability == 0 || offHandDurability < mainHandDurability) {
							FVT.VARS.toolDurability = offHandDurability;
							FVT.VARS.toolHand = Hand.OFF_HAND;
							FVT.VARS.resetToolWarningTicks();
						}
					}
				}

				FVT.VARS.mainHandToolItemStack = mainHandItem;
				FVT.VARS.offHandToolItemStack = offHandItem;
			}

		});
	}
}
