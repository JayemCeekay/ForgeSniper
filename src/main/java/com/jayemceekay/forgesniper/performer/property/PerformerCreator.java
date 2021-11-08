package com.jayemceekay.forgesniper.performer.property;

import com.jayemceekay.forgesniper.performer.Performer;

@FunctionalInterface
public interface PerformerCreator {

    Performer create();

}