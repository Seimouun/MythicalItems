package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class HailBlade extends MythicalEventItemStack {

	public static HashMap<String, Long> lastClickHash = new HashMap<>();
	
	public HailBlade() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Hail Blade (NOT FINISHED)");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(!lastClickHash.containsKey(player.getName())) {
					new BukkitRunnable() {
						
						@Override
						public void run() {
							System.out.println(System.currentTimeMillis() - lastClickHash.get(player.getName()));
							if(System.currentTimeMillis() - lastClickHash.get(player.getName()) > 140) {
								lastClickHash.remove(player.getName());
								cancel();
							}else {
								Item item = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.IRON_SWORD));
								item.setVelocity(player.getLocation().getDirection().multiply(3));
								new BukkitRunnable() {
									
									@Override
									public void run() {
										if(!Util.containsOnly(Util.getBlocksAtLocation(item.getLocation()), Material.AIR) || item.isOnGround()) {
											item.setVelocity(new Vector());
											item.remove();
											cancel();
										}
									}
								}.runTaskTimer(Main.getPlugin(), 0, 0);
							}
						}
					}.runTaskTimer(Main.getPlugin(), 0, 2);
				}
				lastClickHash.put(player.getName(), System.currentTimeMillis());
			}
		}
		return false;
	}

}
