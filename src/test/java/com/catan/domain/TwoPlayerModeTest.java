package com.catan.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.easymock.EasyMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TwoPlayerModeTest {

    private Board board;
    private GameWindowController mockController;
    private TurnStateMachine turnStateMachine;
    private NumberCardDeck mockDeck;

    @BeforeEach
    public void setUp() {
        mockController = EasyMock.createNiceMock(GameWindowController.class);
        turnStateMachine = new TurnStateMachine();
        mockDeck = EasyMock.createNiceMock(NumberCardDeck.class);
        board = new Board(mockController, turnStateMachine, mockDeck);
        EasyMock.replay(mockController, mockDeck);
    }

    // ==================== Enable Two-Player Mode ====================

    @Test
    public void testEnableTwoPlayerMode() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        assertTrue(board.isTwoPlayerMode());
        assertNotNull(board.getNeutralPlayer1());
        assertNotNull(board.getNeutralPlayer2());
        assertEquals(Turn.WHITE, board.getNeutralPlayer1().color);
        assertEquals(Turn.ORANGE, board.getNeutralPlayer2().color);
    }

    @Test
    public void testStartingTokensFirstPlayer() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        assertEquals(Board.STARTING_TOKENS_FIRST_PLAYER, redPlayer.getTradeTokens());
    }

    @Test
    public void testStartingTokensSecondPlayer() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player bluePlayer = board.turnToPlayer.get(Turn.BLUE);
        assertEquals(Board.STARTING_TOKENS_SECOND_PLAYER, bluePlayer.getTradeTokens());
    }

    @Test
    public void testDefaultModeIsNotTwoPlayer() {
        assertFalse(board.isTwoPlayerMode());
    }

    // ==================== Neutral Player Placement ====================

    @Test
    public void testPlaceNeutralSettlement() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        board.cityPoints = new ArrayList<>();
        CityPoint city = new CityPoint(100, 200);
        board.cityPoints.add(city);

        NeutralPlayer neutral = board.getNeutralPlayer1();
        int settlementsBefore = neutral.settlements;
        boolean result = board.placeNeutralSettlement(neutral, 100, 200);

        assertTrue(result);
        assertTrue(city.hasSettlement());
        assertEquals(Turn.WHITE, city.getOwner());
        assertEquals(settlementsBefore - 1, neutral.settlements);
    }

    @Test
    public void testPlaceNeutralSettlementFailsOnOccupied() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        board.cityPoints = new ArrayList<>();
        CityPoint city = new CityPoint(100, 200);
        city.placeSettlement(Turn.RED);
        board.cityPoints.add(city);

        NeutralPlayer neutral = board.getNeutralPlayer1();
        boolean result = board.placeNeutralSettlement(neutral, 100, 200);
        assertFalse(result);
    }

    @Test
    public void testPlaceNeutralSettlementFailsWhenNotTwoPlayerMode() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(new CityPoint(100, 200));

        boolean result = board.placeNeutralSettlement(neutral, 100, 200);
        assertFalse(result);
    }

    @Test
    public void testPlaceNeutralRoad() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        board.roadPoints = new ArrayList<>();
        RoadPoint road = new RoadPoint(50, 60);
        board.roadPoints.add(road);

        NeutralPlayer neutral = board.getNeutralPlayer1();
        int roadsBefore = neutral.roads;
        boolean result = board.placeNeutralRoad(neutral, 50, 60);

        assertTrue(result);
        assertTrue(road.hasRoad);
        assertEquals(Turn.WHITE, road.getOwner());
        assertEquals(roadsBefore - 1, neutral.roads);
    }

    @Test
    public void testPlaceNeutralRoadFailsOnOccupied() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        board.roadPoints = new ArrayList<>();
        RoadPoint road = new RoadPoint(50, 60);
        road.placeRoad(Turn.RED);
        board.roadPoints.add(road);

        NeutralPlayer neutral = board.getNeutralPlayer1();
        boolean result = board.placeNeutralRoad(neutral, 50, 60);
        assertFalse(result);
    }

    // ==================== Trade Token Earning ====================

    @Test
    public void testTokensForDesertAdjacentSettlement() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint city = new CityPoint(100, 200);
        ArrayList<Integer> tileNums = new ArrayList<>();
        tileNums.add(5);
        tileNums.add(0);
        ArrayList<Terrain> terrains = new ArrayList<>();
        terrains.add(Terrain.FIELD);
        terrains.add(Terrain.DESERT);
        city.setTileValues(tileNums, terrains);

        // City has 2 terrains -> also coast adjacent (fewer than 3 hexes)
        int tokens = board.awardTradeTokensForSettlement(player, city);
        assertEquals(Board.TOKENS_FOR_DESERT_ADJACENT + Board.TOKENS_FOR_COAST_ADJACENT, tokens);
    }

    @Test
    public void testTokensForCoastAdjacentSettlement() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint city = new CityPoint(100, 200);
        ArrayList<Integer> tileNums = new ArrayList<>();
        tileNums.add(5);
        tileNums.add(6);
        ArrayList<Terrain> terrains = new ArrayList<>();
        terrains.add(Terrain.FIELD);
        terrains.add(Terrain.FOREST);
        city.setTileValues(tileNums, terrains);

        int tokens = board.awardTradeTokensForSettlement(player, city);
        assertEquals(Board.TOKENS_FOR_COAST_ADJACENT, tokens);
    }

    @Test
    public void testNoTokensForInlandSettlement() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint city = new CityPoint(100, 200);
        ArrayList<Integer> tileNums = new ArrayList<>();
        tileNums.add(5);
        tileNums.add(6);
        tileNums.add(8);
        ArrayList<Terrain> terrains = new ArrayList<>();
        terrains.add(Terrain.FIELD);
        terrains.add(Terrain.FOREST);
        terrains.add(Terrain.HILL);
        city.setTileValues(tileNums, terrains);

        int tokens = board.awardTradeTokensForSettlement(player, city);
        assertEquals(0, tokens);
    }

    @Test
    public void testNoTokensAwardedWhenNotTwoPlayerMode() {
        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint city = new CityPoint(100, 200);
        int tokens = board.awardTradeTokensForSettlement(player, city);
        assertEquals(0, tokens);
    }

    // ==================== Discard Knight for Tokens ====================

    @Test
    public void testDiscardKnightForTokens() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player player = board.turnToPlayer.get(Turn.RED);
        player.addDevelopmentCard(new DevelopmentCard(DevCards.KNIGHT));
        int tokensBefore = player.getTradeTokens();

        boolean result = board.discardKnightForTokens(Turn.RED);

        assertTrue(result);
        assertEquals(tokensBefore + Board.TOKENS_FOR_KNIGHT_DISCARD, player.getTradeTokens());
        assertTrue(player.getDevCards().isEmpty());
    }

    @Test
    public void testDiscardKnightFailsWithNoKnight() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        boolean result = board.discardKnightForTokens(Turn.RED);
        assertFalse(result);
    }

    @Test
    public void testDiscardKnightFailsWhenNotTwoPlayerMode() {
        Player player = board.turnToPlayer.get(Turn.RED);
        player.addDevelopmentCard(new DevelopmentCard(DevCards.KNIGHT));
        boolean result = board.discardKnightForTokens(Turn.RED);
        assertFalse(result);
    }

    // ==================== Forced Trade ====================

    @Test
    public void testForcedTradeSuccess() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player actor = board.turnToPlayer.get(Turn.RED);
        Player opponent = board.turnToPlayer.get(Turn.BLUE);

        // Give opponent resources to steal from
        opponent.addResources(ResourceType.WOOD, 3);
        // Give actor resources to trade back
        actor.addResources(ResourceType.BRICK, 3);

        ArrayList<ResourceType> giveBack = new ArrayList<>();
        giveBack.add(ResourceType.BRICK);
        giveBack.add(ResourceType.BRICK);

        int actorTokensBefore = actor.getTradeTokens();
        ArrayList<ResourceType> stolen = board.executeForcedTrade(Turn.RED, Turn.BLUE, giveBack);

        assertEquals(2, stolen.size());
        assertEquals(actorTokensBefore - Board.FORCED_TRADE_COST, actor.getTradeTokens());
    }

    @Test
    public void testForcedTradeFailsWithNoTokens() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player actor = board.turnToPlayer.get(Turn.RED);
        // Spend all tokens
        actor.spendTradeTokens(actor.getTradeTokens());

        Player opponent = board.turnToPlayer.get(Turn.BLUE);
        opponent.addResources(ResourceType.WOOD, 3);

        ArrayList<ResourceType> giveBack = new ArrayList<>();
        giveBack.add(ResourceType.BRICK);
        giveBack.add(ResourceType.BRICK);

        ArrayList<ResourceType> stolen = board.executeForcedTrade(Turn.RED, Turn.BLUE, giveBack);
        assertTrue(stolen.isEmpty());
    }

    @Test
    public void testForcedTradeFailsOpponentTooFewCards() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player opponent = board.turnToPlayer.get(Turn.BLUE);
        opponent.addResources(ResourceType.WOOD, 1); // only 1 card

        ArrayList<ResourceType> giveBack = new ArrayList<>();
        giveBack.add(ResourceType.BRICK);
        giveBack.add(ResourceType.BRICK);

        ArrayList<ResourceType> stolen = board.executeForcedTrade(Turn.RED, Turn.BLUE, giveBack);
        assertTrue(stolen.isEmpty());
    }

    @Test
    public void testForcedTradeFailsNotTwoPlayerMode() {
        ArrayList<ResourceType> giveBack = new ArrayList<>();
        giveBack.add(ResourceType.BRICK);
        giveBack.add(ResourceType.BRICK);

        ArrayList<ResourceType> stolen = board.executeForcedTrade(Turn.RED, Turn.BLUE, giveBack);
        assertTrue(stolen.isEmpty());
    }

    @Test
    public void testForcedTradeFailsWrongGiveBackCount() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player opponent = board.turnToPlayer.get(Turn.BLUE);
        opponent.addResources(ResourceType.WOOD, 3);

        ArrayList<ResourceType> giveBack = new ArrayList<>();
        giveBack.add(ResourceType.BRICK); // only 1, need 2

        ArrayList<ResourceType> stolen = board.executeForcedTrade(Turn.RED, Turn.BLUE, giveBack);
        assertTrue(stolen.isEmpty());
    }

    // ==================== Robber Move with Tokens ====================

    @Test
    public void testRobberMoveWithTokens() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player player = board.turnToPlayer.get(Turn.BLUE);
        board.robberMoved = false;

        boolean result = board.executeRobberMoveWithTokens(Turn.BLUE);

        assertTrue(result);
        assertTrue(board.robberMoved);
        assertEquals(Board.STARTING_TOKENS_SECOND_PLAYER - Board.ROBBER_MOVE_COST, player.getTradeTokens());
    }

    @Test
    public void testRobberMoveFailsNoTokens() {
        board.enableTwoPlayerMode(Turn.WHITE, Turn.ORANGE);
        Player player = board.turnToPlayer.get(Turn.RED);
        player.spendTradeTokens(player.getTradeTokens());

        boolean result = board.executeRobberMoveWithTokens(Turn.RED);
        assertFalse(result);
    }

    @Test
    public void testRobberMoveFailsNotTwoPlayerMode() {
        boolean result = board.executeRobberMoveWithTokens(Turn.RED);
        assertFalse(result);
    }

    // ==================== TurnStateMachine Two-Player Mode ====================

    @Test
    public void testTwoPlayerTurnCycle() {
        TurnStateMachine tsm = new TurnStateMachine();
        tsm.setTwoPlayerMode(true);

        // Round 1: RED -> BLUE
        assertEquals(Turn.RED, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn());
        // After BLUE in round 1, should go to round 2 (backward)
        tsm.nextTurn();
        assertEquals(2, tsm.getRound());
        assertEquals(Turn.BLUE, tsm.getTurn());

        // Round 2 backward: BLUE -> RED
        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        // After RED in round 2, should go to round 3 (forward)
        tsm.nextTurn();
        assertEquals(3, tsm.getRound());
        assertEquals(Turn.RED, tsm.getTurn());
    }

    @Test
    public void testTwoPlayerNormalPlayCycle() {
        TurnStateMachine tsm = new TurnStateMachine();
        tsm.setTwoPlayerMode(true);

        // Advance through setup rounds
        tsm.nextTurn(); // R1: RED -> BLUE
        tsm.nextTurn(); // R1: BLUE -> R2
        tsm.nextTurn(); // R2: BLUE -> RED
        tsm.nextTurn(); // R2: RED -> R3

        assertEquals(3, tsm.getRound());
        assertEquals(Turn.RED, tsm.getTurn());

        // Round 3+: RED -> BLUE -> RED (next round)
        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        assertEquals(4, tsm.getRound());
    }

    @Test
    public void testTwoPlayerHasRolledResets() {
        TurnStateMachine tsm = new TurnStateMachine();
        tsm.setTwoPlayerMode(true);
        tsm.hasRolled = true;
        tsm.nextTurn();
        assertFalse(tsm.getHasRolled());
    }

    // ==================== Player Trade Token Methods ====================

    @Test
    public void testPlayerTradeTokensInitiallyZero() {
        Player player = new Player(Turn.RED);
        assertEquals(0, player.getTradeTokens());
    }

    @Test
    public void testPlayerAddTradeTokens() {
        Player player = new Player(Turn.RED);
        player.addTradeTokens(5);
        assertEquals(5, player.getTradeTokens());
    }

    @Test
    public void testPlayerCanSpendTradeTokens() {
        Player player = new Player(Turn.RED);
        player.addTradeTokens(3);
        assertTrue(player.canSpendTradeTokens(2));
        assertTrue(player.canSpendTradeTokens(3));
        assertFalse(player.canSpendTradeTokens(4));
    }

    @Test
    public void testPlayerSpendTradeTokens() {
        Player player = new Player(Turn.RED);
        player.addTradeTokens(5);
        player.spendTradeTokens(2);
        assertEquals(3, player.getTradeTokens());
    }

    @Test
    public void testPlayerSpendTradeTokensDoesNotGoNegative() {
        Player player = new Player(Turn.RED);
        player.addTradeTokens(1);
        player.spendTradeTokens(5); // should not deduct
        assertEquals(1, player.getTradeTokens());
    }
}
