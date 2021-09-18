package mods.grissess.mde.manager;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FlightManager extends ManagerBoolean {

	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		doServerTick(event);
	}
	
	@Override
	public boolean shouldCancel(EntityPlayerMP player) {
		return player.onGround || player.capabilities.isFlying;
	}
	
	@Override
	public void affect(EntityPlayerMP player) {
		player.setElytraFlying();
	}
}
