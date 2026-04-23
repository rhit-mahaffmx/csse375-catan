package com.catan.datasource;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.catan.domain.HarborPoint;
import com.catan.domain.ResourceType;
import com.catan.domain.Terrain;


public class CatanFileReader {


    public static ArrayList<Point> readCoordinatesFromFile(BufferedReader bufferedReader) {
        ArrayList<Point> coordinates = new ArrayList<>();
        try {
            String coordinatePairAsString;
            while ((coordinatePairAsString = bufferedReader.readLine()) != null) {
                String[] splitCoordinates = coordinatePairAsString.split(",");
                Point coordAsPoint = new Point(Integer.parseInt(splitCoordinates[0]), Integer.parseInt(splitCoordinates[1]));
                coordinates.add(coordAsPoint);
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exeption reading line", e);
        }
        return coordinates;
    }

    public static ArrayList<ArrayList<Terrain>> readTerrainsFromFile(BufferedReader bufferedReader) {
        ArrayList<ArrayList<Terrain>> allTerrains = new ArrayList<>();
        try {
            String terrainInput;
            while ((terrainInput = bufferedReader.readLine()) != null) {
                ArrayList<Terrain> terrainsPerPoint = new ArrayList<>();
                String[] splitTerrains = terrainInput.split(",");
                for (String splitTerrain : splitTerrains) {
                    terrainsPerPoint.add(Terrain.valueOf(splitTerrain));
                }
                allTerrains.add(terrainsPerPoint);
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exeption reading line", e);
        }
        return allTerrains;
    }

    public static ArrayList<ResourceType> readResourceTypesFromFile(BufferedReader bufferedReader) {
        ArrayList<ResourceType> resourceTypes = new ArrayList<>();
        try {
            String resourceTypeInput;
            while ((resourceTypeInput = bufferedReader.readLine()) != null) {
                resourceTypes.add(ResourceType.valueOf(resourceTypeInput));
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exception reading line", e);
        }
        return resourceTypes;
    }

    public static ArrayList<Integer> readRobberNumbersFromFile(BufferedReader bufferedReader) {
        ArrayList<Integer> numbers = new ArrayList<>();
        try {
            String numberInput;
            while ((numberInput = bufferedReader.readLine()) != null) {
                numbers.add(Integer.parseInt(numberInput));
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exception reading line", e);
        }
        return numbers;
    }

    public static ArrayList<Set<Integer>> readNeighborsFromFile(BufferedReader bufferedReader) {
        ArrayList<Set<Integer>> allNeighborSets = new ArrayList<>();
        try {
            String neighborsInput;
            while ((neighborsInput = bufferedReader.readLine()) != null) {
                Set<Integer> neighborSet = new HashSet<>();
                String[] splitNeighbors = neighborsInput.split(",");
                for (String splitNeighbor : splitNeighbors) {
                    neighborSet.add(Integer.parseInt(splitNeighbor));
                }
                allNeighborSets.add(neighborSet);
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exeption reading line", e);
        }
        return allNeighborSets;
    }

    public static ArrayList<ArrayList<Integer>> readTileValues(BufferedReader bufferedReader) {
        ArrayList<ArrayList<Integer>> allTileValues = new ArrayList<>();
        try {
            String tileValueString;
            while ((tileValueString = bufferedReader.readLine()) != null) {
                ArrayList<Integer> tileValuesPerPoint = new ArrayList<>();
                String[] splitValues = tileValueString.split(",");
                for (String splitValue : splitValues) {
                    tileValuesPerPoint.add(Integer.parseInt(splitValue));
                }
                allTileValues.add(tileValuesPerPoint);
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exeption reading line", e);
        }
        return allTileValues;
    }

    public static BufferedReader getBufferedReaderFromFileName(FileInputStream stream) {
        DataInputStream dataStream = new DataInputStream(stream);
        return new BufferedReader(new InputStreamReader(dataStream));
    }
    public static ArrayList<HarborPoint> readHarborsFromFile(BufferedReader bufferedReader) {

        ArrayList<HarborPoint> harborPoints = new ArrayList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(",");
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                String resourceText = parts[2].trim().toUpperCase();
                ResourceType res = resourceText.equalsIgnoreCase("NULL")
                        ? ResourceType.NULL
                        : ResourceType.valueOf(resourceText.toUpperCase());


                harborPoints.add(new HarborPoint(x, y, res));
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exception reading harbor line", e);
        }
        return harborPoints;
    }

    public static ArrayList<int[]> readFishingGroundsFromFile(BufferedReader bufferedReader) {
        ArrayList<int[]> fishingGrounds = new ArrayList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                int diceNumber = Integer.parseInt(parts[2].trim());
                fishingGrounds.add(new int[]{x, y, diceNumber});
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O exception reading fishing grounds", e);
        }
        return fishingGrounds;
    }
}
