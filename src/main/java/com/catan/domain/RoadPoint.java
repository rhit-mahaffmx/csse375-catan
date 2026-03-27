package com.catan.domain;

import java.util.ArrayList;

public class RoadPoint extends GameComponent {

    ArrayList<CityPoint> neighbors;
    boolean hasRoad = false;
    boolean isSideways = false;
    Turn owner = Turn.NONE;

    public RoadPoint(int x, int y) {
        super(x, y);
        this.neighbors = new ArrayList<>();
    }

    public void addNeighbor(CityPoint cityPoint) {
        neighbors.add(cityPoint);
    }

    public boolean hasRoad() {
        return hasRoad;
    }

    public Turn getOwner() {
        return owner;
    }

    public void placeRoad(Turn turn) {
        owner = turn;
        hasRoad = true;
    }

    public boolean isSideways() {
        return isSideways;
    }

    public void setSideways(boolean sideways) {
        this.isSideways = sideways;
    }
}
