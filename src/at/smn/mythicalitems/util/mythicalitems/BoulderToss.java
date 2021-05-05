package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class BoulderToss extends MythicalEventItemStack {
	
	private final Material[] MATERIAL_POOL = new Material[] {Material.STONE, Material.DIRT, Material.COBBLESTONE};
	private final int DAMAGE = 8;
	private static HashMap<String, Set<FallingBlock>> fallingBlockList = new HashMap<String, Set<FallingBlock>>();
	
	public BoulderToss() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Boulder Toss");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Location lookingBlock = Util.getTargetBlock(player, 50);
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 0.6f, 1f);
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_EXTEND, 0.6f, 0.3f);
				int count = 0;
				fallingBlockList.put(player.getName(), Sets.newHashSet());
				for(int x = -2; x < 3; x++) {
					for(int y = -2; y < 3; y++) {
						for(int z = 0; z <= 1; z++) {
							if(Math.abs(x) == 2 && Math.abs(y) == 2) {
								break;
							}
							int random = new Random().nextInt(MATERIAL_POOL.length);
							Material mat = MATERIAL_POOL[random];
							FallingBlock block = player.getWorld().spawnFallingBlock(player.getLocation().add(x,4 - z,y), mat.createBlockData());
							if(random == 2)
								player.getWorld().spawnParticle(Particle.LAVA, player.getLocation().add(x,4 - z,y), 10, 1, 1, 1, 0.1, null, true);
							Vector dir = Util.genVec(block.getLocation(), lookingBlock);
							block.setVelocity(new Vector(0, 0.1, 0));
							block.setGravity(false);
							block.setCustomName(player.getName());
							block.setMetadata("owner", new FixedMetadataValue(Main.getPlugin(), player.getName()));
							block.setMetadata("options", new FixedMetadataValue(Main.getPlugin(), "donotspawn"));
							block.setDropItem(false);
							fallingBlockList.get(player.getName()).add(block);
							
							boolean first = (count == 0);
							count++;
							new BukkitRunnable() {
								@Override
								public void run() {
									if(first) {
										block.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 0.5f);
										block.getWorld().playSound(player.getLocation(), Sound.BLOCK_PISTON_CONTRACT, 0.6f, 0.3f);
										new BukkitRunnable() {
											
											@Override
											public void run() {
												if(!Util.containsOnly(Util.getBlocksAtLocation(block.getLocation()), Material.AIR) || block.getVelocity().length() < 2) {
													explodeBoulder(block.getLocation(), block, player);
													cancel();
												}
											}
										}.runTaskTimer(Main.getPlugin(), 0, 1);
									}
									block.setVelocity(dir.clone().multiply(3));
								}
							}.runTaskLater(Main.getPlugin(), 15);
						}
					}
				}
			}
		}else if(obj[0] instanceof EntityChangeBlockEvent) {
			EntityChangeBlockEvent event = (EntityChangeBlockEvent)obj[0];
			Player player = (Player)obj[1];
			explodeBoulder(event.getEntity().getLocation(), event.getEntity(), player);
		}
		return true;
	}
	public void explodeBoulder(Location loc, Entity entity, Player player) {
		loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 40, 2, 2, 2, 0.1, null, true);
		loc.getWorld().spawnParticle(Particle.LAVA, loc, 10, 2, 2, 2, 0.1, null, true);
		loc.getWorld().playSound(loc, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 4f, 0.5f);
		for(Entity e : entity.getNearbyEntities(3, 3, 3)) {
			if(e instanceof LivingEntity && !(e instanceof FallingBlock)) {
				if(!e.getUniqueId().equals(player.getUniqueId())) {
					Util.damageEntity((LivingEntity)e, player, DAMAGE);
				}
			}
		}
		for(FallingBlock block : fallingBlockList.get(entity.getCustomName())) {
			block.remove();
		}
	}
	

}
