package mods.grissess.mde.proxy;

import org.apache.logging.log4j.Logger;

import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.dm2.Linkage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class Client implements IProxy {

	@Override
	public void sidedLinkage() {
			try {
				Linkage.ModKeys = Class.forName(Linkage.MOD_KEYS);
				Linkage.MK_KEY_BREATH = Linkage.ModKeys.getDeclaredField("KEY_BREATH");
				Linkage.KEY_BREATH = (KeyBinding) Linkage.MK_KEY_BREATH.get(null);
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				Logger log = MorphDragonExtensions.instance.getLogger();
				log.catching(e);
				log.error("C.sL: while initializing sided linkage");
			}
	}

	@Override
	public boolean getBreathingKey() {
		return ((KeyBinding) Linkage.KEY_BREATH).isKeyDown();
	}

	@Override
	public Entity getClientEntity(int dim, int id) {
		WorldClient world = Minecraft.getMinecraft().world;
		if(world == null || world.provider == null || world.provider.getDimension() != dim) return null;
		return world.getEntityByID(id);
	}

	@Override
	public void scheduleClientTask(Runnable toRun) {
		Minecraft.getMinecraft().addScheduledTask(toRun);
	}

	@Override
	public EntityPlayer getLocalPlayer() {
		return Minecraft.getMinecraft().player;
	}

}
