package com.jayemceekay.forgesniper.sniper.snipe;

import com.jayemceekay.forgesniper.brush.Brush;
import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import com.jayemceekay.forgesniper.sniper.Sniper;
import com.jayemceekay.forgesniper.sniper.ToolKit.Toolkit;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessageSender;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import net.minecraft.world.entity.player.Player;

public class Snipe {
    private final Sniper sniper;
    private final Toolkit toolkit;
    private final ToolkitProperties toolkitProperties;
    private final BrushProperties brushProperties;
    private final Brush brush;

    public Snipe(Sniper sniper, Toolkit toolkit, ToolkitProperties toolkitProperties, BrushProperties brushProperties, Brush brush) {
        this.sniper = sniper;
        this.toolkit = toolkit;
        this.toolkitProperties = toolkitProperties;
        this.brushProperties = brushProperties;
        this.brush = brush;
    }

    public SnipeMessenger createMessenger() {
        Player player = this.sniper.getPlayer();
        return new SnipeMessenger(this.toolkitProperties, this.brushProperties, player);
    }

    public SnipeMessageSender createMessageSender() {
        Player player = this.sniper.getPlayer();
        return new SnipeMessageSender(this.toolkitProperties, this.brushProperties, player);
    }

    public Sniper getSniper() {
        return this.sniper;
    }

    public Toolkit getToolkit() {
        return this.toolkit;
    }

    public ToolkitProperties getToolkitProperties() {
        return this.toolkitProperties;
    }

    public BrushProperties getBrushProperties() {
        return this.brushProperties;
    }

    public Brush getBrush() {
        return this.brush;
    }
}
