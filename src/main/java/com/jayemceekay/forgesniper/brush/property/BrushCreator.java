package com.jayemceekay.forgesniper.brush.property;

import com.jayemceekay.forgesniper.brush.Brush;

@FunctionalInterface
public interface BrushCreator {
    Brush create();
}
