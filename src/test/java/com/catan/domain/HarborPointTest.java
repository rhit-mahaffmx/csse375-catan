package com.catan.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class HarborPointTest {
    @Test
    public void testGetTwoTerrainsHarborPoint(){
        HarborPoint testPoint = new HarborPoint(1, 1, ResourceType.BRICK);
        List<Integer> testNumbers = List.of(1, 2);
        List<Terrain> testTerrains = List.of(Terrain.FOREST, Terrain.DESERT);
        testPoint.setTileValues(testNumbers, testTerrains);

        assertEquals(2, testPoint.getTerrains().size());
        assertTrue(testPoint.getTerrains().contains(Terrain.FOREST));
        assertTrue(testPoint.getTerrains().contains(Terrain.DESERT));
    }

    @Test
    public void testGetTwoValues() {
        HarborPoint testPoint = new HarborPoint(1, 1, ResourceType.BRICK);
        List<Integer> testNumbers = List.of(1, 2);
        List<Terrain> testTerrains = List.of(Terrain.FOREST, Terrain.DESERT);
        testPoint.setTileValues(testNumbers, testTerrains);
        assertEquals(2, testPoint.getTileValues().size());
        assertTrue(testPoint.getTileValues().contains(1));
        assertTrue(testPoint.getTileValues().contains(2));

    }

    @Test
    public void testGetXAndY(){
        HarborPoint testPoint = new HarborPoint(10, 20, ResourceType.BRICK);
        assertEquals(10, testPoint.getX());
        assertEquals(20, testPoint.getY());
    }
    @Test
    public void testGetXAndYNegative(){
        HarborPoint testPoint = new HarborPoint(-1, -1, ResourceType.BRICK);
        assertEquals(-1, testPoint.getX());
        assertEquals(-1, testPoint.getY());
    }

    @Test
    public void testGetTradingResource(){
        HarborPoint testPoint = new HarborPoint(1, 1, ResourceType.BRICK);
        assertEquals(ResourceType.BRICK, testPoint.getTradingResource());
    }

    @Test
    void isGenericReturnsTrueForGenericHarbor() {
        HarborPoint generic = new HarborPoint(100, 200, ResourceType.NULL);

        assertTrue(generic.isGeneric(), "Harbor should report generic (3:1) when ResourceType is NULL");
    }

    @Test
    void isGenericReturnsFalseForResourceHarbor() {
        HarborPoint wheatHarbor = new HarborPoint(100, 200, ResourceType.WHEAT);

        assertFalse(wheatHarbor.isGeneric(), "Harbor should report non-generic when it has a resource");
    }
}
