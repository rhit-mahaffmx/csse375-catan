package com.catan.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NumberCardDeck {
    private static final int[] CARD_COUNTS = {0, 0, 1, 2, 3, 4, 5, 6, 5, 4, 3, 2, 1};

    private static final EventType[][] EVENT_ASSIGNMENTS = {
        {},  // 0
        {},  // 1
        {EventType.NO_EVENT},                                                                   // 2  (1 card)
        {EventType.EARTHQUAKE, EventType.NO_EVENT},                                             // 3  (2 cards)
        {EventType.EPIDEMIC, EventType.NO_EVENT, EventType.NO_EVENT},                           // 4  (3 cards)
        {EventType.GOOD_NEIGHBORS, EventType.NO_EVENT, EventType.NO_EVENT, EventType.NO_EVENT}, // 5  (4 cards)
        {EventType.CALM_SEAS, EventType.NO_EVENT, EventType.NO_EVENT, EventType.NO_EVENT, EventType.NO_EVENT}, // 6  (5 cards)
        {EventType.ROBBER_ATTACK, EventType.ROBBER_ATTACK, EventType.ROBBER_ATTACK, EventType.ROBBER_ATTACK, EventType.ROBBER_ATTACK, EventType.ROBBER_ATTACK}, // 7  (6 cards)
        {EventType.TOURNAMENT, EventType.NO_EVENT, EventType.NO_EVENT, EventType.NO_EVENT, EventType.NO_EVENT}, // 8  (5 cards)
        {EventType.TRADE_ADVANTAGE, EventType.NO_EVENT, EventType.NO_EVENT, EventType.NO_EVENT}, // 9  (4 cards)
        {EventType.CONFLICT, EventType.NO_EVENT, EventType.NO_EVENT},                           // 10 (3 cards)
        {EventType.NO_EVENT, EventType.NO_EVENT},                                               // 11 (2 cards)
        {EventType.NO_EVENT},                                                                   // 12 (1 card)
    };

    private final List<EventCard> fullDeck;
    private final List<EventCard> drawPile;
    private final Random random;

    public NumberCardDeck(Random random) {
        this.random = random;
        this.fullDeck = new ArrayList<>();
        for (int number = 2; number <= 12; number++) {
            EventType[] events = EVENT_ASSIGNMENTS[number];
            for (int i = 0; i < CARD_COUNTS[number]; i++) {
                fullDeck.add(new EventCard(events[i], number));
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
