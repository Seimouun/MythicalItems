package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class DualWield extends MythicalEventItemStack{

	public static HashMap<String, Boolean> playerAttackHand = new HashMap<String, Boolean>();
	public static HashMap<String, ItemStack> playerOffhandHash = new HashMap<>();
	
	public DualWield() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.LEGENDARY, "Dual Wield");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerItemHeldEvent) {
			boolean to = (boolean)obj[1];
			PlayerItemHeldEvent event = (PlayerItemHeldEvent)obj[0];
			Player player = event.getPlayer();
			if(to) {
				playerOffhandHash.put(player.getName(), player.getInventory().getItemInOffHand());
				player.getInventory().setItemInOffHand(Util.createMythicalItem(Material.DIAMOND_SWORD, "Dual Repulsor", MythicalItemRarity.LEGENDARY));
			}else {
				player.getInventory().setItemInOffHand(playerOffhandHash.getOrDefault(player.getName(), null));
			}
		}else if(obj[0] instanceof PlayerInteractEntityEvent) {
			PlayerInteractEntityEvent event = (PlayerInteractEntityEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getHand() == EquipmentSlot.HAND && event.getEventName().equals("PlayerInteractEntityEvent")) {
				float damage = 1;
				if(!playerAttackHand.containsKey(player.getName()) || playerAttackHand.get(player.getName())) {
					BlockData data = Material.REDSTONE_BLOCK.createBlockData();
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK, event.getRightClicked().getLocation().add(0, event.getRightClicked().getHeight()/2, 0), 100, 0.2, 1, 0.2, 0.04, data, true);
					damage = 1.5f;
				}
				playerAttackHand.put(player.getName(), true);
				Util.attackEntityOffHand(player, event.getRightClicked(), damage);
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
			
			if(event.getDamager() instanceof Player) {
				Player player = (Player)event.getDamager();
				double damage = 1;
				if(!playerAttackHand.containsKey(player.getName()) || !playerAttackHand.get(player.getName())) {
					BlockData data = Material.REDSTONE_BLOCK.createBlockData();
					player.getWorld().spawnParticle(Particle.BLOCK_CRACK, event.getEntity().getLocation().add(0, event.getEntity().getHeight()/2, 0), 100, 0.2, 0.5, 0.2, 0.04, data, true);
					damage = 1.5;
				}
				event.setDamage(event.getDamage() * damage);
				playerAttackHand.put(player.getName(), true);
			}
		}
		return false;
	}

}
