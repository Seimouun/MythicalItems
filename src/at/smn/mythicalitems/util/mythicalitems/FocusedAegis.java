package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class FocusedAegis extends MythicalEventItemStack {

	public static HashMap<String, Long> lastClickHash = new HashMap<>();
	public static HashMap<String, Integer> damageTaken = new HashMap<>();
	private final int DAMAGE_MULT = 3;
	
	public FocusedAegis() {
		super(Material.GOLDEN_SWORD, MythicalItemRarity.DEMONIC, "Focused Aegis");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(!lastClickHash.containsKey(player.getName())) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 100, true));
					player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100000, 128, true));
					new BukkitRunnable() {
						
						@Override
						public void run() {
							if(System.currentTimeMillis() - lastClickHash.get(player.getName()) > 140) {
								focus(player);
								lastClickHash.remove(player.getName());
								damageTaken.remove(player.getName());
								cancel();
							}else {
								Location loc = player.getEyeLocation().add(player.getLocation().getDirection());
								int damage = damageTaken.getOrDefault(player.getName(), 0);
								DustOptions options = new DustOptions(Color.fromRGB(255, 255 - ((damage > 255) ? 255 : damage), 255 - ((damage > 255) ? 255 : damage)), 1f);
								player.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, 0, 0, 0, 0, options, true);
								options = new DustOptions(Color.fromRGB(255, 255 - ((damage > 255) ? 255 : damage), 255 - ((damage > 255) ? 255 : damage)), damage / 50f);
								player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 10, 0, 0.5, 0, 0.2, options, true);
							}
						}
					}.runTaskTimer(Main.getPlugin(), 0, 2);
				}
				lastClickHash.put(player.getName(), System.currentTimeMillis());
			}
			//EntityDamageByEntityEvent
		}else if(obj[0] instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)obj[0];
			Player player = (Player)event.getEntity();
			if(lastClickHash.containsKey(player.getName())) {
				damageTaken.put(player.getName(), damageTaken.getOrDefault(player.getName(), 0) + (int)event.getDamage() * DAMAGE_MULT);
				event.setDamage(0);
				Bukkit.getScheduler().runTaskLater(Main.getPlugin(), () -> player.setVelocity(new Vector()), 1l);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1, 1.5f + new Random().nextFloat() * 0.5f);
			}
		}
		return false;
	}
	public void focus(Player player) {
		player.removePotionEffect(PotionEffectType.SLOW);
		player.removePotionEffect(PotionEffectType.JUMP);
		int damageTaken = FocusedAegis.damageTaken.getOrDefault(player.getName(), 0);
		Vector direction = player.getLocation().getDirection();
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 0.1f + damageTaken / 50, 0.5f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 0.1f + damageTaken / 50, 1.2f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.1f + damageTaken / 50, 1.2f);
		for (double i = 0; i < 10 + damageTaken / 4; i+=0.2) {
			Location loc = player.getEyeLocation().add(direction.clone().multiply(i));
			DustOptions options = new DustOptions(Color.fromRGB(255, 255 - ((damageTaken > 255) ? 255 : damageTaken), 255 - ((damageTaken > 255) ? 255 : damageTaken)), 1 + damageTaken / 50f);
			player.getWorld().spawnParticle(Particle.REDSTONE, loc, damageTaken / 5, damageTaken / 80D, damageTaken / 80D, damageTaken / 80D, 0.1, options, true);
			for(Entity e : loc.getWorld().getNearbyEntities(loc, damageTaken / 80D, damageTaken / 80D, damageTaken / 80D)) {
				if(!e.getUniqueId().equals(player.getUniqueId())) {
					Util.damageEntity((LivingEntity)e, player, damageTaken / 15);
				}
			}
		}
	}

}
