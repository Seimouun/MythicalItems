package at.smn.mythicalitems.util.mythicalitems;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;
import net.minecraft.server.v1_16_R3.PacketPlayOutAnimation;

public class TitanicHydra extends MythicalEventItemStack{

	public static HashMap<String, Giant> giantList = new HashMap<>();
	public static HashMap<String, Long> lastHitTime = new HashMap<>();
	
	private final int TITAN_DAMAGE = 15;
	private final long HIT_DELAY = 2000;
	
	public TitanicHydra() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.DEMONIC, "Titanic Hydra");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(player.getHealth() >= 18 && !giantList.containsKey(player.getName())) {
					player.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(), 10, 0, 0, 0, 0, null, true);
					LightningStrike strike = player.getWorld().strikeLightningEffect(player.getLocation());
					strike.setCustomName("titanStrike");
					strike.setFireTicks(0);
					Giant giant = (Giant)player.getWorld().spawnEntity(player.getLocation(), EntityType.GIANT);
					giant.addPassenger(player);
					giantList.put(player.getName(), giant);
					for (int i = 0; i < 9; i++) {
						player.getWorld().spawnParticle(Particle.FLASH, player.getLocation().add(0,i,0), 10, 0, 0, 0, 10, null, true);
					}
					player.getWorld().playSound(player.getLocation(), Sound.ITEM_ARMOR_EQUIP_CHAIN, 1f, 0.5f);
				}
			}else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
				if(giantList.containsKey(player.getName())) {
					if(System.currentTimeMillis() - lastHitTime.getOrDefault(player.getName(), 0L) > HIT_DELAY) {
						Giant giant = giantList.get(player.getName());
						PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftEntity)giant).getHandle(), 0);
						for(Player p : Bukkit.getOnlinePlayers()) {
							((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
						}
						lastHitTime.put(player.getName(), System.currentTimeMillis());
						for(Entity e : giant.getWorld().getNearbyEntities(giant.getLocation().add(player.getLocation().getDirection().multiply(2)), 4, 6, 4)) {
							if(e instanceof LivingEntity && e != giant) {
								e.setVelocity(Util.genVec(giant.getLocation(), e.getLocation()).normalize().multiply(0.5).setY(0.3));
								Util.damageEntity(((LivingEntity)e), player, TITAN_DAMAGE);
							}
						}
					}
				}
			}
		}
		return false;
	}
	public static void killTitan(Player player) {
		if(player != null) {
			Giant giant = TitanicHydra.giantList.get(player.getName());
			giant.removePassenger(player);
			giant.damage(1000);
			giant.getWorld().spawnParticle(Particle.SMOKE_LARGE, giant.getLocation().add(0,giant.getHeight()/2,0).add(0,0,0), 100, 3, giant.getHeight()/2, 3, 1, null, true);
			giant.getWorld().spawnParticle(Particle.CLOUD, giant.getLocation().add(0,giant.getHeight()/2,0).add(0,0,0), 300, 2, giant.getHeight()/2.2, 2, 0.1, null, true);
			player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1f, 0.5f);
			player.getWorld().playSound(player.getLocation(), Sound.ITEM_TOTEM_USE, 0.5f, 1.5f);
			TitanicHydra.giantList.remove(player.getName());
		}
	}

}
