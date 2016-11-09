package net.pl3x.bukkit.pl3xsigns.lineofsight;

import net.pl3x.bukkit.pl3xsigns.configuration.Config;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Set;

public class LineOfSight {
    public static Block getTargetBlock(Player player) {
        if (Config.USE_OLD_LINE_OF_SIGHT) {
            return player.getTargetBlock((Set<Material>) null, 5);
        }

        World world = player.getWorld();
        RayTrace rayTrace = new RayTrace(player.getEyeLocation());
        ArrayList<Vector> positions = rayTrace.traverse(Config.LINE_OF_SIGHT_DISTANCE, Config.LINE_OF_SIGHT_ACCURACY);
        ArrayList<BlockVector> blockPositions = new ArrayList<>();

        for (Vector pos : positions) {
            BlockVector blockPos = pos.toBlockVector();
            if (!blockPositions.contains(blockPos)) {
                blockPositions.add(blockPos);
            }
        }

        for (BlockVector blockPos : blockPositions) {
            Block block = blockPos.toLocation(world).getBlock();
            if (block.getType() == Material.AIR) {
                continue; // ignore air blocks
            }
            BoundingBox box = new BoundingBox(block);
            for (Vector position : positions) {
                if (rayTrace.intersects(position, box.getMin(), box.getMax())) {
                    return block; // return first non-air block in line of sight
                }
            }
        }

        return null;
    }
}
