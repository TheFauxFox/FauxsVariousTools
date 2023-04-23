package com.fauxdev.quilt.fvt.mixin;

import com.fauxdev.quilt.fvt.utils.ISimpleOption;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * So mod features can be reset to default.
 *
 * @author Flourick
 */
@Mixin(SimpleOption.class)
abstract class OptionMixin<T> implements ISimpleOption<T>
{
	@Final
	@Shadow
	private T defaultValue;

	@Shadow
	public abstract void setValue(T value);

	@Override
	public void FVT_setValueToDefault()
	{
		setValue(defaultValue);
	}

	@Override
	public T FVT_getDefaultValue()
	{
		return defaultValue;
	}
}
