package com.jayemceekay.forgesniper.brush.type.performer;

import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.material.MaterialSets;
import com.jayemceekay.forgesniper.util.text.NumericParser;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.text.TextFormatting;
import org.enginehub.piston.converter.SuggestionHelper;

public class UnderlayBrush extends AbstractPerformerBrush {
    private static final int DEFAULT_DEPTH = 3;
    private boolean allBlocks;
    private int depth;

    public UnderlayBrush() {
    }

    public void loadProperties() {
        this.depth = 3;
    }

    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String[] var4 = parameters;
        int var5 = parameters.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String parameter = var4[var6];
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(TextFormatting.GOLD + "Underlay Brush Parameters:");
                messenger.sendMessage(TextFormatting.AQUA + "/b under all -- Sets the brush to overlay over ALL materials, not just natural surface ones (will no longer ignore trees and buildings).");
                messenger.sendMessage(TextFormatting.AQUA + "/b under some -- Sets the brush to overlay over natural surface materials.");
                messenger.sendMessage(TextFormatting.AQUA + "/b under d[n] -- Sets the number of blocks thick to change to n.");
                return;
            }

            if (parameter.equalsIgnoreCase("all")) {
                this.allBlocks = true;
                messenger.sendMessage(TextFormatting.BLUE + "Will underlay over any block: " + this.depth);
            } else if (parameter.equalsIgnoreCase("some")) {
                this.allBlocks = false;
                messenger.sendMessage(TextFormatting.BLUE + "Will underlay only natural block types: " + this.depth);
            } else if (parameter.equalsIgnoreCase("d[")) {
                Integer depth = NumericParser.parseInteger(parameter.replace("d[", "").replace("]", ""));
                if (depth != null) {
                    this.depth = depth < 1 ? 1 : depth;
                    messenger.sendMessage(TextFormatting.AQUA + "Depth set to: " + this.depth);
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
            return SuggestionHelper.limitByPrefix(Stream.of("d[", "all", "some"), parameter);
        } else {
            return SuggestionHelper.limitByPrefix(Stream.of("d[", "all", "some"), "");
        }
    }

    public void handleArrowAction(Snipe snipe) {
        try {
            this.underlay(snipe);
        } catch (MaxChangedBlocksException var3) {
            var3.printStackTrace();
        }

    }

    public void handleGunpowderAction(Snipe snipe) {
        try {
            this.underlay2(snipe);
        } catch (MaxChangedBlocksException var3) {
            var3.printStackTrace();
        }

    }

    private void underlay(Snipe snipe) throws MaxChangedBlocksException {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow((double)brushSize + 0.5D, 2.0D);

        for(int z = brushSize; z >= -brushSize; --z) {
            for(int x = brushSize; x >= -brushSize; --x) {
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();

                for(int y = blockY; y < blockY + this.depth; ++y) {
                    if (memory[x + brushSize][z + brushSize] != 1 && Math.pow((double)x, 2.0D) + Math.pow((double)z, 2.0D) <= brushSizeSquared) {
                        int i;
                        if (this.allBlocks) {
                            for(i = 0; i < this.depth; ++i) {
                                if (!this.clampY(blockX + x, y + i, blockZ + z).getBlockType().getMaterial().isAir()) {
                                    this.performer.perform(this.getEditSession(), blockX + x, this.clampY(y + i), blockZ + z, this.clampY(blockX + x, y + i, blockZ + z));
                                    memory[x + brushSize][z + brushSize] = 1;
                                }
                            }
                        } else if (MaterialSets.OVERRIDEABLE.contains(this.getBlockType(blockX + x, y, blockZ + z))) {
                            for(i = 0; i < this.depth; ++i) {
                                if (!this.clampY(blockX + x, y + i, blockZ + z).getBlockType().getMaterial().isAir()) {
                                    this.performer.perform(this.getEditSession(), blockX + x, this.clampY(y + i), blockZ + z, this.clampY(blockX + x, y + i, blockZ + z));
                                    memory[x + brushSize][z + brushSize] = 1;
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private void underlay2(Snipe snipe) throws MaxChangedBlocksException {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        int brushSize = toolkitProperties.getBrushSize();
        int[][] memory = new int[brushSize * 2 + 1][brushSize * 2 + 1];
        double brushSizeSquared = Math.pow((double)brushSize + 0.5D, 2.0D);

        for(int z = brushSize; z >= -brushSize; --z) {
            for(int x = brushSize; x >= -brushSize; --x) {
                BlockVector3 targetBlock = this.getTargetBlock();
                int blockX = targetBlock.getX();
                int blockY = targetBlock.getY();
                int blockZ = targetBlock.getZ();

                for(int y = blockY; y < blockY + this.depth; ++y) {
                    if (memory[x + brushSize][z + brushSize] != 1 && Math.pow((double)x, 2.0D) + Math.pow((double)z, 2.0D) <= brushSizeSquared) {
                        int i;
                        if (this.allBlocks) {
                            for(i = -1; i < this.depth - 1; ++i) {
                                this.performer.perform(this.getEditSession(), blockX + x, this.clampY(y - i), blockZ + z, this.clampY(blockX + x, y - i, blockZ + z));
                                memory[x + brushSize][z + brushSize] = 1;
                            }
                        } else if (MaterialSets.OVERRIDEABLE_WITH_ORES.contains(this.getBlockType(blockX + x, y, blockZ + z))) {
                            for(i = -1; i < this.depth - 1; ++i) {
                                this.performer.perform(this.getEditSession(), blockX + x, this.clampY(y - i), blockZ + z, this.clampY(blockX + x, y - i, blockZ + z));
                                memory[x + brushSize][z + brushSize] = 1;
                            }
                        }
                    }
                }
            }
        }

    }

    public void sendInfo(Snipe snipe) {
        snipe.createMessageSender().brushNameMessage().brushSizeMessage().send();
    }
}