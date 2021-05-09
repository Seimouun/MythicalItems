package at.smn.mythicalitems.util.mythicalitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
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

public class UnstableMatter extends MythicalEventItemStack{

	public static HashMap<String, Long> lastClickHash = new HashMap<>();
	public static List<String> placedFailed = new ArrayList<>();
	public static HashMap<String, Integer> matterAmount = new HashMap<>();
	public static HashMap<String, List<Location>> locationPlacedList = new HashMap<>();
	
	private final DustOptions OPTIONS = new DustOptions(Color.BLACK, 0.4f);
	private final Particle PARTICLE = Particle.REDSTONE;
	private final int MAX_LENGTH = 30;
	private final int DAMAGE = 12;
	
	public UnstableMatter() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Unstable Matter");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(!lastClickHash.containsKey(player.getName())) {
					matterAmount.put(player.getName(), 3);
					new BukkitRunnable() {
						
						double i = 0;
						
						@Override
						public void run() {
							System.out.println(System.currentTimeMillis() - lastClickHash.get(player.getName()));
							if(System.currentTimeMillis() - lastClickHash.get(player.getName()) > 205) {
								new BukkitRunnable() {
									
									@Override
									public void run() {
										int size = locationPlacedList.getOrDefault(player.getName(), new ArrayList<>()).size();
										
										if(size > 0) {
											implodeLocations(player);
											locationPlacedList.remove(player.getName());
											cancel();
										}
										if(placedFailed.contains(player.getName()) || matterAmount.getOrDefault(player.getName(), 3) == 3) {
											placedFailed.remove(player.getName());
											cancel();
										}
									}
								}.runTaskTimer(Main.getPlugin(), 0, 1);
								lastClickHash.remove(player.getName());
								cancel();
							}else {
								for (int j = 0; j < 3; j++) {
									double x = Math.sin(i);
									double z = Math.cos(i);
									
									int matter = matterAmount.get(player.getName());
									
									Vector partVec = Util.rotateVectorAroundZ(new Vector(x, 0, z), 45);
									if(matter >= 1) {
										player.getWorld().spawnParticle(PARTICLE, player.getLocation().add(partVec).add(0,1,0), 10, 0, 0, 0, 0, OPTIONS, true);
									}
									if(matter >= 2) {
										partVec = Util.rotateVectorAroundY(Util.rotateVectorAroundZ(new Vector(x, 0, z), -45), -90);
										player.getWorld().spawnParticle(PARTICLE, player.getLocation().add(partVec).add(0,1,0), 10, 0, 0, 0, 0, OPTIONS, true);
									}
									if(matter >= 3) {
										partVec = Util.rotateVectorAroundZ(Util.rotateVectorAroundY(new Vector(x, 0, z), 90), -60);
										player.getWorld().spawnParticle(PARTICLE, player.getLocation().add(partVec).add(0,1,0), 10, 0, 0, 0, 0, OPTIONS, true);
									}
									i+=0.1;
								}
								for(Location loc : locationPlacedList.getOrDefault(player.getName(), new ArrayList<>())) {
									player.getWorld().spawnParticle(PARTICLE, loc, 2, 0.1, 0.1, 0.1, 0, new DustOptions(Color.BLACK, 1.5f), true);
								}
							}
						}
					}.runTaskTimer(Main.getPlugin(), 0, 1);
				}
				lastClickHash.put(player.getName(), System.currentTimeMillis());
			}else if(lastClickHash.containsKey(player.getName())) {
				int matter = matterAmount.getOrDefault(player.getName(), 0);
				matterAmount.put(player.getName(), matter - 1);
				if(matter > 0) {
					Vector lookDir = player.getLocation().getDirection();
					Location loc = player.getEyeLocation();
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1f, 1.8f + new Random().nextFloat() * 0.2f);
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1f, 1.8f + new Random().nextFloat() * 0.2f);
					new BukkitRunnable() {
						
						double length = 0;
						
						@Override
						public void run() {
							if(length < MAX_LENGTH) {
								for (int j = 0; j < 15; j++) {
									Location locTemp = loc.clone().add(lookDir.clone().multiply(length));
									player.getWorld().spawnParticle(PARTICLE, locTemp, 10, 0, 0, 0, 0, OPTIONS, true);
									if(locTemp.getBlock().getType() != Material.AIR) {
										List<Location> locList = locationPlacedList.getOrDefault(player.getName(), new ArrayList<>());
										locList.add(locTemp);
										locationPlacedList.put(player.getName(), locList);
										placedFailed.remove(player.getName());
										cancel();
										break;
									}
									length+=0.2;
								}
							}else {
								placedFailed.add(player.getName());
								cancel();
							}
						}
					}.runTaskTimer(Main.getPlugin(), 0, 1);
				}
			}
			//EntityDamageByEntityEvent
		}
		return false;
	}
	public void implodeLocations(Player player) {
		for(Location loc : locationPlacedList.getOrDefault(player.getName(), new ArrayList<>())) {
			loc.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 2.5f, 1f + new Random().nextFloat() * 0.1f);
			new BukkitRunnable() {
				
				double i = 7;
				
				@Override
				public void run() {
					if(i > 0) {
						for (int j = 0; j < 4; j++) {
							double x = Math.sin(i) * i;
							double z = Math.cos(i) * i;
							Vector partVec = new Vector(x, 0, z);
							loc.getWorld().spawnParticle(PARTICLE, loc.clone().add(partVec).add(0,0.1,0), 10, 0, 0, 0, 0, new DustOptions(Color.BLACK, 1.5f), true);
							double x2 = Math.sin(i) * i;
							double z2 = Math.cos(i) * i;
							Vector partVec2 = new Vector(-x2, 0, -z2);
							loc.getWorld().spawnParticle(PARTICLE, loc.clone().add(partVec2).add(0,0.1,0), 10, 0, 0, 0, 0, new DustOptions(Color.BLACK, 1.5f), true);
							i-=0.1;
						}
					}else {
						loc.getWorld().playSound(loc, Sound.BLOCK_END_PORTAL_SPAWN, 2.5f, 1.6f + new Random().nextFloat() * 0.1f);
						loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2f, 1f + new Random().nextFloat() * 0.1f);
						loc.getWorld().spawnParticle(PARTICLE, loc.clone().add(0,0.1,0), 30, 2, 0, 2, 1, new DustOptions(Color.BLACK, 1.5f), true);
						loc.getWorld().spawnParticle(Particle.SQUID_INK, loc.clone().add(0,0.1,0), 100, 0, 0, 0, 1, null, true);
						loc.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc.clone().add(0,0.1,0), 100, 0, 0, 0, 1, null, true);
						loc.getWorld().spawnParticle(Particle.PORTAL, loc.clone().add(0,0.1,0), 100, 0, 0, 0, 1, null, true);
						for(Entity e : loc.getWorld().getNearbyEntities(loc, 6, 2, 6)) {
							e.setVelocity(Util.genVec(e.getLocation(), loc).setY(0.3));
							if(e instanceof LivingEntity) {
								Util.damageEntity(((LivingEntity)e), player, DAMAGE);
							}
						}
						cancel();
					}
					if(Util.floatingNumbersEqual(i, 1.4, 0.001)){
						loc.getWorld().playSound(loc, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 2.5f, 1.6f + new Random().nextFloat() * 0.1f);
					}
				}
			}.runTaskTimer(Main.getPlugin(), 0, 1);
		}
	}

}
