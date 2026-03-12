package com.catan.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TurnStateMachineTest {

    @Test
    public void testGetFirstTurnColor() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        assertEquals(Turn.RED, turnStateMachine.getTurn());
    }

    @Test
    public void testNextTurn() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        assertEquals(Turn.RED, turnStateMachine.getTurn());
        turnStateMachine.nextTurn();

        assertEquals(Turn.BLUE, turnStateMachine.getTurn());
    }

    @Test
    public void testMultipleTurns() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        assertEquals(Turn.RED, turnStateMachine.getTurn());
        turnStateMachine.nextTurn();

        assertEquals(Turn.BLUE, turnStateMachine.getTurn());

        turnStateMachine.nextTurn();
        assertEquals(Turn.ORANGE, turnStateMachine.getTurn());

        turnStateMachine.nextTurn();
        assertEquals(Turn.WHITE, turnStateMachine.getTurn());
    }

    @Test
    public void testOverflowTurns() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        assertEquals(Turn.RED, turnStateMachine.getTurn());
        turnStateMachine.nextTurn();

        assertEquals(Turn.BLUE, turnStateMachine.getTurn());

        turnStateMachine.nextTurn();
        assertEquals(Turn.ORANGE, turnStateMachine.getTurn());

        turnStateMachine.nextTurn();
        assertEquals(Turn.WHITE, turnStateMachine.getTurn());

        turnStateMachine.nextTurn();
        assertEquals(Turn.WHITE, turnStateMachine.getTurn());

    }

    @Test
    public void testGetRound(){
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        assertEquals(1, turnStateMachine.getRound());

        turnStateMachine.nextTurn(); //Red to Blue
        turnStateMachine.nextTurn(); //Blue to Orange
        turnStateMachine.nextTurn(); //Orange to White
        turnStateMachine.nextTurn(); //White to Red

        assertEquals(2, turnStateMachine.getRound());
    }

    @Test
    public void testGetDiceRolledInitFalse() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        assertFalse(turnStateMachine.hasRolled);
    }

    @Test
    public void testChangeTurnResetsDiceRolled() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();
        turnStateMachine.hasRolled = true;

        turnStateMachine.nextTurn();

        assertFalse(turnStateMachine.hasRolled);
    }

    @Test
    public void testGetHasRolledTrue() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();
        turnStateMachine.hasRolled = true;

        assertTrue(turnStateMachine.getHasRolled());
    }

    @Test
    public void testGetHasRolledFalse() {
        TurnStateMachine turnStateMachine = new TurnStateMachine();

        assertFalse(turnStateMachine.getHasRolled());
    }

    @Test
    public void testSetForwardAndIsForward() {
        TurnStateMachine tsm = new TurnStateMachine();
        assertTrue(tsm.isForward());
        tsm.setForward(false);
        assertFalse(tsm.isForward());
        tsm.setForward(true);
        assertTrue(tsm.isForward());
    }

    @Test
    public void testNextTurnForwardR1Progression() {
        TurnStateMachine tsm = new TurnStateMachine();
        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn());
        assertEquals(1, tsm.getRound());
        tsm.nextTurn();
        assertEquals(Turn.ORANGE, tsm.getTurn());
        assertEquals(1, tsm.getRound());
        tsm.nextTurn();
        assertEquals(Turn.WHITE, tsm.getTurn());
        assertEquals(1, tsm.getRound());
    }
    @Test
    public void testTurnSequenceThroughR1ToR2Transition() {
        TurnStateMachine tsm = new TurnStateMachine();

        tsm.nextTurn();
        tsm.nextTurn();
        tsm.nextTurn();
        assertEquals(Turn.WHITE, tsm.getTurn());
        assertEquals(1, tsm.getRound());
        assertTrue(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.WHITE, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());

        tsm.setForward(false);
        assertFalse(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.ORANGE, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());
    }

    @Test
    public void testTurnSequenceThroughR2Backward() {
        TurnStateMachine tsm = new TurnStateMachine();
        tsm.nextTurn(); tsm.nextTurn(); tsm.nextTurn();
        tsm.nextTurn();
        tsm.setForward(false);
        assertEquals(Turn.WHITE, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.ORANGE, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());
    }

    @Test
    public void testTurnSequenceThroughR2ToR3Transition() {
        TurnStateMachine tsm = new TurnStateMachine();
        tsm.nextTurn(); tsm.nextTurn(); tsm.nextTurn();
        tsm.nextTurn();
        tsm.setForward(false);
        tsm.nextTurn();
        tsm.nextTurn();
        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        assertEquals(3, tsm.getRound());
        assertTrue(tsm.isForward());

        assertTrue(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.BLUE, tsm.getTurn());
        assertEquals(3, tsm.getRound());
        assertTrue(tsm.isForward());
    }


    @Test
    public void testTurnSequenceThroughR3WrapAround() {
        TurnStateMachine tsm = new TurnStateMachine();
        tsm.nextTurn(); tsm.nextTurn(); tsm.nextTurn();
        tsm.nextTurn(); tsm.setForward(false);
        tsm.nextTurn(); tsm.nextTurn(); tsm.nextTurn();
        tsm.nextTurn(); tsm.setForward(true);
        tsm.nextTurn(); tsm.nextTurn(); tsm.nextTurn();
        assertEquals(Turn.WHITE, tsm.getTurn());
        assertEquals(3, tsm.getRound());
        assertTrue(tsm.isForward());

        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        assertEquals(4, tsm.getRound());
        assertTrue(tsm.isForward());
    }


    @Test
    public void testNextTurnR1ToR2TransitionCheckFailsOnDirection() {
        TurnStateMachine tsm = new TurnStateMachine();

        tsm.nextTurn(); tsm.nextTurn(); tsm.nextTurn();
        assertEquals(Turn.WHITE, tsm.getTurn());
        assertEquals(1, tsm.getRound());
        assertTrue(tsm.isForward());


        tsm.setForward(false);
        assertFalse(tsm.isForward());

        tsm.nextTurn();

        assertEquals(Turn.ORANGE, tsm.getTurn());
        assertEquals(1, tsm.getRound());
        assertFalse(tsm.isForward());
    }

    @Test
    public void testNextTurnR2ToR3TransitionCheckFailsOnDirection() {
        TurnStateMachine tsm = new TurnStateMachine();

        tsm.nextTurn(); tsm.nextTurn(); tsm.nextTurn();
        tsm.nextTurn();
        tsm.setForward(false);
        tsm.nextTurn();
        tsm.nextTurn();
        tsm.nextTurn();
        assertEquals(Turn.RED, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertFalse(tsm.isForward());


        tsm.setForward(true);
        assertTrue(tsm.isForward());

        tsm.nextTurn();

        assertEquals(Turn.BLUE, tsm.getTurn());
        assertEquals(2, tsm.getRound());
        assertTrue(tsm.isForward());
    }


}
