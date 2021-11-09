package com.jayemceekay.forgesniper.performer;

import com.jayemceekay.forgesniper.performer.property.PerformerProperties;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class PerformerRegistry {
    private final Map<String, PerformerProperties> performerProperties = new HashMap();

    public PerformerRegistry() {
    }

    public void register(PerformerProperties properties) {
        List<String> aliases = properties.getAliases();

        for (String alias : aliases) {
            this.performerProperties.put(alias, properties);
        }

    }

    @Nullable
    public PerformerProperties getPerformerProperties(String alias) {
        return this.performerProperties.get(alias);
    }

    public Map<String, PerformerProperties> getPerformerProperties() {
        return Collections.unmodifiableMap(this.performerProperties);
    }
}
