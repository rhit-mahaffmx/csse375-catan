package com.catan.domain;

import java.util.Random;

public class Dice {
    private final Random random;
    private final int diceSize = 6;

    public Dice(Random random) {
        this.random = random;
    }

    public int roll() {

        int die1 = random.nextInt(diceSize) + 1;
        int die2 = random.nextInt(diceSize) + 1;
        return die1 + die2;
    }
}
