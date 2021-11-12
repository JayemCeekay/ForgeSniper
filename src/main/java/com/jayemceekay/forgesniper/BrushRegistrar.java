package com.jayemceekay.forgesniper;

import com.jayemceekay.forgesniper.brush.BrushRegistry;
import com.jayemceekay.forgesniper.brush.property.BrushProperties;
import com.jayemceekay.forgesniper.brush.type.*;
import com.jayemceekay.forgesniper.brush.type.blend.BlendBallBrush;
import com.jayemceekay.forgesniper.brush.type.blend.BlendDiscBrush;
import com.jayemceekay.forgesniper.brush.type.blend.BlendVoxelBrush;
import com.jayemceekay.forgesniper.brush.type.blend.BlendVoxelDiscBrush;
import com.jayemceekay.forgesniper.brush.type.performer.*;
import com.jayemceekay.forgesniper.brush.type.performer.disc.DiscBrush;
import com.jayemceekay.forgesniper.brush.type.performer.disc.DiscFaceBrush;
import com.jayemceekay.forgesniper.brush.type.performer.disc.VoxelDiscBrush;
import com.jayemceekay.forgesniper.brush.type.performer.disc.VoxelDiscFaceBrush;
import com.jayemceekay.forgesniper.brush.type.performer.splatter.*;


public class BrushRegistrar {
    public static final BrushProperties DEFAULT_BRUSH_PROPERTIES = BrushProperties.builder().name("Snipe").alias("s").alias("snipe").creator(SnipeBrush::new).build();
    private final BrushRegistry registry;

    public BrushRegistrar(BrushRegistry registry) {
        this.registry = registry;
        this.registerBrushes();
    }

    public void registerBrushes() {


        this.registerBallBrush();
        this.registerBlendBallBrush();
        this.registerBlendDiscBrush();
        this.registerBlendVoxelBrush();
        this.registerSnipeBrush();
        this.registerErodeBrush();
        this.registerCleanSnowBrush();
        this.registerVoxelDiscBrush();
        this.registerVoxelDiscFaceBrush();
        this.registerBlobBrush();
        this.registerBlendVoxelBrush();
        this.registerBlendVoxelDiscBrush();
        this.registerCheckerVoxelDiscBrush();
        this.registerCopyPastaBrush();
        this.registerCylinderBrush();
        this.registerDiscBrush();
        this.registerDiscFaceBrush();
        this.registerDrainBrush();
        this.registerEllipseBrush();
        this.registerEllipsoidBrush();
        this.registerEraserBrush();
        this.registerErodeBlendBrush();
        this.registerExtrudeBrush();
        this.registerFillDownBrush();
        this.registerJaggedLineBrush();
        this.registerLineBrush();
        this.registerOverlayBrush();
        this.registerPullBrush();
        this.registerRandomErodeBrush();
        this.registerRingBrush();
        this.registerSetBrush();
        this.registerSnowConeBrush();
        this.registerSplatterBallBrush();
        this.registerSplatterDiscBrush();
        this.registerSplatterOverlayBrush();
        this.registerSplatterVoxelBrush();
        this.registerSplatterVoxelDiscBrush();
        this.registerSplineBrush();
        this.registerTriangleBrush();
        this.registerUnderlayBrush();
        this.registerVoxelBrush();
    }

    private void registerBallBrush() {
        BrushProperties properties = BrushProperties.builder().name("Ball").alias("b").alias("ball").creator(BallBrush::new).build();
        this.registry.register(properties);
    }

    private void registerBlendBallBrush() {
        BrushProperties properties = BrushProperties.builder().name("Blend Ball").alias("bb").alias("blendball").alias("blend_ball").creator(BlendBallBrush::new).build();
        this.registry.register(properties);
    }

    private void registerBlendDiscBrush() {
        BrushProperties properties = BrushProperties.builder().name("Blend Disc").alias("bd").alias("blenddisc").alias("blend_disc").creator(BlendDiscBrush::new).build();
        this.registry.register(properties);
    }

