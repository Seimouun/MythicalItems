package at.smn.mythicalitems.util.mythicalitems;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;

public class SeismicWave extends MythicalEventItemStack{
	
	private final int OFFSET = 2;
	private final Material[] MATERIAL_POOL = new Material[] {Material.STONE, Material.DIRT, Material.COARSE_DIRT};
	private final int DAMAGE = 5;
	
	public SeismicWave() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Seismic Wave");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Location loc = player.getEyeLocation().subtract(0,0.5,0);
				loc.setPitch(0);
				Vector dir = loc.getDirection();
				for (int i = OFFSET; i < 25; i++) {
					int counter = i;
					new BukkitRunnable() {
						
						@Override
						public void run() {
							Vector dirTemp = dir.clone();
							Vector vec = new Vector(dirTemp.getZ(), 0, -dirTemp.getX());
							for (int j = -2; j < 2; j++) {
								Location spawnLoc = loc.clone().add(dir.clone().multiply(counter / 1.1)).add(vec.clone().multiply(j));
								if(spawnLoc.getBlock().getType().equals(Material.AIR)) {
									Material mat = spawnLoc.clone().subtract(0,1.5,0).getBlock().getType();
									mat = (mat == Material.AIR || mat == Material.WATER) ? MATERIAL_POOL[new Random().nextInt(MATERIAL_POOL.length)] : mat;
									FallingBlock block = player.getWorld().spawnFallingBlock(spawnLoc, mat.createBlockData());
									block.setVelocity(new Vector(0, 0.3, 0));
									block.setDropItem(false);
									block.setCustomName("seismicwave");
									block.setMetadata("options", new FixedMetadataValue(Main.getPlugin(), "donotspawn"));
									if(j == 0) {
										spawnLoc.getWorld().spawnParticle(Particle.LAVA, spawnLoc, 2, 1, 0.2, 1, 0.1, null, true);
										spawnLoc.getWorld().playSound(spawnLoc, Sound.BLOCK_GRASS_BREAK, 0.6f, 0.5f * new Random().nextFloat());
										for(Entity e : block.getNearbyEntities(1, 1, 1)) {
											if(!e.getName().equals(player.getName()) && !e.getName().equals("seismicwave") && e instanceof LivingEntity) {
												e.setVelocity(new Vector(0,0.5,0));
												Util.damageEntity((LivingEntity)e, player, DAMAGE);
											}
										}
									}
								}
							}
						}
					}.runTaskLater(Main.getPlugin(), (i-OFFSET) * 2);
				}
			}
		}
		return false;
	}

}
