package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class HailBlade extends MythicalEventItemStack {

	public static HashMap<String, Long> lastClickHash = new HashMap<>();
	public static HashMap<String, HashMap<UUID, Integer>> hailBladeHitCount = new HashMap<>();
	
	public HailBlade() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Hail Blade");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(!lastClickHash.containsKey(player.getName())) {
					hailBladeHitCount.remove(player.getName());
					new BukkitRunnable() {
						
						@Override
						public void run() {
							if(System.currentTimeMillis() - lastClickHash.get(player.getName()) > 150) {
								lastClickHash.remove(player.getName());
								player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 2f, 0.5f);
								player.getWorld().playSound(player.getLocation(), Sound.BLOCK_CONDUIT_DEACTIVATE, 2f, 0.5f + new Random().nextFloat() * 0.1f);
								HashMap<UUID, Integer> map = hailBladeHitCount.getOrDefault(player.getName(), new HashMap<UUID, Integer>());
								for(UUID uuid : map.keySet()) {
									Entity e = Bukkit.getEntity(uuid);
									if(e != null) {
										int damage = map.getOrDefault(uuid, 0) / 2;
										Util.damageEntity((LivingEntity) e, player, damage);
										e.getWorld().spawnParticle(Particle.SOUL, e.getLocation().add(0,e.getHeight()/2,0), 30, 0.05, e.getHeight()/2 - 0.3, 0.05, 0.1, null, true);
										e.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, e.getLocation().add(0,e.getHeight()/2,0), 30, 0.05, e.getHeight()/2 - 0.3, 0.05, 0.1, null, true);
									}
								}
								cancel();
							}else {
								Random random = new Random();
								Vector randomDir = new Vector(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1).multiply(0.5);
								Trident item = (Trident) player.getWorld().spawnEntity(player.getEyeLocation().add(randomDir).add(player.getLocation().getDirection().multiply(1.5)), EntityType.TRIDENT);
								item.setVelocity(player.getLocation().getDirection().multiply(3));
								item.setCustomName("Hail Blade Projectile");
								item.setMetadata("owner", new FixedMetadataValue(Main.getPlugin(), player.getName()));
								player.getWorld().playSound(player.getLocation(), Sound.ENTITY_DROWNED_SHOOT, 1f, 1.7f + new Random().nextFloat() * 0.3f);
							}
						}
					}.runTaskTimer(Main.getPlugin(), 0, 2);
				}
				lastClickHash.put(player.getName(), System.currentTimeMillis());
			}
		}else if(obj[0] instanceof ProjectileHitEvent) {
			ProjectileHitEvent event = (ProjectileHitEvent)obj[0];
			Player player = (Player)obj[1];
			HashMap<UUID, Integer> map = hailBladeHitCount.getOrDefault(player.getName(), new HashMap<UUID, Integer>());
			int hitCountForUUID = map.getOrDefault(event.getHitEntity().getUniqueId(), 0);
			map.put(event.getHitEntity().getUniqueId(), hitCountForUUID + 1);
			hailBladeHitCount.put(player.getName(), map);
		}
		return false;
	}

}
