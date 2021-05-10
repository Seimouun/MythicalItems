package at.smn.mythicalitems.util.mythicalitems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class DemonicFangs extends MythicalEventItemStack{

	public DemonicFangs() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Demonic Fangs");
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean getGlowing() {
		return true;
	}
	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Entity target = glowingList.get(player.getName());
				if(target == null)
					return false;
				Location origin = player.getLocation();
				new BukkitRunnable() {
					
					double d = 1;
					
					@Override
					public void run() {
						Location loc = origin.clone().add(Util.genVec(origin, target.getLocation()).multiply(d));
						if(loc.distance(target.getLocation()) > 0.5) {
							EvokerFangs fangs = (EvokerFangs)player.getWorld().spawnEntity(loc, EntityType.EVOKER_FANGS);
							fangs.setCustomName("demonic fang");
							
							d += 1;
						}else {
							cancel();
						}
					}
				}.runTaskTimer(Main.getPlugin(), 0, 2);
			}
		}
		return false;
	}

}
