package com.catan.domain;

import java.util.Random;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DevelopmentCardTest {

    @Test
    public void testDrawKnight(){

        DevelopmentCard developmentCard = new DevelopmentCard(DevCards.KNIGHT);

        assertEquals(DevCards.KNIGHT, developmentCard.getType());
    }

    @Test
    public void testDrawVictoryPoint(){

        DevelopmentCard developmentCard = new DevelopmentCard(DevCards.VICTORY_POINT);

        assertEquals(DevCards.VICTORY_POINT, developmentCard.getType());
    }

    @Test
    public void testDrawYearOfPlenty(){
        DevelopmentCard developmentCard = new DevelopmentCard(DevCards.YEAR_OF_PLENTY);

        assertEquals(DevCards.YEAR_OF_PLENTY, developmentCard.getType());
    }

    @Test
    public void testDrawRoadBuilding(){
        DevelopmentCard developmentCard = new DevelopmentCard(DevCards.ROAD_BUILDING);

        assertEquals(DevCards.ROAD_BUILDING, developmentCard.getType());
    }

    @Test
    public void testDrawMonopoly(){
        DevelopmentCard developmentCard = new DevelopmentCard(DevCards.MONOPOLY);

        assertEquals(DevCards.MONOPOLY, developmentCard.getType());
    }

    @Test public void setTurnBought1(){
        DevelopmentCard developmentCard = new DevelopmentCard(DevCards.KNIGHT);

        developmentCard.setTurnBought(1);

        assertEquals(1, developmentCard.getTurnBought());
    }

    @Test public void setTurnBought2(){
        DevelopmentCard developmentCard = new DevelopmentCard(DevCards.KNIGHT);

        developmentCard.setTurnBought(2);

        assertEquals(2, developmentCard.getTurnBought());
    }


}
