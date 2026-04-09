package com.catan.domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Player {
    public Turn color;
    public int numKnightsPlayed = 0;
    public final static int DISCARD_THRESHOLD = 7;

    public int settlements = Board.INITIAL_SETTLEMENTS;
    public int roads = Board.INITIAL_ROADS;
    private int victoryPoints = 0;
    private int freeRoads = 0;
    private final ArrayList<FishToken> fishTokens = new ArrayList<>();
    private boolean hasOldShoe = false;
    private final HashMap<ResourceType, Integer> resources = new HashMap<>();
    private final ArrayList<DevelopmentCard> developmentCards = new ArrayList<>();

    public Player(Turn turn) {
        color = turn;

        if(turn == Turn.BANK){
            resources.put(ResourceType.WOOD, Integer.MAX_VALUE);
            resources.put(ResourceType.SHEEP, Integer.MAX_VALUE);
            resources.put(ResourceType.BRICK, Integer.MAX_VALUE);
            resources.put(ResourceType.WHEAT, Integer.MAX_VALUE);
            resources.put(ResourceType.ORE, Integer.MAX_VALUE);
            resources.put(ResourceType.NULL, Integer.MAX_VALUE);
        }
        else{
            resources.put(ResourceType.WOOD, 0);
            resources.put(ResourceType.SHEEP, 0);
            resources.put(ResourceType.BRICK, 0);
            resources.put(ResourceType.WHEAT, 0);
            resources.put(ResourceType.ORE, 0);
            resources.put(ResourceType.NULL, 0);
        }
    }

    public int getResource(ResourceType resourceType) {
        return resources.get(resourceType);
    }

    public void addResources(ResourceType resourceType, int amount) {
        resources.put(resourceType, resources.get(resourceType) + amount);
    }

    public void subResources(ResourceType resourceType, int amount) {
        int resourceAmt = resources.get(resourceType);

        if (resourceAmt < amount) {
            return;
        } else {
            resources.put(resourceType, resourceAmt - amount);
        }
    }

    public void payForSettlement() {
        resources.put(ResourceType.WOOD, resources.get(ResourceType.WOOD) - 1);
        resources.put(ResourceType.BRICK, resources.get(ResourceType.BRICK) - 1);
        resources.put(ResourceType.SHEEP, resources.get(ResourceType.SHEEP) - 1);
        resources.put(ResourceType.WHEAT, resources.get(ResourceType.WHEAT) - 1);
    }

    public boolean canPayForSettlement() {
        return resources.get(ResourceType.WOOD) > 0
                && resources.get(ResourceType.BRICK) > 0
                && resources.get(ResourceType.SHEEP) > 0
                && resources.get(ResourceType.WHEAT) > 0;
    }

    public boolean canPayForRoad() {
        return resources.get(ResourceType.WOOD) > 0 && resources.get(ResourceType.BRICK) > 0;
    }

    public void payForRoad() {
        resources.put(ResourceType.WOOD, resources.get(ResourceType.WOOD) - 1);
        resources.put(ResourceType.BRICK, resources.get(ResourceType.BRICK) - 1);
    }

    public ArrayList<DevelopmentCard> getDevCards() {
        return developmentCards;
    }

    public void addDevelopmentCard(DevelopmentCard devCard) {
        developmentCards.add(devCard);
    }

    public void removeDevelopmentCard(DevelopmentCard devCard){
        developmentCards.remove(devCard);
    }

    public boolean canPayForDevCard() {
        return resources.get(ResourceType.WHEAT) > 0
                && resources.get(ResourceType.ORE) > 0
                && resources.get(ResourceType.SHEEP) > 0;
    }

    public void payForDevCard() {
        resources.put(ResourceType.WHEAT, resources.get(ResourceType.WHEAT) - 1);
        resources.put(ResourceType.ORE, resources.get(ResourceType.ORE) - 1);
        resources.put(ResourceType.SHEEP, resources.get(ResourceType.SHEEP) - 1);
    }


    public boolean canAfford(HashMap<ResourceType, Integer> cost) {
        for (ResourceType resource : cost.keySet()) {
            if (resources.get(resource) < cost.get(resource)) {
                return false;
            }
        }
        return true;
    }

    public boolean canPayToUpgradeSettlement() {
        return resources.get(ResourceType.ORE) >= 3 && resources.get(ResourceType.WHEAT) >= 2;
    }

    public void payToUpgradeSettlement() {
        resources.put(ResourceType.WHEAT, resources.get(ResourceType.WHEAT) - 2);
        resources.put(ResourceType.ORE, resources.get(ResourceType.ORE) - 3);
    }

    public int getTotalResources() {
        int total = 0;

        for (int count : resources.values()) {
            total += count;
        }

        return total;
    }

    public boolean needsToDiscard() {
        return getTotalResources() > DISCARD_THRESHOLD;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void addVictoryPoints(Integer addedVP){
        victoryPoints += addedVP;
    }

    public void discard(HashMap<ResourceType, Integer> discardMap) {
        for (ResourceType type : ResourceType.values()) {
            if (type == ResourceType.NULL) continue;
            int toDiscard = discardMap.getOrDefault(type, 0);
            subResources(type, toDiscard);
        }
    }

    public void discardHalf(HashMap<ResourceType, Integer> discardMap) {
        int required = getRequiredDiscardCount();
        int sum = 0;

        for (ResourceType type : ResourceType.values()) {
            if (type == ResourceType.NULL) continue;
            sum += discardMap.getOrDefault(type, 0);
        }
        if (sum != required) {
            return;
        }

        discard(discardMap);
    }

    public int getRequiredDiscardCount() {
        return getTotalResources() / 2;
    }

    public ArrayList<ResourceType> getResourcesAsList() {
        ArrayList<ResourceType> resourcesAsList = new ArrayList<>();

        for (ResourceType key : resources.keySet()) {
            int num = resources.get(key);
            for (int i = 0; i < num; i++) {
                resourcesAsList.add(key);
            }
        }

        return resourcesAsList;
    }

    public void addFreeRoads(int freeRoads) {
        this.freeRoads += freeRoads;
    }

    public int getFreeRoads() {
        return freeRoads;
    }

    public void removeFreeRoads(int freeRoads) {
        this.freeRoads -= freeRoads;
    }

    public int getFishTokens() {
        return getTotalFish();
    }

    public void addFishToken(FishToken token) {
        if (token.isOldShoe()) {
            hasOldShoe = true;
        } else {
            fishTokens.add(token);
        }
    }

    public void addFishTokens(int amount) {
        // Legacy convenience method - adds 1-fish tokens
        for (int i = 0; i < amount; i++) {
            fishTokens.add(new FishToken(1));
        }
    }

    public int getTotalFish() {
        int total = 0;
        for (FishToken token : fishTokens) {
            total += token.getFishCount();
        }
        return total;
    }

    public boolean spendFishTokens(int cost) {
        if (getTotalFish() < cost) {
            return false;
        }
        ArrayList<FishToken> spent = selectTokensForCost(cost);
        fishTokens.removeAll(spent);
        return true;
    }

    ArrayList<FishToken> selectTokensForCost(int cost) {
        ArrayList<FishToken> sorted = new ArrayList<>(fishTokens);
        sorted.sort((a, b) -> Integer.compare(a.getFishCount(), b.getFishCount()));
        ArrayList<FishToken> selected = new ArrayList<>();
        int sum = 0;
        for (FishToken token : sorted) {
            if (sum >= cost) break;
            selected.add(token);
            sum += token.getFishCount();
        }
        return selected;
    }

    public ArrayList<FishToken> getFishTokenList() {
        return new ArrayList<>(fishTokens);
    }

    public boolean hasOldShoe() {
        return hasOldShoe;
    }

    public void setOldShoe(boolean hasIt) {
        this.hasOldShoe = hasIt;
    }

    public int getVictoryPointsNeededToWin() {
        return Board.VP_WIN_AMOUNT + (hasOldShoe ? 1 : 0);
    }
}

