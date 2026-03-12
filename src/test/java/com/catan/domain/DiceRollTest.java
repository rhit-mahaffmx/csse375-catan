package com.catan.domain;
import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.Random;
public class DiceRollTest {


    @Test
    public void testTwoDiceRollWithMockedRandom() {
        // Create a mock for Random.
        Random randomMock = EasyMock.createMock(Random.class);

        EasyMock.expect(randomMock.nextInt(6)).andReturn(3).once();
        EasyMock.expect(randomMock.nextInt(6)).andReturn(4).once();


        EasyMock.replay(randomMock);

        Dice dice = new Dice(randomMock);



        assertEquals(9, dice.roll());

        EasyMock.verify(randomMock);
    }
}
