package mods.grissess.mde.proxy;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IProxy {
	void sidedLinkage();
	boolean getBreathingKey();
	Entity getClientEntity(int dim, int id);
	void scheduleClientTask(Runnable toRun);
	EntityPlayer getLocalPlayer();
}
