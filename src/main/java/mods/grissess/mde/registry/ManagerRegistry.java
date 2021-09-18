package mods.grissess.mde.registry;

import mods.grissess.mde.manager.BreathManager;
import mods.grissess.mde.manager.FlightManager;
import net.minecraftforge.common.MinecraftForge;

public class ManagerRegistry {
	public BreathManager breath = new BreathManager();
	public FlightManager flight = new FlightManager();
	
	public void register() {
		MinecraftForge.EVENT_BUS.register(breath);
		MinecraftForge.EVENT_BUS.register(flight);
	}
}
