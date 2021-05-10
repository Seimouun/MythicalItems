package at.smn.mythicalitems.util.mythicalitems;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.util.MythicalEventItemStack;

public class ShulkerShot extends MythicalEventItemStack {

	public ShulkerShot() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.LEGENDARY, "Shulker Shot");
	}

	@Override
	public boolean action(Object... obj) {
		if(obj[0] instanceof PlayerInteractEvent) {
			PlayerInteractEvent event = (PlayerInteractEvent)obj[0];
			Player player = event.getPlayer();
			if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SHULKER_SHOOT, 2f, 1f + new Random().nextFloat() * 0.2f);
				ShulkerBullet bullet = (ShulkerBullet) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SHULKER_BULLET);
				bullet.setTarget(glowingList.get(player.getName()));
			}
		}
		return false;
	}
	@Override
	public boolean getGlowing() {
		return true;
	}

}
