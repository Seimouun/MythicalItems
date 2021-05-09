package at.smn.mythicalitems.listeners;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import at.smn.mythicalitems.util.ItemRegistry;
import at.smn.mythicalitems.util.MythicalItemStack;

public class ItemInventoryListener implements Listener {

	@EventHandler
	public void onItemTake(InventoryClickEvent event) {
		if(event.getCurrentItem() != null && MythicalItemStack.isMythicalItem(event.getCurrentItem()) && (ItemRegistry.slashNames.contains(MythicalItemStack.stripDisplayName(event.getCurrentItem().getItemMeta().getDisplayName())))) {
			event.setCancelled(true);
			return;
		}
		if(event.getCurrentItem() != null && ItemRegistry.rarityHash.containsKey(event.getCurrentItem().getType())) {
			ItemRegistry.makeItemMythical(event.getCurrentItem());
		}
	}
	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if(event.getItem() != null && event.getItem().getItemStack() != null && ItemRegistry.rarityHash.containsKey(event.getItem().getItemStack().getType())) {
			ItemRegistry.makeItemMythical(event.getItem().getItemStack());
		}
	}
	@EventHandler
	public void onItemPlace(InventoryMoveItemEvent event) {
		System.out.println("move");
		if(event.getItem() != null && ItemRegistry.rarityHash.containsKey(event.getItem().getType())) {
			ItemRegistry.makeItemMythical(event.getItem());
		}
	}
	@EventHandler
	public void itemCraftEvent(PrepareItemCraftEvent event) {
		if(event.getRecipe() != null && event.getRecipe().getResult() != null && ItemRegistry.rarityHash.containsKey(event.getRecipe().getResult().getType())) {
			ItemStack item = event.getRecipe().getResult();
			event.getInventory().setItem(0, new MythicalItemStack(item.getType(), ItemRegistry.rarityHash.get(item.getType())));
		}
	}
	@EventHandler
	public void itemSmithEvent(PrepareSmithingEvent event) {
		if(event.getResult() != null && ItemRegistry.rarityHash.containsKey(event.getResult().getType())) {
			ItemStack item = event.getResult();
			event.setResult(new MythicalItemStack(item.getType(), ItemRegistry.rarityHash.get(item.getType())));
		}
	}
	@EventHandler
	public void onChestOpen(PlayerInteractEvent event) {
		if(event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.CHEST)) {
			Chest chest = (Chest)event.getClickedBlock().getState();
			for(ItemStack stack : chest.getBlockInventory().getContents()) {
				if(stack != null && ItemRegistry.rarityHash.containsKey(stack.getType())) {
					ItemRegistry.makeItemMythical(stack);
				}
			}
		}
	}
	
}
