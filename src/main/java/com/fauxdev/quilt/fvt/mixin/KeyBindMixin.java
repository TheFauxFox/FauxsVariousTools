package com.fauxdev.quilt.fvt.mixin;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fauxdev.quilt.fvt.utils.IKeyBinding;
import com.mojang.blaze3d.platform.InputUtil;
import net.minecraft.client.option.KeyBind;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds an option to register listeners for key up and key down events.
 *
 * @author Flourick
 */
@Mixin(KeyBind.class)
abstract class KeyBindMixin implements IKeyBinding
{
	@Shadow
	private boolean pressed;

	@Final
	@Shadow
	private static Map<InputUtil.Key, KeyBind> KEY_BINDS_BY_KEY;

	private List<FVT_KeyDownListener> keyDownListeners = new ArrayList<>();
	private List<FVT_KeyUpListener> keyUpListeners = new ArrayList<>();

	private void FVT_onKeyDownEvent()
	{
		for(FVT_KeyDownListener l : keyDownListeners) {
			l.keyDownListener();
		}
	}

	private void FVT_onKeyUpEvent()
	{
		for(FVT_KeyUpListener l : keyUpListeners) {
			l.keyUpListener();
		}
	}

	@Override
	public void FVT_registerKeyDownListener(FVT_KeyDownListener listener)
	{
		keyDownListeners.add(listener);
	}

	@Override
	public void FVT_registerKeyUpListener(FVT_KeyUpListener listener)
	{
		keyUpListeners.add(listener);
	}

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void onSetKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo info)
	{
		KeyBind keyBinding = KEY_BINDS_BY_KEY.get(key);

		if(keyBinding != null) {
			if(pressed && !keyBinding.isPressed()) {
				((KeyBindMixin)(Object)keyBinding).FVT_onKeyDownEvent();
			}
			else if(!pressed) {
				((KeyBindMixin)(Object)keyBinding).FVT_onKeyUpEvent();
			}
		}
	}
}