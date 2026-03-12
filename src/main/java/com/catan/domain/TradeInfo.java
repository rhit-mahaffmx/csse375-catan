package com.catan.domain;

import java.util.HashMap;

public class TradeInfo {

    private Turn player;
    private HashMap<ResourceType, Integer> resources = new HashMap<>();

    public TradeInfo(){
        resources.put(ResourceType.WOOD, 0);
        resources.put(ResourceType.WHEAT, 0);
        resources.put(ResourceType.ORE, 0);
        resources.put(ResourceType.BRICK, 0);
        resources.put(ResourceType.SHEEP, 0);
    }

    public void setPlayer(Turn turn){
        player = turn;
    }

    public Turn getPlayer(){
        return player;
    }

    public void setResources(ResourceType resource, int number){
        resources.put(resource, number);
    }

    public HashMap<ResourceType, Integer> getResources(){
        return resources;
    }
}
