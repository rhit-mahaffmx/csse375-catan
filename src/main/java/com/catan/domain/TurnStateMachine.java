package com.catan.domain;

public class TurnStateMachine {

    public final static Turn FIRST_TURN = Turn.RED;
    public final static boolean INITIAL_DICE_ROLLED = false;
    public boolean hasRolled;
    private Turn turn;
    private int round = 1;
    private boolean forward = true;
    private boolean twoPlayerMode = false;

    public TurnStateMachine() {
        this.turn = FIRST_TURN;
        this.hasRolled = INITIAL_DICE_ROLLED;
    }

    public void setTwoPlayerMode(boolean twoPlayerMode) {
        this.twoPlayerMode = twoPlayerMode;
    }

    public boolean isTwoPlayerMode() {
        return twoPlayerMode;
    }

    public Turn getTurn() {
        return this.turn;
    }

    public void setForward(boolean forward) {
        this.forward = forward;
    }

    public boolean isForward() {
        return this.forward;
    }
    public void nextTurn() {
        if (twoPlayerMode) {
            nextTurnTwoPlayer();
        } else {
            nextTurnStandard();
        }
        this.hasRolled = false;
    }

    private void nextTurnTwoPlayer() {
        if (this.round == 1 && this.turn == Turn.BLUE && this.forward) {
            this.round = 2;
            this.forward = false;
        } else if (this.round == 2 && this.turn == Turn.RED && !this.forward) {
            this.round = 3;
            this.forward = true;
        } else {
            if (this.forward) {
                if (this.turn == Turn.RED) {
                    this.turn = Turn.BLUE;
                } else if (this.turn == Turn.BLUE) {
                    this.round++;
                    this.turn = Turn.RED;
                }
            } else {
                if (this.turn == Turn.BLUE) {
                    this.turn = Turn.RED;
                }
            }
        }
    }

    private void nextTurnStandard() {
        boolean direction = this.forward;

        if (this.round == 1 && this.turn == Turn.WHITE && direction) {
            this.round = 2;
            this.forward = false;
        }
        else if (this.round == 2 && this.turn == Turn.RED && !direction) {
            this.round = 3;
            this.forward = true;
        }
        else {
            if (direction) {
                if (this.turn == Turn.RED) {
                    this.turn = Turn.BLUE;
                } else if (this.turn == Turn.BLUE) {
                    this.turn = Turn.ORANGE;
                } else if (this.turn == Turn.ORANGE) {
                    this.turn = Turn.WHITE;
                } else if (this.turn == Turn.WHITE){
                    this.round++;
                    this.turn = Turn.RED;
                }
            } else {
                if (this.turn == Turn.WHITE) {
                    this.turn = Turn.ORANGE;
                } else if (this.turn == Turn.ORANGE) {
                    this.turn = Turn.BLUE;
                } else if (this.turn == Turn.BLUE) {
                    this.turn = Turn.RED;
                }
            }
        }
    }

    public int getRound(){
        return round;
    }

    public boolean getHasRolled() {
        return hasRolled;
    }
}