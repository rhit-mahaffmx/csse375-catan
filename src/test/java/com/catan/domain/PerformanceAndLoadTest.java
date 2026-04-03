package com.catan.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Q4 - Performance and Load Testing (Agile Testing Quadrants)
 *
 * These tests verify that critical game operations complete within
 * acceptable time bounds, even under heavy load or worst-case scenarios.
 * They are technology-facing tests that critique the product.
 */
public class PerformanceAndLoadTest {

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

    // ==================== Board Initialization Performance ====================

    @Test
    public void testBoardInitializationCompletesWithinTimeLimit() {
        long startTime = System.nanoTime();

        loadFullBoard();

        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;
        assertTrue(elapsedMs < 2000,
                "Board initialization took " + elapsedMs + "ms, expected < 2000ms");
    }

    @Test
    public void testRepeatedBoardInitializationDoesNotDegrade() {
        long[] times = new long[10];

        for (int i = 0; i < 10; i++) {
            EasyMock.reset(mockController, mockTurnStateMachine, mockDeck);
            EasyMock.replay(mockController, mockTurnStateMachine, mockDeck);
            board = new Board(mockController, mockTurnStateMachine, mockDeck);

            long startTime = System.nanoTime();
            loadFullBoard();
            times[i] = (System.nanoTime() - startTime) / 1_000_000;
        }

        // The last initialization should not be significantly slower than the first
        // Allow 5x tolerance for JIT warmup
        assertTrue(times[9] < times[0] * 5 + 500,
                "Board initialization degraded: first=" + times[0] + "ms, last=" + times[9] + "ms");
    }

    // ==================== Longest Road Algorithm Performance ====================

