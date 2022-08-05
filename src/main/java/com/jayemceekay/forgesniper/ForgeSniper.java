package com.jayemceekay.forgesniper;

import com.jayemceekay.forgesniper.brush.BrushRegistry;
import com.jayemceekay.forgesniper.command.ForgeSniperCommandHandler;
import com.jayemceekay.forgesniper.events.ForgeSniperEventHandler;
import com.jayemceekay.forgesniper.performer.PerformerRegistry;
import com.jayemceekay.forgesniper.sniper.SniperRegistry;
import com.sk89q.worldedit.util.formatting.text.format.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("forgesniper")
public class ForgeSniper {
    public static final Logger LOGGER = LogManager.getLogger();
    public static SniperRegistry sniperRegistry = new SniperRegistry();
    public static BrushRegistry brushRegistry;
    public static PerformerRegistry performerRegistry;
    public static ResourceLocation FORGESNIPER_CONFIG_FOLDER = new ResourceLocation("forgesniper", FMLPaths.CONFIGDIR.relative().toString());

    public ForgeSniper() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ForgeSniperCommandHandler.class);
        LOGGER.info(TextColor.GREEN + "Loading Brushes");
        brushRegistry = this.loadBrushRegistry();
        LOGGER.info("Loaded " + brushRegistry.getBrushProperties().keySet().size() + " Brushes");
        LOGGER.info("Loading Performers");
        performerRegistry = this.loadPerformerRegistry();
        LOGGER.info("Registering ForgeSniper Events");
        MinecraftForge.EVENT_BUS.register(new ForgeSniperEventHandler());
        LOGGER.info("ForgeSniper Events Registered");
    }

    private BrushRegistry loadBrushRegistry() {
        BrushRegistry brushRegistry = new BrushRegistry();
        new BrushRegistrar(brushRegistry);
        return brushRegistry;
    }

    private PerformerRegistry loadPerformerRegistry() {
        PerformerRegistry performerRegistry = new PerformerRegistry();
        new PerformerRegistrar(performerRegistry);
        return performerRegistry;
    }
}
