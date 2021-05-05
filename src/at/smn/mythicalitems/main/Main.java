package at.smn.mythicalitems.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import at.smn.mythicalitems.commands.ItemWheelCommand;
import at.smn.mythicalitems.listeners.InventoryClickListener;
import at.smn.mythicalitems.listeners.ItemInventoryListener;
import at.smn.mythicalitems.listeners.MythicalEventListener;
import at.smn.mythicalitems.util.ItemRegistry;
import at.smn.mythicalitems.util.mythicalitems.TitanicHydra;

public class Main extends JavaPlugin {

	//cool sounds:
	/*
	 * bell.resonate
	 * ender_dragon.hurt
	 * illusioner.mirror_move
	 * illusioner.prepare_blindness
	 * ITEM_ARMOR_EQUIP_CHAIN
	 * knifes: ENTITY_DROWNED_SHOOT
	 * BLOCK_CONDUIT_DEACTIVATE
	 * 881
	 */
	public static Main plugin;
	
	@Override
	public void onEnable() {
		super.onEnable();
		plugin = this;
		getCommand("mythicalitems").setExecutor(new ItemWheelCommand());
		getCommand("mi").setExecutor(new ItemWheelCommand());
		
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new ItemInventoryListener(), this);
		pm.registerEvents(new InventoryClickListener(), this);
		pm.registerEvents(new MythicalEventListener(), this);
		
		ItemRegistry.registerItems();
		MythicalEventListener.registerProtocolListener();
	}
	@Override
	public void onDisable() {
		super.onDisable();
		for(String entry : TitanicHydra.giantList.keySet()) {
			TitanicHydra.killTitan(Bukkit.getPlayer(entry));
		}
	}
	public static Main getPlugin() {
		return plugin;
	}
	
}
