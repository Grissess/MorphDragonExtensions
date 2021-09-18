package mods.grissess.mde.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Server implements IProxy {

	@Override
	public void sidedLinkage() {}

	@Override
	public boolean getBreathingKey() {
		return false;
	}

	@Override
	public Entity getClientEntity(int dim, int id) {
		return null;
	}

	@Override
	public void scheduleClientTask(Runnable toRun) {}

	@Override
	public EntityPlayer getLocalPlayer() {
		return null;
	}

}
