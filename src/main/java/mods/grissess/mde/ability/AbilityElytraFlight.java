package mods.grissess.mde.ability;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import me.ichun.mods.morph.api.ability.Ability;
import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.net.Network;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class AbilityElytraFlight extends Ability {
	public double jumpImpulse = 0.75;
	public transient boolean jumpHeld = false;
	
	public static final String TYPE = "elytraFlight";
	
	public AbilityElytraFlight() {}
	public AbilityElytraFlight(double jumpImpulse) {
		this.jumpImpulse = jumpImpulse;
	}
	
	@Override
	public Ability clone() {
		return new AbilityElytraFlight(jumpImpulse);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public Ability parse(String[] args) {
		jumpImpulse = Double.parseDouble(args[0]);
		return this;
	}
	
	@Override
	public void tick() {
		Logger log = MorphDragonExtensions.instance.getLogger();
		EntityPlayer parent = (EntityPlayer) getParent();
		if(parent == null) return;
		if(parent.world.isRemote) {
			EntityPlayerSP player = (EntityPlayerSP) parent;
			if(!player.isElytraFlying()) {
				if(!player.onGround && player.motionY < 0 && player.movementInput.jump) {
					log.info(String.format("AEF.t: setting flight for %s", player));
					MorphDragonExtensions.instance.channel.sendToServer(
							new Network.PacketSetElytraFlight(
									parent,
									true
							)
					);
				}
			} else {
				if(player.movementInput.jump && !jumpHeld) {
					log.info("AEF.t: jump");
					player.motionY = jumpImpulse;
				}
				jumpHeld = player.movementInput.jump;
			}
		}
	}
	
	@Override
	public void kill(ArrayList<Ability> next) {
		EntityPlayer parent = (EntityPlayer) getParent();
		if(parent == null) return;
		if(parent.isServerWorld()) {
			if(parent.isElytraFlying()) {
				((EntityPlayerMP) parent).clearElytraFlying();
			}
		}
	}
}
