package com.jayemceekay.forgesniper.events;


import com.jayemceekay.forgesniper.ForgeSniper;
import com.jayemceekay.forgesniper.sniper.Sniper;
import com.jayemceekay.forgesniper.sniper.SniperRegistry;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.item.ItemType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeSniperEventHandler {

    public ForgeSniperEventHandler() {
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        ForgeSniper.sniperRegistry.registerSniper(new Sniper(event.getPlayer()));
        ForgeSniper.sniperRegistry.getSniper(event.getPlayer().getUniqueID()).sendInfo(event.getPlayer());
    }


    @SubscribeEvent
    public void onToolRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getSide().isServer()) {
            PlayerEntity player = event.getPlayer();
            SniperRegistry sniperRegistry = ForgeSniper.sniperRegistry;
            Sniper sniper = sniperRegistry.getSniper(player.getUniqueID());
            if (sniper == null) {
                return;
            }

            if (player.isCreative() && sniper.isEnabled()) {
                ItemType usedItem = ForgeAdapter.adapt(event.getItemStack().getItem());
                BlockVector3 clickedBlock = ForgeAdapter.adapt(event.getPlayer().world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale(128)), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player)).getPos());
                sniper.snipe(player, event, usedItem, clickedBlock);
            } else if (sniper.getCurrentToolkit() != null) {
                SnipeMessenger sender = new SnipeMessenger(sniper.getCurrentToolkit().getProperties(), sniper.getCurrentToolkit().getCurrentBrushProperties(), sniper.getPlayer());
                sender.sendMessage(TextFormatting.RED + "Sniper is disabled!");
            }
        }

    }
}
