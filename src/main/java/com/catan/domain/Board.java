package com.catan.domain;

import com.catan.datasource.BoardDataInputs;
import com.catan.datasource.CatanFileReader;

import java.awt.*;
import java.io.*;
import java.util.*;

public class Board {
    public final static String CITY_COORDINATES_FILE_PATH = "src/main/resources/inputs/cityCoords";
    public final static String ROAD_COORDINATES_FILE_PATH = "src/main/resources/inputs/roadCoords";
    public static final String ROBBER_COORDINATES_FILE_PATH = "src/main/resources/inputs/robberCoords";
    public static final String ROBBER_RESOURCE_FILE_PATH = "src/main/resources/inputs/robberResources";
    public static final String ROBBER_NUMBER_FILE_PATH = "src/main/resources/inputs/robberNumbers";
    public final static String TERRAIN_COORDINATES_FILE_PATH = "src/main/resources/inputs/cityTerrains";
    public final static String TILE_VALUE_COORDINATES_FILE_PATH = "src/main/resources/inputs/cityValues";
    public static final String CITY_NEIGHBORS_FILEPATH = "src/main/resources/inputs/cityNeighbors";
    public static final String ROAD_NEIGHBORS_FILEPATH = "src/main/resources/inputs/roadNeighbors";
    public static final String HARBORS_FILE_PATH = "src/main/resources/inputs/harbors";


    public final static int INITIAL_SETTLEMENTS = 5;
    public final static int INITIAL_ROADS = 15;
    public final static int INITIAL_VICTORY_POINTS = 0;

    public final static int VP_WIN_AMOUNT = 10;
    public final static int MINIMUM_ARMY = 3;
    private static final Integer ARMY_VP = 3;
    public final static int GENERIC_TRADE_VALUE = 3;
    public final static int BANK_TRADE_VALUE = 4;
    public final static int HARBOR_TRADE_VALUE = 2;

    public final static int NUM_CITYPOINTS = 54;
    public final static int NUM_ROADPOINTS = 72;
    private static final int DEV_CARD_AMT = 5;
    private static final int KNIGHT = 0;
    private static final int VICTORY_POINT = 1;
    private static final int YEAR_OF_PLENTY = 2;
    private static final int ROAD_BUILDING = 3;

    public int numRolled;
    public boolean robberMoved;

    ResourceType robberResource;
    int robberNumber;
    private Boolean knightUsed = false;
    static final ResourceType INITIAL_ROBBER_RESOURCE_TYPE = ResourceType.NULL;
    static final int INITIAL_ROBBER_NUMBER = 10;
    private static final boolean NO_SEVEN_ROLLED = true;
    private static final boolean ROBBER_ALREADY_MOVED = true;
    private static final int MIN_LONGEST_ROAD_LEN = 5;
    private static final int LONGEST_ROAD_BONUS = 2;
    public final static int DISCARD_THRESHOLD = 7;
    private Turn currentLongestRoadPlayer = Turn.BANK;
    private int currentLongestRoadLength = 0;

    GameWindowController gameWindowController;
    TurnStateMachine turnStateMachine;
    Dice dice;

    public ArrayList<CityPoint> cityPoints;
    public ArrayList<RoadPoint> roadPoints;
    public ArrayList<RobberPoint> robberPoints;
    public HashMap<Turn, Integer> longestRoad = new HashMap<>();
    public Set<Player> eligiblePlayers = new HashSet<>();

    HashMap<Turn, Player> turnToPlayer = new HashMap<>();

    Random rand = new Random();

    public Board(GameWindowController gameWindowController, TurnStateMachine turnStateMachine, Dice dice){
        this.gameWindowController = gameWindowController;
        this.turnStateMachine = turnStateMachine;
        this.dice = dice;

        turnToPlayer.put(Turn.RED, new Player(Turn.RED));
        turnToPlayer.put(Turn.BLUE, new Player(Turn.BLUE));
        turnToPlayer.put(Turn.ORANGE, new Player(Turn.ORANGE));
        turnToPlayer.put(Turn.WHITE, new Player(Turn.WHITE));
        turnToPlayer.put(Turn.BANK, new Player(Turn.BANK));

        longestRoad.put(Turn.RED, 0);
        longestRoad.put(Turn.BLUE, 0);
        longestRoad.put(Turn.ORANGE, 0);
        longestRoad.put(Turn.WHITE, 0);

        robberResource = INITIAL_ROBBER_RESOURCE_TYPE;
        robberNumber = INITIAL_ROBBER_NUMBER;
        robberMoved = NO_SEVEN_ROLLED;

    }
    public void loadBoardData(BoardDataInputs dataInputs) {
        initializeBoardFromStreams(dataInputs);
    }

