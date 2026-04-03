package com.catan.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NumberCardDeck {
    private static final int[] CARD_COUNTS = {0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};

    private final List<EventCard> fullDeck;
    private final List<EventCard> drawPile;
    private final Random random;

    public NumberCardDeck(Random random) {
        this.random = random;
        this.fullDeck = new ArrayList<>();
        for (int number = 2; number <= 12; number++) {
            for (int i = 0; i < CARD_COUNTS[number]; i++) {
                fullDeck.add(new EventCard(EventType.NO_EVENT, number));
            }
        }
        this.drawPile = new ArrayList<>();
        reshuffle();
    }

    public void reshuffle() {
        drawPile.clear();
        drawPile.addAll(fullDeck);
        Collections.shuffle(drawPile, random);
    }

    public EventCard drawCard() {
        if (drawPile.isEmpty()) {
            reshuffle();
        }
        return drawPile.remove(drawPile.size() - 1);
    }

    public int drawNumber() {
        return drawCard().getDiceNumber();
    }

    public int cardsRemaining() {
        return drawPile.size();
    }

    public int deckSize() {
        return fullDeck.size();
    }
}
