package at.smn.mythicalitems.util.mythicalitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;

public class UnbreakableWill extends MythicalEventItemStack{

	public static HashMap<String, List<Location>> locationList = new HashMap<>();
	
	public UnbreakableWill() {
		super(Material.DIAMOND_SWORD, MythicalItemRarity.DEMONIC, "Unbreakable Will");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				player.setFallDistance(0);
				generateSphere(player, player.getLocation(), 2, true, new Material[] {Material.STONE, Material.DIRT, Material.GRASS_BLOCK, Material.OBSIDIAN});
				new BukkitRunnable() {
					
					@Override
					public void run() {
						removeSphere(player);
					}
				}.runTaskLater(Main.getPlugin(), 100);
			}
		}
		return false;
	}
	public static void generateSphere(Player player, Location centerBlock, int radius, boolean hollow, Material[] materials) {

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY() + 1;
        int bz = centerBlock.getBlockZ();
        
        List<Location> locList = locationList.getOrDefault(player.getName(), new ArrayList<>());
        
        for(int y = -radius; y <= radius; y++) {
            
            int yTemp = y;
            new BukkitRunnable() {
					
				@Override
				public void run() {
					for(int z = -radius; z <= radius; z++) {
						for(int x = -radius; x <= radius; x++) {
							if((Math.abs(x) == radius && Math.abs(z) == radius) || (Math.abs(yTemp) == radius && (Math.abs(x) == radius || Math.abs(z) == radius))) {
								Location l = new Location(centerBlock.getWorld(), x + bx, yTemp + by, z + bz);
								if(l.getBlock().getType() == Material.WATER) {
									l.getBlock().setType(Material.AIR);
								}
							}else if(Math.abs(x) == radius || Math.abs(z) == radius || Math.abs(yTemp) == radius){
								Location l = new Location(centerBlock.getWorld(), x + bx, yTemp + by, z + bz);
								if(l.getBlock().getType() == Material.AIR || l.getBlock().getType() == Material.GRASS || l.getBlock().getType() == Material.TALL_GRASS || l.getBlock().getType() == Material.WATER) {
									Material mat = materials[new Random().nextInt(materials.length)];
									player.getWorld().playSound(player.getLocation(), mat.createBlockData().getSoundGroup().getPlaceSound(), 1f, 1f);
				                    locList.add(l);
				                    //player.sendMessage("added loc");
				                    l.getBlock().setType(mat);
								}
							}
			            }
					}
				}
			}.runTaskLater(Main.getPlugin(), (y + 1) * 3);
        }
        locationList.put(player.getName(), locList);
    }
	public static void removeSphere(Player player) {
		if(locationList.containsKey(player.getName())) {
			for(Location loc : locationList.get(player.getName())) {
				BlockData data = loc.getBlock().getBlockData();
				loc.getBlock().setType(Material.AIR);
				player.getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 10, 0.5, 0.5, 0.5, 0.1, data, true);
			}
			locationList.remove(player.getName());
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1f, 1f);
		}
	}
	public static boolean containsInLocationList(Location loc) {
		for(List<Location> locList : locationList.values()) {
			if(locList.contains(loc)) {
				return true;
			}
		}
		return false;
	}

}
