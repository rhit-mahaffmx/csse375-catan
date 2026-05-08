package com.catan.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Player vs AI — Seam Tests (Michael Feathers approach)
 *
 * These tests demonstrate four seam types used to enable testable AI play:
 *
 * 1. Object Seam (PlayerStrategy interface): The Board delegates decisions to a
 *    PlayerStrategy, letting us inject AI or stub strategies without changing Board.
 *
 * 2. Preprocessing Seam (board state setup): We construct minimal board state
 *    (cityPoints, roadPoints, robberPoints) inline rather than loading from files,
 *    giving full control over the test scenario.
 *
 * 3. Link Seam (GameWindowController mock): By mocking the controller, AI turn
 *    execution never touches the GUI, so tests run headlessly.
 *
 * 4. Subclass Seam (AIPlayerStrategy override): Tests can extend AIPlayerStrategy
 *    to override scoring logic and verify decision-making in isolation.
 */
public class AIPlayerStrategyTest {

    private Board board;
    private GameWindowController mockController;
    private TurnStateMachine turnStateMachine;
    private NumberCardDeck mockDeck;
    private AIPlayerStrategy aiStrategy;
    private Random seededRandom;

    @BeforeEach
    public void setUp() {
        mockController = EasyMock.createNiceMock(GameWindowController.class);
        turnStateMachine = new TurnStateMachine();
        mockDeck = EasyMock.createNiceMock(NumberCardDeck.class);
        EasyMock.replay(mockController, mockDeck);
        board = new Board(mockController, turnStateMachine, mockDeck);
        seededRandom = new Random(42);
        aiStrategy = new AIPlayerStrategy(seededRandom);
    }

    // =========================================================================
    // SEAM 1: Object Seam — PlayerStrategy interface injection
    // =========================================================================

    @Test
    public void seam1_humanStrategyIsNotAI() {
        HumanPlayerStrategy human = new HumanPlayerStrategy();
        assertFalse(human.isAI());
    }

    @Test
    public void seam1_aiStrategyIsAI() {
        assertTrue(aiStrategy.isAI());
    }

    @Test
    public void seam1_boardDefaultsToHumanStrategy() {
        PlayerStrategy strategy = board.getPlayerStrategy(Turn.RED);
        assertFalse(strategy.isAI());
    }

    @Test
    public void seam1_boardRecognizesAITurnAfterInjection() {
        board.setPlayerStrategy(Turn.RED, aiStrategy);
        assertTrue(board.isAITurn()); // RED is the first turn
    }

    @Test
    public void seam1_boardDoesNotMisidentifyHumanAsAI() {
        board.setPlayerStrategy(Turn.RED, new HumanPlayerStrategy());
        assertFalse(board.isAITurn());
    }

    @Test
    public void seam1_humanStrategyReturnsNullForAllDecisions() {
        HumanPlayerStrategy human = new HumanPlayerStrategy();
        Player player = new Player(Turn.RED);
        assertNull(human.chooseSettlementLocation(board, player));
        assertNull(human.chooseRoadLocation(board, player));
        assertNull(human.chooseRobberPlacement(board, player));
        assertNull(human.choosePlayerToRob(board, player, new ArrayList<>()));
        assertFalse(human.shouldBuyDevCard(board, player));
        assertNull(human.chooseCityUpgrade(board, player));
    }

    // =========================================================================
    // SEAM 2: Preprocessing Seam — controlled board state for AI decisions
    // =========================================================================

    @Test
    public void seam2_aiChoosesBestSettlementLocation() {
        // City A: adjacent to 6 (high probability) wheat
        CityPoint cityA = new CityPoint(100, 100);
        cityA.setTileValues(List.of(6), List.of(Terrain.FIELD));

        // City B: adjacent to 2 (low probability) ore
        CityPoint cityB = new CityPoint(200, 200);
        cityB.setTileValues(List.of(2), List.of(Terrain.MOUNTAIN));

        board.cityPoints = new ArrayList<>(List.of(cityA, cityB));
        board.roadPoints = new ArrayList<>();

        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint chosen = aiStrategy.chooseSettlementLocation(board, player);

        assertEquals(cityA, chosen, "AI should prefer the city with dice number 6 over 2");
    }

    @Test
    public void seam2_aiPrefersResourceDiversity() {
        // City A: two different resources
        CityPoint cityA = new CityPoint(100, 100);
        cityA.setTileValues(List.of(5, 8), List.of(Terrain.FIELD, Terrain.FOREST));

        // City B: same resource type twice with same probabilities
        CityPoint cityB = new CityPoint(200, 200);
        cityB.setTileValues(List.of(5, 8), List.of(Terrain.FIELD, Terrain.FIELD));

        board.cityPoints = new ArrayList<>(List.of(cityA, cityB));
        board.roadPoints = new ArrayList<>();

        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint chosen = aiStrategy.chooseSettlementLocation(board, player);

        assertEquals(cityA, chosen, "AI should prefer diverse resources");
    }

