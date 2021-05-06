package at.smn.mythicalitems.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;
import com.google.common.collect.Sets;

import at.smn.mythicalitems.enums.MythicalItemRarity;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.DataWatcher;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumHand;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.NBTTagInt;
import net.minecraft.server.v1_16_R3.NBTTagList;
import net.minecraft.server.v1_16_R3.NBTTagString;
import net.minecraft.server.v1_16_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class Util {

	public static ItemStack createItem(Material mat, String itemName) {
		ItemStack is = new ItemStack(mat);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(itemName);
		is.setItemMeta(im);
		return is;
	}
	public static MythicalItemStack createMythicalItem(Material mat, String itemName, MythicalItemRarity rarity) {
		MythicalItemStack is = new MythicalItemStack(mat, rarity);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GRAY + "[" + rarity.getColorCode() + rarity.getIcon() + ChatColor.GRAY + "] " + ChatColor.RESET + itemName);
		is.setItemMeta(im);
		return is;
	}
	public static MythicalItemStack createMythicalItem(Material mat, String itemName, MythicalItemRarity rarity, int durability) {
		MythicalItemStack is = new MythicalItemStack(mat, rarity);
		net.minecraft.server.v1_16_R3.ItemStack item = CraftItemStack.asNMSCopy(is);
        NBTTagCompound nbt = (item.hasTag() ? item.getTag() : new NBTTagCompound());
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound aS = new NBTTagCompound();
        aS.set("AttributeName", NBTTagString.a("genertic.attackDamage"));
        aS.set("Name", NBTTagString.a("genertic.attackDamage"));
        aS.set("Amount", NBTTagInt.a(durability));
        aS.set("Operation", NBTTagInt.a(0));
        aS.set("UUIDLeast", NBTTagInt.a(894654));
        aS.set("UUIDMost", NBTTagInt.a(2872));
        modifiers.add(aS);
        nbt.set("AttributeModifiers", modifiers);
        item.setTag(nbt);
        is = ItemRegistry.makeItemMythical(CraftItemStack.asBukkitCopy(item));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GRAY + "[" + rarity.getColorCode() + rarity.getIcon() + ChatColor.GRAY + "] " + ChatColor.RESET + itemName);
		is.setItemMeta(im);
		return is;
	}
    public static Vector genVec(Location a, Location b) {
        double dX = a.getX() - b.getX();
        double dY = a.getY() - b.getY();
        double dZ = a.getZ() - b.getZ();
        double yaw = Math.atan2(dZ, dX);
        double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
        double x = Math.sin(pitch) * Math.cos(yaw);
        double y = Math.sin(pitch) * Math.sin(yaw);
        double z = Math.cos(pitch);

        Vector vector = new Vector(x, z, y);
        //If you want to: vector = vector.normalize();

        return vector;
    }
    public static Vector getRightHeadDirection(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        return new Vector(-direction.getZ(), 0.0, direction.getX()).normalize();
    }
 
    public static Vector getLeftHeadDirection(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        return new Vector(direction.getZ(), 0.0, -direction.getX()).normalize();
    }
    public static Location getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock.getLocation();
    }
    public static Set<Material> getBlocksAtLocation(Location location) {
        Set<Material> materials = Sets.newHashSet();
        materials.add(getRelativeBlock(location, 0, 0, 0));
        materials.add(getRelativeBlock(location, 0.66, 0, -0.66));
        materials.add(getRelativeBlock(location, 0.66, 0, 0.66));
        materials.add(getRelativeBlock(location, -0.66, 0, 0.66));
        materials.add(getRelativeBlock(location, -0.66, 0, -0.66));
        materials.add(getRelativeBlock(location, 0, 0, -0.66));
        materials.add(getRelativeBlock(location, 0, 0, 0.66));
        materials.add(getRelativeBlock(location, -0.66, 0, 0));
        materials.add(getRelativeBlock(location, 0.66, 0, 0));
        return materials;
    }
    public static Material getRelativeBlock(Location location, double x, double y, double z) {
        return new Location(location.getWorld(), location.getX() + x, location.getY() + y, location.getZ() + z).getBlock().getType();
    }
    public static boolean containsOnly(Collection<?> collection, Object o) {
    	for(Object o1 : collection) {
    		if(!o1.equals(o)) {
    			return false;
    		}
    	}
    	return true;
    }
    public static ItemStack createItem(Material mat, String itemName, String[] lore, int attackSpeed) {
		ItemStack iStack = new ItemStack(mat);
		net.minecraft.server.v1_16_R3.ItemStack item = CraftItemStack.asNMSCopy(iStack);
        NBTTagCompound nbt = (item.hasTag() ? item.getTag() : new NBTTagCompound());
        NBTTagList modifiers = new NBTTagList();
        NBTTagCompound aS = new NBTTagCompound();
        aS.set("AttributeName", NBTTagString.a("genertic.attackSpeed"));
        aS.set("Name", NBTTagString.a("genertic.attackSpeed"));
        aS.set("Amount", NBTTagInt.a(attackSpeed));
        aS.set("Operation", NBTTagInt.a(0));
        aS.set("UUIDLeast", NBTTagInt.a(894654));
        aS.set("UUIDMost", NBTTagInt.a(2872));
        modifiers.add(aS);
        nbt.set("AttributeModifiers", modifiers);
        item.setTag(nbt);
        iStack = CraftItemStack.asBukkitCopy(item);
        ItemMeta iMeta = iStack.getItemMeta();
		iMeta.setDisplayName(itemName);
		iMeta.setLore(Arrays.asList(lore));
		iStack.setItemMeta(iMeta);
		return iStack;
	}
    public static void damageEntity(LivingEntity e, Player player, int damage) {
    	EntityDamageByEntityEvent en = new EntityDamageByEntityEvent(player, e, DamageCause.ENTITY_ATTACK, damage);
        Bukkit.getServer().getPluginManager().callEvent(en);
        if(!en.isCancelled()) {
        	e.damage(damage);
        }
    }
    public static double round(double d, int places) {
    	int dev = (int) Math.pow(10, places);
    	double roundOff = (double) Math.round(d * dev) / dev;
    	return roundOff;
    }
    public static Object getValueInMap(Map<?, ?> map, Object value) {
    	for(Entry<?, ?> o : map.entrySet()) {
    		if(o.getValue().equals(value)) {
    			return o.getKey();
    		}
    	}
    	return null;
    }
    public static void addGlow(List<Player> playerList, Entity e) {
    	ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = pm.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, e.getEntityId()); //Set packet's entity id
        WrappedDataWatcher watcher = new WrappedDataWatcher(); //Create data watcher, the Entity Metadata packet requires this
        Serializer serializer = Registry.get(Byte.class); //Found this through google, needed for some stupid reason
        watcher.setEntity(e); //Set the new data watcher's target
        watcher.setObject(0, serializer, (byte) (0x40)); //Set status to glowing, found on protocol page
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects()); //Make the packet's datawatcher the one we created
        try {
        	for(Player player : playerList) {
        		pm.sendServerPacket(player, packet);
        	}
        } catch (InvocationTargetException exception) {
        	exception.printStackTrace();
        }
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static void setEntityGlowing(Entity glowingEntity, Player reciever, boolean glow) {
    	if(glowingEntity != null && glowingEntity.getCustomName() != null && glowingEntity.getCustomName().equals("skysplitter"))
    		return;
        try {
            net.minecraft.server.v1_16_R3.Entity entityPlayer = ((CraftEntity) glowingEntity).getHandle();

            DataWatcher dataWatcher = entityPlayer.getDataWatcher();

            entityPlayer.glowing = glow; // For the update method in EntityPlayer to prevent switching back.

            // The map that stores the DataWatcherItems is private within the DataWatcher Object.
            // We need to use Reflection to access it from Apache Commons and change it.
            Map<Integer, DataWatcher.Item<?>> map = (Map<Integer, DataWatcher.Item<?>>) FieldUtils.readDeclaredField(dataWatcher, "d", true);

            // Get the 0th index for the BitMask value. http://wiki.vg/Entities#Entity
            DataWatcher.Item item = map.get(0);
            byte initialBitMask = (Byte) item.b(); // Gets the initial bitmask/byte value so we don't overwrite anything.
            byte bitMaskIndex = (byte) 0x40; // The index as specified in wiki.vg/Entities
            if (glow) {
                item.a((byte) (initialBitMask));
            } else {
                item.a((byte) (initialBitMask & ~(1 << bitMaskIndex))); // Inverts the specified bit from the index.
            }
            PacketPlayOutEntityMetadata metadataPacket = new PacketPlayOutEntityMetadata(glowingEntity.getEntityId(), dataWatcher, true);

            ((CraftPlayer) reciever).getHandle().playerConnection.sendPacket(metadataPacket);
        } catch (IllegalAccessException e) { // Catch statement necessary for FieldUtils.readDeclaredField()
            e.printStackTrace();
        } catch (Exception e) {
        }
    }
    public static void setExpBar(Player player, int i, int tickrate, float maxTime) {
    	player.setExp(1 - i / maxTime);
		if(i % (20 / tickrate) == 0) {
			player.setLevel((int) ((maxTime - i) / 20 / tickrate));
		}
    }
    public static Vector rotateVectorAroundZ(Vector vector, double degrees) {
        double rad = Math.toRadians(degrees);
       
        double currentX = vector.getX();
        double currentY = vector.getY();
        double currentZ = vector.getZ();
       
        double cosine = Math.cos(rad);
        double sine = Math.sin(rad);
       
        return new Vector(currentX, (cosine * currentY - sine * currentZ), currentZ);
    }
    public static Vector rotateVectorAroundX(Vector vector, double degrees) {
        double rad = Math.toRadians(degrees);
       
        double currentX = vector.getX();
        double currentY = vector.getY();
        double currentZ = vector.getZ();
       
        double cosine = Math.cos(rad);
        double sine = Math.sin(rad);
       
        return new Vector(currentX, (cosine * currentY - sine * currentX), currentZ);
    }
    public static Vector rotateVectorAroundY(Vector vector, double degrees) {
    	double rad = Math.toRadians(degrees);
    	   
        double currentX = vector.getX();
        double currentZ = vector.getZ();
       
        double cosine = Math.cos(rad);
        double sine = Math.sin(rad);
       
        return new Vector((cosine * currentX - sine * currentZ), vector.getY(), (sine * currentX + cosine * currentZ));
    }
    public static boolean floatingNumbersEqual(double a, double b, double threshold) {
    	if(Math.abs(a-b) < threshold) {
    		return true;
    	}
    	return false;
    }
    public static void offHandAnimation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PlayerConnection playerConnection = entityPlayer.playerConnection;
        PacketPlayOutAnimation packetPlayOutAnimation = new PacketPlayOutAnimation(entityPlayer, 3);

        playerConnection.sendPacket(packetPlayOutAnimation);
        playerConnection.a(new PacketPlayInArmAnimation(EnumHand.OFF_HAND));
    }

}
