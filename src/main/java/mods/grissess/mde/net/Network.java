package mods.grissess.mde.net;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.Logger;

import io.netty.buffer.ByteBuf;
import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.dm2.Linkage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class Network {
	public static enum PacketKind {
		SET_ELYTRA_FLIGHT,
		SET_DRAGON_BREED,
		SET_DRAGON_BREATHING,
		DUMP_ENTITIES,
	};
	
	public abstract static class Packet implements IMessage {
		public abstract PacketKind getKind();
	}
	
	public abstract static class Handler<T extends IMessage> implements IMessageHandler<T, IMessage> {
		public abstract PacketKind getKind();
	}
	
	public static class PacketSetElytraFlight extends Packet {
		public EntityPlayer player;
		public boolean enabled;
		
		public PacketSetElytraFlight() {}
		public PacketSetElytraFlight(EntityPlayer player, boolean enabled) {
			this.player = player;
			this.enabled = enabled;
		}
		
		@Override
		public PacketKind getKind() {
			return PacketKind.SET_ELYTRA_FLIGHT;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			World world = DimensionManager.getWorld(buf.readInt());
			player = (EntityPlayer) world.getEntityByID(buf.readInt());
			enabled = buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(player.dimension);
			buf.writeInt(player.getEntityId());
			buf.writeBoolean(enabled);
		}
	}
	
	public static class HandlerSetElytraFlight extends Handler<PacketSetElytraFlight> {
		@Override
		public PacketKind getKind() {
			return PacketKind.SET_ELYTRA_FLIGHT;
		}

		@Override
		public IMessage onMessage(PacketSetElytraFlight message, MessageContext ctx) {
			((WorldServer) ctx.getServerHandler().player.world).addScheduledTask(() -> {
				MorphDragonExtensions.instance.getLogger()
					.info(String.format("HSEF.oM: player %s enabled %s", message.player, message.enabled));
				EntityPlayerMP player = (EntityPlayerMP) message.player;
				if(player != null) {
					if(message.enabled) {
						MorphDragonExtensions.instance.managers.flight.add(player);
					} else {
						MorphDragonExtensions.instance.managers.flight.remove(player);
					}
				}
			});
			return null;
		}
	}
	
	public static class PacketSetDragonBreed extends Packet {
		public static final int MAX_NAME_BYTES = 48;
		
		public EntityPlayer player;
		String breedName;
		
		public PacketSetDragonBreed() {}
		public PacketSetDragonBreed(EntityPlayer ply, String breed) {
			player = ply;
			breedName = breed;
		}

		@Override
		public PacketKind getKind() {
			return PacketKind.SET_DRAGON_BREED;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			int dim = buf.readInt();
			int id = buf.readInt();
			player = (EntityPlayer) MorphDragonExtensions.proxy.getClientEntity(dim, id);
			int bytes = buf.readInt();
			if(bytes > MAX_NAME_BYTES) {
				bytes = MAX_NAME_BYTES;
			}
			breedName = buf.readBytes(bytes).toString(StandardCharsets.UTF_8);
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(player.dimension);
			buf.writeInt(player.getEntityId());
			byte[] bytes = breedName.getBytes(StandardCharsets.UTF_8);
			buf.writeInt(bytes.length);
			buf.writeBytes(bytes);
		}
	}
	
	public static class HandlerSetDragonBreed extends Handler<PacketSetDragonBreed> {
		@Override
		public PacketKind getKind() {
			return PacketKind.SET_DRAGON_BREED;
		}

		@Override
		public IMessage onMessage(PacketSetDragonBreed message, MessageContext ctx) {
			MorphDragonExtensions.proxy.scheduleClientTask(() -> {
				Logger log = MorphDragonExtensions.instance.getLogger();
				if(message.player == null) {
					log.warn(String.format("HSDB.oM: player is null (for breed %s), aborting", message.breedName));
					return;
				}
				Entity morph = MorphDragonExtensions.instance.morph.getMorphEntity(
						message.player.world,
						message.player.getName(),
						Side.CLIENT
				);
				if(Linkage.isLinked() && Linkage.EntityDragonTameable.isInstance(morph)) {
					try {
						Field breedField = Linkage.EnumDragonBreed.getDeclaredField(message.breedName);
						if(breedField != null && breedField.isEnumConstant()) {
							Linkage.setBreed(morph, message.breedName);
							Object breed = Linkage.EDT_getBreedType.invoke(morph);
							log.info(String.format("HSDB.oM: set ent %s breed to %s (returns as %s)", morph, message.breedName, breed));
						} else {
							log.warn(String.format("HSDB.oM: failed to set ent %s breed to %s (not a known variant)", morph, message.breedName));
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException | SecurityException e) {
						log.catching(e);
						log.error(String.format("HSDB.oM: set ent %s breed %s failed due to exception", morph, message.breedName));
					}
				} else {
					log.warn(String.format("HSDB.oM: failed to set ent %s breed %s: no DM2 link or bad ent", morph, message.breedName));
				}
			});
			return null;
		}
	}
	
	public static class PacketSetDragonBreathing extends Packet {
		public Entity player;
		public boolean state;
		
		public PacketSetDragonBreathing() {}
		public PacketSetDragonBreathing(Entity ply, boolean sta) {
			player = ply;
			state = sta;
		}
		
		@Override
		public PacketKind getKind() {
			return PacketKind.SET_DRAGON_BREATHING;
		}

		@Override
		public void fromBytes(ByteBuf buf) {
			World world = DimensionManager.getWorld(buf.readInt());
			player = world.getEntityByID(buf.readInt());
			state = buf.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf buf) {
			buf.writeInt(player.dimension);
			buf.writeInt(player.getEntityId());
			buf.writeBoolean(state);
		}
	}
	
	public static class HandlerSetDragonBreathing extends Handler<PacketSetDragonBreathing> {
		@Override
		public PacketKind getKind() {
			return PacketKind.SET_DRAGON_BREATHING;
		}

		@Override
		public IMessage onMessage(PacketSetDragonBreathing message, MessageContext ctx) {
			((WorldServer) ctx.getServerHandler().player.world).addScheduledTask(() -> {
				Logger log = MorphDragonExtensions.instance.getLogger();
				EntityPlayerMP player = (EntityPlayerMP) message.player;
				if(player == null) {
					log.warn("HSDBg.oM: received null player");
					return;
				}
				if(message.state) {
					MorphDragonExtensions.instance.managers.breath.add(player);
				} else {
					MorphDragonExtensions.instance.managers.breath.remove(player);
				}
			});
			return null;
		}
	}
	
	public static class PacketDumpEntities extends Packet {
		public PacketDumpEntities() {}
		@Override
		public PacketKind getKind() {
			return PacketKind.DUMP_ENTITIES;
		}

		@Override
		public void fromBytes(ByteBuf buf) { }

		@Override
		public void toBytes(ByteBuf buf) { }
	}
	
	public static class HandlerDumpEntities extends Handler<PacketDumpEntities> {
		@Override
		public PacketKind getKind() {
			return PacketKind.DUMP_ENTITIES;
		}

		@Override
		public IMessage onMessage(PacketDumpEntities message, MessageContext ctx) {
			MorphDragonExtensions.proxy.scheduleClientTask(() -> {
				EntityPlayer player = MorphDragonExtensions.proxy.getLocalPlayer();
				Entity morph = MorphDragonExtensions.instance.morph.getMorphEntity(
						player.world,
						player.getName(),
						Side.CLIENT
				);
				player.sendMessage(new TextComponentString(
						String.format(
								"Client: you appear to be entity %s with morph %s",
								player, morph
						)
				));
				if(Linkage.isLinked() && Linkage.EntityDragonTameable.isInstance(morph)) {
					try {
						player.sendMessage(new TextComponentString(String.format(
								"Client: your dragon is %s, %s, breed %s",
								(boolean)Linkage.EDT_isFlying.invoke(morph)? "flying" : "not flying",
								(boolean)Linkage.EDT_isUsingBreathWeapon.invoke(morph)? "breathing" : "not breathing",
								Linkage.EDT_getBreedType.invoke(morph)
						)));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						player.sendMessage(new TextComponentString(String.format(
								"Client: exception %s occured while getting dragon info",
								e
						)));
					}
				}
			});
			return null;
		}
	}
}
