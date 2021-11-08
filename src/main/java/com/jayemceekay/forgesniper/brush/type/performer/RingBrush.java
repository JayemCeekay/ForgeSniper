package com.jayemceekay.forgesniper.brush.type.performer;

import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.text.NumericParser;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.text.TextFormatting;
import org.enginehub.piston.converter.SuggestionHelper;

public class RingBrush extends AbstractPerformerBrush {
    private static final double DEFAULT_INNER_SIZE = 0.0D;
    private double trueCircle;
    private double innerSize = 0.0D;

    public RingBrush() {
    }

    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String[] var4 = parameters;
        int var5 = parameters.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String parameter = var4[var6];
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(TextFormatting.GOLD + "Ring Brush Parameters:");
                messenger.sendMessage(TextFormatting.AQUA + "/b ri [true|false] -- Uses a true circle algorithm instead of the skinnier version with classic sniper nubs. (false is default)");
                messenger.sendMessage(TextFormatting.AQUA + "/b ri ir[n] -- Sets the inner radius to n units.");
                return;
            }

            if (parameter.equalsIgnoreCase("true")) {
                this.trueCircle = 0.5D;
                messenger.sendMessage(TextFormatting.AQUA + "True circle mode ON.");
            } else if (parameter.equalsIgnoreCase("false")) {
                this.trueCircle = 0.0D;
                messenger.sendMessage(TextFormatting.AQUA + "True circle mode OFF.");
            } else if (parameter.startsWith("ir[")) {
                Double innerSize = NumericParser.parseDouble(parameter.replace("ir[", "").replace("]", ""));
                if (innerSize != null) {
                    this.innerSize = innerSize;
                    messenger.sendMessage(TextFormatting.AQUA + "The inner radius has been set to: " + TextFormatting.RED + this.innerSize);
                } else {
                    messenger.sendMessage(TextFormatting.RED + "Invalid number.");
                }
            } else {
                messenger.sendMessage(TextFormatting.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
            }
        }

    }

    public List<String> handleCompletions(String[] parameters) {
        if (parameters.length > 0) {
            String parameter = parameters[parameters.length - 1];
            return SuggestionHelper.limitByPrefix(Stream.of("true", "false", "ir"), parameter);
        } else {
            return SuggestionHelper.limitByPrefix(Stream.of("true", "false", "ir"), "");
        }
    }

    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = this.getTargetBlock();

        try {
            this.ring(snipe, targetBlock);
        } catch (MaxChangedBlocksException var4) {
            var4.printStackTrace();
        }

    }

    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = this.getLastBlock();

        try {
            this.ring(snipe, lastBlock);
        } catch (MaxChangedBlocksException var4) {
            var4.printStackTrace();
        }

    }

    private void ring(Snipe snipe, BlockVector3 targetBlock) throws MaxChangedBlocksException {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        double outerSquared = Math.pow((double)brushSize + this.trueCircle, 2.0D);
        double innerSquared = Math.pow(this.innerSize, 2.0D);
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();

        for(int x = brushSize; x >= 0; --x) {
            double xSquared = Math.pow((double)x, 2.0D);

            for(int z = brushSize; z >= 0; --z) {
                double ySquared = Math.pow((double)z, 2.0D);
                if (xSquared + ySquared <= outerSquared && xSquared + ySquared >= innerSquared) {
                    this.performer.perform(this.getEditSession(), blockX + x, blockY, blockZ + z, this.getBlock(blockX + x, blockY, blockZ + z));
                    this.performer.perform(this.getEditSession(), blockX + x, blockY, blockZ - z, this.getBlock(blockX + x, blockY, blockZ - z));
                    this.performer.perform(this.getEditSession(), blockX - x, blockY, blockZ + z, this.getBlock(blockX - x, blockY, blockZ + z));
                    this.performer.perform(this.getEditSession(), blockX - x, blockY, blockZ - z, this.getBlock(blockX - x, blockY, blockZ - z));
                }
            }
        }

    }

    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender().brushNameMessage().brushSizeMessage().message(TextFormatting.AQUA + "The inner radius is " + TextFormatting.RED + this.innerSize).send();
    }
}
