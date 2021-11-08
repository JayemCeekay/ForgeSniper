package com.jayemceekay.forgesniper.sniper.ToolKit;

import javax.annotation.Nullable;
import java.util.Arrays;


public enum ToolAction {
    ARROW,
    GUNPOWDER;

    private ToolAction() {
    }

    @Nullable
    public static ToolAction getToolAction(String name) {
        return (ToolAction) Arrays.stream(values()).filter((toolAction) -> {
            return name.equalsIgnoreCase(toolAction.name());
        }).findFirst().orElse(null);
    }
}

