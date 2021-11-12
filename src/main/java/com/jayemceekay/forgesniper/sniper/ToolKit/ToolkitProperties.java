package com.jayemceekay.forgesniper.sniper.ToolKit;

import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class ToolkitProperties {
    private final List<BlockState> voxelList = new ArrayList<>();
    private BlockState blockData;
    private BlockState replaceBlockData;
    private int brushSize;
    private int voxelHeight;
    private int cylinderCenter;
    private Integer blockTracerRange = 128;
    private boolean lightningEnabled;

    public ToolkitProperties() {
        this.blockData = BlockTypes.AIR.getDefaultState();
        this.replaceBlockData = BlockTypes.AIR.getDefaultState();
        this.brushSize = 3;
        this.voxelHeight = 1;
        this.cylinderCenter = 0;
    }

    public void reset() {
        this.resetBlockData();
        this.resetReplaceBlockData();
        this.brushSize = 3;
        this.voxelHeight = 1;
        this.cylinderCenter = 0;
        this.blockTracerRange = 128;
        this.lightningEnabled = false;
        this.voxelList.clear();
    }

    public void resetBlockData() {
        this.blockData = BlockTypes.AIR.getDefaultState();
    }

    public void resetReplaceBlockData() {
        this.replaceBlockData = BlockTypes.AIR.getDefaultState();
    }

    public BlockType getBlockType() {
        return this.blockData.getBlockType();
    }

    public void setBlockType(BlockType type) {
        this.blockData = type.getDefaultState();
    }

    public BlockType getReplaceBlockType() {
        return this.replaceBlockData.getBlockType();
    }

    public void setReplaceBlockType(BlockType type) {
        this.replaceBlockData = type.getDefaultState();
    }

    public void addToVoxelList(BlockState blockData) {
        this.voxelList.add(blockData);
    }

    public void removeFromVoxelList(BlockState blockData) {
        this.voxelList.remove(blockData);
    }

    public void clearVoxelList() {
        this.voxelList.clear();
    }

    public boolean isVoxelListContains(BlockState blockData) {
        return this.voxelList.contains(blockData);
    }

    public BlockState getBlockData() {
        return this.blockData;
    }

    public void setBlockData(BlockState blockData) {
        this.blockData = blockData;
    }

    public BlockState getReplaceBlockData() {
        return this.replaceBlockData;
    }

    public void setReplaceBlockData(BlockState replaceBlockData) {
        this.replaceBlockData = replaceBlockData;
    }

    public int getBrushSize() {
        return this.brushSize;
    }

    public void setBrushSize(int brushSize) {
        this.brushSize = brushSize;
    }

    public int getVoxelHeight() {
        return this.voxelHeight;
    }

    public void setVoxelHeight(int voxelHeight) {
        this.voxelHeight = voxelHeight;
    }

    public int getCylinderCenter() {
        return this.cylinderCenter;
    }

    public void setCylinderCenter(int cylinderCenter) {
        this.cylinderCenter = cylinderCenter;
    }

    @Nullable
    public Integer getBlockTracerRange() {
        return this.blockTracerRange;
    }

    public void setBlockTracerRange(Integer blockTracerRange) {
        this.blockTracerRange = blockTracerRange;
    }

    public boolean isLightningEnabled() {
        return this.lightningEnabled;
    }

    public void setLightningEnabled(boolean lightningEnabled) {
        this.lightningEnabled = lightningEnabled;
    }

    public List<BlockState> getVoxelList() {
        return Collections.unmodifiableList(this.voxelList);
    }
}