    public ArrayList<CityPoint> createCities(FileInputStream coordStream, FileInputStream terrainStream, FileInputStream tileStream) {
        BufferedReader coordReader = CatanFileReader.getBufferedReaderFromFileName(coordStream);
        ArrayList<Point> coordinatePoints = CatanFileReader.readCoordinatesFromFile(coordReader);

        BufferedReader terrainReader = CatanFileReader.getBufferedReaderFromFileName(terrainStream);
        ArrayList<ArrayList<Terrain>> terrains = CatanFileReader.readTerrainsFromFile(terrainReader);

        BufferedReader tileValueReader = CatanFileReader.getBufferedReaderFromFileName(tileStream);
        ArrayList<ArrayList<Integer>> tileValues = CatanFileReader.readTileValues(tileValueReader);

        ArrayList<CityPoint> cities = new ArrayList<>();
        for (int i = 0; i < coordinatePoints.size(); i++) {
            CityPoint pointToAdd = new CityPoint(coordinatePoints.get(i).x, coordinatePoints.get(i).y);
            pointToAdd.setTileValues(tileValues.get(i), terrains.get(i));
            cities.add(pointToAdd);
        }
        return cities;
    }

    public ArrayList<RoadPoint> createRoads(FileInputStream roadStream) {
        BufferedReader bufferedReader = CatanFileReader.getBufferedReaderFromFileName(roadStream);
        ArrayList<Point> coordinatePoints = CatanFileReader.readCoordinatesFromFile(bufferedReader);
        ArrayList<RoadPoint> roads = new ArrayList<>();
        for (Point coordinatePoint : coordinatePoints) {
            roads.add(new RoadPoint(coordinatePoint.x, coordinatePoint.y));
        }
        return roads;
    }

    public ArrayList<HarborPoint> createHarbors(FileInputStream harborStream) {
        BufferedReader harborReader = CatanFileReader.getBufferedReaderFromFileName(harborStream);
        return CatanFileReader.readHarborsFromFile(harborReader);
    }

    private void initializeBoardFromStreams(BoardDataInputs dataInputs) {
        ArrayList<CityPoint> originalCityPoints = createCities(
                dataInputs.getCityCoordsStream(),
                dataInputs.getCityTerrainsStream(),
                dataInputs.getCityValuesStream()
        );
        ArrayList<HarborPoint> harborPoints = createHarbors(dataInputs.getHarborsStream());
        ArrayList<RoadPoint> localRoadPoints = createRoads(dataInputs.getRoadCoordsStream());

        addAllCityNeighbors(originalCityPoints, localRoadPoints, dataInputs.getCityNeighborsStream());
        addAllRoadNeighbors(localRoadPoints, originalCityPoints, dataInputs.getRoadNeighborsStream());

        ArrayList<CityPoint> finalCityPoints = new ArrayList<>();
        Map<Point, CityPoint> originalCityMap = new HashMap<>();
        for (CityPoint cp : originalCityPoints) {
            originalCityMap.put(new Point(cp.getX(), cp.getY()), cp);
        }
        Map<Point, HarborPoint> harborMap = new HashMap<>();
        for (HarborPoint hp : harborPoints) {
            harborMap.put(new Point(hp.getX(), hp.getY()), hp);
        }
        Set<Point> addedLocations = new HashSet<>();
        for (Map.Entry<Point, HarborPoint> harborEntry : harborMap.entrySet()) {
            Point location = harborEntry.getKey();
            HarborPoint harbor = harborEntry.getValue();
            CityPoint originalCity = originalCityMap.get(location);
                harbor.setTileValues(originalCity.getTileValues(), originalCity.getTerrains());
                harbor.neighbors.addAll(originalCity.neighbors);

            finalCityPoints.add(harbor);
            addedLocations.add(location);
        }
        for (Map.Entry<Point, CityPoint> cityEntry : originalCityMap.entrySet()) {
            Point location = cityEntry.getKey();
            if (!addedLocations.contains(location)) {
                finalCityPoints.add(cityEntry.getValue());
            }
        }

        Map<Point, CityPoint> finalCityMap = new HashMap<>();
        for (CityPoint cp : finalCityPoints) {
            finalCityMap.put(new Point(cp.getX(), cp.getY()), cp);
        }
        for (RoadPoint road : localRoadPoints) {
            ArrayList<CityPoint> updatedNeighbors = new ArrayList<>();
            for (CityPoint originalNeighbor : road.neighbors) {
                Point neighborLocation = new Point(originalNeighbor.getX(), originalNeighbor.getY());
                CityPoint finalNeighbor = finalCityMap.get(neighborLocation);
                    updatedNeighbors.add(finalNeighbor);
            }
            road.neighbors.clear();
            road.neighbors.addAll(updatedNeighbors);
        }

        this.cityPoints = finalCityPoints;
        this.roadPoints = localRoadPoints;
        this.robberPoints = createRobberPoints(
                dataInputs.getRobberCoordsStream(),
                dataInputs.getRobberResourceStream(),
                dataInputs.getRobberNumberStream()
        );
    }

    public void addAllCityNeighbors(ArrayList<CityPoint> cityPoints, ArrayList<RoadPoint> roadPoints, FileInputStream neighborsStream) {
        BufferedReader bufferedReader = CatanFileReader.getBufferedReaderFromFileName(neighborsStream);

        ArrayList<Set<Integer>> cityNeighbors = CatanFileReader.readNeighborsFromFile(bufferedReader);
        for (int i = 0; i < NUM_CITYPOINTS; i++) {
            for (int neighbor : cityNeighbors.get(i)) {
                cityPoints.get(i).addNeighbor(roadPoints.get(neighbor));
            }
        }
    }

