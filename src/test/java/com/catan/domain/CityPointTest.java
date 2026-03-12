package com.catan.domain;

import java.util.List;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CityPointTest {
    @Test
    public void testGetThreeTerrains(){
        CityPoint testPoint = new CityPoint(1, 1);
        List<Integer> testNumbers = List.of(1, 2, 3);
        List<Terrain> testTerrains = List.of(Terrain.FOREST, Terrain.DESERT, Terrain.FIELD);
        testPoint.setTileValues(testNumbers, testTerrains);
        assertEquals(3, testPoint.getTerrains().size());
        assertTrue(testPoint.getTerrains().contains(Terrain.FOREST));
        assertTrue(testPoint.getTerrains().contains(Terrain.DESERT));
        assertTrue(testPoint.getTerrains().contains(Terrain.FIELD));
    }

    @Test
    public void testGetThreeValues(){
        CityPoint testPoint = new CityPoint(1, 1);
        List<Integer> testNumbers = List.of(1, 2, 3);
        List<Terrain> testTerrains = List.of(Terrain.FOREST, Terrain.DESERT, Terrain.FIELD);
        testPoint.setTileValues(testNumbers, testTerrains);
        assertEquals(3, testPoint.getTileValues().size());
        assertTrue(testPoint.getTileValues().contains(1));
        assertTrue(testPoint.getTileValues().contains(2));
        assertTrue(testPoint.getTileValues().contains(3));
    }

    @Test
    public void testGetXAndY(){
        CityPoint testPoint = new CityPoint(1, 1);
        assertEquals(1, testPoint.getX());
        assertEquals(1, testPoint.getY());
    }

    @Test
    public void testGetXAndYNegative(){
        CityPoint testPoint = new CityPoint(-1, -1);
        assertEquals(-1, testPoint.getX());
        assertEquals(-1, testPoint.getY());
    }

    @Test
    public void testAddNeighbor() {
        CityPoint testPoint = new CityPoint(1, 1);
        RoadPoint roadPoint = EasyMock.createMock(RoadPoint.class);
        testPoint.addNeighbor(roadPoint);
        assertEquals(1, testPoint.neighbors.size());
    }

    @Test
    public void testAddMultipleNeighbors() {
        CityPoint testPoint = new CityPoint(1, 1);
        RoadPoint roadPoint = EasyMock.createMock(RoadPoint.class);

        testPoint.addNeighbor(roadPoint);
        testPoint.addNeighbor(roadPoint);
        assertEquals(2, testPoint.neighbors.size());
    }

    @Test
    public void testHasSettlement(){
        CityPoint testPoint = new CityPoint(1, 1);
        assertEquals(false, testPoint.hasSettlement());
    }

    @Test
    public void testPlaceSettlement(){
        CityPoint testPoint = new CityPoint(1, 1);
        testPoint.placeSettlement(Turn.RED);
        assertEquals(true, testPoint.hasSettlement());
    }

    @Test
    public void testGetOwnerBlue(){
        CityPoint testPoint = new CityPoint(1, 1);
        testPoint.placeSettlement(Turn.BLUE);
        assertEquals(Turn.BLUE, testPoint.getOwner());
    }


    @Test
    public void testGatherResourcesFromCityPoint() {

        CityPoint city = new CityPoint(10, 10);


        city.setTileValues(
                List.of(4, 9),
                List.of(Terrain.FOREST, Terrain.PASTURE)
        );

        List<ResourceCard> cards = city.gatherResources();

        assertEquals(2, cards.size());
        assertTrue(cards.stream().anyMatch(c -> c.getResourceType() == ResourceType.WOOD));
        assertTrue(cards.stream().anyMatch(c -> c.getResourceType() == ResourceType.SHEEP));
    }


}
