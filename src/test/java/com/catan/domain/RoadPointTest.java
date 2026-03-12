package com.catan.domain;

import org.easymock.EasyMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoadPointTest {

    @Test
    public void testGetXAndYOrigin() {
        RoadPoint roadPoint = new RoadPoint(0, 0);
        assertEquals(0, roadPoint.getX());
        assertEquals(0, roadPoint.getY());
    }

    @Test
    public void testGetXAndYQ1() {
        RoadPoint roadPoint = new RoadPoint(1, 1);
        assertEquals(1, roadPoint.getX());
        assertEquals(1, roadPoint.getY());
    }

    @Test
    public void testGetXAndYQ2() {
        RoadPoint roadPoint = new RoadPoint(-1, 1);
        assertEquals(-1, roadPoint.getX());
        assertEquals(1, roadPoint.getY());
    }

    @Test
    public void testGetXAndYQ3() {
        RoadPoint roadPoint = new RoadPoint(-1, -1);
        assertEquals(-1, roadPoint.getX());
        assertEquals(-1, roadPoint.getY());
    }

    @Test
    public void testGetXAndYQ4() {
        RoadPoint roadPoint = new RoadPoint(1, -1);
        assertEquals(1, roadPoint.getX());
        assertEquals(-1, roadPoint.getY());
    }

    @Test
    public void testAddNeighbor() {
        RoadPoint testPoint = new RoadPoint(1, 1);
        CityPoint cityPoint = EasyMock.createMock(CityPoint.class);
        testPoint.addNeighbor(cityPoint);
        assertEquals(1, testPoint.neighbors.size());
    }

    @Test
    public void testAddMultipleNeighbors() {
        RoadPoint testPoint = new RoadPoint(1, 1);
        CityPoint cityPoint = EasyMock.createMock(CityPoint.class);

        testPoint.addNeighbor(cityPoint);
        testPoint.addNeighbor(cityPoint);

        assertEquals(2, testPoint.neighbors.size());
    }

    @Test
    public void testHasRoad(){
        RoadPoint testPoint = new RoadPoint(1, 1);
        assertFalse(testPoint.hasRoad());
    }

    @Test
    public void testPlaceRoad(){
        RoadPoint testPoint = new RoadPoint(1, 1);
        testPoint.placeRoad(Turn.RED);
        assertTrue(testPoint.hasRoad());
    }

    @Test
    public void testGetOwnerBlue(){
        RoadPoint testPoint = new RoadPoint(1, 1);
        testPoint.placeRoad(Turn.BLUE);
        assertEquals(Turn.BLUE, testPoint.getOwner());
    }
}
