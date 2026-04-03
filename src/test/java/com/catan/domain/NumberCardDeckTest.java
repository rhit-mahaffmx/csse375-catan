package com.catan.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class NumberCardDeckTest {

    @Test
    public void testDeckHas36Cards() {
        NumberCardDeck deck = new NumberCardDeck(new Random());
        assertEquals(36, deck.deckSize());
        assertEquals(36, deck.cardsRemaining());
    }

    @Test
    public void testDrawReducesCardsRemaining() {
        NumberCardDeck deck = new NumberCardDeck(new Random());
        deck.drawCard();
        assertEquals(35, deck.cardsRemaining());
    }

    @Test
    public void testDrawNumberReturnsValidRange() {
        NumberCardDeck deck = new NumberCardDeck(new Random(42));
        for (int i = 0; i < 36; i++) {
            int num = deck.drawNumber();
            assertTrue(num >= 2 && num <= 12,
                    "Card number " + num + " outside valid range [2, 12]");
        }
    }

    @Test
    public void testFullDeckDistribution() {
        NumberCardDeck deck = new NumberCardDeck(new Random());
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 2; i <= 12; i++) counts.put(i, 0);

        for (int i = 0; i < 36; i++) {
            int num = deck.drawNumber();
            counts.put(num, counts.get(num) + 1);
        }

        assertEquals(1, counts.get(2));
        assertEquals(2, counts.get(3));
        assertEquals(3, counts.get(4));
        assertEquals(4, counts.get(5));
        assertEquals(5, counts.get(6));
        assertEquals(6, counts.get(7));
        assertEquals(5, counts.get(8));
        assertEquals(4, counts.get(9));
        assertEquals(3, counts.get(10));
        assertEquals(2, counts.get(11));
        assertEquals(1, counts.get(12));
    }

    @Test
    public void testAutoReshuffleWhenEmpty() {
        NumberCardDeck deck = new NumberCardDeck(new Random());
        for (int i = 0; i < 36; i++) {
            deck.drawCard();
        }
        assertEquals(0, deck.cardsRemaining());

        EventCard card = deck.drawCard();
        assertTrue(card.getDiceNumber() >= 2 && card.getDiceNumber() <= 12);
        assertEquals(35, deck.cardsRemaining());
    }

    @Test
    public void testReshuffleRestoresDeck() {
        NumberCardDeck deck = new NumberCardDeck(new Random());
        deck.drawCard();
        deck.drawCard();
        assertEquals(34, deck.cardsRemaining());

        deck.reshuffle();
        assertEquals(36, deck.cardsRemaining());
    }

    @Test
    public void testDrawCardReturnsEventCard() {
        NumberCardDeck deck = new NumberCardDeck(new Random(42));
        EventCard card = deck.drawCard();
        assertEquals(EventType.NO_EVENT, card.getEventType());
        assertTrue(card.getDiceNumber() >= 2 && card.getDiceNumber() <= 12);
    }

    @Test
    public void testMultipleFullCyclesPreserveDistribution() {
        NumberCardDeck deck = new NumberCardDeck(new Random());
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 2; i <= 12; i++) counts.put(i, 0);

        for (int cycle = 0; cycle < 3; cycle++) {
            for (int i = 0; i < 36; i++) {
                int num = deck.drawNumber();
                counts.put(num, counts.get(num) + 1);
            }
        }

        // After 3 complete cycles (108 draws), each number appears 3x its deck count
        assertEquals(3, counts.get(2));
        assertEquals(6, counts.get(3));
        assertEquals(9, counts.get(4));
        assertEquals(12, counts.get(5));
        assertEquals(15, counts.get(6));
        assertEquals(18, counts.get(7));
        assertEquals(15, counts.get(8));
        assertEquals(12, counts.get(9));
        assertEquals(9, counts.get(10));
        assertEquals(6, counts.get(11));
        assertEquals(3, counts.get(12));
    }
}
