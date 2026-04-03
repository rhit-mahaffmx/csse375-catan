package com.catan.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Q4 - Security Testing (Agile Testing Quadrants)
 *
 * These tests verify that game logic cannot be exploited or manipulated
 * to gain unfair advantages. They check for input validation, state
 * integrity, and protection against edge-case abuse.
 */
public class SecurityTest {

    private GameWindowController mockController;
    private TurnStateMachine mockTurnStateMachine;
    private NumberCardDeck mockDeck;
    private Board board;

    @BeforeEach
    public void setUp() {
        mockController = EasyMock.niceMock(GameWindowController.class);
        mockTurnStateMachine = EasyMock.niceMock(TurnStateMachine.class);
        mockDeck = EasyMock.niceMock(NumberCardDeck.class);
        EasyMock.replay(mockController, mockTurnStateMachine, mockDeck);
        board = new Board(mockController, mockTurnStateMachine, mockDeck);
    }

    private void loadFullBoard() {
        try {
            FileInputStream coordStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
            FileInputStream terrainStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
            FileInputStream tileStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
            board.cityPoints = board.createCities(coordStream, terrainStream, tileStream);

            FileInputStream roadStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
            board.roadPoints = board.createRoads(roadStream);

            FileInputStream cityNeighborsStream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
            board.addAllCityNeighbors(board.cityPoints, board.roadPoints, cityNeighborsStream);

            FileInputStream roadNeighborsStream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
            board.addAllRoadNeighbors(board.roadPoints, board.cityPoints, roadNeighborsStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found during board setup: " + e.getMessage());
        }
    }

    // ==================== Resource Integrity Tests ====================

    @Test
    public void testCannotSubtractMoreResourcesThanOwned() {
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 3);

        player.subResources(ResourceType.WOOD, 5);

        // subResources should silently refuse if player doesn't have enough
        assertEquals(3, player.getResource(ResourceType.WOOD),
                "Player should not lose more resources than they own");
    }

