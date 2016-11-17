package net.pl3x.bukkit.pl3xsigns.hook;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_11_R1.ChatComponentText;
import net.minecraft.server.v1_11_R1.IChatBaseComponent;
import net.pl3x.bukkit.pl3xsigns.Pl3xSigns;
import net.pl3x.bukkit.pl3xsigns.SignData;
import net.pl3x.bukkit.pl3xsigns.event.LoadSignPacketEvent;
import net.pl3x.bukkit.pl3xsigns.event.UpdateSignPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ProtocolLibHook {
    private static Field colorField = null;

    public static void registerPacketListeners(Pl3xSigns plugin) {
        try {
            colorField = BaseComponent.class.getDeclaredField("color");
            colorField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.TILE_ENTITY_DATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                if (packet.getIntegers().read(0) != 9) {
                    return; // not a sign update packet
                }

                com.comphenix.protocol.wrappers.BlockPosition pos = packet.getBlockPositionModifier().read(0);
                com.comphenix.protocol.wrappers.nbt.NbtCompound nbt = (NbtCompound) packet.getNbtModifier().read(0).deepClone();

                if (pos == null || nbt == null) {
                    return; // packet missing info; ignore it
                }

                if (nbt.containsKey("Pl3xSignsEditor")) {
                    return; // this is a custom sign editor packet; ignore
                }

                Player player = event.getPlayer();
                Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                if (!(block.getState() instanceof Sign)) {
                    return; // position is not a sign
                }

                UpdateSignPacketEvent updateSignPacketEvent = new UpdateSignPacketEvent(player, getLines(nbt), block.getLocation());
                Bukkit.getPluginManager().callEvent(updateSignPacketEvent);

                setLines(nbt, updateSignPacketEvent.getLines(), updateSignPacketEvent.isForceColor() || player.hasPermission("sign.color"));


                //packet.getNbtModifier().write(0, nbt);
                PacketContainer newPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.TILE_ENTITY_DATA);
                newPacket.getIntegers().write(0, 9);
                newPacket.getBlockPositionModifier().write(0, pos);
                newPacket.getNbtModifier().write(0, nbt);

                event.setPacket(newPacket);
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<NbtCompound> nbtList = new ArrayList<>();
                List<NbtBase<?>> rawList = packet.getListNbtModifier().read(0);
                Iterator<NbtBase<?>> iter = rawList.iterator();
                while (iter.hasNext()) {
                    NbtBase<?> obj = iter.next();
                    if (obj instanceof NbtCompound) {
                        NbtCompound nbt = (NbtCompound) obj;
                        if (!nbt.containsKey("id") || !"Sign".equals(nbt.getString("id"))) {
                            continue; // not a sign NBT
                        }
                        nbtList.add(nbt);
                        iter.remove();
                    }
                }

                List<SignData> signDatas = new ArrayList<>();
                for (NbtCompound nbt : nbtList) {
                    String[] lines = getLines(nbt);
                    SignData signData = new SignData(Arrays.copyOf(lines, lines.length), nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z"));
                    signDatas.add(signData);
                }

                Player player = event.getPlayer();
                LoadSignPacketEvent loadSignPacketEvent = new LoadSignPacketEvent(player, signDatas);
                Bukkit.getPluginManager().callEvent(loadSignPacketEvent);

                signDatas = loadSignPacketEvent.getSignDatas();
                for (int i = 0; i < signDatas.size(); i++) {
                    setLines(nbtList.get(i), signDatas.get(i).getLines(), loadSignPacketEvent.isForceColor() || player.hasPermission("sign.color"));
                }

                rawList.addAll(nbtList);

                packet.getListNbtModifier().write(0, rawList);
            }
        });
    }

    private static String getLine(String raw) {
        BaseComponent[] components = ComponentSerializer.parse(raw);
        String componentText = "";
        for (BaseComponent component : components) {
            String text = getLegacyText(component);
            if (text.isEmpty() && component.getExtra() != null) {
                for (BaseComponent extra : component.getExtra()) {
                    text = text + getLegacyText(extra);
                }
            }
            componentText = componentText + text;
        }
        return componentText;
    }

    private static String[] getLines(NbtCompound nbt) {
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            int j = i + 1;
            lines[i] = nbt.containsKey("Text" + j) ? getLine(nbt.getString("Text" + j)) : "";
        }
        return lines;
    }

    private static void setLines(NbtCompound nbt, String[] lines, boolean colorize) {
        for (int i = 0; i < 4; i++) {
            int j = i + 1;
            String text = lines[i];
            if (colorize) {
                text = ChatColor.translateAlternateColorCodes('&', text);
            }
            text = IChatBaseComponent.ChatSerializer.a(new ChatComponentText(text));
            nbt.put("Text" + j, text);
        }
    }

    private static String getLegacyText(BaseComponent... components) {
        StringBuilder builder = new StringBuilder();
        for (BaseComponent baseComponent : components) {
            TextComponent component = (TextComponent) baseComponent;

            ChatColor color = null;
            try {
                color = (ChatColor) colorField.get(component);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (color != null) {
                builder.append(color);
            }


            if (component.isBold()) {
                builder.append(ChatColor.BOLD);
            }

            if (component.isItalic()) {
                builder.append(ChatColor.ITALIC);
            }

            if (component.isUnderlined()) {
                builder.append(ChatColor.UNDERLINE);
            }

            if (component.isStrikethrough()) {
                builder.append(ChatColor.STRIKETHROUGH);
            }

            if (component.isObfuscated()) {
                builder.append(ChatColor.MAGIC);
            }

            builder.append(component.getText());
        }
        return builder.toString();
    }
}
