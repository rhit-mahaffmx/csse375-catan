package com.catan.domain;

import java.util.Random;

public class DevelopmentCard {
    private DevCards type;
    private int turnBought;

    public DevelopmentCard(DevCards type) {
        this.type = type;
    }

    public DevCards getType(){
        return type;
    }

    public void setTurnBought(int turn){
        turnBought = turn;
    }

    public Integer getTurnBought(){
        return turnBought;
    }
}
