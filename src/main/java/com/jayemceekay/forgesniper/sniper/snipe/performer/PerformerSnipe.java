package com.jayemceekay.forgesniper.sniper.snipe.performer;

import com.jayemceekay.forgesniper.brush.Brush;
import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import com.jayemceekay.forgesniper.performer.Performer;
import com.jayemceekay.forgesniper.performer.property.PerformerProperties;
import com.jayemceekay.forgesniper.sniper.Sniper;
import com.jayemceekay.forgesniper.sniper.ToolKit.Toolkit;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.performer.message.PerformerSnipeMessageSender;
import com.jayemceekay.forgesniper.sniper.snipe.performer.message.PerformerSnipeMessenger;
import net.minecraft.entity.player.PlayerEntity;

public class PerformerSnipe extends Snipe {
    private final PerformerProperties performerProperties;
    private final Performer performer;

    public PerformerSnipe(Snipe snipe, PerformerProperties performerProperties, Performer performer) {
        this(snipe.getSniper(), snipe.getToolkit(), snipe.getToolkitProperties(), snipe.getBrushProperties(), snipe.getBrush(), performerProperties, performer);
    }

    public PerformerSnipe(Sniper sniper, Toolkit toolkit, ToolkitProperties toolkitProperties, BrushProperties brushProperties, Brush brush, PerformerProperties performerProperties, Performer performer) {
        super(sniper, toolkit, toolkitProperties, brushProperties, brush);
        this.performerProperties = performerProperties;
        this.performer = performer;
    }

    public PerformerSnipeMessenger createMessenger() {
        ToolkitProperties toolkitProperties = this.getToolkitProperties();
        BrushProperties brushProperties = this.getBrushProperties();
        Sniper sniper = this.getSniper();
        PlayerEntity player = sniper.getPlayer();
        return new PerformerSnipeMessenger(toolkitProperties, brushProperties, this.performerProperties, player);
    }

    public PerformerSnipeMessageSender createMessageSender() {
        ToolkitProperties toolkitProperties = this.getToolkitProperties();
        BrushProperties brushProperties = this.getBrushProperties();
        Sniper sniper = this.getSniper();
        PlayerEntity player = sniper.getPlayer();
        return new PerformerSnipeMessageSender(toolkitProperties, brushProperties, this.performerProperties, player);
    }

    public PerformerProperties getPerformerProperties() {
        return this.performerProperties;
    }

    public Performer getPerformer() {
        return this.performer;
    }
}
