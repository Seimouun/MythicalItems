package at.smn.mythicalitems.util.mythicalitems;

import org.bukkit.Material;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.util.MythicalEventItemStack;

public class DualWield extends MythicalEventItemStack{

	public DualWield() {
		super(Material.NETHERITE_SWORD, MythicalItemRarity.LEGENDARY, "Dual Wield");
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean action(Object... obj) {
		// TODO Auto-generated method stub
		return false;
	}

}
