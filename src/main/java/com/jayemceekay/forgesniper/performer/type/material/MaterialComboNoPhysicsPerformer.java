package com.jayemceekay.forgesniper.performer.type.material;

import com.jayemceekay.forgesniper.performer.type.AbstractPerformer;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.performer.PerformerSnipe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.util.SideEffect;
import com.sk89q.worldedit.world.block.BlockState;

public class MaterialComboNoPhysicsPerformer extends AbstractPerformer {
    private BlockState blockData;
    private BlockState replaceBlockData;

    public MaterialComboNoPhysicsPerformer() {
    }

    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.blockData = toolkitProperties.getBlockData();
        this.replaceBlockData = toolkitProperties.getReplaceBlockData();
    }

    public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
        editSession.setSideEffectApplier(editSession.getSideEffectApplier().with(SideEffect.UPDATE, SideEffect.State.OFF).with(SideEffect.NEIGHBORS, SideEffect.State.OFF));

        if (block.equals(this.replaceBlockData)) {
            this.setBlockType(editSession, x, y, z, this.blockData.getBlockType());
        }

    }

    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender().performerNameMessage().blockTypeMessage().replaceBlockDataMessage().send();
    }
}
