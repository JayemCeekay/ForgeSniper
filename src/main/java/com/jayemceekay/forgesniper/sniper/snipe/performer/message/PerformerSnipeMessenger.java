package com.jayemceekay.forgesniper.sniper.snipe.performer.message;

import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import com.jayemceekay.forgesniper.performer.property.PerformerProperties;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.message.Messenger;
import net.minecraft.entity.player.PlayerEntity;

public class PerformerSnipeMessenger extends SnipeMessenger {
    private final PerformerProperties performerProperties;

    public PerformerSnipeMessenger(ToolkitProperties toolkitProperties, BrushProperties brushProperties, PerformerProperties performerProperties, PlayerEntity player) {
        super(toolkitProperties, brushProperties, player);
        this.performerProperties = performerProperties;
    }

    public void sendPerformerNameMessage() {
        Messenger messenger = this.getMessenger();
        String performerName = this.performerProperties.getName();
        messenger.sendPerformerNameMessage(performerName);
    }
}
