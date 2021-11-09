package com.jayemceekay.forgesniper.sniper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SniperRegistry {
    private final Map<UUID, Sniper> snipers = new HashMap<>();

    public SniperRegistry() {
    }

    public void getOrRegisterSniper(Sniper sniper) {
        this.snipers.putIfAbsent(sniper.getPlayer().getUniqueID(), sniper);
    }

    public Sniper getSniper(UUID uuid) {
        return this.snipers.get(uuid);
    }
}
