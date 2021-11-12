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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Sniper {
    private static final String DEFAULT_TOOLKIT_NAME = "default";
    private final List<Toolkit> toolkits = new ArrayList<>();
    private boolean enabled = true;
    private final PlayerEntity player;

    public Sniper(PlayerEntity player) {
        this.player = player;
        Toolkit defaultToolkit = this.createDefaultToolkit();
        this.toolkits.add(defaultToolkit);
    }

    private Toolkit createDefaultToolkit() {
        Toolkit toolkit = new Toolkit("default");
        ItemStack arrow = new ItemStack(Items.ARROW);
        ItemStack wand = new ItemStack(Items.GUNPOWDER);
        toolkit.addToolAction(arrow.copy(), ToolAction.ARROW);
        toolkit.addToolAction(wand.copy(), ToolAction.WAND);
        this.updateItemStackInfo(toolkit);
        return toolkit;
    }

    public PlayerEntity getPlayer() {
        return this.player;
    }

    @Nullable
    public Toolkit getCurrentToolkit() {
        PlayerEntity player = this.getPlayer();
        ItemStack itemInHand = player.getHeldItemMainhand();
        return getToolkit(itemInHand);
    }

    public void addToolkit(Toolkit toolkit) {
        this.toolkits.add(toolkit);
    }

    @Nullable
    public Toolkit getToolkit(ItemStack itemType) {
        return this.toolkits.stream().filter((toolkit) -> toolkit.hasToolAction(itemType)).findFirst().orElse(null);
    }

    @Nullable
    public Toolkit getToolkit(String toolkitName) {
        return this.toolkits.stream().filter((toolkit) -> toolkitName.equals(toolkit.getToolkitName())).findFirst().orElse(null);
    }

    public void removeToolkit(Toolkit toolkit) {
        this.toolkits.remove(toolkit);
    }

    public boolean snipe(PlayerEntity player, PlayerInteractEvent action, ItemStack usedItem, @Nullable BlockVector3 clickedBlock) {
        if (this.toolkits.isEmpty()) {
            return false;
        } else {
            Toolkit toolkit = this.getToolkit(usedItem);
            if (toolkit == null) {
                player.sendMessage(new StringTextComponent(TextFormatting.RED + "No toolkit found for this item."), player.getUniqueID());
                return false;
            } else {
                ToolAction toolAction = toolkit.getToolAction(usedItem);
                if (toolAction == null) {
                    player.sendMessage(new StringTextComponent(TextFormatting.RED + "No tool action found for this item."), player.getUniqueID());
                    return false;
                } else {
                    BrushProperties currentBrushProperties = toolkit.getCurrentBrushProperties();
                    this.snipeOnCurrentThread(player, action, clickedBlock, toolkit, toolAction, currentBrushProperties);
                    return true;
                }
            }
        }
    }

    public synchronized void snipeOnCurrentThread(PlayerEntity player, PlayerInteractEvent action, @Nullable BlockVector3 clickedBlock, Toolkit toolkit, ToolAction toolAction, BrushProperties currentBrushProperties) {

        LocalSession session = WorldEdit.getInstance().getSessionManager().findByName(player.getName().getString());

        if (session != null) {
            EditSession editSession = session.createEditSession(ForgeAdapter.adaptPlayer((ServerPlayerEntity) player));

            try {
                ToolkitProperties toolkitProperties = toolkit.getProperties();
                BlockVector3 rayTraceTargetBlock = null;
                BlockVector3 rayTraceLastBlock = null;
                BlockVector3 targetBlock;
                if (clickedBlock == null) {
                    targetBlock = ForgeAdapter.adapt(player.world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale((double) this.getCurrentToolkit().getProperties().getBlockTracerRange())), BlockMode.OUTLINE, FluidMode.NONE, player)).getPos());
                    if (targetBlock != null) {
                        rayTraceTargetBlock = targetBlock;
                    }
                }

                Direction lastRayTraceResultFace = player.world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale((double) this.getCurrentToolkit().getProperties().getBlockTracerRange())), BlockMode.OUTLINE, FluidMode.NONE, player)).getFace();
                BlockVector3 lastRayTraceResult = ForgeAdapter.adapt(player.world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale((double) this.getCurrentToolkit().getProperties().getBlockTracerRange())), BlockMode.OUTLINE, FluidMode.NONE, player)).getPos().offset(lastRayTraceResultFace));
                if (lastRayTraceResult != null) {
                    rayTraceLastBlock = lastRayTraceResult;
                }

                targetBlock = clickedBlock == null ? rayTraceTargetBlock : clickedBlock;
                if (action instanceof RightClickItem) {
                    if (targetBlock.getY() < editSession.getWorld().getMinY() && Materials.isEmpty(editSession.getBlock(BlockVector3.at(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ())).getBlockType())) {
                        player.sendMessage(new StringTextComponent(TextFormatting.RED + "Snipe target block must be visible."), player.getUniqueID());
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

    public void sendInfo(PlayerEntity sender) {
        Toolkit toolkit = this.getCurrentToolkit();
        if (toolkit == null) {
            sender.sendMessage(new StringTextComponent("Current toolkit: none"), sender.getUniqueID());
        } else {
            sender.sendMessage(new StringTextComponent("Current toolkit: " + toolkit.getToolkitName()), sender.getUniqueID());
            BrushProperties brushProperties = toolkit.getCurrentBrushProperties();
            Brush brush = toolkit.getCurrentBrush();
            if (brush == null) {
                sender.sendMessage(new StringTextComponent("No brush selected."), sender.getUniqueID());
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

    public UUID getUuid() {
        return this.player.getUniqueID();
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

            if (this.getPlayer().inventory.hasItemStack(toolMaterial)) {

                int index = getItemSlot(toolMaterial);

                this.getPlayer().inventory.deleteStack(toolMaterial);

                toolMaterial.setDisplayName(new StringTextComponent(TextFormatting.AQUA + "" + TextFormatting.BOLD + "" + TextFormatting.UNDERLINE + ""  + toolkit.getCurrentBrushProperties().getName() + "-" + TextFormatting.ITALIC + action.toString()));
                CompoundNBT display = toolMaterial.getOrCreateChildTag("display");
                ListNBT tag = display.getList("Lore", 8);
                tag.clear();
                tag.add( StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.DARK_AQUA + "Size: " + toolkit.getProperties().getBrushSize()))));
                toolkit.getCurrentBrush().getSettings().forEach((setting, value) -> {
                    tag.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.DARK_AQUA + setting + ": " + value))));
                        });
                tag.add( StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.BLUE + "Material: " + toolkit.getProperties().getBlockData()))));
                tag.add( StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(TextFormatting.BLUE + "Replacing: " + toolkit.getProperties().getReplaceBlockData().toString()))));
                display.put("Lore", tag);

                this.getPlayer().inventory.setInventorySlotContents(index, toolMaterial.copy());

            }
        });
    }

    public int getItemSlot(ItemStack itemStack) {
        for (int i = 0; i < this.getPlayer().inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i).isItemEqualIgnoreDurability(itemStack)) {
                return i;
            }
        }
        return -1;
    }
}
