package com.catan.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A GUI-less replacement for GameWindowController that records every
 * call the Board makes to the presentation layer. Instead of rendering
 * JavaFX widgets, it logs each action into inspectable lists.
 *
 * Use this as the seam that scripts out the GUI in test harnesses.
 */
public class RecordingGameController extends GameWindowController {

    // ---- Recorded events ----
    public final List<String> log = new ArrayList<>();
    public final List<SettlementEvent> settlements = new ArrayList<>();
    public final List<CityEvent> cities = new ArrayList<>();
    public final List<RoadEvent> roads = new ArrayList<>();
    public final List<TurnStateData> turnStates = new ArrayList<>();
    public final List<Integer> diceRolls = new ArrayList<>();
    public final List<String> eventTexts = new ArrayList<>();
    public final List<HashMap<ResourceType, Integer>> resourceSnapshots = new ArrayList<>();
    public final List<ArrayList<DevelopmentCard>> devCardSnapshots = new ArrayList<>();
    public final List<String> invalidMessages = new ArrayList<>();
    public Player winner = null;
    public boolean gameStarted = false;
    public int discardDialogsShown = 0;
    public int stealDialogsShown = 0;

    public record SettlementEvent(Turn turn, int x, int y) {}
    public record CityEvent(Turn turn, int x, int y) {}
    public record RoadEvent(Turn turn, int x, int y) {}

    /**
     * Construct with a null GameWindow — we never touch the real GUI.
     */
    public RecordingGameController() {
        super(null);
    }

    // ---- Overrides that record instead of rendering ----

    @Override
    public void startGame() {
        gameStarted = true;
        log.add("startGame");
    }

    @Override
    public void placeCityButton(Board board, int x, int y) {
        log.add("placeCityButton(" + x + "," + y + ")");
    }

    @Override
    public void placeRoadButton(Board board, int x, int y) {
        log.add("placeRoadButton(" + x + "," + y + ")");
    }

    @Override
    public void showSettlement(Turn turn, int x, int y) {
        settlements.add(new SettlementEvent(turn, x, y));
        log.add("showSettlement(" + turn + "," + x + "," + y + ")");
    }

    @Override
    public void showCity(Turn turn, int x, int y) {
        cities.add(new CityEvent(turn, x, y));
        log.add("showCity(" + turn + "," + x + "," + y + ")");
    }

    @Override
    public void showRoad(Turn turn, int x, int y) {
        roads.add(new RoadEvent(turn, x, y));
        log.add("showRoad(" + turn + "," + x + "," + y + ")");
    }

    @Override
    public void showInitialTurnState(TurnStateData turnData) {
        turnStates.add(turnData);
        log.add("showTurnState(" + turnData.turn + ",s=" + turnData.settlements
                + ",r=" + turnData.roads + ",vp=" + turnData.victoryPoints + ")");
    }

    @Override
    public void addNextTurnButton(Board board) {
        log.add("addNextTurnButton");
    }

    @Override
    public void addDiceRollButton(Board board) {
        log.add("addDiceRollButton");
    }

    @Override
    public void showDiceRoll(int diceRoll) {
        diceRolls.add(diceRoll);
        log.add("diceRoll(" + diceRoll + ")");
    }

    @Override
    public void showEventText(String message) {
        eventTexts.add(message);
        log.add("event(" + message + ")");
    }

    @Override
    public void showInitialRobberState(int x, int y) {
        log.add("robberState(" + x + "," + y + ")");
    }

    @Override
    public void placeRobberButton(Board board, int x, int y) {
        log.add("placeRobberButton(" + x + "," + y + ")");
    }

    @Override
    public void placeDevCardButton(Board board) {
        log.add("placeDevCardButton");
    }

    @Override
    public void showDevCards(Board board, ArrayList<DevelopmentCard> devCards) {
        devCardSnapshots.add(new ArrayList<>(devCards));
        log.add("showDevCards(" + devCards.size() + ")");
    }

    @Override
    public void clearDevCards() {
        log.add("clearDevCards");
    }

    @Override
    public void showResourceCards(Board board, HashMap<ResourceType, Integer> resourceMap) {
        resourceSnapshots.add(new HashMap<>(resourceMap));
        log.add("showResources(" + resourceMap + ")");
    }

    @Override
    public void showInvalidInputAndPass(String message) {
        invalidMessages.add(message);
        log.add("invalid(" + message + ")");
    }

    @Override
    public void gameOver(Player player) {
        winner = player;
        log.add("gameOver(" + player.color + ")");
    }

    @Override
    public void showTradeDialogue(Board board) {
        log.add("showTradeDialogue");
    }

    @Override
    public void showDiscardDialog(Board board) {
        discardDialogsShown++;
        log.add("showDiscardDialog");
    }

    @Override
    public void hideDiscardDialog() {
        log.add("hideDiscardDialog");
    }

    @Override
    public void showStealDialog(Board board, Set<Player> players) {
        stealDialogsShown++;
        log.add("showStealDialog(" + players.size() + " players)");
    }

    @Override
    public void removeStealDialog() {
        log.add("removeStealDialog");
    }

    @Override
    public void openYoPTradeMenu(Board board, Turn player) {
        log.add("openYoPTradeMenu(" + player + ")");
    }

    @Override
    public void openMonopolyMenu(Board board, Turn player) {
        log.add("openMonopolyMenu(" + player + ")");
    }

    /** Clear all recorded state for reuse between test phases. */
    public void clearLog() {
        log.clear();
        settlements.clear();
        cities.clear();
        roads.clear();
        turnStates.clear();
        diceRolls.clear();
        eventTexts.clear();
        resourceSnapshots.clear();
        devCardSnapshots.clear();
        invalidMessages.clear();
        winner = null;
        discardDialogsShown = 0;
        stealDialogsShown = 0;
    }
}
