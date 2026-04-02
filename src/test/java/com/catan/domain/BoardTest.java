package com.catan.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.catan.datasource.BoardDataInputs;
import com.catan.presentation.GameWindow;

public class BoardTest {

    private ArrayList<CityPoint> helpCreateCities(Board board) {
        try {
            FileInputStream coordStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
            FileInputStream terrainStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
            FileInputStream tileStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
            return board.createCities(coordStream, terrainStream, tileStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound Exception creating cities in tests: " + e.getMessage());
        }
    }

    private ArrayList<RoadPoint> helpCreateRoads(Board board) {
        try {
            FileInputStream coordStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
            return board.createRoads(coordStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound Exception creating roads in tests: " + e.getMessage());
        }
    }

    private ArrayList<HarborPoint> helpCreateHarbors(Board board) {
        try {
            FileInputStream harborStream = new FileInputStream(Board.HARBORS_FILE_PATH);
            return board.createHarbors(harborStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound Exception creating harbors in tests: " + e.getMessage());
        }
    }

    private void helpAddAllCityNeighbors(Board board, ArrayList<CityPoint> cities, ArrayList<RoadPoint> roads) {
        try {
            FileInputStream stream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
            board.addAllCityNeighbors(cities, roads, stream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound Exception adding city neighbors in tests: " + e.getMessage());
        }
    }

    private void helpAddAllRoadNeighbors(Board board, ArrayList<RoadPoint> roads, ArrayList<CityPoint> cities) {
        try {
            FileInputStream stream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
            board.addAllRoadNeighbors(roads, cities, stream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound Exception adding road neighbors in tests: " + e.getMessage());
        }
    }

    private FileInputStream createMockFileInputStreamFromString(String data) throws IOException {
        File tempFile = File.createTempFile("test_board_init_", ".txt");
        tempFile.deleteOnExit();
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(data);
        }
        return new FileInputStream(tempFile);
    }

    private String readFileContentToString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        File file = new File(filePath);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String currentLine;
            boolean firstLine = true;
            while ((currentLine = br.readLine()) != null) {
                if (!firstLine) {
                    contentBuilder.append("\n");
                }
                contentBuilder.append(currentLine);
                firstLine = false;
            }
        }
        return contentBuilder.toString();
    }

    private String getActualCityCoordsData() throws IOException {
        return readFileContentToString(Board.CITY_COORDINATES_FILE_PATH);
    }

    private String getActualCityTerrainData() throws IOException {
        return readFileContentToString(Board.TERRAIN_COORDINATES_FILE_PATH);
    }

    private String getActualCityValuesData() throws IOException {
        return readFileContentToString(Board.TILE_VALUE_COORDINATES_FILE_PATH);
    }

    private String getActualHarborsData() throws IOException {
        return readFileContentToString(Board.HARBORS_FILE_PATH);
    }

    private String getActualRoadCoordsData() throws IOException {
        return readFileContentToString(Board.ROAD_COORDINATES_FILE_PATH);
    }

    private String getActualCityNeighborsData() throws IOException {
        return readFileContentToString(Board.CITY_NEIGHBORS_FILEPATH);
    }

    private String getActualRoadNeighborsData() throws IOException {
        return readFileContentToString(Board.ROAD_NEIGHBORS_FILEPATH);
    }

    private String getActualRobberCoordsData() throws IOException {
        return readFileContentToString(Board.ROBBER_COORDINATES_FILE_PATH);
    }

    private String getActualRobberResourceData() throws IOException {
        return readFileContentToString(Board.ROBBER_RESOURCE_FILE_PATH);
    }

    private String getActualRobberNumberData() throws IOException {
        return readFileContentToString(Board.ROBBER_NUMBER_FILE_PATH);
    }


    @Test
    public void testCreateAllCities() {
        GameWindowController gameWindowController = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        assertEquals(Board.NUM_CITYPOINTS, helpCreateCities(testBoard).size());
    }

    @Test
    public void testCreateAllRoads() {
        GameWindowController gameWindowController = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        assertEquals(Board.NUM_ROADPOINTS, helpCreateRoads(testBoard).size());
    }

    @Test
    public void testCreateAllRobberPoints() {
        GameWindowController gameWindowController = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        try {
            FileInputStream coordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
            FileInputStream resourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
            FileInputStream numberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);
            assertEquals(19, testBoard.createRobberPoints(coordsStream, resourceStream, numberStream).size());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound Exception while testing: " + e.getMessage());
        }
    }

    @Test
    public void testAddAllCityNeighbors() {
        GameWindowController gameWindowController = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        ArrayList<CityPoint> cityPoints = helpCreateCities(testBoard);
        ArrayList<RoadPoint> roadPoints = helpCreateRoads(testBoard);

        helpAddAllCityNeighbors(testBoard, cityPoints, roadPoints);

        assertEquals(Board.NUM_CITYPOINTS, cityPoints.size());
    }

    @Test
    public void testAddAllRoadNeighbors() {
        GameWindowController gameWindowController = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        ArrayList<RoadPoint> roadPoints = helpCreateRoads(testBoard);
        ArrayList<CityPoint> cityPoints = helpCreateCities(testBoard);

        helpAddAllRoadNeighbors(testBoard, roadPoints, cityPoints);

        assertEquals(Board.NUM_ROADPOINTS, roadPoints.size());
    }

    @Test
    public void testAddAllRobberPoints() {
        GameWindowController gameWindowController = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        ArrayList<RobberPoint> robberPoints;
        try {
            FileInputStream coordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
            FileInputStream resourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
            FileInputStream numberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);
            robberPoints = testBoard.createRobberPoints(coordsStream, resourceStream, numberStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFound Exception while testing: " + e.getMessage());
        }

        for (int i = 0; i < 19; i++) {
            RobberPoint robberPoint = robberPoints.get(i);
            gameWindowController.placeRobberButton(EasyMock.eq(testBoard), EasyMock.eq(robberPoint.getX()), EasyMock.eq(robberPoint.getY()));
        }

        EasyMock.replay(gameWindowController);

        testBoard.addAllRobberPoints(robberPoints);
        EasyMock.verify(gameWindowController);
    }

    @Test
    public void testAddAllRoads() {
        GameWindowController gameWindowController = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);
        ArrayList<RoadPoint> roadPoints = helpCreateRoads(testBoard);

        for (int i = 0; i < Board.NUM_ROADPOINTS; i++) {
            RoadPoint roadPoint = roadPoints.get(i);
            gameWindowController.placeRoadButton(EasyMock.eq(testBoard), EasyMock.eq(roadPoint.getX()), EasyMock.eq(roadPoint.getY()));
        }

        EasyMock.replay(gameWindowController);

        testBoard.addAllRoadPoints(roadPoints);
        EasyMock.verify(gameWindowController);
    }

    @Test
    public void testAddAllCitiesTopRow() {
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        ArrayList<CityPoint> cityPoints = helpCreateCities(testBoard);

        for (int i = 0; i < Board.NUM_CITYPOINTS; i++) {
            CityPoint cityPoint = cityPoints.get(i);
            gameWindowController.placeCityButton(EasyMock.eq(testBoard), EasyMock.eq(cityPoint.getX()), EasyMock.eq(cityPoint.getY()));
        }

        EasyMock.replay(gameWindowController);

        testBoard.addAllCities(cityPoints);
        EasyMock.verify(gameWindowController);
    }

