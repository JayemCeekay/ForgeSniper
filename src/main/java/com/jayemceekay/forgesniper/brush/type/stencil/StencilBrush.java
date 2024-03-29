package com.jayemceekay.forgesniper.brush.type.stencil;

import com.jayemceekay.forgesniper.ForgeSniper;
import com.jayemceekay.forgesniper.brush.type.AbstractBrush;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.material.Materials;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import org.enginehub.piston.converter.SuggestionHelper;

import java.io.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * This is paste only currently. Assumes files exist, and thus has no usefulness until I add in saving stencils later. Uses sniper-exclusive stencil format: 3
 * shorts for X,Z,Y size of cuboid 3 shorts for X,Z,Y offsets from the -X,-Z,-Y corner. This is the reference point for pasting, corresponding to where you
 * click your brush. 1 long integer saying how many runs of blocks are in the schematic (data is compressed into runs) 1 per run: ( 1 boolean: true = compressed
 * line ahead, false = locally unique block ahead. This wastes a bit instead of a byte, and overall saves space, as long as at least 1/8 of all RUNS are going
 * to be size 1, which in Minecraft is almost definitely true. IF boolean was true, next unsigned byte stores the number of consecutive blocks of the same type,
 * up to 256. IF boolean was false, there is no byte here, goes straight to ID and data instead, which applies to just one block. 2 bytes to identify type of
 * block. First byte is ID, second is data. This applies to every one of the line of consecutive blocks if boolean was true. )
 * TODO: Make limit a config option
 */
public class StencilBrush extends AbstractBrush {

    private static final int DEFAULT_PASTE_OPTION = 1;

    private final int[] firstPoint = new int[3];
    private final int[] secondPoint = new int[3];
    private final int[] pastePoint = new int[3];
    private String filename = "NoFileLoaded";
    private short x;
    private short z;
    private short y;
    private short xRef;
    private short zRef;
    private short yRef;
    private byte point = 1;

    private boolean legacy = false;

    private byte pasteOption; // 0 = full, 1 = fill, 2 = replace

    @Override
    public void loadProperties() {
        this.pasteOption = (byte) 0;

        File dataFolder = new File(ForgeSniper.FORGESNIPER_CONFIG_FOLDER.getPath(), "/stencils");
        dataFolder.mkdirs();
    }

    @Override
    public void handleCommand(String[] parameters, Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        String firstParameter = parameters[0];

        if (firstParameter.equalsIgnoreCase("info")) {
            messenger.sendMessage(ChatFormatting.GOLD + "Stencil Brush Parameters:");
            messenger.sendMessage(ChatFormatting.AQUA + "/b st (full|fill|replace) [s] -- Loads the specified stencil s. " +
                    "Full/fill/replace must come first. Full = paste all blocks, fill = paste only into air blocks, replace = " +
                    "paste full blocks in only, but replace anything in their way.");
        } else {
            byte pasteOption;
            byte pasteParam;
            if (firstParameter.equalsIgnoreCase("full")) {
                pasteOption = 0;
                pasteParam = 1;
            } else if (firstParameter.equalsIgnoreCase("fill")) {
                pasteOption = 1;
                pasteParam = 1;
            } else if (firstParameter.equalsIgnoreCase("replace")) {
                pasteOption = 2;
                pasteParam = 1;
            } else {
                // Reset to [s] parameter expected.
                pasteOption = 1;
                pasteParam = 0;
            }
            if (firstParameter.equalsIgnoreCase("legacy")) {
                this.legacy = !this.legacy;
                messenger.sendMessage(ChatFormatting.GREEN + "Legacy stencil format " + (this.legacy ? "enabled" : "disabled"));
                return;
            }
            if (parameters.length != 1 + pasteParam) {
                messenger.sendMessage(ChatFormatting.RED + "Missing arguments, this command expects more.");
                return;
            }

            this.pasteOption = pasteOption;
            try {
                this.filename = parameters[pasteParam];
                File file = new File(ForgeSniper.FORGESNIPER_CONFIG_FOLDER.getPath(), "/stencils/" + this.filename + ".vstencil");
                if (file.exists()) {
                    messenger.sendMessage(ChatFormatting.RED + "Stencil '" + this.filename + "' exists and was loaded. Make sure you are using gunpowder if you do not want any chance of overwriting the file.");
                } else {
                    messenger.sendMessage(ChatFormatting.AQUA + "Stencil '" + this.filename + "' does not exist. Ready to be saved to, but cannot be pasted.");
                }
            } catch (RuntimeException exception) {
                exception.printStackTrace();
                messenger.sendMessage(ChatFormatting.RED + "You need to type a stencil name.");
            }
        }
    }

