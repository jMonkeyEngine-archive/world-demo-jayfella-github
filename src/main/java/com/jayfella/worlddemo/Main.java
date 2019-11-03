package com.jayfella.worlddemo;

import com.jayfella.fastnoise.NoiseLayer;
import com.jayfella.jme.plotters.meshplotter.MeshPlotterSettings;
import com.jayfella.jme.worldpager.DemoWorldState;
import com.jayfella.jme.worldpager.core.CellSize;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jayfella.jme.worldpager.grid.SpriteGrid;
import com.jayfella.jme.worldpager.world.AbstractWorldState;
import com.jayfella.jme.worldpager.world.WorldSettings;
import com.jayfella.worlddemo.grass.GrassPathsRule;
import com.jayfella.worlddemo.gui.WorldSettingsState;
import com.jayfella.worlddemo.tree.PlottedModel;
import com.jayfella.worlddemo.tree.TreesGrid;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.system.AppSettings;
import com.jme3.water.WaterFilter;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

public class Main extends SimpleApplication {

    public static void main(String[] args) {

        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("My Awesome Game");
        settings.setResolution(1280, 720);

        app.setSettings(settings);
        app.start();

    }

    private AbstractWorldState world;

    @Override
    public void simpleInitApp() {

        // initialize lemur
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");


        // move the camera up a bit so we're above ground.
        cam.setLocation(new Vector3f(0, 30, 0));

        // set the sky to a nice blue
        viewPort.setBackgroundColor(new ColorRGBA(0.5f, 0.6f, 0.7f, 1.0f));

        // move about a bit quicker.
        flyCam.setMoveSpeed(100);
        flyCam.setDragToRotate(true);

        // add some light
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(-1, -1, 0).normalizeLocal());
        rootNode.addLight(directionalLight);

        rootNode.addLight(new AmbientLight(ColorRGBA.White.mult(0.1f)));

        // create our world.
        WorldSettings worldSettings = new WorldSettings();
        worldSettings.setWorldName("Test World");
        worldSettings.setSeed(123);
        worldSettings.setNumThreads(3);

        // the demo world from the world-pager only creates terrain.
        world = new DemoWorldState(worldSettings);
        stateManager.attach(world);

        // add a grass layer.
        SceneGrid grassGrid = createGrassGrid();
        world.addSceneGrid(grassGrid);

        // add a flowers layer
        SceneGrid flowersGrid = createFlowersGrid();
        world.addSceneGrid(flowersGrid);

        // add trees layer
        SceneGrid treesGrid = createTreesGrid();
        world.addSceneGrid(treesGrid);

        // Post-Processing
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        // add an ocean.
        WaterFilter waterFilter = new WaterFilter(rootNode, directionalLight.getDirection());
        waterFilter.setWaterHeight(8);
        fpp.addFilter(waterFilter);
        viewPort.addProcessor(fpp);

        // add some shadows
        DirectionalLightShadowFilter shadowFilter = new DirectionalLightShadowFilter(assetManager, 2048, 3);
        shadowFilter.setLight(directionalLight);
        shadowFilter.setShadowZExtend(256);
        shadowFilter.setShadowZFadeLength(128);
        fpp.addFilter(shadowFilter);

