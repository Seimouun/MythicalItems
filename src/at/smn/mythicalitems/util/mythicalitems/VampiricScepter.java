package at.smn.mythicalitems.util.mythicalitems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;

public class VampiricScepter extends MythicalEventItemStack {

	public static List<String> playerInvisibleList = new ArrayList<>(); 
	private final int INVIS_TIME = 60;
	
	public VampiricScepter() {
		super(Material.IRON_SWORD, MythicalItemRarity.DEMONIC, "Vampiric Scepter");
	}
	
	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				unHidePlayer(player);
			}
		}else if(obj[0] instanceof EntityDeathEvent) {
			EntityDeathEvent event = (EntityDeathEvent)obj[0];
			Player player = event.getEntity().getKiller();
			
			hidePlayer(player);
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					unHidePlayer(player);
				}
			}.runTaskLater(Main.getPlugin(), INVIS_TIME);
		}else if(obj[0] instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)obj[0];
			if(playerInvisibleList.contains(event.getDamager().getName())) {
				unHidePlayer((Player)event.getDamager());
			}
		}
		return true;
	}
	private void hidePlayer(Player player) {
		if(!playerInvisibleList.contains(player.getName())) {
			playerInvisibleList.add(player.getName());
			DustOptions opt = new DustOptions(Color.ORANGE, 4);
			player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation(), 50, 0.5, 1, 0.5, 1, opt, true);
			player.getWorld().spawnParticle(Particle.SQUID_INK, player.getLocation(), 50, 0.5, 1, 0.5, 0.1, null, true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, INVIS_TIME + 10, 2, true));
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, INVIS_TIME + 10, 1, true));
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, INVIS_TIME + 10, 4, true));
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.6f, 0.5f);
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.hidePlayer(Main.getPlugin(), player);
			}
		}
	}
	private void unHidePlayer(Player player) {
		System.out.println("unhide player");
		if(playerInvisibleList.contains(player.getName())) {
			playerInvisibleList.remove(player.getName());
			player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 50, 0.5, 1, 0.5, 0, null, true);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.removePotionEffect(PotionEffectType.SPEED);
			player.removePotionEffect(PotionEffectType.REGENERATION);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.4f, 0.8f);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1f, 0.5f);
			for(Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(Main.getPlugin(), player);
			}
		}
	}

}
