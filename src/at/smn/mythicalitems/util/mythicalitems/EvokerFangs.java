package at.smn.mythicalitems.util.mythicalitems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;

public class EvokerFangs extends MythicalEventItemStack {

	public EvokerFangs() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.LEGENDARY, "Evoker Fangs");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Location loc = player.getLocation();
				loc.setPitch(0);
				Vector dir = loc.getDirection();
				new BukkitRunnable() {
					
					double d = 1;
					
					@Override
					public void run() {
						if(d < 15) {
							player.getWorld().spawnEntity(loc.clone().add(dir.clone().multiply(d)), EntityType.EVOKER_FANGS);
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
