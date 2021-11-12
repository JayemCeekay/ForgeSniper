package com.jayemceekay.forgesniper.util.message;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class Messenger {
    private final PlayerEntity sender;

    public Messenger(PlayerEntity sender) {
        this.sender = sender;
    }

    public void sendBrushNameMessage(String brushName) {
        this.sendMessage(TextFormatting.AQUA + "Brush Type: " + TextFormatting.LIGHT_PURPLE + brushName);
    }

    public void sendPerformerNameMessage(String performerName) {
        this.sendMessage(TextFormatting.DARK_PURPLE + "Performer: " + TextFormatting.DARK_GREEN + performerName);
    }

    public void sendBlockTypeMessage(BlockType blockType) {
        this.sendMessage(TextFormatting.GOLD + "Voxel: " + TextFormatting.RED + blockType.getId());
    }

    public void sendBlockDataMessage(BlockState blockData) {
        this.sendMessage(TextFormatting.BLUE + "Data Variable: " + TextFormatting.DARK_RED + blockData.getAsString());
    }

    public void sendReplaceBlockTypeMessage(BlockType replaceBlockType) {
        this.sendMessage(TextFormatting.AQUA + "Replace Material: " + TextFormatting.RED + replaceBlockType.getId());
    }

    public void sendReplaceBlockDataMessage(BlockState replaceBlockData) {
        this.sendMessage(TextFormatting.DARK_GRAY + "Replace Data Variable: " + TextFormatting.DARK_RED + replaceBlockData.getAsString());
    }

    public void sendBrushSizeMessage(int brushSize) {
        this.sendMessage(TextFormatting.GREEN + "Brush Size: " + TextFormatting.DARK_RED + brushSize);
        if (brushSize >= 15) {
            this.sendMessage(TextFormatting.RED + "WARNING: Large brush size selected!");
        }

    }

    public void sendCylinderCenterMessage(int cylinderCenter) {
        this.sendMessage(TextFormatting.BLUE + "Brush Center: " + TextFormatting.DARK_RED + cylinderCenter);
    }

    public void sendVoxelHeightMessage(int voxelHeight) {
        this.sendMessage(TextFormatting.DARK_AQUA + "Brush Height: " + TextFormatting.DARK_RED + voxelHeight);
    }

    public void sendVoxelListMessage(List<? extends BlockState> voxelList) {
        if (voxelList.isEmpty()) {
            this.sendMessage(TextFormatting.DARK_GREEN + "No blocks selected!");
        }

        String message = voxelList.stream().map((state) -> {
            return TextFormatting.AQUA + state.getAsString();
        }).collect(Collectors.joining(TextFormatting.WHITE + ", ", TextFormatting.DARK_GREEN + "Block Types Selected: ", ""));
        this.sendMessage(message);
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(new StringTextComponent(message), this.sender.getUniqueID());
    }
}
