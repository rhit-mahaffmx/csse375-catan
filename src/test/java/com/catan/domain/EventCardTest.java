package com.catan.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

public class EventCardTest {

    @Test
    public void testEventCardCreation() {
        EventCard card = new EventCard(EventType.ROBBER_ATTACK, 7);
        assertEquals(EventType.ROBBER_ATTACK, card.getEventType());
        assertEquals(7, card.getDiceNumber());
    }

    @Test
    public void testEventCardNoEvent() {
        EventCard card = new EventCard(EventType.NO_EVENT, 5);
        assertEquals(EventType.NO_EVENT, card.getEventType());
        assertEquals(5, card.getDiceNumber());
    }

    @Test
    public void testEventCardNewYear() {
        EventCard card = new EventCard(EventType.NEW_YEAR, 0);
        assertEquals(EventType.NEW_YEAR, card.getEventType());
        assertEquals(0, card.getDiceNumber());
    }

    @Test
    public void testAllEventTypes() {
        EventType[] types = EventType.values();
        assertEquals(10, types.length);
        assertNotNull(EventType.valueOf("ROBBER_ATTACK"));
        assertNotNull(EventType.valueOf("EARTHQUAKE"));
        assertNotNull(EventType.valueOf("EPIDEMIC"));
        assertNotNull(EventType.valueOf("GOOD_NEIGHBORS"));
        assertNotNull(EventType.valueOf("CALM_SEAS"));
        assertNotNull(EventType.valueOf("TOURNAMENT"));
        assertNotNull(EventType.valueOf("TRADE_ADVANTAGE"));
        assertNotNull(EventType.valueOf("CONFLICT"));
        assertNotNull(EventType.valueOf("NO_EVENT"));
        assertNotNull(EventType.valueOf("NEW_YEAR"));
    }
}
