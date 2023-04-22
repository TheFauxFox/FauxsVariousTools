package com.fauxdev.quilt.fvt;

import com.fauxdev.quilt.fvt.settings.FVTOptions;
import com.fauxdev.quilt.fvt.settings.FVTSettingsScreen;
import com.fauxdev.quilt.fvt.utils.FVTVars;
import com.mojang.blaze3d.platform.InputUtil;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBind;
import net.minecraft.client.option.Option;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
import org.quiltmc.loader.api.ModContainer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// We're only dealing with EntityOutline, Fullbright, Freecam, ToolBreakWarning (for now)

public class FVT implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("FVT");

	public static FVT INSTANCE;
	public static MinecraftClient MC;
	public static FVTOptions OPTIONS;
	public static final FVTVars VARS = new FVTVars();

	private KeyBind toolBreakingOverrideKeybind;

	@Override
	public void onInitializeClient(ModContainer mod) {
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

	private void handleFeatureKeybindPress(KeyBind keybind, Option<Boolean> option, String key)
	{
		while(keybind.wasPressed()) {
			option.set(!option.get());

			if(option.get()) {
				FVT.MC.inGameHud.getChatHud().addMessage(Text.translatable("fvt.chat_messages_prefix", Text.translatable("fvt.mod.enabled", Text.translatable(key))));
			}
			else {
				FVT.MC.inGameHud.getChatHud().addMessage(Text.translatable("fvt.chat_messages_prefix", Text.translatable("fvt.mod.disabled", Text.translatable(key))));
			}
		}
	}

	private void registerKeybinds()
	{
		KeyBind openSettingsMenuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind("fvt.options.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		toolBreakingOverrideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind("fvt.tool_breaking_override.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "FVT"));
		KeyBind fullbrightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind("fvt.fullbright.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBind freecamKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind("fvt.freecam.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBind entityOutlineKeybind = KeyBindingHelper.registerKeyBinding(new KeyBind("fvt.entity_outline.name", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));

		ClientTickEvents.END.register(client ->
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
		ClientTickEvents.END.register(client ->
		{
			if(FVT.MC.player == null && FVT.OPTIONS.freecam.get()) {
				// disables freecam if leaving a world
				FVT.OPTIONS.freecam.set(false);
			}
		});

		ClientTickEvents.END.register(clientWorld ->
		{
			if(FVT.OPTIONS.toolWarning.get() && MC.player != null) {
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
