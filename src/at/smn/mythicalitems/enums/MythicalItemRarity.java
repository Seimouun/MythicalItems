package at.smn.mythicalitems.enums;

import org.bukkit.ChatColor;

public enum MythicalItemRarity {
	
	COMMON(ChatColor.GRAY), UNCOMMON(ChatColor.GREEN), RARE(ChatColor.BLUE), EPIC(ChatColor.DARK_PURPLE), LEGENDARY(ChatColor.GOLD), DEMONIC(ChatColor.DARK_RED);
	
	ChatColor colorCode;
	
	private MythicalItemRarity(ChatColor colorCode) {
		this.colorCode = colorCode;
	}
	public ChatColor getColorCode() {
		return colorCode;
	}
	public char getIcon() {
		return name().charAt(0);
	}
	
}
