package com.jayfella.worlddemo.gui;

import com.jayfella.fastnoise.LayeredNoise;
import com.jayfella.fastnoise.NoiseLayer;
import com.jayfella.jme.plotters.meshplotter.MeshPlotterSettings;
import com.jayfella.jme.worldpager.DemoWorldState;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jayfella.jme.worldpager.grid.SpriteGrid;
import com.jayfella.jme.worldpager.grid.TerrainGrid;
import com.jayfella.jme.worldpager.world.AbstractWorldState;
import com.jayfella.worlddemo.tree.PlottedModel;
import com.jayfella.worlddemo.tree.TreesGrid;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.TabbedPanel;
import com.simsilica.lemur.props.PropertyPanel;

public class WorldSettingsState extends BaseAppState {

    private final AbstractWorldState world;
    private TabbedPanel tabbedPanel;

    private Material terrainMaterial;
    private Material grassMaterial;
    private Material flowersMaterial;

    public WorldSettingsState(AbstractWorldState world) {
        this.world = world;
    }

    private Container createWorldNoiseContainer() {

        Container container = new Container();

        LayeredNoise layeredNoise = ((DemoWorldState)world).getLayeredNoise();

        PropertyPanel noiseProps = container.addChild(new PropertyPanel("glass"));
        noiseProps.addBooleanProperty("Hard Floor", layeredNoise, "hardFloor");
        noiseProps.addFloatProperty("Hard Floor Height", layeredNoise, "hardFloorHeight", 0, 128, 0.1f);
        noiseProps.addFloatProperty("Hard Floor Strength", layeredNoise, "hardFloorStrength", 0, 128, 0.1f);

        TabbedPanel tabbedPanel = container.addChild(new TabbedPanel());

        for (NoiseLayer layer : layeredNoise.getLayers()) {

            PropertyPanel layerProps = new PropertyPanel("glass");
            layerProps.addIntProperty("Seed", layer, "seed", Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
            layerProps.addFloatProperty("Strength", layer, "strength", 0, 512, 1);
            layerProps.addIntProperty("Octaves", layer, "fractalOctaves", 0, 16, 1);

            tabbedPanel.addTab(layer.getName(), layerProps);
        }

        Button refreshButton = container.addChild(new Button("Refresh World"));
        refreshButton.addClickCommands(source -> {
            world.getSceneGrids().forEach(SceneGrid::refreshGrid);
        });

        return container;
    }

    private Container createTerrainContainer() {

        Container container = new Container();

        TerrainGrid terrainGrid = (TerrainGrid) world.getSceneGrid("Terrain");
        GridSettings gridSettings = terrainGrid.getGridSettings();

        this.terrainMaterial = terrainGrid.getMaterial();

        PropertyPanel propertyPanel = container.addChild(new PropertyPanel("glass"));

        // grid Settings
        propertyPanel.addIntProperty("View Distance", gridSettings, "viewDistance", 1, 20, 1);
        propertyPanel.addEnumProperty("Grid Size", gridSettings, "cellSize");
        propertyPanel.addIntProperty("Additions Per Frame", gridSettings, "additionsPerFrame", 1, 32, 1);
        propertyPanel.addIntProperty("Removals Per Frame", gridSettings, "removalsPerFrame", 1, 32, 1);

        PropertyPanel materialProps = container.addChild(new PropertyPanel("glass"));
        materialProps.addFloatProperty("Low Res Distance", this, "lowResDistance", 0, 512, 0.1f);
        materialProps.addFloatProperty("Noise Scale", this, "noiseScale", 0, 2, 0.01f);

        Button refreshButton = container.addChild(new Button("Refresh Grid"));
        refreshButton.addClickCommands(source -> {
            terrainGrid.refreshGrid();
        });

        return container;
    }

    // terrain settings
    private float lowResDistance = 32;
    public float getLowResDistance() { return lowResDistance; }
    public void setLowResDistance(float lowResDistance) {
        this.lowResDistance = lowResDistance;
        terrainMaterial.setFloat("LowResDistance", lowResDistance);
    }

    float noiseScale = 1.0f;
    public float getNoiseScale() { return noiseScale; }
    public void setNoiseScale(float noiseScale) {
        this.noiseScale = noiseScale;
        terrainMaterial.setFloat("NoiseScale", noiseScale);
    }

    private Container createGrassContainer() {

        Container container = new Container();

        SpriteGrid grassGrid = (SpriteGrid) world.getSceneGrid("Grass");
        GridSettings gridSettings = grassGrid.getGridSettings();

        grassMaterial = grassGrid.getMaterial();

        // grid Settings
        PropertyPanel gridProps = container.addChild(new PropertyPanel("glass"));
        gridProps.addIntProperty("View Distance", gridSettings, "viewDistance", 1, 20, 1);
        gridProps.addEnumProperty("Grid Size", gridSettings, "cellSize");
        gridProps.addIntProperty("Additions Per Frame", gridSettings, "additionsPerFrame", 1, 32, 1);
        gridProps.addIntProperty("Removals Per Frame", gridSettings, "removalsPerFrame", 1, 32, 1);

        MeshPlotterSettings plotterSettings = grassGrid.getPlotterSettings();

        PropertyPanel plotterProps = container.addChild(new PropertyPanel("glass"));
        plotterProps.addFloatProperty("Min Size", plotterSettings, "minSize", 0.01f, 3f, 0.01f);
        plotterProps.addFloatProperty("Max Size", plotterSettings, "maxSize", 0.01f, 3f, 0.01f);
        plotterProps.addFloatProperty("Density", plotterSettings, "density", 0.1f, 16f, 0.01f);
        plotterProps.addFloatProperty("Min World Height", plotterSettings, "minWorldHeight", 0, 256, 0.1f);
        plotterProps.addFloatProperty("Min Height Deviation", plotterSettings, "minWorldHeightDeviation", 0, 10, 0.01f);
        plotterProps.addFloatProperty("Max World Height", plotterSettings, "maxWorldHeight", 0, 256, 0.1f);
        plotterProps.addFloatProperty("Max Height Deviation", plotterSettings, "maxWorldHeightDeviation", 0, 10, 0.01f);


        Button refreshButton = container.addChild(new Button("Refresh Grid"));
        refreshButton.addClickCommands(source -> {
            grassGrid.refreshGrid();
        });

        // these settings aren't affected by the refresh button.

        PropertyPanel materialProps = container.addChild(new PropertyPanel("glass"));
        materialProps.addFloatProperty("Distance Falloff", this, "grassDistanceFalloff", 0, 10000, 0.1f);



        return container;

    }

    // grass settings
    private float grassDistanceFalloff = 512;
    public float getGrassDistanceFalloff() { return grassDistanceFalloff; }
    public void setGrassDistanceFalloff(float distanceFalloff) {
        this.grassDistanceFalloff = distanceFalloff;
        grassMaterial.setFloat("DistanceFalloff", distanceFalloff);
    }

    private Container createFlowersContainer() {

        Container container = new Container();

        SpriteGrid flowersGrid = (SpriteGrid) world.getSceneGrid("Flowers");
        GridSettings gridSettings = flowersGrid.getGridSettings();

        flowersMaterial = flowersGrid.getMaterial();

        PropertyPanel propertyPanel = container.addChild(new PropertyPanel("glass"));

        // grid Settings
        propertyPanel.addIntProperty("View Distance", gridSettings, "viewDistance", 1, 20, 1);
        propertyPanel.addEnumProperty("Grid Size", gridSettings, "cellSize");
        propertyPanel.addIntProperty("Additions Per Frame", gridSettings, "additionsPerFrame", 1, 32, 1);
        propertyPanel.addIntProperty("Removals Per Frame", gridSettings, "removalsPerFrame", 1, 32, 1);

        MeshPlotterSettings plotterSettings = flowersGrid.getPlotterSettings();

        PropertyPanel plotterProps = container.addChild(new PropertyPanel("glass"));
        plotterProps.addFloatProperty("Min Size", plotterSettings, "minSize", 0.01f, 3f, 0.01f);
        plotterProps.addFloatProperty("Max Size", plotterSettings, "maxSize", 0.01f, 3f, 0.01f);
        plotterProps.addFloatProperty("Density", plotterSettings, "density", 0.1f, 16f, 0.01f);
        plotterProps.addFloatProperty("Min World Height", plotterSettings, "minWorldHeight", 0, 256, 0.1f);
        plotterProps.addFloatProperty("Min Height Deviation", plotterSettings, "minWorldHeightDeviation", 0, 10, 0.01f);
        plotterProps.addFloatProperty("Max World Height", plotterSettings, "maxWorldHeight", 0, 256, 0.1f);
        plotterProps.addFloatProperty("Max Height Deviation", plotterSettings, "maxWorldHeightDeviation", 0, 10, 0.01f);

        Button refreshButton = container.addChild(new Button("Refresh Grid"));
        refreshButton.addClickCommands(source -> {
            flowersGrid.refreshGrid();
        });

        // these settings aren't affected by the refresh button.

        PropertyPanel materialProps = container.addChild(new PropertyPanel("glass"));
        materialProps.addFloatProperty("Distance Falloff", this, "flowersDistanceFalloff", 0, 10000, 0.1f);

        return container;

    }

    // flowers settings
    private float flowersDistanceFalloff = 320;
    public float getFlowersDistanceFalloff() { return flowersDistanceFalloff; }
    public void setFlowersDistanceFalloff(float flowersDistanceFalloff) {
        this.flowersDistanceFalloff = flowersDistanceFalloff;
        flowersMaterial.setFloat("DistanceFalloff", flowersDistanceFalloff);
    }

    private Container createTreesContainer() {

        Container container = new Container();

        TreesGrid treesGrid = (TreesGrid) world.getSceneGrid("Trees");
        GridSettings gridSettings = treesGrid.getGridSettings();

        PropertyPanel propertyPanel = container.addChild(new PropertyPanel("glass"));

        // grid Settings
        propertyPanel.addIntProperty("View Distance", gridSettings, "viewDistance", 1, 20, 1);
        propertyPanel.addEnumProperty("Grid Size", gridSettings, "cellSize");
        propertyPanel.addIntProperty("Additions Per Frame", gridSettings, "additionsPerFrame", 1, 32, 1);
        propertyPanel.addIntProperty("Removals Per Frame", gridSettings, "removalsPerFrame", 1, 32, 1);

        TabbedPanel tabbedPanel = container.addChild(new TabbedPanel());

        for (PlottedModel tree : treesGrid.getTrees()) {

            PropertyPanel treeProps = new PropertyPanel("glass");

            treeProps.addFloatProperty("Radius Min", tree, "minRadius", 0.1f, 32, 0.1f);
            treeProps.addFloatProperty("Radius Max", tree, "maxRadius", 0.1f, 32, 0.1f);

            treeProps.addFloatProperty("Min Space Between", tree, "minSpaceBetween", 0.1f, 32, 0.1f);

            treeProps.addFloatProperty("Scale Min", tree, "minScale", 0.1f, 32, 0.1f);
            treeProps.addFloatProperty("Scale Max", tree, "maxScale", 0.1f, 32, 0.1f);

            treeProps.addFloatProperty("World Height Min", tree, "minHeight", 0, 128, 0.1f);
            treeProps.addFloatProperty("World Height Max", tree, "maxHeight", 0, 128, 0.1f);

            treeProps.addFloatProperty("Likelihood", tree, "likelihood", 0, 1, 0.01f);

            treeProps.addIntProperty("Max Attempts", tree, "maxAttempts", 1, 5000, 1);

            tabbedPanel.addTab(tree.getName(), treeProps);
        }

        Button refreshButton = container.addChild(new Button("Refresh Grid"));
        refreshButton.addClickCommands(source -> {
            treesGrid.refreshGrid();
        });

        return container;

    }

    @Override
    protected void initialize(Application app) {

        tabbedPanel = new TabbedPanel();
        tabbedPanel.setLocalTranslation(10, app.getCamera().getHeight() - 10, 1);

        tabbedPanel.addTab("World Noise", createWorldNoiseContainer());
        tabbedPanel.addTab("Terrain", createTerrainContainer());
        tabbedPanel.addTab("Grass", createGrassContainer());
        tabbedPanel.addTab("Flowers", createFlowersContainer());
        tabbedPanel.addTab("Trees", createTreesContainer());
    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {
        ((SimpleApplication)getApplication()).getGuiNode().attachChild(tabbedPanel);
    }

    @Override
    protected void onDisable() {
        tabbedPanel.removeFromParent();
    }



}
