package at.smn.mythicalitems.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;

public abstract class MythicalEventItemStack extends MythicalItemStack implements IMythicalEvent {

	public static List<Player> holdSelectionItem = new ArrayList<>();
	public static HashMap<String, Entity> glowingList = new HashMap<>();
	
	public MythicalEventItemStack(Material mat, MythicalItemRarity rarity) {
		super(mat, rarity);
	}
	public MythicalEventItemStack(Material mat, MythicalItemRarity rarity, String name) {
		super(mat, rarity, name);
	}
	public MythicalEventItemStack(Material mat, MythicalItemRarity rarity, String name, int durability) {
		super(mat, rarity, name, durability);
	}

	public abstract boolean action(Object... obj);
	
	public static MythicalEventItemStack getStackFromBukkit(ItemStack is) {
		if(is == null || is.getItemMeta() == null || is.getItemMeta().getLore() == null || is.getItemMeta().getDisplayName() == null)
			return null;
		String name = is.getItemMeta().getDisplayName();
		MythicalItemRarity rarity = MythicalItemRarity.valueOf(ChatColor.stripColor(is.getItemMeta().getLore().get(0).replace(ChatColor.GRAY + "Rarity" + ChatColor.WHITE + ": ", "")));
		for(MythicalEventItemStack stack : ItemRegistry.itemRegistry) {
			if(stack.getRarity().equals(rarity) && name.equals(stack.getItemMeta().getDisplayName())) {
				return stack;
			}
		}
		return null;
	}
	public String getItemName() {
		return this.name;
	}
	public static void startGlowUpdate() {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if(holdSelectionItem.size() > 0) {
					for(Player player : holdSelectionItem) {
						RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(), player.getLocation().getDirection(), 20D, 0.4D, (e) -> {return !(e.equals(player) || e instanceof Item || e.isDead());});
						Entity hitEntity = (result == null) ? null : result.getHitEntity();
						Entity previousEntity = glowingList.getOrDefault(player.getName(), null);
						if(previousEntity == null || hitEntity == null || previousEntity.getUniqueId() != hitEntity.getUniqueId()) {
							if(previousEntity != null) {
								Util.setEntityGlowing(previousEntity, player, false);
								glowingList.remove(player.getName());
							}
							if(hitEntity != null) {
								Util.setEntityGlowing(hitEntity, player, true);
								glowingList.put(player.getName(), hitEntity);
							}
						}
					}
				}else {
					cancel();
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 1);
	}
}
