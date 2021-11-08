package com.jayemceekay.forgesniper.sniper.snipe.performer.message;


import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import com.jayemceekay.forgesniper.performer.property.PerformerProperties;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessageSender;
import com.jayemceekay.forgesniper.util.message.MessageSender;
import net.minecraft.entity.player.PlayerEntity;

public class PerformerSnipeMessageSender extends SnipeMessageSender {
    private final PerformerProperties performerProperties;

    public PerformerSnipeMessageSender(ToolkitProperties toolkitProperties, BrushProperties brushProperties, PerformerProperties performerProperties, PlayerEntity player) {
        super(toolkitProperties, brushProperties, player);
        this.performerProperties = performerProperties;
    }

    public PerformerSnipeMessageSender performerNameMessage() {
        MessageSender messageSender = this.getMessageSender();
        String performerName = this.performerProperties.getName();
        messageSender.performerNameMessage(performerName);
        return this;
    }
}
