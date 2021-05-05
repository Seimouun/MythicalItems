package at.smn.mythicalitems.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import at.smn.mythicalitems.util.MythicalItemStack;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null) {
			if(event.getView().getTitle().equals("Mythical Items")) {
				MythicalItemStack stack = MythicalItemStack.getStackFromBukkit(event.getCurrentItem());
				event.getWhoClicked().getInventory().addItem(stack);
				event.setCancelled(true);
			}
		}
	}
	
}
