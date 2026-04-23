package com.catan.presentation;

import java.io.FileInputStream;
import java.util.Locale;
import java.util.Random;

import com.catan.datasource.BoardDataInputs;
import com.catan.domain.Board;
import com.catan.domain.GameWindowController;
import com.catan.domain.NumberCardDeck;
import com.catan.domain.Turn;
import com.catan.domain.TurnStateMachine;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * GUI Test Harness that scripts a visual game playthrough through the real Catan GUI.
 *
 * Boots the actual GameWindow and executes scripted game moves via the Board domain layer.
 * Each action renders on the real game window so you can watch the game play out visually.
 *
 * Run this class directly as a Java application (it extends Application).
 * The harness skips the language-selection screen and goes straight into a scripted game.
 *
 * To adjust playback speed, change MOVE_DELAY_MS.
 * To change dice outcomes, change the Random seed in RANDOM_SEED.
 */
public class CatanGUITestHarness extends Application {

    private static final long MOVE_DELAY_MS = 1000;
    private static final long RANDOM_SEED = 42;

    private Board board;
    private TurnStateMachine turnStateMachine;

    // ---- City coordinates (index → x,y from cityCoords file) ----
    // Round 1 settlements (widely separated across the board)
    private static final int[] RED_R1_CITY    = {275, 113};  // City 0  — top-left
    private static final int[] BLUE_R1_CITY   = {631, 251};  // City 15 — right
    private static final int[] ORANGE_R1_CITY = {92, 423};   // City 27 — far-left
    private static final int[] WHITE_R1_CITY  = {520, 664};  // City 53 — bottom

    // Round 2 settlements (reverse order, also widely separated)
    private static final int[] WHITE_R2_CITY  = {630, 527};  // City 42 — right-mid
    private static final int[] ORANGE_R2_CITY = {213, 630};  // City 47 — bottom-left
    private static final int[] BLUE_R2_CITY   = {333, 215};  // City 8  — top-center
    private static final int[] RED_R2_CITY    = {451, 423};  // City 30 — center

    // ---- Road coordinates adjacent to the settlements above ----
    // Each road's endpoints (from roadNeighbors) include the settlement city.
    private static final int[] RED_R1_ROAD    = {302, 132};  // Road 1  — connects City 0+4
    private static final int[] BLUE_R1_ROAD   = {630, 288};  // Road 22 — connects City 15+20
    private static final int[] ORANGE_R1_ROAD = {94, 390};   // Road 33 — connects City 21+27
    private static final int[] WHITE_R1_ROAD  = {488, 648};  // Road 70 — connects City 49+53

    private static final int[] WHITE_R2_ROAD  = {605, 544};  // Road 61 — connects City 42+46
    private static final int[] ORANGE_R2_ROAD = {212, 598};  // Road 62 — connects City 43+47
    private static final int[] BLUE_R2_ROAD   = {331, 183};  // Road 7  — connects City 4+8
    private static final int[] RED_R2_ROAD    = {451, 390};  // Road 36 — connects City 24+30

    // Robber relocation target for when a 7 is rolled (desert-ish area, center of board)
    private static final int[] ROBBER_TARGET  = {390, 390};  // Robber coord index 9

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Internationalization i18n = new Internationalization();
        i18n.setLocale(new Locale("en", "US"));

        GameWindow gameWindow = new GameWindow(i18n);
        GameWindowController gwc = new GameWindowController(gameWindow);
        turnStateMachine = new TurnStateMachine();
        NumberCardDeck deck = new NumberCardDeck(new Random(RANDOM_SEED));

        board = new Board(gwc, turnStateMachine, deck);

