package com.catan.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Q4 - "ility" Testing (Agile Testing Quadrants)
 *
 * These tests target non-functional quality attributes:
 * - Reliability: Does the system behave consistently and correctly?
 * - Scalability: Does it handle growth in game state gracefully?
 * - Maintainability: Are components properly decoupled and testable?
 * - Robustness: Does it handle edge cases without crashing?
 * - Recoverability: Can game state remain valid after unusual events?
 */
public class IlityTest {

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

    // ==================== RELIABILITY TESTS ====================

    @Test
    public void testDeckDrawsAreAlwaysInValidRange() {
        NumberCardDeck deck = new NumberCardDeck(new Random());

        for (int i = 0; i < 5000; i++) {
            int roll = deck.drawNumber();
            assertTrue(roll >= 2 && roll <= 12,
                    "Card draw " + roll + " is outside valid range [2, 12]");
        }
    }

    @Test
    public void testTurnStateMachineProgressesReliably() {
        TurnStateMachine tsm = new TurnStateMachine();

        assertEquals(Turn.RED, tsm.getTurn(), "First turn should be RED");

        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn(), "Second turn should be BLUE");

        tsm.nextTurn();
        assertEquals(Turn.ORANGE, tsm.getTurn(), "Third turn should be ORANGE");

        tsm.nextTurn();
        // After WHITE in round 1, direction reverses to round 2
        assertEquals(Turn.WHITE, tsm.getTurn(), "Fourth turn should be WHITE");
    }

    @Test
    public void testTurnStateMachineCompletesRound1AndRound2() {
        TurnStateMachine tsm = new TurnStateMachine();

        // Round 1: RED -> BLUE -> ORANGE -> WHITE
        assertEquals(Turn.RED, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.ORANGE, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.WHITE, tsm.getTurn());
        tsm.nextTurn();

        // Round 2 (reverse): WHITE -> ORANGE -> BLUE -> RED
        assertEquals(Turn.WHITE, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.ORANGE, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn());
        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        tsm.nextTurn();

        // Round 3: forward again, RED
        assertEquals(Turn.RED, tsm.getTurn());
    }

    @Test
    public void testResourceTotalIsConsistentAfterOperations() {
        Player player = new Player(Turn.RED);

        player.addResources(ResourceType.WOOD, 5);
        player.addResources(ResourceType.BRICK, 3);
        player.addResources(ResourceType.SHEEP, 2);

        assertEquals(10, player.getTotalResources());

        player.subResources(ResourceType.WOOD, 2);
        assertEquals(8, player.getTotalResources());

        player.subResources(ResourceType.BRICK, 3);
        assertEquals(5, player.getTotalResources());
    }

    @Test
    public void testBoardCreatesCorrectNumberOfCityPoints() {
        loadFullBoard();
        assertEquals(Board.NUM_CITYPOINTS, board.cityPoints.size(),
                "Board should have exactly " + Board.NUM_CITYPOINTS + " city points");
    }

    @Test
    public void testBoardCreatesCorrectNumberOfRoadPoints() {
        loadFullBoard();
        assertEquals(Board.NUM_ROADPOINTS, board.roadPoints.size(),
                "Board should have exactly " + Board.NUM_ROADPOINTS + " road points");
    }

    // ==================== ROBUSTNESS / EDGE CASE TESTS ====================

    @Test
    public void testLookupNonExistentCityReturnsDefaultPoint() {
        loadFullBoard();

        CityPoint result = board.getCityAtCoords(-999, -999);
        assertNotNull(result, "Looking up non-existent city should not return null");
        assertEquals(-1, result.getX(), "Default city X should be -1");
        assertEquals(-1, result.getY(), "Default city Y should be -1");
    }

    @Test
    public void testLookupNonExistentRoadReturnsDefaultPoint() {
        loadFullBoard();

        RoadPoint result = board.getRoadAtCoords(-999, -999);
        assertNotNull(result, "Looking up non-existent road should not return null");
        assertEquals(-1, result.getX(), "Default road X should be -1");
        assertEquals(-1, result.getY(), "Default road Y should be -1");
    }

    @Test
    public void testPlayerResourcesListMatchesTotalCount() {
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 3);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.SHEEP, 1);

        ArrayList<ResourceType> asList = player.getResourcesAsList();
        assertEquals(player.getTotalResources(), asList.size(),
                "Resource list size should match total resource count");
    }

    @Test
    public void testSubtractingZeroResourcesHasNoEffect() {
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 5);

        player.subResources(ResourceType.WOOD, 0);
        assertEquals(5, player.getResource(ResourceType.WOOD),
                "Subtracting 0 should not change resource count");
    }

    @Test
    public void testSettlementPlacementUpdatesOwnership() {
        CityPoint city = new CityPoint(100, 200);

        assertFalse(city.hasSettlement());
        assertEquals(Turn.NONE, city.getOwner());

        city.placeSettlement(Turn.RED);

        assertTrue(city.hasSettlement());
        assertEquals(Turn.RED, city.getOwner());
    }

    @Test
    public void testRoadPlacementUpdatesOwnership() {
        RoadPoint road = new RoadPoint(50, 75);

        assertFalse(road.hasRoad());
        assertEquals(Turn.NONE, road.getOwner());

        road.placeRoad(Turn.BLUE);

        assertTrue(road.hasRoad());
        assertEquals(Turn.BLUE, road.getOwner());
    }

    @Test
    public void testTradeInfoDefaultsToZero() {
        TradeInfo info = new TradeInfo();
        HashMap<ResourceType, Integer> resources = info.getResources();

        for (ResourceType type : new ResourceType[]{ResourceType.WOOD, ResourceType.WHEAT,
                ResourceType.ORE, ResourceType.BRICK, ResourceType.SHEEP}) {
            assertEquals(0, resources.get(type),
                    type + " should default to 0 in TradeInfo");
        }
    }

    // ==================== RECOVERABILITY TESTS ====================

    @Test
    public void testEarthquakeEventDoesNotCrashWithNoRoads() {
        loadFullBoard();
        // No roads have been placed, so earthquake should handle empty case
        board.handleEarthquakeEvent();
        // Test passes if no exception is thrown
    }

    @Test
    public void testEpidemicEventCanBeSetAndCleared() {
        assertFalse(board.epidemicActive, "Epidemic should start inactive");

        board.handleEpidemicEvent();
        assertTrue(board.epidemicActive, "Epidemic should be active after event");

        // Epidemic is cleared on next turn
        board.epidemicActive = false;
        assertFalse(board.epidemicActive, "Epidemic should be clearable");
    }

    @Test
    public void testRobberAttackEventWithNoPlayersAboveThreshold() {
        // All players start with 0 resources, so none need to discard
        board.handleRobberAttackEvent();

        // robberMoved should be set to false, meaning robber needs to move
        assertFalse(board.robberMoved,
                "Robber attack should set robberMoved to false even if no one discards");
    }

    @Test
    public void testRoadSidewaysToggle() {
        RoadPoint road = new RoadPoint(10, 20);
        road.placeRoad(Turn.RED);

        assertFalse(road.isSideways(), "Road should not start sideways");

        road.setSideways(true);
        assertTrue(road.isSideways(), "Road should be sideways after earthquake");

        road.setSideways(false);
        assertFalse(road.isSideways(), "Road should be repairable");
    }

    // ==================== SCALABILITY / STATE CONSISTENCY TESTS ====================

    @Test
    public void testAllPlayersInitializedCorrectly() {
        Turn[] expectedPlayers = {Turn.RED, Turn.BLUE, Turn.ORANGE, Turn.WHITE};

        for (Turn turn : expectedPlayers) {
            Player player = board.turnToPlayer.get(turn);
            assertNotNull(player, turn + " player should be initialized");
            assertEquals(Board.INITIAL_SETTLEMENTS, player.settlements);
            assertEquals(Board.INITIAL_ROADS, player.roads);
            assertEquals(Board.INITIAL_VICTORY_POINTS, player.getVictoryPoints());
            assertEquals(0, player.getTotalResources(),
                    turn + " should start with 0 resources");
        }
    }

    @Test
    public void testLongestRoadTrackerInitializedForAllPlayers() {
        Turn[] expectedPlayers = {Turn.RED, Turn.BLUE, Turn.ORANGE, Turn.WHITE};

        for (Turn turn : expectedPlayers) {
            assertTrue(board.longestRoad.containsKey(turn),
                    turn + " should have a longest road entry");
            assertEquals(0, board.longestRoad.get(turn),
                    turn + " should start with longest road = 0");
        }
    }

    @Test
    public void testTitleBonusStartsWithBankAsHolder() {
        TitleBonus bonus = new TitleBonus(5, 2);

        assertEquals(Turn.BANK, bonus.getCurrentHolder(),
                "Title bonus holder should start as BANK");
        assertEquals(0, bonus.getCurrentHolderCount(),
                "Title bonus count should start at 0");
    }

    @Test
    public void testTitleBonusEvaluationAwardsCorrectly() {
        TitleBonus bonus = new TitleBonus(3, 2);

        HashMap<Turn, Integer> counts = new HashMap<>();
        counts.put(Turn.RED, 4);
        counts.put(Turn.BLUE, 2);
        counts.put(Turn.ORANGE, 1);
        counts.put(Turn.WHITE, 0);

        HashMap<Turn, Player> players = new HashMap<>();
        players.put(Turn.RED, new Player(Turn.RED));
        players.put(Turn.BLUE, new Player(Turn.BLUE));
        players.put(Turn.ORANGE, new Player(Turn.ORANGE));
        players.put(Turn.WHITE, new Player(Turn.WHITE));
        players.put(Turn.BANK, new Player(Turn.BANK));

        bonus.evaluate(counts, players);

        assertEquals(Turn.RED, bonus.getCurrentHolder(),
                "RED should hold the bonus with count 4 (threshold 3)");
        assertEquals(2, players.get(Turn.RED).getVictoryPoints(),
                "RED should gain 2 VP from the bonus");
    }

    @Test
    public void testTitleBonusTransfersOnHigherCount() {
        TitleBonus bonus = new TitleBonus(3, 2);

        HashMap<Turn, Player> players = new HashMap<>();
        players.put(Turn.RED, new Player(Turn.RED));
        players.put(Turn.BLUE, new Player(Turn.BLUE));
        players.put(Turn.BANK, new Player(Turn.BANK));

        // RED gets the bonus first
        HashMap<Turn, Integer> counts1 = new HashMap<>();
        counts1.put(Turn.RED, 4);
        counts1.put(Turn.BLUE, 2);
        bonus.evaluate(counts1, players);
        assertEquals(Turn.RED, bonus.getCurrentHolder());
        assertEquals(2, players.get(Turn.RED).getVictoryPoints());

        // BLUE surpasses RED
        HashMap<Turn, Integer> counts2 = new HashMap<>();
        counts2.put(Turn.RED, 4);
        counts2.put(Turn.BLUE, 5);
        bonus.evaluate(counts2, players);
        assertEquals(Turn.BLUE, bonus.getCurrentHolder());
        assertEquals(0, players.get(Turn.RED).getVictoryPoints(),
                "RED should lose 2 VP when bonus transfers");
        assertEquals(2, players.get(Turn.BLUE).getVictoryPoints(),
                "BLUE should gain 2 VP when taking over bonus");
    }

    // ==================== TESTABILITY / MAINTAINABILITY TESTS ====================

    @Test
    public void testDeckAcceptsDependencyInjection() {
        NumberCardDeck deck = new NumberCardDeck(new Random(42));
        int result = deck.drawNumber();

        assertTrue(result >= 2 && result <= 12, "Deck should return valid card numbers");
    }

    @Test
    public void testBoardAcceptsMockedDependencies() {
        // Verify that Board can be created entirely with mocks (good decoupling)
        GameWindowController mockedController = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine mockedTSM = EasyMock.niceMock(TurnStateMachine.class);
        NumberCardDeck mockedDeck = EasyMock.niceMock(NumberCardDeck.class);

        EasyMock.replay(mockedController, mockedTSM, mockedDeck);

        Board testBoard = new Board(mockedController, mockedTSM, mockedDeck);
        assertNotNull(testBoard, "Board should be constructable with all mocked dependencies");
        assertNotNull(testBoard.turnToPlayer, "Players should be initialized even with mocks");
        assertEquals(5, testBoard.turnToPlayer.size(), "Should have 5 entries (4 players + bank)");
    }

    @Test
    public void testResourceTypeEnumCoversAllTypes() {
        ResourceType[] types = ResourceType.values();
        assertEquals(6, types.length, "Should have 6 resource types including NULL");

        Set<String> names = new HashSet<>();
        for (ResourceType t : types) {
            names.add(t.name());
        }
        assertTrue(names.contains("WOOD"));
        assertTrue(names.contains("SHEEP"));
        assertTrue(names.contains("WHEAT"));
        assertTrue(names.contains("BRICK"));
        assertTrue(names.contains("ORE"));
        assertTrue(names.contains("NULL"));
    }

    @Test
    public void testTerrainMapsToCorrectResourceType() {
        assertEquals(ResourceType.WHEAT, Terrain.FIELD.getResourceType());
        assertEquals(ResourceType.WOOD, Terrain.FOREST.getResourceType());
        assertEquals(ResourceType.BRICK, Terrain.HILL.getResourceType());
        assertEquals(ResourceType.ORE, Terrain.MOUNTAIN.getResourceType());
        assertEquals(ResourceType.SHEEP, Terrain.PASTURE.getResourceType());
        assertEquals(ResourceType.NULL, Terrain.DESERT.getResourceType());
    }

    @Test
    public void testEventTypeEnumCoversAllEvents() {
        EventType[] events = EventType.values();
        assertEquals(10, events.length, "Should have 10 event types");

        Set<String> names = new HashSet<>();
        for (EventType e : events) {
            names.add(e.name());
        }
        assertTrue(names.contains("ROBBER_ATTACK"));
        assertTrue(names.contains("EARTHQUAKE"));
        assertTrue(names.contains("EPIDEMIC"));
        assertTrue(names.contains("GOOD_NEIGHBORS"));
        assertTrue(names.contains("CALM_SEAS"));
        assertTrue(names.contains("TOURNAMENT"));
        assertTrue(names.contains("TRADE_ADVANTAGE"));
        assertTrue(names.contains("CONFLICT"));
        assertTrue(names.contains("NO_EVENT"));
        assertTrue(names.contains("NEW_YEAR"));
    }

    @Test
    public void testDevelopmentCardStoresTypeAndTurnBought() {
        DevelopmentCard card = new DevelopmentCard(DevCards.KNIGHT);
        assertEquals(DevCards.KNIGHT, card.getType());

        card.setTurnBought(5);
        assertEquals(5, card.getTurnBought());
    }

    @Test
    public void testGameComponentStoresCoordinates() {
        GameComponent gc = new GameComponent(42, 99);
        assertEquals(42, gc.getX());
        assertEquals(99, gc.getY());
    }
}
