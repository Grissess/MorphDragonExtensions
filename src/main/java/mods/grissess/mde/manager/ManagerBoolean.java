package mods.grissess.mde.manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import mods.grissess.mde.MorphDragonExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ManagerBoolean {
	public Map<Integer, Set<Integer>> inForce = new HashMap<Integer, Set<Integer>>();

	public void add(EntityPlayerMP player) {
		MorphDragonExtensions.instance.getLogger()
			.info(String.format("FM.aF: added %s", player));
		if(!inForce.containsKey(player.dimension)) {
			inForce.put(player.dimension, new HashSet<Integer>());
		}
		inForce.get(player.dimension).add(player.getEntityId());
	}
	
	public void remove(EntityPlayerMP player) {
		MorphDragonExtensions.instance.getLogger()
			.info(String.format("FM.rF: removed %s", player));
		if(inForce.containsKey(player.dimension)) {
			inForce.get(player.dimension).remove(player.getEntityId());
			if(inForce.get(player.dimension).isEmpty()) {
				inForce.remove(player.dimension);
			}
		}
	}

	// Not subscribed--call it from your own EventBusSubscriber
	public final void doServerTick(TickEvent.ServerTickEvent event) {
		if(event.phase == Phase.END) {
			Logger log = MorphDragonExtensions.instance.getLogger();
			Set<Integer> removeWorlds = new HashSet<Integer>();
			for(Entry<Integer, Set<Integer>> entry: inForce.entrySet()) {
				World world = DimensionManager.getWorld(entry.getKey());
				if(world == null) {
					log.info(String.format("MB.oST: removing dim %d due to unload", entry.getKey()));
					removeWorlds.add(entry.getKey());
				} else {
					Set<Integer> removeIds = new HashSet<Integer>();
					for(int id: entry.getValue()) {
						Entity ent = world.getEntityByID(id);
						if(ent == null) {
							log.info(String.format("MB.oST: removing ent %d (no longer exists)", id));
							removeIds.add(id);
						} else {
							EntityPlayerMP player = (EntityPlayerMP) ent;
							if(player == null || shouldCancel(player)) {
								log.info(String.format("MB.oST: removing player %s (null or should cancel)", player));
								removeIds.add(id);
							} else {
								// log.info(String.format("MB.oST: inForce %s", player));
								affect(player);
							}
						}
					}
					entry.getValue().removeAll(removeIds);
				}
			}
			for(int dim: removeWorlds) {
				inForce.remove(dim);
			}
		}
	}
	
	public boolean shouldCancel(EntityPlayerMP player) {
		return false;
	}
	
	public void affect(EntityPlayerMP player) {}
}
