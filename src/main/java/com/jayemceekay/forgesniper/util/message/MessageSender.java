package com.jayemceekay.forgesniper.util.message;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class MessageSender {
    private final PlayerEntity sender;
    private final ArrayList<String> messages = new ArrayList(0);

    public MessageSender(PlayerEntity sender) {
        this.sender = sender;
    }

    public MessageSender brushNameMessage(String brushName) {
        this.messages.add(TextFormatting.AQUA + "Brush Type: " + TextFormatting.LIGHT_PURPLE + brushName);
        return this;
    }

    public MessageSender performerNameMessage(String performerName) {
        this.messages.add(TextFormatting.DARK_PURPLE + "Performer: " + TextFormatting.DARK_GREEN + performerName);
        return this;
    }

    public MessageSender blockTypeMessage(BlockType blockType) {
        this.messages.add(TextFormatting.GOLD + "Voxel: " + TextFormatting.RED + blockType.getId());
        return this;
    }

    public MessageSender blockDataMessage(BlockState blockData) {
        this.messages.add(TextFormatting.GOLD + "voxel: " + TextFormatting.RED + blockData.getAsString());
        return this;
    }

    public MessageSender replaceBlockTypeMessage(BlockType replaceBlockType) {
        this.messages.add(TextFormatting.AQUA + "Replace Material: " + TextFormatting.RED + replaceBlockType.getId());
        return this;
    }

    public MessageSender replaceBlockDataMessage(BlockState replaceBlockData) {
        this.messages.add(TextFormatting.DARK_GRAY + "Replace Data Variable: " + TextFormatting.DARK_RED + replaceBlockData.getAsString());
        return this;
    }

    public MessageSender brushSizeMessage(int brushSize) {
        this.messages.add(TextFormatting.GREEN + "Brush Size: " + TextFormatting.DARK_RED + brushSize);
        if (brushSize >= 15) {
            this.messages.add(TextFormatting.RED + "WARNING: Large brush size selected!");
        }

        return this;
    }

    public MessageSender cylinderCenterMessage(int cylinderCenter) {
        this.messages.add(TextFormatting.BLUE + "Brush Center: " + TextFormatting.DARK_RED + cylinderCenter);
        return this;
    }

    public MessageSender voxelHeightMessage(int voxelHeight) {
        this.messages.add(TextFormatting.DARK_AQUA + "Brush Height: " + TextFormatting.DARK_RED + voxelHeight);
        return this;
    }

    public MessageSender voxelListMessage(List<? extends BlockState> voxelList) {
        if (voxelList.isEmpty()) {
            this.messages.add(TextFormatting.DARK_GREEN + "No blocks selected!");
        }

        String message = voxelList.stream().map((state) -> {
            return TextFormatting.AQUA + state.getAsString();
        }).collect(Collectors.joining(TextFormatting.WHITE + ", ", TextFormatting.DARK_GREEN + "Block Types Selected: ", ""));
        this.messages.add(message);
        return this;
    }

    public MessageSender message(String message) {
        this.messages.add(message);
        return this;
    }

    public void send() {
        this.messages.forEach((s) -> {
            this.sender.sendMessage(new StringTextComponent(s), this.sender.getUniqueID());
        });
    }
}
