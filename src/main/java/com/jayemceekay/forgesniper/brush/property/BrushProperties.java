package com.jayemceekay.forgesniper.brush.property;

import java.util.List;

public class BrushProperties {
    private final String name;
    private final List<String> aliases;
    private final BrushCreator creator;

    BrushProperties(String name, List<String> aliases, BrushCreator creator) {
        this.name = name;
        this.aliases = aliases;
        this.creator = creator;
    }

    public static BrushPropertiesBuilder builder() {
        return new BrushPropertiesBuilder();
    }

    public String getName() {
        return this.name;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public BrushCreator getCreator() {
        return this.creator;
    }
}
