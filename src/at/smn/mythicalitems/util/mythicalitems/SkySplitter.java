package at.smn.mythicalitems.util.mythicalitems;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class SkySplitter extends MythicalEventItemStack{

	public SkySplitter() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Sky Splitter");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				RayTraceResult result = player.getWorld().rayTraceBlocks(player.getEyeLocation(), player.getLocation().getDirection(), 20);
				Location loc = result.getHitBlock().getLocation().add(0.5,0,0.5);
				Shulker shulker = (Shulker)player.getWorld().spawnEntity(loc, EntityType.SHULKER);
				shulker.setCustomName("skysplitter");
				shulker.setInvulnerable(true);
				shulker.setGlowing(true);
				shulker.setInvisible(true);
				shulker.setAI(false);
				shulker.getWorld().playSound(shulker.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 4f, 0.8f);
				
				int level = player.getLevel();
				float xp = player.getExp();
				
				new BukkitRunnable() {
					
					int i = 0;
					
					@Override
					public void run() {
						if(i == 20) {
							player.setLevel(level);
							player.setExp(xp);
						}else if(i < 20){
							Util.setExpBar(player, i, 2, 20);
						}
						if(i >= 20) {
							Location thunderLoc = loc.clone().add(new Random().nextInt(10) - 5,0,new Random().nextInt(10) - 5);
							LightningStrike strike = thunderLoc.getWorld().strikeLightning(checkLocation(thunderLoc, 10));
							strike.setCustomName("skysplitterstrike");
						}
						if(i >= 100) {
							shulker.remove();
							cancel();
						}
						Location locSky = loc.clone();
						locSky.setY(shulker.getLocation().getY() + 60);
						loc.getWorld().spawnParticle(Particle.CLOUD, locSky, 500, 10, 1, 10, 0.04, null, true);
						i++;
					}
				}.runTaskTimer(Main.getPlugin(), 0, 2);
			}
		}
		return false;
	}
	public static Location checkLocation(Location loc, int maxHeight) {
		Location locTemp = loc.clone();
		for (int i = 0; i < maxHeight; i++) {
			if(!locTemp.add(0,1,0).getBlock().getType().equals(Material.AIR)) {
				return locTemp;
			}
		}
		return loc;
	}

}
