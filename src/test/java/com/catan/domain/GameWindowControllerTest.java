package com.catan.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import com.catan.presentation.GameWindow;

import java.util.*;

public class GameWindowControllerTest {

    @Test
    public void testStartGame() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        testWindow.run();
        EasyMock.replay(testWindow);
        GameWindowController testController = new GameWindowController(testWindow);
        testController.startGame();
        EasyMock.verify(testWindow);
    }

    @Test
    public void addRoadButton(){
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        Board testBoard = EasyMock.createMock(Board.class);
        testWindow.addRoadButton(testBoard, 1, 1);
        EasyMock.replay(testWindow);
        GameWindowController testController = new GameWindowController(testWindow);
        testController.placeRoadButton(testBoard, 1, 1);
        EasyMock.verify(testWindow);
    }

    @Test
    public void placeRobberButton() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        Board testBoard = EasyMock.createMock(Board.class);

        testWindow.addRobberButton(testBoard, 1, 1);

        EasyMock.replay(testWindow);
        GameWindowController testController = new GameWindowController(testWindow);
        testController.placeRobberButton(testBoard, 1, 1);
        EasyMock.verify(testWindow);
    }

    @Test
    public void addButtonsAtLocation(){
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        Board testBoard = EasyMock.createMock(Board.class);
        testWindow.addCityButton(testBoard, 1, 1);
        testWindow.addCityButton(testBoard, 5, 5);
        EasyMock.replay(testWindow);
        GameWindowController testController = new GameWindowController(testWindow);
        testController.placeCityButton(testBoard, 1, 1);
        testController.placeCityButton(testBoard, 5, 5);
        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowInitialTurnState() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);
        TurnStateData turnStateData = EasyMock.createMock(TurnStateData.class);

        testWindow.showInitialTurnState(turnStateData);
        EasyMock.replay(testWindow);

        testController.showInitialTurnState(turnStateData);
        EasyMock.verify(testWindow);

    }

    @Test
    public void testShowNextTurnState() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);
        TurnStateData turnStateData = EasyMock.createMock(TurnStateData.class);

        testWindow.showInitialTurnState(turnStateData);
        EasyMock.replay(testWindow);

        testController.showInitialTurnState(turnStateData);
        EasyMock.verify(testWindow);
    }

    @Test
    public void testAddNextTurnButton() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        Board testBoard = EasyMock.createMock(Board.class);

        testWindow.addNextTurnButton(testBoard);
        EasyMock.replay(testWindow);

        testController.addNextTurnButton(testBoard);

        EasyMock.verify(testWindow);
    }


    @Test
    public void testShowSettlement() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        testWindow.showSettlement(TurnStateMachine.FIRST_TURN, 1, 1);

        EasyMock.replay(testWindow);

        testController.showSettlement(TurnStateMachine.FIRST_TURN, 1, 1);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowCity() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        testWindow.showCity(TurnStateMachine.FIRST_TURN, 1, 1);

        EasyMock.replay(testWindow);

        testController.showCity(TurnStateMachine.FIRST_TURN, 1, 1);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowRoad() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        testWindow.showRoad(TurnStateMachine.FIRST_TURN, 1, 1);

        EasyMock.replay(testWindow);

        testController.showRoad(TurnStateMachine.FIRST_TURN, 1, 1);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testAddBuyDevCardButton() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        Board testBoard = EasyMock.createMock(Board.class);

        testWindow.addDevCardButton(testBoard);
        EasyMock.replay(testWindow);

        testController.placeDevCardButton(testBoard);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowDevCard(){
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);
        DevelopmentCard testDevCard = EasyMock.createMock(DevelopmentCard.class);
        ArrayList<DevelopmentCard> devCards = new ArrayList<>(Arrays.asList(testDevCard));
        Board testBoard = EasyMock.createMock(Board.class);
        testWindow.showDevCard(testBoard, devCards);

        EasyMock.replay(testWindow);

        testController.showDevCards(testBoard, devCards);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testRemoveOldDevCards(){
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        testWindow.clearDevImages();
        testWindow.clearDevText();
        testWindow.clearDevButtons();

        EasyMock.replay(testWindow);

        testController.clearDevCards();

        EasyMock.verify(testWindow);
    }
    @Test
    public void testAddDiceRollButton() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        Board testBoard = EasyMock.createMock(Board.class);

        testWindow.addDiceRollButton(testBoard);
        EasyMock.replay(testWindow);

        testController.addDiceRollButton(testBoard);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowDiceRoll() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        testWindow.showDiceRoll(10);
        EasyMock.replay(testWindow);

        testController.showDiceRoll(10);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowInitialRobberState() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);

        testWindow.showInitialRobberState(220, 504);

        EasyMock.replay(testWindow);

        testController.showInitialRobberState(220, 504);

        EasyMock.verify(testWindow);

    }

    @Test
    public void testShowResources() {
    GameWindow testWindow = EasyMock.createMock(GameWindow.class);
    GameWindowController testController = new GameWindowController(testWindow);
    Board testBoard = EasyMock.createMock(Board.class);

    HashMap<ResourceType, Integer> expectedResources =new HashMap<>();

    expectedResources.put(ResourceType.WHEAT, 1);
    expectedResources.put(ResourceType.SHEEP, 1);
    expectedResources.put(ResourceType.ORE, 1);

    testWindow.showResourceCards(EasyMock.anyObject());
    EasyMock.replay(testWindow);

    testController.showResourceCards(testBoard, expectedResources);
    EasyMock.verify(testWindow);
    }

    @Test
    public void testShowInvalidInputAndPass() {
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);

        GameWindowController testController = new GameWindowController(testWindow);

        testWindow.showInvalidInputAndPass("Message");

        EasyMock.replay(testWindow);

        testController.showInvalidInputAndPass("Message");

        EasyMock.verify(testWindow);
    }


    @Test
    public void testAddTradeDialogue(){
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);
        Board testBoard = EasyMock.createMock(Board.class);
        testWindow.addTradeDialogue(testBoard);

        EasyMock.replay(testWindow);

        testController.showTradeDialogue(testBoard);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testGameOver(){
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);
        Player testPlayer = EasyMock.createMock(Player.class);
        testWindow.gameOver(testPlayer);

        EasyMock.replay(testWindow);

        testController.gameOver(testPlayer);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowDiscardDialogue(){
        GameWindow testWindow = EasyMock.createMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);
        Board testBoard = EasyMock.createMock(Board.class);
        testWindow.addDiscardDialogue(testBoard);

        EasyMock.replay(testWindow);

        testController.showDiscardDialog(testBoard);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testHideDiscardDialog() {
        GameWindow testWindow = EasyMock.strictMock(GameWindow.class);

        testWindow.removeDiscardDialog();

        EasyMock.replay(testWindow);
        GameWindowController controller = new GameWindowController(testWindow);
        controller.hideDiscardDialog();
        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowStealDialog() {
        GameWindow testWindow = EasyMock.strictMock(GameWindow.class);
        GameWindowController controller = new GameWindowController(testWindow);
        Board board = EasyMock.mock(Board.class);

        Set<Player> players = new HashSet<>();
        players.add(new Player(Turn.RED));

        testWindow.showStealDialog(board, players);
        EasyMock.replay(testWindow);

        controller.showStealDialog(board, players);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testRemoveStealDialog() {
        GameWindow testWindow = EasyMock.strictMock(GameWindow.class);
        GameWindowController controller = new GameWindowController(testWindow);

        testWindow.removeStealDialog();
        EasyMock.replay(testWindow);

        controller.removeStealDialog();

        EasyMock.verify(testWindow);
    }

    @Test
    public void testShowYoPDialogue() {
        GameWindow testWindow = EasyMock.strictMock(GameWindow.class);
        GameWindowController controller = new GameWindowController(testWindow);
        Board testBoard = EasyMock.mock(Board.class);

        testWindow.openYoPDialogue(testBoard, Turn.RED);
        EasyMock.replay(testWindow);

        controller.openYoPTradeMenu(testBoard, Turn.RED);

        EasyMock.verify(testWindow);
    }

    @Test
    public void testOpenMonopolyMenu() {
        GameWindow testWindow = EasyMock.strictMock(GameWindow.class);
        GameWindowController testController = new GameWindowController(testWindow);
        Board testBoard = EasyMock.mock(Board.class);

        testWindow.openMonopolyDialogue(testBoard, Turn.ORANGE);

        EasyMock.replay(testWindow, testBoard);

        testController.openMonopolyMenu(testBoard, Turn.ORANGE);

        EasyMock.verify(testWindow);
    }

}
