package at.smn.mythicalitems.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Giant;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

import at.smn.mythicalitems.main.Main;
import at.smn.mythicalitems.util.MythicalEventItemStack;
import at.smn.mythicalitems.util.Util;
import at.smn.mythicalitems.util.mythicalitems.PerfectExecution;
import at.smn.mythicalitems.util.mythicalitems.TitanicHydra;
import at.smn.mythicalitems.util.mythicalitems.UnbreakableWill;

public class MythicalEventListener implements Listener {
	
	@EventHandler
	public void onProjectileLand(ProjectileHitEvent event) {
		if("Hail Blade Projectile".equals(event.getEntity().getCustomName())) {
			event.getEntity().remove();
		}
	}
	
	@EventHandler
	public void onItemUse(PlayerInteractEvent event) {
		MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(event.getItem());
		if(stack != null) {
			stack.action(event);
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(event.getPlayer().getInventory().getItemInMainHand());
		if(stack != null) {
			stack.action(event);
		}
	}
	@EventHandler
	public void onKillEntity(EntityDeathEvent event) {
		if(event.getEntity().getKiller() != null) {
			MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(event.getEntity().getKiller().getInventory().getItemInMainHand());
			if(stack != null) {
				stack.action(event);
			}
		}
	}
	@EventHandler
	public void onHurtEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof LightningStrike) {
			LightningStrike strike = (LightningStrike) event.getDamager();
			if("titanStrike".equals(strike.getCustomName())) {
				event.setCancelled(true);
			}
		}else if(event.getDamager() instanceof Player) {
			MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(((Player)event.getDamager()).getInventory().getItemInMainHand());
			if(stack != null) {
				stack.action(event);
			}
		}else if(event.getEntity() instanceof Player) {
			MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(((Player)event.getEntity()).getInventory().getItemInMainHand());
			if(stack != null) {
				stack.action(event);
			}
		}
		if(event.getEntity() instanceof Giant) {
			if(TitanicHydra.giantList.containsValue(event.getEntity()) && event.getDamage() >= ((LivingEntity)event.getEntity()).getHealth()) {
				String playerName = (String) Util.getValueInMap(TitanicHydra.giantList, event.getEntity());
				Player player = Bukkit.getPlayer(playerName);
				event.setCancelled(true);
				TitanicHydra.killTitan(player);
			}
		}
	}
	@EventHandler
	public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
		MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(event.getPlayer().getInventory().getItemInMainHand());
		if(stack != null) {
			stack.action(event);
		}
	}
	@EventHandler
    public void onFallingBlockFall(EntityChangeBlockEvent event) {
		if(event.getEntity().getCustomName() != null) {
			List<MetadataValue> dataValues = event.getEntity().getMetadata("options");
			if(dataValues != null && dataValues.get(0) != null && dataValues.get(0).asString().equals("donotspawn")) {
				event.setCancelled(true);
			}
			if(event.getEntity().getCustomName().equals("seismicwave")) {
				Material to = event.getTo(); 
				BlockData data = to.createBlockData();
				Location loc = event.getEntity().getLocation().add(0,0.5,0);
				event.getEntity().getWorld().spawnParticle(Particle.BLOCK_CRACK, loc, 10, 0.5, 0.5, 0.5, 0.1, data, true);
				loc.getWorld().playSound(loc, data.getSoundGroup().getBreakSound(), 0.6f, 0.5f);
			}
			if(event.getEntity().getMetadata("owner").size() > 0) {
				Player player = Bukkit.getPlayer(event.getEntity().getMetadata("owner").get(0).asString());
				if(player != null) {
					MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(player.getInventory().getItemInMainHand());
					if(stack != null) {
						event.setCancelled(true);
						stack.action(event, player);
					}
				}
			}
		}
    }
	@EventHandler
	public void onLightningIgniteBlock(BlockIgniteEvent event) {
		if(event.getCause() == IgniteCause.LIGHTNING) {
			LightningStrike strike = (LightningStrike) event.getIgnitingEntity();
			if("skysplitterstrike".equals(strike.getCustomName())) {
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onSneak(PlayerToggleSneakEvent event) {
		if(TitanicHydra.giantList.containsKey(event.getPlayer().getName())) {
			TitanicHydra.killTitan(event.getPlayer());
		}
	}
	@EventHandler
	public void onItemSwitch(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack currentItem = player.getInventory().getItem(event.getNewSlot());
		MythicalEventItemStack stack = MythicalEventItemStack.getStackFromBukkit(currentItem);
		ItemStack previousItem = player.getInventory().getItem(event.getPreviousSlot());
		MythicalEventItemStack stackFrom = MythicalEventItemStack.getStackFromBukkit(previousItem);
		if(stack != null && stack.getItemName().equals("Perfect Execution")) {
			if(MythicalEventItemStack.holdSelectionItem.size() <= 0) {
				MythicalEventItemStack.holdSelectionItem.add(event.getPlayer());
				PerfectExecution.startGlowUpdate();
			}else {
				MythicalEventItemStack.holdSelectionItem.add(event.getPlayer());
			}
		}else {
			Entity e = MythicalEventItemStack.glowingList.get(player.getName());
			Util.setEntityGlowing(e, player, false);
			MythicalEventItemStack.glowingList.remove(player.getName());
			MythicalEventItemStack.holdSelectionItem.remove(event.getPlayer());
			if(stackFrom != null) {
				stackFrom.action(event, false);
			}
			if(stack != null) {
				stack.action(event, true);
			}
		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if(UnbreakableWill.containsInLocationList(event.getBlock().getLocation())) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onItemTakeOutOfItemStand(PlayerArmorStandManipulateEvent event) {
		if(event.getRightClicked().getCustomName().equals("HailBladeStand")) {
			event.setCancelled(true);
		}
	}
	public static void registerProtocolListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.getPlugin(), ListenerPriority.NORMAL, PacketType.Play.Client.STEER_VEHICLE) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				if(TitanicHydra.giantList.containsKey(event.getPlayer().getName())) {
					Player player = event.getPlayer();
					Giant giant = TitanicHydra.giantList.get(player.getName());
					float leftRight = event.getPacket().getFloat().read(0);
					float forwardBackward = event.getPacket().getFloat().read(1);
					boolean jumping = event.getPacket().getBooleans().read(0);
					Location loc = player.getLocation();
					loc.setPitch(0);
					Vector direction = loc.getDirection().normalize();
					double x = Util.round(direction.getX(), 2);
					double z = Util.round(direction.getZ(), 2);
					double y = (giant.isOnGround() && jumping) ? 1.5 : 0;
					Vector dir = new Vector(z * leftRight + x * forwardBackward, y, x * -leftRight + z * forwardBackward);
					Vector moveDir = new Vector(0,giant.getVelocity().getY(),0).add(dir.multiply(0.5));
					giant.setRotation(player.getLocation().getYaw(), player.getLocation().getPitch());
					giant.setVelocity(moveDir);
				}
			}
		});
	}
	
	public enum MythicalEventType{
		RIGHT_USE, LEFT_USE, KILL;
	}
	
}
