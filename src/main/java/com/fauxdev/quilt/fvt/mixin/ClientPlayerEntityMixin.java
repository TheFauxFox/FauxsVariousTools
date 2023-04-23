package com.fauxdev.quilt.fvt.mixin;


import com.fauxdev.quilt.fvt.FVT;
import com.ibm.icu.math.BigDecimal;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.JumpingMount;
import net.minecraft.util.math.MathHelper;


/**
 * FEATURES: Chat Death Coordinates, Disable 'W' To Sprint, Freecam, Hotbar Autohide, AutoElytra, FreeLook, Spyglass Zoom
 *
 * @author Flourick, gliscowo
 */
@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
	@Shadow
	public abstract JumpingMount getJumpingMount();

	@Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
	private void onDropSelectedItem(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "updateHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getHealth()F", ordinal = 0))
	private void onUpdateHealth(float health, CallbackInfo info)
	{
		// disables freecam if you take damage while using it
		if(this.hurtTime == 10 && FVT.OPTIONS.freecam.getValue()) {
			FVT.OPTIONS.freecam.setValue(false);
		}
	}

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void onTickMovement(CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			this.setVelocity(FVT.VARS.playerVelocity);

			float forward = FVT.MC.player.input.movementForward;
			float up = (FVT.MC.player.input.jumping ? 1.0f : 0.0f) - (FVT.MC.player.input.sneaking ? 1.0f : 0.0f);
			float side = FVT.MC.player.input.movementSideways;

			FVT.VARS.freecamForwardSpeed = forward != 0 ? FVT_updateMotion(FVT.VARS.freecamForwardSpeed, forward) : FVT.VARS.freecamForwardSpeed * 0.5f;
			FVT.VARS.freecamUpSpeed = up != 0 ?  FVT_updateMotion(FVT.VARS.freecamUpSpeed, up) : FVT.VARS.freecamUpSpeed * 0.5f;
			FVT.VARS.freecamSideSpeed = side != 0 ?  FVT_updateMotion(FVT.VARS.freecamSideSpeed , side) : FVT.VARS.freecamSideSpeed * 0.5f;

			double rotateX = Math.sin(FVT.VARS.freecamYaw * Math.PI / 180.0D);
			double rotateZ = Math.cos(FVT.VARS.freecamYaw * Math.PI / 180.0D);
			double speed = FVT.MC.player.isSprinting() ? 1.2D : 0.55D;

			FVT.VARS.prevFreecamX = FVT.VARS.freecamX;
			FVT.VARS.prevFreecamY = FVT.VARS.freecamY;
			FVT.VARS.prevFreecamZ = FVT.VARS.freecamZ;

			FVT.VARS.freecamX += (FVT.VARS.freecamSideSpeed * rotateZ - FVT.VARS.freecamForwardSpeed * rotateX) * speed;
			FVT.VARS.freecamY += FVT.VARS.freecamUpSpeed * speed;
			FVT.VARS.freecamZ += (FVT.VARS.freecamForwardSpeed * rotateZ + FVT.VARS.freecamSideSpeed * rotateX) * speed;
		}
	}

	private float FVT_updateMotion(float motion, float direction)
	{
		return (direction + motion == 0) ? 0.0f : MathHelper.clamp(motion + ((direction < 0) ? -0.35f : 0.35f), -1f, 1f);
	}

	// PREVENTS SENDING VEHICLE MOVEMENT PACKETS TO SERVER (freecam)
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", ordinal = 0))
	private boolean hijackHasVehicle(ClientPlayerEntity player)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			return false;
		}

		return this.hasVehicle();
	}

	// PREVENTS HORSES FROM JUMPING (freecam)
	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;", ordinal = 0))
	private JumpingMount hijackGetJumpingMount(ClientPlayerEntity player)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			return null;
		}

		return this.getJumpingMount();
	}

	// PREVENTS BOAT MOVEMENT (freecam)
	@Inject(method = "tickRiding", at = @At("HEAD"), cancellable = true)
	private void onTickRiding(CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			super.tickRiding();
			info.cancel();
		}
	}

	// PREVENTS MOVEMENT (freecam)
	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void onMove(CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			info.cancel();
		}
	}

	// PREVENTS MORE MOVEMENT (freecam)
	@Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
	private void onIsCamera(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			info.setReturnValue(false);
		}
	}

	// PREVENTS SNEAKING (freecam)
	@Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
	private void onIsSneaking(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			info.setReturnValue(false);
		}
	}

	// UPDATES YAW AND PITCH BASED ON MOUSE MOVEMENT (freecam & freelook)
	@Override
	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			FVT.VARS.freecamYaw += cursorDeltaX * 0.15D;
			FVT.VARS.freecamPitch = MathHelper.clamp(FVT.VARS.freecamPitch + cursorDeltaY * 0.15D, -90, 90);
		}
		else {
			super.changeLookDirection(cursorDeltaX, cursorDeltaY);
		}
	}

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) { super(world, profile); } // IGNORED
}
