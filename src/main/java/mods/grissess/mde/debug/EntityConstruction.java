package mods.grissess.mde.debug;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.Logger;

import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.dm2.Linkage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

// Uncomment to enable
//@Mod.EventBusSubscriber
@SuppressWarnings("unused")
public class EntityConstruction {
	public static ArrayList<Entity> ents = new ArrayList<Entity>();
	@SubscribeEvent
	public static void onEntityConstructed(EntityEvent.EntityConstructing event) {
		Logger log = MorphDragonExtensions.instance.getLogger();
		try {
			Entity ent = event.getEntity();
			if(!ent.world.isRemote && Linkage.isLinked() && Linkage.EntityBreathNode.isInstance(ent)) {
				log.info(String.format("EC.oEC: breath node constructed: %s", ent));
				ents.add(ent);
			}
		} catch(Throwable e) {
			log.catching(e);
			log.error(String.format("EC.oEC: above while calling toString on entity of class %s", event.getEntity().getClass()));
		}
	}
	
	@SubscribeEvent
	public static void onServerTick(TickEvent.ServerTickEvent event) {
		Logger log = MorphDragonExtensions.instance.getLogger();
		Iterator<Entity> iter = ents.iterator();
		if(iter.hasNext()) {
			log.info("EC.oST: --- tick ---");
		}
		while(iter.hasNext()) {
			Entity ent = iter.next();
			if(ent.isDead) {
				log.info(String.format("EC.oST: removed %s due to death", ent));
				iter.remove();
				continue;
			}
			if(!Linkage.EntityBreathNode.isInstance(ent)) {
				log.warn(String.format("EC.oST: removed %s (wasn't a EBN?)", ent));
				iter.remove();
				continue;
			}
			try {
				Object breathNode = Linkage.EBN_breathNode.get(ent);
				float age = (float) Linkage.BN_getAgeTicks.invoke(breathNode);
				log.info(String.format("EC.oST: tracked entity %s (node %s age %f)", ent, breathNode, age));
			} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
				log.catching(e);
				log.error(String.format("EC.oST: while describing %s", ent));
			}
		}
	}
}
