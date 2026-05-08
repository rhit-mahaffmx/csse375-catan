package com.catan.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NeutralPlayerTest {

    @Test
    public void testNeutralPlayerInitialization() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        assertEquals(Turn.WHITE, neutral.color);
        assertEquals(Board.INITIAL_SETTLEMENTS, neutral.settlements);
        assertEquals(Board.INITIAL_ROADS, neutral.roads);
    }

    @Test
    public void testHasSettlementsRemaining() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        assertTrue(neutral.hasSettlementsRemaining());
    }

    @Test
    public void testHasRoadsRemaining() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        assertTrue(neutral.hasRoadsRemaining());
    }

    @Test
    public void testUseSettlement() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        int before = neutral.settlements;
        neutral.useSettlement();
        assertEquals(before - 1, neutral.settlements);
    }

    @Test
    public void testUseRoad() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        int before = neutral.roads;
        neutral.useRoad();
        assertEquals(before - 1, neutral.roads);
    }

    @Test
    public void testSettlementsDepletedReturnsNoRemaining() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        for (int i = 0; i < Board.INITIAL_SETTLEMENTS; i++) {
            neutral.useSettlement();
        }
        assertFalse(neutral.hasSettlementsRemaining());
        assertEquals(0, neutral.settlements);
    }

    @Test
    public void testRoadsDepletedReturnsNoRemaining() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.WHITE);
        for (int i = 0; i < Board.INITIAL_ROADS; i++) {
            neutral.useRoad();
        }
        assertFalse(neutral.hasRoadsRemaining());
        assertEquals(0, neutral.roads);
    }

    @Test
    public void testUseSettlementAtZeroDoesNotGoNegative() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.ORANGE);
        for (int i = 0; i < Board.INITIAL_SETTLEMENTS; i++) {
            neutral.useSettlement();
        }
        neutral.useSettlement(); // should not go negative
        assertEquals(0, neutral.settlements);
    }

    @Test
    public void testUseRoadAtZeroDoesNotGoNegative() {
        NeutralPlayer neutral = new NeutralPlayer(Turn.ORANGE);
        for (int i = 0; i < Board.INITIAL_ROADS; i++) {
            neutral.useRoad();
        }
        neutral.useRoad(); // should not go negative
        assertEquals(0, neutral.roads);
    }
}
