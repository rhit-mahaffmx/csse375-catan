package com.catan.presentation;

import com.catan.domain.Turn;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameWindowTest {

    @Test
    public void testGetPlayerInfoRed() {
        PlayerInfo info = GameWindow.getPlayerInfo(Turn.RED);
        assertEquals("Player 1 (Red)", info.name());
        assertEquals(Color.RED, info.color());
    }

    @Test
    public void testGetPlayerInfoBlue() {
        PlayerInfo info = GameWindow.getPlayerInfo(Turn.BLUE);
        assertEquals("Player 2 (Blue)", info.name());
        assertEquals(Color.BLUE, info.color());
    }

    @Test
    public void testGetPlayerInfoOrange() {
        PlayerInfo info = GameWindow.getPlayerInfo(Turn.ORANGE);
        assertEquals("Player 3 (Orange)", info.name());
        assertEquals(Color.ORANGE, info.color());
    }

    @Test
    public void testGetPlayerInfoWhite() {
        PlayerInfo info = GameWindow.getPlayerInfo(Turn.WHITE);
        assertEquals("Player 4 (White)", info.name());
        assertEquals(Color.WHITE, info.color());
    }
}