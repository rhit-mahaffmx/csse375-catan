package com.catan.domain;

public class TurnStateData {
    public Turn turn;
    public int settlements;
    public int roads;
    public int victoryPoints;

    public TurnStateData(Turn turn, int settlements, int roads, int victoryPoints) {
        this.turn = turn;
        this.settlements = settlements;
        this.roads = roads;
        this.victoryPoints = victoryPoints;
    }
}
