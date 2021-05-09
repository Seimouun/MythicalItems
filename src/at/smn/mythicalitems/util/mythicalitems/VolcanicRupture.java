package at.smn.mythicalitems.util.mythicalitems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class VolcanicRupture extends MythicalEventItemStack {

	public VolcanicRupture() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Volcanic Rupture");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Location origin = player.getEyeLocation();
				new BukkitRunnable() {
					
					double d = 0;
					Vector originVector = player.getEyeLocation().getDirection();
					
					@Override
					public void run() {
						if(d < 10) {
							originVector = originVector.add(player.getLocation().getDirection());
							Vector direction = origin.getDirection().multiply(d).add(originVector);
							Location loc = origin.clone().add(direction);
							player.getWorld().spawnParticle(Particle.FLAME, loc, 20, 0.1, 0.1, 0.1, 0.02, null, true);
							d+=0.2;
						}else {
							cancel();
						}
					}
				}.runTaskTimer(Main.getPlugin(), 0, 1);
			}
			
		}
		return false;
	}
	public boolean checkEntities(Entity originalEntity, Location loc) {
		for(Entity e : loc.getWorld().getNearbyEntities(loc, 1,1,1)) {
			spreadRupture(originalEntity, e);
			return true;
		}
		return false;
	}
	public void spreadRupture(Entity from, Entity to) {
		new BukkitRunnable() {
			
			Vector direction = Util.genVec(from.getLocation(), to.getLocation());
			Location origin = from.getLocation();
			double d = 0;
			
			@Override
			public void run() {
				Location loc = origin.clone().add(direction.clone().multiply(d));
				from.getWorld().spawnParticle(Particle.FLAME, loc, 15, 0.05, 0.05, 0.05, 0.01, null, true);
				d+=0.1;
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(), 0, 1);
	}
}
