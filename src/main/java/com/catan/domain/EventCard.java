package com.catan.domain;

public class EventCard {
    private final EventType eventType;
    private final int diceNumber;

    public EventCard(EventType eventType, int diceNumber) {
        this.eventType = eventType;
        this.diceNumber = diceNumber;
    }

    public EventType getEventType() {
        return eventType;
    }

    public int getDiceNumber() {
        return diceNumber;
    }
}
