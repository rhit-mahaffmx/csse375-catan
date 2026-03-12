package com.catan.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    public void testGetPlayerColor() {
        Player player = new Player(Turn.RED);
        assertEquals(Turn.RED, player.color);
    }

    @Test
    public void testGetDifferentPlayers() {
        Player playerOne = new Player(Turn.RED);
        Player playerTwo = new Player(Turn.BLUE);

        assertEquals(Turn.RED, playerOne.color);
        assertEquals(Turn.BLUE, playerTwo.color);
    }

    @Test
    public void testGetPlayerInitialSettlements() {
        Player playerOne = new Player(Turn.RED);
        assertEquals(Board.INITIAL_SETTLEMENTS, playerOne.settlements);
    }

    @Test
    public void testGetPlayerInitialRoads() {
        Player playerOne = new Player(Turn.RED);
        assertEquals(Board.INITIAL_ROADS, playerOne.roads);
    }

    @Test
    public void testGetPlayerInitialWood() {
        Player playerOne = new Player(Turn.RED);

        assertEquals(0, playerOne.getResource(ResourceType.WOOD));
    }

    @Test
    public void testGetPlayerInitialBrick() {
        Player playerOne = new Player(Turn.RED);

        assertEquals(0, playerOne.getResource(ResourceType.BRICK));
    }

    @Test
    public void testGetPlayerInitialSheep() {
        Player playerOne = new Player(Turn.RED);

        assertEquals(0, playerOne.getResource(ResourceType.SHEEP));
    }

    @Test
    public void testGetPlayerInitialOre() {
        Player playerOne = new Player(Turn.RED);

        assertEquals(0, playerOne.getResource(ResourceType.ORE));
    }

    @Test
    public void testGetPlayerInitialWheat() {
        Player playerOne = new Player(Turn.RED);

        assertEquals(0, playerOne.getResource(ResourceType.WHEAT));
    }

    @Test
    public void testAddPlayerResources() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.WOOD, 1);
        assertEquals(1, playerOne.getResource(ResourceType.WOOD));
        assertEquals(0, playerOne.getResource(ResourceType.ORE));
        assertEquals(0, playerOne.getResource(ResourceType.BRICK));
        assertEquals(0, playerOne.getResource(ResourceType.SHEEP));
        assertEquals(0, playerOne.getResource(ResourceType.WHEAT));
    }

    @Test
    public void testCanPayForSettlementHasNothing() {
        Player playerOne = new Player(Turn.RED);

        assertFalse(playerOne.canPayForSettlement());
    }

    @Test
    public void testCanPayForSettlementNoWood() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.WOOD, 1);

        assertFalse(playerOne.canPayForSettlement());
    }

    @Test
    public void testCanPayForSettlementNoBrick() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.BRICK, 1);

        assertFalse(playerOne.canPayForSettlement());
    }

    @Test
    public void testCanPayForSettlementNoSheep() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.SHEEP, 1);

        assertFalse(playerOne.canPayForSettlement());
    }

    @Test
    public void testCanPayForSettlementNoWheat() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.WHEAT, 1);

        assertFalse(playerOne.canPayForSettlement());
    }

    @Test
    public void testCanPayForSettlementProperResources() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.WOOD, 1);
        playerOne.addResources(ResourceType.BRICK, 1);
        playerOne.addResources(ResourceType.SHEEP, 1);
        playerOne.addResources(ResourceType.WHEAT, 1);

        assertTrue(playerOne.canPayForSettlement());
    }

    @Test
    public void testPayForSettlement() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.WOOD, 1);
        playerOne.addResources(ResourceType.BRICK, 1);
        playerOne.addResources(ResourceType.SHEEP, 1);
        playerOne.addResources(ResourceType.WHEAT, 1);

        playerOne.payForSettlement();

        assertEquals(0, playerOne.getResource(ResourceType.WOOD));
        assertEquals(0, playerOne.getResource(ResourceType.BRICK));
        assertEquals(0, playerOne.getResource(ResourceType.SHEEP));
        assertEquals(0, playerOne.getResource(ResourceType.WHEAT));
    }


    @Test
    public void testCanPayForRoadNoResources() {
        Player playerOne = new Player(Turn.RED);

        assertFalse(playerOne.canPayForRoad());
    }

    @Test
    public void testCanPayForRoadNoWood() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.BRICK, 1);

        assertFalse(playerOne.canPayForRoad());
    }

    @Test
    public void testCanPayForRoadNoBrick() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.WOOD, 1);

        assertFalse(playerOne.canPayForRoad());
    }

    @Test
    public void testCanPayForRoadProperResources() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.BRICK, 1);
        playerOne.addResources(ResourceType.WOOD, 1);

        assertTrue(playerOne.canPayForRoad());
    }

    @Test
    public void testPayForRoad() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.BRICK, 1);
        playerOne.addResources(ResourceType.WOOD, 1);

        playerOne.payForRoad();

        assertEquals(0, playerOne.getResource(ResourceType.BRICK));
        assertEquals(0, playerOne.getResource(ResourceType.WOOD));
    }

    @Test
    public void testGetInitialDevCards() {
        Player playerOne = new Player(Turn.RED);

        assertEquals(0, playerOne.getDevCards().size());
    }

    @Test
    public void testAddDevCard() {
        Player player = new Player(Turn.RED);

        DevelopmentCard devCard = EasyMock.mock(DevelopmentCard.class);
        player.addDevelopmentCard(devCard);

        assertEquals(1, player.getDevCards().size());
    }

    @Test
    public void testCanGetDevCardNoResources() {
        Player player = new Player(Turn.RED);
        assertFalse(player.canPayForDevCard());
    }

    @Test
    public void testCanGetDevCardProperResources() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WHEAT, 1);
        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.SHEEP, 1);

        assertTrue(player.canPayForDevCard());
    }

    @Test
    public void testCanGetDevCardNoWheat() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.SHEEP, 1);

        assertFalse(player.canPayForDevCard());
    }

    @Test
    public void testCanGetDevCardNoOre() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WHEAT, 1);
        player.addResources(ResourceType.SHEEP, 1);

        assertFalse(player.canPayForDevCard());
    }

    @Test
    public void testCanGetDevCardNoSheep() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        assertFalse(player.canPayForDevCard());
    }

    @Test
    public void testPayForDevCard() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WHEAT, 1);
        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.SHEEP, 1);

        player.payForDevCard();

        assertEquals(0, player.getResource(ResourceType.WHEAT));
        assertEquals(0, player.getResource(ResourceType.ORE));
        assertEquals(0, player.getResource(ResourceType.SHEEP));
    }

    @Test
    public void subtractResources() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.BRICK, 5);
        playerOne.subResources(ResourceType.BRICK, 2);

        assertEquals(0, playerOne.getResource(ResourceType.WOOD));
        assertEquals(0, playerOne.getResource(ResourceType.ORE));
        assertEquals(3, playerOne.getResource(ResourceType.BRICK));
        assertEquals(0, playerOne.getResource(ResourceType.SHEEP));
        assertEquals(0, playerOne.getResource(ResourceType.WHEAT));
    }

    @Test
    public void subtractResourcesLessThanOne() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.BRICK, 1);
        playerOne.subResources(ResourceType.BRICK, 2);

        assertEquals(0, playerOne.getResource(ResourceType.WOOD));
        assertEquals(0, playerOne.getResource(ResourceType.ORE));
        assertEquals(1, playerOne.getResource(ResourceType.BRICK));
        assertEquals(0, playerOne.getResource(ResourceType.SHEEP));
        assertEquals(0, playerOne.getResource(ResourceType.WHEAT));
    }

    @Test
    public void testCanAfford(){
        Player playerOne = new Player(Turn.RED);

        HashMap<ResourceType, Integer> testResources = new HashMap<ResourceType, Integer>();
        testResources.put(ResourceType.BRICK, 0);
        testResources.put(ResourceType.WOOD, 0);
        testResources.put(ResourceType.WHEAT, 0);
        testResources.put(ResourceType.SHEEP, 0);
        testResources.put(ResourceType.ORE, 5);

        playerOne.addResources(ResourceType.ORE, 5);

        assertEquals(true, playerOne.canAfford(testResources));
    }

    @Test
    public void testCanAffordFalse(){
        Player playerOne = new Player(Turn.RED);

        HashMap<ResourceType, Integer> testResources = new HashMap<ResourceType, Integer>();
        testResources.put(ResourceType.BRICK, 0);
        testResources.put(ResourceType.WOOD, 3);
        testResources.put(ResourceType.WHEAT, 0);
        testResources.put(ResourceType.SHEEP, 0);
        testResources.put(ResourceType.ORE, 5);

        playerOne.addResources(ResourceType.ORE, 4);
        playerOne.addResources(ResourceType.WOOD, 2);

        assertEquals(false, playerOne.canAfford(testResources));
    }

    @Test
    public void testPlayerCanPayToUpgradeSettlementNoResources() {
        Player playerOne = new Player(Turn.RED);
        assertFalse(playerOne.canPayToUpgradeSettlement());
    }

    @Test
    public void testPlayerCanPayToUpgradeSettlementNoWheat() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.ORE, 3);

        assertFalse(playerOne.canPayToUpgradeSettlement());
    }

    @Test
    public void testPlayerCanPayToUpgradeSettlementNoOre() {
        Player playerOne = new Player(Turn.RED);
        playerOne.addResources(ResourceType.WHEAT, 2);
        assertFalse(playerOne.canPayToUpgradeSettlement());
    }

    @Test
    public void testPlayerCanPayToUpgradeSettlementProperResources() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.ORE, 3);
        playerOne.addResources(ResourceType.WHEAT, 2);

        assertTrue(playerOne.canPayToUpgradeSettlement());

    }

    @Test
    public void testPayToUpgradeSettlement() {
        Player playerOne = new Player(Turn.RED);

        playerOne.addResources(ResourceType.ORE, 3);
        playerOne.addResources(ResourceType.WHEAT, 2);

        playerOne.payToUpgradeSettlement();

        assertEquals(0, playerOne.getResource(ResourceType.ORE));
        assertEquals(0, playerOne.getResource(ResourceType.WHEAT));
    }

    @Test
    public void testCountTotalResources() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.BRICK, 3);
        player.addResources(ResourceType.SHEEP, 1);

        assertEquals(6, player.getTotalResources());
    }

    @Test
    public void testPlayerDoesNotNeedToDiscard() {
        Player player = new Player(Turn.RED);

        assertFalse(player.needsToDiscard());
    }

    @Test
    public void testPlayerWithSevenResourcesDoesNotNeedToDiscard() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 4);
        player.addResources(ResourceType.BRICK, 3);

        assertFalse(player.needsToDiscard());
    }

    @Test
    public void testPlayerWithMoreThanSevenResourcesNeedsToDiscard() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 5);
        player.addResources(ResourceType.BRICK, 3);

        assertTrue(player.needsToDiscard());
    }

    @Test
    public void testStartWithZeroVP(){
        Player testPlayer = new Player(Turn.RED);

        assertEquals(0, testPlayer.getVictoryPoints());
    }

    @Test
    public void addOneVictoryPoint(){
        Player testPlayer = new Player(Turn.RED);

        testPlayer.addVictoryPoints(1);

        assertEquals(1, testPlayer.getVictoryPoints());
    }

    @Test
    public void addMoreThanOneVictoryPoint(){
        Player testPlayer = new Player(Turn.RED);

        testPlayer.addVictoryPoints(2);

        assertEquals(2, testPlayer.getVictoryPoints());
    }

    @Test
    public void testPlayerDiscard() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 5);
        player.addResources(ResourceType.BRICK, 4);
        assertEquals(9, player.getTotalResources());
        assertTrue(player.needsToDiscard());

        HashMap<ResourceType,Integer> toDiscard = new HashMap<>();
        toDiscard.put(ResourceType.WOOD, 1);
        toDiscard.put(ResourceType.BRICK, 1);
        toDiscard.put(ResourceType.ORE, 0);
        toDiscard.put(ResourceType.SHEEP, 0);
        toDiscard.put(ResourceType.WHEAT, 0);
        player.discard(toDiscard);

        assertEquals(4, player.getResource(ResourceType.WOOD));
        assertEquals(3, player.getResource(ResourceType.BRICK));
        assertEquals(7, player.getTotalResources());
    }

    @Test
    public void testPlayerDiscardNone() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 3);
        player.addResources(ResourceType.BRICK, 3);

        HashMap<ResourceType,Integer> toDiscard = new HashMap<>();
        toDiscard.put(ResourceType.WOOD, 0);
        toDiscard.put(ResourceType.BRICK, 0);
        toDiscard.put(ResourceType.ORE, 0);
        toDiscard.put(ResourceType.SHEEP, 0);
        toDiscard.put(ResourceType.WHEAT, 0);
        player.discard(toDiscard);

        assertEquals(3, player.getResource(ResourceType.WOOD));
        assertEquals(3, player.getResource(ResourceType.BRICK));
        assertEquals(6, player.getTotalResources());
    }

    @Test
    public void testPlayerDiscardNegative() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 8);


        HashMap<ResourceType,Integer> toDiscard = new HashMap<>();
        toDiscard.put(ResourceType.WOOD, 9);

        player.discard(toDiscard);

        assertEquals(8, player.getResource(ResourceType.WOOD));

    }

    @Test
    public void testDiscardHalfRoundedDown() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 5);
        player.addResources(ResourceType.BRICK, 4);

        HashMap<ResourceType,Integer> toDiscard = new HashMap<>();
        toDiscard.put(ResourceType.WOOD, 4);
        player.discardHalf(toDiscard);

        assertEquals(5, player.getTotalResources());
    }

    @Test
    public void testDiscardHalfNoResources() {
        Player player = new Player(Turn.RED);

        HashMap<ResourceType,Integer> empty = new HashMap<>();

        player.discardHalf(empty);
        assertEquals(0, player.getTotalResources());
    }


    @Test
    public void testDiscardHalfTooFew() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 3);
        player.addResources(ResourceType.BRICK, 1);


        HashMap<ResourceType,Integer> wrong = new HashMap<>();
        wrong.put(ResourceType.WOOD, 1);
        player.discardHalf(wrong);

        assertEquals(3, player.getResource(ResourceType.WOOD));
        assertEquals(1, player.getResource(ResourceType.BRICK));
    }

    @Test
    public void testDiscardHalfTooMany() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 3);

        HashMap<ResourceType,Integer> tooMany = new HashMap<>();
        tooMany.put(ResourceType.WOOD, 2);
        tooMany.put(ResourceType.SHEEP, 2);

        player.discardHalf(tooMany);

        assertEquals(2, player.getResource(ResourceType.WOOD));
        assertEquals(1, player.getResource(ResourceType.BRICK));
        assertEquals(3, player.getResource(ResourceType.SHEEP));
        assertEquals(6, player.getTotalResources());
    }

    @Test
    public void testResourcesAsListNoResources() {
        Player player = new Player(Turn.RED);

        assertEquals(0, player.getResourcesAsList().size());

    }

    @Test
    public void testResourcesAsListOneResource() {
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WHEAT, 1);

        assertEquals(ResourceType.WHEAT, player.getResourcesAsList().getFirst());
    }

    @Test
    public void testResourcesAsListMultipleOfOneResource() {
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WHEAT, 2);

        assertEquals(2, player.getResourcesAsList().size());
        assertEquals(ResourceType.WHEAT, player.getResourcesAsList().getFirst());
        assertEquals(ResourceType.WHEAT, player.getResourcesAsList().get(1));
    }

    @Test
    public void testResourcesAsListOneOfMultipleResources() {
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WHEAT, 1);
        player.addResources(ResourceType.BRICK, 1);

        assertEquals(2, player.getResourcesAsList().size());
        assertTrue(player.getResourcesAsList().contains(ResourceType.WHEAT));
        assertTrue(player.getResourcesAsList().contains(ResourceType.BRICK));
    }

    @Test
    public void testResourcesAsListMultipleOfMultipleResources() {
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WHEAT, 2);
        player.addResources(ResourceType.BRICK, 2);

        assertEquals(4, player.getResourcesAsList().size());
        assertEquals(2, Collections.frequency(player.getResourcesAsList(), ResourceType.WHEAT));
        assertEquals(2, Collections.frequency(player.getResourcesAsList(), ResourceType.BRICK));

    }

    @Test
    public void testBankPlayer(){
        Player bank = new Player(Turn.BANK);

        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.WOOD));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.BRICK));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.SHEEP));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.ORE));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.WHEAT));
    }

    @Test
    public void testRemoveDevCard(){
        Player player = new Player(Turn.RED);
        DevelopmentCard devCard = new DevelopmentCard(DevCards.YEAR_OF_PLENTY);
        player.addDevelopmentCard(devCard);

        player.removeDevelopmentCard(devCard);
        assertEquals(0, player.getDevCards().size());
    }

    @Test
    public void testAddFreeRoads(){
        Player player = new Player(Turn.RED);
        player.addFreeRoads(2);

        assertEquals(2, player.getFreeRoads());
    }

    @Test
    public void testRemoveFreeRoads(){
        Player player = new Player(Turn.RED);
        player.addFreeRoads(2);
        player.removeFreeRoads(1);

        assertEquals(1, player.getFreeRoads());
    }

}
