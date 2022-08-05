package com.jayemceekay.forgesniper.performer.type.material;

import com.jayemceekay.forgesniper.performer.type.AbstractPerformer;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.performer.PerformerSnipe;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;

import java.util.HashMap;
import java.util.Map;

public class MaterialMaterialPerformer extends AbstractPerformer {
    private BlockState type;
    private BlockState replaceType;

    public MaterialMaterialPerformer() {
    }

    public void initialize(PerformerSnipe snipe) {
        ToolkitProperties toolkitProperties = snipe.getToolkitProperties();
        this.type = toolkitProperties.getBlockData();
        this.replaceType = toolkitProperties.getReplaceBlockData();
    }

    public void perform(EditSession editSession, int x, int y, int z, BlockState block) throws MaxChangedBlocksException {
        if (block.getBlockType() == this.replaceType.getBlockType()) {

            Map<Property<?>, Object> stateMap = new HashMap<>(type.getStates());
            block.getStates().forEach((key, value) -> {
                if (type.getStates().containsKey(key)) {
                    stateMap.put(key, value);
                }
            });

            this.setBlockData(editSession, x, y, z, this.type.getBlockType().getState(stateMap));
        }

    }

    public void sendInfo(PerformerSnipe snipe) {
        snipe.createMessageSender().performerNameMessage().blockTypeMessage().replaceBlockTypeMessage().send();
    }
}
