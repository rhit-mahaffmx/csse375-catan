package com.catan.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SettlementTest {
    @Test
    public void testGetColor(){
        Settlement testSettlement = new Settlement(Turn.RED);
        assertEquals(Turn.RED, testSettlement.getColor());
    }

    @Test
    public void testCreateBlueSettlement(){
        Settlement testSettlement = new Settlement(Turn.BLUE);
        assertEquals(Turn.BLUE, testSettlement.getColor());
    }

}
