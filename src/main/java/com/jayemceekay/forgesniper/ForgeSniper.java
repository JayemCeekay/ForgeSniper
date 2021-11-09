package com.jayemceekay.forgesniper;

import com.jayemceekay.forgesniper.brush.BrushRegistry;
import com.jayemceekay.forgesniper.command.ForgeSniperCommandHandler;
import com.jayemceekay.forgesniper.events.ForgeSniperEventHandler;
import com.jayemceekay.forgesniper.performer.PerformerRegistry;
import com.jayemceekay.forgesniper.sniper.SniperRegistry;
import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("forgesniper")
public class ForgeSniper {
    public static SniperRegistry sniperRegistry = new SniperRegistry();
    public static BrushRegistry brushRegistry;
    public static PerformerRegistry performerRegistry;
    public static final Logger LOGGER = LogManager.getLogger();

    public ForgeSniper() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(ForgeSniperCommandHandler.class);
        LOGGER.info("Loading Brushes");
        brushRegistry = this.loadBrushRegistry();
        LOGGER.info("Loaded " + brushRegistry.getBrushProperties().values().stream().distinct().count() + " Brushes");
        LOGGER.info("Loading Performers");
        performerRegistry = this.loadPerformerRegistry();
        LOGGER.info("Registering ForgeSniper Events");
        MinecraftForge.EVENT_BUS.register(new ForgeSniperEventHandler());
        LOGGER.info("ForgeSniper Events Registered");
    }

    private void doClientStuff(FMLClientSetupEvent event) {
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
    }

    private void processIMC(InterModProcessEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

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
