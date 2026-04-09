package com.catan.domain;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
public class DiceRollTest {


    @Test
    public void testNumberCardDeckDrawReturnsValidNumber() {
        NumberCardDeck deck = new NumberCardDeck(new Random(42));

        int result = deck.drawNumber();

        assertTrue(result >= 2 && result <= 12,
                "Card draw " + result + " should be between 2 and 12");
    }
}