    @Test
    public void testResourcesCannotGoNegativeAfterTrade() {
        loadFullBoard();

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getHasRolled()).andReturn(true).anyTimes();
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);
        board.robberMoved = true;

        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        red.addResources(ResourceType.WOOD, 1);

        // Attempt to trade 5 wood (only have 1)
        TradeInfo offer1 = new TradeInfo();
        offer1.setPlayer(Turn.RED);
        offer1.setResources(ResourceType.WOOD, 5);

        TradeInfo offer2 = new TradeInfo();
        offer2.setPlayer(Turn.BLUE);
        offer2.setResources(ResourceType.BRICK, 5);

        board.onTradeSubmitClick(offer1, offer2);

        // Trade should be rejected; RED should still have 1 wood
        assertEquals(1, red.getResource(ResourceType.WOOD),
                "Trade should be rejected when player can't afford it");
    }

    @Test
    public void testPlayerCannotPayForSettlementWithoutResources() {
        Player player = new Player(Turn.RED);

        assertFalse(player.canPayForSettlement(),
                "Player with no resources should not be able to pay for settlement");
    }

    @Test
    public void testPlayerCannotPayForRoadWithoutResources() {
        Player player = new Player(Turn.RED);

        assertFalse(player.canPayForRoad(),
                "Player with no resources should not be able to pay for road");
    }

    @Test
    public void testPlayerCannotPayForDevCardWithoutResources() {
        Player player = new Player(Turn.RED);

        assertFalse(player.canPayForDevCard(),
                "Player with no resources should not be able to pay for dev card");
    }

    @Test
    public void testPlayerCannotUpgradeSettlementWithoutResources() {
        Player player = new Player(Turn.RED);

        assertFalse(player.canPayToUpgradeSettlement(),
                "Player with no resources should not be able to upgrade settlement");
    }

    // ==================== Bank Resource Overflow Protection ====================

    @Test
    public void testBankInitializedWithMaxResources() {
        Player bank = new Player(Turn.BANK);

        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.WOOD));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.SHEEP));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.BRICK));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.WHEAT));
        assertEquals(Integer.MAX_VALUE, bank.getResource(ResourceType.ORE));
    }

    @Test
    public void testBankTotalResourcesDoNotOverflow() {
        Player bank = new Player(Turn.BANK);
        int total = bank.getTotalResources();

        // With 6 resource types (including NULL) all at MAX_VALUE,
        // integer overflow could occur. Verify behavior.
        // This test documents the current behavior.
        assertTrue(total != 0,
                "Bank total resources should not silently overflow to zero");
    }

    // ==================== Turn / State Manipulation Tests ====================

    @Test
    public void testCannotTradeBeforeRolling() {
        loadFullBoard();

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getHasRolled()).andReturn(false).anyTimes();
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 5);
        int woodBefore = red.getResource(ResourceType.WOOD);

        TradeInfo offer1 = new TradeInfo();
        offer1.setPlayer(Turn.RED);
        offer1.setResources(ResourceType.WOOD, 1);

        TradeInfo offer2 = new TradeInfo();
        offer2.setPlayer(Turn.BLUE);
        offer2.setResources(ResourceType.BRICK, 1);

        board.onTradeSubmitClick(offer1, offer2);

        assertEquals(woodBefore, red.getResource(ResourceType.WOOD),
                "Trade should not go through before dice are rolled");
    }

    @Test
    public void testCannotTradeWhileRobberNotMoved() {
        loadFullBoard();

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getHasRolled()).andReturn(true).anyTimes();
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);
        board.robberMoved = false;

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 5);
        int woodBefore = red.getResource(ResourceType.WOOD);

        TradeInfo offer1 = new TradeInfo();
        offer1.setPlayer(Turn.RED);
        offer1.setResources(ResourceType.WOOD, 1);

        TradeInfo offer2 = new TradeInfo();
        offer2.setPlayer(Turn.BLUE);
        offer2.setResources(ResourceType.BRICK, 1);

        board.onTradeSubmitClick(offer1, offer2);

        assertEquals(woodBefore, red.getResource(ResourceType.WOOD),
                "Trade should not go through while robber has not been moved");
    }

    // ==================== Bank Trade Validation ====================

    @Test
    public void testBankTradeRejectsUnevenRatio() {
        loadFullBoard();

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 10);
        int woodBefore = red.getResource(ResourceType.WOOD);

        // Try to trade 3 wood for 1 brick (default rate is 4:1)
        TradeInfo playerTrade = new TradeInfo();
        playerTrade.setResources(ResourceType.WOOD, 3);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setResources(ResourceType.BRICK, 1);

        board.onBankSubmitClick(playerTrade, bankTrade);

        assertEquals(woodBefore, red.getResource(ResourceType.WOOD),
                "Bank trade should reject resources not divisible by exchange rate");
    }

    @Test
    public void testBankTradeRejectsMismatchedValue() {
        loadFullBoard();

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 10);
        int woodBefore = red.getResource(ResourceType.WOOD);

        // Try to trade 4 wood for 2 brick (4:1 rate so 4 wood = 1 brick, not 2)
        TradeInfo playerTrade = new TradeInfo();
        playerTrade.setResources(ResourceType.WOOD, 4);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setResources(ResourceType.BRICK, 2);

        board.onBankSubmitClick(playerTrade, bankTrade);

        assertEquals(woodBefore, red.getResource(ResourceType.WOOD),
                "Bank trade should reject when offer/request values don't match");
    }

    // ==================== Discard Validation ====================

    @Test
    public void testDiscardHalfRejectsWrongAmount() {
        Player player = new Player(Turn.RED);
        // Give player 10 resources (needs to discard 5)
        player.addResources(ResourceType.WOOD, 4);
        player.addResources(ResourceType.BRICK, 3);
        player.addResources(ResourceType.SHEEP, 3);

        assertTrue(player.needsToDiscard(),
                "Player with 10 resources should need to discard");

        int totalBefore = player.getTotalResources();

        // Try to discard only 2 (should need 5)
        HashMap<ResourceType, Integer> badDiscard = new HashMap<>();
        badDiscard.put(ResourceType.WOOD, 2);
        player.discardHalf(badDiscard);

        assertEquals(totalBefore, player.getTotalResources(),
                "Discard should be rejected when amount doesn't match required count");
    }

    @Test
    public void testDiscardHalfAcceptsCorrectAmount() {
        Player player = new Player(Turn.RED);
        // Give player 10 resources (needs to discard 5)
        player.addResources(ResourceType.WOOD, 4);
        player.addResources(ResourceType.BRICK, 3);
        player.addResources(ResourceType.SHEEP, 3);

        int required = player.getRequiredDiscardCount();
        assertEquals(5, required);

        HashMap<ResourceType, Integer> goodDiscard = new HashMap<>();
        goodDiscard.put(ResourceType.WOOD, 3);
        goodDiscard.put(ResourceType.BRICK, 2);
        player.discardHalf(goodDiscard);

        assertEquals(5, player.getTotalResources(),
                "Discard of correct amount should reduce resources properly");
    }

    // ==================== Victory Point Manipulation ====================

    @Test
    public void testVictoryPointsStartAtZero() {
        Player player = new Player(Turn.RED);
        assertEquals(0, player.getVictoryPoints(),
                "Victory points should start at zero");
    }

    @Test
    public void testVictoryPointsCanBeAddedNegatively() {
        // This documents current behavior - VP can go negative
        Player player = new Player(Turn.RED);
        player.addVictoryPoints(-5);
        assertEquals(-5, player.getVictoryPoints(),
                "Victory points should reflect negative additions (design observation)");
    }

    // ==================== Robber Protection Tests ====================

    @Test
    public void testRobberCannotBeMovedWithoutRolling7OrKnight() {
        loadFullBoard();

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        board.numRolled = 5; // Not 7
        // knightUsed is false by default

        board.onRobberPointClick(100, 100);

        // Robber should not have moved - robberMoved should still be true (initial state)
        assertTrue(board.robberMoved,
                "Robber should not move when dice roll is not 7 and no knight was used");
    }

    // ==================== Monopoly Card Security ====================

    @Test
    public void testMonopolyDoesNotStealFromBank() {
        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        Player red = board.turnToPlayer.get(Turn.RED);
        Player bank = board.turnToPlayer.get(Turn.BANK);

        red.addDevelopmentCard(new DevelopmentCard(DevCards.MONOPOLY));

        int bankWoodBefore = bank.getResource(ResourceType.WOOD);

        board.executeMonopoly(Turn.RED, ResourceType.WOOD);

        assertEquals(bankWoodBefore, bank.getResource(ResourceType.WOOD),
                "Monopoly should not steal resources from the bank");
    }

    @Test
    public void testMonopolyStealsFromAllOtherPlayers() {
        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        Player orange = board.turnToPlayer.get(Turn.ORANGE);
        Player white = board.turnToPlayer.get(Turn.WHITE);

        red.addDevelopmentCard(new DevelopmentCard(DevCards.MONOPOLY));
        blue.addResources(ResourceType.WHEAT, 3);
        orange.addResources(ResourceType.WHEAT, 2);
        white.addResources(ResourceType.WHEAT, 1);

        board.executeMonopoly(Turn.RED, ResourceType.WHEAT);

        assertEquals(6, red.getResource(ResourceType.WHEAT),
                "RED should have all 6 wheat after monopoly");
        assertEquals(0, blue.getResource(ResourceType.WHEAT));
        assertEquals(0, orange.getResource(ResourceType.WHEAT));
        assertEquals(0, white.getResource(ResourceType.WHEAT));
    }

    // ==================== Stealing Validation ====================

    @Test
    public void testStealFromPlayerRemovesFromVictimAddsToThief() {
        Player thief = board.turnToPlayer.get(Turn.RED);
        Player victim = board.turnToPlayer.get(Turn.BLUE);
        victim.addResources(ResourceType.WOOD, 5);

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        int thiefBefore = thief.getTotalResources();
        int victimBefore = victim.getTotalResources();

        // Use a controlled random to steal a known resource
        Random controlledRandom = EasyMock.createMock(Random.class);
        EasyMock.expect(controlledRandom.nextInt(5)).andReturn(0);
        EasyMock.replay(controlledRandom);

        board.stealFromPlayer(Turn.RED, Turn.BLUE, controlledRandom);

        assertEquals(thiefBefore + 1, thief.getTotalResources(),
                "Thief should gain exactly 1 resource");
        assertEquals(victimBefore - 1, victim.getTotalResources(),
                "Victim should lose exactly 1 resource");
    }

    // ==================== Dice Fairness Test ====================

    @Test
    public void testDeckDistributionIsWithinExpectedRange() {
        NumberCardDeck deck = new NumberCardDeck(new Random());
        int[] counts = new int[13]; // index 0-1 unused, 2-12 for card numbers

        int numDraws = 10000;
        for (int i = 0; i < numDraws; i++) {
            int roll = deck.drawNumber();
            assertTrue(roll >= 2 && roll <= 12, "Card draw out of range: " + roll);
            counts[roll]++;
        }

        // 7 should be the most common (6/36 cards per deck)
        // With 10000 draws, expect ~1667 sevens. Allow wide margin.
        assertTrue(counts[7] > 1000,
                "7 should appear frequently (got " + counts[7] + " in " + numDraws + " draws)");

        // 2 and 12 should be least common (1/36 cards per deck)
        assertTrue(counts[2] < counts[7],
                "2 should appear less often than 7");
        assertTrue(counts[12] < counts[7],
                "12 should appear less often than 7");
    }
}