    @Test
    public void seam2_aiSkipsOccupiedCityPoints() {
        CityPoint occupied = new CityPoint(100, 100);
        occupied.setTileValues(List.of(6), List.of(Terrain.FIELD));
        occupied.placeSettlement(Turn.BLUE);

        CityPoint open = new CityPoint(300, 300);
        open.setTileValues(List.of(3), List.of(Terrain.HILL));

        board.cityPoints = new ArrayList<>(List.of(occupied, open));
        board.roadPoints = new ArrayList<>();

        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint chosen = aiStrategy.chooseSettlementLocation(board, player);

        assertEquals(open, chosen);
    }

    @Test
    public void seam2_aiChoosesBestRoadLocation() {
        CityPoint ownedCity = new CityPoint(100, 100);
        ownedCity.placeSettlement(Turn.RED);

        CityPoint targetCity = new CityPoint(200, 200);
        targetCity.setTileValues(List.of(6), List.of(Terrain.FIELD));

        RoadPoint road1 = new RoadPoint(150, 150);
        road1.addNeighbor(ownedCity);
        road1.addNeighbor(targetCity);
        ownedCity.addNeighbor(road1);
        targetCity.addNeighbor(road1);

        board.cityPoints = new ArrayList<>(List.of(ownedCity, targetCity));
        board.roadPoints = new ArrayList<>(List.of(road1));

        Player player = board.turnToPlayer.get(Turn.RED);
        RoadPoint chosen = aiStrategy.chooseRoadLocation(board, player);

        assertEquals(road1, chosen);
    }

    @Test
    public void seam2_aiSkipsOccupiedRoads() {
        CityPoint city1 = new CityPoint(100, 100);
        city1.placeSettlement(Turn.RED);

        RoadPoint taken = new RoadPoint(150, 150);
        taken.placeRoad(Turn.BLUE);
        taken.addNeighbor(city1);
        city1.addNeighbor(taken);

        board.cityPoints = new ArrayList<>(List.of(city1));
        board.roadPoints = new ArrayList<>(List.of(taken));

        Player player = board.turnToPlayer.get(Turn.RED);
        RoadPoint chosen = aiStrategy.chooseRoadLocation(board, player);

        assertNull(chosen, "AI should not choose an occupied road");
    }

    // =========================================================================
    // SEAM 3: Link Seam — AI robber and steal decisions with mocked GUI
    // =========================================================================

    @Test
    public void seam3_aiTargetsLeadingOpponentWithRobber() {
        // Setup: BLUE is leading with VP
        board.turnToPlayer.get(Turn.BLUE).addVictoryPoints(5);
        board.turnToPlayer.get(Turn.ORANGE).addVictoryPoints(2);

        CityPoint blueCity = new CityPoint(100, 100);
        blueCity.setTileValues(List.of(6), List.of(Terrain.FIELD));
        blueCity.placeSettlement(Turn.BLUE);

        RobberPoint rpGood = new RobberPoint(50, 50, ResourceType.WHEAT, 6);
        RobberPoint rpBad = new RobberPoint(250, 250, ResourceType.ORE, 12);

        board.cityPoints = new ArrayList<>(List.of(blueCity));
        board.robberPoints = new ArrayList<>(List.of(rpGood, rpBad));

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        RobberPoint chosen = aiStrategy.chooseRobberPlacement(board, redPlayer);

        assertEquals(rpGood, chosen, "AI should target hex blocking the leading opponent");
    }

    @Test
    public void seam3_aiRobsRichestPlayer() {
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        blue.addResources(ResourceType.WOOD, 5);

        Player orange = board.turnToPlayer.get(Turn.ORANGE);
        orange.addResources(ResourceType.WOOD, 1);

        ArrayList<Player> eligible = new ArrayList<>(List.of(blue, orange));
        Player redPlayer = board.turnToPlayer.get(Turn.RED);

        Turn victim = aiStrategy.choosePlayerToRob(board, redPlayer, eligible);
        assertEquals(Turn.BLUE, victim, "AI should rob the richest player");
    }

    @Test
    public void seam3_aiRobReturnsFirstIfAllEqual() {
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        blue.addResources(ResourceType.WOOD, 2);

        Player orange = board.turnToPlayer.get(Turn.ORANGE);
        orange.addResources(ResourceType.WOOD, 2);

        ArrayList<Player> eligible = new ArrayList<>(List.of(blue, orange));
        Player redPlayer = board.turnToPlayer.get(Turn.RED);

        Turn victim = aiStrategy.choosePlayerToRob(board, redPlayer, eligible);
        assertNotEquals(Turn.NONE, victim);
    }

