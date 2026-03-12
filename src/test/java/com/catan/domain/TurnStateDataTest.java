package com.catan.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TurnStateDataTest {

    @Test
    public void testCreateTurnStateDataFirstTurn() {
        TurnStateData data = new TurnStateData(TurnStateMachine.FIRST_TURN, Board.INITIAL_SETTLEMENTS, Board.INITIAL_ROADS, Board.INITIAL_VICTORY_POINTS);

        assertEquals(TurnStateMachine.FIRST_TURN, data.turn);
        assertEquals(Board.INITIAL_SETTLEMENTS, data.settlements);
        assertEquals(Board.INITIAL_ROADS, data.roads);
        assertEquals(Board.INITIAL_VICTORY_POINTS, data.victoryPoints);
    }
}
