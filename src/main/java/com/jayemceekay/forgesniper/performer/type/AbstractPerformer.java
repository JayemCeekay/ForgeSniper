package com.jayemceekay.forgesniper.performer.type;

import com.jayemceekay.forgesniper.performer.Performer;
import com.jayemceekay.forgesniper.performer.property.PerformerProperties;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;

public abstract class AbstractPerformer implements Performer {
    private PerformerProperties properties;

    public AbstractPerformer() {
    }

    public void setBlockType(EditSession editSession, int x, int y, int z, BlockType type) throws MaxChangedBlocksException {
        this.setBlockData(editSession, x, y, z, type.getDefaultState());
    }

    public void setBlockData(EditSession editSession, int x, int y, int z, BlockState blockState) throws MaxChangedBlocksException {
        editSession.setBlock(BlockVector3.at(x, y, z), blockState);
    }

    public PerformerProperties getProperties() {
        return this.properties;
    }

    public void setProperties(PerformerProperties properties) {
        this.properties = properties;
    }

    public void loadProperties() {
    }
}
