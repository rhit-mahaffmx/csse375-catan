package com.catan.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Random;

import com.catan.datasource.BoardDataInputs;

/**
 * Headless game harness that runs a full Catan game without JavaFX.
 *
 * This replaces the GUI with a {@link RecordingGameController} and loads
 * the real board data (cities, roads, terrains, harbors, robber hexes)
 * from disk — exactly like MainApp does — so characterization tests
 * exercise the real game flow end-to-end.
 *
 * Usage:
 * <pre>
 *   GameTestHarness game = GameTestHarness.newStandardGame();
 *   game.placeSettlement(391, 320);   // Round 1, RED
 *   game.placeRoad(421, 338);
 *   game.nextTurn();                  // advance to BLUE
 *   ...
 *   game.rollDice();                  // round 3+
 *   game.assertPlayerResources(Turn.RED, ResourceType.BRICK, 1);
 * </pre>
 */
public class GameTestHarness {

    public final RecordingGameController controller;
    public final TurnStateMachine turnStateMachine;
    public final NumberCardDeck deck;
    public final Board board;

    private GameTestHarness(RecordingGameController controller,
                            TurnStateMachine turnStateMachine,
                            NumberCardDeck deck,
                            Board board) {
        this.controller = controller;
        this.turnStateMachine = turnStateMachine;
        this.deck = deck;
        this.board = board;
    }

    /**
     * Create a fully-initialized game with real board data and a seeded
     * random for deterministic NumberCardDeck shuffling.
     */
    public static GameTestHarness newStandardGame(long seed) {
        RecordingGameController controller = new RecordingGameController();
        TurnStateMachine tsm = new TurnStateMachine();
        NumberCardDeck deck = new NumberCardDeck(new Random(seed));
        Board board = new Board(controller, tsm, deck);

        try {
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot load board data files — run tests from project root", e);
        }

        return new GameTestHarness(controller, tsm, deck, board);
    }

    /** Convenience overload with default seed 42. */
    public static GameTestHarness newStandardGame() {
        return newStandardGame(42L);
    }

    // =========== Game actions — mirrors what GUI button clicks do ===========

    /** Simulate clicking a city/settlement point on the board. */
    public void placeSettlement(int x, int y) {
        board.onCityPointClick(x, y);
    }

    /** Simulate clicking a road point on the board. */
    public void placeRoad(int x, int y) {
        board.onRoadPointClick(x, y);
    }

    /** Simulate clicking "Next Turn". */
    public void nextTurn() {
        board.onNextTurnClick();
    }

    /** Simulate clicking "Roll Dice" — uses the seeded NumberCardDeck. */
    public void rollDice() {
        board.onRollDiceClick();
    }

    /** Simulate clicking "Buy Dev Card" — uses the Board's random. */
    public void buyDevCard() {
        board.onBuyDevCardClick();
    }

    /** Simulate submitting a player-to-player trade. */
    public void trade(Turn from, HashMap<ResourceType, Integer> give,
                      Turn to, HashMap<ResourceType, Integer> receive) {
        TradeInfo offer1 = new TradeInfo();
        offer1.setPlayer(from);
        for (var entry : give.entrySet()) {
            offer1.setResources(entry.getKey(), entry.getValue());
        }
        TradeInfo offer2 = new TradeInfo();
        offer2.setPlayer(to);
        for (var entry : receive.entrySet()) {
            offer2.setResources(entry.getKey(), entry.getValue());
        }
        board.onTradeSubmitClick(offer1, offer2);
    }

    /** Simulate a bank trade (4:1, 3:1, or 2:1 depending on harbors). */
    public void bankTrade(HashMap<ResourceType, Integer> give,
                          HashMap<ResourceType, Integer> receive) {
        TradeInfo playerTrade = new TradeInfo();
        for (var entry : give.entrySet()) {
            playerTrade.setResources(entry.getKey(), entry.getValue());
        }
        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        for (var entry : receive.entrySet()) {
            bankTrade.setResources(entry.getKey(), entry.getValue());
        }
        board.onBankSubmitClick(playerTrade, bankTrade);
    }

    /** Simulate robber placement click. */
    public void moveRobber(int x, int y) {
        board.onRobberPointClick(x, y);
    }

    /** Simulate discarding for all players who need to. */
    public void submitDiscards(HashMap<Turn, HashMap<ResourceType, Integer>> allDiscards) {
        board.onSubmitDiscard(allDiscards);
    }

    /** Redeem fish tokens for a Fishermen benefit. */
    public boolean redeemFish(Turn turn, FishRedemptionType type) {
        return board.redeemFishTokens(turn, type);
    }

    /** Pass the old shoe from one player to another. */
    public void passOldShoe(Turn from, Turn to) {
        board.passOldShoe(from, to);
    }

    /** Use a Knight dev card. */
    public void playKnight() {
        board.onKnightCardClick();
    }

    /** Use a Year of Plenty dev card. */
    public void playYearOfPlenty() {
        board.onYoPClick();
    }

    /** Use a Road Building dev card. */
    public void playRoadBuilding() {
        board.onRoadBuildingClick();
    }

    /** Use a Monopoly dev card. */
    public void playMonopoly() {
        board.onMonopolyClick();
    }

    // ============ Query helpers — inspect game state without GUI ============

    public Player getPlayer(Turn turn) {
        return board.turnToPlayer.get(turn);
    }

    public Turn currentTurn() {
        return turnStateMachine.getTurn();
    }

    public int currentRound() {
        return turnStateMachine.getRound();
    }

    public int getResource(Turn turn, ResourceType type) {
        return board.turnToPlayer.get(turn).getResource(type);
    }

    public int getVictoryPoints(Turn turn) {
        return board.turnToPlayer.get(turn).getVictoryPoints();
    }

    public int getTotalFish(Turn turn) {
        return board.turnToPlayer.get(turn).getTotalFish();
    }

    public boolean hasOldShoe(Turn turn) {
        return board.turnToPlayer.get(turn).hasOldShoe();
    }

    public int getSettlementsRemaining(Turn turn) {
        return board.turnToPlayer.get(turn).settlements;
    }

    public int getRoadsRemaining(Turn turn) {
        return board.turnToPlayer.get(turn).roads;
    }

    public int getLongestRoad(Turn turn) {
        return board.longestRoad.getOrDefault(turn, 0);
    }

    public boolean isRobberMoved() {
        return board.robberMoved;
    }

    public int getLastDiceRoll() {
        return board.numRolled;
    }

    /** Get the last TurnStateData the controller recorded. */
    public TurnStateData getLastTurnState() {
        if (controller.turnStates.isEmpty()) return null;
        return controller.turnStates.get(controller.turnStates.size() - 1);
    }

    /** Seed the Board's internal random for deterministic dev card buys / earthquake. */
    public void seedBoardRandom(long seed) {
        board.rand = new Random(seed);
    }

    /** Inject a FishTokenSupplier for controlled fish tests. */
    public void setFishTokenSupplier(FishTokenSupplier supplier) {
        board.fishTokenSupplier = supplier;
    }

    /** Give a player resources directly (for setting up test scenarios). */
    public void giveResources(Turn turn, ResourceType type, int amount) {
        board.turnToPlayer.get(turn).addResources(type, amount);
    }

    /** Give a player fish tokens directly. */
    public void giveFishTokens(Turn turn, int amount) {
        board.turnToPlayer.get(turn).addFishTokens(amount);
    }
}
