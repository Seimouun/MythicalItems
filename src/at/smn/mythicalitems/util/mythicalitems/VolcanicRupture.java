package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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

	public static HashMap<String, Integer> entityHitHash = new HashMap<>();
	public static final int DAMAGE = 15;
	
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
				entityHitHash.remove(player.getName());
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1f, 0.6f);
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
							
							for(Entity e : loc.getWorld().getNearbyEntities(loc, 0.4,0.4,0.4)) {
								if(!e.equals(player)) {
									Util.damageEntity((LivingEntity)e, player, DAMAGE);
									((LivingEntity)e).setFireTicks(60);
									spreadRupture(player, e);
									cancel();
									player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1f, 1f + new Random().nextFloat() * 0.3f);
									break;
								}
							}
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
	public void spreadRupture(Player player, Entity originalEntity) {
		for(Entity e : originalEntity.getNearbyEntities(6,3,6)) {
			if(!e.equals(player) && !e.getUniqueId().equals(originalEntity.getUniqueId()) && entityHitHash.getOrDefault(player.getName(), 0) < 10 && e instanceof LivingEntity) {
				entityHitHash.put(player.getName(), entityHitHash.getOrDefault(player.getName(), 0) + 1);
				rupture(player, originalEntity, e);
			}
		}
	}
	public void rupture(Player player, Entity from, Entity to) {
		new BukkitRunnable() {
			
			Location origin = from.getLocation().add(0,from.getHeight()/2,0);
			double d = 0;
			
			@Override
			public void run() {
				if(d <= 1) {
					Location loc = Util.lerp3D(d, origin, to.getLocation().add(0,to.getHeight()/2,0));
					from.getWorld().spawnParticle(Particle.FLAME, loc, 15, 0.05, 0.05, 0.05, 0.01, null, true);
					d+=0.1;
				}else {
					spreadRupture(player, to);
					Util.damageEntity((LivingEntity)to, player, DAMAGE);
					((LivingEntity)to).setFireTicks(60);
					((LivingEntity)to).setVelocity(Util.genVec(from.getLocation(), to.getLocation()).normalize().multiply(0.3));
					player.getWorld().playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1f, 1f + new Random().nextFloat() * 0.3f);
					player.getWorld().spawnParticle(Particle.LAVA, to.getLocation(), 3, 0.1, 0.1, 0.1, 0.02, null, true);
					cancel();
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 1);
	}
}