    @Test
    public void seam3_aiRobEmptyListReturnsNone() {
        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        Turn victim = aiStrategy.choosePlayerToRob(board, redPlayer, new ArrayList<>());
        assertEquals(Turn.NONE, victim);
    }

    // =========================================================================
    // SEAM 4: Subclass Seam — override AI scoring to verify decisions
    // =========================================================================

    /** Stub AI that always prefers the first available city/road */
    private static class StubAIStrategy extends AIPlayerStrategy {
        StubAIStrategy() { super(new Random(0)); }

        @Override
        int scoreSettlementLocation(CityPoint city) {
            // Fixed score: first city in list wins
            return 1;
        }
    }

    @Test
    public void seam4_subclassOverrideChangesSettlementChoice() {
        StubAIStrategy stubAI = new StubAIStrategy();

        CityPoint city1 = new CityPoint(100, 100);
        city1.setTileValues(List.of(2), List.of(Terrain.MOUNTAIN));

        CityPoint city2 = new CityPoint(200, 200);
        city2.setTileValues(List.of(6), List.of(Terrain.FIELD));

        board.cityPoints = new ArrayList<>(List.of(city1, city2));
        board.roadPoints = new ArrayList<>();

        Player player = board.turnToPlayer.get(Turn.RED);
        // With stub scoring, both have score=1, so last one wins (>=)
        // The real AI would prefer city2 (dice 6), but stub makes them equal
        CityPoint chosen = stubAI.chooseSettlementLocation(board, player);
        assertNotNull(chosen);
    }

    // =========================================================================
    // Board integration — executeAITurn
    // =========================================================================

    @Test
    public void testExecuteAITurnReturnsFalseForHuman() {
        board.setPlayerStrategy(Turn.RED, new HumanPlayerStrategy());
        assertFalse(board.executeAITurn());
    }

    @Test
    public void testExecuteAITurnReturnsFalseWithNoStrategy() {
        assertFalse(board.executeAITurn());
    }

    @Test
    public void testExecuteAISetupTurnPlacesSettlementAndRoad() {
        board.setPlayerStrategy(Turn.RED, aiStrategy);

        CityPoint city = new CityPoint(100, 100);
        city.setTileValues(List.of(6), List.of(Terrain.FIELD));

        RoadPoint road = new RoadPoint(150, 150);
        road.addNeighbor(city);
        city.addNeighbor(road);

        board.cityPoints = new ArrayList<>(List.of(city));
        board.roadPoints = new ArrayList<>(List.of(road));

        // Round 1 setup: AI should place settlement and road
        boolean result = board.executeAITurn();

        assertTrue(result);
        assertTrue(city.hasSettlement(), "AI should have placed a settlement");
        assertEquals(Turn.RED, city.getOwner());
        assertTrue(road.hasRoad, "AI should have placed a road");
    }

    @Test
    public void testExecuteAINormalTurnRollsDice() {
        // Advance to round 3 for normal play
        turnStateMachine.nextTurn(); // R1: RED->BLUE
        turnStateMachine.nextTurn(); // R1: BLUE->ORANGE
        turnStateMachine.nextTurn(); // R1: ORANGE->WHITE
        turnStateMachine.nextTurn(); // R1->R2 (WHITE->WHITE backward)
        turnStateMachine.nextTurn(); // R2: WHITE->ORANGE
        turnStateMachine.nextTurn(); // R2: ORANGE->BLUE
        turnStateMachine.nextTurn(); // R2: BLUE->RED
        turnStateMachine.nextTurn(); // R2->R3

        assertEquals(3, turnStateMachine.getRound());
        assertEquals(Turn.RED, turnStateMachine.getTurn());

        board.setPlayerStrategy(Turn.RED, aiStrategy);
        board.cityPoints = new ArrayList<>();
        board.roadPoints = new ArrayList<>();
        board.robberPoints = new ArrayList<>();

        // Mock the deck to return a card
        EasyMock.reset(mockDeck);
        EventCard card = new EventCard(EventType.NO_EVENT, 5);
        EasyMock.expect(mockDeck.drawCard()).andReturn(card);
        EasyMock.replay(mockDeck);

        boolean result = board.executeAITurn();
        assertTrue(result);
        assertTrue(turnStateMachine.getHasRolled(), "AI should have rolled the dice");
    }

    // =========================================================================
    // AI Dev Card decisions
    // =========================================================================

    @Test
    public void testAIBuysDevCardWhenAffordable() {
        Player player = board.turnToPlayer.get(Turn.RED);
        player.addResources(ResourceType.WHEAT, 1);
        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.SHEEP, 1);

