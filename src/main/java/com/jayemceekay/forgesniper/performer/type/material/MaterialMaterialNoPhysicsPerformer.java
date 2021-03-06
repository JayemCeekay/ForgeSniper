package com.jayemceekay.forgesniper.performer.type.material;

import com.jayemceekay.forgesniper.performer.type.AbstractPerformer;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.performer.PerformerSnipe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;

public class MaterialMaterialNoPhysicsPerformer extends AbstractPerformer {
    private BlockType type;
    private BlockType replaceType;

    public MaterialMaterialNoPhysicsPerformer() {
    }

    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.type = toolkitProperties.getBlockType();
        this.replaceType = toolkitProperties.getReplaceBlockType();
    }

    public void perform(EditSession editSession, int x, int y, int z, BlockState block) throws MaxChangedBlocksException {
        editSession.setSideEffectApplier(editSession.getSideEffectApplier().with(SideEffect.UPDATE, SideEffect.State.OFF).with(SideEffect.NEIGHBORS, SideEffect.State.OFF));

        if (block.getBlockType() == this.replaceType) {
            this.setBlockType(editSession, x, y, z, this.type);
        }

    }

    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender().performerNameMessage().blockTypeMessage().replaceBlockTypeMessage().send();
    }
}
