package mods.grissess.mde.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.apache.logging.log4j.Logger;

import me.ichun.mods.morph.api.ability.Ability;
import me.ichun.mods.morph.api.ability.IAbilityHandler;
import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.ability.AbilityContinuousEffect;
import mods.grissess.mde.ability.AbilityDM2Specific;
import mods.grissess.mde.ability.AbilityElytraFlight;
import mods.grissess.mde.ability.AbilityPickRange;

public class AbilityRegistry {
	public static final ArrayList<Class<? extends Ability>> ABILITIES = new ArrayList<Class<? extends Ability>>();
	
	static {
		ABILITIES.add(AbilityPickRange.class);
		ABILITIES.add(AbilityContinuousEffect.class);
		ABILITIES.add(AbilityElytraFlight.class);
		ABILITIES.add(AbilityDM2Specific.class);
	}
	
	public static void register(IAbilityHandler abilities) {
		Logger logger = MorphDragonExtensions.instance.getLogger();
		logger.info("AR.r: Ability registration begins");
		for(Class<? extends Ability> clz: ABILITIES) {
			String type = "";
			try {
				type = clz.getDeclaredConstructor().newInstance().getType();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				logger.catching(e);
				logger.error(String.format("AR.r: Could not construct ability to register %s", clz));
			}
			MorphDragonExtensions.instance.getLogger().info(String.format("AR.r: Registering ability %s", type));
			abilities.registerAbility(type, clz);
		}
		logger.info("AR.r: Ability registration ends");
	}
}