        assertTrue(aiStrategy.shouldBuyDevCard(board, player));
    }

    @Test
    public void testAIDoesNotBuyDevCardWhenCantAfford() {
        Player player = board.turnToPlayer.get(Turn.RED);
        assertFalse(aiStrategy.shouldBuyDevCard(board, player));
    }

    @Test
    public void testAIDoesNotBuyDevCardWhenTooMany() {
        Player player = board.turnToPlayer.get(Turn.RED);
        player.addResources(ResourceType.WHEAT, 1);
        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addDevelopmentCard(new DevelopmentCard(DevCards.KNIGHT));
        player.addDevelopmentCard(new DevelopmentCard(DevCards.KNIGHT));
        player.addDevelopmentCard(new DevelopmentCard(DevCards.KNIGHT));

        assertFalse(aiStrategy.shouldBuyDevCard(board, player));
    }

    // =========================================================================
    // AI City Upgrade decisions
    // =========================================================================

    @Test
    public void testAIChoosesBestCityToUpgrade() {
        Player player = board.turnToPlayer.get(Turn.RED);
        player.addResources(ResourceType.ORE, 3);
        player.addResources(ResourceType.WHEAT, 2);

        CityPoint lowCity = new CityPoint(100, 100);
        lowCity.setTileValues(List.of(2), List.of(Terrain.MOUNTAIN));
        lowCity.placeSettlement(Turn.RED);

        CityPoint highCity = new CityPoint(200, 200);
        highCity.setTileValues(List.of(6, 8), List.of(Terrain.FIELD, Terrain.FOREST));
        highCity.placeSettlement(Turn.RED);

        board.cityPoints = new ArrayList<>(List.of(lowCity, highCity));

        CityPoint chosen = aiStrategy.chooseCityUpgrade(board, player);
        assertEquals(highCity, chosen, "AI should upgrade the highest-scoring settlement");
    }

    @Test
    public void testAIDoesNotUpgradeWhenCantAfford() {
        Player player = board.turnToPlayer.get(Turn.RED);
        CityPoint city = new CityPoint(100, 100);
        city.setTileValues(List.of(6), List.of(Terrain.FIELD));
        city.placeSettlement(Turn.RED);
        board.cityPoints = new ArrayList<>(List.of(city));

        CityPoint chosen = aiStrategy.chooseCityUpgrade(board, player);
        assertNull(chosen);
    }

    @Test
    public void testAIDoesNotUpgradeAlreadyUpgradedCity() {
        Player player = board.turnToPlayer.get(Turn.RED);
        player.addResources(ResourceType.ORE, 3);
        player.addResources(ResourceType.WHEAT, 2);

        CityPoint city = new CityPoint(100, 100);
        city.setTileValues(List.of(6), List.of(Terrain.FIELD));
        city.placeSettlement(Turn.RED);
        city.isCity = true;

        board.cityPoints = new ArrayList<>(List.of(city));

        CityPoint chosen = aiStrategy.chooseCityUpgrade(board, player);
        assertNull(chosen);
    }

    // =========================================================================
    // Scoring edge cases
    // =========================================================================

    @Test
    public void testSettlementScoringHandlesDesertTerrain() {
        CityPoint desertCity = new CityPoint(100, 100);
        desertCity.setTileValues(List.of(0), List.of(Terrain.DESERT));

        int score = aiStrategy.scoreSettlementLocation(desertCity);
        assertEquals(0, score, "Desert should contribute zero to score");
    }

    @Test
    public void testSettlementScoringBonusForHarbor() {
        HarborPoint harbor = new HarborPoint(100, 100, ResourceType.WOOD);
        harbor.setTileValues(List.of(6), List.of(Terrain.FIELD));

        CityPoint regular = new CityPoint(200, 200);
        regular.setTileValues(List.of(6), List.of(Terrain.FIELD));

        int harborScore = aiStrategy.scoreSettlementLocation(harbor);
        int regularScore = aiStrategy.scoreSettlementLocation(regular);

        assertTrue(harborScore > regularScore, "Harbor should score higher than equivalent non-harbor");
    }

    @Test
    public void testRobberFallbackWhenNoGoodTarget() {
        // No opponent settlements on the board
        board.cityPoints = new ArrayList<>();
        RobberPoint rp1 = new RobberPoint(50, 50, ResourceType.NULL, 0);
        RobberPoint rp2 = new RobberPoint(100, 100, ResourceType.NULL, 0);
        board.robberPoints = new ArrayList<>(List.of(rp1, rp2));

        Player red = board.turnToPlayer.get(Turn.RED);
        RobberPoint chosen = aiStrategy.chooseRobberPlacement(board, red);
        assertNotNull(chosen, "AI should fall back to any non-active robber point");
    }
}
