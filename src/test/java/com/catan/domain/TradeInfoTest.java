package com.catan.domain;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class TradeInfoTest {

    @Test
    public void testGetPlayer(){
        TradeInfo testInfo = new TradeInfo();

        testInfo.setPlayer(Turn.RED);

        assertEquals(Turn.RED, testInfo.getPlayer());
    }

    @Test
    public void testGetPlayerBlue(){
        TradeInfo testInfo = new TradeInfo();

        testInfo.setPlayer(Turn.BLUE);

        assertEquals(Turn.BLUE, testInfo.getPlayer());
    }

    @Test
    public void testGetItemsToTrade(){
        TradeInfo testInfo = new TradeInfo();

        testInfo.setResources(ResourceType.WOOD, 2);
        HashMap<ResourceType, Integer> tradeValues = testInfo.getResources();

        assertEquals(tradeValues.get(ResourceType.WOOD), 2);
        assertEquals(tradeValues.get(ResourceType.BRICK), 0);
        assertEquals(tradeValues.get(ResourceType.ORE), 0);
        assertEquals(tradeValues.get(ResourceType.SHEEP), 0);
        assertEquals(tradeValues.get(ResourceType.WHEAT), 0);
    }

    @Test
    public void testGetItemsToTradeBrick(){
        TradeInfo testInfo = new TradeInfo();

        testInfo.setResources(ResourceType.WOOD, 2);
        testInfo.setResources(ResourceType.BRICK, 2);
        HashMap<ResourceType, Integer> tradeValues = testInfo.getResources();

        assertEquals(tradeValues.get(ResourceType.WOOD), 2);
        assertEquals(tradeValues.get(ResourceType.BRICK), 2);
        assertEquals(tradeValues.get(ResourceType.ORE), 0);
        assertEquals(tradeValues.get(ResourceType.SHEEP), 0);
        assertEquals(tradeValues.get(ResourceType.WHEAT), 0);
    }

    @Test
    public void testTradeInfoConstructor(){
        TradeInfo testInfo = new TradeInfo();

        HashMap<ResourceType, Integer> tradeValues = testInfo.getResources();

        assertEquals(tradeValues.get(ResourceType.WOOD), 0);
        assertEquals(tradeValues.get(ResourceType.BRICK), 0);
        assertEquals(tradeValues.get(ResourceType.ORE), 0);
        assertEquals(tradeValues.get(ResourceType.SHEEP), 0);
        assertEquals(tradeValues.get(ResourceType.WHEAT), 0);
    }
}
