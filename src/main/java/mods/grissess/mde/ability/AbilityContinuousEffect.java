package mods.grissess.mde.ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.ichun.mods.morph.api.ability.Ability;
import mods.grissess.mde.MorphDragonExtensions;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class AbilityContinuousEffect extends Ability {
	public Map<Integer, Integer> effectMap = new HashMap<Integer, Integer>();
	public Map<Potion, Integer> potions;
	
	public static final int OCCASION = 20;
	public static final String TYPE = "continuousEffect";
	public static final ArrayList<Ability> EMPTY = new ArrayList<Ability>();

	public int counter = OCCASION;
	
	public AbilityContinuousEffect() {}
	
	public AbilityContinuousEffect(Map<Integer, Integer> effmap, Map<Potion, Integer> potmap) {
		effectMap.clear();
		if(effmap != null) {
			for(Entry<Integer, Integer> entry: effmap.entrySet()) {
				effectMap.put(entry.getKey(), entry.getValue());
			}
		}
		if(potions != null) potions.clear();
		if(potmap != null) {
			if(potions == null) potions = new HashMap<Potion, Integer>();
			for(Entry<Potion, Integer> entry: potmap.entrySet()) {
				potions.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	@Override
	public Ability clone() {
		return new AbilityContinuousEffect(effectMap, potions);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public Ability parse(String[] args) {
		effectMap.clear();
		for(int idx = 0; idx + 1 < args.length; idx += 2) {
			effectMap.put(
					Integer.parseInt(args[idx]), Integer.parseInt(args[idx+1])
			);
		}
		return this;
	}
	
	@Override
	public void init() {
		EntityPlayer parent = (EntityPlayer) getParent();
		if(parent == null) return;
		MorphDragonExtensions.instance.getLogger()
			.info(String.format("ACE.i: effect %s to %s effects %s potions %s",  this, parent, effectMap, potions));
		potions = new HashMap<Potion, Integer>();
		AbstractAttributeMap attrMap = parent.getAttributeMap();
		for(Entry<Integer, Integer> entry: effectMap.entrySet()) {
			int potionId = entry.getKey(), amplifier = entry.getValue();
			Potion potion = Potion.getPotionById(potionId);
			potion.applyAttributesModifiersToEntity(parent, attrMap, amplifier);
			MorphDragonExtensions.instance.getLogger().info(
					String.format("ACE.i: effect %s to %s applied %s (name %s, id %d) amplifier %d", this, parent, potion, potion.getName(), potionId, amplifier)
			);
			potions.put(potion, amplifier);
		}
	}
	
	@Override
	public void tick() {
		if(--counter == 0) {
			counter = OCCASION;
			// MorphDragonExtensions.instance.getLogger().info("ACE.t: tick");
			if(potions == null) return;
			EntityPlayer parent = (EntityPlayer) getParent();
			if(parent == null) return;
			AbstractAttributeMap attrMap = parent.getAttributeMap();
			for(Entry<Potion, Integer> entry: potions.entrySet()) {
				entry.getKey().applyAttributesModifiersToEntity(parent, attrMap, entry.getValue());
			}
		}
	}
	
	@Override
	public void kill(ArrayList<Ability> next) {
		if(potions == null) return;
		EntityPlayer parent = (EntityPlayer) getParent();
		if(parent == null) return;
		for(Entry<Potion, Integer> entry: potions.entrySet()) {
			Potion potion = entry.getKey();
			int amplifier = entry.getValue();
			potion.removeAttributesModifiersFromEntity(parent, parent.getAttributeMap(), amplifier);
			MorphDragonExtensions.instance.getLogger().info(
					String.format("ACE.k: effect %s from %s removed %s (name %s, id %s) amplifier %d", this, parent, potion, potion.getName(), Potion.getIdFromPotion(potion), amplifier)
			);
		}
		potions.clear();
		potions = null;
	}
}
