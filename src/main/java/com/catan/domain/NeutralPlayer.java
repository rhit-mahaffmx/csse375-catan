package com.catan.domain;

/**
 * Represents a neutral player in the two-player variant of Catan.
 * Neutral players have settlements and roads placed on the board but do not
 * produce resources. Their settlements block spaces, occupy harbors, and
 * count toward the Longest Road calculation.
 */
public class NeutralPlayer {
    public final Turn color;
    public int settlements = Board.INITIAL_SETTLEMENTS;
    public int roads = Board.INITIAL_ROADS;

    public NeutralPlayer(Turn color) {
        this.color = color;
    }

    public boolean hasSettlementsRemaining() {
        return settlements > 0;
    }

    public boolean hasRoadsRemaining() {
        return roads > 0;
    }

    public void useSettlement() {
        if (settlements > 0) {
            settlements--;
        }
    }

    public void useRoad() {
        if (roads > 0) {
            roads--;
        }
    }
}
