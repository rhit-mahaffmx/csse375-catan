package com.catan.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.catan.presentation.GameWindow;

public class GameWindowController {
    private final GameWindow gameWindow;

    public GameWindowController(GameWindow givenGameWindow) {
        this.gameWindow = givenGameWindow;
    }

    public void placeCityButton(Board board, int x, int y){
        gameWindow.addCityButton(board, x, y);
    }
    public void placeRoadButton(Board board, int x, int y){
        gameWindow.addRoadButton(board, x, y);
    }

    public void startGame(){
        gameWindow.run();
    }

    public void showInitialTurnState(TurnStateData turnData) {
        gameWindow.showInitialTurnState(turnData);
    }

    public void showSettlement(Turn turn, int x, int y){
        gameWindow.showSettlement(turn, x, y);
    }

    public void showCity(Turn turn, int x, int y) {
        gameWindow.showCity(turn, x, y);
    }

    public void addNextTurnButton(Board board) {
        this.gameWindow.addNextTurnButton(board);
    }

    public void showRoad(Turn turn, int x, int y) {
        gameWindow.showRoad(turn, x, y);
    }

    public void addDiceRollButton(Board board) { this.gameWindow.addDiceRollButton(board);
    }

    public void showDiceRoll(int diceRoll) {
        gameWindow.showDiceRoll(diceRoll);
    }

    public void showInitialRobberState(int x, int y) {
        gameWindow.showInitialRobberState(x, y);
    }

    public void placeRobberButton(Board testBoard, int x, int y) {
        gameWindow.addRobberButton(testBoard, x, y);
    }

    public void placeDevCardButton(Board board){
        gameWindow.addDevCardButton(board);
    }
    public void showDevCards(Board board, ArrayList<DevelopmentCard> devCards){ gameWindow.showDevCard(board, devCards); }

    public void clearDevCards(){
        gameWindow.clearDevText();
        gameWindow.clearDevImages();
        gameWindow.clearDevButtons();
    }

    public void showResourceCards(Board board, HashMap<ResourceType, Integer> resourceMap) {
        gameWindow.showResourceCards(resourceMap);
    }

    public void showInvalidInputAndPass(String message) {
        gameWindow.showInvalidInputAndPass(message);
    }

    public void gameOver(Player player){
        gameWindow.gameOver(player);
    }

    public void showTradeDialogue(Board board) {gameWindow.addTradeDialogue(board);}

    public void showDiscardDialog(Board board) {
        gameWindow.addDiscardDialogue(board);
    }

    public void hideDiscardDialog() {
        gameWindow.removeDiscardDialog();
    }

    public void showStealDialog(Board board, Set<Player> players) {
        gameWindow.showStealDialog(board, players);
    }

    public void removeStealDialog() {
        gameWindow.removeStealDialog();
    }

    public void openYoPTradeMenu(Board board, Turn player){ gameWindow.openYoPDialogue(board, player); }

    public void openMonopolyMenu(Board board, Turn player) {
        gameWindow.openMonopolyDialogue(board,player);
    }

    public void showEventText(String message) {
        gameWindow.showEventText(message);
    }
}
