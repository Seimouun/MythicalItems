package at.smn.mythicalitems.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import at.smn.mythicalitems.util.ItemRegistry;
import at.smn.mythicalitems.util.MythicalEventItemStack;

public class ItemWheelCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player)sender;
			Inventory inv = Bukkit.createInventory(null, 36, "Mythical Items");
			System.out.println(ItemRegistry.itemRegistry);
			for(MythicalEventItemStack i : ItemRegistry.itemRegistry) {
				System.out.println(i);
				inv.addItem(i);
			}
			player.openInventory(inv);
		}
		return false;
	}

}
