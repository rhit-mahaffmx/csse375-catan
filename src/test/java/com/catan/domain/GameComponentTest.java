package com.catan.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameComponentTest {

    @Test
    public void testGameComponentGetX() {
        GameComponent gameComponent = new GameComponent(1, 1);

        assertEquals(1, gameComponent.getX());
    }

    @Test
    public void testGameComponentGetY() {
        GameComponent gameComponent = new GameComponent(1, 1);

        assertEquals(1, gameComponent.getY());
    }

    @Test
    public void testGameComponentMultipleObjects() {
        GameComponent gameComponentOne = new GameComponent(1, 1);
        GameComponent gameComponentTwo = new GameComponent(2, 2);

        assertEquals(1, gameComponentOne.getX());
        assertEquals(1, gameComponentOne.getY());

        assertEquals(2, gameComponentTwo.getX());
        assertEquals(2, gameComponentTwo.getY());
    }
}
