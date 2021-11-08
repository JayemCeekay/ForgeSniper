package com.jayemceekay.forgesniper.brush;

import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
            String alias = s;
            this.brushProperties.put(alias, brushProperties);
        }

    }

    @Nullable
    public BrushProperties getBrushProperties(String alias) {
        return (BrushProperties)this.brushProperties.get(alias);
    }

    public Map<String, BrushProperties> getBrushProperties() {
        return Collections.unmodifiableMap(this.brushProperties);
    }
}
