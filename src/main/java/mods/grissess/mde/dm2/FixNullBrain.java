package mods.grissess.mde.dm2;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;

import mods.grissess.mde.MorphDragonExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class FixNullBrain {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		if(!Linkage.isLinked()) return;
		Logger log = MorphDragonExtensions.instance.getLogger();
		for(WorldServer w: DimensionManager.getWorlds()) {
			for(Entity e: w.loadedEntityList) {
				try {
					if(Linkage.EntityDragonTameable.isInstance(e) && Linkage.EDT_getBrain.invoke(e) == null) {
						log.warn(String.format("FNB.oST: found a null brain of %s in %s", e, w));
						Linkage.EDT_addHelper.invoke(e, Linkage.DB_new.newInstance(e));
					}
				} catch (
						IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| InstantiationException e1
				) {
					log.catching(e1);
					log.error(String.format("FNB.oST: while fixing null brain of %s in %s", e, w));
					log.fatal("Server is probably about to sink with an NPE!");
				}
			}
		}
	}
}