    public void addAllRoadNeighbors(ArrayList<RoadPoint> roadPoints, ArrayList<CityPoint> cityPoints, FileInputStream neighborsStream) {
        BufferedReader bufferedReader = CatanFileReader.getBufferedReaderFromFileName(neighborsStream);

        ArrayList<Set<Integer>> roadNeighbors = CatanFileReader.readNeighborsFromFile(bufferedReader);
        for (int i = 0; i < NUM_ROADPOINTS; i++) {
            for (int neighbor : roadNeighbors.get(i)) {
                roadPoints.get(i).addNeighbor(cityPoints.get(neighbor));
            }
        }
    }

    public void addAllCities(ArrayList<CityPoint> cityPoints) {
        for (CityPoint cityPoint : cityPoints) {
            this.gameWindowController.placeCityButton(this, cityPoint.getX(), cityPoint.getY());
        }
    }

    public void addAllRoadPoints(ArrayList<RoadPoint> roadPoints) {
        for (RoadPoint roadPoint : roadPoints) {
            this.gameWindowController.placeRoadButton(this, roadPoint.getX(), roadPoint.getY());

        }
    }

    public void showInitialTurnState() {
        Turn turn = turnStateMachine.getTurn();
        TurnStateData turnData = new TurnStateData(turn, INITIAL_SETTLEMENTS, INITIAL_ROADS, INITIAL_VICTORY_POINTS);
        this.gameWindowController.showInitialTurnState(turnData);
    }

    public void startGame() {
        this.gameWindowController.startGame();
    }

    public Turn getTurn() {
        return this.turnStateMachine.getTurn();
    }

    public void nextTurn() {
        this.turnStateMachine.nextTurn();
    }

    public int getPlayersSettlements(Turn player) {
        return turnToPlayer.get(player).settlements;
    }

    public int getPlayersRoads(Turn player) {
        return turnToPlayer.get(player).roads;
    }

    public void onCityPointClick(int x, int y) {
        Turn currentTurn = this.turnStateMachine.getTurn();
        int round = turnStateMachine.getRound();
        Player player = turnToPlayer.get(currentTurn);
        if (isInvalidCityPointClick(player, round)) {
            return;
        } else {
            CityPoint currentCity = getCityAtCoords(x, y);
            if (currentCity.hasSettlement()) {
                handleUpgradeSettlement(x, y, player, currentTurn, round, currentCity);
            } else {
                handlePlacement(x, y, player, round, currentCity);
                updateLongestRoad();
            }
        }
    }

    private boolean isInvalidCityPointClick(Player player, int round) {
        if (player.settlements < 4 && round <= 2) {
            return true;
        } else if (player.settlements == 4 && round <= 1) {
            return true;
        } else if ((round > 2) && !player.canPayForSettlement() && !player.canPayToUpgradeSettlement()) {
            return true;
        } else if ((round > 2) && !turnStateMachine.getHasRolled()) {
            return true;
        } else if(!robberMoved) {
            return true;
        }
        return false;
    }

    private void handleUpgradeSettlement(int x, int y, Player player, Turn currentTurn, int round, CityPoint currentCity) {
        if (round <= 2) {
            return;
        }

        if (currentTurn != currentCity.owner) {
            return;
        }

        player.payToUpgradeSettlement();

        currentCity.isCity = true;
        player.addVictoryPoints(1);
        gameWindowController.showCity(player.color, x, y);
        TurnStateData updatedTurnData = new TurnStateData(player.color, player.settlements, player.roads, player.getVictoryPoints());
        gameWindowController.showInitialTurnState(updatedTurnData);
        gameWindowController.showResourceCards(this, playerResourcesMap(player));
    }

    private void handlePlacement(int x, int y, Player player, int round, CityPoint currentCity) {
        if (pointTooCloseToSettlement(currentCity)) {
            return;
        }

        if (round > 2) {
            player.payForSettlement();
        }

        for (CityPoint cityPoint : cityPoints) {
            if (cityPoint.getX() == x && cityPoint.getY() == y) {
                placeSettlement(cityPoint, player, round);
            }
        }
    }