        BoardDataInputs dataInputs = new BoardDataInputs(
                new FileInputStream(Board.CITY_COORDINATES_FILE_PATH),
                new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH),
                new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH),
                new FileInputStream(Board.HARBORS_FILE_PATH),
                new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH),
                new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH),
                new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH),
                new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH),
                new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH),
                new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH)
        );
        board.loadBoardData(dataInputs);

        board.addAllCities(board.cityPoints);
        board.addAllRoadPoints(board.roadPoints);
        board.addAllRobberPoints(board.robberPoints);
        board.addNextTurnButton();
        board.addDiceRollButton();
        board.addBuyDevCardButton();
        board.showInitialTurnState();
        board.showInitialRobberState();
        board.showResources();
        board.showDevCards();
        board.showTradeDialogue();
        board.showDiscardDialog();
        board.startGame();

        log("=== Catan GUI Test Harness — Scripted Playthrough ===");
        log("Move delay: " + MOVE_DELAY_MS + "ms | Random seed: " + RANDOM_SEED);

        Thread scriptThread = new Thread(() -> {
            try {
                scriptGamePlaythrough();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log("Script interrupted.");
            }
        });
        scriptThread.setDaemon(true);
        scriptThread.start();
    }

    // ======================== Scripted Playthrough ========================

    private void scriptGamePlaythrough() throws InterruptedException {
        // ---- ROUND 1: Forward (RED → BLUE → ORANGE → WHITE) ----
        placeInitial("RED",    RED_R1_CITY,    RED_R1_ROAD,    1);
        placeInitial("BLUE",   BLUE_R1_CITY,   BLUE_R1_ROAD,   1);
        placeInitial("ORANGE", ORANGE_R1_CITY, ORANGE_R1_ROAD, 1);
        placeInitial("WHITE",  WHITE_R1_CITY,  WHITE_R1_ROAD,  1);

        // ---- ROUND 2: Reverse (WHITE → ORANGE → BLUE → RED) ----
        placeInitial("WHITE",  WHITE_R2_CITY,  WHITE_R2_ROAD,  2);
        placeInitial("ORANGE", ORANGE_R2_CITY, ORANGE_R2_ROAD, 2);
        placeInitial("BLUE",   BLUE_R2_CITY,   BLUE_R2_ROAD,   2);
        placeInitial("RED",    RED_R2_CITY,    RED_R2_ROAD,    2);

        // ---- ROUNDS 3+: Main game loop — several turns of dice rolling ----
        int turnsToPlay = 12; // 3 full rotations through all 4 players
        for (int i = 0; i < turnsToPlay; i++) {
            playMainTurn(i + 1);
        }

        log("=== Scripted playthrough complete. Window remains open for inspection. ===");
    }

    private void placeInitial(String color, int[] city, int[] road, int round) throws InterruptedException {
        log("[Round " + round + "] " + color + " placing settlement at (" + city[0] + "," + city[1] + ")");
        doMove(() -> board.onCityPointClick(city[0], city[1]));

        log("[Round " + round + "] " + color + " placing road at (" + road[0] + "," + road[1] + ")");
        doMove(() -> board.onRoadPointClick(road[0], road[1]));

        log("[Round " + round + "] " + color + " ending turn");
        doMove(() -> board.onNextTurnClick());
    }

    private void playMainTurn(int turnNumber) throws InterruptedException {
        Turn currentTurn = turnStateMachine.getTurn();
        log("[Main Turn " + turnNumber + "] " + currentTurn + " rolling dice...");

        doMove(() -> board.onRollDiceClick());

        int rolled = board.numRolled;
        log("[Main Turn " + turnNumber + "] " + currentTurn + " rolled " + rolled);

        if (rolled == 7) {
            // Must move the robber before anything else
            log("[Main Turn " + turnNumber + "] 7 rolled — moving robber to (" +
                    ROBBER_TARGET[0] + "," + ROBBER_TARGET[1] + ")");
            doMove(() -> board.onRobberPointClick(ROBBER_TARGET[0], ROBBER_TARGET[1]));
        }

        // In a full script you could add settlement/road buying, trading, dev cards here.
        // For now, just advance to next turn after rolling.
        log("[Main Turn " + turnNumber + "] " + currentTurn + " ending turn");
        doMove(() -> board.onNextTurnClick());
    }

    // ======================== Helpers ========================

    private void doMove(Runnable action) throws InterruptedException {
        Platform.runLater(action);
        Thread.sleep(MOVE_DELAY_MS);
    }

    private static void log(String message) {
        System.out.println("[Harness] " + message);
    }
}
