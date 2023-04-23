package com.fauxdev.quilt.fvt.mixin;

import java.util.ArrayList;
import java.util.List;

import com.fauxdev.quilt.fvt.FVT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;

/**
 * FEATURES: Prevent Tool Breaking, Freecam, Use Delay, Entity Outline, Placement Lock, Hotbar Autohide, Offhand AutoEat
 *
 * @author Flourick, gliscowo
 */
@Mixin(value = MinecraftClient.class, priority = 999)
abstract class MinecraftClientMixin {

	@Shadow
	public ClientPlayerEntity player;

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void onDoAttack(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.noToolBreaking.getValue() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
			}
		}

		if(FVT.OPTIONS.freecam.getValue()) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void onHandleBlockBreaking(boolean bl, CallbackInfo info) {
		if(FVT.OPTIONS.noToolBreaking.getValue() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}

		if(FVT.OPTIONS.freecam.getValue()) {
			info.cancel();
		}
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void onDoItemUseBefore(CallbackInfo info) {
		if(FVT.OPTIONS.noToolBreaking.getValue() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getStackInHand(Hand.MAIN_HAND).isEmpty() ? null : player.getStackInHand(Hand.MAIN_HAND);
			ItemStack offHandItem = player.getStackInHand(Hand.OFF_HAND).isEmpty() ? null : player.getStackInHand(Hand.OFF_HAND);

			if(mainHandItem != null && mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof CrossbowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof BowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}

			if(offHandItem != null && offHandItem.isDamaged()) {
				if(offHandItem.getItem() instanceof MiningToolItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof CrossbowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof TridentItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof BowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}

		if(FVT.OPTIONS.freecam.getValue()) {
			info.cancel();
		}
	}

	@Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
	private void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> info) {
		if(FVT.OPTIONS.entityOutline.getValue() && entity.getType() != EntityType.PLAYER || (FVT.OPTIONS.freecam.getValue() && entity.equals(FVT.MC.player) && !FVT.MC.options.hudHidden)) {
			info.setReturnValue(true);
		}
	}
}
