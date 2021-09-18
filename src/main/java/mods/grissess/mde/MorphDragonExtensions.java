package mods.grissess.mde;

import org.apache.logging.log4j.Logger;

import me.ichun.mods.morph.api.IApi;
import me.ichun.mods.morph.api.MorphApi;
import me.ichun.mods.morph.api.ability.AbilityApi;
import me.ichun.mods.morph.api.ability.IAbilityHandler;
import mods.grissess.mde.command.CommandMDE;
import mods.grissess.mde.dm2.Linkage;
import mods.grissess.mde.net.Network;
import mods.grissess.mde.proxy.IProxy;
import mods.grissess.mde.registry.AbilityRegistry;
import mods.grissess.mde.registry.ManagerRegistry;
import net.minecraft.command.ServerCommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MorphDragonExtensions.MODID, name = MorphDragonExtensions.NAME, version = MorphDragonExtensions.VERSION)
public class MorphDragonExtensions
{
    public static final String MODID = "mde";
    public static final String NAME = "Morph Dragon Extensions";
    public static final String VERSION = "0.1";

    private static Logger logger;
    
    public Logger getLogger() {
    	return logger;
    }
    
    public SimpleNetworkWrapper channel;
    @SidedProxy(
    		modId = MODID,
    		clientSide = "mods.grissess.mde.proxy.Client",
    		serverSide = "mods.grissess.mde.proxy.Server"
    )
    public static IProxy proxy;
    public IApi morph;
    public ManagerRegistry managers = new ManagerRegistry();
    
    @Mod.Instance(MODID)
    public static MorphDragonExtensions instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        logger.info("MDE.pI: Hello, dergs!");
        managers.register();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        morph = MorphApi.getApiImpl();
        if(!morph.isMorphApi()) {
        	logger.error("MDE.i: Refusing to load--Morph API reports not loaded!");
        	return;
        }
        IAbilityHandler abilities = AbilityApi.getApiImpl();
        AbilityRegistry.register(abilities);
        
        channel = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
        channel.registerMessage(
        		Network.HandlerSetDragonBreed.class,
        		Network.PacketSetDragonBreed.class,
        		Network.PacketKind.SET_DRAGON_BREED.ordinal(),
        		Side.CLIENT
        );
        channel.registerMessage(
        		Network.HandlerDumpEntities.class,
        		Network.PacketDumpEntities.class,
        		Network.PacketKind.DUMP_ENTITIES.ordinal(),
        		Side.CLIENT
		);
        channel.registerMessage(
        		Network.HandlerSetElytraFlight.class,
        		Network.PacketSetElytraFlight.class,
        		Network.PacketKind.SET_ELYTRA_FLIGHT.ordinal(),
        		Side.SERVER
        );
        channel.registerMessage(
        		Network.HandlerSetDragonBreathing.class,
        		Network.PacketSetDragonBreathing.class,
        		Network.PacketKind.SET_DRAGON_BREATHING.ordinal(),
        		Side.SERVER
        );
        
        Linkage.init();
        proxy.sidedLinkage();
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    	// logger.info(String.format("MDE.sS: starting event, side %s", event.getSide()));
		((ServerCommandManager) event.getServer().commandManager).registerCommand(
				new CommandMDE()
		);
		logger.info("MDE.sS: commands registered");
		// sorry
		//Configurator.setRootLevel(Level.ALL);
    }
}