    private boolean pointTooCloseToSettlement(CityPoint currentCity) {
        for (CityPoint cityPoint : cityPoints) {
            if (cityPoint.hasSettlement()) {
                if (withinTwoRoads(cityPoint, currentCity)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void placeSettlement(CityPoint cityPoint, Player player, int round) {
        cityPoint.placeSettlement(player.color);
        player.addVictoryPoints(1);
        if (round == 2) {
            giveResourcesOnPlacingSettlement(player, cityPoint);
        }

        player.settlements--;

        gameWindowController.showSettlement(player.color, cityPoint.getX(), cityPoint.getY());
        TurnStateData updatedTurnData = new TurnStateData(player.color, player.settlements, player.roads, player.getVictoryPoints());
        gameWindowController.showInitialTurnState(updatedTurnData);
        gameWindowController.showResourceCards(this, playerResourcesMap(player));
    }

    private void giveResourcesOnPlacingSettlement(Player player, CityPoint cityPoint) {
        ArrayList<ResourceCard> resources = cityPoint.gatherResources();
        for (ResourceCard card : resources) {
            ResourceType cardType = card.getResourceType();
            player.addResources(cardType, 1);
        }
        showResources();
    }

    public void onRoadPointClick(int x, int y) {
        Turn currentTurn = this.turnStateMachine.getTurn();
        Player player = turnToPlayer.get(currentTurn);
        int round = turnStateMachine.getRound();
        boolean adjacentRoadOwned = false;

        if (isInvalidRoadPointClick(player, round)) {
            return;
        }

        for (CityPoint cityPoint : getRoadAtCoords(x, y).neighbors) {
            adjacentRoadOwned = checkAdjacentRoadOwned(cityPoint, currentTurn);
            if (cityPoint.getOwner().equals(currentTurn) || (cityPoint.getOwner().equals(Turn.NONE) && adjacentRoadOwned)) {
                for (RoadPoint roadPoint : roadPoints) {
                    if (roadPoint.getX() == x && roadPoint.getY() == y) {
                        handlePlaceRoad(roadPoint, currentTurn, player, round);
                    }
                }
            }
        }
    }

    private boolean checkAdjacentRoadOwned(CityPoint cityPoint, Turn currentTurn) {
        for (RoadPoint neighborPoint : cityPoint.neighbors) {
            if (neighborPoint.getOwner().equals(currentTurn)) {
                return true;
            }
        }
        return false;
    }

    private boolean isInvalidRoadPointClick(Player player, int round) {
        if (player.roads < 14 && round <= 2) {
            return true;
        } else if (player.roads == 14 && round <= 1) {
            return true;
        } else if (((round > 2) && !player.canPayForRoad()) && (player.getFreeRoads() == 0)) {
            return true;
        } else if ((round > 2) && !turnStateMachine.getHasRolled()) {
            return true;
        } else if (!robberMoved) {
            return true;
        }

        return false;
    }

    private void handlePlaceRoad(RoadPoint roadPoint, Turn currentTurn, Player player, int round) {
        if (roadPoint.hasRoad) {
            return;
        }
        roadPoint.placeRoad(currentTurn);
        updateLongestRoad();
        if (player.getFreeRoads() > 0) {
            player.removeFreeRoads(1);
        } else if (round > 2) {
            player.payForRoad();
        }

        player.roads--;

        gameWindowController.showRoad(player.color, roadPoint.getX(), roadPoint.getY());
        TurnStateData updatedTurnData = new TurnStateData(player.color, player.settlements, player.roads,player.getVictoryPoints());
        gameWindowController.showInitialTurnState(updatedTurnData);
        gameWindowController.showResourceCards(this, playerResourcesMap(player));
    }

    void updateLongestRoad() {
        for(Turn turnToCheck: turnToPlayer.keySet()) {
            int longestPathLen = 1;

            for (RoadPoint road : roadPoints) {
                if (road.getOwner().equals(turnToCheck)) {
                    longestPathLen = Math.max(longestPathLen, calculateRoadLen(road, turnToCheck));
                }
            }
            longestRoad.put(turnToCheck, longestPathLen);
            for (Turn turn : longestRoad.keySet()) {
                // tested by hand, changing conditional boundaries causes tests to fail, so there is an equivalent mutant here
                if (longestRoad.get(turn) > currentLongestRoadLength && longestRoad.get(turn) >= MIN_LONGEST_ROAD_LEN) {
                    handleGiveNewPlayerLongestRoad(turn);
                } else if (longestRoad.get(turn) < MIN_LONGEST_ROAD_LEN && turn == currentLongestRoadPlayer){
                    handleLongestRoadBlockedBySettlement();
                }
            }
        }
    }

    private void handleGiveNewPlayerLongestRoad(Turn turn) {
        if (turn == currentLongestRoadPlayer) {
            return;
        }
        Player longestRoadPlayer = turnToPlayer.get(currentLongestRoadPlayer);
        longestRoadPlayer.addVictoryPoints(-LONGEST_ROAD_BONUS);

        Player newLongestRoadPlayer = turnToPlayer.get(turn);
        newLongestRoadPlayer.addVictoryPoints(LONGEST_ROAD_BONUS);

        currentLongestRoadPlayer = turn;
        currentLongestRoadLength = longestRoad.get(turn);
    }

    private void handleLongestRoadBlockedBySettlement() {
        Player longestRoadPlayer = turnToPlayer.get(currentLongestRoadPlayer);
        longestRoadPlayer.addVictoryPoints(-LONGEST_ROAD_BONUS);
        currentLongestRoadPlayer = Turn.BANK;
    }

    private int calculateRoadLen(RoadPoint roadPoint, Turn player) {
        Set<RoadPoint> visited = new HashSet<>();
        return calculateRoadLenHelper(roadPoint, null, player, visited);

    }

    private int calculateRoadLenHelper(RoadPoint road, CityPoint prevCity, Turn player, Set<RoadPoint> visited) {
        visited.add(road);
        int maxLen = 0;
        for (CityPoint neighborCity : road.neighbors) {
            if (!neighborCity.equals(prevCity) && (neighborCity.getOwner().equals(player) || neighborCity.getOwner().equals(Turn.NONE))) {
                for (RoadPoint nextRoad : neighborCity.neighbors) {
                    if (!nextRoad.equals(road) && nextRoad.getOwner().equals(player) && !visited.contains(nextRoad)) {
                        Set<RoadPoint> newVisited = new HashSet<>(visited);
                        int pathLen = calculateRoadLenHelper(nextRoad, neighborCity, player, newVisited);
                        maxLen = Math.max(maxLen, pathLen);
                    }
                }
            }
        }
        return maxLen + 1;
    }

    public RoadPoint getRoadAtCoords(int x, int y) {
        for (RoadPoint roadPoint : roadPoints) {
            if (roadPoint.getX() == x && roadPoint.getY() == y) {
                return roadPoint;
            }
        }
        return new RoadPoint(-1, -1);
    }

    public CityPoint getCityAtCoords(int x, int y) {
        for (CityPoint cityPoint : cityPoints) {
            if (cityPoint.getX() == x && cityPoint.getY() == y) {
                return cityPoint;
            }
        }
        return new CityPoint(-1, -1);
    }

    public void onNextTurnClick() {
        if ( !robberMoved) {
            return;
        }
        int round = turnStateMachine.getRound();
        Turn turn = turnStateMachine.getTurn();
        Player player = turnToPlayer.get(turn);

        if (player.getVictoryPoints() >= VP_WIN_AMOUNT) {
            gameWindowController.gameOver(player);
            return;
        }

        if (isInvalidNextTurnClick(round, player)) {
            return;
        }

        this.gameWindowController.clearDevCards();
        player.removeFreeRoads(player.getFreeRoads());
        turnStateMachine.nextTurn();
        turn = turnStateMachine.getTurn();
        player = turnToPlayer.get(turn);
        showDevCards();
        showResources();
        TurnStateData turnData = new TurnStateData(player.color, player.settlements, player.roads, player.getVictoryPoints());
        this.gameWindowController.showInitialTurnState(turnData);
    }

    private boolean isInvalidNextTurnClick(int round, Player player) {
        if (round == 1 && player.settlements == INITIAL_SETTLEMENTS) {
            return true;
        } else if (round == 1 && player.roads == INITIAL_ROADS) {
            return true;
        } else if (round == 2 && player.settlements == INITIAL_SETTLEMENTS - 1) {
            return true;
        } else if (round == 2 && player.roads == INITIAL_ROADS - 1) {
            return true;
        } else if (round > 2 && !turnStateMachine.getHasRolled()) {
            return true;
        }
        return false;
    }


    public void addNextTurnButton() {
        this.gameWindowController.addNextTurnButton(this);
    }

    public boolean withinTwoRoads(CityPoint cityPoint1, CityPoint cityPoint2) {
        return checkCityNeighbors(cityPoint1, cityPoint2, 0);
    }

    boolean checkRoadNeighbors(RoadPoint roadPoint, CityPoint pointToCheck, int depth) {
        if (depth < 2) {
            ArrayList<Boolean> neighborReturn = new ArrayList<>();
            for (CityPoint cityPoint : roadPoint.neighbors) {
                neighborReturn.add(checkCityNeighbors(cityPoint, pointToCheck, depth));
            }
            for (Boolean b : neighborReturn) {
                if (b) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean checkCityNeighbors(CityPoint neighborCity, CityPoint pointToCheck, int depth) {
        if (depth >= 2) {
            return false;
        } else if (neighborCity.equals(pointToCheck)) {
            return true;
        } else {
            ArrayList<Boolean> neighborReturn = new ArrayList<>();
            for (RoadPoint roadPoint : neighborCity.neighbors) {
                neighborReturn.add(checkRoadNeighbors(roadPoint, pointToCheck, depth + 1));
            }
            for (Boolean b : neighborReturn) {
                if (b) {
                    return true;
                }
            }
            return false;
        }
    }

    public void onRollDiceClick() {
        if (turnStateMachine.getRound() < 3) {
            gameWindowController.showInvalidInputAndPass("Cannot roll dice until third turn");
        } else if (!turnStateMachine.getHasRolled()) {
            numRolled = dice.roll();
            turnStateMachine.hasRolled = true;
            showDiceRoll(numRolled);
            if (numRolled == DISCARD_THRESHOLD) {
                handleTellPlayersToDiscard();
            } else {
                giveResourcesToBorderingSettlements();
            }
        }
    }

    private void handleTellPlayersToDiscard() {
        robberMoved = false;
        for (Player player : turnToPlayer.values()) {
            if (player.needsToDiscard()) {
                showDiscardDialog();
                return;
            }
        }
    }

    protected void giveResourcesToBorderingSettlements() {
        for (CityPoint cityPoint : cityPoints) {
            if (cityPoint.hasSettlement && cityPoint.getTileValues().contains(numRolled)) {
                Terrain terrain = cityPoint.tileValueToTerrain.get(numRolled);
                ResourceType resourceType = terrain.getResourceType();
                Player owner = turnToPlayer.get(cityPoint.owner);
                if (numRolled == robberNumber && robberResource.equals(resourceType)) {
                    continue;
                }
                if(resourceType == ResourceType.WOOD && numRolled == 9){
                    resourceType = ResourceType.SHEEP;
                }
                if (cityPoint.isCity) {
                    owner.addResources(resourceType, 2);
                } else {
                    owner.addResources(resourceType, 1);
                }
            }
        }
        showResources();
    }

    public void addDiceRollButton() {
        this.gameWindowController.addDiceRollButton(this);
    }

    public void showDiceRoll(int diceRoll) {
        gameWindowController.showDiceRoll(diceRoll);
    }

    public void showInitialRobberState() {
        gameWindowController.showInitialRobberState(220, 504);
    }


    public void showTradeDialogue() {
        gameWindowController.showTradeDialogue(this);
    }

    public ArrayList<RobberPoint> createRobberPoints(FileInputStream coordsStream, FileInputStream resourceStream, FileInputStream numberStream) {
        BufferedReader coordinateReader = CatanFileReader.getBufferedReaderFromFileName(coordsStream);
        ArrayList<Point> coordinatePoints = CatanFileReader.readCoordinatesFromFile(coordinateReader);

        BufferedReader resourceReader = CatanFileReader.getBufferedReaderFromFileName(resourceStream);
        ArrayList<ResourceType> resourceTypes = CatanFileReader.readResourceTypesFromFile(resourceReader);

        BufferedReader numberReader = CatanFileReader.getBufferedReaderFromFileName(numberStream);
        ArrayList<Integer> diceNumbers = CatanFileReader.readRobberNumbersFromFile(numberReader);

        ArrayList<RobberPoint> robberPoints = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            Point point = coordinatePoints.get(i);
            ResourceType resourceType = resourceTypes.get(i);
            int diceNumber = diceNumbers.get(i);
            robberPoints.add(new RobberPoint(point.x, point.y, resourceType, diceNumber));
        }
        return robberPoints;
    }

    public void addAllRobberPoints(ArrayList<RobberPoint> robberPoints) {
        for (RobberPoint robberPoint : robberPoints) {
            gameWindowController.placeRobberButton(this, robberPoint.getX(), robberPoint.getY());
        }
    }

    public void buyDevCard() {
        Turn currentTurn = turnStateMachine.getTurn();
        Player player = turnToPlayer.get(currentTurn);
        int drawnType = rand.nextInt(DEV_CARD_AMT);
        if (player.canPayForDevCard()) {
            player.payForDevCard();
            gameWindowController.showResourceCards(this, playerResourcesMap(player));
            if(drawnType == KNIGHT){
                DevelopmentCard cardToAdd = new DevelopmentCard(DevCards.KNIGHT);
                cardToAdd.setTurnBought(turnStateMachine.getRound());
                player.addDevelopmentCard(cardToAdd);
            } else if (drawnType == VICTORY_POINT) {
                player.addDevelopmentCard(new DevelopmentCard(DevCards.VICTORY_POINT));
                player.addVictoryPoints(1);
            } else if (drawnType == YEAR_OF_PLENTY) {
                DevelopmentCard cardToAdd = new DevelopmentCard(DevCards.YEAR_OF_PLENTY);
                cardToAdd.setTurnBought(turnStateMachine.getRound());
                player.addDevelopmentCard(cardToAdd);
            } else if (drawnType == ROAD_BUILDING) {
                DevelopmentCard cardToAdd = new DevelopmentCard(DevCards.ROAD_BUILDING);
                cardToAdd.setTurnBought(turnStateMachine.getRound());
                player.addDevelopmentCard(cardToAdd);
            } else {
                DevelopmentCard cardToAdd = new DevelopmentCard(DevCards.MONOPOLY);
                cardToAdd.setTurnBought(turnStateMachine.getRound());
                player.addDevelopmentCard(cardToAdd);
            }
        }
    }

    public void onBuyDevCardClick() {
        buyDevCard();
        showDevCards();
    }

    public void addBuyDevCardButton() {
        gameWindowController.placeDevCardButton(this);
    }

    public void showDevCards() {
        Turn currentTurn = turnStateMachine.getTurn();
        Player player = turnToPlayer.get(currentTurn);
        gameWindowController.showDevCards(this, player.getDevCards());

    }

    public void onRobberPointClick(int x, int y) {
        if (numRolled != DISCARD_THRESHOLD && !knightUsed) {
            return;
        } else if (robberMoved) {
            return;
        }
        for (RobberPoint robberPoint : robberPoints) {
            if (robberPoint.getX() == x && robberPoint.getY() == y && !robberPoint.hasRobber) {
                robberPoint.hasRobber = true;
                robberResource = robberPoint.resourceType;
                robberNumber = robberPoint.diceNumber;
                robberMoved = ROBBER_ALREADY_MOVED;
                knightUsed = false;

                gameWindowController.showInitialRobberState(x, y);
                eligiblePlayers = getEligiblePlayersToRob(robberPoint);
                if (eligiblePlayers.size() > 0) {

                    gameWindowController.showStealDialog(this, eligiblePlayers);
                }
            }
            else{
                robberPoint.hasRobber = false;
            }
        }
    }

    public void robCardFromPlayer(Turn robbedTurn, Random random) {
        Turn currentTurn = turnStateMachine.getTurn();
        Player currentPlayer = turnToPlayer.get(currentTurn);
        Player robbedPlayer = turnToPlayer.get(robbedTurn);
        ArrayList<ResourceType> cardsAsList = robbedPlayer.getResourcesAsList();

        int cardStolen = random.nextInt(cardsAsList.size());
        ResourceType resourceToSteal = cardsAsList.get(cardStolen);
        currentPlayer.addResources(resourceToSteal, 1);
        robbedPlayer.subResources(resourceToSteal, 1);

        gameWindowController.showResourceCards(this, playerResourcesMap(currentPlayer));
        gameWindowController.removeStealDialog();
    }

    Set<Player> getEligiblePlayersToRob(RobberPoint robberPoint) {
        Set<Player> players = new HashSet<>();
        Turn turn = turnStateMachine.getTurn();
        for (CityPoint cityPoint : cityPoints) {
            if (!cityPoint.hasSettlement) {
                continue;
            }
            for (Integer tileNum : cityPoint.tileValueToTerrain.keySet()) {
                Terrain terrain = cityPoint.tileValueToTerrain.get(tileNum);

                if (tileNum == robberPoint.diceNumber && terrain.getResourceType().equals(robberPoint.resourceType)) {
                    players.add(turnToPlayer.get(cityPoint.owner));
                }
            }
        }
        players.remove(turnToPlayer.get(turn));
        return players;
    }


    public void showResources() {
        Turn currentTurn = turnStateMachine.getTurn();
        Player player = turnToPlayer.get(currentTurn);
        gameWindowController.showResourceCards(this, playerResourcesMap(player));
    }

    public HashMap<ResourceType, Integer> playerResourcesMap(Player player) {
        HashMap<ResourceType, Integer> resourceMap = new HashMap<>();
        for (ResourceType type : ResourceType.values()) {
            if (type != ResourceType.NULL) {
                resourceMap.put(type, player.getResource(type));
            }
        }
        return resourceMap;
    }

    public void onTradeSubmitClick(TradeInfo offer1, TradeInfo offer2) {
        if (!turnStateMachine.getHasRolled()) {
            return;
        } else if (!robberMoved) {
            return;
        }
        Turn currentTurn = turnStateMachine.getTurn();
        Player player1 = turnToPlayer.get(currentTurn);
        Player player2 = turnToPlayer.get(offer2.getPlayer());

        HashMap<ResourceType, Integer> tradeResources1 = offer1.getResources();
        HashMap<ResourceType, Integer> tradeResources2 = offer2.getResources();

        if (!player1.canAfford(tradeResources1) || !player2.canAfford(tradeResources2)) {
            return;
        }

        for (ResourceType resource : tradeResources1.keySet()) {
            int num = tradeResources1.get(resource);
            player1.subResources(resource, num);
            player2.addResources(resource, num);
        }

        for (ResourceType resource : tradeResources2.keySet()) {
            int num = tradeResources2.get(resource);
            player2.subResources(resource, num);
            player1.addResources(resource, num);
        }
        showResources();
    }

    public void showDiscardDialog() {
        gameWindowController.showDiscardDialog(this);
    }

    public void closeDiscardDialog() {
        for (Player player : turnToPlayer.values()) {
            if (player.needsToDiscard()) {
                return;
            }
        }
        gameWindowController.hideDiscardDialog();
    }

    public HashMap<Turn, HashMap<ResourceType, Integer>> getAllPlayersResourceMaps() {
        HashMap<Turn, HashMap<ResourceType, Integer>> maps = new HashMap<>();
        for (Turn turn : turnToPlayer.keySet()) {
            if (turn != Turn.BANK) {
                maps.put(turn, playerResourcesMap(turnToPlayer.get(turn)));
            }
        }
        return maps;
    }

    public void onSubmitDiscard(HashMap<Turn, HashMap<ResourceType, Integer>> allDiscards) {

        for (var entry : allDiscards.entrySet()) {
            Turn turn = entry.getKey();
            Player player = turnToPlayer.get(turn);
            player.discardHalf(entry.getValue());
        }
        boolean anyoneLeft = false;
        for (Player player : turnToPlayer.values()) {
            if (player.needsToDiscard()) {
                anyoneLeft = true;
                break;
            }
        }
        if (!anyoneLeft) {
            gameWindowController.hideDiscardDialog();
            showResources();
        }
    }

    public void onYoPClick() {
        Turn curTurn = turnStateMachine.getTurn();
        int round = turnStateMachine.getRound();
        Player player = turnToPlayer.get(curTurn);
        for (DevelopmentCard devCard : player.getDevCards()) {
            if (devCard.getType() == DevCards.YEAR_OF_PLENTY) {
                if (devCard.getTurnBought() < round) {
                    player.removeDevelopmentCard(devCard);
                    gameWindowController.openYoPTradeMenu(this, curTurn);
                    showDevCards();
                    return;
                }
            }
        }
    }

    public void onRoadBuildingClick() {
        Turn curTurn = turnStateMachine.getTurn();
        int round = turnStateMachine.getRound();
        Player player = turnToPlayer.get(curTurn);
        for(DevelopmentCard devCard : player.getDevCards()){
            if(devCard.getType() == DevCards.ROAD_BUILDING){
                if(devCard.getTurnBought() < round) {
                    player.removeDevelopmentCard(devCard);
                    player.addFreeRoads(2);
                    showDevCards();
                    return;
                }
            }
        }
    }

    public void onBankSubmitClick(TradeInfo playerTrade, TradeInfo bankTrade) {
        playerTrade.setPlayer(turnStateMachine.getTurn());
        Player player1 = turnToPlayer.get(playerTrade.getPlayer());

        HashMap<ResourceType, Integer> playerResources = playerTrade.getResources();
        HashMap<ResourceType, Integer> bankResources = bankTrade.getResources();

        if (!player1.canAfford(playerResources)) {
            return;
        }

        boolean hasGenericHarbor = false;
        Set<ResourceType> specificHarborTypes = new HashSet<>();

        if (this.cityPoints != null) {
            for (CityPoint cp : this.cityPoints) {
                if (cp.getOwner() == player1.color) {
                    if (cp instanceof HarborPoint harborPoint) {
                        if (harborPoint.isGeneric()) {
                            hasGenericHarbor = true;
                        } else if ( harborPoint.getTradingResource() != ResourceType.NULL) {
                            specificHarborTypes.add(harborPoint.getTradingResource());
                        }
                    }
                }
            }
        }

        int totalValueOffered = 0;

        for (ResourceType resource : playerResources.keySet()) {
            int numToGive = playerResources.get(resource);

            int rate = BANK_TRADE_VALUE;
            if (specificHarborTypes.contains(resource)) {
                rate = HARBOR_TRADE_VALUE;
            } else if(hasGenericHarbor){
                rate = GENERIC_TRADE_VALUE;
            }
            if(numToGive % rate != 0){
                return;
            }
            totalValueOffered += (numToGive / rate);
        }

        int numToGet = 0;
        for (ResourceType resource : bankResources.keySet()) {
            int num = bankResources.get(resource);
            numToGet += num;
        }


        if (totalValueOffered != numToGet) {
            return;
        }

        for (ResourceType resource : playerResources.keySet()) {
            int num = playerResources.get(resource);
              player1.subResources(resource, num);
        }

            for (ResourceType resource : bankResources.keySet()) {
                int num = bankResources.get(resource);

                    player1.addResources(resource, num);
        }
        showResources();
    }

    public void onKnightCardClick() {
        Turn turn = turnStateMachine.getTurn();
        int round = turnStateMachine.getRound();
        Player player = turnToPlayer.get(turn);
        for(DevelopmentCard devCard : player.getDevCards()){
            if(devCard.getType() == DevCards.KNIGHT){
                if(devCard.getTurnBought() < round) {
                    player.numKnightsPlayed++;
                    player.removeDevelopmentCard(devCard);
                    knightUsed = true;
                    robberMoved = false;
                    checkLargestArmy();
                    return;
                }
            }
        }
    }

    private void checkLargestArmy(){
        ArrayList<Integer> knightsPlayed = new ArrayList<>();
        for(Player player : turnToPlayer.values()){
            knightsPlayed.add(player.numKnightsPlayed);
        }
        int mostPlayed = Collections.max(knightsPlayed);
        for(Player player : turnToPlayer.values()){
            if(player.hasLargestArmy && player.numKnightsPlayed == mostPlayed){
                return;
            }
        }
        if(mostPlayed >= MINIMUM_ARMY) {
            for (Player player : turnToPlayer.values()) {
                if (player.numKnightsPlayed == mostPlayed) {
                    if(!player.hasLargestArmy){
                        player.addVictoryPoints(ARMY_VP);
                        player.hasLargestArmy = true;
                    }
                } else {
                    if (player.hasLargestArmy) {
                        player.addVictoryPoints(-ARMY_VP);
                    }
                    player.hasLargestArmy = false;
                }
            }
        }
    }

    public void onMonopolyClick() {
        Turn curTurn = turnStateMachine.getTurn();
        int round = turnStateMachine.getRound();
        Player player = turnToPlayer.get(curTurn);
        for (DevelopmentCard devCard : player.getDevCards()) {
            if (devCard.getType() == DevCards.MONOPOLY) {
                if (devCard.getTurnBought() < round) {
                    gameWindowController.openMonopolyMenu(this, curTurn);
                }
            }
        }
    }


    public void executeMonopoly(Turn stealingTurn, ResourceType selectedResource) {
        Player stealingPlayer = turnToPlayer.get(stealingTurn);

        int totalStolen = 0;
        for (Map.Entry<Turn, Player> entry : turnToPlayer.entrySet()) {
            Turn otherTurn = entry.getKey();
            Player otherPlayer = entry.getValue();

            if (otherTurn == stealingTurn || otherTurn == Turn.BANK) continue;

            int amount = otherPlayer.getResource(selectedResource);
                otherPlayer.subResources(selectedResource, amount);
                totalStolen += amount;
        }
        stealingPlayer.addResources(selectedResource, totalStolen);

        for (DevelopmentCard card : new ArrayList<>(stealingPlayer.getDevCards())) {
            if (card.getType() == DevCards.MONOPOLY) {
                stealingPlayer.removeDevelopmentCard(card);
                break;
            }
        }
        showResources();
    }
}
