package mods.grissess.mde.dm2;

import org.apache.logging.log4j.Logger;

import me.ichun.mods.morph.api.IApi;
import mods.grissess.mde.MorphDragonExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber
public class ExplosiveDigging {
	public static final float DIG_STRENGTH = 4;
	public static boolean suppressEntityDamage = false;

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onPlayerClick(PlayerInteractEvent.LeftClickBlock event) {
		if(!Linkage.isLinked()) return;
		World world = event.getWorld();
		if(world.isRemote) return;
		Logger log = MorphDragonExtensions.instance.getLogger();
		EntityPlayer player = event.getEntityPlayer();
		IApi morphAPI = MorphDragonExtensions.instance.morph;
		EntityLivingBase morph = morphAPI.getMorphEntity(world, player.getName(), Side.SERVER);
		if(Linkage.EntityDragonTameable.isInstance(morph)) {
			ItemStack stack = player.getHeldItemMainhand();
			if(stack.isEmpty()) {
				event.setUseBlock(Result.DENY);
				event.setUseItem(Result.DENY);
				BlockPos pos = event.getPos();
				log.info(String.format("ED.oPC: digging at %s", pos));
				suppressEntityDamage = true;
				Explosion expl = new Explosion(world, morph, pos.getX(), pos.getY(), pos.getZ(), DIG_STRENGTH, false, true);
				expl.doExplosionA();
				// skip B; damage happens in onDetonate
				suppressEntityDamage = false;
			}
		}
	}
	
	@SubscribeEvent
	public static void onDetonate(ExplosionEvent.Detonate event) {
		if(suppressEntityDamage) {
			// Logger log = MorphDragonExtensions.instance.getLogger();
			event.getAffectedEntities().clear(); // Still need to do this because it happens in A
			World w = event.getWorld();
			if(w.isRemote) return;
			Explosion expl = event.getExplosion();
			for(BlockPos pos: event.getAffectedBlocks()) {
				IBlockState bs = w.getBlockState(pos);
				if(bs.getMaterial() == Material.AIR) continue;
				Block bk = bs.getBlock();
				if(bk.canDropFromExplosion(expl)) {
					bk.dropBlockAsItem(w, pos, bs, 0);
				}
				bk.onBlockExploded(w, pos, expl);
			}
		}
	}
}
