package mods.grissess.mde.dm2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import mods.grissess.mde.MorphDragonExtensions;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class Linkage {
	public static final String
		ENTITY_DRAGON_TAMEABLE = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.EntityTameableDragon";
	public static Class<?> EntityDragonTameable = null;
	
	public static final String
		MOD_KEYS = "com.TheRPGAdventurer.ROTD.inits.ModKeys";
	public static Class<?> ModKeys = null;
	
	public static final String
		SERVER_PROXY = "com.TheRPGAdventurer.ROTD.proxy.ServerProxy";
	public static Class<?> ServerProxy = null;
	
	public static final String
		MESSAGE_DRAGON_BREATH = "com.TheRPGAdventurer.ROTD.network.MessageDragonBreath";
	public static Class<?> MessageDragonBreath;
	
	public static final String
		DRAGON_MOUNTS = "com.TheRPGAdventurer.ROTD.DragonMounts";
	public static Class<?> DragonMounts;
	
	public static final String
		ENUM_DRAGON_BREED = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds.EnumDragonBreed";
	public static Class<?> EnumDragonBreed;
	
	public static final String
		DRAGON_BREED = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breeds.DragonBreed";
	public static Class<?> DragonBreed;
	
	public static final String
		POWER = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode$Power";
	public static Class<?> Power;
	
	public static final String
		DRAGON_LIFE_STAGE_HELPER = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonLifeStageHelper";
	public static Class<?> DragonLifeStageHelper;
	
	public static final String
		DRAGON_ANIMATOR = "com.TheRPGAdventurer.ROTD.client.model.dragon.anim.DragonAnimator";
	public static Class<?> DragonAnimator;
	
	public static final String
		ENTITY_BREATH_NODE = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.EntityBreathNode";
	public static Class<?> EntityBreathNode;
	
	public static final String
		BREATH_NODE = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathNode";
	public static Class<?> BreathNode;
	
	public static final String
		DRAGON_BREATH_HELPER = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.DragonBreathHelper";
	public static Class<?> DragonBreathHelper;
	
	public static final String
		BREATH_AFFECTED_AREA = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathAffectedArea";
	public static Class<?> BreathAffectedArea;
	
	public static final String
		BREATH_AFFECTED_ENTITY = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.BreathAffectedEntity";
	public static Class<?> BreathAffectedEntity;
	
	public static final String
		BREATH_WEAPON = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.breath.weapons.BreathWeapon";
	public static Class<?> BreathWeapon;
	
	public static final String
		DRAGON_BRAIN = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonBrain";
	public static Class<?> DragonBrain;
	
	public static final String
		DRAGON_HELPER = "com.TheRPGAdventurer.ROTD.objects.entity.entitytameabledragon.helper.DragonHelper";
	public static Class<?> DragonHelper;
	
	public static Method EDT_isFlying;
	public static Method EDT_setFlying;
	public static Method EDT_isUsingBreathWeapon;
	public static Method EDT_setUsingBreathWeapon;
	public static Method EDT_isUsingAltBreathWeapon;
	public static Method EDT_setUsingAltBreathWeapon;
	public static Method EDT_getBreedType;
	public static Method EDT_setBreedType;
	public static Method EDT_getBreed;
	public static Method EDT_getLifeStageHelper;
	public static Method EDT_getAnimator;
	public static Method EDT_getBreathHelper;
	public static Method EDT_getBrain;
	public static Method EDT_addHelper;
	public static Field EDT_DATA_BREED;
	public static DataParameter<String> DATA_BREED;
	
	public static Field MK_KEY_BREATH;
	public static Object KEY_BREATH;
	
	public static Method SP_getNetwork;
	public static SimpleNetworkWrapper network;
	
	public static Constructor<?> MDB_new;
	
	public static Field DM_instance;
	public static Field DM_proxy;
	public static Field DM_NETWORK_WRAPPER;
	public static Object dragonMounts;
	public static Object dragonMountsProxy;
	
	public static Method DB_continueAndUpdateBreathing;
	
	public static Method DLSH_getBreathPower;
	
	public static Method DA_getThroatPosition;
	
	public static Field EBN_breathNode;
	
	public static Method BN_getAgeTicks;
	
	public static Method DBH_getBreathAffectedArea;
	
	public static Field BAA_entityBreathNodes;
	public static Field BAA_entitiesAffectedByBeam;
	public static Field BAA_breathWeapon;
	public static Method BAA_implementEffectsOnEntitiesTick;
	
	public static Method BAE_getHitDensity;
	
	public static Field BW_FIRE_DAMAGE;
	public static Field BW_dragon;
	
	public static Constructor<?> DB_new;
	
	// For the accessible class Entity:
	public static String OBF_E_ENTITY_ID = "field_145783_c";
	public static Field E_entityId;
	
	public static boolean isLinked() {
		return EntityDragonTameable != null;
	}

	@SuppressWarnings("unchecked")
	public static void init() {
		Logger log = MorphDragonExtensions.instance.getLogger();
		try {
			EntityDragonTameable = Class.forName(ENTITY_DRAGON_TAMEABLE);
			ServerProxy = Class.forName(SERVER_PROXY);
			MessageDragonBreath = Class.forName(MESSAGE_DRAGON_BREATH);
			DragonMounts = Class.forName(DRAGON_MOUNTS);
			EnumDragonBreed = Class.forName(ENUM_DRAGON_BREED);
			DragonBreed = Class.forName(DRAGON_BREED);
			Power = Class.forName(POWER);
			DragonLifeStageHelper = Class.forName(DRAGON_LIFE_STAGE_HELPER);
			DragonAnimator = Class.forName(DRAGON_ANIMATOR);
			EntityBreathNode = Class.forName(ENTITY_BREATH_NODE);
			BreathNode = Class.forName(BREATH_NODE);
			DragonBreathHelper = Class.forName(DRAGON_BREATH_HELPER);
			BreathAffectedArea = Class.forName(BREATH_AFFECTED_AREA);
			BreathAffectedEntity = Class.forName(BREATH_AFFECTED_ENTITY);
			BreathWeapon = Class.forName(BREATH_WEAPON);
			DragonBrain = Class.forName(DRAGON_BRAIN);
			DragonHelper = Class.forName(DRAGON_HELPER);
		} catch(ClassNotFoundException e) {
			log.catching(e);
			log.error("Couldn't load critical information from DM2, there will be no support");
			return;
		}
		
		try {
			EDT_isFlying = EntityDragonTameable.getDeclaredMethod("isFlying");
			EDT_setFlying = EntityDragonTameable.getDeclaredMethod("setFlying", Boolean.TYPE);
			EDT_isUsingBreathWeapon = EntityDragonTameable.getDeclaredMethod("isUsingBreathWeapon");
			EDT_setUsingBreathWeapon = EntityDragonTameable.getDeclaredMethod("setUsingBreathWeapon", Boolean.TYPE);
			EDT_isUsingAltBreathWeapon = EntityDragonTameable.getDeclaredMethod("isUsingAltBreathWeapon");
			EDT_setUsingAltBreathWeapon = EntityDragonTameable.getDeclaredMethod("setUsingAltBreathWeapon", Boolean.TYPE);
			EDT_getBreedType = EntityDragonTameable.getDeclaredMethod("getBreedType");
			EDT_setBreedType = EntityDragonTameable.getDeclaredMethod("setBreedType", EnumDragonBreed);
			EDT_getBreed = EntityDragonTameable.getDeclaredMethod("getBreed");
			EDT_getLifeStageHelper = EntityDragonTameable.getDeclaredMethod("getLifeStageHelper");
			EDT_getAnimator = EntityDragonTameable.getDeclaredMethod("getAnimator");
			EDT_getBreathHelper = EntityDragonTameable.getDeclaredMethod("getBreathHelper");
			EDT_getBrain = EntityDragonTameable.getDeclaredMethod("getBrain");
			EDT_addHelper = EntityDragonTameable.getDeclaredMethod("addHelper", DragonHelper);
			EDT_addHelper.setAccessible(true);
			// see setBreed
			EDT_DATA_BREED = EntityDragonTameable.getDeclaredField("DATA_BREED");
			EDT_DATA_BREED.setAccessible(true);
			DATA_BREED = (DataParameter<String>) EDT_DATA_BREED.get(null);
			
			SP_getNetwork = ServerProxy.getDeclaredMethod("getNetwork");
			
			MDB_new = MessageDragonBreath.getDeclaredConstructor(Integer.TYPE, Boolean.TYPE);
			
			DM_instance = DragonMounts.getDeclaredField("instance");
			DM_proxy = DragonMounts.getDeclaredField("proxy");
			DM_NETWORK_WRAPPER = DragonMounts.getDeclaredField("NETWORK_WRAPPER");
			dragonMounts = DM_instance.get(null);
			dragonMountsProxy = DM_proxy.get(null);
			network = (SimpleNetworkWrapper) DM_NETWORK_WRAPPER.get(null);
			
			DB_continueAndUpdateBreathing = DragonBreed.getDeclaredMethod("continueAndUpdateBreathing", World.class, Vec3d.class, Vec3d.class, Power, EntityDragonTameable);
			
			DLSH_getBreathPower = DragonLifeStageHelper.getDeclaredMethod("getBreathPower");
			
			DA_getThroatPosition = DragonAnimator.getDeclaredMethod("getThroatPosition");
			
			EBN_breathNode = EntityBreathNode.getDeclaredField("breathNode");
			EBN_breathNode.setAccessible(true);
			
			BN_getAgeTicks = BreathNode.getDeclaredMethod("getAgeTicks");
			
			DBH_getBreathAffectedArea = DragonBreathHelper.getDeclaredMethod("getBreathAffectedArea");
			
			BAA_entityBreathNodes = BreathAffectedArea.getDeclaredField("entityBreathNodes");
			BAA_entityBreathNodes.setAccessible(true);
			BAA_entitiesAffectedByBeam = BreathAffectedArea.getDeclaredField("entitiesAffectedByBeam");
			BAA_entitiesAffectedByBeam.setAccessible(true);
			BAA_breathWeapon = BreathAffectedArea.getDeclaredField("breathWeapon");
			BAA_implementEffectsOnEntitiesTick = BreathAffectedArea.getDeclaredMethod("implementEffectsOnEntitiesTick", World.class, HashMap.class);
			BAA_implementEffectsOnEntitiesTick.setAccessible(true);
			
			BAE_getHitDensity = BreathAffectedEntity.getDeclaredMethod("getHitDensity");
			
			BW_FIRE_DAMAGE = BreathWeapon.getDeclaredField("FIRE_DAMAGE");
			BW_FIRE_DAMAGE.setAccessible(true);
			BW_dragon = BreathWeapon.getDeclaredField("dragon");
			BW_dragon.setAccessible(true);
			
			DB_new = DragonBrain.getConstructor(EntityDragonTameable);
			
			E_entityId = Entity.class.getDeclaredField(OBF_E_ENTITY_ID);
			E_entityId.setAccessible(true);
		} catch (NoSuchMethodException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
			log.catching(e);
			log.error("Couldn't load methods from DM2--are you sure you're using the right version?");
			EntityDragonTameable = null;
			return;
		}
		
		log.info("L.i: Linkage to DM2 established");
	}
	
	public static void sendBreathMessage(int dragonId, boolean state) {
		try {
			Object message = MDB_new.newInstance(dragonId, state);
			network.sendToServer((IMessage) message);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Logger log = MorphDragonExtensions.instance.getLogger();
			log.catching(e);
			log.error(String.format("L.sBM: sending breath message for %d, state %s", dragonId, state));
		}
	}
	
	// Ordinarily, this can't be done clientside
	public static void setBreed(Object dragon, String breed) {
		if(!isLinked()) return;
		if(!EntityDragonTameable.isInstance(dragon)) {
			MorphDragonExtensions.instance.getLogger()
				.warn(String.format("L.sB: ent %s not a dragon (while setting breed %s)", dragon, breed));
			return;
		}
		((Entity) dragon).getDataManager().set(DATA_BREED, breed);
	}
}