    @Test
    public void testAddCityPlacesTerrain() {
        GameWindowController testWindowController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testWindowController, turnStateMachine, dice);
        ArrayList<CityPoint> cityPoints = helpCreateCities(testBoard);
        for (CityPoint cityPoint : cityPoints) {
            assertFalse(cityPoint.getTerrains().isEmpty());
        }
    }

    @Test
    public void testAddCityPlacesCorrectTerrain() {
        GameWindowController testWindowController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testWindowController, turnStateMachine, dice);
        ArrayList<CityPoint> cityPoints = helpCreateCities(testBoard);
        assertEquals(Terrain.HILL, cityPoints.getFirst().getTerrains().getFirst());
        assertEquals(Terrain.FIELD, cityPoints.getLast().getTerrains().getFirst());
    }

    @Test
    public void testAddCityAssignsNumbers() {
        GameWindowController testWindowController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testWindowController, turnStateMachine, dice);
        ArrayList<CityPoint> cityPoints = helpCreateCities(testBoard);
        for (CityPoint cityPoint : cityPoints) {
            assertFalse(cityPoint.getTileValues().isEmpty());
        }
    }

    @Test
    public void testAddCityAssignsValueCorrectly() throws IOException {
        GameWindowController testWindowController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testWindowController, turnStateMachine, dice);
        ArrayList<CityPoint> cityPoints = helpCreateCities(testBoard);
        assertEquals(8, cityPoints.getFirst().getTileValues().getFirst());
        assertEquals(4, cityPoints.getLast().getTileValues().getFirst());
    }

    @Test
    public void testStartGame() {
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        gameWindowController.startGame();

        EasyMock.replay(gameWindowController);
        testBoard.startGame();

        EasyMock.verify(gameWindowController);
    }

    @Test
    public void testGetTurnColor() {
        // Record
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        // Replay
        EasyMock.replay(turnStateMachine);
        Turn turn = testBoard.getTurn();

        // Verify
        EasyMock.verify(turnStateMachine);
        assertEquals(Turn.RED, turn);
    }

    @Test
    public void testNextTurn() {
        //Record
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);


        // Replay
        EasyMock.replay(turnStateMachine);
        Turn firstTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn secondTurn = testBoard.getTurn();

        // Verify
        EasyMock.verify(turnStateMachine);
        assertEquals(Turn.RED, firstTurn);
        assertEquals(Turn.BLUE, secondTurn);
    }

    @Test
    public void testMultipleTurns() {
        //Record
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);

        // Replay
        EasyMock.replay(turnStateMachine);
        Turn firstTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn secondTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn thirdTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn fourthTurn = testBoard.getTurn();

        // Verify
        EasyMock.verify(turnStateMachine);
        assertEquals(Turn.RED, firstTurn);
        assertEquals(Turn.BLUE, secondTurn);
        assertEquals(Turn.ORANGE, thirdTurn);
        assertEquals(Turn.WHITE, fourthTurn);
    }

    @Test
    public void testNextTurnUpdatesDisplay() {
        // Setup mocks
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        // Mock initial state
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        Player player = testBoard.turnToPlayer.get(Turn.RED);
        player.settlements = 2; // Ensure not initial
        player.roads = 2;

        // Mock next turn
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        Player nextPlayer = testBoard.turnToPlayer.get(Turn.BLUE);

        // Capture the TurnStateData passed to showInitialTurnState
        Capture<TurnStateData> capturedTurnData = EasyMock.newCapture();
        controllerTest.showInitialTurnState(EasyMock.capture(capturedTurnData));

        // Other calls
        controllerTest.clearDevCards();
        controllerTest.showDevCards(EasyMock.anyObject(Board.class), EasyMock.anyObject());
        controllerTest.showResourceCards(EasyMock.anyObject(Board.class), EasyMock.anyObject());

        // Replay
        EasyMock.replay(turnStateMachine, controllerTest);

        // Assume robber moved
        testBoard.robberMoved = true;

        // Call onNextTurnClick
        testBoard.onNextTurnClick();

        // Verify
        EasyMock.verify(turnStateMachine, controllerTest);

        // Check that the captured TurnStateData has the correct turn
        TurnStateData turnData = capturedTurnData.getValue();
        assertEquals(Turn.BLUE, turnData.turn);
    }

    @Test
    public void testOverflowTurns() {
        //Record
        GameWindowController gameWindowController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(gameWindowController, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        // Replay
        EasyMock.replay(turnStateMachine);
        Turn firstTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn secondTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn thirdTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn fourthTurn = testBoard.getTurn();
        testBoard.nextTurn();
        Turn fifthTurn = testBoard.getTurn();


        // Verify
        EasyMock.verify(turnStateMachine);
        assertEquals(Turn.RED, firstTurn);
        assertEquals(Turn.BLUE, secondTurn);
        assertEquals(Turn.ORANGE, thirdTurn);
        assertEquals(Turn.WHITE, fourthTurn);
        assertEquals(Turn.RED, fifthTurn);
    }

    @Test
    public void testGetRedsInitialSettlements() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        assertEquals(5, testBoard.getPlayersSettlements(Turn.RED));
    }

    @Test
    public void testGetRedsInitialRoads() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        assertEquals(15, testBoard.getPlayersRoads(Turn.RED));
    }

    @Test
    public void testShowInitialTurnState() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);

        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(turnStateMachine);
        EasyMock.replay(controllerTest);

        testBoard.showInitialTurnState();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testShowInitialRobberState() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        controllerTest.showInitialRobberState(220, 504);

        EasyMock.replay(controllerTest);

        testBoard.showInitialRobberState();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testOnClickCity() {
        CityPoint testPoint = new CityPoint(1, 1);

        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(testPoint));
        RoadPoint testRoad = new RoadPoint(10, 10);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoad));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showCity(TurnStateMachine.FIRST_TURN, 1, 1);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(1, 1);
        assertTrue(testPoint.hasSettlement());
        assertEquals(Turn.RED, testPoint.getOwner());
    }

    @Test
    public void testOnClickCityMultipleTurnsVerifyOwner() {
        CityPoint testPoint = new CityPoint(1, 1);

        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(testPoint));
        RoadPoint testRoad = new RoadPoint(10, 10);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoad));

        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.replay(turnStateMachine);

        testBoard.nextTurn();
        testBoard.onCityPointClick(1, 1);
        assertTrue(testPoint.hasSettlement());
        assertEquals(Turn.BLUE, testPoint.getOwner());
    }

    @Test
    public void testOnClickCityPlaceAndDecreaseSettlements() {
        CityPoint testPoint = new CityPoint(1, 1);
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(testPoint));
        RoadPoint testRoad = new RoadPoint(10, 10);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoad));

        controllerTest.showSettlement(testBoard.getTurn(), testPoint.getX(), testPoint.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.replay(controllerTest);

        testBoard.onCityPointClick(1, 1);

        EasyMock.verify(controllerTest);
        assertTrue(testPoint.hasSettlement());
        assertEquals(TurnStateMachine.FIRST_TURN, testPoint.getOwner());
        assertEquals(Board.INITIAL_SETTLEMENTS - 1, testBoard.getPlayersSettlements(TurnStateMachine.FIRST_TURN));
    }

    @Test
    public void testClickOnCityWithExistingSettlement() {
        CityPoint testPoint = new CityPoint(1, 1);
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(testPoint));
        RoadPoint testRoad = new RoadPoint(10, 10);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoad));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        controllerTest.showSettlement(TurnStateMachine.FIRST_TURN, testPoint.getX(), testPoint.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(1, 1);
        testBoard.onCityPointClick(1, 1);

        EasyMock.verify(controllerTest);
        assertTrue(testPoint.hasSettlement());
        assertEquals(TurnStateMachine.FIRST_TURN, testPoint.getOwner());
        assertEquals(Board.INITIAL_SETTLEMENTS - 1, testBoard.getPlayersSettlements(TurnStateMachine.FIRST_TURN));
    }

    @Test
    public void testClickOnCityWithOtherPlayersSettlement() {
        CityPoint testPoint = new CityPoint(1, 1);
        RoadPoint testRoad = new RoadPoint(10, 10);
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(testPoint));
        testBoard.roadPoints = new ArrayList<>(List.of(testRoad));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);

        controllerTest.showSettlement(TurnStateMachine.FIRST_TURN, testPoint.getX(), testPoint.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(1, 1);
        testBoard.nextTurn();
        testBoard.onCityPointClick(1, 1);

        EasyMock.verify(controllerTest);
        assertTrue(testPoint.hasSettlement());
        assertEquals(TurnStateMachine.FIRST_TURN, testPoint.getOwner());
        assertEquals(Board.INITIAL_SETTLEMENTS - 1, testBoard.getPlayersSettlements(TurnStateMachine.FIRST_TURN));
        assertEquals(Board.INITIAL_SETTLEMENTS, testBoard.getPlayersSettlements(Turn.BLUE));
    }

    @Test
    public void testOnClickRoad() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad());
        assertEquals(Turn.RED, testBoard.getRoadAtCoords(421, 338).getOwner());
    }

    @Test
    public void testOnClickRoadMultipleTurnsVerifyOwner() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);

        EasyMock.replay(turnStateMachine);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        testBoard.nextTurn();
        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad());
        assertEquals(Turn.BLUE, testBoard.getRoadAtCoords(421, 338).getOwner());
    }

    @Test
    public void testClickOnRoadWithExistingRoad() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.replay(turnStateMachine);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        testBoard.nextTurn();
        testBoard.onCityPointClick(391, 320);
        testBoard.getRoadAtCoords(421, 338).hasRoad = true;
        testBoard.getRoadAtCoords(421, 338).owner = Turn.BLUE;
        testBoard.onRoadPointClick(421, 338);

        EasyMock.verify(turnStateMachine);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad);
        assertEquals(Turn.BLUE, testBoard.getRoadAtCoords(421, 338).getOwner());
        assertEquals(Board.INITIAL_ROADS, testBoard.getPlayersRoads(TurnStateMachine.FIRST_TURN));
    }

    @Test
    public void testClickOnRoadWithOtherPlayersRoad() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showSettlement(TurnStateMachine.FIRST_TURN, 391, 320);
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        controllerTest.showRoad(TurnStateMachine.FIRST_TURN, 421, 338);
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.replay(controllerTest);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        testBoard.nextTurn();
        testBoard.onRoadPointClick(421, 338);

        EasyMock.verify(controllerTest);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad);
        assertEquals(TurnStateMachine.FIRST_TURN, testBoard.getRoadAtCoords(421, 338).getOwner());
        assertEquals(Board.INITIAL_ROADS - 1, testBoard.getPlayersRoads(TurnStateMachine.FIRST_TURN));
        assertEquals(Board.INITIAL_ROADS, testBoard.getPlayersRoads(Turn.BLUE));
    }

    @Test
    public void testOnNextTurnClickAfterPlacedSettlementAndRoad() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 1;
        player.roads = Board.INITIAL_ROADS - 1;
        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        controllerTest.clearDevCards();
        controllerTest.showDevCards(testBoard, new ArrayList<>());
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);
        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(controllerTest, turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testNextTurnBeforePlaceFirstSettlementNextTurnNotCalled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testNextTurnBeforePlaceFirstRoadNextTurnNotCalled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 1;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);

        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testNextTurnBeforePlaceSecondSettlementNextTurnNotCalled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 1;
        player.roads = Board.INITIAL_ROADS - 1;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);

    }

    @Test
    public void testNextTurnBeforePlaceSecondRoadNextTurnNotCalled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 2;
        player.roads = Board.INITIAL_ROADS - 1;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);

    }

    @Test
    public void testNextTurnAfterPlaceSecondRoad() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 2;
        player.roads = Board.INITIAL_ROADS - 2;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        controllerTest.clearDevCards();
        controllerTest.showDevCards(testBoard, new ArrayList<>());
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);
        controllerTest.showResourceCards(testBoard, expectedResources);
        controllerTest.showInitialTurnState(EasyMock.anyObject());

        EasyMock.replay(turnStateMachine, controllerTest);
        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine, controllerTest);

    }

    @Test
    public void testNextTurnBeforeRollDiceNextTurnNotCalled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 2;
        player.roads = Board.INITIAL_ROADS - 2;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);

        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testNextTurnAfterRollDice() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 2;
        player.roads = Board.INITIAL_ROADS - 2;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        controllerTest.clearDevCards();
        controllerTest.showDevCards(testBoard, new ArrayList<>());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testNextTurnWithAllSettlementsAfterRound1IsAllowed() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS;
        player.roads = Board.INITIAL_ROADS - 2;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE).times(3);
        controllerTest.clearDevCards();
        controllerTest.showDevCards(testBoard, new ArrayList<>());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);

    }

    @Test
    public void testNextTurnWithAllRoadsAfterRound1IsAllowed() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player = new Player(TurnStateMachine.FIRST_TURN);
        player.settlements = Board.INITIAL_SETTLEMENTS - 2;
        player.roads = Board.INITIAL_ROADS;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.put(TurnStateMachine.FIRST_TURN, player);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE).times(3);
        controllerTest.clearDevCards();
        controllerTest.showDevCards(testBoard, new ArrayList<>());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(turnStateMachine);

        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine);

    }

    @Test
    public void testAddNextTurnButton() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        controllerTest.addNextTurnButton(testBoard);

        EasyMock.replay(controllerTest);

        testBoard.addNextTurnButton();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testPlaceMoreThanOneSettlement() {
        CityPoint testPoint = new CityPoint(1, 1);
        CityPoint testPoint2 = new CityPoint(2, 2);

        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(Arrays.asList(testPoint, testPoint2));
        RoadPoint testRoad = new RoadPoint(10, 10);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoad));

        testBoard.onCityPointClick(1, 1);
        testBoard.onCityPointClick(2, 2);
        assertTrue(testPoint.hasSettlement());
        assertFalse(testPoint2.hasSettlement());
    }

    @Test
    public void testPlaceMoreThanOneRoad() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);


        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        testBoard.onRoadPointClick(364, 338);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad());
        assertFalse(testBoard.getRoadAtCoords(364, 338).hasRoad());
    }

    @Test
    public void testGetRoadAtCoords() {
        RoadPoint testRoadPoint = new RoadPoint(1, 1);

        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoadPoint));

        RoadPoint testReturn = testBoard.getRoadAtCoords(1, 1);
        assertEquals(testRoadPoint, testReturn);
    }

    @Test
    public void testGetNonExistentRoadAtCoordsReturnsNullRoad() {
        RoadPoint testRoadPoint = new RoadPoint(1, 1);
        RoadPoint testNullRoadPoint = new RoadPoint(-1, -1);

        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoadPoint));

        RoadPoint testReturn = testBoard.getRoadAtCoords(-100, -100);
        assertEquals(testNullRoadPoint.getX(), testReturn.getX());
        assertEquals(testNullRoadPoint.getY(), testReturn.getY());
    }

    @Test
    public void testPlaceRoadOnNeighbors() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad());
    }

    @Test
    public void testPlaceRoadNotOnNeighbors() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(331, 390);
        assertFalse(testBoard.getRoadAtCoords(331, 390).hasRoad());
    }

    @Test
    public void testGetCityFromCoords() {
        CityPoint testCityPoint = new CityPoint(1, 1);

        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(testCityPoint));

        CityPoint testReturn = testBoard.getCityAtCoords(1, 1);
        assertEquals(testCityPoint, testReturn);
    }

    @Test
    public void testGetNonExistentCityAtCoordsReturnsNullCity() {
        CityPoint testCityPoint = new CityPoint(1, 1);
        CityPoint testNullCityPoint = new CityPoint(-1, -1);

        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(testCityPoint));

        CityPoint testReturn = testBoard.getCityAtCoords(-100, -100);
        assertEquals(testNullCityPoint.getX(), testReturn.getX());
        assertEquals(testNullCityPoint.getY(), testReturn.getY());
    }

    @Test
    public void testCheckCityNeighborsDepthGreaterThanTwo() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        CityPoint neighborCity = EasyMock.mock(CityPoint.class);
        CityPoint pointToCheck = EasyMock.mock(CityPoint.class);

        assertFalse(testBoard.checkCityNeighbors(neighborCity, pointToCheck, 2));
    }

    @Test
    public void testWithinTwoRoadsTrue() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        CityPoint testCity1 = testBoard.getCityAtCoords(391, 320);
        CityPoint testCity2 = testBoard.getCityAtCoords(330, 352);

        assertTrue(testBoard.withinTwoRoads(testCity1, testCity2));
    }

    @Test
    public void testPlaceWithinTwoRoadsFails() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = EasyMock.partialMockBuilder(Board.class)
                .addMockedMethod("withinTwoRoads")
                .addMockedMethod("updateLongestRoad")
                .withConstructor(GameWindowController.class, TurnStateMachine.class, Dice.class)
                .withArgs(controllerTest, turnStateMachine, dice)
                .createMock();

        CityPoint ownedCity = new CityPoint(1, 1);
        ownedCity.hasSettlement = true;
        ownedCity.owner = Turn.RED;

        CityPoint cityToClick = new CityPoint(2, 2);

        testBoard.cityPoints = new ArrayList<>(List.of(ownedCity, cityToClick));


        EasyMock.expect(testBoard.withinTwoRoads(ownedCity, cityToClick)).andReturn(true);
        testBoard.updateLongestRoad();

        EasyMock.replay(testBoard);

        testBoard.onCityPointClick(2, 2);
        assertFalse(cityToClick.hasSettlement);

        EasyMock.verify(testBoard);
    }

    @Test
    public void testCheckRoadNeighborsDepthOfTwo() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        RoadPoint roadPoint = EasyMock.mock(RoadPoint.class);
        CityPoint cityPoint = EasyMock.mock(CityPoint.class);

        assertFalse(testBoard.checkRoadNeighbors(roadPoint, cityPoint, 2));
    }

    @Test
    public void testWithinTwoRoadsFalse() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        CityPoint testCity1 = testBoard.getCityAtCoords(391, 320);
        CityPoint testCity2 = testBoard.getCityAtCoords(272, 320);

        assertFalse(testBoard.withinTwoRoads(testCity1, testCity2));
    }

    @Test
    public void testPlayerTriesToPlaceTwoSettlementsInFirstTurn() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testCity1 = testBoard.getCityAtCoords(391, 320);
        CityPoint testCity2 = testBoard.getCityAtCoords(272, 320);

        controllerTest.showSettlement(testBoard.getTurn(), testCity1.getX(), testCity1.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.replay(controllerTest);

        testBoard.onCityPointClick(391, 320);
        testBoard.onCityPointClick(272, 320);

        EasyMock.verify(controllerTest);
        assertTrue(testCity1.hasSettlement());
        assertEquals(TurnStateMachine.FIRST_TURN, testCity1.getOwner());
        assertEquals(Board.INITIAL_SETTLEMENTS - 1, testBoard.getPlayersSettlements(TurnStateMachine.FIRST_TURN));

        assertFalse(testCity2.hasSettlement());
        assertEquals(Turn.NONE, testCity2.getOwner());
        assertEquals(Board.INITIAL_SETTLEMENTS - 1, testBoard.getPlayersSettlements(TurnStateMachine.FIRST_TURN));

    }

    @Test
    public void testPlayerTriesToPlaceTwoRoadsInFirstTurn() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testCity1 = testBoard.getCityAtCoords(391, 320);
        RoadPoint testRoad1 = testBoard.getRoadAtCoords(421, 338);
        RoadPoint testRoad2 = testBoard.getRoadAtCoords(364, 338);

        controllerTest.showSettlement(testBoard.getTurn(), testCity1.getX(), testCity1.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        controllerTest.showRoad(testBoard.getTurn(), testRoad1.getX(), testRoad1.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.replay(controllerTest);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        testBoard.onRoadPointClick(364, 338);

        EasyMock.verify(controllerTest);
        assertTrue(testRoad1.hasRoad());
        assertEquals(TurnStateMachine.FIRST_TURN, testRoad1.getOwner());
        assertEquals(Board.INITIAL_ROADS - 1, testBoard.getPlayersRoads(TurnStateMachine.FIRST_TURN));

        assertFalse(testRoad2.hasRoad);
        assertEquals(Turn.NONE, testRoad2.getOwner());
        assertEquals(Board.INITIAL_ROADS - 1, testBoard.getPlayersRoads(TurnStateMachine.FIRST_TURN));
    }

    @Test
    public void testPlaceSecondSettlementValid() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(273, 251);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WOOD, 1);
        expectedResources.put(ResourceType.BRICK, 1);
        expectedResources.put(ResourceType.WHEAT, 1);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, expectedResources);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());

        EasyMock.verify(controllerTest);

        assertTrue(testRedSecondCity.hasSettlement);
        assertEquals(Turn.RED, testRedCity.owner);

        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
    }

    @Test
    public void testPlaceSecondRoadValid() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(273, 251);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);
        RoadPoint testRedSecondRoad = testBoard.getRoadAtCoords(271, 288);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WOOD, 1);
        expectedResources.put(ResourceType.BRICK, 1);
        expectedResources.put(ResourceType.WHEAT, 1);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, expectedResources);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showRoad(Turn.RED, testRedSecondRoad.getX(), testRedSecondRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, expectedResources);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());
        testBoard.onRoadPointClick(testRedSecondRoad.getX(), testRedSecondRoad.getY());

        EasyMock.verify(controllerTest);

        assertTrue(testRedSecondRoad.hasRoad);
        assertEquals(Turn.RED, testRedSecondRoad.owner);
    }

    @Test
    public void testPlaceSecondRoadValidDoNotCostResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.BRICK, 1);
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));


        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;


        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 2);
        expectedResources.put(ResourceType.WOOD, 2);
        expectedResources.put(ResourceType.WHEAT, 1);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);


        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(273, 251);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);
        RoadPoint testRedSecondRoad = testBoard.getRoadAtCoords(271, 288);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(testBoard.turnToPlayer.get(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(testBoard.turnToPlayer.get(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showResourceCards(EasyMock.same(testBoard), EasyMock.eq(expectedResources));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        Player playerForResources = new Player(Turn.RED);
        playerForResources.addResources(ResourceType.WHEAT, 1);
        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, expectedResources);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showRoad(Turn.RED, testRedSecondRoad.getX(), testRedSecondRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(playerForResources));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());
        testBoard.turnToPlayer.get(Turn.RED).payForRoad(); // get rid of resources
        testBoard.turnToPlayer.get(Turn.RED).payForRoad(); // get rid of resources
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        testBoard.onRoadPointClick(testRedSecondRoad.getX(), testRedSecondRoad.getY());

        EasyMock.verify(controllerTest);

        assertTrue(testRedSecondRoad.hasRoad);
        assertEquals(Turn.RED, testRedSecondRoad.owner);
    }


    @Test
    public void testPlaceSecondRoadValidDoesNotSpendResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.BRICK, 1);
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));


        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(273, 251);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);
        RoadPoint testRedSecondRoad = testBoard.getRoadAtCoords(271, 288);


        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(testBoard.turnToPlayer.get(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(testBoard.turnToPlayer.get(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.ORANGE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.WHITE)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);

        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WOOD, 2);
        expectedResources.put(ResourceType.BRICK, 2);
        expectedResources.put(ResourceType.WHEAT, 1);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);
        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, expectedResources);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showRoad(Turn.RED, testRedSecondRoad.getX(), testRedSecondRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, expectedResources);


        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());
        testBoard.onRoadPointClick(testRedSecondRoad.getX(), testRedSecondRoad.getY());

        EasyMock.verify(controllerTest);

        assertTrue(testRedSecondRoad.hasRoad);
        assertEquals(Turn.RED, testRedSecondRoad.owner);
        // They were given a wood and brick after placing second settlement
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }


    @Test
    public void testPlaceTwoSettlementsInSecondTurn() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(273, 251);
        CityPoint testRedThirdCity = testBoard.getCityAtCoords(391, 320);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 1);
        expectedResources.put(ResourceType.WOOD, 1);
        expectedResources.put(ResourceType.WHEAT, 1);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, expectedResources);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).times(2);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());
        testBoard.onCityPointClick(testRedThirdCity.getX(), testRedThirdCity.getY());

        EasyMock.verify(controllerTest);

        assertFalse(testRedThirdCity.hasSettlement);
    }

    @Test
    public void testPlaceTwoRoadsInSecondTurn() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(273, 251);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);
        RoadPoint testRedSecondRoad = testBoard.getRoadAtCoords(271, 288);
        RoadPoint testRedThirdRoad = testBoard.getRoadAtCoords(305, 232);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);

        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 1);
        expectedResources.put(ResourceType.WOOD, 1);
        expectedResources.put(ResourceType.WHEAT, 1);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WHEAT, 1);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));
        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).times(2);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showRoad(Turn.RED, testRedSecondRoad.getX(), testRedSecondRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));


        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);


        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());
        testBoard.onRoadPointClick(testRedSecondRoad.getX(), testRedSecondRoad.getY());
        testBoard.onRoadPointClick(testRedThirdRoad.getX(), testRedThirdRoad.getY());

        EasyMock.verify(controllerTest);

        assertFalse(testRedThirdRoad.hasRoad);
    }

    @Test
    public void testAddDiceRollButton() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = new TurnStateMachine();
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        controllerTest.addDiceRollButton(testBoard);

        EasyMock.replay(controllerTest);

        testBoard.addDiceRollButton();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testOnClickDiceRollButtonUpdateNumRolled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(6);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDiceRoll(6);

        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, dice, controllerTest);


        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice, controllerTest);
        assertEquals(6, testBoard.numRolled);
    }

    @Test
    public void testRollNumberWithOneTilePlayerHasZeroBuildings() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;
        cityPoint.setTileValues(List.of(12), List.of(Terrain.PASTURE));

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(12);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);

        controllerTest.showDiceRoll(12);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, dice, controllerTest);


        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice, controllerTest);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));

    }


    @Test
    public void testRollNumberWithOneTilePlayerHasOneBuilding() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;
        cityPoint.setTileValues(List.of(12), List.of(Terrain.PASTURE));

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(12);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        controllerTest.showDiceRoll(12);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 1);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, dice, controllerTest);


        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.SHEEP));

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice, controllerTest);
        assertEquals(1, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.SHEEP));

    }

    @Test
    public void testRollNumberDontGiveToNonOwned() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;
        cityPoint.setTileValues(List.of(12), List.of(Terrain.PASTURE));

        CityPoint cityPointTwo = new CityPoint(2, 2);
        cityPointTwo.owner = Turn.NONE;
        cityPointTwo.hasSettlement = false;
        cityPointTwo.setTileValues(List.of(12), List.of(Terrain.PASTURE));

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPointTwo));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(12);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);

        controllerTest.showDiceRoll(12);

        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 1);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, dice, controllerTest);


        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.SHEEP));

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice, controllerTest);
        assertEquals(1, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.SHEEP));
    }

    @Test
    public void testRollNumberDontGiveToWrongNumber() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;
        cityPoint.setTileValues(List.of(12), List.of(Terrain.PASTURE));

        CityPoint cityPointTwo = new CityPoint(2, 2);
        cityPointTwo.owner = Turn.BLUE;
        cityPointTwo.hasSettlement = true;
        cityPointTwo.setTileValues(List.of(10), List.of(Terrain.FOREST));

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPointTwo));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(12);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDiceRoll(12);

        HashMap<ResourceType, Integer> expected = new HashMap<>();
        expected.put(ResourceType.SHEEP, 1);
        expected.put(ResourceType.ORE, 0);
        expected.put(ResourceType.WHEAT, 0);
        expected.put(ResourceType.WOOD, 0);
        expected.put(ResourceType.BRICK, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expected));
        EasyMock.replay(turnStateMachine, dice, controllerTest);


        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.WOOD));

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice, controllerTest);
        assertEquals(1, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.WOOD));
    }

    @Test
    public void testRollNumberOnePlayerHasMultipleSettlements() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;
        cityPoint.setTileValues(List.of(8), List.of(Terrain.MOUNTAIN));

        CityPoint cityPointTwo = new CityPoint(2, 2);
        cityPointTwo.owner = TurnStateMachine.FIRST_TURN;
        cityPointTwo.hasSettlement = true;
        cityPointTwo.setTileValues(List.of(8), List.of(Terrain.MOUNTAIN));

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPointTwo));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(8);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN).times(1);

        EasyMock.replay(turnStateMachine, dice);

        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice);
        assertEquals(2, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));
    }

    @Test
    public void testRollNumberMoreThanOnePlayerHasOneSettlements() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;
        cityPoint.setTileValues(List.of(8), List.of(Terrain.MOUNTAIN));

        CityPoint cityPointTwo = new CityPoint(2, 2);
        cityPointTwo.owner = Turn.BLUE;
        cityPointTwo.hasSettlement = true;
        cityPointTwo.setTileValues(List.of(8), List.of(Terrain.MOUNTAIN));

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPointTwo));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(8);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);

        controllerTest.showDiceRoll(8);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 1);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, dice, controllerTest);


        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.ORE));


        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice, controllerTest);
        assertEquals(1, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.ORE));
    }

    @Test
    public void testRoll7AllowsRobberToBeMoved() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(7);
        EasyMock.replay(turnStateMachine, dice);

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice);

        assertFalse(testBoard.robberMoved);
    }

    @Test
    public void testFriendlyRobberExactly2VP() {
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(null, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(6), List.of(Terrain.FIELD));
        cityPoint.placeSettlement(Turn.BLUE);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        Player bluePlayer = testBoard.turnToPlayer.get(Turn.BLUE);
        bluePlayer.addVictoryPoints(2);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Set<Player> eligiblePlayers = testBoard.getEligiblePlayersToRob(robberPoint);

        assertTrue(eligiblePlayers.contains(bluePlayer));
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testNextTurnBeforeMoveRobberFails() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(7);
        EasyMock.replay(turnStateMachine, dice);

        testBoard.onRollDiceClick();
        testBoard.onNextTurnClick();

        EasyMock.verify(turnStateMachine, dice);

        EasyMock.verify(turnStateMachine);

    }

    @Test
    public void testRoll7NoResourcesGathered() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(7);
        EasyMock.replay(turnStateMachine, dice);

        ArrayList<Turn> turns = new ArrayList<>(List.of(Turn.RED, Turn.BLUE, Turn.ORANGE, Turn.WHITE));
        ArrayList<ResourceType> resources = new ArrayList<>(List.of(ResourceType.ORE, ResourceType.WOOD, ResourceType.BRICK, ResourceType.SHEEP, ResourceType.WHEAT));

        for (Turn turn : turns) {
            for (ResourceType resourceType : resources) {
                assertEquals(0, testBoard.turnToPlayer.get(turn).getResource(resourceType));
            }
        }

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice);

        for (Turn turn : turns) {
            for (ResourceType resourceType : resources) {
                assertEquals(0, testBoard.turnToPlayer.get(turn).getResource(resourceType));
            }
        }
    }

    @Test
    public void testRollNon7DoesNotAllowRobberToBeMoved() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(6);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);

        controllerTest.showDiceRoll(6);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, dice, controllerTest);


        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice, controllerTest);

        assertTrue(testBoard.robberMoved);
    }

    @Test
    public void testClickDiceTwiceInOnceTurn() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        EasyMock.expect(dice.roll()).andReturn(6);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        controllerTest.showDiceRoll(6);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(ResourceType.SHEEP, 0);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, dice, controllerTest);


        testBoard.onRollDiceClick();
        testBoard.onRollDiceClick();

        EasyMock.verify(dice, controllerTest);
        assertEquals(6, testBoard.numRolled);

    }

    @Test
    public void testClickRollDiceInFirstTurnDontRoll() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showInvalidInputAndPass("Cannot roll dice until third turn");
        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, controllerTest);
    }

    @Test
    public void testClickRollDiceInSecondTurnDontRoll() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        cityPoint.hasSettlement = true;

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showInvalidInputAndPass("Cannot roll dice until third turn");
        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, controllerTest);
    }


    @Test
    public void testShowDice() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        controllerTest.showDiceRoll(7);


        EasyMock.replay(controllerTest);


        testBoard.showDiceRoll(7);


        EasyMock.verify(controllerTest);
    }

    @Test
    public void testPlaceSecondSettlementGiveResources() {
        GameWindowController controllerTest = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(273, 251);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());

        EasyMock.verify(controllerTest);

        assertTrue(testRedSecondCity.hasSettlement);
        assertEquals(Turn.RED, testRedCity.owner);
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
    }

    @Test
    public void testPlaceSecondSettlementResourcesBorderingDesert() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        HashMap<ResourceType, Integer> expectedMap = new HashMap<>();
        expectedMap.put(ResourceType.SHEEP, 1);
        expectedMap.put(ResourceType.ORE, 1);
        expectedMap.put(ResourceType.WHEAT, 0);
        expectedMap.put(ResourceType.WOOD, 0);
        expectedMap.put(ResourceType.BRICK, 0);


        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        CityPoint testBlueCity = testBoard.getCityAtCoords(575, 147);
        CityPoint testOrangeCity = testBoard.getCityAtCoords(571, 630);
        CityPoint testWhiteCity = testBoard.getCityAtCoords(213, 630);
        CityPoint testRedSecondCity = testBoard.getCityAtCoords(212, 423);

        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);
        RoadPoint testBlueRoad = testBoard.getRoadAtCoords(544, 132);
        RoadPoint testOrangeRoad = testBoard.getRoadAtCoords(545, 648);
        RoadPoint testWhiteRoad = testBoard.getRoadAtCoords(240, 648);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.BLUE, testBlueCity.getX(), testBlueCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.BLUE, testBlueRoad.getX(), testBlueRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.ORANGE, testOrangeCity.getX(), testOrangeCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.ORANGE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.ORANGE, testOrangeRoad.getX(), testOrangeRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.WHITE, testWhiteCity.getX(), testWhiteCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.WHITE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showRoad(Turn.WHITE, testWhiteRoad.getX(), testWhiteRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedMap));
        controllerTest.showSettlement(Turn.RED, testRedSecondCity.getX(), testRedSecondCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.SHEEP, 1);
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testBlueCity.getX(), testBlueCity.getY());
        testBoard.onRoadPointClick(testBlueRoad.getX(), testBlueRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testOrangeCity.getX(), testOrangeCity.getY());
        testBoard.onRoadPointClick(testOrangeRoad.getX(), testOrangeRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testWhiteCity.getX(), testWhiteCity.getY());
        testBoard.onRoadPointClick(testWhiteRoad.getX(), testWhiteRoad.getY());
        testBoard.nextTurn();

        testBoard.onCityPointClick(testRedSecondCity.getX(), testRedSecondCity.getY());

        EasyMock.verify(controllerTest);

        assertTrue(testRedSecondCity.hasSettlement);
        assertEquals(Turn.RED, testRedCity.owner);
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testPlayerHasNoSettlementResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertFalse(testRedCity.hasSettlement);
    }

    @Test
    public void testPlaceSettlementNoBrick() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 0);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertFalse(testRedCity.hasSettlement);
    }

    @Test
    public void testPlaceSettlementNoWheat() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 0);

        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertFalse(testRedCity.hasSettlement);
    }

    @Test
    public void testPlaceSettlementNoWood() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 0);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertFalse(testRedCity.hasSettlement);
    }

    @Test
    public void testPlaceSettlementNoSheep() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 0);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertFalse(testRedCity.hasSettlement);
    }

    @Test
    public void testPlayerHasSettlementResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertTrue(testRedCity.hasSettlement);
    }

    @Test
    public void testClickOnOtherPlayersSettlementDoesNotUpgrade() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        Player blue = new Player(Turn.BLUE);
        blue.addResources(ResourceType.WOOD, 1);
        blue.addResources(ResourceType.BRICK, 1);
        blue.addResources(ResourceType.SHEEP, 1);
        blue.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);
        testBoard.turnToPlayer.put(Turn.BLUE, blue);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.nextTurn();
        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertTrue(testRedCity.hasSettlement);
        assertEquals(Turn.RED, testRedCity.owner);
        assertFalse(testRedCity.isCity);
    }


    @Test
    public void testPlayerHasSettlementResourcesButHasNotRolled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertFalse(testRedCity.hasSettlement);
    }

    @Test
    public void testPlayerHasSettlementResourcesButHasNotMovedRobberIfNecessary() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.robberMoved = false;

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertFalse(testRedCity.hasSettlement);
    }


    @Test
    public void testPlayerHasSettlementResourcesUpdatesResourceCount() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        EasyMock.verify(controllerTest);
        assertTrue(testRedCity.hasSettlement);
    }


    @Test
    public void testDecrementResourcesOnPlace() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.WHEAT, 1);

        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testPlaceCityDecrementNonZero() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.WHEAT, 2);
        player.addResources(ResourceType.ORE, 2);

        Player playerAfterPayment = new Player(Turn.RED);
        playerAfterPayment.addResources(ResourceType.WOOD, 1);
        playerAfterPayment.addResources(ResourceType.BRICK, 1);
        playerAfterPayment.addResources(ResourceType.SHEEP, 1);
        playerAfterPayment.addResources(ResourceType.WHEAT, 1);
        playerAfterPayment.addResources(ResourceType.ORE, 2);

        testBoard.turnToPlayer.put(Turn.RED, player);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(playerAfterPayment));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());

        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testPlaceRoadNoResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);


        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);


        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertFalse(testRedRoad.hasRoad);
    }

    @Test
    public void testPlaceRoadNoBrick() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 0);
        player.addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);


        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertFalse(testRedRoad.hasRoad);
    }

    @Test
    public void testPlaceRoadNoWood() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 0);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);


        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertFalse(testRedRoad.hasRoad);
    }

    @Test
    public void testPlayerHasRoadResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertTrue(testRedRoad.hasRoad);
    }

    @Test
    public void testPlayerHasRoadResourcesButHasNotMovedRobberIfNecessary() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.robberMoved = false;
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertFalse(testRedRoad.hasRoad);
    }


    @Test
    public void testPlayerHasRoadResourcesButHasNotRolled() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertFalse(testRedRoad.hasRoad);
    }

    @Test
    public void testPlayerHasRoadResourcesUpdatesResourceCount() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player emptyResourcePlayer = new Player(Turn.RED);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(emptyResourcePlayer));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertTrue(testRedRoad.hasRoad);
    }


    @Test
    public void testPlayerHasRoadResourcesAndHasPlacedInitialRoads() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.turnToPlayer.get(Turn.RED).roads = 13;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));


        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertTrue(testRedRoad.hasRoad);
    }


    @Test
    public void testPlaceRoadDecrementsResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testPlaceRoadDecrementToNonZero() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;

        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);

        CityPoint testRedCity = testBoard.getCityAtCoords(215, 147);
        RoadPoint testRedRoad = testBoard.getRoadAtCoords(243, 130);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player afterPaymentPlayer = new Player(Turn.RED);
        afterPaymentPlayer.addResources(ResourceType.BRICK, 1);
        afterPaymentPlayer.addResources(ResourceType.WOOD, 1);
        afterPaymentPlayer.addResources(ResourceType.SHEEP, 2);
        afterPaymentPlayer.addResources(ResourceType.ORE, 2);
        afterPaymentPlayer.addResources(ResourceType.WHEAT, 2);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.showSettlement(Turn.RED, testRedCity.getX(), testRedCity.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(player));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        controllerTest.showRoad(Turn.RED, testRedRoad.getX(), testRedRoad.getY());
        controllerTest.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        controllerTest.showResourceCards(testBoard, testBoard.playerResourcesMap(afterPaymentPlayer));

        EasyMock.replay(controllerTest);
        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(testRedCity.getX(), testRedCity.getY());
        testBoard.onRoadPointClick(testRedRoad.getX(), testRedRoad.getY());

        EasyMock.verify(controllerTest);
        EasyMock.verify(turnStateMachine);

        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));

    }

    @Test
    public void buyDevCardDecrementsResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.buyDevCard();
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void buyDevCardKnight() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Random rand = EasyMock.mock(Random.class);
        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.rand = rand;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(rand.nextInt(5)).andReturn(0);

        EasyMock.replay(turnStateMachine, rand);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.buyDevCard();
        assertEquals(DevCards.KNIGHT, player.getDevCards().get(0).getType());
        assertEquals(1, player.getDevCards().get(0).getTurnBought());
    }

    @Test
    public void buyDevCardVictoryPoint() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Random rand = EasyMock.mock(Random.class);
        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.rand = rand;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(rand.nextInt(5)).andReturn(1);

        EasyMock.replay(turnStateMachine, rand);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.buyDevCard();
        assertEquals(DevCards.VICTORY_POINT, player.getDevCards().get(0).getType());
    }

    @Test
    public void buyDevCardYoP() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Random rand = EasyMock.mock(Random.class);
        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.rand = rand;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(rand.nextInt(5)).andReturn(2);

        EasyMock.replay(turnStateMachine, rand);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.buyDevCard();
        assertEquals(DevCards.YEAR_OF_PLENTY, player.getDevCards().get(0).getType());
        assertEquals(1, player.getDevCards().get(0).getTurnBought());
    }

    @Test
    public void buyDevCardRoadBuilding() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Random rand = EasyMock.mock(Random.class);
        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.rand = rand;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(rand.nextInt(5)).andReturn(3);

        EasyMock.replay(turnStateMachine, rand);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.buyDevCard();
        assertEquals(DevCards.ROAD_BUILDING, player.getDevCards().get(0).getType());
        assertEquals(1, player.getDevCards().get(0).getTurnBought());
    }

    @Test
    public void buyDevCardMonopoly() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Random rand = EasyMock.mock(Random.class);
        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.rand = rand;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(rand.nextInt(5)).andReturn(4);

        EasyMock.replay(turnStateMachine, rand);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.buyDevCard();
        assertEquals(DevCards.MONOPOLY, player.getDevCards().get(0).getType());
        assertEquals(1, player.getDevCards().get(0).getTurnBought());
    }


    @Test
    public void buyDevCardNotEnoughResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        testBoard.buyDevCard();
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void addDevCardButton() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        controllerTest.placeDevCardButton(testBoard);

        EasyMock.replay(controllerTest);

        testBoard.addBuyDevCardButton();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testOnClickBuysDevCard() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showResourceCards(EasyMock.anyObject(), EasyMock.anyObject());
        controllerTest.showDevCards(testBoard, player.getDevCards());

        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onBuyDevCardClick();

        EasyMock.verify(controllerTest);
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnClickBuysMultipleDevCard() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showResourceCards(EasyMock.anyObject(), EasyMock.anyObject());
        controllerTest.showDevCards(testBoard, player.getDevCards());
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showResourceCards(EasyMock.anyObject(), EasyMock.anyObject());
        controllerTest.showDevCards(testBoard, player.getDevCards());

        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onBuyDevCardClick();
        testBoard.onBuyDevCardClick();

        EasyMock.verify(controllerTest);
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testShowDevCards() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        DevelopmentCard testDevCard = EasyMock.strictMock(DevelopmentCard.class);
        ArrayList<DevelopmentCard> testCards = new ArrayList<>(List.of(testDevCard));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDevCards(testBoard, testCards);

        EasyMock.replay(turnStateMachine);
        EasyMock.replay(controllerTest);

        Player player = new Player(Turn.RED);
        player.addDevelopmentCard(testDevCard);
        testBoard.turnToPlayer.put(Turn.RED, player);
        testBoard.showDevCards();

        EasyMock.verify(turnStateMachine);
        EasyMock.verify(controllerTest);
    }

    @Test
    public void testOnClickBuysDevCardNoSheep() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.SHEEP, 0);
        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.WHEAT, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDevCards(testBoard, player.getDevCards());
        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onBuyDevCardClick();

        EasyMock.verify(controllerTest);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnClickBuysDevCardNoWheat() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.ORE, 1);
        player.addResources(ResourceType.WHEAT, 0);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDevCards(testBoard, player.getDevCards());
        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onBuyDevCardClick();

        EasyMock.verify(controllerTest);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnClickBuysDevCardNoOre() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 1);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.SHEEP, 1);
        player.addResources(ResourceType.ORE, 0);
        player.addResources(ResourceType.WHEAT, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDevCards(testBoard, player.getDevCards());
        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onBuyDevCardClick();

        EasyMock.verify(controllerTest);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnClickBuysDevCardNoResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 0);
        player.addResources(ResourceType.WOOD, 0);
        player.addResources(ResourceType.SHEEP, 0);
        player.addResources(ResourceType.ORE, 0);
        player.addResources(ResourceType.WHEAT, 0);
        testBoard.turnToPlayer.put(Turn.RED, player);


        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDevCards(testBoard, player.getDevCards());
        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.onBuyDevCardClick();

        EasyMock.verify(controllerTest);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.ORANGE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.WHITE).getDevCards().size());
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testRobberStartsInDesert() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        assertEquals(ResourceType.NULL, testBoard.robberResource);
    }

    @Test
    public void testRobberNumberStartsInDesert() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        assertEquals(10, testBoard.robberNumber);
    }

    @Test
    public void testOnRobberPointClick() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        testBoard.onRobberPointClick(1, 1);

        assertTrue(robberPoint.hasRobber);
    }

    @Test
    public void testClickOnRobberPointXMatchesNotY() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 2, ResourceType.BRICK, 8);


        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;

        testBoard.onRobberPointClick(1, 1);

        assertFalse(robberPoint.hasRobber);
    }

    @Test
    public void testOnRobberClickUpdateBoardValues() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        testController.showInitialRobberState(1, 1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(testController, turnStateMachine);

        testBoard.onRobberPointClick(1, 1);

        EasyMock.verify(testController);

        assertEquals(robberPoint.resourceType, testBoard.robberResource);
        assertEquals(robberPoint.diceNumber, testBoard.robberNumber);
    }

    @Test
    public void testClickRobberPointSevenNotRolled() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        testBoard.numRolled = 3;

        testBoard.onRobberPointClick(1, 1);

        assertEquals(Board.INITIAL_ROBBER_RESOURCE_TYPE, testBoard.robberResource);
        assertEquals(Board.INITIAL_ROBBER_NUMBER, testBoard.robberNumber);
    }

    @Test
    public void testPlayerDoesSuccessfulRobberMove() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        RobberPoint robberPointOne = new RobberPoint(1, 1, ResourceType.WHEAT, 6);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.robberPoints = new ArrayList<>(List.of(robberPointOne));
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        testBoard.onRobberPointClick(1, 1);

        assertEquals(robberPointOne.resourceType, testBoard.robberResource);
        assertEquals(robberPointOne.diceNumber, testBoard.robberNumber);

    }

    @Test
    public void testPlayerTriesToMoveRobberTwice() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        RobberPoint robberPointOne = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPointTwo = new RobberPoint(2, 2, ResourceType.BRICK, 8);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.robberPoints = new ArrayList<>(List.of(robberPointOne, robberPointTwo));
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        testBoard.onRobberPointClick(1, 1);
        testBoard.onRobberPointClick(2, 2);

        assertEquals(robberPointOne.resourceType, testBoard.robberResource);
        assertEquals(robberPointOne.diceNumber, testBoard.robberNumber);
    }

    @Test
    public void testShowResources() {

        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);

        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).once();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.BRICK, 1);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.WHEAT, 1);
        redPlayer.addResources(ResourceType.SHEEP, 1);
        redPlayer.addResources(ResourceType.ORE, 1);

        HashMap<ResourceType, Integer> expectedMap = new HashMap<>();
        expectedMap.put(ResourceType.BRICK, 1);
        expectedMap.put(ResourceType.WOOD, 1);
        expectedMap.put(ResourceType.WHEAT, 1);
        expectedMap.put(ResourceType.SHEEP, 1);
        expectedMap.put(ResourceType.ORE, 1);

        controllerTest.showResourceCards(EasyMock.eq(testBoard), EasyMock.eq(expectedMap));
        EasyMock.replay(controllerTest);

        testBoard.showResources();

        EasyMock.verify(turnStateMachine);
        EasyMock.verify(controllerTest);
    }

    @Test
    public void testTradeBeforeRoll() {
        // haha bryson take this
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.BRICK, 2);
        player2.addResources(ResourceType.WOOD, 0);
        player2.addResources(ResourceType.SHEEP, 0);
        player2.addResources(ResourceType.ORE, 0);
        player2.addResources(ResourceType.WHEAT, 0);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 2);

        TradeInfo blueTrade = new TradeInfo();
        blueTrade.setPlayer(Turn.BLUE);
        blueTrade.setResources(ResourceType.BRICK, 2);

        testBoard.onTradeSubmitClick(redTrade, blueTrade);
    }

    @Test
    public void testTradeBetweenPlayers() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.BRICK, 2);
        player2.addResources(ResourceType.WOOD, 0);
        player2.addResources(ResourceType.SHEEP, 0);
        player2.addResources(ResourceType.ORE, 0);
        player2.addResources(ResourceType.WHEAT, 0);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 2);

        TradeInfo blueTrade = new TradeInfo();
        blueTrade.setPlayer(Turn.BLUE);
        blueTrade.setResources(ResourceType.BRICK, 2);

        testBoard.onTradeSubmitClick(redTrade, blueTrade);

        EasyMock.verify(turnStateMachine);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));

        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.ORE));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.BRICK));
    }

    @Test
    public void testTradeBeforeMoveRobberAfter7Rolled() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.BRICK, 2);
        player2.addResources(ResourceType.WOOD, 0);
        player2.addResources(ResourceType.SHEEP, 0);
        player2.addResources(ResourceType.ORE, 0);
        player2.addResources(ResourceType.WHEAT, 0);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 2);

        TradeInfo blueTrade = new TradeInfo();
        blueTrade.setPlayer(Turn.BLUE);
        blueTrade.setResources(ResourceType.BRICK, 2);

        testBoard.robberMoved = false;

        testBoard.onTradeSubmitClick(redTrade, blueTrade);

        EasyMock.verify(turnStateMachine);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));

        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.BRICK));
    }

    @Test
    public void testTradeBetweenPlayersTwoValues() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 4);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 3);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.BRICK, 0);
        player2.addResources(ResourceType.WOOD, 3);
        player2.addResources(ResourceType.SHEEP, 4);
        player2.addResources(ResourceType.ORE, 0);
        player2.addResources(ResourceType.WHEAT, 0);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 2);
        redTrade.setResources(ResourceType.BRICK, 3);

        TradeInfo blueTrade = new TradeInfo();
        blueTrade.setPlayer(Turn.BLUE);
        blueTrade.setResources(ResourceType.WOOD, 2);
        blueTrade.setResources(ResourceType.SHEEP, 3);

        testBoard.onTradeSubmitClick(redTrade, blueTrade);

        EasyMock.verify(turnStateMachine);
        assertEquals(3, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));

        assertEquals(1, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.ORE));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WHEAT));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WOOD));
        assertEquals(3, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.BRICK));
    }

    @Test
    public void testTradeBetweenPlayersNotEnoughResources() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 2);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 3);
        testBoard.turnToPlayer.put(Turn.RED, player1);


        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.BRICK, 0);
        player2.addResources(ResourceType.WOOD, 3);
        player2.addResources(ResourceType.SHEEP, 4);
        player2.addResources(ResourceType.ORE, 0);
        player2.addResources(ResourceType.WHEAT, 0);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);


        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 2);
        redTrade.setResources(ResourceType.BRICK, 3);


        TradeInfo blueTrade = new TradeInfo();
        blueTrade.setPlayer(Turn.BLUE);
        blueTrade.setResources(ResourceType.WOOD, 2);
        blueTrade.setResources(ResourceType.SHEEP, 3);


        testBoard.onTradeSubmitClick(redTrade, blueTrade);


        EasyMock.verify(turnStateMachine);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(3, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        assertEquals(4, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WHEAT));
        assertEquals(3, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.BRICK));
    }

    @Test
    public void showTradeDialogue() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        controllerTest.showTradeDialogue(testBoard);

        EasyMock.replay(controllerTest);

        testBoard.showTradeDialogue();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testShowResourcesMultipleResources() {
        GameWindowController controllerMock = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(controllerMock, turnStateMachine, dice);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WHEAT, 3);
        redPlayer.addResources(ResourceType.SHEEP, 2);
        redPlayer.addResources(ResourceType.WOOD, 4);

        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WOOD, 4);
        expectedResources.put(ResourceType.SHEEP, 2);
        expectedResources.put(ResourceType.WHEAT, 3);
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.ORE, 0);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).once();
        controllerMock.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, controllerMock);

        board.showResources();

        EasyMock.verify(turnStateMachine, controllerMock);
    }

    @Test
    public void testShowResourcesDifferentTurn() {
        GameWindowController controllerMock = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(controllerMock, turnStateMachine, dice);


        Player bluePlayer = board.turnToPlayer.get(Turn.BLUE);
        bluePlayer.addResources(ResourceType.WOOD, 2);
        bluePlayer.addResources(ResourceType.BRICK, 1);

        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WOOD, 2);
        expectedResources.put(ResourceType.BRICK, 1);
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.SHEEP, 0);
        expectedResources.put(ResourceType.ORE, 0);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE).once();
        controllerMock.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedResources));

        EasyMock.replay(turnStateMachine, controllerMock);

        board.showResources();

        EasyMock.verify(turnStateMachine, controllerMock);
    }

    @Test
    public void testPlayerResourcesMapReturnsCorrectCounts() {
        GameWindowController controllerMock = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(controllerMock, turnStateMachine, dice);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 3);
        redPlayer.addResources(ResourceType.SHEEP, 2);

        HashMap<ResourceType, Integer> resourceMap = board.playerResourcesMap(redPlayer);

        assertEquals(3, resourceMap.get(ResourceType.WOOD));
        assertEquals(2, resourceMap.get(ResourceType.SHEEP));
        assertEquals(0, resourceMap.get(ResourceType.WHEAT));
        assertEquals(0, resourceMap.get(ResourceType.BRICK));
        assertEquals(0, resourceMap.get(ResourceType.ORE));
        assertFalse(resourceMap.containsKey(ResourceType.NULL)); // NULL should be skipped
    }

    @Test
    public void testUpgradeSettlementBeforeRound3() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = true;
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(1, 1);

        assertFalse(cityPoint.isCity);
    }

    @Test
    public void testUpgradeSettlementNoResources() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = true;
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).settlements = 3;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(1, 1);

        assertFalse(cityPoint.isCity);
    }

    @Test
    public void testUpgradeSettlementInsufficientOre() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = true;
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.ORE, 2);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).settlements = 3;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(1, 1);

        assertFalse(cityPoint.isCity);
    }

    @Test
    public void testUpgradeSettlementInsufficientWheat() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = true;
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.ORE, 3);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.WHEAT, 1);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).settlements = 3;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(1, 1);

        assertFalse(cityPoint.isCity);
    }

    @Test
    public void testUpgradeSettlementSuccess() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = true;
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.ORE, 3);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).settlements = 3;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        testController.showCity(TurnStateMachine.FIRST_TURN, 1, 1);
        testController.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        testController.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.RED)));

        EasyMock.replay(turnStateMachine, testController);

        testBoard.onCityPointClick(1, 1);

        EasyMock.verify(testController);

        assertTrue(cityPoint.isCity);
    }

    @Test
    public void testCityUpgradeGivesVP() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player1 = EasyMock.mock(Player.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = true;
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).settlements = 3;

        EasyMock.expect(player1.canPayForSettlement()).andReturn(true);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        player1.payToUpgradeSettlement();
        player1.addVictoryPoints(1);
        EasyMock.expect(player1.getVictoryPoints()).andReturn(2);
        EasyMock.expect(player1.getResource(EasyMock.anyObject(ResourceType.class))).andReturn(1).times(5);


        EasyMock.replay(turnStateMachine);
        EasyMock.replay(player1);

        testBoard.turnToPlayer.put(Turn.RED, player1);

        testBoard.onCityPointClick(1, 1);

        EasyMock.verify(player1);
    }

    @Test
    public void testSettlementGivesVP() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player1 = EasyMock.mock(Player.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = false;
        cityPoint.owner = Turn.NONE;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));
        RoadPoint testRoad = new RoadPoint(10, 10);
        testBoard.roadPoints = new ArrayList<>(List.of(testRoad));
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).settlements = 3;

        EasyMock.expect(player1.canPayForSettlement()).andReturn(true);
        player1.payForSettlement();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        player1.addVictoryPoints(1);
        EasyMock.expect(player1.getVictoryPoints()).andReturn(1);

        EasyMock.expect(player1.getResource(EasyMock.anyObject())).andReturn(1);
        EasyMock.expect(player1.getResource(EasyMock.anyObject())).andReturn(1);
        EasyMock.expect(player1.getResource(EasyMock.anyObject())).andReturn(1);
        EasyMock.expect(player1.getResource(EasyMock.anyObject())).andReturn(1);
        EasyMock.expect(player1.getResource(EasyMock.anyObject())).andReturn(1);


        EasyMock.replay(turnStateMachine);
        EasyMock.replay(player1);

        testBoard.turnToPlayer.put(Turn.RED, player1);

        testBoard.onCityPointClick(1, 1);

        EasyMock.verify(player1);
    }

    @Test
    public void testWinOnNextTurn10VP() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player1 = EasyMock.mock(Player.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(5);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.isForward()).andReturn(true);
        EasyMock.expect(player1.getVictoryPoints()).andReturn(10);

        testController.gameOver(player1);

        EasyMock.replay(turnStateMachine);
        EasyMock.replay(player1);
        EasyMock.replay(testController);

        testBoard.turnToPlayer.put(Turn.RED, player1);

        testBoard.onNextTurnClick();

        EasyMock.verify(testController);
    }

    @Test
    public void testWinOnNextTurn11VP() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Player player1 = EasyMock.mock(Player.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(5);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.isForward()).andReturn(true);
        EasyMock.expect(player1.getVictoryPoints()).andReturn(11);

        testController.gameOver(player1);

        EasyMock.replay(turnStateMachine);
        EasyMock.replay(player1);
        EasyMock.replay(testController);

        testBoard.turnToPlayer.put(Turn.RED, player1);

        testBoard.onNextTurnClick();

        EasyMock.verify(testController);
    }

    @Test
    public void testShowDiscardDialog() {
        GameWindowController controller = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, dice);


        controller.showDiscardDialog(board);
        EasyMock.replay(controller);

        board.showDiscardDialog();

        EasyMock.verify(controller);
    }

    @Test
    public void testRollSevenPromptsDiscard() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player red = testBoard.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 5);
        red.addResources(ResourceType.BRICK, 3);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(7);
        controllerTest.showDiceRoll(7);

        controllerTest.showDiscardDialog(testBoard);

        EasyMock.replay(turnStateMachine, dice, controllerTest);

        testBoard.onRollDiceClick();
        EasyMock.verify(turnStateMachine, dice, controllerTest);
    }

    @Test
    public void testRollSevenNoDiscardPrompt() {
        GameWindowController controller = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(7);
        controller.showDiceRoll(7);

        EasyMock.replay(turnStateMachine, dice, controller);

        board.onRollDiceClick();

        EasyMock.verify(controller);
    }

    @Test
    public void testCloseDiscardDialog() {
        GameWindowController controller = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, dice);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 5);

        controller.hideDiscardDialog();

        EasyMock.replay(controller);

        board.closeDiscardDialog();

        EasyMock.verify(controller);
    }

    @Test
    public void testMoreThan7DoesNotCloseDiscardDialog() {

        GameWindowController controller = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, dice);


        Player mockRed = EasyMock.strictMock(Player.class);
        board.turnToPlayer.put(Turn.RED, mockRed);

        EasyMock.expect(mockRed.needsToDiscard()).andReturn(true);

        EasyMock.replay(controller, mockRed);
        board.closeDiscardDialog();
        EasyMock.verify(controller, mockRed);
    }

    @Test
    public void testGetAllPlayersResourceMaps() {
        GameWindowController gameController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(gameController, turnStateMachine, dice);

        HashMap<Turn, HashMap<ResourceType, Integer>> maps = board.getAllPlayersResourceMaps();

        for (HashMap<ResourceType, Integer> hand : maps.values()) {
            for (ResourceType type : ResourceType.values()) {
                if (type == ResourceType.NULL) continue;
                assertEquals(0, hand.get(type).intValue());
            }
        }
    }

    @Test
    public void testOnSubmitDiscardHidesDialogAndShowsResources() {

        GameWindowController gameController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(gameController, turnStateMachine, dice);


        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        red.addResources(ResourceType.WOOD, 5);
        red.addResources(ResourceType.BRICK, 3);
        blue.addResources(ResourceType.WHEAT, 4);
        blue.addResources(ResourceType.ORE, 4);


        HashMap<Turn, HashMap<ResourceType, Integer>> discards = new HashMap<>();
        HashMap<ResourceType, Integer> redMap = new HashMap<>();
        redMap.put(ResourceType.WOOD, 4);
        discards.put(Turn.RED, redMap);
        HashMap<ResourceType, Integer> blueMap = new HashMap<>();
        blueMap.put(ResourceType.WHEAT, 4);
        discards.put(Turn.BLUE, blueMap);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        gameController.hideDiscardDialog();
        HashMap<ResourceType, Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 1);
        expectedRedHand.put(ResourceType.BRICK, 3);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 0);
        expectedRedHand.put(ResourceType.ORE, 0);
        gameController.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));

        EasyMock.replay(gameController, turnStateMachine);
        board.onSubmitDiscard(discards);
        EasyMock.verify(gameController, turnStateMachine);
    }

    @Test
    public void testOnSubmitDiscardSomeStillNeedsTo() {
        GameWindowController gameController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(gameController, turnStateMachine, dice);

        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        red.addResources(ResourceType.WOOD, 5);
        red.addResources(ResourceType.BRICK, 3);
        blue.addResources(ResourceType.WOOD, 4);
        blue.addResources(ResourceType.BRICK, 4);


        HashMap<Turn, HashMap<ResourceType, Integer>> discards = new HashMap<>();
        HashMap<ResourceType, Integer> redMap = new HashMap<>();
        redMap.put(ResourceType.WOOD, 4);
        discards.put(Turn.RED, redMap);

        EasyMock.replay(gameController);

        board.onSubmitDiscard(discards);


        EasyMock.verify(gameController);
    }

    @Test
    public void testPlaceValidRoadTwoAwayFromSettlement() {
        GameWindowController gameController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board board = new Board(gameController, turnStateMachine, dice);

        CityPoint redCity = new CityPoint(1, 1);
        RoadPoint redRoad = new RoadPoint(2, 2);
        CityPoint noCity = new CityPoint(3, 3);
        RoadPoint desiredRoad = new RoadPoint(4, 4);

        redCity.owner = Turn.RED;
        redCity.hasSettlement = true;

        redRoad.owner = Turn.RED;
        redRoad.hasRoad = true;

        redCity.neighbors = new ArrayList<>(List.of(redRoad));
        redRoad.neighbors = new ArrayList<>(List.of(redCity, noCity));
        noCity.neighbors = new ArrayList<>(List.of(redRoad, desiredRoad));
        desiredRoad.neighbors = new ArrayList<>(List.of(noCity));

        board.cityPoints = new ArrayList<>(List.of(redCity, noCity));
        board.roadPoints = new ArrayList<>(List.of(redRoad, desiredRoad));

        board.turnToPlayer.get(Turn.RED).settlements = 3;
        board.turnToPlayer.get(Turn.RED).roads = 13;
        board.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 1);
        board.turnToPlayer.get(Turn.RED).addResources(ResourceType.BRICK, 1);


        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        EasyMock.replay(turnStateMachine);

        board.onRoadPointClick(4, 4);

        assertTrue(desiredRoad.hasRoad);
        assertEquals(Turn.RED, desiredRoad.owner);
    }

    @Test
    public void testGetAllPlayersEligibleForRobNoneEligible() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 3), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.FIELD));
        cityPoint.owner = Turn.RED;
        testBoard.cityPoints = new ArrayList<>(List.of());

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);

        assertEquals(0, testBoard.getEligiblePlayersToRob(robberPoint).size());
    }

    @Test
    public void testGetAllPlayersEligibleForRobNoSettlementsOnBoard() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 3), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.FIELD));
        testBoard.cityPoints = new ArrayList<>(List.of());

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);

        assertEquals(0, testBoard.getEligiblePlayersToRob(robberPoint).size());
    }

    @Test
    public void testGetAllPlayersEligibleForRobOneEligible() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 8), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.PASTURE));
        cityPoint.owner = Turn.RED;
        cityPoint.hasSettlement = true;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));
        testBoard.turnToPlayer.get(Turn.RED).addVictoryPoints(2);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);

        assertEquals(1, testBoard.getEligiblePlayersToRob(robberPoint).size());
    }

    @Test
    public void testGetAllPlayersEligibleForRobNoDuplicates() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 8), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.PASTURE));
        cityPoint.owner = Turn.RED;
        cityPoint.hasSettlement = true;

        CityPoint cityPoint2 = new CityPoint(3, 3);
        cityPoint2.setTileValues(List.of(3, 4, 8), List.of(Terrain.MOUNTAIN, Terrain.HILL, Terrain.PASTURE));
        cityPoint2.owner = Turn.RED;
        cityPoint2.hasSettlement = true;


        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPoint2));
        testBoard.turnToPlayer.get(Turn.RED).addVictoryPoints(2);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);

        assertEquals(1, testBoard.getEligiblePlayersToRob(robberPoint).size());
    }

    @Test
    public void testGetAllPlayersEligibleForRobMultiplePlayers() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 8), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.PASTURE));
        cityPoint.owner = Turn.RED;
        cityPoint.hasSettlement = true;

        CityPoint cityPoint2 = new CityPoint(3, 3);
        cityPoint2.setTileValues(List.of(3, 4, 8), List.of(Terrain.MOUNTAIN, Terrain.HILL, Terrain.PASTURE));
        cityPoint2.owner = Turn.BLUE;
        cityPoint2.hasSettlement = true;


        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPoint2));
        testBoard.turnToPlayer.get(Turn.RED).addVictoryPoints(2);
        testBoard.turnToPlayer.get(Turn.BLUE).addVictoryPoints(2);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);

        Set<Player> eligiblePlayers = testBoard.getEligiblePlayersToRob(robberPoint);
        assertEquals(2, eligiblePlayers.size());
        assertTrue(eligiblePlayers.contains(testBoard.turnToPlayer.get(Turn.RED)));
        assertTrue(eligiblePlayers.contains(testBoard.turnToPlayer.get(Turn.BLUE)));
    }

    @Test
    public void testMoveRobberEligibleForStealOpensDialog() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 8), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.PASTURE));
        cityPoint.owner = Turn.ORANGE;
        cityPoint.hasSettlement = true;

        CityPoint cityPoint2 = new CityPoint(3, 3);
        cityPoint2.setTileValues(List.of(3, 4, 8), List.of(Terrain.MOUNTAIN, Terrain.HILL, Terrain.PASTURE));
        cityPoint2.owner = Turn.BLUE;
        cityPoint2.hasSettlement = true;

        testBoard.turnToPlayer.get(Turn.ORANGE).addVictoryPoints(2);
        testBoard.turnToPlayer.get(Turn.BLUE).addVictoryPoints(2);

        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPoint2));

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        controllerTest.showInitialRobberState(1, 1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        HashSet<Player> expectedPlayers = new HashSet<>();
        expectedPlayers.add(testBoard.turnToPlayer.get(Turn.BLUE));
        expectedPlayers.add(testBoard.turnToPlayer.get(Turn.ORANGE));
        controllerTest.showStealDialog(testBoard, expectedPlayers);

        EasyMock.replay(turnStateMachine, controllerTest);

        testBoard.numRolled = Board.DISCARD_THRESHOLD;
        testBoard.robberMoved = false;
        testBoard.onRobberPointClick(1, 1);

        EasyMock.verify(controllerTest);

    }

    @Test
    public void testMoveRobberNotEligibleForStealDoesNotOpenDialog() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 3), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.FIELD));
        cityPoint.owner = Turn.RED;
        cityPoint.hasSettlement = true;

        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        controllerTest.showInitialRobberState(1, 1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.replay(controllerTest, turnStateMachine);

        testBoard.numRolled = Board.DISCARD_THRESHOLD;
        testBoard.robberMoved = false;
        testBoard.onRobberPointClick(1, 1);

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testRobCardFromPlayerHasOneCard() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player red = new Player(Turn.RED);
        Player blue = new Player(Turn.BLUE);

        blue.addResources(ResourceType.SHEEP, 1);

        testBoard.turnToPlayer.put(Turn.RED, red);
        testBoard.turnToPlayer.put(Turn.BLUE, blue);

        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(random.nextInt(EasyMock.anyInt())).andReturn(0);

        EasyMock.replay(turnStateMachine, random);

        testBoard.robCardFromPlayer(Turn.BLUE, random);

        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));

    }

    @Test
    public void testRobCardFromPlayerHasMultipleOfOneCard() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player red = new Player(Turn.RED);
        Player blue = new Player(Turn.BLUE);

        blue.addResources(ResourceType.SHEEP, 2);

        testBoard.turnToPlayer.put(Turn.RED, red);
        testBoard.turnToPlayer.put(Turn.BLUE, blue);

        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(random.nextInt(EasyMock.anyInt())).andReturn(0);

        EasyMock.replay(turnStateMachine, random);

        testBoard.robCardFromPlayer(Turn.BLUE, random);

        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.BLUE).getResource(ResourceType.SHEEP));

    }

    @Test
    public void testRobCardFromPlayerHasMultipleOfMultipleCards() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        Player red = new Player(Turn.RED);
        Player blue = new Player(Turn.BLUE);

        blue.addResources(ResourceType.SHEEP, 2);
        blue.addResources(ResourceType.BRICK, 2);

        ArrayList<ResourceType> resources = blue.getResourcesAsList();
        ResourceType stolen = resources.get(0);

        testBoard.turnToPlayer.put(Turn.RED, red);
        testBoard.turnToPlayer.put(Turn.BLUE, blue);

        Random random = EasyMock.mock(Random.class);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(random.nextInt(EasyMock.anyInt())).andReturn(0);
        HashMap<ResourceType, Integer> expectedResources = new HashMap<>();
        expectedResources.put(ResourceType.WHEAT, 0);
        expectedResources.put(ResourceType.SHEEP, 0);
        expectedResources.put(ResourceType.BRICK, 0);
        expectedResources.put(ResourceType.WOOD, 0);
        expectedResources.put(ResourceType.ORE, 0);
        expectedResources.put(stolen, 1);
        controllerTest.showResourceCards(testBoard, expectedResources);
        controllerTest.removeStealDialog();

        EasyMock.replay(turnStateMachine, random, controllerTest);

        testBoard.robCardFromPlayer(Turn.BLUE, random);

        EasyMock.verify(controllerTest);

        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(stolen));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.BLUE).getResource(stolen));
        assertEquals(3, testBoard.turnToPlayer.get(Turn.BLUE).getResourcesAsList().size());
    }

    @Test
    public void testCurrentTurnDoesNotShowAsRobOption() {
        GameWindowController controllerTest = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);


        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(1, 2, 8), List.of(Terrain.FIELD, Terrain.FIELD, Terrain.PASTURE));
        cityPoint.owner = Turn.RED;
        cityPoint.hasSettlement = true;

        CityPoint cityPoint2 = new CityPoint(3, 3);
        cityPoint2.setTileValues(List.of(3, 4, 8), List.of(Terrain.MOUNTAIN, Terrain.HILL, Terrain.PASTURE));
        cityPoint2.owner = Turn.BLUE;
        cityPoint2.hasSettlement = true;

        testBoard.turnToPlayer.get(Turn.RED).addVictoryPoints(2);
        testBoard.turnToPlayer.get(Turn.BLUE).addVictoryPoints(2);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint, cityPoint2));

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 8);

        Set<Player> eligiblePlayers = testBoard.getEligiblePlayersToRob(robberPoint);
        assertEquals(1, eligiblePlayers.size());
        assertTrue(eligiblePlayers.contains(testBoard.turnToPlayer.get(Turn.BLUE)));
    }

    @Test
    public void testVPDevCardAddsVP() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        Random randomMock = EasyMock.mock(Random.class);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(randomMock.nextInt(5)).andReturn(1);

        EasyMock.replay(turnStateMachine, randomMock);
        testBoard.rand = randomMock;

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.BRICK, 2);
        player.addResources(ResourceType.WOOD, 2);
        player.addResources(ResourceType.SHEEP, 2);
        player.addResources(ResourceType.ORE, 2);
        player.addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.buyDevCard();

        assertEquals(1, player.getVictoryPoints());
    }

    @Test
    public void testYoPClick() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        controllerTest.openYoPTradeMenu(testBoard, Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        controllerTest.showDevCards(EasyMock.anyObject(), EasyMock.anyObject());

        EasyMock.replay(turnStateMachine, controllerTest);

        Player player = new Player(Turn.RED);
        DevelopmentCard YoP = new DevelopmentCard(DevCards.YEAR_OF_PLENTY);
        YoP.setTurnBought(0);
        player.addDevelopmentCard(YoP);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onYoPClick();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testCannotUseYoPSameTurnBought() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);

        EasyMock.replay(turnStateMachine, controllerTest);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.YEAR_OF_PLENTY);
        testCard.setTurnBought(1);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onYoPClick();

        EasyMock.verify(controllerTest);
    }

    @Test
    public void testUseYoPRemovesCard() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.YEAR_OF_PLENTY);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onYoPClick();

        assertEquals(0, player.getDevCards().size());
    }

    @Test
    public void testPlaceAdjacentRoads() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        testBoard.onRoadPointClick(451, 390);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad());
        assertTrue(testBoard.getRoadAtCoords(451, 390).hasRoad());
    }

    @Test
    public void testPlaceNotAdjacentRoads() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 1);
        player.addResources(ResourceType.BRICK, 1);
        testBoard.turnToPlayer.put(Turn.RED, player);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(421, 338);
        testBoard.onRoadPointClick(690, 390);
        assertTrue(testBoard.getRoadAtCoords(421, 338).hasRoad());
        assertFalse(testBoard.getRoadAtCoords(390, 390).hasRoad());
    }

    @Test
    public void testOnClickRoadBuilding() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.ROAD_BUILDING);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadBuildingClick();

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);

        EasyMock.verify(turnStateMachine);
        assertTrue(testBoard.getRoadAtCoords(331, 390).hasRoad());
        assertTrue(testBoard.getRoadAtCoords(304, 441).hasRoad());
    }

    @Test
    public void testOnClickRoadBuildingRemovesDevCard() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.ROAD_BUILDING);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadBuildingClick();

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);

        EasyMock.verify(turnStateMachine);
        assertTrue(player.getDevCards().isEmpty());
    }

    @Test
    public void testOnClickRoadBuildingThreeRoads() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.ROAD_BUILDING);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);

        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();


        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadBuildingClick();

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);
        testBoard.onRoadPointClick(270, 495);

        EasyMock.verify(turnStateMachine);
        assertTrue(testBoard.getRoadAtCoords(331, 390).hasRoad());
        assertTrue(testBoard.getRoadAtCoords(304, 441).hasRoad());
        assertFalse(testBoard.getRoadAtCoords(270, 495).hasRoad());
    }

    @Test
    public void testOnClickRoadBuildingNoDevCard() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadBuildingClick();

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);

        assertFalse(testBoard.getRoadAtCoords(331, 390).hasRoad());
        assertFalse(testBoard.getRoadAtCoords(304, 441).hasRoad());
    }

    @Test
    public void testOnClickRoadBuildingSameTurnBought() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.ROAD_BUILDING);
        testCard.setTurnBought(4);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);


        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadBuildingClick();

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);

        assertFalse(testBoard.getRoadAtCoords(331, 390).hasRoad());
        assertFalse(testBoard.getRoadAtCoords(304, 441).hasRoad());
    }

    @Test
    public void testClearFreeRoadsNextTurn() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.ROAD_BUILDING);
        testCard.setTurnBought(4);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.isForward()).andReturn(true);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.isForward()).andReturn(true);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(5);
        EasyMock.expect(turnStateMachine.isForward()).andReturn(true);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(5);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.isForward()).andReturn(true);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        turnStateMachine.nextTurn();
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadBuildingClick();
        testBoard.onNextTurnClick();

        assertEquals(0, player.getFreeRoads());
    }

    @Test
    public void testLongestRoad5Roads() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 4);
        player.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();


        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);
        testBoard.onRoadPointClick(270, 495);
        testBoard.onRoadPointClick(245, 544);

        assertEquals(3, player.getVictoryPoints());
    }

    @Test
    public void testLongestRoad5RoadsWith4() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 4);
        player.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();


        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onCityPointClick(573, 215);
        testBoard.onRoadPointClick(545, 232);

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);
        testBoard.onRoadPointClick(270, 495);
        testBoard.onRoadPointClick(245, 544);

        testBoard.onRoadPointClick(480, 234);
        testBoard.onRoadPointClick(450, 183);
        testBoard.onRoadPointClick(420, 132);

        assertEquals(3, player.getVictoryPoints());
    }

    @Test
    public void testLongestRoad4Roads() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 4);
        player.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();


        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);
        testBoard.onRoadPointClick(270, 495);

        assertEquals(1, player.getVictoryPoints());
    }

    @Test
    public void testLongestRoadMoreThan5Roads() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 4);
        player.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.RED, player);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(391, 320);
        testBoard.onRoadPointClick(364, 338);

        testBoard.onRoadPointClick(331, 390);
        testBoard.onRoadPointClick(304, 441);
        testBoard.onRoadPointClick(270, 495);
        testBoard.onRoadPointClick(245, 544);

        assertEquals(3, player.getVictoryPoints());
    }

    @Test
    public void testLongestRoadTwoPlayers() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 6);
        player.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 4);
        player2.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);


        EasyMock.replay(turnStateMachine);

        //RED first place
        testBoard.onCityPointClick(273, 251);
        testBoard.onRoadPointClick(245, 234);

        //BLUE first place
        testBoard.onCityPointClick(571, 560);
        testBoard.onRoadPointClick(540, 544);

        //RED long road
        testBoard.onRoadPointClick(305, 232);
        testBoard.onRoadPointClick(331, 183);
        testBoard.onRoadPointClick(360, 234);
        testBoard.onRoadPointClick(271, 288);
        testBoard.onRoadPointClick(245, 338);
        testBoard.onRoadPointClick(303, 338);

        //BLUE long road
        testBoard.onRoadPointClick(485, 544);
        testBoard.onRoadPointClick(451, 598);
        testBoard.onRoadPointClick(425, 648);
        testBoard.onRoadPointClick(365, 648);

        assertEquals(3, player2.getVictoryPoints());
        assertEquals(1, player.getVictoryPoints());
    }

    @Test
    public void testLongestRoadTwoPlayersOneSteals() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 6);
        player.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 6);
        player2.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);


        EasyMock.replay(turnStateMachine);

        //RED first place
        testBoard.onCityPointClick(571, 560);
        testBoard.onRoadPointClick(540, 544);

        //BLUE first place
        testBoard.onCityPointClick(213, 215);
        testBoard.onRoadPointClick(245, 234);

        //RED long road
        testBoard.onRoadPointClick(485, 544);
        testBoard.onRoadPointClick(423, 544);
        testBoard.onRoadPointClick(365, 544);
        testBoard.onRoadPointClick(303, 544);

        //BLUE long road
        testBoard.onRoadPointClick(305, 232);
        testBoard.onRoadPointClick(360, 234);
        testBoard.onRoadPointClick(425, 232);
        testBoard.onRoadPointClick(480, 234);
        testBoard.onRoadPointClick(545, 232);

        assertEquals(1, player.getVictoryPoints());
        assertEquals(3, player2.getVictoryPoints());
    }

    @Test
    public void testLongestRoadTwoPlayersSameLength() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 6);
        player.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 6);
        player2.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);


        EasyMock.replay(turnStateMachine);

        //RED first place
        testBoard.onCityPointClick(571, 560);
        testBoard.onRoadPointClick(540, 544);

        //BLUE first place
        testBoard.onCityPointClick(213, 215);
        testBoard.onRoadPointClick(245, 234);

        //RED long road
        testBoard.onRoadPointClick(485, 544);
        testBoard.onRoadPointClick(423, 544);
        testBoard.onRoadPointClick(365, 544);
        testBoard.onRoadPointClick(303, 544);

        //BLUE long road
        testBoard.onRoadPointClick(305, 232);
        testBoard.onRoadPointClick(360, 234);
        testBoard.onRoadPointClick(425, 232);
        testBoard.onRoadPointClick(480, 234);

        assertEquals(3, player.getVictoryPoints());
        assertEquals(1, player2.getVictoryPoints());
    }

    @Test
    public void testLongestRoadTwoPlayersLessThanMin() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 6);
        player.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 4);
        player2.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.replay(turnStateMachine);

        //RED first place
        testBoard.onCityPointClick(273, 251);
        testBoard.onRoadPointClick(245, 234);

        //BLUE first place
        testBoard.onCityPointClick(571, 560);
        testBoard.onRoadPointClick(540, 544);

        //RED long road
        testBoard.onRoadPointClick(305, 232);
        testBoard.onRoadPointClick(331, 183);
        testBoard.onRoadPointClick(360, 234);
        testBoard.onRoadPointClick(271, 288);
        testBoard.onRoadPointClick(245, 338);
        testBoard.onRoadPointClick(303, 338);

        //BLUE long road
        testBoard.onRoadPointClick(485, 544);
        testBoard.onRoadPointClick(451, 598);
        testBoard.onRoadPointClick(425, 648);

        assertEquals(1, player2.getVictoryPoints());
        assertEquals(1, player.getVictoryPoints());
    }

    @Test
    public void testLongestRoadLoop() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 6);
        player.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 4);
        player2.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.replay(turnStateMachine);

        //RED first place
        testBoard.onCityPointClick(273, 251);
        testBoard.onRoadPointClick(271, 288);

        //BLUE first place
        testBoard.onCityPointClick(571, 560);
        testBoard.onRoadPointClick(540, 544);

        //RED long road
        testBoard.onRoadPointClick(303, 338);
        testBoard.onRoadPointClick(364, 338);
        testBoard.onRoadPointClick(394, 288);
        testBoard.onRoadPointClick(360, 234);
        testBoard.onRoadPointClick(305, 232);

        //BLUE long road
        testBoard.onRoadPointClick(485, 544);
        testBoard.onRoadPointClick(451, 598);
        testBoard.onRoadPointClick(425, 648);
        testBoard.onRoadPointClick(365, 648);

        assertEquals(1, player2.getVictoryPoints());
        assertEquals(3, player.getVictoryPoints());
    }

    @Test
    public void testLongestRoadBlocked() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 6);
        player.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 4);
        player2.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        EasyMock.replay(turnStateMachine);

        //BLUE first place
        testBoard.onCityPointClick(571, 560);
        testBoard.onRoadPointClick(540, 544);

        //BLUE long road
        testBoard.onRoadPointClick(485, 544);
        testBoard.onRoadPointClick(451, 598);
        testBoard.onRoadPointClick(425, 648);
        testBoard.onRoadPointClick(365, 648);

        assertEquals(3, player2.getVictoryPoints());

        testBoard.onCityPointClick(451, 560);

        assertEquals(1, player2.getVictoryPoints());
        assertEquals(1, player.getVictoryPoints());
    }

    @Test
    public void testLoadHarborsFromBoard() {

        GameWindowController mockController = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine mockTurnMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(mockController, mockTurnMachine, mockDice);

        ArrayList<HarborPoint> harborPoints = helpCreateHarbors(board);

        assertEquals(18, harborPoints.size());
        long genericCount = harborPoints.stream()
                .filter(h -> h.getTradingResource() == ResourceType.NULL)
                .count();
        assertEquals(8, genericCount);
    }


    @Test
    public void testDisjointedRoad() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);


        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 4);
        player2.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        //Build first road and settlement
        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        //Other road placements
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);


        EasyMock.replay(turnStateMachine);

        //BLUE first place
        testBoard.onCityPointClick(571, 560);
        testBoard.onRoadPointClick(540, 544);

        //BLUE long road
        testBoard.onRoadPointClick(451, 598);


        assertEquals(false, testBoard.getRoadAtCoords(451, 598).hasRoad());
    }

    @Test
    public void testBuildBlockedRoad() {
        GameWindowController controllerTest = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        ArrayList<CityPoint> cities = helpCreateCities(testBoard);
        testBoard.cityPoints = cities;
        ArrayList<RoadPoint> roads = helpCreateRoads(testBoard);
        testBoard.roadPoints = roads;
        helpAddAllCityNeighbors(testBoard, cities, roads);
        helpAddAllRoadNeighbors(testBoard, roads, cities);
        testBoard.addAllCities(cities);
        testBoard.addAllRoadPoints(roads);

        Player player = new Player(Turn.RED);
        player.addResources(ResourceType.WOOD, 6);
        player.addResources(ResourceType.BRICK, 6);
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        player2.addResources(ResourceType.WOOD, 4);
        player2.addResources(ResourceType.BRICK, 4);
        testBoard.turnToPlayer.put(Turn.BLUE, player2);


        EasyMock.expect(turnStateMachine.getHasRolled()).andStubReturn(true);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(4);

        EasyMock.replay(turnStateMachine);

        testBoard.onCityPointClick(511, 527);
        testBoard.onRoadPointClick(485, 544);
        testBoard.onRoadPointClick(423, 544);

        testBoard.onCityPointClick(392, 527);

        testBoard.onRoadPointClick(365, 544);

        assertEquals(true, testBoard.getCityAtCoords(392, 527).hasSettlement());
        assertEquals(false, testBoard.getRoadAtCoords(365, 544).hasRoad());
    }

    @Test
    public void testOnSubmitClickBankCorrectAmounts() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 4);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 4);
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        EasyMock.verify(turnStateMachine);
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnSubmitClickBankIncorrectAmount() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 3);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 4);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(3, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnSubmitClickBankNotFour() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 3);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 3);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(3, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnSubmitClickBankReceiveTooMuch() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 4);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 4);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 2);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(4, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnSubmitClickBankReceiveTooLittle() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 4);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 4);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 0);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(4, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnSubmitClickBankGiveTooMuch() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 5);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 5);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(5, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnSubmitClickBankEightToOne() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 8);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 8);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(8, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testOnSubmitClickBankEightToTwo() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player1 = new Player(Turn.RED);
        player1.addResources(ResourceType.BRICK, 0);
        player1.addResources(ResourceType.WOOD, 0);
        player1.addResources(ResourceType.SHEEP, 0);
        player1.addResources(ResourceType.ORE, 0);
        player1.addResources(ResourceType.WHEAT, 8);
        testBoard.turnToPlayer.put(Turn.RED, player1);

        Player bank = new Player(Turn.BANK);
        testBoard.turnToPlayer.put(Turn.BANK, bank);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setResources(ResourceType.WHEAT, 8);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 2);

        testBoard.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(2, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void testCityPointGivesDouble() {
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        CityPoint city = new CityPoint(1, 1);

        city.hasSettlement = true;
        city.isCity = true;
        city.owner = Turn.RED;

        city.setTileValues(List.of(8), List.of(Terrain.MOUNTAIN));

        testBoard.cityPoints = new ArrayList<>(List.of(city));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(dice.roll()).andReturn(8);

        EasyMock.replay(turnStateMachine, dice);

        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice);
        assertEquals(2, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));

    }

    @Test
    void onBankSubmitClickGenericHarborThreeToOneTrade() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WHEAT, 10);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);


        HarborPoint genericHarbor = new HarborPoint(20, 10, ResourceType.NULL);
        genericHarbor.owner = Turn.RED;
        genericHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(genericHarbor);

        redTrade.setResources(ResourceType.WHEAT, 3);
        bankTrade.setResources(ResourceType.BRICK, 1);
        HashMap<ResourceType, Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 0);
        expectedRedHand.put(ResourceType.BRICK, 1);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 7);
        expectedRedHand.put(ResourceType.ORE, 0);
        controller.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));
        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(7, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(1, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        EasyMock.verify(controller);
    }

    @Test
    void onBankSubmitClickSpecificHarborTwoToOneTrade() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.ORE, 5);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);


        HarborPoint oreHarbor = new HarborPoint(30, 20, ResourceType.ORE);
        oreHarbor.owner = Turn.RED;
        oreHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(oreHarbor);


        redTrade.setResources(ResourceType.ORE, 2);
        bankTrade.setResources(ResourceType.WOOD, 1);


        HashMap<ResourceType, Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 1);
        expectedRedHand.put(ResourceType.BRICK, 0);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 0);
        expectedRedHand.put(ResourceType.ORE, 3);
        controller.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));
        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);


        assertEquals(3, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(1, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        EasyMock.verify(controller);
    }

    @Test
    void onBankSubmitClickSpecificHarborInvalidResourceTrade() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WHEAT, 5);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);


        HarborPoint oreHarbor = new HarborPoint(30, 20, ResourceType.ORE);
        oreHarbor.owner = Turn.RED;
        oreHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(oreHarbor);


        redTrade.setResources(ResourceType.WHEAT, 2);
        bankTrade.setResources(ResourceType.WOOD, 1);


        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);


        assertEquals(5, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    void onBankSubmitClickSpecificHarborCorrectResourceButIncorrectAmount() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.ORE, 5);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);


        HarborPoint oreHarbor = new HarborPoint(30, 20, ResourceType.ORE);
        oreHarbor.owner = Turn.RED;
        oreHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(oreHarbor);


        redTrade.setResources(ResourceType.ORE, 3);
        bankTrade.setResources(ResourceType.WOOD, 1);

        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(5, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    void onBankSubmitClickGenericHarborGiveTooMuch() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WHEAT, 10);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);


        HarborPoint genericHarbor = new HarborPoint(20, 10, ResourceType.NULL);
        genericHarbor.owner = Turn.RED;
        genericHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(genericHarbor);


        redTrade.setResources(ResourceType.WHEAT, 4);
        bankTrade.setResources(ResourceType.BRICK, 1);

        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(10, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    void onBankSubmitClickMultipleResourceTypesWithGenericHarbor() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 6);
        redPlayer.addResources(ResourceType.SHEEP, 3);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);

        HarborPoint genericHarbor = new HarborPoint(20, 10, ResourceType.NULL);
        genericHarbor.owner = Turn.RED;
        genericHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(genericHarbor);


        redTrade.setResources(ResourceType.WOOD, 3);
        redTrade.setResources(ResourceType.SHEEP, 3);
        bankTrade.setResources(ResourceType.BRICK, 2);

        HashMap<ResourceType, Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 3);
        expectedRedHand.put(ResourceType.BRICK, 2);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 0);
        expectedRedHand.put(ResourceType.ORE, 0);
        controller.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));
        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(3, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(2, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    void onBankSubmitClickMultipleResourceTypesWithSpecificHarbor() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.ORE, 4);
        redPlayer.addResources(ResourceType.WHEAT, 4);

        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);


        HarborPoint oreHarbor = new HarborPoint(30, 20, ResourceType.ORE);
        oreHarbor.owner = Turn.RED;
        oreHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(oreHarbor);


        redTrade.setResources(ResourceType.ORE, 2);
        redTrade.setResources(ResourceType.WHEAT, 4);
        bankTrade.setResources(ResourceType.WOOD, 2);

        HashMap<ResourceType, Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 2);
        expectedRedHand.put(ResourceType.BRICK, 0);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 0);
        expectedRedHand.put(ResourceType.ORE, 2);
        controller.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));
        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(2, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(2, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    void onBankSubmitClickWithBothHarborTypes() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.ORE, 4);
        redPlayer.addResources(ResourceType.WOOD, 3);
        redPlayer.addResources(ResourceType.WHEAT, 3);


        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);


        HarborPoint oreHarbor = new HarborPoint(30, 20, ResourceType.ORE);
        oreHarbor.owner = Turn.RED;
        oreHarbor.hasSettlement = true;
        HarborPoint genericHarbor = new HarborPoint(20, 10, ResourceType.NULL);
        genericHarbor.owner = Turn.RED;
        genericHarbor.hasSettlement = true;
        board.cityPoints = new ArrayList<>();
        board.cityPoints.add(oreHarbor);
        board.cityPoints.add(genericHarbor);

        redTrade.setResources(ResourceType.ORE, 2);
        redTrade.setResources(ResourceType.WOOD, 3);
        redTrade.setResources(ResourceType.WHEAT, 3);
        bankTrade.setResources(ResourceType.BRICK, 3);

        HashMap<ResourceType, Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 0);
        expectedRedHand.put(ResourceType.BRICK, 3);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 0);
        expectedRedHand.put(ResourceType.ORE, 2);
        controller.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));
        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);

        assertEquals(2, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(3, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    void onBankSubmitClickOfferIncludesNegativeResource() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);


        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);


        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WHEAT, 5);
        redPlayer.addResources(ResourceType.WOOD, 2);


        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);
        redTrade.setResources(ResourceType.WHEAT, 4);
        redTrade.setResources(ResourceType.WOOD, -1);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, -1);


        EasyMock.replay(controller);

        board.onBankSubmitClick(redTrade, bankTrade);


        assertEquals(5, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(2, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));


        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    void onBankSubmitClickRequestIsZeroAmount() {
        GameWindowController controller = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice mockDice = EasyMock.mock(Dice.class);

        Board board = new Board(controller, turnStateMachine, mockDice);


        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();

        EasyMock.replay(turnStateMachine, controller);


        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WHEAT, 5);


        TradeInfo redTrade = new TradeInfo();
        redTrade.setPlayer(Turn.RED);
        redTrade.setResources(ResourceType.WHEAT, 4);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 0);


        board.onBankSubmitClick(redTrade, bankTrade);


        assertEquals(5, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));


        EasyMock.verify(controller);
        EasyMock.verify(turnStateMachine);
    }


    @Test
    public void testKnightDevCard() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.robberMoved = true;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        assertTrue(robberPoint.hasRobber);
    }

    @Test
    public void testKnightRemovesDevCard() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.robberMoved = true;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        assertEquals(0, player.getDevCards().size());
    }

    @Test
    public void testKnightNoKnightCard() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.robberMoved = true;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);


        Player player = new Player(Turn.RED);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        assertFalse(robberPoint.hasRobber);
    }

    @Test
    public void testRobberDoesntMoveWithNoSevenRolledAfterKnight() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 2;
        testBoard.robberMoved = true;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        testBoard.robberMoved = false;

        testBoard.onRobberPointClick(2, 2);

        assertTrue(robberPoint.hasRobber);
        assertFalse(robberPoint2.hasRobber);
    }

    @Test
    public void testKnightUsedSameTurnBought() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.robberMoved = true;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(0);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        assertFalse(robberPoint.hasRobber);
    }

    @Test
    public void testRobberMoveBackToPrevious() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onRobberPointClick(1, 1);

        assertTrue(robberPoint.hasRobber);
        assertFalse(robberPoint2.hasRobber);

        testBoard.robberMoved = false;

        testBoard.onRobberPointClick(2, 2);

        assertTrue(robberPoint2.hasRobber);
        assertFalse(robberPoint.hasRobber);

        testBoard.robberMoved = false;

        testBoard.onRobberPointClick(1, 1);

        assertTrue(robberPoint.hasRobber);
        assertFalse(robberPoint2.hasRobber);
    }

    @Test
    public void testRobberMovesToSameSpot() {
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        testController.showInitialRobberState(1,1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine, testController);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onRobberPointClick(1, 1);

        testBoard.robberMoved = false;

        testBoard.onRobberPointClick(1, 1);

        EasyMock.verify(testController);
    }

    @Test
    public void testLargestArmyGives3Played(){
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        DevelopmentCard testCard2 = new DevelopmentCard(DevCards.KNIGHT);
        testCard2.setTurnBought(0);
        DevelopmentCard testCard3 = new DevelopmentCard(DevCards.KNIGHT);
        testCard3.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        player.addDevelopmentCard(testCard2);
        player.addDevelopmentCard(testCard3);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(2, 2);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);
        assertEquals(3, player.getVictoryPoints());
    }

    @Test
    public void testLargestArmyGives4Played(){
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        DevelopmentCard testCard2 = new DevelopmentCard(DevCards.KNIGHT);
        testCard2.setTurnBought(0);
        DevelopmentCard testCard3 = new DevelopmentCard(DevCards.KNIGHT);
        testCard3.setTurnBought(0);
        DevelopmentCard testCard4 = new DevelopmentCard(DevCards.KNIGHT);
        testCard4.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        player.addDevelopmentCard(testCard2);
        player.addDevelopmentCard(testCard3);
        player.addDevelopmentCard(testCard4);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(2, 2);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(2, 2);
        assertEquals(3, player.getVictoryPoints());
    }

    @Test
    public void testLargestArmyDoesNotGive2Played(){
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        DevelopmentCard testCard2 = new DevelopmentCard(DevCards.KNIGHT);
        testCard2.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        player.addDevelopmentCard(testCard2);
        testBoard.turnToPlayer.put(Turn.RED, player);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(2, 2);

        assertEquals(0, player.getVictoryPoints());
    }

    @Test
    public void testTwoPlayersSameKnightsPlayed(){
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        player.numKnightsPlayed = 2;
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        DevelopmentCard testCard2 = new DevelopmentCard(DevCards.KNIGHT);
        testCard2.setTurnBought(0);
        player2.addDevelopmentCard(testCard2);
        player2.numKnightsPlayed = 2;
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(2, 2);

        EasyMock.verify(turnStateMachine);

        assertEquals(3, player.getVictoryPoints());
        assertEquals(0, player2.getVictoryPoints());
    }

    @Test
    public void testTwoPlayersOneGetsLargestArmy(){
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        player.numKnightsPlayed = 2;
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        DevelopmentCard testCard2 = new DevelopmentCard(DevCards.KNIGHT);
        testCard2.setTurnBought(0);
        player2.addDevelopmentCard(testCard2);
        player2.numKnightsPlayed = 2;
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        EasyMock.verify(turnStateMachine);

        assertEquals(3, player.getVictoryPoints());
        assertEquals(0, player2.getVictoryPoints());
    }

    @Test
    public void testTwoPlayersStealLargestArmy(){
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        RobberPoint robberPoint2 = new RobberPoint(2, 2, ResourceType.BRICK, 4);
        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        robberPoints.add(robberPoint);
        robberPoints.add(robberPoint2);

        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(testController, turnStateMachine, dice);
        testBoard.robberPoints = robberPoints;
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;
        testBoard.cityPoints = new ArrayList<>();

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);

        EasyMock.replay(turnStateMachine);

        Player player = new Player(Turn.RED);
        DevelopmentCard testCard = new DevelopmentCard(DevCards.KNIGHT);
        testCard.setTurnBought(0);
        player.addDevelopmentCard(testCard);
        player.numKnightsPlayed = 2;
        testBoard.turnToPlayer.put(Turn.RED, player);

        Player player2 = new Player(Turn.BLUE);
        DevelopmentCard testCard2 = new DevelopmentCard(DevCards.KNIGHT);
        DevelopmentCard testCard3 = new DevelopmentCard(DevCards.KNIGHT);
        testCard2.setTurnBought(0);
        player2.addDevelopmentCard(testCard2);
        testCard3.setTurnBought(0);
        player2.addDevelopmentCard(testCard3);
        player2.numKnightsPlayed = 2;
        testBoard.turnToPlayer.put(Turn.BLUE, player2);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(2, 2);

        testBoard.onKnightCardClick();
        testBoard.onRobberPointClick(1, 1);

        assertEquals(0, player.getVictoryPoints());
        assertEquals(3, player2.getVictoryPoints());
    }

    @Test
    public void testMonopolyCannotBePlayedInPurchaseRound() {
        GameWindowController controller       = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine     turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        Player redPlayer = new Player(Turn.RED);
        DevelopmentCard monopolyCard = new DevelopmentCard(DevCards.MONOPOLY);
        monopolyCard.setTurnBought(2);
        redPlayer.addDevelopmentCard(monopolyCard);
        testBoard.turnToPlayer.put(Turn.RED, redPlayer);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        EasyMock.replay(controller, turnStateMachine);

        testBoard.onMonopolyClick();

        EasyMock.verify(controller, turnStateMachine);
        assertEquals(1, redPlayer.getDevCards().size());
    }

    @Test
    public void testMonopolyClickOpensDialog() {
        GameWindowController controller = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        Player redPlayer = new Player(Turn.RED);
        DevelopmentCard monopolyCard = new DevelopmentCard(DevCards.MONOPOLY);
        monopolyCard.setTurnBought(1);
        redPlayer.addDevelopmentCard(monopolyCard);
        testBoard.turnToPlayer.put(Turn.RED, redPlayer);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);
        controller.openMonopolyMenu(testBoard, Turn.RED);
        EasyMock.replay(controller, turnStateMachine);

        testBoard.onMonopolyClick();

        EasyMock.verify(controller, turnStateMachine);
    }

    @Test
    public void testExecuteMonopolyTransfersResources() {
        GameWindowController controller = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        Player redPlayer = new Player(Turn.RED);
        Player bluePlayer = new Player(Turn.BLUE);
        Player orangePlayer = new Player(Turn.ORANGE);

        DevelopmentCard monopolyCard = new DevelopmentCard(DevCards.MONOPOLY);
        monopolyCard.setTurnBought(0);
        redPlayer.addDevelopmentCard(monopolyCard);

        bluePlayer.addResources(ResourceType.WHEAT,3);
        orangePlayer.addResources(ResourceType.WHEAT,2);

        testBoard.turnToPlayer.put(Turn.RED, redPlayer);
        testBoard.turnToPlayer.put(Turn.BLUE, bluePlayer);
        testBoard.turnToPlayer.put(Turn.ORANGE, orangePlayer);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        controller.showResourceCards( EasyMock.eq(testBoard), EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.replay(controller, turnStateMachine);

        testBoard.executeMonopoly(Turn.RED, ResourceType.WHEAT);

        assertEquals(5, redPlayer.getResource(ResourceType.WHEAT));
        assertEquals(0, bluePlayer.getResource(ResourceType.WHEAT));
        assertEquals(0, orangePlayer.getResource(ResourceType.WHEAT));
        assertTrue(redPlayer.getDevCards().isEmpty());
    }

    @Test
    public void testExecuteMonopolyWhenNoResourcesAvailable() {
        GameWindowController controller = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice diceStub = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, diceStub);

        Player redPlayer = new Player(Turn.RED);
        Player bluePlayer = new Player(Turn.BLUE);
        Player orangePlayer = new Player(Turn.ORANGE);

        DevelopmentCard monopolyCard = new DevelopmentCard(DevCards.MONOPOLY);
        monopolyCard.setTurnBought(0);
        redPlayer.addDevelopmentCard(monopolyCard);

        testBoard.turnToPlayer.put(Turn.RED, redPlayer);
        testBoard.turnToPlayer.put(Turn.BLUE, bluePlayer);
        testBoard.turnToPlayer.put(Turn.ORANGE, orangePlayer);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        controller.showResourceCards( EasyMock.eq(testBoard), EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.replay(controller, turnStateMachine);

        testBoard.executeMonopoly(Turn.RED, ResourceType.WHEAT);

        assertEquals(0, redPlayer.getResource(ResourceType.WHEAT));
        assertTrue(redPlayer.getDevCards().isEmpty());
    }

    @Test
    public void testHandlePlacementUpdatesLongestRoadVP() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        // --- Setup ---
        // Segment 1: CityA(RED) -- R1(RED) -- C1 -- R2(RED) -- CityB(Target) (Length 2 roads to B)
        // Segment 2: CityE(RED) -- R3(RED) -- C2 -- R4(RED) -- C3 -- R5(RED) -- CityB(Target) (Length 3 roads to B)
        // Placing settlement at CityB connects R2 and R5.
        // Longest path should be 5 (A-R1-C1-R2-B-R5-C3-R4-C2-R3-E).

        // Create distinct points (coordinates need adjustment for distance)
        // Let CityB be at (100, 100)
        CityPoint cityB = new CityPoint(100, 100); // Target city for placement

        // Segment 1 leading to B (A-R1-C1-R2-B) - Need C1 far from E
        CityPoint cityA = new CityPoint(40, 100); cityA.owner = Turn.RED; cityA.hasSettlement = true;
        RoadPoint road1 = new RoadPoint(55, 100); road1.owner = Turn.RED; road1.hasRoad = true;
        CityPoint cityC1 = new CityPoint(70, 100);
        RoadPoint road2 = new RoadPoint(85, 100); road2.owner = Turn.RED; road2.hasRoad = true;
        // road2 connects C1 and B

        // Segment 2 leading to B (E-R3-C2-R4-C3-R5-B) - Need C3 far from A
        CityPoint cityE = new CityPoint(100, 160); cityE.owner = Turn.RED; cityE.hasSettlement = true;
        RoadPoint road3 = new RoadPoint(100, 145); road3.owner = Turn.RED; road3.hasRoad = true;
        CityPoint cityC2 = new CityPoint(100, 130);
        RoadPoint road4 = new RoadPoint(100, 115); road4.owner = Turn.RED; road4.hasRoad = true;
        CityPoint cityC3 = new CityPoint(115, 108); // Adjust coords for linking
        RoadPoint road5 = new RoadPoint(108, 104); road5.owner = Turn.RED; road5.hasRoad = true;
        // road5 connects C3 and B

        // --- Link Everything Correctly ---
        // Segment 1: A <-> R1 <-> C1 <-> R2 <-> B
        cityA.addNeighbor(road1); road1.addNeighbor(cityA); road1.addNeighbor(cityC1);
        cityC1.addNeighbor(road1); cityC1.addNeighbor(road2);
        road2.addNeighbor(cityC1); road2.addNeighbor(cityB);
        cityB.addNeighbor(road2);

        // Segment 2: E <-> R3 <-> C2 <-> R4 <-> C3 <-> R5 <-> B
        cityE.addNeighbor(road3); road3.addNeighbor(cityE); road3.addNeighbor(cityC2);
        cityC2.addNeighbor(road3); cityC2.addNeighbor(road4);
        road4.addNeighbor(cityC2); road4.addNeighbor(cityC3);
        cityC3.addNeighbor(road4); cityC3.addNeighbor(road5);
        road5.addNeighbor(cityC3); road5.addNeighbor(cityB);
        cityB.addNeighbor(road5); // B is connected to R2 and R5

        testBoard.cityPoints = new ArrayList<>(List.of(cityA, cityB, cityC1, cityC2, cityC3, cityE));
        testBoard.roadPoints = new ArrayList<>(List.of(road1, road2, road3, road4, road5));

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.BRICK, 1);
        redPlayer.addResources(ResourceType.SHEEP, 1);
        redPlayer.addResources(ResourceType.WHEAT, 1);
        redPlayer.settlements = 3; // Simulate having placed cityA and cityE already
        redPlayer.roads = 10; // Simulate having placed the 5 roads

        int initialVP = redPlayer.getVictoryPoints(); // Should be 2 (from cityA, cityE)

        // Expectations for placing cityB
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3); // Must be > 2
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine, controller, dice); // Replay all mocks

        // Action: Place the settlement at CityB
        testBoard.onCityPointClick(cityB.getX(), cityB.getY());

        // Verification
        EasyMock.verify(turnStateMachine, controller, dice); // Verify all mocks
        assertTrue(cityB.hasSettlement, "Settlement should be placed at CityB"); // Check placement first
        assertEquals(Turn.RED, cityB.getOwner(), "CityB owner should be RED");
        // VP = Initial(2) + Settlement(1) + LongestRoad(2) = 5
        assertEquals(initialVP + 1 + 2, redPlayer.getVictoryPoints(), "Player VP should be Initial(2) + Settlement(1) + LongestRoad(2) = 5.");
    }

    @Test
    public void testOnRoadClickFailsIfAdjacentRoadOpponentOwned() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        CityPoint city1 = new CityPoint(10, 10);
        RoadPoint blueRoad = new RoadPoint(20, 10); blueRoad.owner = Turn.BLUE; blueRoad.hasRoad = true;
        CityPoint city2 = new CityPoint(30, 10);
        RoadPoint targetRoad = new RoadPoint(40, 10);

        city1.neighbors.add(blueRoad); blueRoad.neighbors.add(city1); blueRoad.neighbors.add(city2);
        city2.neighbors.add(blueRoad); city2.neighbors.add(targetRoad);
        targetRoad.neighbors.add(city2);

        testBoard.cityPoints = new ArrayList<>(List.of(city1, city2));
        testBoard.roadPoints = new ArrayList<>(List.of(blueRoad, targetRoad));

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.BRICK, 1);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.onRoadPointClick(40, 10);

        EasyMock.verify(turnStateMachine, controller, dice);
        assertFalse(targetRoad.hasRoad, "RED should not be able to place road next to only BLUE road.");
        assertEquals(Turn.NONE, targetRoad.getOwner());
    }

    @Test
    public void testOnRoadClickAdjacentToOwnedSettlement() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        CityPoint redCity = new CityPoint(10, 10); redCity.owner = Turn.RED; redCity.hasSettlement = true;
        RoadPoint targetRoad = new RoadPoint(20, 10);

        redCity.neighbors.add(targetRoad); targetRoad.neighbors.add(redCity);

        testBoard.cityPoints = new ArrayList<>(List.of(redCity));
        testBoard.roadPoints = new ArrayList<>(List.of(targetRoad));

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.BRICK, 1);
        redPlayer.roads = 13;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.onRoadPointClick(20, 10);

        EasyMock.verify(turnStateMachine, controller, dice);
        assertTrue(targetRoad.hasRoad, "Road should be placed next to owned settlement.");
        assertEquals(Turn.RED, targetRoad.getOwner());
    }

    @Test
    public void testOnRoadClickAdjacentToUnownedSettlementWithOwnedRoad() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        CityPoint city1 = new CityPoint(10, 10);
        RoadPoint redRoad = new RoadPoint(20, 10); redRoad.owner = Turn.RED; redRoad.hasRoad = true;
        CityPoint unownedCity = new CityPoint(30, 10);
        RoadPoint targetRoad = new RoadPoint(40, 10);

        city1.neighbors.add(redRoad); redRoad.neighbors.add(city1); redRoad.neighbors.add(unownedCity);
        unownedCity.neighbors.add(redRoad); unownedCity.neighbors.add(targetRoad);
        targetRoad.neighbors.add(unownedCity);

        testBoard.cityPoints = new ArrayList<>(List.of(city1, unownedCity));
        testBoard.roadPoints = new ArrayList<>(List.of(redRoad, targetRoad));

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.BRICK, 1);
        redPlayer.roads = 13;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.onRoadPointClick(40, 10);

        EasyMock.verify(turnStateMachine, controller, dice);
        assertTrue(targetRoad.hasRoad, "Road should be placed adjacent to unowned city via owned road.");
        assertEquals(Turn.RED, targetRoad.getOwner());
    }

    @Test
    public void testOnRoadClickFailsAdjacentToOpponentSettlement() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        CityPoint blueCity = new CityPoint(10, 10); blueCity.owner = Turn.BLUE; blueCity.hasSettlement = true;
        RoadPoint targetRoad = new RoadPoint(20, 10);

        blueCity.neighbors.add(targetRoad); targetRoad.neighbors.add(blueCity);

        testBoard.cityPoints = new ArrayList<>(List.of(blueCity));
        testBoard.roadPoints = new ArrayList<>(List.of(targetRoad));

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.BRICK, 1);
        redPlayer.roads = 13;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.onRoadPointClick(20, 10);

        EasyMock.verify(turnStateMachine, controller, dice);
        assertFalse(targetRoad.hasRoad, "Road should not be placed next to opponent settlement.");
        assertEquals(Turn.NONE, targetRoad.getOwner());
    }

    @Test
    public void testOnRoadClickFailsAdjacentToUnownedSettlementNoOwnedRoad() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        CityPoint unownedCity = new CityPoint(10, 10);
        RoadPoint targetRoad = new RoadPoint(20, 10);

        unownedCity.neighbors.add(targetRoad); targetRoad.neighbors.add(unownedCity);

        testBoard.cityPoints = new ArrayList<>(List.of(unownedCity));
        testBoard.roadPoints = new ArrayList<>(List.of(targetRoad));

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.BRICK, 1);
        redPlayer.roads = 13;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.onRoadPointClick(20, 10);

        EasyMock.verify(turnStateMachine, controller, dice);
        assertFalse(targetRoad.hasRoad, "Road should not be placed next to unowned settlement without connection.");
        assertEquals(Turn.NONE, targetRoad.getOwner());
    }

    @Test
    public void testConnectedToOwnedRoadOrCityTrueForOwnedCity() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        CityPoint redCity = new CityPoint(10, 10); redCity.owner = Turn.RED; redCity.hasSettlement = true;
        RoadPoint targetRoad = new RoadPoint(20, 10);

        redCity.neighbors.add(targetRoad); targetRoad.neighbors.add(redCity);

        testBoard.cityPoints = new ArrayList<>(List.of(redCity));
        testBoard.roadPoints = new ArrayList<>(List.of(targetRoad));

        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.BRICK, 1);
        testBoard.turnToPlayer.get(Turn.RED).roads = 13;
        EasyMock.reset(turnStateMachine);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        EasyMock.replay(turnStateMachine);

        testBoard.onRoadPointClick(20, 10);

        EasyMock.verify(turnStateMachine);
        assertTrue(targetRoad.hasRoad, "connectedToOwnedRoadOrCity should allow placement when connected to owned city.");
    }

    @Test
    public void testConnectedToOwnedRoadOrCityTrueForOwnedConnectingRoad() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        CityPoint city1 = new CityPoint(10, 10);
        RoadPoint redRoad = new RoadPoint(20, 10); redRoad.owner = Turn.RED; redRoad.hasRoad = true;
        CityPoint city2 = new CityPoint(30, 10);
        RoadPoint targetRoad = new RoadPoint(40, 10);

        city1.neighbors.add(redRoad); redRoad.neighbors.add(city1); redRoad.neighbors.add(city2);
        city2.neighbors.add(redRoad); city2.neighbors.add(targetRoad);
        targetRoad.neighbors.add(city2);

        testBoard.cityPoints = new ArrayList<>(List.of(city1, city2));
        testBoard.roadPoints = new ArrayList<>(List.of(redRoad, targetRoad));

        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.WOOD, 1);
        testBoard.turnToPlayer.get(Turn.RED).addResources(ResourceType.BRICK, 1);
        testBoard.turnToPlayer.get(Turn.RED).roads = 13;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.onRoadPointClick(40, 10);

        EasyMock.verify(turnStateMachine);
        assertTrue(targetRoad.hasRoad, "connectedToOwnedRoadOrCity should allow placement via connecting owned road.");
    }

    @Test
    public void testConnectedToOwnedRoadOrCityReturnsFalseForUnconnectedRoad() {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);


        CityPoint city1 = new CityPoint(10, 10);
        RoadPoint targetRoad = new RoadPoint(20, 10);
        CityPoint city2 = new CityPoint(30, 10);

        city1.neighbors.add(targetRoad); targetRoad.neighbors.add(city1); targetRoad.neighbors.add(city2);
        city2.neighbors.add(targetRoad);

        testBoard.cityPoints = new ArrayList<>(List.of(city1, city2));
        testBoard.roadPoints = new ArrayList<>(List.of(targetRoad));

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 1);
        redPlayer.addResources(ResourceType.BRICK, 1);
        redPlayer.roads = 13;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);

        EasyMock.replay(turnStateMachine, controller, dice);

        testBoard.onRoadPointClick(20, 10);

        EasyMock.verify(turnStateMachine, controller, dice);
        assertFalse(targetRoad.hasRoad, "Road placement should fail if not connected to owned pieces.");
    }
    @Test
    public void testLoadBoardDataFullDataPopulatesCorrectly() throws IOException {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.niceMock(TurnStateMachine.class);
        Dice dice = EasyMock.niceMock(Dice.class);
        BoardDataInputs dataInputs = EasyMock.createStrictMock(BoardDataInputs.class);

        Board testBoard = new Board(controller, turnStateMachine, dice);

        String cityCoordsDataStr = getActualCityCoordsData();
        String cityNeighborsDataStr = getActualCityNeighborsData();
        String cityTerrainDataStr = getActualCityTerrainData();
        String cityValuesDataStr = getActualCityValuesData();
        String harborsDataStr = getActualHarborsData();
        String roadCoordsDataStr = getActualRoadCoordsData();
        String roadNeighborsDataStr = getActualRoadNeighborsData();
        String robberCoordsDataStr = getActualRobberCoordsData();
        String robberResourcesDataStr = getActualRobberResourceData();
        String robberNumbersDataStr = getActualRobberNumberData();

        EasyMock.expect(dataInputs.getCityCoordsStream()).andReturn(createMockFileInputStreamFromString(cityCoordsDataStr));
        EasyMock.expect(dataInputs.getCityTerrainsStream()).andReturn(createMockFileInputStreamFromString(cityTerrainDataStr));
        EasyMock.expect(dataInputs.getCityValuesStream()).andReturn(createMockFileInputStreamFromString(cityValuesDataStr));
        EasyMock.expect(dataInputs.getHarborsStream()).andReturn(createMockFileInputStreamFromString(harborsDataStr));
        EasyMock.expect(dataInputs.getRoadCoordsStream()).andReturn(createMockFileInputStreamFromString(roadCoordsDataStr));
        EasyMock.expect(dataInputs.getCityNeighborsStream()).andReturn(createMockFileInputStreamFromString(cityNeighborsDataStr));
        EasyMock.expect(dataInputs.getRoadNeighborsStream()).andReturn(createMockFileInputStreamFromString(roadNeighborsDataStr));
        EasyMock.expect(dataInputs.getRobberCoordsStream()).andReturn(createMockFileInputStreamFromString(robberCoordsDataStr));
        EasyMock.expect(dataInputs.getRobberResourceStream()).andReturn(createMockFileInputStreamFromString(robberResourcesDataStr));
        EasyMock.expect(dataInputs.getRobberNumberStream()).andReturn(createMockFileInputStreamFromString(robberNumbersDataStr));

        EasyMock.replay(controller, turnStateMachine, dice, dataInputs);
        testBoard.loadBoardData(dataInputs);
        EasyMock.verify(dataInputs);

        assertEquals(Board.NUM_CITYPOINTS, testBoard.cityPoints.size());
        assertEquals(Board.NUM_ROADPOINTS, testBoard.roadPoints.size());
        int expectedRobberPoints = robberCoordsDataStr.isEmpty() ? 0 : robberCoordsDataStr.split("\n").length;
        if (robberCoordsDataStr.trim().isEmpty() && expectedRobberPoints > 0) expectedRobberPoints = 0;
        assertEquals(expectedRobberPoints, testBoard.robberPoints.size());

        CityPoint city0_harbor = testBoard.getCityAtCoords(275, 113);
        assertNotNull(city0_harbor);
        assertEquals(275, city0_harbor.getX());
        assertEquals(113, city0_harbor.getY());
        assertTrue(city0_harbor instanceof HarborPoint);
        assertEquals(ResourceType.NULL, ((HarborPoint) city0_harbor).getTradingResource());
        assertEquals(List.of(Terrain.HILL), city0_harbor.getTerrains());
        assertEquals(List.of(Integer.valueOf(8)), city0_harbor.getTileValues());
        assertEquals(2, city0_harbor.neighbors.size());
        RoadPoint road0_instance = testBoard.getRoadAtCoords(243,130);
        RoadPoint road1_instance = testBoard.getRoadAtCoords(302,132);
        assertNotNull(road0_instance);
        assertNotNull(road1_instance);
        assertTrue(city0_harbor.neighbors.contains(road0_instance));
        assertTrue(city0_harbor.neighbors.contains(road1_instance));

        CityPoint city1_harbor = testBoard.getCityAtCoords(390, 113);
        assertNotNull(city1_harbor);
        assertTrue(city1_harbor instanceof HarborPoint);
        assertEquals(ResourceType.WHEAT, ((HarborPoint) city1_harbor).getTradingResource());
        assertEquals(List.of(Terrain.FOREST), city1_harbor.getTerrains());
        assertEquals(List.of(Integer.valueOf(10)), city1_harbor.getTileValues());
        assertEquals(2, city1_harbor.neighbors.size());
        RoadPoint road2_instance = testBoard.getRoadAtCoords(361,130);
        RoadPoint road3_instance = testBoard.getRoadAtCoords(420,132);
        assertNotNull(road2_instance);
        assertNotNull(road3_instance);
        assertTrue(city1_harbor.neighbors.contains(road2_instance));
        assertTrue(city1_harbor.neighbors.contains(road3_instance));


        CityPoint city4 = testBoard.getCityAtCoords(330,147);
        assertNotNull(city4);
        assertFalse(city4 instanceof HarborPoint);
        assertEquals(List.of(Terrain.HILL, Terrain.FOREST), city4.getTerrains());
        assertEquals(Arrays.asList(Integer.valueOf(8), Integer.valueOf(10)), city4.getTileValues());
        assertEquals(3, city4.neighbors.size());
        RoadPoint road7_instance = testBoard.getRoadAtCoords(331,183);
        assertNotNull(road7_instance);
        assertTrue(city4.neighbors.contains(road1_instance));
        assertTrue(city4.neighbors.contains(road2_instance));
        assertTrue(city4.neighbors.contains(road7_instance));


        CityPoint city3_instance = testBoard.getCityAtCoords(215,147);
        assertNotNull(city3_instance);
        assertEquals(2, road0_instance.neighbors.size());
        assertTrue(road0_instance.neighbors.contains(city0_harbor));
        assertTrue(road0_instance.neighbors.contains(city3_instance));
        assertTrue(road0_instance.neighbors.stream().anyMatch(n -> n == city0_harbor));
        assertTrue(road0_instance.neighbors.stream().anyMatch(n -> n == city3_instance));


        assertEquals(2, road1_instance.neighbors.size());
        assertTrue(road1_instance.neighbors.contains(city0_harbor));
        assertTrue(road1_instance.neighbors.contains(city4));
        assertTrue(road1_instance.neighbors.stream().anyMatch(n -> n == city0_harbor));
        assertTrue(road1_instance.neighbors.stream().anyMatch(n -> n == city4));


        RoadPoint road6_instance = testBoard.getRoadAtCoords(211,183);
        CityPoint city7_instance = testBoard.getCityAtCoords(213,215);
        assertNotNull(road6_instance);
        assertNotNull(city7_instance);
        assertEquals(2, road6_instance.neighbors.size());
        assertTrue(road6_instance.neighbors.contains(city3_instance));
        assertTrue(road6_instance.neighbors.contains(city7_instance));

    }
    @Test
    public void testLoadBoardDataAllStreamsEmptyThrowsExpectedException() throws IOException {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.niceMock(TurnStateMachine.class);
        Dice dice = EasyMock.niceMock(Dice.class);
        BoardDataInputs dataInputs = EasyMock.createStrictMock(BoardDataInputs.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        String emptyData = "";

        EasyMock.expect(dataInputs.getCityCoordsStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getCityTerrainsStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getCityValuesStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getHarborsStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getRoadCoordsStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getCityNeighborsStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getRoadNeighborsStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getRobberCoordsStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getRobberResourceStream()).andReturn(createMockFileInputStreamFromString(emptyData));
        EasyMock.expect(dataInputs.getRobberNumberStream()).andReturn(createMockFileInputStreamFromString(emptyData));

        EasyMock.replay(controller, turnStateMachine, dice, dataInputs);

        assertThrows(RuntimeException.class, () -> {
            testBoard.loadBoardData(dataInputs);
        });
    }

    @Test
    public void testLoadBoardDataCityNeighborIndexOutOfBounds() throws IOException {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.niceMock(TurnStateMachine.class);
        Dice dice = EasyMock.niceMock(Dice.class);
        BoardDataInputs dataInputs = EasyMock.createStrictMock(BoardDataInputs.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        String cityCoords = "0,0\n1,1";
        String roadCoords = "10,10";
        String cityNeighbors = "10\n";

        EasyMock.expect(dataInputs.getCityCoordsStream()).andReturn(createMockFileInputStreamFromString(cityCoords));
        EasyMock.expect(dataInputs.getCityTerrainsStream()).andReturn(createMockFileInputStreamFromString("DESERT\nDESERT"));
        EasyMock.expect(dataInputs.getCityValuesStream()).andReturn(createMockFileInputStreamFromString("0\n0"));
        EasyMock.expect(dataInputs.getHarborsStream()).andReturn(createMockFileInputStreamFromString(""));
        EasyMock.expect(dataInputs.getRoadCoordsStream()).andReturn(createMockFileInputStreamFromString(roadCoords));
        EasyMock.expect(dataInputs.getCityNeighborsStream()).andReturn(createMockFileInputStreamFromString(cityNeighbors));
        EasyMock.expect(dataInputs.getRoadNeighborsStream()).andReturn(createMockFileInputStreamFromString(getActualRoadNeighborsData())).anyTimes();
        EasyMock.expect(dataInputs.getRobberCoordsStream()).andReturn(createMockFileInputStreamFromString(getActualRobberCoordsData())).anyTimes();
        EasyMock.expect(dataInputs.getRobberResourceStream()).andReturn(createMockFileInputStreamFromString(getActualRobberResourceData())).anyTimes();
        EasyMock.expect(dataInputs.getRobberNumberStream()).andReturn(createMockFileInputStreamFromString(getActualRobberNumberData())).anyTimes();

        EasyMock.replay(controller, turnStateMachine, dice, dataInputs);


        assertThrows(IndexOutOfBoundsException.class, () -> testBoard.loadBoardData(dataInputs));
    }

    @Test
    public void testLoadBoardDataNoHarbors() throws IOException {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.niceMock(TurnStateMachine.class);
        Dice dice = EasyMock.niceMock(Dice.class);
        BoardDataInputs dataInputs = EasyMock.createStrictMock(BoardDataInputs.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        String cityCoordsDataStr = getActualCityCoordsData();
        String cityNeighborsDataStr = getActualCityNeighborsData();
        String cityTerrainDataStr = getActualCityTerrainData();
        String cityValuesDataStr = getActualCityValuesData();
        String emptyHarborsDataStr = "";
        String roadCoordsDataStr = getActualRoadCoordsData();
        String roadNeighborsDataStr = getActualRoadNeighborsData();
        String robberCoordsDataStr = getActualRobberCoordsData();
        String robberResourcesDataStr = getActualRobberResourceData();
        String robberNumbersDataStr = getActualRobberNumberData();

        EasyMock.expect(dataInputs.getCityCoordsStream()).andReturn(createMockFileInputStreamFromString(cityCoordsDataStr));
        EasyMock.expect(dataInputs.getCityTerrainsStream()).andReturn(createMockFileInputStreamFromString(cityTerrainDataStr));
        EasyMock.expect(dataInputs.getCityValuesStream()).andReturn(createMockFileInputStreamFromString(cityValuesDataStr));
        EasyMock.expect(dataInputs.getHarborsStream()).andReturn(createMockFileInputStreamFromString(emptyHarborsDataStr));
        EasyMock.expect(dataInputs.getRoadCoordsStream()).andReturn(createMockFileInputStreamFromString(roadCoordsDataStr));
        EasyMock.expect(dataInputs.getCityNeighborsStream()).andReturn(createMockFileInputStreamFromString(cityNeighborsDataStr));
        EasyMock.expect(dataInputs.getRoadNeighborsStream()).andReturn(createMockFileInputStreamFromString(roadNeighborsDataStr));
        EasyMock.expect(dataInputs.getRobberCoordsStream()).andReturn(createMockFileInputStreamFromString(robberCoordsDataStr));
        EasyMock.expect(dataInputs.getRobberResourceStream()).andReturn(createMockFileInputStreamFromString(robberResourcesDataStr));
        EasyMock.expect(dataInputs.getRobberNumberStream()).andReturn(createMockFileInputStreamFromString(robberNumbersDataStr));
        EasyMock.replay(controller, turnStateMachine, dice, dataInputs);

        testBoard.loadBoardData(dataInputs);
        EasyMock.verify(dataInputs);

        assertEquals(Board.NUM_CITYPOINTS, testBoard.cityPoints.size());
        for (CityPoint cp : testBoard.cityPoints) {
            assertFalse(cp instanceof HarborPoint);
        }
        CityPoint cityAt275_113 = testBoard.getCityAtCoords(275, 113);
        assertNotNull(cityAt275_113);
        assertFalse(cityAt275_113 instanceof HarborPoint);
        assertEquals(List.of(Terrain.HILL), cityAt275_113.getTerrains());
        assertEquals(List.of(Integer.valueOf(8)), cityAt275_113.getTileValues());
    }


    @Test
    public void testLoadBoardDataBadCityPoint() throws IOException {
        GameWindowController controller = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.niceMock(TurnStateMachine.class);
        Dice dice = EasyMock.niceMock(Dice.class);
        BoardDataInputs dataInputs = EasyMock.createStrictMock(BoardDataInputs.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        String malformedCityCoords = "275,113\nabc,def\n515,113";

        EasyMock.expect(dataInputs.getCityCoordsStream()).andReturn(createMockFileInputStreamFromString(malformedCityCoords));
        EasyMock.expect(dataInputs.getCityTerrainsStream()).andReturn(createMockFileInputStreamFromString(getActualCityTerrainData())).anyTimes();
        EasyMock.expect(dataInputs.getCityValuesStream()).andReturn(createMockFileInputStreamFromString(getActualCityValuesData())).anyTimes();
        EasyMock.expect(dataInputs.getHarborsStream()).andReturn(createMockFileInputStreamFromString(getActualHarborsData())).anyTimes();
        EasyMock.expect(dataInputs.getRoadCoordsStream()).andReturn(createMockFileInputStreamFromString(getActualRoadCoordsData())).anyTimes();
        EasyMock.expect(dataInputs.getCityNeighborsStream()).andReturn(createMockFileInputStreamFromString(getActualCityNeighborsData())).anyTimes();
        EasyMock.expect(dataInputs.getRoadNeighborsStream()).andReturn(createMockFileInputStreamFromString(getActualRoadNeighborsData())).anyTimes();
        EasyMock.expect(dataInputs.getRobberCoordsStream()).andReturn(createMockFileInputStreamFromString(getActualRobberCoordsData())).anyTimes();
        EasyMock.expect(dataInputs.getRobberResourceStream()).andReturn(createMockFileInputStreamFromString(getActualRobberResourceData())).anyTimes();
        EasyMock.expect(dataInputs.getRobberNumberStream()).andReturn(createMockFileInputStreamFromString(getActualRobberNumberData())).anyTimes();
        EasyMock.replay(controller, turnStateMachine, dice, dataInputs);

        assertThrows(RuntimeException.class, () -> testBoard.loadBoardData(dataInputs));
    }
    @Test
    public void testOnBankSubmitClickOwnsSettlementNotAHarborPoint() {
        GameWindowController controller = EasyMock.createStrictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.createStrictMock(TurnStateMachine.class);
        Dice dice = EasyMock.niceMock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);
        testBoard.cityPoints = new ArrayList<>();

        Player redPlayer = testBoard.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.BRICK, 4);
        redPlayer.addResources(ResourceType.WOOD, 0);


        CityPoint regularCityPoint = new CityPoint(1, 1);
        regularCityPoint.owner = Turn.RED;
        regularCityPoint.hasSettlement();
        testBoard.cityPoints.add(regularCityPoint);

        TradeInfo playerTrade = new TradeInfo();
        playerTrade.setPlayer(Turn.RED);
        playerTrade.setResources(ResourceType.BRICK, 4);

        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.WOOD, 1);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        controller.showResourceCards(EasyMock.eq(testBoard), EasyMock.anyObject(HashMap.class));
        EasyMock.expectLastCall().times(1);

        EasyMock.replay(controller, turnStateMachine, dice);
        testBoard.onBankSubmitClick(playerTrade, bankTrade);
        EasyMock.verify(controller, turnStateMachine);

        assertEquals(0, redPlayer.getResource(ResourceType.BRICK)); // 4 used at 4:1 default
        assertEquals(1, redPlayer.getResource(ResourceType.WOOD));
    }



    @Test
    public void testExecuteMonopolyIgnoresPlayerWithZeroResources() {
        GameWindowController controller = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board testBoard = new Board(controller, turnStateMachine, dice);

        Player redPlayer = new Player(Turn.RED);
        Player bluePlayer = new Player(Turn.BLUE);
        Player orangePlayer = new Player(Turn.ORANGE);

        DevelopmentCard monopolyCard = new DevelopmentCard(DevCards.MONOPOLY);
        monopolyCard.setTurnBought(0);
        redPlayer.addDevelopmentCard(monopolyCard);

        bluePlayer.addResources(ResourceType.WHEAT, 0);
        orangePlayer.addResources(ResourceType.WHEAT, 2);

        testBoard.turnToPlayer.put(Turn.RED, redPlayer);
        testBoard.turnToPlayer.put(Turn.BLUE, bluePlayer);
        testBoard.turnToPlayer.put(Turn.ORANGE, orangePlayer);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        controller.showResourceCards(EasyMock.eq(testBoard), EasyMock.anyObject());
        EasyMock.expectLastCall().once();
        EasyMock.replay(controller, turnStateMachine);

        testBoard.executeMonopoly(Turn.RED, ResourceType.WHEAT);

        EasyMock.verify(controller);
        assertEquals(2, redPlayer.getResource(ResourceType.WHEAT));
        assertEquals(0, bluePlayer.getResource(ResourceType.WHEAT));
        assertEquals(0, orangePlayer.getResource(ResourceType.WHEAT));
    }

    @Test
    void onMonopolyClickPlayerHasOtherDevCardNotMonopoly() {
        GameWindowController controller = EasyMock.strictMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board = new Board(controller, turnStateMachine, dice);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);

        DevelopmentCard knightCard = new DevelopmentCard(DevCards.KNIGHT);
        knightCard.setTurnBought(1);
        redPlayer.addDevelopmentCard(knightCard);
        DevelopmentCard vpCard = new DevelopmentCard(DevCards.VICTORY_POINT);
        redPlayer.addDevelopmentCard(vpCard);



        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(2);

        EasyMock.replay(controller, turnStateMachine);


        board.onMonopolyClick();


        EasyMock.verify(controller, turnStateMachine);

        assertEquals(2, redPlayer.getDevCards().size());
        assertTrue(redPlayer.getDevCards().stream().anyMatch(c -> c.getType() == DevCards.KNIGHT));
        assertTrue(redPlayer.getDevCards().stream().anyMatch(c -> c.getType() == DevCards.VICTORY_POINT));
    }


    @Test
    void executeMonopolyPlayerHasNoDevCards() {
        GameWindowController controller = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board = new Board(controller, turnStateMachine, dice);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        Player bluePlayer = board.turnToPlayer.get(Turn.BLUE);

        bluePlayer.addResources(ResourceType.ORE, 2);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();

        HashMap<ResourceType,Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 0);
        expectedRedHand.put(ResourceType.BRICK, 0);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 0);
        expectedRedHand.put(ResourceType.ORE,  2);
        controller.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));
        EasyMock.replay(controller, turnStateMachine);

        board.executeMonopoly(Turn.RED, ResourceType.ORE);


        assertEquals(2, redPlayer.getResource(ResourceType.ORE));
        assertEquals(0, bluePlayer.getResource(ResourceType.ORE));

        assertTrue(redPlayer.getDevCards().isEmpty());

        EasyMock.verify(controller, turnStateMachine);
    }

    @Test
    void executeMonopolyPlayerHasOtherDevCardsNotMonopoly() {
        GameWindowController controller = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board = new Board(controller, turnStateMachine, dice);

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        Player bluePlayer = board.turnToPlayer.get(Turn.BLUE);

        DevelopmentCard knightCard = new DevelopmentCard(DevCards.KNIGHT);
        knightCard.setTurnBought(1);
        redPlayer.addDevelopmentCard(knightCard);

        bluePlayer.addResources(ResourceType.ORE, 2);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();

        HashMap<ResourceType,Integer> expectedRedHand = new HashMap<>();
        expectedRedHand.put(ResourceType.WOOD, 0);
        expectedRedHand.put(ResourceType.BRICK, 0);
        expectedRedHand.put(ResourceType.SHEEP, 0);
        expectedRedHand.put(ResourceType.WHEAT, 0);
        expectedRedHand.put(ResourceType.ORE,  2);
        controller.showResourceCards(EasyMock.eq(board), EasyMock.eq(expectedRedHand));
        EasyMock.replay(controller, turnStateMachine);


        board.executeMonopoly(Turn.RED, ResourceType.ORE);


        assertEquals(2, redPlayer.getResource(ResourceType.ORE));
        assertEquals(0, bluePlayer.getResource(ResourceType.ORE));

        assertEquals(1, redPlayer.getDevCards().size());
        assertEquals(DevCards.KNIGHT, redPlayer.getDevCards().getFirst().getType());

        EasyMock.verify(controller, turnStateMachine);
    }

    @Test
    public void testCannotRobOnSecond9(){
        GameWindow gameWindow = EasyMock.mock(GameWindow.class);
        GameWindowController gameWindowController = new GameWindowController(gameWindow);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board;
        try {
            board = new Board(gameWindowController, turnStateMachine, dice);
            FileInputStream cityCoordsStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
            FileInputStream cityTerrainsStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
            FileInputStream cityValuesStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
            FileInputStream harborsStream = new FileInputStream(Board.HARBORS_FILE_PATH);
            FileInputStream roadCoordsStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
            FileInputStream cityNeighborsStream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
            FileInputStream roadNeighborsStream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
            FileInputStream robberCoordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
            FileInputStream robberResourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
            FileInputStream robberNumberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);

            BoardDataInputs dataInputs = new BoardDataInputs(
                    cityCoordsStream, cityTerrainsStream, cityValuesStream,
                    harborsStream, roadCoordsStream, cityNeighborsStream,
                    roadNeighborsStream, robberCoordsStream, robberResourceStream,
                    robberNumberStream
            );
            board.loadBoardData(dataInputs);

        } catch (IOException excep) {

            throw new RuntimeException("Failed to load essential game data, application cannot start.", excep);
        }
        board.addAllCities(board.cityPoints);
        board.addAllRoadPoints(board.roadPoints);
        board.addAllRobberPoints(board.robberPoints);

        //REPLAY
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.BLUE);
        EasyMock.replay(turnStateMachine);

        //PLACE RED
        board.onCityPointClick(153, 320);

        //ROB OTHER 9
        board.robberMoved = false;
        board.numRolled = 7;
        board.onRobberPointClick(509, 390);

        //ASSERT
        assertEquals(0, board.eligiblePlayers.size());
    }

    @Test
    public void testDouble9CorrectResources(){
        GameWindow gameWindow = EasyMock.mock(GameWindow.class);
        GameWindowController gameWindowController = new GameWindowController(gameWindow);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board;
        try {
            board = new Board(gameWindowController, turnStateMachine, dice);
            FileInputStream cityCoordsStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
            FileInputStream cityTerrainsStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
            FileInputStream cityValuesStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
            FileInputStream harborsStream = new FileInputStream(Board.HARBORS_FILE_PATH);
            FileInputStream roadCoordsStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
            FileInputStream cityNeighborsStream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
            FileInputStream roadNeighborsStream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
            FileInputStream robberCoordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
            FileInputStream robberResourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
            FileInputStream robberNumberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);

            BoardDataInputs dataInputs = new BoardDataInputs(
                    cityCoordsStream, cityTerrainsStream, cityValuesStream,
                    harborsStream, roadCoordsStream, cityNeighborsStream,
                    roadNeighborsStream, robberCoordsStream, robberResourceStream,
                    robberNumberStream
            );
            board.loadBoardData(dataInputs);

        } catch (IOException excep) {

            throw new RuntimeException("Failed to load essential game data, application cannot start.", excep);
        }
        board.addAllCities(board.cityPoints);
        board.addAllRoadPoints(board.roadPoints);
        board.addAllRobberPoints(board.robberPoints);

        Player p1 = new Player(Turn.RED);
        board.turnToPlayer.put(Turn.RED, p1);

        //REPLAY
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(1);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(dice.roll()).andReturn(9);
        EasyMock.replay(dice, turnStateMachine);

        //PLACE RED
        board.onCityPointClick(211, 352);

        //ROLL RICe
        board.onRollDiceClick();

        //ASSERT
        assertEquals(true, p1.getResourcesAsList().contains(ResourceType.SHEEP));
    }

    @Test
    public void testUpgradeSettlementCostsResources() {
        GameWindow gameWindow = EasyMock.mock(GameWindow.class);
        GameWindowController testController = EasyMock.mock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(testController, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(1, 1);
        cityPoint.hasSettlement = true;
        cityPoint.owner = TurnStateMachine.FIRST_TURN;
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.ORE, 3);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).addResources(ResourceType.WHEAT, 2);
        testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).settlements = 3;

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(TurnStateMachine.FIRST_TURN);
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true);
        testController.showCity(TurnStateMachine.FIRST_TURN, 1, 1);
        testController.showInitialTurnState(EasyMock.anyObject(TurnStateData.class));
        testController.showResourceCards(testBoard, testBoard.playerResourcesMap(new Player(Turn.BLUE)));

        EasyMock.replay(turnStateMachine, testController);

        testBoard.onCityPointClick(1, 1);

        EasyMock.verify(testController);

        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(Turn.RED).getResource(ResourceType.WHEAT));

    }

    @Test
    public void testRobberBlocksResource(){
        GameWindowController controllerTest = EasyMock.createMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.strictMock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);

        CityPoint city = new CityPoint(1, 1);

        city.hasSettlement = true;
        city.isCity = true;
        city.owner = Turn.RED;

        testBoard.robberNumber = 8;
        testBoard.robberResource = ResourceType.ORE;

        city.setTileValues(List.of(8), List.of(Terrain.MOUNTAIN));

        testBoard.cityPoints = new ArrayList<>(List.of(city));

        EasyMock.expect(turnStateMachine.getRound()).andReturn(3);
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(false);
        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED);
        EasyMock.expect(dice.roll()).andReturn(8);

        EasyMock.replay(turnStateMachine, dice);

        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));

        testBoard.onRollDiceClick();

        EasyMock.verify(turnStateMachine, dice);
        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.ORE));
        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.WOOD));
        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.BRICK));
        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.SHEEP));
        assertEquals(0, testBoard.turnToPlayer.get(TurnStateMachine.FIRST_TURN).getResource(ResourceType.WHEAT));

    }

    @Test
    public void testHarbormasterBonus() throws IOException {
        GameWindow gameWindow = EasyMock.mock(GameWindow.class);
        GameWindowController gameWindowController = new GameWindowController(gameWindow);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board;
        try {
            board = new Board(gameWindowController, turnStateMachine, dice);
            FileInputStream cityCoordsStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
            FileInputStream cityTerrainsStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
            FileInputStream cityValuesStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
            FileInputStream harborsStream = new FileInputStream(Board.HARBORS_FILE_PATH);
            FileInputStream roadCoordsStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
            FileInputStream cityNeighborsStream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
            FileInputStream roadNeighborsStream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
            FileInputStream robberCoordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
            FileInputStream robberResourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
            FileInputStream robberNumberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);
            BoardDataInputs dataInputs = new BoardDataInputs(
                    cityCoordsStream, cityTerrainsStream, cityValuesStream,
                    harborsStream, roadCoordsStream, cityNeighborsStream,
                    roadNeighborsStream, robberCoordsStream, robberResourceStream,
                    robberNumberStream
            );
            board.loadBoardData(dataInputs);
        } catch (IOException excep) {
            throw new RuntimeException("Failed to load essential game data, application cannot start.", excep);
        }
        board.addAllCities(board.cityPoints);
        board.addAllRoadPoints(board.roadPoints);
        board.addAllRobberPoints(board.robberPoints);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3).anyTimes();
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();
        EasyMock.replay(turnStateMachine);

        board.robberMoved = true;
        assertEquals(3, turnStateMachine.getRound());
        assertEquals(Turn.RED, board.getTurn());

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 10);
        redPlayer.addResources(ResourceType.BRICK, 10);
        redPlayer.addResources(ResourceType.SHEEP, 10);
        redPlayer.addResources(ResourceType.WHEAT, 10);

        int placed = 0;
        for (CityPoint cityPoint : board.cityPoints) {
            if (!(cityPoint instanceof HarborPoint) || cityPoint.getOwner() != Turn.NONE) {
                continue;
            }
            int settlementsBefore = redPlayer.settlements;
            board.onCityPointClick(cityPoint.getX(), cityPoint.getY());
            if (redPlayer.settlements == settlementsBefore - 1) {
                placed++;
            }
            if (placed == 2) {
                break;
            }
        }
        assertEquals(2, placed);
        assertEquals(4, redPlayer.getVictoryPoints());
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testHarbormasterTransfer() throws IOException {
        GameWindow gameWindow = EasyMock.mock(GameWindow.class);
        GameWindowController gameWindowController = new GameWindowController(gameWindow);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board;
        try {
            board = new Board(gameWindowController, turnStateMachine, dice);
            FileInputStream cityCoordsStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
            FileInputStream cityTerrainsStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
            FileInputStream cityValuesStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
            FileInputStream harborsStream = new FileInputStream(Board.HARBORS_FILE_PATH);
            FileInputStream roadCoordsStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
            FileInputStream cityNeighborsStream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
            FileInputStream roadNeighborsStream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
            FileInputStream robberCoordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
            FileInputStream robberResourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
            FileInputStream robberNumberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);
            BoardDataInputs dataInputs = new BoardDataInputs(
                    cityCoordsStream, cityTerrainsStream, cityValuesStream,
                    harborsStream, roadCoordsStream, cityNeighborsStream,
                    roadNeighborsStream, robberCoordsStream, robberResourceStream,
                    robberNumberStream
            );
            board.loadBoardData(dataInputs);
        } catch (IOException excep) {
            throw new RuntimeException("Failed to load essential game data, application cannot start.", excep);
        }
        board.addAllCities(board.cityPoints);
        board.addAllRoadPoints(board.roadPoints);
        board.addAllRobberPoints(board.robberPoints);

        final Turn[] currentTurn = new Turn[]{Turn.RED};
        EasyMock.expect(turnStateMachine.getTurn()).andAnswer(() -> currentTurn[0]).anyTimes();
        EasyMock.expect(turnStateMachine.getRound()).andReturn(3).anyTimes();
        EasyMock.expect(turnStateMachine.getHasRolled()).andReturn(true).anyTimes();
        turnStateMachine.nextTurn();
        EasyMock.expectLastCall().andAnswer(() -> {
            currentTurn[0] = Turn.BLUE;
            return null;
        }).once();
        EasyMock.replay(turnStateMachine);

        board.robberMoved = true;
        assertEquals(3, turnStateMachine.getRound());
        assertEquals(Turn.RED, board.getTurn());

        Player redPlayer = board.turnToPlayer.get(Turn.RED);
        redPlayer.addResources(ResourceType.WOOD, 10);
        redPlayer.addResources(ResourceType.BRICK, 10);
        redPlayer.addResources(ResourceType.SHEEP, 10);
        redPlayer.addResources(ResourceType.WHEAT, 10);
        int redPlaced = 0;
        for (CityPoint cityPoint : board.cityPoints) {
            if (!(cityPoint instanceof HarborPoint) || cityPoint.getOwner() != Turn.NONE) {
                continue;
            }
            int settlementsBefore = redPlayer.settlements;
            board.onCityPointClick(cityPoint.getX(), cityPoint.getY());
            if (redPlayer.settlements == settlementsBefore - 1) {
                redPlaced++;
            }
            if (redPlaced == 2) {
                break;
            }
        }
        assertEquals(2, redPlaced);

        board.onNextTurnClick();
        assertEquals(Turn.BLUE, board.getTurn());

        Player bluePlayer = board.turnToPlayer.get(Turn.BLUE);
        bluePlayer.addResources(ResourceType.WOOD, 20);
        bluePlayer.addResources(ResourceType.BRICK, 20);
        bluePlayer.addResources(ResourceType.SHEEP, 20);
        bluePlayer.addResources(ResourceType.WHEAT, 20);
        int bluePlaced = 0;
        for (CityPoint cityPoint : board.cityPoints) {
            if (!(cityPoint instanceof HarborPoint) || cityPoint.getOwner() != Turn.NONE) {
                continue;
            }
            int settlementsBefore = bluePlayer.settlements;
            board.onCityPointClick(cityPoint.getX(), cityPoint.getY());
            if (bluePlayer.settlements == settlementsBefore - 1) {
                bluePlaced++;
            }
            if (bluePlaced == 3) {
                break;
            }
        }
        assertEquals(3, bluePlaced);
        assertEquals(5, bluePlayer.getVictoryPoints());
        assertEquals(2, redPlayer.getVictoryPoints());
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testFriendlyRobberWithZeroVP() {
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(null, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(8), List.of(Terrain.FOREST));
        cityPoint.placeSettlement(Turn.BLUE);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        Player bluePlayer = testBoard.turnToPlayer.get(Turn.BLUE);
        // Player has 0 VP - should be protected

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WOOD, 8);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Set<Player> eligiblePlayers = testBoard.getEligiblePlayersToRob(robberPoint);

        assertFalse(eligiblePlayers.contains(bluePlayer));
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testFriendlyRobberWithOneVP() {
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(null, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(9), List.of(Terrain.PASTURE));
        cityPoint.placeSettlement(Turn.BLUE);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        Player bluePlayer = testBoard.turnToPlayer.get(Turn.BLUE);
        bluePlayer.addVictoryPoints(1);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.SHEEP, 9);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Set<Player> eligiblePlayers = testBoard.getEligiblePlayersToRob(robberPoint);

        assertFalse(eligiblePlayers.contains(bluePlayer));
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testFriendlyRobberWith3VP() {
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(null, turnStateMachine, dice);

        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(5), List.of(Terrain.HILL));
        cityPoint.placeSettlement(Turn.BLUE);
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));

        Player bluePlayer = testBoard.turnToPlayer.get(Turn.BLUE);
        bluePlayer.addVictoryPoints(3);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.BRICK, 5);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Set<Player> eligiblePlayers = testBoard.getEligiblePlayersToRob(robberPoint);

        assertTrue(eligiblePlayers.contains(bluePlayer));
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testFriendlyRobberBlocksRobberMovement() {
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(6), List.of(Terrain.FIELD));
        cityPoint.placeSettlement(Turn.BLUE);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(null, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;

        Player bluePlayer = testBoard.turnToPlayer.get(Turn.BLUE);
        bluePlayer.addVictoryPoints(0); // 0 VP - friendly robber should protect

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        testBoard.onRobberPointClick(1, 1);

        assertFalse(robberPoint.hasRobber);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testFriendlyRobberAllowsMovementAbove2VP() {
        GameWindowController controllerTest = EasyMock.niceMock(GameWindowController.class);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        CityPoint cityPoint = new CityPoint(2, 2);
        cityPoint.setTileValues(List.of(6), List.of(Terrain.FIELD));
        cityPoint.placeSettlement(Turn.BLUE);

        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(controllerTest, turnStateMachine, dice);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));
        testBoard.cityPoints = new ArrayList<>(List.of(cityPoint));
        testBoard.numRolled = 7;
        testBoard.robberMoved = false;

        Player bluePlayer = testBoard.turnToPlayer.get(Turn.BLUE);
        bluePlayer.addVictoryPoints(2); // 2 VP - not protected

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        testBoard.onRobberPointClick(1, 1);

        assertTrue(robberPoint.hasRobber);
        assertEquals(ResourceType.WHEAT, testBoard.robberResource);
        assertEquals(6, testBoard.robberNumber);
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testMultipleSettlementsDifferentPlayersOnHex() {
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);

        Board testBoard = new Board(null, turnStateMachine, dice);

        // Setup Blue player with 0 VP (protected)
        CityPoint blueCityPoint = new CityPoint(2, 2);
        blueCityPoint.setTileValues(List.of(6), List.of(Terrain.FIELD));
        blueCityPoint.placeSettlement(Turn.BLUE);
        
        // Setup White player with 3 VP (not protected)
        CityPoint whiteCityPoint = new CityPoint(3, 3);
        whiteCityPoint.setTileValues(List.of(6), List.of(Terrain.FIELD));
        whiteCityPoint.placeSettlement(Turn.WHITE);

        testBoard.cityPoints = new ArrayList<>(List.of(blueCityPoint, whiteCityPoint));

        Player bluePlayer = testBoard.turnToPlayer.get(Turn.BLUE);
        Player whitePlayer = testBoard.turnToPlayer.get(Turn.WHITE);
        whitePlayer.addVictoryPoints(3);

        RobberPoint robberPoint = new RobberPoint(1, 1, ResourceType.WHEAT, 6);
        testBoard.robberPoints = new ArrayList<>(List.of(robberPoint));

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Set<Player> eligiblePlayers = testBoard.getEligiblePlayersToRob(robberPoint);

        assertFalse(eligiblePlayers.contains(bluePlayer));
        assertTrue(eligiblePlayers.contains(whitePlayer));
        EasyMock.verify(turnStateMachine);
    }

    @Test
    public void testFriendlyRobberLowVp() throws IOException {
        GameWindow gameWindow = EasyMock.mock(GameWindow.class);
        GameWindowController gameWindowController = new GameWindowController(gameWindow);
        TurnStateMachine turnStateMachine = EasyMock.mock(TurnStateMachine.class);
        Dice dice = EasyMock.mock(Dice.class);
        Board board;
        try {
            board = new Board(gameWindowController, turnStateMachine, dice);
            FileInputStream cityCoordsStream = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
            FileInputStream cityTerrainsStream = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
            FileInputStream cityValuesStream = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
            FileInputStream harborsStream = new FileInputStream(Board.HARBORS_FILE_PATH);
            FileInputStream roadCoordsStream = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
            FileInputStream cityNeighborsStream = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
            FileInputStream roadNeighborsStream = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
            FileInputStream robberCoordsStream = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
            FileInputStream robberResourceStream = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
            FileInputStream robberNumberStream = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH);
            BoardDataInputs dataInputs = new BoardDataInputs(
                    cityCoordsStream, cityTerrainsStream, cityValuesStream,
                    harborsStream, roadCoordsStream, cityNeighborsStream,
                    roadNeighborsStream, robberCoordsStream, robberResourceStream,
                    robberNumberStream
            );
            board.loadBoardData(dataInputs);
        } catch (IOException excep) {
            throw new RuntimeException("Failed to load essential game data, application cannot start.", excep);
        }
        board.addAllCities(board.cityPoints);
        board.addAllRoadPoints(board.roadPoints);
        board.addAllRobberPoints(board.robberPoints);

        EasyMock.expect(turnStateMachine.getTurn()).andReturn(Turn.RED).anyTimes();
        EasyMock.replay(turnStateMachine);

        Player bluePlayer = board.turnToPlayer.get(Turn.BLUE);
        CityPoint blueCity = board.cityPoints.stream()
                .filter(cp -> cp.getTerrains().stream().anyMatch(terrain -> terrain != Terrain.DESERT))
                .findFirst()
                .orElseThrow();
        blueCity.placeSettlement(Turn.BLUE);
        bluePlayer.addVictoryPoints(1);

        RobberPoint matchingRobberPoint = null;
        for (Map.Entry<Integer, Terrain> tileData : blueCity.tileValueToTerrain.entrySet()) {
            int tileValue = tileData.getKey();
            ResourceType resourceType = tileData.getValue().getResourceType();
            for (RobberPoint robberPoint : board.robberPoints) {
                if (robberPoint.diceNumber == tileValue && robberPoint.resourceType == resourceType) {
                    matchingRobberPoint = robberPoint;
                }
            }
        }
        assertNotNull(matchingRobberPoint);

        Set<Player> eligiblePlayers = board.getEligiblePlayersToRob(matchingRobberPoint);

        assertFalse(eligiblePlayers.contains(bluePlayer));
        EasyMock.verify(turnStateMachine);
    }
}
