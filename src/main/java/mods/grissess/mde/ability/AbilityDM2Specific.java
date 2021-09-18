package mods.grissess.mde.ability;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.Logger;

import me.ichun.mods.morph.api.ability.Ability;
import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.dm2.Linkage;
import mods.grissess.mde.net.Network;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class AbilityDM2Specific extends Ability {
	public static final String TYPE = "dm2Specific";
	public static final int OCCASION = 600;
	
	/*
	 * NB: quite a few things aren't sync'd to the client because of the way
	 * Morph ultimately works; we have to handle that synchronization ourselves.
	 * Fortunately, we can borrow DM2's own data channels for as much.
	 */
	public boolean firstTick = true;
	public int occasion = OCCASION;
	
	public AbilityDM2Specific() {}
	
	@Override
	public Ability clone() {
		return new AbilityDM2Specific();
	}
	
	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public void tick() {
		Logger log = MorphDragonExtensions.instance.getLogger();
		if(!Linkage.isLinked()) return;
		EntityPlayer parent = (EntityPlayer) getParent();
		if(parent == null) return;
		EntityLivingBase morph = MorphDragonExtensions.instance.morph.getMorphEntity(
				parent.world, parent.getName(),
				parent.world.isRemote ? Side.CLIENT : Side.SERVER
		);
		if(morph == null) return;
		try {
			/*
			log.info(String.format(
					"ADM2S.t: %s tick: dragon %s state %s, %s, breed %s",
					parent.world.isRemote? "client" : "server",
					morph,
					(boolean) Linkage.EDT_isFlying.invoke(morph)? "flying" : "not flying",
					(boolean) Linkage.EDT_isUsingBreathWeapon.invoke(morph)? "breathing" : "not breathing",
					Linkage.EDT_getBreedType.invoke(morph)
			));
			int notBreath = 0;
			for(Entity ent: parent.world.loadedEntityList) {
				if(Linkage.EntityBreathNode.isInstance(ent)) {
					log.info(String.format(
							"ADM2S.t: breath entity %s",
							ent
					));
				} else {
					notBreath++;
				}
			}
			log.info(String.format("ADM2S.t: non-breath-node entities: %d/%d", notBreath, parent.world.loadedEntityList.size()));
			*/
			if(firstTick || --occasion <= 0) {
				occasion = OCCASION;
				firstTick = false;
				log.info("ADM2S.t: occasion");
				if(!parent.world.isRemote) {
					String breedName = ((Enum<?>) Linkage.EDT_getBreedType.invoke(morph)).name();
					log.info(String.format("ADM2S.t: sending breed %s", breedName));
					MorphDragonExtensions.instance.channel.sendToAll(
							new Network.PacketSetDragonBreed(
									parent,
									breedName
							)
					);
				}
			}
			Linkage.EDT_setFlying.invoke(morph, parent.isElytraFlying() || parent.capabilities.isFlying);
			if(parent.world.isRemote) {
				boolean breathState = MorphDragonExtensions.proxy.getBreathingKey();
				if(breathState != (boolean) Linkage.EDT_isUsingBreathWeapon.invoke(morph)) {
					log.info(String.format("ADM2S.t: breath state now %s", breathState));
					Linkage.EDT_setUsingBreathWeapon.invoke(morph, breathState);
					MorphDragonExtensions.instance.channel.sendToServer(
							new Network.PacketSetDragonBreathing(
									parent,
									breathState
							)
					);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			log.catching(e);
			log.error(String.format("ADM2S.t: Error syncing state on %s, morph %s!", getParent(), morph));
		}
	}
}
