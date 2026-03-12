package com.catan.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CityPoint extends GameComponent {
    HashMap<Integer, Terrain> tileValueToTerrain = new HashMap<>();
    ArrayList<RoadPoint> neighbors;
    boolean hasSettlement = false;
    public boolean isCity = false;
    Turn owner = Turn.NONE;

    public CityPoint(int x, int y) {
        super(x, y);
        this.neighbors = new ArrayList<>();
    }

    public List<Terrain> getTerrains(){
        return new ArrayList<>(tileValueToTerrain.values());
    }

    public List<Integer> getTileValues(){
        return new ArrayList<>(tileValueToTerrain.keySet());
    }

    public void setTileValues(List<Integer> tileNumbers, List<Terrain> tileTerrains){
        for(int i = 0; i < tileTerrains.size(); i++){
            tileValueToTerrain.put(tileNumbers.get(i), tileTerrains.get(i));
        }
    }

    public void addNeighbor(RoadPoint roadPoint) {
        neighbors.add(roadPoint);
    }

    public boolean hasSettlement(){
        return hasSettlement;
    }

    public Turn getOwner(){
        return owner;
    }

    public void placeSettlement(Turn currentTurn){
        this.owner = currentTurn;
        this.hasSettlement = true;
    }

    public ArrayList<ResourceCard> gatherResources() {
        ArrayList<ResourceCard> result = new ArrayList<>();
        for (Terrain terrain : getTerrains()) {
            int val = valFromTerrain(terrain);
            ResourceType type = terrain.getResourceType();
            if(val == 9 && terrain == Terrain.FOREST){
                result.add(new ResourceCard(ResourceType.SHEEP));
            }
            else{
                if (type != null) {
                    result.add(new ResourceCard(type));
                }
            }
        }
        return result;
    }

    private int valFromTerrain(Terrain terrain) {
        for(int val : tileValueToTerrain.keySet()) {
            if(tileValueToTerrain.get(val) == terrain) {
                return val;
            }
        }
        return 0;
    }
}
