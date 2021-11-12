package com.jayemceekay.forgesniper.events;


import com.jayemceekay.forgesniper.ForgeSniper;
import com.jayemceekay.forgesniper.sniper.Sniper;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceContext;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeSniperEventHandler {

    public ForgeSniperEventHandler() {
    }

    @SubscribeEvent
    public void onPlayerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        ForgeSniper.sniperRegistry.getOrRegisterSniper(event.getPlayer());
    }


    @SubscribeEvent
    public void onToolRightClick(PlayerInteractEvent.RightClickItem event) {


        if (event.getSide().isServer()) {
            PlayerEntity player = event.getPlayer();
            Sniper sniper = ForgeSniper.sniperRegistry.getOrRegisterSniper(event.getPlayer());
            if (sniper == null) {
                return;
            }

            if (player.isCreative()) {
                if (sniper.isEnabled()) {
                    if (sniper.getCurrentToolkit() != null) {
                        ItemStack usedItem = sniper.getPlayer().getHeldItemMainhand();
                        BlockVector3 clickedBlock = ForgeAdapter.adapt(event.getPlayer().world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale(sniper.getCurrentToolkit().getProperties().getBlockTracerRange())), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player)).getPos());
                        sniper.snipe(player, event, usedItem, clickedBlock);
                    } //else {
                      //  sniper.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "You current held item is not bound to a ForgeSniper toolkit. Use /fs tool to create one."), player.getUniqueID());
                   // }
                } //else {
                   // sniper.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "ForgeSniper is disabled. Use /fs sniper enable to re-enable it."), player.getUniqueID());
               // }
            } //else {
                //sniper.getPlayer().sendMessage(new StringTextComponent(TextFormatting.RED + "You must be in creative mode to use ForgeSniper!"), player.getUniqueID());
           // }
        }

    }
}
