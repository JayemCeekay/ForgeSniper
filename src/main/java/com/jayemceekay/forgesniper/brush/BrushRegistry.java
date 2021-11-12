package com.jayemceekay.forgesniper.brush;

import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class BrushRegistry {
    public final Map<String, BrushProperties> brushProperties = new HashMap();

    public BrushRegistry() {
    }

    public void register(BrushProperties brushProperties) {
        List<String> aliases = brushProperties.getAliases();

        for (String s : aliases) {
            this.brushProperties.put(s, brushProperties);
        }

    }

    @Nullable
    public BrushProperties getBrushProperties(String alias) {
        return this.brushProperties.get(alias);
    }

    public Map<String, BrushProperties> getBrushProperties() {
        return Collections.unmodifiableMap(this.brushProperties);
    }
}
