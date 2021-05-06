package at.smn.mythicalitems.util.mythicalitems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class DualWield extends MythicalEventItemStack{

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
				player.getInventory().setItemInOffHand(new ItemStack(Material.DIAMOND_SWORD));
			}else {
				player.getInventory().setItemInOffHand(null);
			}
		}else if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Util.offHandAnimation(player);
			}
		}			
		return false;
	}

}
