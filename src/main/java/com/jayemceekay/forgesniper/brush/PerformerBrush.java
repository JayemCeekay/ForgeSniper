package com.jayemceekay.forgesniper.brush;

import com.jayemceekay.forgesniper.performer.PerformerRegistry;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;

public interface PerformerBrush extends Brush {
    void handlePerformerCommand(String[] parameters, Snipe snipe, PerformerRegistry performerRegistry);

    void initialize(Snipe snipe);

    void sendPerformerInfo(Snipe snipe);
}
