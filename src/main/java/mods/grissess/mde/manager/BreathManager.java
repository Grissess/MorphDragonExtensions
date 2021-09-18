package mods.grissess.mde.manager;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;

import me.ichun.mods.morph.api.IApi;
import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.dm2.Linkage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class BreathManager extends ManagerBoolean {
	// Ordinarily, we could just update EDT.setUsingBreathWeapon, but Morph sets
	// the server entity Y=-500 "to avoid world interaction", so we have to
	// reach into this call stack ourselves in order to inject the correct
	// position information. As a result, this is largely borrowed from DM2's
	// [decompiled] source.
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		doServerTick(event);
	}
	
	@Override
	public void affect(EntityPlayerMP player) {
		Logger log = MorphDragonExtensions.instance.getLogger();
		Entity morph = MorphDragonExtensions.instance.morph.getMorphEntity(
				player.world,
				player.getName(),
				Side.SERVER
		);
		if(Linkage.isLinked() && Linkage.EntityDragonTameable.isInstance(morph)) {
			try {
				// Basically, invoke this method, but substitute in the (real)
				// player position parameters
				Object breed = Linkage.EDT_getBreed.invoke(morph);
				Object lifeHelper = Linkage.EDT_getLifeStageHelper.invoke(morph);
				Object power = Linkage.DLSH_getBreathPower.invoke(lifeHelper);
				// The position is still a problem during this tick, so we'll
				// update Y for a brief moment
				/*
				double oldY = morph.posY;
				morph.posY = player.posY;
				Object anim = Linkage.EDT_getAnimator.invoke(morph);
				Vec3d origin = (Vec3d) Linkage.DA_getThroatPosition.invoke(anim);
				morph.posY = oldY;
				*/
				Vec3d origin = player.getPositionVector().addVector(0, morph.getEyeHeight(), 0);
				Vec3d terminus = origin.add(player.getLookVec());
				Linkage.DB_continueAndUpdateBreathing.invoke(
						breed,
						player.world,
						origin,
						terminus,
						power,
						morph
				);
				/*
				log.info(String.format(
						"BM.a: cAUB on %s power %s morph %s player %s lh %s anim %s origin %s terminus %s",
						breed, power, morph, player, lifeHelper, anim, origin, terminus
				));
				*/
				/*
				Object breathHelper = Linkage.EDT_getBreathHelper.invoke(morph);
				Object breathArea = Linkage.DBH_getBreathAffectedArea.invoke(breathHelper);
				Object breathWeapon = Linkage.BAA_breathWeapon.get(breathArea);
				@SuppressWarnings("unchecked")
				HashMap<Integer, Object> affected = (HashMap<Integer, Object>) Linkage.BAA_entitiesAffectedByBeam.get(breathArea);
				for(Entry<Integer, Object> ent: affected.entrySet()) {
					int entID = ent.getKey();
					Entity entity = player.world.getEntityByID(entID);
					float density = (float)Linkage.BAE_getHitDensity.invoke(ent.getValue());
					float damage = density * Linkage.BW_FIRE_DAMAGE.getFloat(breathWeapon);
					log.info(String.format(
							"BM.a: breath affects ent %d (%s) density %f (damage %f)",
							entID,
							entity,
							density,
							damage
					));
					// entity.attackEntityFrom(DamageSource.causeMobDamage(player), damage);
				}
				*/
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.catching(e);
				log.error(String.format("BM.a: while updating breath"));
			}
		} else {
			log.warn(String.format(
					"BM.a: player %s morph %s not a dragon while breathing",
					player, morph
			));
		}
	}
	
	/*
	@SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
	public void onLivingAttackFirst(LivingAttackEvent event) {
		Logger log = MorphDragonExtensions.instance.getLogger();
		DamageSource source = event.getSource();
		EntityLivingBase victim = event.getEntityLiving();
		IApi morph = MorphDragonExtensions.instance.morph;
		if(!victim.world.isRemote && morph.isEntityAMorph(victim, Side.SERVER)) {
			// Generally, the morph shouldn't take damage--the player should
			// (this should be redundant with Morph, but nonetheless)
			event.setCanceled(true);
			return;
		}
		log.info(String.format(
				"BM.oLAF: event %s, source %s, target %s",
				event, event.getSource().damageType, event.getEntityLiving()
		));
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onPlayerAttackTarget(AttackEntityEvent event) {
		Logger log = MorphDragonExtensions.instance.getLogger();
		EntityPlayer player = event.getEntityPlayer();
		Entity victim = event.getTarget();
		log.info(String.format(
				"BM.oPAT: event %s, player %s, victim %s, canceled %s, result %s",
				event, player, victim, event.isCanceled(), event.getResult()
		));
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event) {
		Logger log = MorphDragonExtensions.instance.getLogger();
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult cursor = mc.objectMouseOver;
		PlayerControllerMP controller = mc.playerController;
		log.info(String.format(
				"BM.oCT: cursor %s, reach %f",
				cursor, controller == null ? null : controller.getBlockReachDistance()
		));
	}
	*/
	
	// XXX: need to receiveCanceled because Morph cancels damage events caused
	// by a Morph; we set ourselves to a lower priority to intercept that
	@SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
	public void onLivingAttack(LivingAttackEvent event) {
		// Logger log = MorphDragonExtensions.instance.getLogger();
		DamageSource source = event.getSource();
		EntityLivingBase victim = event.getEntityLiving();
		IApi morph = MorphDragonExtensions.instance.morph;
		if(!victim.world.isRemote && morph.isEntityAMorph(victim, Side.SERVER)) {
			// Generally don't let the morph entity take damage--the player should instead
			// (and a bunch of weird things cause it to take damage anyway)
			// the following is somewhat redundant with Morph itself
			event.setCanceled(true);
			return;
		}
		/*
		log.info(String.format(
				"BM.oLA: damage event %s, source %s, target %s",
				event, source.damageType,
				event.getEntityLiving()
		));
		*/
		if(source instanceof EntityDamageSource) {
			// EntityDamageSource eds = (EntityDamageSource) source;
			EntityLivingBase attacker = (EntityLivingBase) source.getTrueSource();
			/*
			log.info(String.format(
					"BM.oLA: ... attacker %s, immediate %s",
					attacker, source.getImmediateSource()
			));
			*/
			if(morph.isEntityAMorph(attacker, Side.SERVER)) {
				if(victim instanceof EntityPlayer) {
					EntityPlayer ply = (EntityPlayer) victim;
					if(attacker == morph.getMorphEntity(ply.world, ply.getName(), Side.SERVER)) {
						// Don't let the player damage themselves
						event.setCanceled(true);
						return;
					}
				}
				event.setCanceled(false);  // uncancel Morph's cancellation
			}
		}
	}
}