    private void registerBlendVoxelBrush() {
        BrushProperties properties = BrushProperties.builder().name("Blend Voxel").alias("bv").alias("blendvoxel").alias("blend_voxel").creator(BlendVoxelBrush::new).build();
        this.registry.register(properties);
    }

    private void registerBlendVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder().name("Blend Voxel Disc").alias("bvd").alias("blendvoxeldisc").alias("blend_voxel_disc").creator(BlendVoxelDiscBrush::new).build();
        this.registry.register(properties);
    }

    private void registerBlobBrush() {
        BrushProperties properties = BrushProperties.builder().name("Blob").alias("blob").alias("splatblob").creator(BlobBrush::new).build();
        this.registry.register(properties);
    }

    private void registerCheckerVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder().name("Checker Voxel Disc").alias("cvd").alias("checkervoxeldisc").alias("checker_voxel_disc").creator(CheckerVoxelDiscBrush::new).build();
        this.registry.register(properties);
    }

    private void registerCleanSnowBrush() {
        BrushProperties properties = BrushProperties.builder().name("Clean Snow").alias("cls").alias("cleansnow").alias("clean_snow").creator(CleanSnowBrush::new).build();
        this.registry.register(properties);
    }

    private void registerCopyPastaBrush() {
        BrushProperties properties = BrushProperties.builder().name("Copy Pasta").alias("cp").alias("copypasta").alias("copy_pasta").creator(CopyPastaBrush::new).build();
        this.registry.register(properties);
    }

    private void registerCylinderBrush() {
        BrushProperties properties = BrushProperties.builder().name("Cylinder").alias("c").alias("cylinder").creator(CylinderBrush::new).build();
        this.registry.register(properties);
    }

    private void registerDiscBrush() {
        BrushProperties properties = BrushProperties.builder().name("Disc").alias("d").alias("disc").creator(DiscBrush::new).build();
        this.registry.register(properties);
    }

    private void registerDiscFaceBrush() {
        BrushProperties properties = BrushProperties.builder().name("Disc Face").alias("df").alias("discface").alias("disc_face").creator(DiscFaceBrush::new).build();
        this.registry.register(properties);
    }

    private void registerDrainBrush() {
        BrushProperties properties = BrushProperties.builder().name("Drain").alias("drain").creator(DrainBrush::new).build();
        this.registry.register(properties);
    }

    private void registerEllipseBrush() {
        BrushProperties properties = BrushProperties.builder().name("Ellipse").alias("el").alias("ellipse").creator(EllipseBrush::new).build();
        this.registry.register(properties);
    }

    private void registerEllipsoidBrush() {
        BrushProperties properties = BrushProperties.builder().name("Ellipsoid").alias("elo").alias("ellipsoid").creator(EllipsoidBrush::new).build();
        this.registry.register(properties);
    }

    private void registerEraserBrush() {
        BrushProperties properties = BrushProperties.builder().name("Eraser").alias("erase").alias("eraser").creator(EraserBrush::new).build();
        this.registry.register(properties);
    }

    private void registerErodeBlendBrush() {
        BrushProperties properties = BrushProperties.builder().name("Erode BlendBall").alias("eb").alias("erodeblend").alias("erodeblendball").creator(ErodeBlendBrush::new).build();
        this.registry.register(properties);
    }

    private void registerErodeBrush() {
        BrushProperties properties = BrushProperties.builder().name("Erode").alias("e").alias("erode").creator(ErodeBrush::new).build();
        this.registry.register(properties);
    }

    private void registerExtrudeBrush() {
        BrushProperties properties = BrushProperties.builder().name("Extrude").alias("ex").alias("extrude").creator(ExtrudeBrush::new).build();
        this.registry.register(properties);
    }

    private void registerFillDownBrush() {
        BrushProperties properties = BrushProperties.builder().name("Fill Down").alias("fd").alias("filldown").alias("fill_down").creator(FillDownBrush::new).build();
        this.registry.register(properties);
    }

    private void registerJaggedLineBrush() {
        BrushProperties properties = BrushProperties.builder().name("Jagged Line").alias("j").alias("jagged").alias("jagged_line").creator(JaggedLineBrush::new).build();
        this.registry.register(properties);
    }

    private void registerLineBrush() {
        BrushProperties properties = BrushProperties.builder().name("Line").alias("l").alias("line").creator(LineBrush::new).build();
        this.registry.register(properties);
    }

    private void registerOverlayBrush() {
        BrushProperties properties = BrushProperties.builder().name("Overlay").alias("over").alias("overlay").creator(OverlayBrush::new).build();
        this.registry.register(properties);
    }

    private void registerPullBrush() {
        BrushProperties properties = BrushProperties.builder().name("Pull").alias("pull").creator(PullBrush::new).build();
        this.registry.register(properties);
    }

    private void registerRandomErodeBrush() {
        BrushProperties properties = BrushProperties.builder().name("Random Erode").alias("re").alias("randomerode").alias("randome_rode").creator(RandomErodeBrush::new).build();
        this.registry.register(properties);
    }

    private void registerRingBrush() {
        BrushProperties properties = BrushProperties.builder().name("Ring").alias("ri").alias("ring").creator(RingBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSetBrush() {
        BrushProperties properties = BrushProperties.builder().name("Set").alias("set").creator(SetBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSnipeBrush() {
        this.registry.register(DEFAULT_BRUSH_PROPERTIES);
    }

    private void registerSnowConeBrush() {
        BrushProperties properties = BrushProperties.builder().name("Snow Cone").alias("snow").alias("snowcone").alias("snow_cone").creator(SnowConeBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSplatterBallBrush() {
        BrushProperties properties = BrushProperties.builder().name("Splatter Ball").alias("sb").alias("splatball").alias("splatter_ball").creator(SplatterBallBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSplatterDiscBrush() {
        BrushProperties properties = BrushProperties.builder().name("Splatter Disc").alias("sd").alias("splatdisc").alias("splatter_disc").creator(SplatterDiscBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSplatterOverlayBrush() {
        BrushProperties properties = BrushProperties.builder().name("Splatter Overlay").alias("sover").alias("splatteroverlay").alias("splatter_overlay").creator(SplatterOverlayBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSplatterVoxelBrush() {
        BrushProperties properties = BrushProperties.builder().name("Splatter Voxel").alias("sv").alias("splattervoxel").alias("splatter_voxel").creator(SplatterVoxelBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSplatterVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder().name("Splatter Voxel Disc").alias("svd").alias("splatvoxeldisc").alias("splatter_voxel_disc").creator(SplatterVoxelDiscBrush::new).build();
        this.registry.register(properties);
    }

    private void registerSplineBrush() {
        BrushProperties properties = BrushProperties.builder().name("Spline").alias("sp").alias("spline").creator(SplineBrush::new).build();
        this.registry.register(properties);
    }

    private void registerTriangleBrush() {
        BrushProperties properties = BrushProperties.builder().name("Triangle").alias("tri").alias("triangle").creator(TriangleBrush::new).build();
        this.registry.register(properties);
    }

    private void registerUnderlayBrush() {
        BrushProperties properties = BrushProperties.builder().name("Underlay").alias("under").alias("underlay").creator(UnderlayBrush::new).build();
        this.registry.register(properties);
    }

    private void registerVoxelBrush() {
        BrushProperties properties = BrushProperties.builder().name("Voxel").alias("v").alias("voxel").creator(VoxelBrush::new).build();
        this.registry.register(properties);
    }

    private void registerVoxelDiscBrush() {
        BrushProperties properties = BrushProperties.builder().name("Voxel Disc").alias("vd").alias("voxeldisc").alias("voxel_disc").creator(VoxelDiscBrush::new).build();
        this.registry.register(properties);
    }

    private void registerVoxelDiscFaceBrush() {
        BrushProperties properties = BrushProperties.builder().name("Voxel Disc Face").alias("vdf").alias("voxeldiscface").alias("voxel_disc_face").creator(VoxelDiscFaceBrush::new).build();
        this.registry.register(properties);
    }
}
