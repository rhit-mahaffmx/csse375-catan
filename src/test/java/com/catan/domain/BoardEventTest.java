package com.catan.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class BoardEventTest {

    private Board createBoard() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        Dice dice = EasyMock.niceMock(Dice.class);
        EasyMock.expect(tsm.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(gwc, tsm, dice);
        return new Board(gwc, tsm, dice);
    }

    private Board createBoardWithMocks(GameWindowController gwc, TurnStateMachine tsm) {
        Dice dice = EasyMock.niceMock(Dice.class);
        EasyMock.replay(dice);
        return new Board(gwc, tsm, dice);
    }

    // ==================== Epidemic Tests ====================

    @Test
    public void testEpidemicSetsFlag() {
        Board board = createBoard();
        assertFalse(board.epidemicActive);
        board.handleEpidemicEvent();
        assertTrue(board.epidemicActive);
    }

    @Test
    public void testHandleEventEpidemic() {
        Board board = createBoard();
        board.handleEvent(EventType.EPIDEMIC);
        assertTrue(board.epidemicActive);
    }

    @Test
    public void testEpidemicCitiesProduceOneResource() {
        Board board = createBoard();

        CityPoint city = new CityPoint(100, 100);
        city.setTileValues(
                java.util.List.of(5),
                java.util.List.of(Terrain.FOREST)
        );
        city.placeSettlement(Turn.RED);
        city.isCity = true;

        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(city);
        board.numRolled = 5;
        board.robberResource = ResourceType.NULL;
        board.robberNumber = 10;

        board.handleEpidemicEvent();
        board.giveResourcesToBorderingSettlements();

        Player red = board.turnToPlayer.get(Turn.RED);
        assertEquals(1, red.getResource(ResourceType.WOOD));
    }

    @Test
    public void testNonEpidemicCitiesProduceTwoResources() {
        Board board = createBoard();

        CityPoint city = new CityPoint(100, 100);
        city.setTileValues(
                java.util.List.of(5),
                java.util.List.of(Terrain.FOREST)
        );
        city.placeSettlement(Turn.RED);
        city.isCity = true;

        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(city);
        board.numRolled = 5;
        board.robberResource = ResourceType.NULL;
        board.robberNumber = 10;

        board.giveResourcesToBorderingSettlements();

        Player red = board.turnToPlayer.get(Turn.RED);
        assertEquals(2, red.getResource(ResourceType.WOOD));
    }

    @Test
    public void testEpidemicSettlementsStillProduceOne() {
        Board board = createBoard();

        CityPoint city = new CityPoint(100, 100);
        city.setTileValues(
                java.util.List.of(5),
                java.util.List.of(Terrain.FOREST)
        );
        city.placeSettlement(Turn.RED);

        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(city);
        board.numRolled = 5;
        board.robberResource = ResourceType.NULL;
        board.robberNumber = 10;

        board.handleEpidemicEvent();
        board.giveResourcesToBorderingSettlements();

        Player red = board.turnToPlayer.get(Turn.RED);
        assertEquals(1, red.getResource(ResourceType.WOOD));
    }

    // ==================== Earthquake Tests ====================

    @Test
    public void testEarthquakeMarksSidewaysRoad() {
        Board board = createBoard();
        board.rand = new Random(42);

        RoadPoint road = new RoadPoint(10, 10);
        road.placeRoad(Turn.RED);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(road);
        board.cityPoints = new ArrayList<>();

        board.handleEarthquakeEvent();

        assertTrue(road.isSideways());
    }

    @Test
    public void testEarthquakeSkipsPlayersWithNoRoads() {
        Board board = createBoard();
        board.rand = new Random(42);

        RoadPoint road = new RoadPoint(10, 10);
        road.placeRoad(Turn.BLUE);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(road);
        board.cityPoints = new ArrayList<>();

        board.handleEarthquakeEvent();

        assertTrue(road.isSideways());
    }

    @Test
    public void testEarthquakeDoesNotDoubleSideways() {
        Board board = createBoard();
        board.rand = new Random(42);

        RoadPoint road1 = new RoadPoint(10, 10);
        road1.placeRoad(Turn.RED);
        road1.setSideways(true);

        RoadPoint road2 = new RoadPoint(20, 20);
        road2.placeRoad(Turn.RED);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(road1);
        board.roadPoints.add(road2);
        board.cityPoints = new ArrayList<>();

        board.handleEarthquakeEvent();

        assertTrue(road1.isSideways());
        assertTrue(road2.isSideways());
    }

    @Test
    public void testSidewaysRoadDoesNotCountForLongestRoad() {
        Board board = createBoard();

        CityPoint c1 = new CityPoint(0, 0);
        CityPoint c2 = new CityPoint(10, 0);
        CityPoint c3 = new CityPoint(20, 0);

        RoadPoint r1 = new RoadPoint(5, 0);
        r1.placeRoad(Turn.RED);
        r1.neighbors = new ArrayList<>();
        r1.neighbors.add(c1);
        r1.neighbors.add(c2);

        RoadPoint r2 = new RoadPoint(15, 0);
        r2.placeRoad(Turn.RED);
        r2.setSideways(true);
        r2.neighbors = new ArrayList<>();
        r2.neighbors.add(c2);
        r2.neighbors.add(c3);

        c1.neighbors = new ArrayList<>();
        c1.neighbors.add(r1);
        c2.neighbors = new ArrayList<>();
        c2.neighbors.add(r1);
        c2.neighbors.add(r2);
        c3.neighbors = new ArrayList<>();
        c3.neighbors.add(r2);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(r1);
        board.roadPoints.add(r2);
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(c1);
        board.cityPoints.add(c2);
        board.cityPoints.add(c3);

        board.updateLongestRoad();

        assertEquals(1, board.longestRoad.get(Turn.RED));
    }

    @Test
    public void testSidewaysRoadStillAllowsTraversal() {
        Board board = createBoard();

        CityPoint c1 = new CityPoint(0, 0);
        CityPoint c2 = new CityPoint(10, 0);
        CityPoint c3 = new CityPoint(20, 0);
        CityPoint c4 = new CityPoint(30, 0);

        RoadPoint r1 = new RoadPoint(5, 0);
        r1.placeRoad(Turn.RED);

        RoadPoint r2 = new RoadPoint(15, 0);
        r2.placeRoad(Turn.RED);
        r2.setSideways(true);

        RoadPoint r3 = new RoadPoint(25, 0);
        r3.placeRoad(Turn.RED);

        r1.neighbors = new ArrayList<>();
        r1.neighbors.add(c1);
        r1.neighbors.add(c2);

        r2.neighbors = new ArrayList<>();
        r2.neighbors.add(c2);
        r2.neighbors.add(c3);

        r3.neighbors = new ArrayList<>();
        r3.neighbors.add(c3);
        r3.neighbors.add(c4);

        c1.neighbors = new ArrayList<>();
        c1.neighbors.add(r1);
        c2.neighbors = new ArrayList<>();
        c2.neighbors.add(r1);
        c2.neighbors.add(r2);
        c3.neighbors = new ArrayList<>();
        c3.neighbors.add(r2);
        c3.neighbors.add(r3);
        c4.neighbors = new ArrayList<>();
        c4.neighbors.add(r3);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(r1);
        board.roadPoints.add(r2);
        board.roadPoints.add(r3);
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(c1);
        board.cityPoints.add(c2);
        board.cityPoints.add(c3);
        board.cityPoints.add(c4);

        board.updateLongestRoad();

        assertEquals(2, board.longestRoad.get(Turn.RED));
    }

    // ==================== Repair Road Tests ====================

    @Test
    public void testRepairRoad() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        EasyMock.expect(tsm.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(gwc, tsm);

        Board board = createBoardWithMocks(gwc, tsm);

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 1);
        red.addResources(ResourceType.BRICK, 1);

        RoadPoint road = new RoadPoint(10, 10);
        road.placeRoad(Turn.RED);
        road.setSideways(true);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(road);
        board.cityPoints = new ArrayList<>();

        board.repairRoad(10, 10);

        assertFalse(road.isSideways());
        assertEquals(0, red.getResource(ResourceType.WOOD));
        assertEquals(0, red.getResource(ResourceType.BRICK));
    }

    @Test
    public void testRepairRoadCannotAfford() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        EasyMock.expect(tsm.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(gwc, tsm);

        Board board = createBoardWithMocks(gwc, tsm);

        RoadPoint road = new RoadPoint(10, 10);
        road.placeRoad(Turn.RED);
        road.setSideways(true);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(road);
        board.cityPoints = new ArrayList<>();

        board.repairRoad(10, 10);

        assertTrue(road.isSideways());
    }

    @Test
    public void testRepairRoadWrongOwner() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        EasyMock.expect(tsm.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(gwc, tsm);

        Board board = createBoardWithMocks(gwc, tsm);

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 1);
        red.addResources(ResourceType.BRICK, 1);

        RoadPoint road = new RoadPoint(10, 10);
        road.placeRoad(Turn.BLUE);
        road.setSideways(true);

        board.roadPoints = new ArrayList<>();
        board.roadPoints.add(road);

        board.repairRoad(10, 10);

        assertTrue(road.isSideways());
    }

    // ==================== Good Neighbors Tests ====================

    @Test
    public void testGoodNeighborsPassesResources() {
        Board board = createBoard();

        board.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 1);
        board.turnToPlayer.get(Turn.BLUE).addResources(ResourceType.BRICK, 1);
        board.turnToPlayer.get(Turn.ORANGE).addResources(ResourceType.WHEAT, 1);
        board.turnToPlayer.get(Turn.WHITE).addResources(ResourceType.ORE, 1);

        HashMap<Turn, ResourceType> choices = new HashMap<>();
        choices.put(Turn.RED, ResourceType.WOOD);
        choices.put(Turn.BLUE, ResourceType.BRICK);
        choices.put(Turn.ORANGE, ResourceType.WHEAT);
        choices.put(Turn.WHITE, ResourceType.ORE);

        board.executeGoodNeighbors(choices);

        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));

        assertEquals(0, board.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.BRICK));
        assertEquals(1, board.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WOOD));

        assertEquals(0, board.turnToPlayer.get(Turn.ORANGE).getResource(ResourceType.WHEAT));
        assertEquals(1, board.turnToPlayer.get(Turn.ORANGE).getResource(ResourceType.BRICK));

        assertEquals(0, board.turnToPlayer.get(Turn.WHITE).getResource(ResourceType.ORE));
        assertEquals(1, board.turnToPlayer.get(Turn.WHITE).getResource(ResourceType.WHEAT));
    }

    @Test
    public void testGoodNeighborsPlayerWithNoCards() {
        Board board = createBoard();

        board.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 1);

        HashMap<Turn, ResourceType> choices = new HashMap<>();
        choices.put(Turn.RED, ResourceType.WOOD);

        board.executeGoodNeighbors(choices);

        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, board.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WOOD));
    }

    // ==================== Calm Seas Tests ====================

    @Test
    public void testCalmSeasNoHarbors() {
        Board board = createBoard();
        ArrayList<Turn> winners = board.getCalmSeasWinners();
        assertTrue(winners.isEmpty());
    }

    @Test
    public void testCalmSeasOneWinner() {
        Board board = createBoard();
        board.harborSettlements.put(Turn.RED, 2);
        board.harborSettlements.put(Turn.BLUE, 1);

        ArrayList<Turn> winners = board.getCalmSeasWinners();
        assertEquals(1, winners.size());
        assertEquals(Turn.RED, winners.get(0));
    }

    @Test
    public void testCalmSeasTiedWinners() {
        Board board = createBoard();
        board.harborSettlements.put(Turn.RED, 2);
        board.harborSettlements.put(Turn.BLUE, 2);

        ArrayList<Turn> winners = board.getCalmSeasWinners();
        assertEquals(2, winners.size());
        assertTrue(winners.contains(Turn.RED));
        assertTrue(winners.contains(Turn.BLUE));
    }

    // ==================== Tournament Tests ====================

    @Test
    public void testTournamentNoKnights() {
        Board board = createBoard();
        ArrayList<Turn> winners = board.getTournamentWinners();
        assertTrue(winners.isEmpty());
    }

    @Test
    public void testTournamentOneWinner() {
        Board board = createBoard();
        board.turnToPlayer.get(Turn.RED).numKnightsPlayed = 3;
        board.turnToPlayer.get(Turn.BLUE).numKnightsPlayed = 1;

        ArrayList<Turn> winners = board.getTournamentWinners();
        assertEquals(1, winners.size());
        assertEquals(Turn.RED, winners.get(0));
    }

    @Test
    public void testTournamentTiedWinners() {
        Board board = createBoard();
        board.turnToPlayer.get(Turn.RED).numKnightsPlayed = 2;
        board.turnToPlayer.get(Turn.ORANGE).numKnightsPlayed = 2;

        ArrayList<Turn> winners = board.getTournamentWinners();
        assertEquals(2, winners.size());
        assertTrue(winners.contains(Turn.RED));
        assertTrue(winners.contains(Turn.ORANGE));
    }

    // ==================== Give Resource Tests ====================

    @Test
    public void testGiveResourceToPlayer() {
        Board board = createBoard();
        board.giveResourceToPlayer(Turn.RED, ResourceType.ORE);
        assertEquals(1, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
    }

    // ==================== Trade Advantage Tests ====================

    @Test
    public void testTradeAdvantageNoHolder() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        EasyMock.replay(gwc, tsm);

        Board board = createBoardWithMocks(gwc, tsm);
        board.handleTradeAdvantageEvent();
        assertTrue(board.eligiblePlayers.isEmpty());
    }

    @Test
    public void testTradeAdvantageWithHolder() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        EasyMock.replay(gwc, tsm);

        Board board = createBoardWithMocks(gwc, tsm);

        board.longestRoad.put(Turn.RED, 6);
        board.longestRoadBonus.evaluate(board.longestRoad, board.turnToPlayer);

        board.turnToPlayer.get(Turn.BLUE).addResources(ResourceType.WHEAT, 3);

        board.handleTradeAdvantageEvent();

        assertFalse(board.eligiblePlayers.isEmpty());
    }

    // ==================== Conflict Tests ====================

    @Test
    public void testConflictNoHolder() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        EasyMock.replay(gwc, tsm);

        Board board = createBoardWithMocks(gwc, tsm);
        board.handleConflictEvent();
        assertTrue(board.eligiblePlayers.isEmpty());
    }

    @Test
    public void testConflictWithHolder() {
        GameWindowController gwc = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine tsm = EasyMock.niceMock(TurnStateMachine.class);
        EasyMock.replay(gwc, tsm);

        Board board = createBoardWithMocks(gwc, tsm);

        HashMap<Turn, Integer> knightCounts = new HashMap<>();
        knightCounts.put(Turn.RED, 5);
        knightCounts.put(Turn.BLUE, 0);
        knightCounts.put(Turn.ORANGE, 0);
        knightCounts.put(Turn.WHITE, 0);
        board.largestArmyBonus.evaluate(knightCounts, board.turnToPlayer);

        board.turnToPlayer.get(Turn.BLUE).addResources(ResourceType.ORE, 2);

        board.handleConflictEvent();

        assertFalse(board.eligiblePlayers.isEmpty());
    }

    // ==================== Steal From Player Tests ====================

    @Test
    public void testStealFromPlayer() {
        Board board = createBoard();

        board.turnToPlayer.get(Turn.BLUE).addResources(ResourceType.WHEAT, 3);

        Random seeded = new Random(42);
        board.stealFromPlayer(Turn.RED, Turn.BLUE, seeded);

        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        assertEquals(1, red.getResource(ResourceType.WHEAT));
        assertEquals(2, blue.getResource(ResourceType.WHEAT));
    }

    @Test
    public void testStealFromPlayerEmpty() {
        Board board = createBoard();

        Random seeded = new Random(42);
        board.stealFromPlayer(Turn.RED, Turn.BLUE, seeded);

        Player red = board.turnToPlayer.get(Turn.RED);
        assertEquals(0, red.getResource(ResourceType.WHEAT));
    }

    // ==================== Robber Attack Event Tests ====================

    @Test
    public void testRobberAttackSetsRobberMoved() {
        Board board = createBoard();
        board.robberMoved = true;

        board.handleRobberAttackEvent();

        assertFalse(board.robberMoved);
    }

    // ==================== No Event / New Year Tests ====================

    @Test
    public void testNoEventDoesNothing() {
        Board board = createBoard();

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 5);
        int woodBefore = red.getResource(ResourceType.WOOD);

        board.handleEvent(EventType.NO_EVENT);

        assertEquals(woodBefore, red.getResource(ResourceType.WOOD));
    }

    @Test
    public void testNewYearPlaceholder() {
        Board board = createBoard();
        board.handleEvent(EventType.NEW_YEAR);
    }

    // ==================== RoadPoint Sideways Tests ====================

    @Test
    public void testRoadPointSidewaysDefault() {
        RoadPoint road = new RoadPoint(0, 0);
        assertFalse(road.isSideways());
    }

    @Test
    public void testRoadPointSetSideways() {
        RoadPoint road = new RoadPoint(0, 0);
        road.setSideways(true);
        assertTrue(road.isSideways());
        road.setSideways(false);
        assertFalse(road.isSideways());
    }
}