    @Test
    public void testLongestRoadCalculationWithManyRoads() {
        loadFullBoard();

        // Place 15 roads for RED (the maximum) in a connected chain
        int roadsToPlace = Math.min(15, board.roadPoints.size());
        for (int i = 0; i < roadsToPlace; i++) {
            board.roadPoints.get(i).placeRoad(Turn.RED);
        }

        long startTime = System.nanoTime();
        board.updateLongestRoad();
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 1000,
                "Longest road calculation with " + roadsToPlace + " roads took "
                        + elapsedMs + "ms, expected < 1000ms");
    }

    @Test
    public void testLongestRoadCalculationWithAllPlayersMaxRoads() {
        loadFullBoard();

        // Distribute roads across all 4 players
        Turn[] players = {Turn.RED, Turn.BLUE, Turn.ORANGE, Turn.WHITE};
        int roadsPerPlayer = board.roadPoints.size() / 4;
        for (int p = 0; p < players.length; p++) {
            for (int i = 0; i < roadsPerPlayer; i++) {
                int roadIndex = p * roadsPerPlayer + i;
                if (roadIndex < board.roadPoints.size()) {
                    board.roadPoints.get(roadIndex).placeRoad(players[p]);
                }
            }
        }

        long startTime = System.nanoTime();
        board.updateLongestRoad();
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 2000,
                "Longest road calculation with all players' roads took "
                        + elapsedMs + "ms, expected < 2000ms");
    }

    // ==================== Resource Distribution Performance ====================

    @Test
    public void testResourceDistributionWithFullBoardSettlements() {
        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        loadFullBoard();

        // Place settlements on many city points
        Turn[] players = {Turn.RED, Turn.BLUE, Turn.ORANGE, Turn.WHITE};
        for (int i = 0; i < board.cityPoints.size(); i++) {
            board.cityPoints.get(i).placeSettlement(players[i % 4]);
        }

        board.numRolled = 6;

        long startTime = System.nanoTime();
        board.giveResourcesToBorderingSettlements();
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 500,
                "Resource distribution took " + elapsedMs + "ms, expected < 500ms");
    }

    @Test
    public void testResourceDistributionRepeatedRolls() {
        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);

        loadFullBoard();

        // Place some settlements
        Turn[] players = {Turn.RED, Turn.BLUE, Turn.ORANGE, Turn.WHITE};
        for (int i = 0; i < 20 && i < board.cityPoints.size(); i++) {
            board.cityPoints.get(i).placeSettlement(players[i % 4]);
        }

        long startTime = System.nanoTime();
        // Simulate 100 dice rolls worth of resource distribution
        for (int roll = 2; roll <= 12; roll++) {
            board.numRolled = roll;
            for (int repeat = 0; repeat < 10; repeat++) {
                board.giveResourcesToBorderingSettlements();
            }
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 2000,
                "100+ resource distributions took " + elapsedMs + "ms, expected < 2000ms");
    }

    // ==================== Point Lookup Performance ====================

    @Test
    public void testCityLookupPerformanceUnderLoad() {
        loadFullBoard();

        long startTime = System.nanoTime();
        // Simulate 1000 city lookups (as would happen with many clicks)
        for (int i = 0; i < 1000; i++) {
            board.getCityAtCoords(100 + (i % 50), 200 + (i % 50));
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 1000,
                "1000 city lookups took " + elapsedMs + "ms, expected < 1000ms");
    }

    @Test
    public void testRoadLookupPerformanceUnderLoad() {
        loadFullBoard();

        long startTime = System.nanoTime();
        // Simulate 1000 road lookups
        for (int i = 0; i < 1000; i++) {
            board.getRoadAtCoords(100 + (i % 50), 200 + (i % 50));
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 1000,
                "1000 road lookups took " + elapsedMs + "ms, expected < 1000ms");
    }

    // ==================== Dice Roll Performance ====================

    @Test
    public void testDeckDrawPerformanceUnderLoad() {
        NumberCardDeck realDeck = new NumberCardDeck(new Random());

        long startTime = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            realDeck.drawNumber();
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 500,
                "10000 card draws took " + elapsedMs + "ms, expected < 500ms");
    }

    // ==================== Player Resource Operations Performance ====================

    @Test
    public void testPlayerResourceOperationsUnderLoad() {
        Player player = new Player(Turn.RED);

        long startTime = System.nanoTime();
        // Simulate heavy resource churn: 10000 add/sub cycles
        for (int i = 0; i < 10000; i++) {
            player.addResources(ResourceType.WOOD, 3);
            player.addResources(ResourceType.BRICK, 2);
            player.addResources(ResourceType.SHEEP, 1);
            player.subResources(ResourceType.WOOD, 1);
            player.subResources(ResourceType.BRICK, 1);
            player.getTotalResources();
            player.canPayForSettlement();
            player.canPayForRoad();
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 1000,
                "10000 resource operation cycles took " + elapsedMs + "ms, expected < 1000ms");
    }

    // ==================== Trade Performance ====================

    @Test
    public void testMultipleTradesPerformance() {
        loadFullBoard();

        EasyMock.reset(mockTurnStateMachine);
        EasyMock.expect(mockTurnStateMachine.getHasRolled()).andReturn(true).anyTimes();
        EasyMock.expect(mockTurnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(mockTurnStateMachine);
        board.robberMoved = true;

        // Give RED and BLUE plenty of resources
        board.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 10000);
        board.turnToPlayer.get(Turn.BLUE).addResources(ResourceType.BRICK, 10000);

        long startTime = System.nanoTime();
        for (int i = 0; i < 500; i++) {
            TradeInfo offer1 = new TradeInfo();
            offer1.setPlayer(Turn.RED);
            offer1.setResources(ResourceType.WOOD, 1);

            TradeInfo offer2 = new TradeInfo();
            offer2.setPlayer(Turn.BLUE);
            offer2.setResources(ResourceType.BRICK, 1);

            board.onTradeSubmitClick(offer1, offer2);
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 2000,
                "500 trades took " + elapsedMs + "ms, expected < 2000ms");
    }

    // ==================== Proximity Check Performance ====================

    @Test
    public void testWithinTwoRoadsCheckPerformance() {
        loadFullBoard();

        CityPoint city1 = board.cityPoints.get(0);
        CityPoint city2 = board.cityPoints.get(board.cityPoints.size() - 1);

        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            board.withinTwoRoads(city1, city2);
        }
        long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

        assertTrue(elapsedMs < 2000,
                "1000 proximity checks took " + elapsedMs + "ms, expected < 2000ms");
    }
}
