package net.pl3x.bukkit.signs.protocollib;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtBase;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.pl3x.bukkit.signs.Signs;
import net.pl3x.bukkit.signs.event.LoadSignPacketEvent;
import net.pl3x.bukkit.signs.event.UpdateSignPacketEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * This hook listens to sign packets from server to clients to update the lines players actually see.
 * <p>
 * Plugins can listen to UpdateSignPacketEvent and LoadSignPacketEvent to edit the lines the players actually see
 * without actually editing the lines on the sign in the server world. This allows for per-player sign information, etc.
 */
public class ProtocolLibHook {
    public static void registerPacketListeners(Signs plugin) {
        /*
         * This listens to packets that update a single sign (usually called when a sign is edited)
         */
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.TILE_ENTITY_DATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                if (packet.getIntegers().read(0) != 9) {
                    return; // not a sign update packet
                }

                BlockPosition pos = packet.getBlockPositionModifier().read(0);
                NbtCompound nbt = (NbtCompound) packet.getNbtModifier().read(0).deepClone();

                if (pos == null || nbt == null) {
                    return; // packet missing info; ignore
                }

                if (nbt.containsKey("Pl3xSignsEditor")) {
                    return; // this is a custom sign editor packet; ignore
                }

                Player player = event.getPlayer();
                Block block = player.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ());
                if (!(block.getState() instanceof Sign)) {
                    return; // position is not a sign; ignore
                }

                UpdateSignPacketEvent updateSignPacketEvent = new UpdateSignPacketEvent(player, getLines(nbt), block.getLocation());
                updateSignPacketEvent.callEvent();

                setLines(nbt, updateSignPacketEvent.getLines());

                packet.getIntegers().write(0, 9);
                packet.getBlockPositionModifier().write(0, pos);
                packet.getNbtModifier().write(0, nbt);
            }
        });

        /*
         * This listens to packets that send entire chunks to players, allowing to edit all the signs in the chunks
         */
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                List<NbtBase<?>> rawNBTList = packet.getListNbtModifier().read(0);
                List<NbtCompound> signNBTs = new ArrayList<>();

                Iterator<NbtBase<?>> iter = rawNBTList.iterator();
                while (iter.hasNext()) {
                    NbtBase<?> obj = iter.next();
                    if (obj instanceof NbtCompound) {
                        NbtCompound nbt = (NbtCompound) obj;
                        if (nbt.containsKey("id") && "minecraft:sign".equals(nbt.getString("id"))) {
                            signNBTs.add(nbt);
                            iter.remove();
                        }
                    }
                }

                List<SignData> signDatas = new ArrayList<>();
                for (NbtCompound nbt : signNBTs) {
                    signDatas.add(new SignData(getLines(nbt), nbt.getInteger("x"), nbt.getInteger("y"), nbt.getInteger("z")));
                }

                LoadSignPacketEvent loadSignPacketEvent = new LoadSignPacketEvent(event.getPlayer(), signDatas);
                if (loadSignPacketEvent.callEvent()) {
                    signDatas = loadSignPacketEvent.getSignDatas();
                    for (int i = 0; i < signDatas.size(); i++) {
                        setLines(signNBTs.get(i), signDatas.get(i).getLines());
                    }
                    rawNBTList.addAll(signNBTs);
                    packet.getListNbtModifier().write(0, rawNBTList);
                }
            }
        });
    }

    private static String[] getLines(NbtCompound nbt) {
        String[] lines = new String[4];
        for (int i = 0; i < 4; i++) {
            lines[i] = nbt.containsKey("Text" + (i + 1)) ? parseLine(nbt.getString("Text" + (i + 1))) : "";
        }
        return lines;
    }

    private static void setLines(NbtCompound nbt, String[] lines) {
        for (int i = 0; i < 4; i++) {
            nbt.put("Text" + (i + 1), IChatBaseComponent.ChatSerializer.a(new ChatComponentText(lines[i])));
        }
    }

    private static String parseLine(String raw) {
        IChatBaseComponent component = IChatBaseComponent.ChatSerializer.a(raw);
        if (component != null) {
            return component.e();
        }
        return "";
    }
}
