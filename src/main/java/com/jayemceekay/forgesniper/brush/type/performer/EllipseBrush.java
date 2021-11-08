package com.jayemceekay.forgesniper.brush.type.performer;

import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessageSender;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.text.NumericParser;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Direction;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.text.TextFormatting;
import org.enginehub.piston.converter.SuggestionHelper;

public class EllipseBrush extends AbstractPerformerBrush {
    private static final double TWO_PI = 6.283185307179586D;
    private static final int SCL_MIN = 1;
    private static final int SCL_MAX = 9999;
    private static final int STEPS_MIN = 1;
    private static final int STEPS_MAX = 2000;
    private static final int DEFAULT_SCL = 10;
    private static final int DEFAULT_STEPS = 200;
    private boolean fill;
    private double stepSize;
    private int sclMin;
    private int sclMax;
    private int stepsMin;
    private int stepsMax;
    private int xscl;
    private int yscl;
    private int steps;

    public EllipseBrush() {
    }

    public void loadProperties() {
        this.sclMin = 1;
        this.sclMax = 9999;
        this.stepsMin = 1;
        this.stepsMax = 2000;
        this.xscl = 10;
        this.yscl = 10;
        this.steps = 200;
    }

    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String[] var4 = parameters;
        int var5 = parameters.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            String parameter = var4[var6];
            if (parameter.equalsIgnoreCase("info")) {
                messenger.sendMessage(TextFormatting.GOLD + "Ellipse Brush Parameters:");
                messenger.sendMessage(TextFormatting.AQUA + "/b el fill -- Toggles fill mode. Default is false.");
                messenger.sendMessage(TextFormatting.AQUA + "/b el x[n] -- Sets X size modifier to n.");
                messenger.sendMessage(TextFormatting.AQUA + "/b el y[n] -- Sets Y size modifier to n.");
                messenger.sendMessage(TextFormatting.AQUA + "/b el t[n] -- Sets the amount of time steps.");
                return;
            }

