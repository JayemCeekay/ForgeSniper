package com.jayemceekay.forgesniper.sniper;

import com.jayemceekay.forgesniper.brush.Brush;
import com.jayemceekay.forgesniper.brush.PerformerBrush;
import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolAction;
import com.jayemceekay.forgesniper.sniper.ToolKit.Toolkit;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.util.material.Materials;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sniper {
    private static final String DEFAULT_TOOLKIT_NAME = "default";
    private final List<Toolkit> toolkits = new ArrayList<>();
    private final Player player;
    private boolean enabled = false;

    public Sniper(Player player) {
        this.player = player;
        Toolkit defaultToolkit = this.createDefaultToolkit();
        this.toolkits.add(defaultToolkit);
    }

    private Toolkit createDefaultToolkit() {
        Toolkit toolkit = new Toolkit("default");
        CompoundTag tag = new CompoundTag();
        tag.putString("toolkit", "default");
        ItemStack arrow = new ItemStack(Items.ARROW);
        ItemStack wand = new ItemStack(Items.GUNPOWDER);
        arrow.setTag(tag);
        wand.setTag(tag);
        toolkit.addToolAction(arrow.copy(), ToolAction.ARROW);
        toolkit.addToolAction(wand.copy(), ToolAction.WAND);
        this.updateItemStackInfo(toolkit);
        return toolkit;
    }

    public Player getPlayer() {
        return this.player;
    }

    public UUID getUuid() {
        return this.player.getUUID();
    }

    @Nullable
    public Toolkit getCurrentToolkit() {
        Player player = this.getPlayer();
        ItemStack itemInHand = player.getMainHandItem();
        return getToolkit(itemInHand);
    }

    public void addToolkit(Toolkit toolkit) {
        this.toolkits.add(toolkit);
    }

    @Nullable
    public Toolkit getToolkit(ItemStack itemType) {
        if (itemType.hasTag() && itemType.getTag().contains("toolkit")) {
            return this.toolkits.stream().filter((toolkit) -> toolkit.hasToolAction(itemType)).findFirst().orElse(null);
        }
        return null;
    }

    @Nullable
    public Toolkit getToolkit(String toolkitName) {
        return this.toolkits.stream().filter((toolkit) -> toolkitName.equals(toolkit.getToolkitName())).findFirst().orElse(null);
    }

    public void removeToolkit(Toolkit toolkit) {
        this.toolkits.remove(toolkit);
    }

    public boolean snipe(Player player, PlayerInteractEvent action, ItemStack usedItem, @Nullable BlockVector3 clickedBlock) {
        if (this.toolkits.isEmpty()) {
            return false;
        } else {
            Toolkit toolkit = this.getToolkit(usedItem);
            if (toolkit == null) {
                player.sendMessage(new TextComponent(TextColor.fromLegacyFormat(ChatFormatting.RED) + "No toolkit found for this item."), player.getUUID());
                return false;
            } else {
                ToolAction toolAction = toolkit.getToolAction(usedItem);
                if (toolAction == null) {
                    player.sendMessage(new TextComponent(TextColor.fromLegacyFormat(ChatFormatting.RED) + "No tool action found for this item."), player.getUUID());
                    return false;
                } else {
                    BrushProperties currentBrushProperties = toolkit.getCurrentBrushProperties();
                    this.snipeOnCurrentThread(player, action, clickedBlock, toolkit, toolAction, currentBrushProperties);
                    return true;
                }
            }
        }
    }

    public synchronized void snipeOnCurrentThread(Player player, PlayerInteractEvent action, @Nullable BlockVector3 clickedBlock, Toolkit toolkit, ToolAction toolAction, BrushProperties currentBrushProperties) {

        LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName().getString());

        if (session != null) {
            EditSession editSession = session.createEditSession(ForgeAdapter.adaptPlayer((ServerPlayer) player));

            try {
                ToolkitProperties toolkitProperties = toolkit.getProperties();
                BlockVector3 rayTraceTargetBlock = null;
                BlockVector3 rayTraceLastBlock = null;
                BlockVector3 targetBlock;
                if (clickedBlock == null) {
                    targetBlock = ForgeAdapter.adapt(player.level.clip(new ClipContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookAngle().scale((double) this.getCurrentToolkit().getProperties().getBlockTracerRange())), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos());
                    if (targetBlock != null) {
                        rayTraceTargetBlock = targetBlock;
                    }
                }
                Direction direction = player.level.clip(new ClipContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookAngle().scale(128)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getDirection();
                BlockVector3 lastRayTraceResult = ForgeAdapter.adapt(player.level.clip(new ClipContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookAngle().scale((double) this.getCurrentToolkit().getProperties().getBlockTracerRange())), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getBlockPos().offset(direction.getNormal()));
                if (lastRayTraceResult != null) {
                    rayTraceLastBlock = lastRayTraceResult;
                }

                targetBlock = clickedBlock == null ? rayTraceTargetBlock : clickedBlock;
                if (action instanceof RightClickItem) {
                    if (targetBlock.getY() < editSession.getWorld().getMinY() && Materials.isEmpty(editSession.getBlock(BlockVector3.at(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ())).getBlockType())) {
                        player.sendMessage(new TextComponent(TextColor.fromLegacyFormat(ChatFormatting.RED) + "Snipe target block must be visible."), player.getUUID());
                        return;
                    }

                    Brush currentBrush = toolkit.getCurrentBrush();
                    if (currentBrush == null) {
                        return;
                    }

                    Snipe snipe = new Snipe(this, toolkit, toolkitProperties, currentBrushProperties, currentBrush);
                    if (currentBrush instanceof PerformerBrush) {
                        PerformerBrush performerBrush = (PerformerBrush) currentBrush;
                        performerBrush.initialize(snipe);
                    }

                    currentBrush.perform(snipe, toolAction, editSession, targetBlock, rayTraceLastBlock);
                }
            } finally {
                session.remember(editSession);
                editSession.flushSession();
                editSession.close();
            }
        }
    }

    public void sendInfo(Player sender) {
        Toolkit toolkit = this.getCurrentToolkit();
        if (toolkit == null) {
            sender.sendMessage(new TextComponent("Current toolkit: none"), sender.getUUID());
        } else {
            sender.sendMessage(new TextComponent("Current toolkit: " + toolkit.getToolkitName()), sender.getUUID());
            BrushProperties brushProperties = toolkit.getCurrentBrushProperties();
            Brush brush = toolkit.getCurrentBrush();
            if (brush == null) {
                sender.sendMessage(new TextComponent("No brush selected."), sender.getUUID());
            } else {
                ToolkitProperties toolkitProperties = toolkit.getProperties();
                Snipe snipe = new Snipe(this, toolkit, toolkitProperties, brushProperties, brush);
                brush.sendInfo(snipe);
                if (brush instanceof PerformerBrush) {
                    PerformerBrush performer = (PerformerBrush) brush;
                    performer.sendPerformerInfo(snipe);
                }

            }
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Toolkit> getToolkits() {
        return this.toolkits;
    }

    public void updateItemStackInfo(Toolkit toolkit) {
        toolkit.getToolActions().forEach((toolMaterial, action) -> {

            if (this.getPlayer().getInventory().contains(toolMaterial)) {

                int index = getItemSlot(toolMaterial);

                this.getPlayer().getInventory().removeItem(toolMaterial);

                toolMaterial.setHoverName(new TextComponent(ChatFormatting.AQUA + "" + ChatFormatting.BOLD + "" + ChatFormatting.UNDERLINE + "" + toolkit.getCurrentBrushProperties().getName() + "-" + ChatFormatting.ITALIC + action.toString()));
                CompoundTag display = toolMaterial.getOrCreateTagElement("display");
                ListTag tag = display.getList("Lore", 8);
                tag.clear();
                tag.add(StringTag.valueOf(TextComponent.Serializer.toJson(new TextComponent(ChatFormatting.DARK_AQUA + "Size: " + toolkit.getProperties().getBrushSize()))));
                toolkit.getCurrentBrush().getSettings().forEach((setting, value) -> {
                    tag.add(StringTag.valueOf(TextComponent.Serializer.toJson(new TextComponent(ChatFormatting.DARK_AQUA + setting + ": " + value))));
                });
                tag.add(StringTag.valueOf(TextComponent.Serializer.toJson(new TextComponent(ChatFormatting.BLUE + "Material: " + toolkit.getProperties().getBlockData()))));
                tag.add(StringTag.valueOf(TextComponent.Serializer.toJson(new TextComponent(ChatFormatting.BLUE + "Replacing: " + toolkit.getProperties().getReplaceBlockData().toString()))));
                display.put("Lore", tag);

                if (index >= 0) {
                    this.getPlayer().getInventory().setItem(index, toolMaterial.copy());
                }

            }
        });
    }

    public int getItemSlot(ItemStack itemStack) {
        for (int i = 0; i < this.getPlayer().getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).sameItemStackIgnoreDurability(itemStack) && (player.getInventory().getItem(i).hasTag() && player.getInventory().getItem(i).getTag().contains("toolkit"))) {
                return i;
            }
        }
        return -1;
    }

    public void cleanToolkits() {
        this.toolkits.forEach(toolkit -> {
            if (toolkit.getToolActions().size() == 0) {
                this.removeToolkit(toolkit);
                this.player.sendMessage(new TextComponent("Removed toolkit " + toolkit.getToolkitName() + " because it has no actions."), this.player.getUUID());
            }
        });
    }
}
