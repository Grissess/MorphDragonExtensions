package mods.grissess.mde.command;

import java.lang.reflect.InvocationTargetException;

import mods.grissess.mde.MorphDragonExtensions;
import mods.grissess.mde.dm2.Linkage;
import mods.grissess.mde.net.Network;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;

public class CommandMDE extends CommandBase {
	public static final String NAME = "mde";
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/" + NAME + " help\n(This command isn't well documented and intended for development only)";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length == 0) {
			sender.sendMessage(new TextComponentString(getUsage(sender)));
			return;
		}
		if(args[0].equalsIgnoreCase("help")) {
			sender.sendMessage(new TextComponentString("commands:"));
			sender.sendMessage(new TextComponentString("- help"));
			sender.sendMessage(new TextComponentString("- entities"));
		} else if(args[0].equalsIgnoreCase("entities")) {
			Entity ent = sender.getCommandSenderEntity();
			EntityPlayer ply = (EntityPlayer) ent;
			if(ply == null) {
				sender.sendMessage(new TextComponentString("you don't appear to be a player, sorry"));
				return;
			}
			Entity morph = MorphDragonExtensions.instance.morph.getMorphEntity(
					ply.world,
					ply.getName(),
					Side.SERVER
			);
			sender.sendMessage(new TextComponentString(String.format(
					"Server: you appear to be entity %s with morph %s",
					ply, morph
			)));
			if(Linkage.isLinked() && Linkage.EntityDragonTameable.isInstance(morph)) {
				try {
					sender.sendMessage(new TextComponentString(String.format(
							"Server: your dragon morph is %s, %s, breed %s",
							(boolean) Linkage.EDT_isFlying.invoke(morph)? "flying" : "not flying",
							(boolean) Linkage.EDT_isUsingBreathWeapon.invoke(morph)? "breathing" : "not breathing",
							Linkage.EDT_getBreedType.invoke(morph)
					)));
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					sender.sendMessage(new TextComponentString(String.format(
							"Server: Exception %s occured while getting dragon info",
							e
					)));
				}
			}
			MorphDragonExtensions.instance.channel.sendTo(
					new Network.PacketDumpEntities(),
					(EntityPlayerMP) ply
			);
		}
	}

}
