package at.smn.mythicalitems.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import at.smn.mythicalitems.enums.MythicalItemRarity;

public class MythicalItemStack extends ItemStack {

	public MythicalItemRarity rarity;
	public String name = null;
	public int durability = -1;
	
	public MythicalItemStack(Material mat, MythicalItemRarity rarity) {
		super(mat);
		this.rarity = rarity;
	}
	public MythicalItemStack(Material mat, MythicalItemRarity rarity, String name) {
		super(mat);
		this.rarity = rarity;
		this.name = name;
	}
	public MythicalItemStack(Material mat, MythicalItemRarity rarity, String name, int durability) {
		super(mat);
		this.rarity = rarity;
		this.name = name;
		this.durability = durability;
	}
	public MythicalItemStack(Material mat, MythicalItemRarity rarity, ItemMeta meta) {
		super(mat);
		setItemMeta(meta);
		this.rarity = rarity;
	}
	public MythicalItemRarity getRarity() {
		return rarity;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof MythicalItemStack) {
			MythicalItemStack other = (MythicalItemStack)obj;
			boolean eq = getType().equals(other.getType()) && getName().equals(other.getName()) && getRarity().equals(other.getRarity());
			return eq;
		}
		return false;
	}
	public String getName() {
		String itemString = WordUtils.capitalizeFully(getType().name().replace("_", " "));
		return itemString;
	}
	@Override
	public ItemMeta getItemMeta() {
		ItemMeta im = super.getItemMeta();
		if(rarity != null) {
			List<String> lore = new ArrayList<>();
			MythicalItemRarity rarity = getRarity();
			lore.add("§7Rarity§f: " + rarity.getColorCode() + rarity.name());
			im.setLore(lore);
			im.setDisplayName("§r§7[" + rarity.getColorCode() + rarity.getIcon() + "§7] §r" + ((this.name == null) ? getName() : name));
			setItemMeta(im);
		}
		return im;
	}
	public static MythicalItemStack getStackFromBukkit(ItemStack is) {
		if(is == null || is.getItemMeta() == null || is.getItemMeta().getLore() == null || is.getItemMeta().getDisplayName() == null)
			return null;
		String name = is.getItemMeta().getDisplayName();
		MythicalItemRarity rarity = MythicalItemRarity.valueOf(ChatColor.stripColor(is.getItemMeta().getLore().get(0).replace("§7Rarity§f: ", "")));
		for(MythicalItemStack stack : ItemRegistry.itemRegistry) {
			if(stack.getRarity().equals(rarity) && name.equals(stack.getItemMeta().getDisplayName())) {
				return stack;
			}
		}
		return null;
	}
}
