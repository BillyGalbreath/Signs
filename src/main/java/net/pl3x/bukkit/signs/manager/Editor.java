package net.pl3x.bukkit.signs.manager;

import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_13_R2.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_13_R2.TileEntitySign;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_13_R2.block.CraftSign;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Editor {
    private static final Set<Sign> signs = new HashSet<>();

    public static boolean isOpen(Sign sign) {
        return signs.contains(sign);
    }

    public static void open(Player player, Sign sign) {
        signs.add(sign);
        Editor.sendSignWindowPacket(player, sign);
    }

    public static boolean close(Sign sign) {
        return signs.remove(sign);
    }

    public static void clear() {
        signs.clear();
    }

    private static void sendSignWindowPacket(Player player, Sign sign) {
        // make sign editable
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign nmsSign = ((CraftSign) sign).getTileEntity();
        nmsSign.isEditable = true;
        try {
            Field editor = nmsSign.getClass().getDeclaredField("g");
            editor.setAccessible(true);
            editor.set(nmsSign, nmsPlayer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        NBTTagCompound nbt = new NBTTagCompound();
        for (int i = 0; i < 4; i++) {
            String line = ChatColor.stripColor(sign.getLine(i).replace("\u00a7", "&"));
            line = IChatBaseComponent.ChatSerializer.a(new ChatComponentText(line));
            nbt.setString("Text" + (i + 1), line);
            nbt.setString("Pl3xSignsEditor", "true");
        }
        nbt.setString("text", "");
        nbt.setString("id", "Sign");
        nbt.setInt("x", sign.getX());
        nbt.setInt("y", sign.getY());
        nbt.setInt("z", sign.getZ());

        // update sign text in player's client world
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutTileEntityData(nmsSign.getPosition(), 9, nbt));

        // send open sign packet
        nmsPlayer.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(nmsSign.getPosition()));
    }
}
