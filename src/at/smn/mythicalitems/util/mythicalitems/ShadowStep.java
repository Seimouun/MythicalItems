package at.smn.mythicalitems.util.mythicalitems;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class ShadowStep extends MythicalEventItemStack {
	
	ArrayList<String> stepList = new ArrayList<String>();
	
	public ShadowStep() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Shadow Step");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			Location loc = player.getLocation();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(!stepList.contains(player.getName())) {
					stepList.add(player.getName());
					float xp = player.getExp();
					int level = player.getLevel();
					new BukkitRunnable() {
						
						int i = 0;
						
						@Override
						public void run() {
							if(i % 5 == 0) {
								player.getWorld().spawnParticle(Particle.SQUID_INK, player.getLocation(), 5, 0.1, 0, 0.1, 0, null, true);
							}
							if(i == 37) {
								player.playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE, 10f, 1f);
							}
							if(i >= 80 || !stepList.contains(player.getName())) {
								player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 1f, 1f);
								player.teleport(loc);
								player.getWorld().spawnParticle(Particle.FLASH, loc, 20, 0.1, 0.6, 0.1, 0, null, true);
								player.setExp(xp);
								player.setLevel(level);
								if(!stepList.contains(player.getName())) {
									player.stopSound(Sound.ENTITY_ILLUSIONER_MIRROR_MOVE);
									player.getWorld().playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_HURT, 1f, 1.5f);
								}
								player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 2f, 1f);
								stepList.remove(player.getName());
								cancel();
							}else {
								Util.setExpBar(player, i, 1, 80);
								player.getWorld().spawnParticle(Particle.SQUID_INK, loc, 20, 0.1, 0.6, 0.1, 0, null, true);
							}
							i++;
						}
					}.runTaskTimer(Main.getPlugin(), 0, 1);
				}else {
					stepList.remove(player.getName());
				}
			}
		}
		return false;
	}

}
