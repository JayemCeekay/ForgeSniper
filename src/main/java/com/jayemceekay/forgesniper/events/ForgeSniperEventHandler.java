package com.jayemceekay.forgesniper.events;


import com.jayemceekay.forgesniper.ForgeSniper;
import com.jayemceekay.forgesniper.sniper.Sniper;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
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
            Player player = event.getPlayer();
            Sniper sniper = ForgeSniper.sniperRegistry.getOrRegisterSniper(event.getPlayer());
            if (sniper == null) {
                return;
            }

            if (player.isCreative()) {
                if (sniper.isEnabled()) {
                    if (sniper.getCurrentToolkit() != null) {
                        ItemStack usedItem = sniper.getPlayer().getMainHandItem();
                        BlockVector3 clickedBlock = ForgeAdapter.adapt(player.level.clip(new ClipContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookAngle().scale((double) sniper.getCurrentToolkit().getProperties().getBlockTracerRange())), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos());
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
