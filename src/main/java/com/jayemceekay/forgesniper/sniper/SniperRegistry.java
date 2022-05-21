package com.jayemceekay.forgesniper.sniper;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SniperRegistry {
    private final Map<UUID, Sniper> snipers = new HashMap<>();

    public SniperRegistry() {
    }

    public Sniper getOrRegisterSniper(PlayerEntity player) {

        if(getSniper(player.getUniqueID()) == null) {
            return this.snipers.put(player.getUniqueID(), new Sniper(player));
        }
        return getSniper(player.getUniqueID());
    }

    public void removeSniper(PlayerEntity player) {
        snipers.remove(player.getUniqueID());
    }

    public Sniper getSniper(UUID uuid) {
        return this.snipers.get(uuid);
    }
}