            if (parameter.equalsIgnoreCase("fill")) {
                if (this.fill) {
                    this.fill = false;
                    messenger.sendMessage(TextFormatting.AQUA + "Fill mode is disabled");
                } else {
                    this.fill = true;
                    messenger.sendMessage(TextFormatting.AQUA + "Fill mode is enabled");
                }
            } else {
                Integer steps;
                if (parameter.startsWith("x[")) {
                    steps = NumericParser.parseInteger(parameter.replace("x[", "").replace("]", ""));
                    if (steps != null && steps >= this.sclMin && steps <= this.sclMax) {
                        this.xscl = steps;
                        messenger.sendMessage(TextFormatting.AQUA + "X-scale modifier set to: " + this.xscl);
                    } else {
                        messenger.sendMessage(TextFormatting.RED + "Invalid number.");
                    }
                } else if (parameter.equalsIgnoreCase("y[")) {
                    steps = NumericParser.parseInteger(parameter.replace("y[", "").replace("]", ""));
                    if (steps != null && steps >= this.sclMin && steps <= this.sclMax) {
                        this.yscl = steps;
                        messenger.sendMessage(TextFormatting.AQUA + "Y-scale modifier set to: " + this.yscl);
                    } else {
                        messenger.sendMessage(TextFormatting.RED + "Invalid number.");
                    }
                } else if (parameter.startsWith("t[")) {
                    steps = NumericParser.parseInteger(parameter.replace("t[", "").replace("]", ""));
                    if (steps != null && steps >= this.stepsMin && steps <= this.stepsMax) {
                        this.steps = steps;
                        messenger.sendMessage(TextFormatting.AQUA + "Render step number set to: " + this.steps);
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
            return SuggestionHelper.limitByPrefix(Stream.of("fill", "x[", "y[", "t["), parameter);
        } else {
            return SuggestionHelper.limitByPrefix(Stream.of("fill", "x[", "y[", "t["), "");
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        execute(snipe, targetBlock);
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 lastBlock = getLastBlock();
        execute(snipe, lastBlock);
    }

    private void execute(Snipe snipe, BlockVector3 targetBlock) {
        this.stepSize = TWO_PI / this.steps;
        if (this.fill) {
            try {
                ellipseFill(snipe, targetBlock);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        } else {
            ellipse(snipe, targetBlock);
        }
    }

    private void ellipse(Snipe snipe, BlockVector3 targetBlock) {
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        try {
            for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                int x = (int) Math.round(this.xscl * Math.cos(steps));
                int y = (int) Math.round(this.yscl * Math.sin(steps));
                BlockVector3 lastBlock = getLastBlock();
                Direction face = getDirection(getTargetBlock(), lastBlock);
                if (face != null) {
                    switch (face) {
                        case NORTH:
                        case SOUTH:
                            this.performer.perform(
                                    getEditSession(),
                                    blockX,
                                    blockY + x,
                                    blockZ + y,
                                    getBlock(blockX, blockY + x, blockZ + y)
                            );
                            break;
                        case EAST:
                        case WEST:
                            this.performer.perform(
                                    getEditSession(),
                                    blockX + x,
                                    blockY + y,
                                    blockZ,
                                    getBlock(blockX + x, blockY + y, blockZ)
                            );
                            break;
                        case UP:
                        case DOWN:
                            this.performer.perform(
                                    getEditSession(),
                                    blockX + x,
                                    blockY,
                                    blockZ + y,
                                    getBlock(blockX + x, blockY, blockZ + y)
                            );
                            break;
                        default:
                            break;
                    }
                }
                if (steps >= TWO_PI) {
                    break;
                }
            }
        } catch (RuntimeException | MaxChangedBlocksException exception) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(TextFormatting.RED + "Invalid target.");
        }
    }

    private void ellipseFill(Snipe snipe, BlockVector3 targetBlock) throws MaxChangedBlocksException {
        EditSession editSession = getEditSession();
        int ix = this.xscl;
        int iy = this.yscl;
        int blockX = targetBlock.getX();
        int blockY = targetBlock.getY();
        int blockZ = targetBlock.getZ();
        this.performer.perform(editSession, blockX, blockY, blockZ, getBlock(blockX, blockY, blockZ));
        try {
            if (ix >= iy) { // Need this unless you want weird holes
                for (iy = this.yscl; iy >= editSession.getMinimumPoint().getY(); iy--) {
                    for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                        int x = (int) Math.round(ix * Math.cos(steps));
                        int y = (int) Math.round(iy * Math.sin(steps));
                        BlockVector3 lastBlock = getLastBlock();
                        Direction face = getDirection(getTargetBlock(), lastBlock);
                        if (face != null) {
                            switch (face) {
                                case NORTH:
                                case SOUTH:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX,
                                            blockY + x,
                                            blockZ + y,
                                            getBlock(blockX, blockY + x, blockZ + y)
                                    );
                                    break;
                                case EAST:
                                case WEST:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY + y,
                                            blockZ,
                                            getBlock(blockX + x, blockY + y, blockZ)
                                    );
                                    break;
                                case UP:
                                case DOWN:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY,
                                            blockZ + y,
                                            getBlock(blockX + x, blockY, blockZ + y)
                                    );
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    ix--;
                }
            } else {
                for (ix = this.xscl; ix >= editSession.getMinimumPoint().getY(); ix--) {
                    for (double steps = 0; (steps <= TWO_PI); steps += this.stepSize) {
                        int x = (int) Math.round(ix * Math.cos(steps));
                        int y = (int) Math.round(iy * Math.sin(steps));
                        BlockVector3 lastBlock = getLastBlock();
                        Direction face = getDirection(getTargetBlock(), lastBlock);
                        if (face != null) {
                            switch (face) {
                                case NORTH:
                                case SOUTH:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX,
                                            blockY + x,
                                            blockZ + y,
                                            getBlock(blockX, blockY + x, blockZ + y)
                                    );
                                    break;
                                case EAST:
                                case WEST:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY + y,
                                            blockZ,
                                            getBlock(blockX + x, blockY + y, blockZ)
                                    );
                                    break;
                                case UP:
                                case DOWN:
                                    this.performer.perform(
                                            getEditSession(),
                                            blockX + x,
                                            blockY,
                                            blockZ + y,
                                            getBlock(blockX + x, blockY, blockZ + y)
                                    );
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (steps >= TWO_PI) {
                            break;
                        }
                    }
                    iy--;
                }
            }
        } catch (RuntimeException | MaxChangedBlocksException exception) {
            SnipeMessenger messenger = snipe.createMessenger();
            messenger.sendMessage(TextFormatting.RED + "Invalid target.");
        }
    }

    public void sendInfo(Snipe snipe) {
        if (this.xscl < this.sclMin || this.xscl > this.sclMax) {
            this.xscl = 10;
        }

        if (this.yscl < this.sclMin || this.yscl > this.sclMax) {
            this.yscl = 10;
        }

        if (this.steps < this.stepsMin || this.steps > this.stepsMax) {
            this.steps = 200;
        }

        SnipeMessageSender messageSender = snipe.createMessageSender();
        messageSender.brushNameMessage().message(TextFormatting.AQUA + "X-size set to: " + TextFormatting.DARK_AQUA + this.xscl).message(TextFormatting.AQUA + "Y-size set to: " + TextFormatting.DARK_AQUA + this.yscl).message(TextFormatting.AQUA + "Render step number set to: " + TextFormatting.DARK_AQUA + this.steps).message(TextFormatting.AQUA + "Fill mode is " + (this.fill ? "enabled" : "disabled")).send();
    }
}
