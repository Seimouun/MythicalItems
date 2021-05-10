package at.smn.mythicalitems.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import at.smn.mythicalitems.util.mythicalitems.BoulderToss;
import at.smn.mythicalitems.util.mythicalitems.CrystalSlash;
import at.smn.mythicalitems.util.mythicalitems.DemonicFangs;
import at.smn.mythicalitems.util.mythicalitems.DualWield;
import at.smn.mythicalitems.util.mythicalitems.EvokerFangs;
import at.smn.mythicalitems.util.mythicalitems.FlingShot;
import at.smn.mythicalitems.util.mythicalitems.FocusedAegis;
import at.smn.mythicalitems.util.mythicalitems.GoldenAegis;
import at.smn.mythicalitems.util.mythicalitems.HailBlade;
import at.smn.mythicalitems.util.mythicalitems.PerfectExecution;
import at.smn.mythicalitems.util.mythicalitems.SeismicWave;
import at.smn.mythicalitems.util.mythicalitems.ShadowStep;
import at.smn.mythicalitems.util.mythicalitems.ShulkerShot;
import at.smn.mythicalitems.util.mythicalitems.SkySplitter;
import at.smn.mythicalitems.util.mythicalitems.TitanicHydra;
import at.smn.mythicalitems.util.mythicalitems.UnbreakableWill;
import at.smn.mythicalitems.util.mythicalitems.UnstableMatter;
import at.smn.mythicalitems.util.mythicalitems.VampiricScepter;
import at.smn.mythicalitems.util.mythicalitems.VolcanicRupture;

public class ItemRegistry {
	
	public static HashMap<Material, MythicalItemRarity> rarityHash = new HashMap<Material, MythicalItemRarity>();

	public static ArrayList<MythicalEventItemStack> itemRegistry = new ArrayList<>();
	
	public static List<String> slashNames = Arrays.asList(new String[] {"Dual Slash", "Dual Repulsor"});
	
	public static void registerItems() {
		rarityHash.put(Material.WOODEN_SWORD, MythicalItemRarity.COMMON);
		rarityHash.put(Material.STONE_SWORD, MythicalItemRarity.COMMON);
		rarityHash.put(Material.GOLDEN_SWORD, MythicalItemRarity.COMMON);
		rarityHash.put(Material.IRON_SWORD, MythicalItemRarity.UNCOMMON);
		rarityHash.put(Material.DIAMOND_SWORD, MythicalItemRarity.UNCOMMON);
		rarityHash.put(Material.NETHERITE_SWORD, MythicalItemRarity.RARE);
		itemRegistry.add(new VampiricScepter());
		itemRegistry.add(new BoulderToss());
		itemRegistry.add(new SeismicWave());
		itemRegistry.add(new GoldenAegis());
		itemRegistry.add(new FocusedAegis());
		itemRegistry.add(new TitanicHydra());
		itemRegistry.add(new UnbreakableWill());
		itemRegistry.add(new ShadowStep());
		itemRegistry.add(new FlingShot());
		itemRegistry.add(new PerfectExecution());
		itemRegistry.add(new SkySplitter());
		itemRegistry.add(new UnstableMatter());
		itemRegistry.add(new HailBlade());
		itemRegistry.add(new DualWield());
		itemRegistry.add(new CrystalSlash());
		itemRegistry.add(new VolcanicRupture());
		itemRegistry.add(new EvokerFangs());
		itemRegistry.add(new DemonicFangs());
		itemRegistry.add(new ShulkerShot());
	}
	public static MythicalItemStack makeItemMythical(ItemStack stack) {
		if(MythicalItemStack.getStackFromBukkit(stack) == null) {
			MythicalItemStack is = new MythicalItemStack(stack.getType(), rarityHash.get(stack.getType()), stack.getItemMeta());
			stack.setItemMeta(is.getItemMeta());
			return is;
		}
		return null;
	}
	
}
