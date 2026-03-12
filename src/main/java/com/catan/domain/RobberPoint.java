package com.catan.domain;

public class RobberPoint extends GameComponent {
    public boolean hasRobber = false;
    public ResourceType resourceType;
    public int diceNumber;


    public RobberPoint(int x, int y, ResourceType resourceType, int diceNumber) {
        super(x, y);
        this.resourceType = resourceType;
        this.diceNumber = diceNumber;
    }
}