        // alter the view distance of the terrain grid.
        // world.getSceneGrid("Terrain").getGridSettings().setViewDistance(12);


        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        stateManager.attach(new WorldSettingsState(world));
    }

    private SceneGrid createTreesGrid() {

        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_64);
        gridSettings.setViewDistance(4);

        // tree 1
        PlottedModel tree_1 = new PlottedModel("Fir 1", assetManager.loadModel("Models/Fir1/fir1_androlo.j3o"));
        tree_1.setMinRadius(10);
        tree_1.setMaxRadius(12);
        tree_1.setMinSpaceBetween(16);
        tree_1.setMinScale(6);
        tree_1.setMaxScale(10);
        tree_1.setMinHeight(10);
        tree_1.setMaxHeight(256);
        tree_1.setLikelihood(0.1f);
        tree_1.setMaxAttempts(32);

        // tree 2
        Node oakTree = (Node) assetManager.loadModel("Models/Oak/tree_oak.j3o");
        Geometry oakTrunk = (Geometry) oakTree.getChild("oak trunk");
        oakTrunk.setMaterial(assetManager.loadMaterial("Models/Oak/Oak_Trunk.j3m"));

        Geometry oakLeaves = (Geometry) oakTree.getChild("oak leaves");
        oakLeaves.setMaterial(assetManager.loadMaterial("Models/Oak/Oak_Leaves.j3m"));

        PlottedModel tree_2 = new PlottedModel("Oak", oakTree);
        tree_2.setMinRadius(12);
        tree_2.setMaxRadius(18);
        tree_2.setMinSpaceBetween(19);
        tree_2.setMinScale(10);
        tree_2.setMaxScale(16);
        tree_2.setMinHeight(10);
        tree_2.setMaxHeight(256);
        tree_2.setLikelihood(0.3f);
        tree_2.setMaxAttempts(32);

        // tree 3
        Node mapleTree = (Node) assetManager.loadModel("Models/Maple/tree_maple.j3o");
        Geometry mapleTrunk = (Geometry) mapleTree.getChild("maple trunk");
        mapleTrunk.setMaterial(assetManager.loadMaterial("Models/Maple/Maple_Trunk.j3m"));

        Geometry mapleLeaves = (Geometry) mapleTree.getChild("maple leaves");
        mapleLeaves.setMaterial(assetManager.loadMaterial("Models/Maple/Maple_Leaves.j3m"));

        PlottedModel tree_3 = new PlottedModel("Maple", mapleTree);
        tree_3.setMinRadius(16);
        tree_3.setMaxRadius(22);
        tree_3.setMinSpaceBetween(24);
        tree_3.setMinScale(8);
        tree_3.setMaxScale(14);
        tree_3.setMinHeight(10);
        tree_3.setMaxHeight(256);
        tree_3.setLikelihood(0.4f);
        tree_3.setMaxAttempts(32);

        TreesGrid treesGrid = new TreesGrid(world, gridSettings, tree_1, tree_2, tree_3);
        treesGrid.setName("Trees");

        return treesGrid;
    }

    private SceneGrid createFlowersGrid() {

        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_32);
        gridSettings.setViewDistance(3);
        SpriteGrid flowersGrid = new SpriteGrid(world, gridSettings);
        flowersGrid.setName("Flowers");

        // create some plotter settings so we can customize the output
        MeshPlotterSettings meshPlotterSettings = new MeshPlotterSettings();
        meshPlotterSettings.setMinSize(0.2f); // the minimum size of a grass clump.
        meshPlotterSettings.setMaxSize(1.3f); // the maximum size of a grass clump.
        meshPlotterSettings.setDensity(3.6f); // how close together the grass will generate.
        meshPlotterSettings.setMinWorldHeight(10.0f); // the lowest height grass will grow (above sea level).
        meshPlotterSettings.setMinWorldHeightDeviation(1.5f); // add a bit of deviation to the min height.
        meshPlotterSettings.setMaxWorldHeight(256); // the maximum height grass will grow.
        meshPlotterSettings.setMaxWorldHeightDeviation(0.5f); // add a bit of deviation to the max height.
        flowersGrid.setPlotterSettings(meshPlotterSettings);

        flowersGrid.setMaterial(createFlowersMaterial(assetManager));

        // add some noise to the grass layer.
        NoiseLayer grassLayer_1 = new NoiseLayer("layer 1", 7654);
        grassLayer_1.setScale(new Vector2f(0.5f, 1.5f));

        NoiseLayer grassLayer_2 = new NoiseLayer("layer 1", 2345);
        grassLayer_2.setScale(new Vector2f(1.5f, 0.5f));

        flowersGrid.getNoiseGenerator().addLayer(grassLayer_1);
        flowersGrid.getNoiseGenerator().addLayer(grassLayer_2);

        // add some rules to the generator
        GrassPathsRule grassPathsRule = new GrassPathsRule(flowersGrid);
        grassPathsRule.setThreshold(0.1f);
        grassPathsRule.setSecondChance(0.1f);
        flowersGrid.setPlotterRules(grassPathsRule);

        return flowersGrid;

    }

    private Material createFlowersMaterial(AssetManager assetManager) {

        Material flowersMaterial = new Material(assetManager, "MatDefs/Vegetation-Sprite.j3md");
        flowersMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Sprite-Vegetation/sprite-flowers.png"));
        flowersMaterial.setTexture("Noise", assetManager.loadTexture("Textures/Noise/noise-x3-512.png"));
        flowersMaterial.setFloat("AlphaDiscardThreshold", 0.65f);
        flowersMaterial.setFloat("DistanceFalloff", 320);

        return flowersMaterial;

    }

    private SceneGrid createGrassGrid() {

        GridSettings gridSettings = new GridSettings();
        gridSettings.setCellSize(CellSize.Size_32);
        gridSettings.setViewDistance(8);
        SpriteGrid grassGrid = new SpriteGrid(world, gridSettings);
        grassGrid.setName("Grass");

        // create some plotter settings so we can customize the output
        MeshPlotterSettings meshPlotterSettings = new MeshPlotterSettings();

        meshPlotterSettings.setMinSize(0.2f); // the minimum size of a grass clump.
        meshPlotterSettings.setMaxSize(1.3f); // the maximum size of a grass clump.

        meshPlotterSettings.setDensity(0.2f); // how close together the grass will generate.

        meshPlotterSettings.setMinWorldHeight(10.0f); // the lowest height grass will grow (above sea level).
        meshPlotterSettings.setMinWorldHeightDeviation(1.5f); // add a bit of deviation to the min height.

        meshPlotterSettings.setMaxWorldHeight(256); // the maximum height grass will grow.
        meshPlotterSettings.setMaxWorldHeightDeviation(0.5f); // add a bit of deviation to the max height.

        grassGrid.setPlotterSettings(meshPlotterSettings);

        grassGrid.setMaterial(createGrassMaterial(assetManager));

        // add some noise to the grass layer.
        NoiseLayer grassLayer_1 = new NoiseLayer("layer 1", 543);
        grassLayer_1.setScale(new Vector2f(2.5f, 1.5f));

        NoiseLayer grassLayer_2 = new NoiseLayer("layer 1", 432);
        grassLayer_2.setScale(new Vector2f(1.5f, 2.5f));

        grassGrid.getNoiseGenerator().addLayer(grassLayer_1);
        grassGrid.getNoiseGenerator().addLayer(grassLayer_2);

        // add some rules to the generator
        GrassPathsRule grassPathsRule = new GrassPathsRule(grassGrid);
        grassPathsRule.setThreshold(0.2f);
        grassPathsRule.setSecondChance(0.2f);
        grassGrid.setPlotterRules(grassPathsRule);

        return grassGrid;
    }

    private Material createGrassMaterial(AssetManager assetManager) {

        Material grassMaterial = new Material(assetManager, "MatDefs/Vegetation-Sprite.j3md");
        grassMaterial.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Sprite-Vegetation/sprite-grass.png"));
        grassMaterial.setTexture("Noise", assetManager.loadTexture("Textures/Noise/noise-x3-512.png"));
        grassMaterial.setFloat("AlphaDiscardThreshold", 0.65f);
        grassMaterial.setFloat("DistanceFalloff", 512);

        return grassMaterial;
    }

    @Override
    public void simpleUpdate(float tpf) {

        // update the world with our location.
        world.setFollower(cam.getLocation());
    }

}