    @Override
    public List<String> handleCompletions(String[] parameters, Snipe snipe) {
        if (parameters.length > 0) {
            String parameter = parameters[parameters.length - 1];
            return SuggestionHelper.limitByPrefix(Stream.of("full", "fill", "replace"), parameter);
        } else {
            return SuggestionHelper.limitByPrefix(Stream.of("full", "fill", "replace"), "");
        }
    }

    @Override
    public void handleArrowAction(Snipe snipe) { // will be used to copy/save later on?
        SnipeMessenger messenger = snipe.createMessenger();
        BlockVector3 targetBlock = getTargetBlock();
        if (this.point == 1) {
            this.firstPoint[0] = targetBlock.getX();
            this.firstPoint[1] = targetBlock.getZ();
            this.firstPoint[2] = targetBlock.getY();
            messenger.sendMessage(ChatFormatting.GRAY + "First point");
            messenger.sendMessage("X:" + this.firstPoint[0] + " Z:" + this.firstPoint[1] + " Y:" + this.firstPoint[2]);
            this.point = 2;
        } else if (this.point == 2) {
            this.secondPoint[0] = targetBlock.getX();
            this.secondPoint[1] = targetBlock.getZ();
            this.secondPoint[2] = targetBlock.getY();
            if ((Math.abs(this.firstPoint[0] - this.secondPoint[0]) * Math.abs(this.firstPoint[1] - this.secondPoint[1]) * Math.abs(
                    this.firstPoint[2] - this.secondPoint[2])) > 5000000) {
                messenger.sendMessage(ChatFormatting.DARK_RED + "Area selected is too large. (Limit is 5,000,000 blocks)");
                this.point = 1;
            } else {
                messenger.sendMessage(ChatFormatting.GRAY + "Second point");
                messenger.sendMessage("X:" + this.secondPoint[0] + " Z:" + this.secondPoint[1] + " Y:" + this.secondPoint[2]);
                this.point = 3;
            }
        } else if (this.point == 3) {
            this.pastePoint[0] = targetBlock.getX();
            this.pastePoint[1] = targetBlock.getZ();
            this.pastePoint[2] = targetBlock.getY();
            messenger.sendMessage(ChatFormatting.GRAY + "Paste Reference point");
            messenger.sendMessage("X:" + this.pastePoint[0] + " Z:" + this.pastePoint[1] + " Y:" + this.pastePoint[2]);
            this.point = 1;
            this.stencilSave(snipe);
        }
    }

    @Override
    public void handleGunpowderAction(Snipe snipe) { // will be used to paste later on
        this.stencilPaste(snipe);
    }

