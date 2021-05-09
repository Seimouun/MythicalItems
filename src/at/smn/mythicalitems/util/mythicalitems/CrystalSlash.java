package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.util.ItemRegistry;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.MythicalItemStack;
import at.smn.mythicalitems.util.Util;

public class CrystalSlash extends MythicalEventItemStack {

	public static HashMap<String, Boolean> playerAttackHand = new HashMap<String, Boolean>();
	public static HashMap<String, ItemStack> playerOffhandHash = new HashMap<>();
	
	private static final int damage = 15;
	
	public CrystalSlash() {
		super(Material.DIAMOND_SWORD, MythicalItemRarity.DEMONIC, "Crystal Slash");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerItemHeldEvent) {
			boolean to = (boolean)obj[1];
			PlayerItemHeldEvent event = (PlayerItemHeldEvent)obj[0];
			Player player = event.getPlayer();
			if(to) {
				ItemStack inHand = player.getInventory().getItemInOffHand();
				if(inHand == null || inHand.getItemMeta() == null || !ItemRegistry.slashNames.contains(MythicalItemStack.stripDisplayName(inHand.getItemMeta().getDisplayName()))) {
					playerOffhandHash.put(player.getName(), player.getInventory().getItemInOffHand());
				}
				player.getInventory().setItemInOffHand(Util.createMythicalItem(Material.DIAMOND_SWORD, "Dual Slash", MythicalItemRarity.DEMONIC));
			}else {
				player.getInventory().setItemInOffHand(playerOffhandHash.getOrDefault(player.getName(), null));
				playerOffhandHash.remove(player.getName());
			}
		}else if(obj[0] instanceof PlayerInteractEntityEvent) {
			PlayerInteractEntityEvent event = (PlayerInteractEntityEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getHand() == EquipmentSlot.HAND && event.getEventName().equals("PlayerInteractEntityEvent")) {
				if(!playerAttackHand.containsKey(player.getName()) || playerAttackHand.get(player.getName())) {
					slash(player);
				}
				playerAttackHand.put(player.getName(), true);
				Util.attackEntityOffHand(player, event.getRightClicked(), 1);
				Util.offHandAnimation(player);
				playerAttackHand.put(player.getName(), false);
			}
		}else if(obj[0] instanceof PlayerInteractEvent) { 
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Util.offHandAnimation(player);
			}
		}else if(obj[0] instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)obj[0];
			if(event.getCause() == DamageCause.ENTITY_ATTACK) {
				if(event.getDamager() instanceof Player) {
					Player player = (Player)event.getDamager();
					if(!playerAttackHand.containsKey(player.getName()) || !playerAttackHand.get(player.getName())) {
						slash(player);
					}
					playerAttackHand.put(player.getName(), true);
				}
			}
		}
		return false;
	}
	public void slash(Player player) {
		DustOptions options = new DustOptions(Color.AQUA, 1f);
		for(float d = 0; d < 3.1415 * 2; d+=0.1) {
			double x = Math.sin(d) * 3;
			double z = Math.cos(d) * 3;
			  
			player.getWorld().spawnParticle(Particle.REDSTONE, player.getLocation().add(x,1,z), 1, 0, 0, 0, 0.04, options, false);
		}
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1.8f);
		player.getWorld().playSound(player.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_1, 1f, 1.8f + new Random().nextFloat() * 0.2f);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_HURT, 1f, 1.3f + new Random().nextFloat() * 0.2f);
		for(Entity e : player.getNearbyEntities(4, 4, 4)) {
			double distance = e.getLocation().distance(player.getLocation());
			if(distance <= 4) {
				if(e instanceof LivingEntity) {
					Util.damageEntity((LivingEntity)e, player, damage);
					e.setVelocity(Util.genVec(player.getLocation(), e.getLocation()).multiply(1.2).setY(0.3));
					((LivingEntity)e).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 2));
				}
			}
		}
	}
}
