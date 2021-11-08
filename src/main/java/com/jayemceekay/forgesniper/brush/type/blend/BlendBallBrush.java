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

public class BlendBallBrush extends AbstractBlendBrush {
    public BlendBallBrush() {
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String[] var4 = parameters;
        int var5 = parameters.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String parameter = var4[var6];
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(TextFormatting.GOLD + "Blend Ball Brush Parameters:");
                messenger.sendMessage(TextFormatting.AQUA + "/b bb water -- Toggles include or exclude (default) water.");
                return;
            }

            super.handleCommand(parameters, snipe);
        }

    }
    @Override
    public void blend(Snipe snipe) throws MaxChangedBlocksException {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        BlockVector3 targetBlock = this.getTargetBlock();
        int smallSphereVolume = (int)MathHelper.sphereVolume(brushSize);
        Set<BlockVector3> smallSphere = new HashSet(smallSphereVolume);
        Map<BlockVector3, BlockType> smallSphereBlockTypes = new HashMap(smallSphereVolume);
        Painters.sphere().center(targetBlock).radius(brushSize).blockSetter((position) -> {
            BlockType type = this.getBlockType(position);
            smallSphere.add(position);
            smallSphereBlockTypes.put(position, type);
        }).paint();
        Iterator var8 = smallSphere.iterator();

        while(var8.hasNext()) {
            BlockVector3 smallSphereBlock = (BlockVector3)var8.next();
            Map<BlockType, Integer> blockTypesFrequencies = new HashMap();
            Painters.cube().center(smallSphereBlock).radius(1).blockSetter((position) -> {
                if (!position.equals(smallSphereBlock)) {
                    BlockType type = this.getBlockType(position);
                    blockTypesFrequencies.merge(type, 1, Integer::sum);
                }
            }).paint();
            CommonMaterial commonMaterial = this.findCommonMaterial(blockTypesFrequencies);
            BlockType type = commonMaterial.getBlockType();
            if (type != null) {
                smallSphereBlockTypes.put(smallSphereBlock, type);
            }
        }

        this.setBlocks(smallSphereBlockTypes);
    }
}
