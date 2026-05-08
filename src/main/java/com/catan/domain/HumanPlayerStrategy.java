package com.catan.domain;

import java.util.ArrayList;

/**
 * Human player strategy — delegates all decisions to GUI interaction.
 * This is a no-op implementation: all methods return null/false because
 * human decisions come through Board's onClick handlers from the GUI.
 */
public class HumanPlayerStrategy implements PlayerStrategy {

    @Override
    public CityPoint chooseSettlementLocation(Board board, Player player) {
        return null; // Human clicks on the board
    }

    @Override
    public RoadPoint chooseRoadLocation(Board board, Player player) {
        return null; // Human clicks on the board
    }

    @Override
    public RobberPoint chooseRobberPlacement(Board board, Player player) {
        return null; // Human clicks on the board
    }

    @Override
    public Turn choosePlayerToRob(Board board, Player player, ArrayList<Player> eligible) {
        return null; // Human selects from dialog
    }

    @Override
    public boolean shouldBuyDevCard(Board board, Player player) {
        return false; // Human clicks buy button
    }

    @Override
    public CityPoint chooseCityUpgrade(Board board, Player player) {
        return null; // Human clicks on existing settlement
    }

    @Override
    public boolean isAI() {
        return false;
    }
}
