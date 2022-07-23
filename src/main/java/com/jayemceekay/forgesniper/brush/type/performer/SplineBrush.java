
package com.jayemceekay.forgesniper.brush.type.performer;

import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessageSender;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.ChatFormatting;
import org.enginehub.piston.converter.SuggestionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class SplineBrush extends AbstractPerformerBrush {
    private final List<BlockVector3> endPts = new ArrayList<>();
    private final List<BlockVector3> ctrlPts = new ArrayList<>();
    private final List<com.jayemceekay.forgesniper.brush.type.performer.SplineBrush.Point> spline = new ArrayList<>();
    private boolean set;
    private boolean ctrl;

    public SplineBrush() {
    }

    public void loadProperties() {
    }

    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];
        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatFormatting.GOLD + "Spline Brush Barameters");
            messenger.sendMessage(ChatFormatting.AQUA + "/b sp ss -- Enables endpoint selection mode for desired curve");
            messenger.sendMessage(ChatFormatting.AQUA + "/b sp sc -- Enables control point selection mode for desired curve");
            messenger.sendMessage(ChatFormatting.AQUA + "/b sp clear -- Clears out the curve selection");
            messenger.sendMessage(ChatFormatting.AQUA + "/b sp ren -- Renders curve from control points");
        } else if (parameters.length == 1) {
            if (firstParameter.equalsIgnoreCase("ss")) {
                if (this.set) {
                    this.set = false;
                    messenger.sendMessage(ChatFormatting.AQUA + "Endpoint selection mode disabled.");
                } else {
                    this.set = true;
                    this.ctrl = false;
                    messenger.sendMessage(ChatFormatting.GRAY + "Endpoint selection mode ENABLED.");
                }
            } else if (firstParameter.equalsIgnoreCase("sc")) {
                if (this.ctrl) {
                    this.ctrl = false;
                    messenger.sendMessage(ChatFormatting.AQUA + "Control point selection mode disabled.");
                } else {
                    this.set = false;
                    this.ctrl = true;
                    messenger.sendMessage(ChatFormatting.GRAY + "Control point selection mode ENABLED.");
                }
            } else if (firstParameter.equalsIgnoreCase("clear")) {
                this.clear(snipe);
            } else if (firstParameter.equalsIgnoreCase("ren")) {
                if (this.endPts.size() == 2 && this.ctrlPts.size() == 2) {
                    if (this.spline(new com.jayemceekay.forgesniper.brush.type.performer.SplineBrush.Point(this.endPts.get(0)), new com.jayemceekay.forgesniper.brush.type.performer.SplineBrush.Point(this.endPts.get(1)), new com.jayemceekay.forgesniper.brush.type.performer.SplineBrush.Point(this.ctrlPts.get(0)), new com.jayemceekay.forgesniper.brush.type.performer.SplineBrush.Point(this.ctrlPts.get(1)), snipe)) {
                        try {
                            this.render();
                        } catch (MaxChangedBlocksException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    messenger.sendMessage(ChatFormatting.RED + "Some endpoints or controlspoints are missing.");
                }
            } else {
                messenger.sendMessage(ChatFormatting.RED + "Invalid brush parameters! Use the \"info\" parameter to display parameter info.");
            }
        } else {
            messenger.sendMessage(ChatFormatting.RED + "Invalid brush parameters length! Use the \"info\" parameter to display parameter info.");
        }

    }

    public List<String> handleCompletions(String[] parameters) {
        if (parameters.length > 0) {
            String parameter = parameters[parameters.length - 1];
            return SuggestionHelper.limitByPrefix(Stream.of("ss", "sc", "clear", "ren"), parameter);
        } else {
            return SuggestionHelper.limitByPrefix(Stream.of("ss", "sc", "clear", "ren"), "");
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (this.set) {
            removeFromSet(snipe, true, targetBlock);
        } else if (this.ctrl) {
            removeFromSet(snipe, false, targetBlock);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) {
        BlockVector3 targetBlock = getTargetBlock();
        if (this.set) {
            addToSet(snipe, true, targetBlock);
        }
        if (this.ctrl) {
            addToSet(snipe, false, targetBlock);
        }
    }

    private void addToSet(Snipe snipe, boolean ep, BlockVector3 targetBlock) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (ep) {
            if (this.endPts.contains(targetBlock) || this.endPts.size() == 2) {
                return;
            }
            this.endPts.add(targetBlock);
            messenger.sendMessage(ChatFormatting.GRAY + "Added block " + ChatFormatting.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock
                    .getZ() + ") " + ChatFormatting.GRAY + "to endpoint selection");
            return;
        }
        if (this.ctrlPts.contains(targetBlock) || this.ctrlPts.size() == 2) {
            return;
        }
        this.ctrlPts.add(targetBlock);
        messenger.sendMessage(ChatFormatting.GRAY + "Added block " + ChatFormatting.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock
                .getZ() + ") " + ChatFormatting.GRAY + "to control point selection");
    }

    private void removeFromSet(Snipe snipe, boolean ep, BlockVector3 targetBlock) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (ep) {
            if (!this.endPts.contains(targetBlock)) {
                messenger.sendMessage(ChatFormatting.RED + "That block is not in the endpoint selection set.");
                return;
            }
            this.endPts.add(targetBlock);
            messenger.sendMessage(ChatFormatting.GRAY + "Removed block " + ChatFormatting.RED + "(" + targetBlock.getX() + ", " + targetBlock
                    .getY() + ", " + targetBlock.getZ() + ") " + ChatFormatting.GRAY + "from endpoint selection");
            return;
        }
        if (!this.ctrlPts.contains(targetBlock)) {
            messenger.sendMessage(ChatFormatting.RED + "That block is not in the control point selection set.");
            return;
        }
        this.ctrlPts.remove(targetBlock);
        messenger.sendMessage(ChatFormatting.GRAY + "Removed block " + ChatFormatting.RED + "(" + targetBlock.getX() + ", " + targetBlock.getY() + ", " + targetBlock
                .getZ() + ") " + ChatFormatting.GRAY + "from control point selection");
    }

    private boolean spline(Point start, Point end, Point c1, Point c2, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        this.spline.clear();
        try {
            Point c = c1.subtract(start)
                    .multiply(3);
            Point b = c2.subtract(c1)
                    .multiply(3)
                    .subtract(c);
            Point a = end.subtract(start)
                    .subtract(c)
                    .subtract(b);
            for (double t = 0.0; t < 1.0; t += 0.01) {
                int px = (int) Math.round(a.getX() * (t * t * t) + b.getX() * (t * t) + c.getX() * t + this.endPts.get(0)
                        .getX());
                int py = (int) Math.round(a.getY() * (t * t * t) + b.getY() * (t * t) + c.getY() * t + this.endPts.get(0)
                        .getY());
                int pz = (int) Math.round(a.getZ() * (t * t * t) + b.getZ() * (t * t) + c.getZ() * t + this.endPts.get(0)
                        .getZ());
                if (!this.spline.contains(new Point(px, py, pz))) {
                    this.spline.add(new Point(px, py, pz));
                }
            }
            return true;
        } catch (RuntimeException exception) {
            messenger.sendMessage(ChatFormatting.RED + "Not enough points selected; " + this.endPts.size() + " endpoints, " + this.ctrlPts
                    .size() + " control points");
            return false;
        }
    }

    private void render() throws MaxChangedBlocksException {
        if (this.spline.isEmpty()) {
            return;
        }
        for (Point point : this.spline) {
            this.performer.perform(
                    getEditSession(),
                    point.getX(),
                    clampY(point.getY()),
                    point.getZ(),
                    clampY(point.getX(), point.getY(), point.getZ())
            );
        }
    }

    private void clear(Snipe snipe) {
        this.spline.clear();
        this.ctrlPts.clear();
        this.endPts.clear();
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendMessage(ChatFormatting.GRAY + "Bezier curve cleared.");
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessageSender messageSender = snipe.createMessageSender()
                .brushNameMessage();
        if (this.set) {
            messageSender.message(ChatFormatting.GRAY + "Endpoint selection mode ENABLED.");
        } else if (this.ctrl) {
            messageSender.message(ChatFormatting.GRAY + "Control point selection mode ENABLED.");
        } else {
            messageSender.message(ChatFormatting.AQUA + "No selection mode enabled.");
        }
        messageSender.send();
    }

    // Vector class for splines
    private static final class Point {

        private int x;
        private int y;
        private int z;

        private Point(BlockVector3 block) {
            this.x = block.getX();
            this.y = block.getY();
            this.z = block.getZ();
        }

        private Point(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point add(Point point) {
            return new Point(this.x + point.x, this.y + point.y, this.z + point.z);
        }

        public Point multiply(int scalar) {
            return new Point(this.x * scalar, this.y * scalar, this.z * scalar);
        }

        public Point subtract(Point point) {
            return new Point(this.x - point.x, this.y - point.y, this.z - point.z);
        }

        public int getX() {
            return this.x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return this.y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getZ() {
            return this.z;
        }

        public void setZ(int z) {
            this.z = z;
        }

    }

}
