package at.smn.mythicalitems.util.mythicalitems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class FlingShot extends MythicalEventItemStack {

	private final int DAMAGE = 8;
	
	public FlingShot() {
		super(Material.DIAMOND_SWORD, MythicalItemRarity.DEMONIC, "Fling Shot");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)obj[0];
			Player player = (Player)event.getDamager();
			if(player.getPassengers().size() > 0) {
				for(Entity passenger : player.getPassengers()) {
					player.removePassenger(passenger);
					passenger.setVelocity(player.getLocation().getDirection().multiply(1.5));
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1f);
					new BukkitRunnable() {
						
						@Override
						public void run() {
							if(passenger.isOnGround()) {
								Location loc = passenger.getLocation();
								loc.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, loc, 20, 2, 0.2, 2, 0.04, null, true);
								loc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, loc, 20, 2, 0.2, 2, 0.04, null, true);
								BlockData data = loc.clone().subtract(0,1,0).getBlock().getBlockData();
								loc.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 40, 2, 0.2, 2, 0.04, data, true);
						    	loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2f, 0.5f);
						    	for(Entity e : loc.getWorld().getNearbyEntities(loc, 2, 1, 2)) {
									if(e instanceof LivingEntity && !(e instanceof FallingBlock)) {
										if(!e.getUniqueId().equals(player.getUniqueId()) && e != passenger) {
											Util.damageEntity((LivingEntity)e, player, DAMAGE);
										}
										e.setVelocity(new Vector(0,1,0));
									}
								}
								cancel();
							}else {
								passenger.getWorld().spawnParticle(Particle.CLOUD, passenger.getLocation().add(0,passenger.getHeight()/2,0), 5, 0.2, 0.2, 0.2, 0.04, null, true);
							}
						}
					}.runTaskTimer(Main.getPlugin(), 0, 2);
				}
				event.setCancelled(true);
			}
		}else if(obj[0] instanceof PlayerInteractAtEntityEvent) {
			PlayerInteractAtEntityEvent event = (PlayerInteractAtEntityEvent)obj[0];
			Player player = event.getPlayer();
			player.addPassenger(event.getRightClicked());
			player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_LEATHER, 1f, 0.9f);
		}
		return false;
	}

}