    private void stencilPaste(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        if (this.filename.matches("NoFileLoaded")) {
            messenger.sendMessage(ChatFormatting.RED + "You did not specify a filename. This is required.");
            return;
        }
        File file = new File(ForgeSniper.FORGESNIPER_CONFIG_FOLDER.getPath(), "/stencils/" + this.filename + ".vstencil");
        if (file.exists()) {
            try {
                DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                this.x = in.readShort();
                this.z = in.readShort();
                this.y = in.readShort();
                this.xRef = in.readShort();
                this.zRef = in.readShort();
                this.yRef = in.readShort();
                int numRuns = in.readInt();
                int currX = -this.xRef; // so if your ref point is +5 x, you want to start pasting -5 blocks from the clicked point (the reference) to get the
                // corner, for example.
                int currZ = -this.zRef;
                int currY = -this.yRef;
                BlockState blockData;
                BlockVector3 targetBlock = getTargetBlock();
                int blockPositionX = targetBlock.getX();
                int blockPositionY = targetBlock.getY();
                int blockPositionZ = targetBlock.getZ();
                if (this.pasteOption == 0) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in, snipe);
                            for (int j = 0; j < numLoops; j++) {
                                setBlockData(
                                        blockPositionX + currX,
                                        clampY(blockPositionY + currY),
                                        blockPositionZ + currZ,
                                        blockData
                                );
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            setBlockData(
                                    blockPositionX + currX,
                                    clampY(blockPositionY + currY),
                                    blockPositionZ + currZ,
                                    readBlockData(in, snipe)
                            );
                            currX++;
                            if (currX == this.x - this.xRef) {
                                currX = -this.xRef;
                                currZ++;
                                if (currZ == this.z - this.zRef) {
                                    currZ = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else if (this.pasteOption == 1) {
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in, snipe);
                            for (int j = 0; j < numLoops; j++) {
                                BlockType type = blockData.getBlockType();
                                if (!Materials.isEmpty(type) && clampY(
                                        blockPositionX + currX,
                                        blockPositionY + currY,
                                        blockPositionZ + currZ
                                ).getBlockType().getMaterial().isAir()) {
                                    setBlockData(
                                            blockPositionX + currX,
                                            clampY(blockPositionY + currY),
                                            blockPositionZ + currZ,
                                            blockData
                                    );
                                }
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in, snipe);
                            BlockType type = blockData.getBlockType();
                            if (!Materials.isEmpty(type) && clampY(
                                    blockPositionX + currX,
                                    blockPositionY + currY,
                                    blockPositionZ + currZ
                            ).getBlockType().getMaterial().isAir()) {
                                // v.sendMessage("currX:" + currX + " currZ:"+currZ + " currY:" + currY + " id:" + id + " data:" + (byte)data);
                                setBlockData(
                                        blockPositionX + currX,
                                        clampY(blockPositionY + currY),
                                        blockPositionZ + currZ,
                                        blockData
                                );
                            }
                            currX++;
                            if (currX == this.x - this.xRef) {
                                currX = -this.xRef;
                                currZ++;
                                if (currZ == this.z - this.zRef) {
                                    currZ = -this.zRef;
                                    currY++;
                                }
                            }
                        }
                    }
                } else { // replace
                    for (int i = 1; i < numRuns + 1; i++) {
                        if (in.readBoolean()) {
                            int numLoops = in.readByte() + 128;
                            blockData = readBlockData(in, snipe);
                            for (int j = 0; j < (numLoops); j++) {
                                BlockType type = blockData.getBlockType();
                                if (!Materials.isEmpty(type)) {
                                    setBlockData(
                                            blockPositionX + currX,
                                            clampY(blockPositionY + currY),
                                            blockPositionZ + currZ,
                                            blockData
                                    );
                                }
                                currX++;
                                if (currX == this.x - this.xRef) {
                                    currX = -this.xRef;
                                    currZ++;
                                    if (currZ == this.z - this.zRef) {
                                        currZ = -this.zRef;
                                        currY++;
                                    }
                                }
                            }
                        } else {
                            blockData = readBlockData(in, snipe);
                            BlockType type = blockData.getBlockType();
                            if (!Materials.isEmpty(type)) {
                                setBlockData(
                                        blockPositionX + currX,
                                        clampY(blockPositionY + currY),
                                        blockPositionZ + currZ,
                                        blockData
                                );
                            }
                            currX++;
                            if (currX == this.x) {
                                currX = 0;
                                currZ++;
                                if (currZ == this.z) {
                                    currZ = 0;
                                    currY++;
                                }
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException | MaxChangedBlocksException exception) {
                messenger.sendMessage(ChatFormatting.RED + "Something went wrong.");
                exception.printStackTrace();
            }
        } else {
            messenger.sendMessage(ChatFormatting.RED + "You need to type a stencil name / your specified stencil does not exist.");
        }
    }

    private BlockState readBlockData(DataInputStream in, Snipe snipe) throws IOException {
        if (this.legacy) {
            int ID = in.readByte() + 128;
            int data = in.readByte() + 128;
            ParserContext parserContext = new ParserContext();
            parserContext.setActor(ForgeAdapter.adaptPlayer((ServerPlayer) snipe.getSniper().getPlayer()));
            parserContext.setExtent(getEditSession());
            try {
                return WorldEdit.getInstance().getBlockFactory().parseFromInput(ID + ":" + data, parserContext).toImmutableState();
            } catch (InputParseException ignored) {
            }
        } else {
            String blockDataString = in.readUTF();
            try {
                ParserContext parserContext = new ParserContext();
                parserContext.setActor(ForgeAdapter.adaptPlayer((ServerPlayer) snipe.getSniper().getPlayer()));
                parserContext.setExtent(getEditSession());
                return WorldEdit.getInstance().getBlockFactory().parseFromInput(blockDataString, parserContext).toImmutableState();
            } catch (InputParseException e) {
                snipe.createMessageSender().message(ChatFormatting.RED + "Invalid Stencil Format, Try Legacy Mode").send();
            }
        }
        return BlockTypes.AIR.getDefaultState();

    }

    private void stencilSave(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        File file = new File(ForgeSniper.FORGESNIPER_CONFIG_FOLDER.getPath(), "/stencils/" + this.filename + ".vstencil");
        try {
            this.x = (short) (Math.abs((this.firstPoint[0] - this.secondPoint[0])) + 1);
            this.z = (short) (Math.abs((this.firstPoint[1] - this.secondPoint[1])) + 1);
            this.y = (short) (Math.abs((this.firstPoint[2] - this.secondPoint[2])) + 1);
            this.xRef = (short) ((this.firstPoint[0] > this.secondPoint[0])
                    ? (this.pastePoint[0] - this.secondPoint[0])
                    : (this.pastePoint[0] - this.firstPoint[0]));
            this.zRef = (short) ((this.firstPoint[1] > this.secondPoint[1])
                    ? (this.pastePoint[1] - this.secondPoint[1])
                    : (this.pastePoint[1] - this.firstPoint[1]));
            this.yRef = (short) ((this.firstPoint[2] > this.secondPoint[2])
                    ? (this.pastePoint[2] - this.secondPoint[2])
                    : (this.pastePoint[2] - this.firstPoint[2]));
            if ((this.x * this.y * this.z) > 50000) {
                messenger.sendMessage(ChatFormatting.AQUA + "Volume exceeds maximum limit.");
                return;
            }
            createParentDirs(file);
            file.createNewFile();
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            int blockPositionX = Math.min(this.firstPoint[0], this.secondPoint[0]);
            int blockPositionZ = Math.min(this.firstPoint[1], this.secondPoint[1]);
            int blockPositionY = Math.min(this.firstPoint[2], this.secondPoint[2]);
            out.writeShort(this.x);
            out.writeShort(this.z);
            out.writeShort(this.y);
            out.writeShort(this.xRef);
            out.writeShort(this.zRef);
            out.writeShort(this.yRef);
            messenger.sendMessage(ChatFormatting.AQUA + "Volume: " + this.x * this.z * this.y + " blockPositionX:" + blockPositionX + " blockPositionZ:" + blockPositionZ + " blockPositionY:" + blockPositionY);
            BlockState[] blockDataArray = new BlockState[this.x * this.z * this.y];
            byte[] runSizeArray = new byte[this.x * this.z * this.y];
            BlockState lastBlockData = getBlock(blockPositionX, blockPositionY, blockPositionZ);
            int counter = 0;
            int arrayIndex = 0;
            for (int y = 0; y < this.y; y++) {
                for (int z = 0; z < this.z; z++) {
                    for (int x = 0; x < this.x; x++) {
                        BlockState thisBlockData = getBlock(blockPositionX + x, blockPositionY + y, blockPositionZ + z);
                        if (!thisBlockData.equals(lastBlockData) || counter == 255) {
                            blockDataArray[arrayIndex] = lastBlockData;
                            runSizeArray[arrayIndex] = (byte) (counter - 128);
                            arrayIndex++;
                            counter = 1;
                        } else {
                            counter++;
                        }
                        lastBlockData = thisBlockData;
                    }
                }
            }
            blockDataArray[arrayIndex] = lastBlockData; // saving last run, which will always be left over.
            runSizeArray[arrayIndex] = (byte) (counter - 128);
            out.writeInt(arrayIndex + 1);
            // v.sendMessage("number of runs = " + arrayIndex);
            for (int i = 0; i < arrayIndex + 1; i++) {
                if (runSizeArray[i] > -127) {
                    out.writeBoolean(true);
                    out.writeByte(runSizeArray[i]);
                } else {
                    out.writeBoolean(false);
                }
                out.writeUTF(blockDataArray[i].getAsString());
            }
            messenger.sendMessage(ChatFormatting.BLUE + "Saved as '" + this.filename + "'.");
            out.close();
        } catch (IOException exception) {
            messenger.sendMessage(ChatFormatting.RED + "Something went wrong.");
            exception.printStackTrace();
        }
    }

    private void createParentDirs(File file) throws IOException {
        File canonicalFile = file.getCanonicalFile();
        File parent = canonicalFile.getParentFile();
        if (parent == null) {
            return;
        }
        parent.mkdirs();
        if (!parent.isDirectory()) {
            throw new IOException("Unable to create parent directories of " + file);
        }
    }

    @Override
    public void sendInfo(Snipe snipe) {
        SnipeMessenger messenger = snipe.createMessenger();
        messenger.sendBrushNameMessage();
        messenger.sendMessage("File loaded: " + this.filename);
    }

}
