package mods.grissess.mde.ability;

import java.util.ArrayList;
import java.util.UUID;

import me.ichun.mods.morph.api.ability.Ability;
import mods.grissess.mde.MorphDragonExtensions;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class AbilityPickRange extends Ability {
	public static final UUID MODIFIER_UUID = UUID.nameUUIDFromBytes("AbilityPickRange modifier".getBytes());
	public double amount;
	
	public static final String TYPE = "pickRange";
	public static final ArrayList<Ability> EMPTY = new ArrayList<Ability>();
	
	public AbilityPickRange() {
		amount = 2.0;
	}
	
	public AbilityPickRange(double amount) {
		this.amount = amount;
	}
	
	@Override
	public Ability clone() {
		return new AbilityPickRange(amount);
	}
	
	@Override
	public String getType() {
		return TYPE;
	}
	
	@Override
	public Ability parse(String[] args) {
		amount = Double.parseDouble(args[0]);
		return this;
	}
	
	@Override
	public void init() {
		EntityPlayer parent = (EntityPlayer) getParent();
		if(parent == null) return;
		MorphDragonExtensions.instance.getLogger()
			.info(String.format("APR.i: effect %s on %s amount %f",  this, parent, amount));
		AttributeModifier modifier = new AttributeModifier(MODIFIER_UUID, "AbilityPickRange", amount - 1.0, 1);
		IAttributeInstance attrib = parent.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
		attrib.removeModifier(MODIFIER_UUID);
		attrib.applyModifier(modifier);
		MorphDragonExtensions.instance.getLogger()
			.info(String.format("APR.i: effect %s to %s applied modifier of %f (resulting in %f)", this, parent, amount - 1.0, attrib.getAttributeValue()));
	}
	
	@Override
	public void kill(ArrayList<Ability> next) {
		EntityPlayer parent = (EntityPlayer) getParent();
		if(parent == null) return;
		IAttributeInstance attrib = parent.getEntityAttribute(EntityPlayer.REACH_DISTANCE);
		attrib.removeModifier(MODIFIER_UUID);
		MorphDragonExtensions.instance.getLogger()
			.info(String.format("APR.k: effect %s from %s removed (resulting in %f)", this, parent, attrib.getAttributeValue()));
	}
}
