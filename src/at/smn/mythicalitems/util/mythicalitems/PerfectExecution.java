package at.smn.mythicalitems.util.mythicalitems;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class PerfectExecution extends MythicalEventItemStack {

	private final int DAMAGE = 20;
	
	public PerfectExecution() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Perfect Execution");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(glowingList.containsKey(player.getName())) {
					Entity e = glowingList.get(player.getName());
					if (e != null) {
						if (e instanceof LivingEntity) {
							GameMode mode = player.getGameMode();
							player.setGameMode(GameMode.SPECTATOR);
							Vector vec = Util.genVec(player.getLocation(), e.getLocation()).normalize();
							player.setVelocity(vec.multiply(4).add(new Vector(0, 0.3, 0)));
							player.setFallDistance(0);
							player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1.8f);
							player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST_FAR, 1, 1.3f);
							player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 0.7f, 1.5f);
							player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 0.5f, 2f);
							new BukkitRunnable() {
								int timer = 0;
								boolean damaged = false;
								
								@Override
								public void run() {
									boolean canContinue = !damaged || player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(2)).getBlock().getType().equals(Material.AIR);
									if (timer < 3 && canContinue) {
										player.getWorld().spawnParticle(Particle.SQUID_INK, player.getLocation().add(0, 1, 0), 10, 0.2, 1, 0.2, 0.01);
										Location playerLoc = player.getLocation();
										Location entityLoc = e.getLocation();
										playerLoc.setY(0);
										entityLoc.setY(0);
										double dist = playerLoc.distance(entityLoc);
										if (dist < 5 && !damaged) {
											Util.damageEntity(((LivingEntity) e), player, DAMAGE);
											player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.1f, 1.4f);
											player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.2f, 0.8f);
											player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_ATTACK_SWEEP, 0.8f, 1.1f);
											damaged = true;
										}
										timer++;
									} else {
										player.setGameMode(mode);
										player.setFlying(false);
										player.setVelocity(vec.multiply(0.2));
										cancel();
									}
								}
							}.runTaskTimer(Main.getPlugin(), 0, 1);
						}
					}
				}
			}
		}
		return false;
	}
	@Override
	public boolean getGlowing() {
		return true;
	}

}
