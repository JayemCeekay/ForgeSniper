package com.jayemceekay.forgesniper.performer.type.combo;

import com.jayemceekay.forgesniper.performer.type.AbstractPerformer;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.performer.PerformerSnipe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.world.block.BlockState;

public class ComboComboPerformer extends AbstractPerformer {
    private BlockState blockData;
    private BlockState replaceBlockData;

    public ComboComboPerformer() {
    }

    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.blockData = toolkitProperties.getBlockData();
        this.replaceBlockData = toolkitProperties.getReplaceBlockData();
    }

    public void perform(EditSession editSession, int x, int y, int z, BlockState block) {
        if (block.equals(this.replaceBlockData)) {
            this.setBlockData(editSession, x, y, z, this.blockData);
        }

    }

    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender().performerNameMessage().blockDataMessage().replaceBlockDataMessage().send();
    }
}
