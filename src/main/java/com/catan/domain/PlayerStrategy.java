package com.catan.domain;

import java.util.ArrayList;

/**
 * Seam interface (Michael Feathers pattern) that abstracts player decision-making.
 *
 * This interface creates a clean separation point between the game engine (Board)
 * and the source of player decisions. A HumanPlayerStrategy delegates to GUI clicks,
 * while an AIPlayerStrategy makes autonomous decisions based on board state.
 *
 * By injecting this interface into Board via a HashMap<Turn, PlayerStrategy>,
 * we avoid modifying existing Board methods — the AI can simply call the same
 * public methods (onCityPointClick, onRoadPointClick, etc.) that the GUI calls.
 */
public interface PlayerStrategy {

    /**
     * Choose where to place a settlement during setup (rounds 1-2) or normal play.
     * Returns the chosen CityPoint, or null if no valid placement exists.
     */
    CityPoint chooseSettlementLocation(Board board, Player player);

    /**
     * Choose where to place a road during setup or normal play.
     * Returns the chosen RoadPoint, or null if no valid placement exists.
     */
    RoadPoint chooseRoadLocation(Board board, Player player);

    /**
     * Choose where to move the robber when a 7 is rolled or knight is played.
     * Returns the chosen RobberPoint.
     */
    RobberPoint chooseRobberPlacement(Board board, Player player);

    /**
     * Choose which player to steal from after placing the robber.
     * Returns the Turn of the chosen victim.
     */
    Turn choosePlayerToRob(Board board, Player player, ArrayList<Player> eligible);

    /**
     * Decide whether to buy a development card this turn.
     */
    boolean shouldBuyDevCard(Board board, Player player);

    /**
     * Decide whether to upgrade a settlement to a city this turn.
     * Returns the CityPoint to upgrade, or null if no upgrade desired.
     */
    CityPoint chooseCityUpgrade(Board board, Player player);

    /**
     * Returns true if this is an AI-controlled strategy (not human).
     */
    boolean isAI();
}
