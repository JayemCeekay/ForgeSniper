package com.jayemceekay.forgesniper.command;

import com.jayemceekay.forgesniper.ForgeSniper;
import com.jayemceekay.forgesniper.brush.Brush;
import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import com.jayemceekay.forgesniper.brush.type.performer.AbstractPerformerBrush;
import com.jayemceekay.forgesniper.sniper.Sniper;
import com.jayemceekay.forgesniper.sniper.ToolKit.Toolkit;
import com.jayemceekay.forgesniper.sniper.ToolKit.ToolkitProperties;
import com.jayemceekay.forgesniper.sniper.snipe.Snipe;
import com.jayemceekay.forgesniper.sniper.snipe.message.SnipeMessenger;
import com.jayemceekay.forgesniper.util.message.MessageSender;
import com.jayemceekay.forgesniper.util.message.Messenger;
import com.jayemceekay.forgesniper.util.text.NumericParser;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.input.InputParseException;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.forge.ForgeAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

import com.sk89q.worldedit.world.block.BlockTypes;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;
import org.enginehub.piston.converter.SuggestionHelper;

public class ForgeSniperCommandHandler {
    public ForgeSniperCommandHandler() {
    }

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {

        event.getDispatcher().register((Commands.literal("b").requires((commandSource) -> commandSource.hasPermissionLevel(2)))
                .then((Commands.argument("brush/size", StringArgumentType.string()).suggests((context, builder) -> {
                    try {
                        String argument = StringArgumentType.getString(context, "brush/size");
                        if (context.getArgument("brush/size", String.class).matches("\\d+")) {
                            for (int i = 0; i < 10; ++i) {
                                builder.suggest(argument + i);
                            }
                        } else {
                            SuggestionHelper.limitByPrefix(ForgeSniper.brushRegistry.getBrushProperties().keySet().stream(), context.getArgument("brush/size", String.class)).forEach(builder::suggest);
                        }
                    } catch (Exception e) {
                        SuggestionHelper.limitByPrefix(ForgeSniper.brushRegistry.getBrushProperties().keySet().stream(), "").forEach(builder::suggest);
                    }

                    return builder.buildFuture();
                }).executes((context) -> {
                    String brushName = StringArgumentType.getString(context, "brush/size");
                    Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
                    if (brushName.matches("\\d+")) {
                        try {
                            sniper.getCurrentToolkit().getProperties().setBrushSize(Integer.parseInt(brushName));
                            sniper.sendInfo(sniper.getPlayer());
                        } catch (Exception e) {
                            (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid brush size!").send();
                        }
                    } else {
                        try {
                            BrushProperties brushProperties = ForgeSniper.brushRegistry.getBrushProperties().get(brushName);
                            sniper.getCurrentToolkit().useBrush(brushProperties);
                            sniper.sendInfo(sniper.getPlayer());
                        } catch (Exception e) {
                            (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid brush name!").send();
                        }
                    }

                    return 0;
                })).then(Commands.argument("args", StringArgumentType.greedyString()).suggests((context, builder) -> {
                    try {
                        BrushProperties properties = ForgeSniper.brushRegistry.getBrushProperties(StringArgumentType.getString(context, "brush/size"));
                        if (properties == null) {
                            return builder.buildFuture();
                        }

                        Brush brush = properties.getCreator().create();

                        try {
                            String[] args = StringUtils.splitByWholeSeparatorPreserveAllTokens(StringArgumentType.getString(context, "args"), " ");
                            brush.handleCompletions(args).stream().filter((s) -> Arrays.stream(args).noneMatch((s1) -> StringUtils.startsWith(s1, s))).forEach((s) -> {
                                builder.suggest(context.getArgument("args", String.class) + s.replaceFirst(args[args.length - 1], ""));
                            });
                        } catch (Exception var5) {
                            brush.handleCompletions(new String[0]).forEach(builder::suggest);
                        }
                    } catch (Exception ignored) {
                    }

                    return builder.buildFuture();
                }).executes((ctx) -> {
                    String brushName = StringArgumentType.getString(ctx, "brush/size");
                    Sniper sniper = ForgeSniper.sniperRegistry.getSniper(ctx.getSource().asPlayer().getUniqueID());

                    try {
                        BrushProperties brushProperties = ForgeSniper.brushRegistry.getBrushProperties().get(brushName);
                        String[] args = StringUtils.splitByWholeSeparatorPreserveAllTokens(StringArgumentType.getString(ctx, "args"), " ");
                        sniper.getCurrentToolkit().useBrush(brushProperties);
                        Brush brush = sniper.getCurrentToolkit().getCurrentBrush();
                        Snipe snipe = new Snipe(sniper, sniper.getCurrentToolkit(), sniper.getCurrentToolkit().getProperties(), brushProperties, brush);
                        if (brush instanceof AbstractPerformerBrush) {
                            AbstractPerformerBrush performerBrush = (AbstractPerformerBrush) brush;
                            performerBrush.handlePerformerCommand(args, snipe, ForgeSniper.performerRegistry);
                        } else {
                            sniper.sendInfo(sniper.getPlayer());
                            brush.handleCommand(args, snipe);
                        }
                    } catch (Exception var8) {
                        (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid brush name!").send();
                    }

                    return 0;
                }))));


        event.getDispatcher().register(Commands.literal("v").requires((commandSource) -> commandSource.hasPermissionLevel(2)).then(Commands.argument("voxel", StringArgumentType.greedyString()).suggests((context, builder) -> {

            try {
                BlockType blockType = BlockTypes.get(StringArgumentType.getString(context, "voxel").split("\\[")[0]);
                String dataString = StringArgumentType.getString(context, "voxel").replace(blockType.getName(), "").replace("[", "").replace("]", "").toLowerCase(Locale.ROOT);
                com.sk89q.worldedit.command.util.SuggestionHelper.getBlockPropertySuggestions(blockType.getId(), dataString).forEach(builder::suggest);

            } catch (Exception e) {
                BlockType.REGISTRY.keySet().forEach(builder::suggest);
            }


            return builder.buildFuture();
        }).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(ForgeAdapter.adaptPlayer(context.getSource().asPlayer()));
                    BlockState state = WorldEdit.getInstance().getBlockFactory().parseFromInput(context.getArgument("voxel", String.class), parserContext).toImmutableState();
                    sniper.getCurrentToolkit().getProperties().setBlockType(state.getBlockType());
                    (new MessageSender(sniper.getPlayer())).blockTypeMessage(state.getBlockType()).send();
                } catch (InputParseException var4) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        })).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    PlayerEntity player = sniper.getPlayer();
                    BlockVector3 targetRayTraceResult = ForgeAdapter.adapt(player.world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale((double) sniper.getCurrentToolkit().getProperties().getBlockTracerRange())), BlockMode.OUTLINE, FluidMode.NONE, player)).getPos());
                    BlockState state = ForgeAdapter.adapt(player.world).getBlock(targetRayTraceResult);
                    sniper.getCurrentToolkit().getProperties().setBlockType(state.getBlockType());
                    (new MessageSender(sniper.getPlayer())).blockTypeMessage(state.getBlockType()).send();
                } catch (Exception var5) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        }));


        event.getDispatcher().register(Commands.literal("vr").requires((commandSource) -> {
            return commandSource.hasPermissionLevel(2);
        }).then(Commands.argument("voxelReplace", StringArgumentType.greedyString()).suggests((context, builder) -> {
            if (builder.getInput().contains("[")) {
                String newInput = context.getArgument("voxelReplace", String.class);
                String typeString = newInput.substring(0, newInput.indexOf("["));
                String dataString = newInput.substring(newInput.indexOf("[") + 1);
                if (BlockType.REGISTRY.get(typeString) != null) {
                    BlockType type = BlockType.REGISTRY.get(typeString);
                    HashMap<String, String> dataMap = new HashMap();
                    String[] data = dataString.split("(,+)");
                    String[] var8 = data;
                    int var9 = data.length;

                    for (int var10 = 0; var10 < var9; ++var10) {
                        String s = var8[var10];
                        String[] keyValue = s.split("=");
                        if (keyValue.length == 2) {
                            dataMap.put(keyValue[0], keyValue[1]);
                        } else {
                            dataMap.put(keyValue[0], "");
                        }
                    }

                    if (dataString.trim().endsWith(",")) {
                        type.getPropertyMap().keySet().stream().filter((sx) -> {
                            return !dataMap.containsKey(sx);
                        }).forEach((sx) -> {
                            builder.suggest(newInput.trim() + " " + sx + "=");
                        });
                    } else if (data[data.length - 1].contains("=")) {
                        String currentKey = data[data.length - 1].substring(0, data[data.length - 1].indexOf("="));
                        (type.getPropertyMap().get(currentKey)).getValues().stream().map(Object::toString).filter((sx) -> {
                            return StringUtils.startsWith(sx, dataMap.get(currentKey));
                        }).forEach((sx) -> {
                            builder.suggest(newInput + sx.replaceFirst(dataMap.get(currentKey), ""));
                        });
                    } else {
                        type.getPropertyMap().keySet().stream().filter((sx) -> {
                            return StringUtils.startsWith(sx, data[data.length - 1]);
                        }).forEach((sx) -> {
                            builder.suggest(newInput + sx.replaceFirst(data[data.length - 1], "") + "=");
                        });
                    }
                }
            } else {
                BlockType.REGISTRY.keySet().forEach(builder::suggest);
            }

            return builder.buildFuture();
        }).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(ForgeAdapter.adaptPlayer((ServerPlayerEntity) sniper.getPlayer()));
                    BlockState state = WorldEdit.getInstance().getBlockFactory().parseFromInput(context.getArgument("voxelReplace", String.class), parserContext).toImmutableState();
                    sniper.getCurrentToolkit().getProperties().setReplaceBlockType(state.getBlockType());
                    (new MessageSender(sniper.getPlayer())).replaceBlockTypeMessage(state.getBlockType()).send();
                } catch (InputParseException var4) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        })).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper((context.getSource()).asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    PlayerEntity player = sniper.getPlayer();
                    BlockVector3 targetRayTraceResult = ForgeAdapter.adapt(player.world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale((double) sniper.getCurrentToolkit().getProperties().getBlockTracerRange())), BlockMode.OUTLINE, FluidMode.NONE, player)).getPos());
                    BlockState state = ForgeAdapter.adapt(player.world).getBlock(targetRayTraceResult);
                    sniper.getCurrentToolkit().getProperties().setReplaceBlockType(state.getBlockType());
                    (new MessageSender(sniper.getPlayer())).replaceBlockTypeMessage(state.getBlockType()).send();
                } catch (Exception var5) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        }));


        event.getDispatcher().register(Commands.literal("vi").requires((commandSource) -> {
            return commandSource.hasPermissionLevel(2);
        }).then(Commands.argument("voxelCombo", StringArgumentType.greedyString()).suggests((context, builder) -> {
            return builder.buildFuture();
        }).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(ForgeAdapter.adaptPlayer((ServerPlayerEntity) sniper.getPlayer()));
                    BlockState state = WorldEdit.getInstance().getBlockFactory().parseFromInput(context.getArgument("voxelCombo", String.class), parserContext).toImmutableState();
                    sniper.getCurrentToolkit().getProperties().setBlockData(state);
                    (new MessageSender(sniper.getPlayer())).blockDataMessage(state).send();
                } catch (InputParseException var4) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        })).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    PlayerEntity player = sniper.getPlayer();
                    BlockVector3 targetRayTraceResult = ForgeAdapter.adapt(player.world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale((double) sniper.getCurrentToolkit().getProperties().getBlockTracerRange())), BlockMode.OUTLINE, FluidMode.NONE, player)).getPos());
                    BlockState state = ForgeAdapter.adapt(player.world).getBlock(targetRayTraceResult);
                    sniper.getCurrentToolkit().getProperties().setBlockData(state);
                    (new MessageSender(sniper.getPlayer())).blockDataMessage(state).send();
                } catch (Exception var5) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        }));


        event.getDispatcher().register(Commands.literal("vir").requires((commandSource) -> {
            return commandSource.hasPermissionLevel(2);
        }).then(Commands.argument("voxelComboReplace", StringArgumentType.greedyString()).suggests((context, builder) -> {
            return builder.buildFuture();
        }).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    ParserContext parserContext = new ParserContext();
                    parserContext.setActor(ForgeAdapter.adaptPlayer((ServerPlayerEntity) sniper.getPlayer()));
                    BlockState state = WorldEdit.getInstance().getBlockFactory().parseFromInput(context.getArgument("voxelComboReplace", String.class), parserContext).toImmutableState();
                    sniper.getCurrentToolkit().getProperties().setReplaceBlockData(state);
                    (new MessageSender(sniper.getPlayer())).replaceBlockDataMessage(state).send();
                } catch (InputParseException var4) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        })).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                try {
                    PlayerEntity player = sniper.getPlayer();
                    BlockVector3 targetRayTraceResult = ForgeAdapter.adapt(player.world.rayTraceBlocks(new RayTraceContext(player.getEyePosition(1.0F), player.getEyePosition(1.0F).add(player.getLookVec().scale((double) sniper.getCurrentToolkit().getProperties().getBlockTracerRange())), BlockMode.OUTLINE, FluidMode.NONE, player)).getPos());
                    BlockState state = ForgeAdapter.adapt(player.world).getBlock(targetRayTraceResult);
                    sniper.getCurrentToolkit().getProperties().setReplaceBlockData(state);
                    (new MessageSender(sniper.getPlayer())).replaceBlockDataMessage(state).send();
                } catch (Exception var5) {
                    (new MessageSender(sniper.getPlayer())).message(TextFormatting.RED + "Invalid Item!").send();
                }
            }

            return 0;
        }));


        event.getDispatcher().register((Commands.literal("d").requires((commandSource) -> {
            return commandSource.hasPermissionLevel(2);
        })).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null && sniper.getCurrentToolkit() != null) {
                sniper.getCurrentToolkit().reset();
                context.getSource().sendFeedback(new StringTextComponent(TextFormatting.AQUA + "Brush settings reset to their default values."), false);
            }

            return 0;
        }));
        event.getDispatcher().register(Commands.literal("vs").requires((commandSource) -> {
            return commandSource.hasPermissionLevel(2);
        }).then(Commands.argument("voxelsniper", StringArgumentType.greedyString()).suggests((context, builder) -> {
            builder.suggest("enable");
            builder.suggest("disable");
            builder.suggest("brushes");
            builder.suggest("range");
            builder.suggest("perf");
            builder.suggest("perflong");
            return builder.buildFuture();
        }).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            String[] arguments = StringUtils.splitByWholeSeparatorPreserveAllTokens(StringArgumentType.getString(context, "voxelsniper"), " ");
            SnipeMessenger sender = new SnipeMessenger(sniper.getCurrentToolkit().getProperties(), sniper.getCurrentToolkit().getCurrentBrushProperties(), sniper.getPlayer());
            if (arguments.length >= 1) {
                String firstArgument = arguments[0];
                Toolkit toolkit;
                if (firstArgument.equalsIgnoreCase("brushes")) {
                    toolkit = sniper == null ? null : sniper.getCurrentToolkit();
                    BrushProperties brushProperties = toolkit == null ? null : toolkit.getCurrentBrushProperties();
                    sender.sendMessage(ForgeSniper.brushRegistry.getBrushProperties().entrySet().stream().map((entry) -> {
                        return (entry.getValue() == brushProperties ? TextFormatting.GOLD : TextFormatting.GRAY) + entry.getKey();
                    }).sorted().collect(Collectors.joining(TextFormatting.WHITE + ", ", TextFormatting.AQUA + "Available brushes: ", "")));
                    return 0;
                }

                if (firstArgument.equalsIgnoreCase("range")) {
                    if (sniper == null) {
                        return 0;
                    }

                    toolkit = sniper.getCurrentToolkit();
                    if (toolkit == null) {
                        return 0;
                    }

                    ToolkitProperties toolkitProperties = toolkit.getProperties();
                    if (toolkitProperties == null) {
                        return 0;
                    }

                    Integer range;
                    if (arguments.length == 2) {
                        range = NumericParser.parseInteger(arguments[1]);
                        if (range == null) {
                            sender.sendMessage(TextFormatting.RED + "Invalid number.");
                            return 0;
                        }

                        if (range < 1) {
                            sender.sendMessage("Values less than 1 are not allowed.");
                            return 0;
                        }

                        toolkitProperties.setBlockTracerRange(range);
                    } else {
                        toolkitProperties.setBlockTracerRange(0);
                    }

                    range = toolkitProperties.getBlockTracerRange();
                    sender.sendMessage(TextFormatting.GOLD + "Distance Restriction toggled " + TextFormatting.DARK_RED + (range == null ? "off" : "on") + TextFormatting.GOLD + ". Range is " + TextFormatting.LIGHT_PURPLE + range);
                    return 0;
                }

                if (firstArgument.equalsIgnoreCase("perf")) {
                    sender.sendMessage(ForgeSniper.performerRegistry.getPerformerProperties().keySet().stream().map((alias) -> {
                        return TextFormatting.GRAY + alias;
                    }).sorted().collect(Collectors.joining(TextFormatting.WHITE + ", ", TextFormatting.AQUA + "Available performers (abbreviated): ", "")));
                    return 0;
                }

                if (firstArgument.equalsIgnoreCase("perflong")) {
                    sender.sendMessage(ForgeSniper.performerRegistry.getPerformerProperties().values().stream().map((properties) -> {
                        return TextFormatting.GRAY + properties.getName();
                    }).sorted().collect(Collectors.joining(TextFormatting.WHITE + ", ", TextFormatting.AQUA + "Available performers: ", "")));
                    return 0;
                }

                if (firstArgument.equalsIgnoreCase("enable")) {
                    sniper.setEnabled(true);
                    sender.sendMessage(TextFormatting.GREEN + "Sniper enabled!");
                } else if (firstArgument.equalsIgnoreCase("disable")) {
                    sniper.setEnabled(false);
                    sender.sendMessage(TextFormatting.RED + "Sniper disabled!");
                }
            }

            return 0;
        })).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            if (sniper != null) {
                SnipeMessenger sender = new SnipeMessenger(sniper.getCurrentToolkit().getProperties(), sniper.getCurrentToolkit().getCurrentBrushProperties(), sniper.getPlayer());
                sender.sendMessage(TextFormatting.DARK_RED + "ForgeSniper - Current Brush Settings:");
                sniper.sendInfo(sniper.getPlayer());
            }

            return 0;
        }));


        event.getDispatcher().register(Commands.literal("vc").then(Commands.argument("center", IntegerArgumentType.integer()).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            SnipeMessenger sender = new SnipeMessenger(sniper.getCurrentToolkit().getProperties(), sniper.getCurrentToolkit().getCurrentBrushProperties(), sniper.getPlayer());
            Toolkit toolkit = sniper.getCurrentToolkit();
            if (toolkit == null) {
                return 0;
            } else {
                ToolkitProperties toolkitProperties = toolkit.getProperties();
                if (toolkitProperties == null) {
                    return 0;
                } else {
                    int center;
                    try {
                        center = IntegerArgumentType.getInteger(context, "center");
                    } catch (ArrayIndexOutOfBoundsException | NumberFormatException var7) {
                        sender.sendMessage(TextFormatting.RED + "Invalid input. Must be a number.");
                        return 0;
                    }

                    toolkitProperties.setCylinderCenter(center);
                    Messenger messenger = new Messenger(sniper.getPlayer());
                    messenger.sendCylinderCenterMessage(center);
                    return 0;
                }
            }
        })));

        event.getDispatcher().register(Commands.literal("vh").then(Commands.argument("height", IntegerArgumentType.integer()).executes((context) -> {
            Sniper sniper = ForgeSniper.sniperRegistry.getSniper(context.getSource().asPlayer().getUniqueID());
            SnipeMessenger sender = new SnipeMessenger(sniper.getCurrentToolkit().getProperties(), sniper.getCurrentToolkit().getCurrentBrushProperties(), sniper.getPlayer());
            if (sniper == null) {
                return 0;
            } else {
                Toolkit toolkit = sniper.getCurrentToolkit();
                if (toolkit == null) {
                    return 0;
                } else {
                    ToolkitProperties toolkitProperties = toolkit.getProperties();
                    if (toolkitProperties == null) {
                        return 0;
                    } else {
                        int height;
                        try {
                            height = IntegerArgumentType.getInteger(context, "height");
                        } catch (ArrayIndexOutOfBoundsException | NumberFormatException var7) {
                            sender.sendMessage(TextFormatting.RED + "Invalid input. Must be a number.");
                            return 0;
                        }

                        toolkitProperties.setVoxelHeight(height);
                        Messenger messenger = new Messenger(sniper.getPlayer());
                        messenger.sendVoxelHeightMessage(height);
                        return 0;
                    }
                }
            }
        })));
    }
}
