package com.jayemceekay.forgesniper.brush.type.blend;

import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.math.MathHelper;
import com.jayemceekay.forgesniper.util.painter.Painters;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.text.TextFormatting;

public class BlendVoxelDiscBrush extends AbstractBlendBrush {
    public BlendVoxelDiscBrush() {
    }

    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        int var5 = parameters.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String parameter = parameters[var6];
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(TextFormatting.GOLD + "Blend Voxel Disc Brush Parameters:");
                messenger.sendMessage(TextFormatting.AQUA + "/b bvd water -- Toggles include or exclude (default) water.");
                return;
            }
        }

        super.handleCommand(parameters, snipe);
    }

    public void blend(Snipe snipe) throws MaxChangedBlocksException {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int squareEdge = 2 * brushSize + 1;
        BlockVector3 targetBlock = this.getTargetBlock();
        int smallSquareArea = MathHelper.square(squareEdge);
        Set<BlockVector3> smallSquare = new HashSet(smallSquareArea);
        Map<BlockVector3, BlockType> smallSquareBlockTypes = new HashMap(smallSquareArea);
        Painters.square().center(targetBlock).radius(brushSize).blockSetter((position) -> {
            BlockType type = this.getBlockType(position);
            smallSquare.add(position);
            smallSquareBlockTypes.put(position, type);
        }).paint();
        Iterator var9 = smallSquare.iterator();

        while(var9.hasNext()) {
            BlockVector3 smallSquareBlock = (BlockVector3)var9.next();
            Map<BlockType, Integer> blockTypesFrequencies = new HashMap();
            Painters.square().center(smallSquareBlock).radius(1).blockSetter((position) -> {
                if (!position.equals(smallSquareBlock)) {
                    BlockType type = this.getBlockType(position);
                    blockTypesFrequencies.merge(type, 1, Integer::sum);
                }
            }).paint();
            CommonMaterial commonMaterial = this.findCommonMaterial(blockTypesFrequencies);
            BlockType type = commonMaterial.getBlockType();
            if (type != null) {
                smallSquareBlockTypes.put(smallSquareBlock, type);
            }
        }

        this.setBlocks(smallSquareBlockTypes);
    }
}
