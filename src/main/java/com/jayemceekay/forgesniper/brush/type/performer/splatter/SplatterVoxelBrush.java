package com.jayemceekay.forgesniper.brush.type.performer.splatter;

import com.jayemceekay.forgesniper.brush.type.performer.AbstractPerformerBrush;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.text.NumericParser;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class SplatterVoxelBrush extends AbstractPerformerBrush {

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(TextFormatting.GOLD + "Splatter Voxel Brush Parameters:");
            messenger.sendMessage(TextFormatting.AQUA + "/b sv s [n] -- Sets a seed percentage to n (1-9999). 100 = 1% Default is " +
                    "1000.");
            messenger.sendMessage(TextFormatting.AQUA + "/b sv g [n] -- Sets a growth percentage to n (1-9999). Default is 1000.");
            messenger.sendMessage(TextFormatting.AQUA + "/b sv r [n] -- Sets a recursion i (1-10).  Default is 3.");
        } else {
            if (parameters.length == 2) {
                if (firstParameter.equalsIgnoreCase("s")) {
                    Integer seedPercent = NumericParser.parseInteger(parameters[1]);
                    if (seedPercent != null && seedPercent >= super.seedPercentMin && seedPercent <= super.seedPercentMax) {
                        this.seedPercent = seedPercent;
                        messenger.sendMessage(TextFormatting.AQUA + "Seed percent set to: " + this.seedPercent / 100 + "%");
                    } else {
                        messenger.sendMessage(TextFormatting.RED + "Seed percent must be an integer " + this.seedPercentMin +
                                "-" + this.seedPercentMax + ".");
                    }
                } else if (firstParameter.equalsIgnoreCase("g")) {
                    Integer growPercent = NumericParser.parseInteger(parameters[1]);
                    if (growPercent != null && growPercent >= super.growthPercentMin && growPercent <= super.growthPercentMax) {
                        this.growthPercent = growPercent;
                        messenger.sendMessage(TextFormatting.AQUA + "Growth percent set to: " + this.growthPercent / 100 + "%");
                    } else {
                        messenger.sendMessage(TextFormatting.RED + "Growth percent must be an integer " + this.growthPercentMin +
                                "-" + this.growthPercentMax + ".");
                    }
                } else if (firstParameter.equalsIgnoreCase("r")) {
                    Integer splatterRecursions = NumericParser.parseInteger(parameters[1]);
                    if (splatterRecursions != null && splatterRecursions >= super.splatterRecursionsMin
                            && splatterRecursions <= super.splatterRecursionsMax) {
                        this.splatterRecursions = splatterRecursions;
                        messenger.sendMessage(TextFormatting.AQUA + "Recursions set to: " + this.splatterRecursions);
                    } else {
                        messenger.sendMessage(TextFormatting.RED + "Recursions must be an integer " + this.splatterRecursionsMin +
                                "-" + this.splatterRecursionsMax + ".");
                    }
                } else {
                    messenger.sendMessage(TextFormatting.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
                }
            } else {
                messenger.sendMessage(TextFormatting.RED + "Invalid brush parameters length! Use the \"info\" parameter to display " +
                        "parameter info.");
            }
        }
    }

    @Override
    public HashMap<String, String> getSettings() {
        this.settings.put("Seed Percent", this.seedPercent / 100 + "");
        this.settings.put("Growth Percent", this.growthPercent / 100 + "");
        this.settings.put("Recursions", this.splatterRecursions + "");
        return super.getSettings();
    }

    @Override
    public List<String> handleCompletions(String[] parameters) {
        if (parameters.length == 1) {
            String parameter = parameters[0];
            return super.sortCompletions(Stream.of("s", "g", "r"), parameter, 0);
        }
        return super.handleCompletions(parameters);
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        try {
            voxelSplatterBall(snipe, targetBlock);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        try {
            voxelSplatterBall(snipe, lastBlock);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

    private void voxelSplatterBall(Snipe snipe, BlockVector3 targetBlock) throws MaxChangedBlocksException {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.seedPercent < super.seedPercentMin || this.seedPercent > super.seedPercentMax) {
            this.seedPercent = DEFAULT_SEED_PERCENT;
            messenger.sendMessage(TextFormatting.BLUE + "Seed percent set to: " + this.seedPercent / 100 + "%");
        }
        if (this.growthPercent < super.growthPercentMin || this.growthPercent > super.growthPercentMax) {
            this.growthPercent = DEFAULT_GROWTH_PERCENT;
            messenger.sendMessage(TextFormatting.BLUE + "Growth percent set to: " + this.growthPercent / 100 + "%");
        }
        if (this.splatterRecursions < super.splatterRecursionsMin || this.splatterRecursions > super.splatterRecursionsMax) {
            this.splatterRecursions = DEFAULT_SPLATTER_RECURSIONS;
            messenger.sendMessage(TextFormatting.BLUE + "Recursions set to: " + this.splatterRecursions);
        }
        int brushSize = toolkitProperties.getBrushSize();
        int[][][] splat = new int[2 * brushSize + 1][2 * brushSize + 1][2 * brushSize + 1];
        // Seed the array
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    if (super.generator.nextInt(super.seedPercentMax + 1) <= this.seedPercent) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        // Grow the seeds
        int gref = this.growthPercent;
        int[][][] tempSplat = new int[2 * brushSize + 1][2 * brushSize + 1][2 * brushSize + 1];
        for (int r = 0; r < this.splatterRecursions; r++) {
            this.growthPercent = gref - ((gref / this.splatterRecursions) * (r));
            for (int x = 2 * brushSize; x >= 0; x--) {
                for (int y = 2 * brushSize; y >= 0; y--) {
                    for (int z = 2 * brushSize; z >= 0; z--) {
                        tempSplat[x][y][z] = splat[x][y][z]; // prime tempsplat
                        int growcheck = 0;
                        if (splat[x][y][z] == 0) {
                            if (x != 0 && splat[x - 1][y][z] == 1) {
                                growcheck++;
                            }
                            if (y != 0 && splat[x][y - 1][z] == 1) {
                                growcheck++;
                            }
                            if (z != 0 && splat[x][y][z - 1] == 1) {
                                growcheck++;
                            }
                            if (x != 2 * brushSize && splat[x + 1][y][z] == 1) {
                                growcheck++;
                            }
                            if (y != 2 * brushSize && splat[x][y + 1][z] == 1) {
                                growcheck++;
                            }
                            if (z != 2 * brushSize && splat[x][y][z + 1] == 1) {
                                growcheck++;
                            }
                        }
                        if (growcheck >= 1 && super.generator.nextInt(super.growthPercentMax + 1) <= this.growthPercent) {
                            tempSplat[x][y][z] = 1; // prevent bleed into splat
                        }
                    }
                }
            }
            // integrate tempsplat back into splat at end of iteration
            for (int x = 2 * brushSize; x >= 0; x--) {
                for (int y = 2 * brushSize; y >= 0; y--) {
                    if (2 * brushSize + 1 >= 0) {
                        System.arraycopy(tempSplat[x][y], 0, splat[x][y], 0, 2 * brushSize + 1);
                    }
                }
            }
        }
        this.growthPercent = gref;
        // Fill 1x1x1 holes
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    if (splat[Math.max(x - 1, 0)][y][z] == 1 && splat[Math.min(
                            x + 1,
                            2 * brushSize
                    )][y][z] == 1 && splat[x][Math.max(0, y - 1)][z] == 1 && splat[x][Math.min(2 * brushSize, y + 1)][z] == 1) {
                        splat[x][y][z] = 1;
                    }
                }
            }
        }
        // Make the changes
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        for (int x = 2 * brushSize; x >= 0; x--) {
            for (int y = 2 * brushSize; y >= 0; y--) {
                for (int z = 2 * brushSize; z >= 0; z--) {
                    if (splat[x][y][z] == 1) {
                        this.performer.perform(
                                getEditSession(),
                                blockX - brushSize + x,
                                blockY - brushSize + z,
                                blockZ - brushSize + y,
                                getBlock(blockX - brushSize + x, blockY - brushSize + z, blockZ - brushSize + y)
                        );
                    }
                }
            }
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        if (this.seedPercent < super.seedPercentMin || this.seedPercent > super.seedPercentMax) {
            this.seedPercent = DEFAULT_SEED_PERCENT;
        }
        if (this.growthPercent < super.growthPercentMin || this.growthPercent > super.growthPercentMax) {
            this.growthPercent = DEFAULT_GROWTH_PERCENT;
        }
        if (this.splatterRecursions < super.splatterRecursionsMin || this.splatterRecursions > super.splatterRecursionsMax) {
            this.splatterRecursions = DEFAULT_SPLATTER_RECURSIONS;
        }
        snipe.createMessageSender()
                .brushNameMessage()
                .brushSizeMessage()
                .message(TextFormatting.BLUE + "Seed percent set to: " + this.seedPercent / 100 + "%")
                .message(TextFormatting.BLUE + "Growth percent set to: " + this.growthPercent / 100 + "%")
                .message(TextFormatting.BLUE + "Recursions set to: " + this.splatterRecursions)
                .send();
    }

}
