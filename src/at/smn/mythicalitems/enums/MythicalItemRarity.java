package at.smn.mythicalitems.enums;

public enum MythicalItemRarity {
	
	COMMON("§7"), UNCOMMON("§a"), RARE("§9"), EPIC("§5"), LEGENDARY("§6"), DEMONIC("§4");
	
	String colorCode;
	
	private MythicalItemRarity(String colorCode) {
		this.colorCode = colorCode;
	}
	public String getColorCode() {
		return colorCode;
	}
	public char getIcon() {
		return name().charAt(0);
	}
	
}
