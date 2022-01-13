package com.jayemceekay.forgesniper.brush.type.performer;

import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.util.text.TextFormatting;
import org.enginehub.piston.converter.SuggestionHelper;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class CheckerVoxelDiscBrush extends AbstractPerformerBrush {
    private boolean useWorldCoordinates = true;

    public CheckerVoxelDiscBrush() {
    }

    public void loadProperties() {
    }

    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();

        for (String parameter : parameters) {
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(TextFormatting.GOLD + "CheckerVoxelDisc Brush Parameters:");
                messenger.sendMessage(TextFormatting.AQUA + "/b cvd [true|false] -- Enables or disables using World Coordinates.");
                return;
            }

            if (parameter.equalsIgnoreCase("true")) {
                this.useWorldCoordinates = true;
                messenger.sendMessage(TextFormatting.AQUA + "Enabled using World Coordinates.");
            } else if (parameter.equalsIgnoreCase("false")) {
                this.useWorldCoordinates = false;
                messenger.sendMessage(TextFormatting.AQUA + "Disabled using World Coordinates.");
            } else {
                messenger.sendMessage(TextFormatting.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
            }
        }

    }

    @Override
    public HashMap<String, String> getSettings() {
        this.settings.put("World Coordinates", String.valueOf(this.useWorldCoordinates));
        return super.getSettings();
    }

    @Override
    public List<String> handleCompletions(String[] parameters) {
        if (parameters.length > 0) {
            String parameter = parameters[parameters.length - 1];
            return SuggestionHelper.limitByPrefix(Stream.of("true", "false"), parameter);
        } else {
            return SuggestionHelper.limitByPrefix(Stream.of("true", "false"), "");
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = this.getTargetBlock();
        this.applyBrush(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = this.getLastBlock();
        this.applyBrush(snipe, lastBlock);
    }

    private void applyBrush(Snipe snipe, BlockVector3 target) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();

        for(int x = brushSize; x >= -brushSize; --x) {
            for(int y = brushSize; y >= -brushSize; --y) {
                int sum = this.useWorldCoordinates ? target.getX() + x + target.getZ() + y : x + y;
                if (sum % 2 != 0) {
                    try {
                        this.performer.perform(this.getEditSession(), target.getX() + x, this.clampY(target.getY()), target.getZ() + y, this.clampY(target.getX() + x, target.getY(), target.getZ() + y));
                    } catch (MaxChangedBlocksException var9) {
                        var9.printStackTrace();
                    }
                }
            }
        }

    }

    @Override
    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender().brushNameMessage().brushSizeMessage().send();
    }
}
