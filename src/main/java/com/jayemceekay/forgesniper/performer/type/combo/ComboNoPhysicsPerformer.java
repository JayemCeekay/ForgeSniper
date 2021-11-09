package com.jayemceekay.forgesniper.performer.type.combo;

import com.jayemceekay.forgesniper.performer.type.AbstractPerformer;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.performer.PerformerSnipe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.world.block.BlockState;

public class ComboNoPhysicsPerformer extends AbstractPerformer {
    private BlockState blockData;

    public ComboNoPhysicsPerformer() {
    }

    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.blockData = toolkitProperties.getBlockData();
    }

    public void perform(EditSession editSession, int x, int y, int z, BlockState block) {

        try {
            this.setBlockData(editSession, x, y, z, this.blockData);
        } catch (MaxChangedBlocksException var7) {
            var7.printStackTrace();
        }

    }

    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender().performerNameMessage().blockTypeMessage().blockDataMessage().send();
    }
}
