package com.jayemceekay.forgesniper.brush.type.performer;

import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.text.NumericParser;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.util.text.TextFormatting;
import org.enginehub.piston.converter.SuggestionHelper;

import java.util.List;
import java.util.stream.Stream;

public class CylinderBrush extends AbstractPerformerBrush {
    private double trueCircle;

    public CylinderBrush() {
    }

    public void loadProperties() {
    }

    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int var6 = parameters.length;

        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(TextFormatting.GOLD + "Cylinder Brush Parameters:");
                messenger.sendMessage(TextFormatting.DARK_AQUA + "/b c [true|false] -- Uses a true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
                messenger.sendMessage(TextFormatting.AQUA + "/b c h[n] -- Sets the cylinder v.voxelHeight to n. Default is 1.");
                messenger.sendMessage(TextFormatting.BLUE + "/b c c[n] -- Sets the origin of the cylinder compared to the target block to n. Positive numbers will move the cylinder upward, negative will move it downward.");
                return;
            }

            if (parameter.equalsIgnoreCase("true")) {
                this.trueCircle = 0.5D;
                messenger.sendMessage(TextFormatting.AQUA + "True circle mode ON.");
            } else if (parameter.equalsIgnoreCase("false")) {
                this.trueCircle = 0.0D;
                messenger.sendMessage(TextFormatting.AQUA + "True circle mode OFF.");
            } else {
                Integer center;
                if (parameter.startsWith("h[")) {
                    center = NumericParser.parseInteger(parameter.replace("h[", "").replace("]", ""));
                    if (center != null) {
                        toolkitProperties.setVoxelHeight(center);
                        messenger.sendMessage(TextFormatting.AQUA + "Cylinder v.voxelHeight set to: " + toolkitProperties.getVoxelHeight());
                    } else {
                        messenger.sendMessage(TextFormatting.RED + "Invalid number.");
                    }
                } else if (parameter.startsWith("c[")) {
                    center = NumericParser.parseInteger(parameter.replace("c[", "").replace("]", ""));
                    if (center != null) {
                        toolkitProperties.setCylinderCenter(center);
                        messenger.sendMessage(TextFormatting.AQUA + "Cylinder origin set to: " + toolkitProperties.getCylinderCenter());
                    } else {
                        messenger.sendMessage(TextFormatting.RED + "Invalid number.");
                    }
                } else {
                    messenger.sendMessage(TextFormatting.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }
            }
        }

    }

    public List<String> handleCompletions(String[] parameters) {
        if (parameters.length > 0) {
            String parameter = parameters[parameters.length - 1];
            return SuggestionHelper.limitByPrefix(Stream.of("h[", "c[", "true", "false"), parameter);
        } else {
            return SuggestionHelper.limitByPrefix(Stream.of("h[", "c[", "true", "false"), "");
        }
    }

    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = this.getTargetBlock();

        try {
            this.cylinder(snipe, targetBlock);
        } catch (MaxChangedBlocksException var4) {
            var4.printStackTrace();
        }

    }

    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = this.getLastBlock();

        try {
            this.cylinder(snipe, lastBlock);
        } catch (MaxChangedBlocksException var4) {
            var4.printStackTrace();
        }

    }

    private void cylinder(Snipe snipe, BlockVector3 targetBlock) throws MaxChangedBlocksException {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        int brushSize = toolkitProperties.getBrushSize();
        int yStartingPoint = targetBlock.getY() + toolkitProperties.getCylinderCenter();
        int yEndPoint = targetBlock.getY() + toolkitProperties.getVoxelHeight() + toolkitProperties.getCylinderCenter();
        if (yEndPoint < yStartingPoint) {
            yEndPoint = yStartingPoint;
        }

        EditSession editSession = this.getEditSession();
        int minHeight = editSession.getMinimumPoint().getY();
        int blockX;
        if (yStartingPoint < minHeight) {
            yStartingPoint = minHeight;
            messenger.sendMessage(TextFormatting.DARK_PURPLE + "Warning: off-world start position.");
        } else {
            blockX = editSession.getMaximumPoint().getY();
            if (yStartingPoint > blockX) {
                yStartingPoint = blockX;
                messenger.sendMessage(TextFormatting.DARK_PURPLE + "Warning: off-world start position.");
            }
        }

        if (yEndPoint < minHeight) {
            yEndPoint = minHeight;
            messenger.sendMessage(TextFormatting.DARK_PURPLE + "Warning: off-world end position.");
        } else {
            blockX = editSession.getMaximumPoint().getY();
            if (yEndPoint > blockX) {
                yEndPoint = blockX;
                messenger.sendMessage(TextFormatting.DARK_PURPLE + "Warning: off-world end position.");
            }
        }

        blockX = targetBlock.getX();
        int blockZ = targetBlock.getZ();
        double bSquared = Math.pow((double)brushSize + this.trueCircle, 2.0D);

        for(int y = yEndPoint; y >= yStartingPoint; --y) {
            for(int x = brushSize; x >= 0; --x) {
                double xSquared = Math.pow(x, 2.0D);

                for(int z = brushSize; z >= 0; --z) {
                    if (xSquared + Math.pow(z, 2.0D) <= bSquared) {
                        this.performer.perform(this.getEditSession(), blockX + x, this.clampY(y), blockZ + z, this.clampY(blockX + x, y, blockZ + z));
                        this.performer.perform(this.getEditSession(), blockX + x, this.clampY(y), blockZ - z, this.clampY(blockX + x, y, blockZ - z));
                        this.performer.perform(this.getEditSession(), blockX - x, this.clampY(y), blockZ + z, this.clampY(blockX - x, y, blockZ + z));
                        this.performer.perform(this.getEditSession(), blockX - x, this.clampY(y), blockZ - z, this.clampY(blockX - x, y, blockZ - z));
                    }
                }
            }
        }

    }

    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender().brushNameMessage().brushSizeMessage().voxelHeightMessage().cylinderCenterMessage().send();
    }
}
