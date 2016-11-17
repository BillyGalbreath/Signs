package net.pl3x.bukkit.pl3xsigns.hook;

import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.ChatComponentText;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.minecraft.server.v1_11_R1.NBTTagCompound;
import net.minecraft.server.v1_11_R1.PacketPlayOutOpenSignEditor;
import net.minecraft.server.v1_11_R1.PacketPlayOutTileEntityData;
import net.minecraft.server.v1_11_R1.TileEntitySign;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_11_R1.block.CraftSign;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class NmsHook {
    public static void sendSignWindowPacket(Player player, Sign sign) {
        // make sign editable
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        TileEntitySign signTileEntity = ((CraftSign) sign).getTileEntity();
        signTileEntity.isEditable = true;
        try {
            Field h = signTileEntity.getClass().getDeclaredField("h");
            h.setAccessible(true);
            h.set(signTileEntity, entityPlayer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // send update packet
        BlockPosition position = new BlockPosition(sign.getX(), sign.getY(), sign.getZ());
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
        PacketPlayOutTileEntityData tileEntityPacket = new PacketPlayOutTileEntityData(position, 9, nbt);
        entityPlayer.playerConnection.sendPacket(tileEntityPacket);

        // send open sign packet
        PacketPlayOutOpenSignEditor signEditorPacket = new PacketPlayOutOpenSignEditor(position);
        entityPlayer.playerConnection.sendPacket(signEditorPacket);
    }
}
