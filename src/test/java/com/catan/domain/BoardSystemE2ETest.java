// package com.catan.domain;

// import com.catan.datasource.BoardDataInputs;
// import org.junit.jupiter.api.Test;

// import java.io.FileInputStream;
// import java.io.IOException;
// import java.util.ArrayDeque;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Random;
// import java.util.Set;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertNotEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// public class BoardSystemE2ETest {

//     @Test
//     public void fullGameplaySystemFlowTouchesCoreGameMechanics() throws IOException {
//         RecordingGameWindowController controller = new RecordingGameWindowController();
//         ScriptedDice dice = new ScriptedDice();
//         TurnStateMachine turnStateMachine = new TurnStateMachine();
//         Board board = new Board(controller, turnStateMachine, dice);

//         loadBoardDataFromFiles(board);

//         assertEquals(Board.NUM_CITYPOINTS, board.cityPoints.size());
//         assertEquals(Board.NUM_ROADPOINTS, board.roadPoints.size());
//         assertEquals(19, board.robberPoints.size());

//         board.addAllCities(board.cityPoints);
//         board.addAllRoadPoints(board.roadPoints);
//         board.addAllRobberPoints(board.robberPoints);
//         board.showInitialTurnState();
//         board.showInitialRobberState();

//         assertEquals(Board.NUM_CITYPOINTS, controller.cityButtonCount);
//         assertEquals(Board.NUM_ROADPOINTS, controller.roadButtonCount);
//         assertEquals(19, controller.robberButtonCount);
//         assertEquals(Turn.RED, controller.lastTurnState.turn);
//         assertEquals(Board.INITIAL_SETTLEMENTS, controller.lastTurnState.settlements);
//         assertEquals(Board.INITIAL_ROADS, controller.lastTurnState.roads);
//         assertEquals(Board.INITIAL_VICTORY_POINTS, controller.lastTurnState.victoryPoints);
//         assertEquals(220, controller.lastRobberX);
//         assertEquals(504, controller.lastRobberY);

//         int safetyCounter = 0;
//         while (turnStateMachine.getRound() < 3) {
//             completeCurrentInitialPlacementTurn(board);
//             safetyCounter++;
//             assertTrue(safetyCounter < 20, "Initial placement phase did not complete");
//         }

//         assertEquals(3, turnStateMachine.getRound());
//         assertEquals(Turn.RED, board.getTurn());
//         assertFalse(turnStateMachine.getHasRolled());

//         Player redPlayer = board.turnToPlayer.get(Turn.RED);
//         Player bluePlayer = board.turnToPlayer.get(Turn.BLUE);
//         CityPoint redSettlement = findAnyOwnedSettlement(board, Turn.RED);
//         int nonDesertRoll = findAnyNonDesertRollValue(redSettlement);

//         board.robberNumber = -1;
//         int redTotalBeforeResourceRoll = redPlayer.getTotalResources();
//         dice.enqueueRoll(nonDesertRoll);
//         board.onRollDiceClick();

//         assertTrue(turnStateMachine.getHasRolled());
//         assertTrue(redPlayer.getTotalResources() > redTotalBeforeResourceRoll);

//         redPlayer.addResources(ResourceType.WHEAT, 2);
//         redPlayer.addResources(ResourceType.ORE, 3);
//         int redVpBeforeCityUpgrade = redPlayer.getVictoryPoints();
//         board.onCityPointClick(redSettlement.getX(), redSettlement.getY());
//         assertTrue(redSettlement.isCity);
//         assertEquals(redVpBeforeCityUpgrade + 1, redPlayer.getVictoryPoints());

//         redPlayer.addResources(ResourceType.WHEAT, 1);
//         redPlayer.addResources(ResourceType.ORE, 1);
//         redPlayer.addResources(ResourceType.SHEEP, 1);
//         int devCardsBefore = redPlayer.getDevCards().size();
//         int wheatBeforeDevBuy = redPlayer.getResource(ResourceType.WHEAT);
//         int oreBeforeDevBuy = redPlayer.getResource(ResourceType.ORE);
//         int sheepBeforeDevBuy = redPlayer.getResource(ResourceType.SHEEP);
//         board.rand = new Random(0);
//         board.onBuyDevCardClick();
//         assertEquals(devCardsBefore + 1, redPlayer.getDevCards().size());
//         assertEquals(wheatBeforeDevBuy - 1, redPlayer.getResource(ResourceType.WHEAT));
//         assertEquals(oreBeforeDevBuy - 1, redPlayer.getResource(ResourceType.ORE));
//         assertEquals(sheepBeforeDevBuy - 1, redPlayer.getResource(ResourceType.SHEEP));

//         redPlayer.addResources(ResourceType.WOOD, 1);
//         bluePlayer.addResources(ResourceType.BRICK, 1);
//         int redWoodBeforePlayerTrade = redPlayer.getResource(ResourceType.WOOD);
//         int redBrickBeforePlayerTrade = redPlayer.getResource(ResourceType.BRICK);
//         int blueWoodBeforePlayerTrade = bluePlayer.getResource(ResourceType.WOOD);
//         int blueBrickBeforePlayerTrade = bluePlayer.getResource(ResourceType.BRICK);
//         TradeInfo playerOffer = new TradeInfo();
//         playerOffer.setResources(ResourceType.WOOD, 1);
//         TradeInfo playerReceive = new TradeInfo();
//         playerReceive.setPlayer(Turn.BLUE);
//         playerReceive.setResources(ResourceType.BRICK, 1);
//         board.onTradeSubmitClick(playerOffer, playerReceive);
//         assertEquals(redWoodBeforePlayerTrade - 1, redPlayer.getResource(ResourceType.WOOD));
//         assertEquals(redBrickBeforePlayerTrade + 1, redPlayer.getResource(ResourceType.BRICK));
//         assertEquals(blueWoodBeforePlayerTrade + 1, bluePlayer.getResource(ResourceType.WOOD));
//         assertEquals(blueBrickBeforePlayerTrade - 1, bluePlayer.getResource(ResourceType.BRICK));

//         ResourceType baselineBankOffered = ResourceType.WOOD;
//         int baselineBankRate = determineBankTradeRate(board, redPlayer, baselineBankOffered);
//         redPlayer.addResources(baselineBankOffered, baselineBankRate);
//         int redWoodBeforeBankTrade = redPlayer.getResource(ResourceType.WOOD);
//         int redOreBeforeBankTrade = redPlayer.getResource(ResourceType.ORE);
//         TradeInfo bankOfferNoHarbor = new TradeInfo();
//         bankOfferNoHarbor.setResources(baselineBankOffered, baselineBankRate);
//         TradeInfo bankReceiveNoHarbor = new TradeInfo();
//         bankReceiveNoHarbor.setPlayer(Turn.BANK);
//         bankReceiveNoHarbor.setResources(ResourceType.ORE, 1);
//         board.onBankSubmitClick(bankOfferNoHarbor, bankReceiveNoHarbor);
//         assertEquals(redWoodBeforeBankTrade - baselineBankRate, redPlayer.getResource(ResourceType.WOOD));
//         assertEquals(redOreBeforeBankTrade + 1, redPlayer.getResource(ResourceType.ORE));

//         redPlayer.addResources(ResourceType.WOOD, 1);
//         redPlayer.addResources(ResourceType.BRICK, 1);
//         redPlayer.addResources(ResourceType.SHEEP, 1);
//         redPlayer.addResources(ResourceType.WHEAT, 1);
//         HarborPoint ownedHarbor = placeAnyAvailableHarborSettlement(board, redPlayer);
//         assertNotNull(ownedHarbor);
//         assertEquals(Turn.RED, ownedHarbor.getOwner());

//         TradeInfo harborOffer = new TradeInfo();
//         TradeInfo harborReceive = new TradeInfo();
//         harborReceive.setPlayer(Turn.BANK);
//         ResourceType harborRequested = ResourceType.ORE;
//         int harborRate;
//         ResourceType harborOffered;
//         if (ownedHarbor.isGeneric()) {
//             harborRate = Board.GENERIC_TRADE_VALUE;
//             harborOffered = ResourceType.BRICK;
//         } else {
//             harborRate = Board.HARBOR_TRADE_VALUE;
//             harborOffered = ownedHarbor.getTradingResource();
//             if (harborOffered == ResourceType.ORE) {
//                 harborRequested = ResourceType.WHEAT;
//             }
//         }
//         redPlayer.addResources(harborOffered, harborRate);
//         int offeredBeforeHarborTrade = redPlayer.getResource(harborOffered);
//         int requestedBeforeHarborTrade = redPlayer.getResource(harborRequested);
//         harborOffer.setResources(harborOffered, harborRate);
//         harborReceive.setResources(harborRequested, 1);
//         board.onBankSubmitClick(harborOffer, harborReceive);
//         assertEquals(offeredBeforeHarborTrade - harborRate, redPlayer.getResource(harborOffered));
//         assertEquals(requestedBeforeHarborTrade + 1, redPlayer.getResource(harborRequested));

//         board.onNextTurnClick();
//         assertEquals(Turn.BLUE, board.getTurn());
//         assertFalse(turnStateMachine.getHasRolled());

//         bluePlayer.addResources(ResourceType.WOOD, 12);
//         int blueTotalBeforeDiscard = bluePlayer.getTotalResources();
//         dice.enqueueRoll(Board.DISCARD_THRESHOLD);
//         board.onRollDiceClick();
//         assertEquals(Board.DISCARD_THRESHOLD, board.numRolled);
//         assertFalse(board.robberMoved);
//         assertTrue(controller.discardDialogShownCount > 0);

//         RobberPoint robberTarget = board.robberPoints.getFirst();
//         board.onRobberPointClick(robberTarget.getX(), robberTarget.getY());
//         assertTrue(board.robberMoved);
//         assertEquals(robberTarget.getX(), controller.lastRobberX);
//         assertEquals(robberTarget.getY(), controller.lastRobberY);

//         int requiredDiscard = bluePlayer.getRequiredDiscardCount();
//         HashMap<ResourceType, Integer> blueDiscard = new TradeInfo().getResources();
//         blueDiscard.put(ResourceType.WOOD, requiredDiscard);
//         HashMap<Turn, HashMap<ResourceType, Integer>> allDiscards = new HashMap<>();
//         allDiscards.put(Turn.BLUE, blueDiscard);
//         board.onSubmitDiscard(allDiscards);
//         assertEquals(blueTotalBeforeDiscard - requiredDiscard, bluePlayer.getTotalResources());

//         assertTrue(controller.placementsShown.contains("settlement"));
//         assertTrue(controller.placementsShown.contains("road"));
//         assertTrue(controller.placementsShown.contains("city"));
//         assertNotEquals(0, controller.showResourcesCallCount);
//     }

//     private void loadBoardDataFromFiles(Board board) throws IOException {
//         try (FileInputStream cityCoords = new FileInputStream(Board.CITY_COORDINATES_FILE_PATH);
//              FileInputStream cityTerrains = new FileInputStream(Board.TERRAIN_COORDINATES_FILE_PATH);
//              FileInputStream cityValues = new FileInputStream(Board.TILE_VALUE_COORDINATES_FILE_PATH);
//              FileInputStream harbors = new FileInputStream(Board.HARBORS_FILE_PATH);
//              FileInputStream roadCoords = new FileInputStream(Board.ROAD_COORDINATES_FILE_PATH);
//              FileInputStream cityNeighbors = new FileInputStream(Board.CITY_NEIGHBORS_FILEPATH);
//              FileInputStream roadNeighbors = new FileInputStream(Board.ROAD_NEIGHBORS_FILEPATH);
//              FileInputStream robberCoords = new FileInputStream(Board.ROBBER_COORDINATES_FILE_PATH);
//              FileInputStream robberResources = new FileInputStream(Board.ROBBER_RESOURCE_FILE_PATH);
//              FileInputStream robberNumbers = new FileInputStream(Board.ROBBER_NUMBER_FILE_PATH)) {

//             BoardDataInputs dataInputs = new BoardDataInputs(
//                     cityCoords, cityTerrains, cityValues, harbors, roadCoords, cityNeighbors,
//                     roadNeighbors, robberCoords, robberResources, robberNumbers
//             );
//             board.loadBoardData(dataInputs);
//         }
//     }

//     private void completeCurrentInitialPlacementTurn(Board board) {
//         Player currentPlayer = board.turnToPlayer.get(board.getTurn());

//         int settlementsBefore = currentPlayer.settlements;
//         boolean settlementPlaced = false;
//         for (CityPoint cityPoint : board.cityPoints) {
//             board.onCityPointClick(cityPoint.getX(), cityPoint.getY());
//             if (currentPlayer.settlements == settlementsBefore - 1) {
//                 settlementPlaced = true;
//                 break;
//             }
//         }
//         assertTrue(settlementPlaced, "Could not place settlement for " + currentPlayer.color);

//         int roadsBefore = currentPlayer.roads;
//         boolean roadPlaced = false;
//         for (RoadPoint roadPoint : board.roadPoints) {
//             board.onRoadPointClick(roadPoint.getX(), roadPoint.getY());
//             if (currentPlayer.roads == roadsBefore - 1) {
//                 roadPlaced = true;
//                 break;
//             }
//         }
//         assertTrue(roadPlaced, "Could not place road for " + currentPlayer.color);

//         board.onNextTurnClick();
//     }

//     private CityPoint findAnyOwnedSettlement(Board board, Turn owner) {
//         for (CityPoint cityPoint : board.cityPoints) {
//             if (cityPoint.getOwner() == owner && cityPoint.hasSettlement()) {
//                 return cityPoint;
//             }
//         }
//         throw new AssertionError("No owned settlement found for " + owner);
//     }

//     private int findAnyNonDesertRollValue(CityPoint cityPoint) {
//         for (Map.Entry<Integer, Terrain> entry : cityPoint.tileValueToTerrain.entrySet()) {
//             if (entry.getValue() != Terrain.DESERT) {
//                 return entry.getKey();
//             }
//         }
//         throw new AssertionError("No non-desert tile value available for city");
//     }

//     private HarborPoint placeAnyAvailableHarborSettlement(Board board, Player player) {
//         int settlementsBefore = player.settlements;
//         for (CityPoint cityPoint : board.cityPoints) {
//             if (cityPoint instanceof HarborPoint harborPoint && cityPoint.getOwner() == Turn.NONE) {
//                 board.onCityPointClick(cityPoint.getX(), cityPoint.getY());
//                 if (player.settlements == settlementsBefore - 1) {
//                     return harborPoint;
//                 }
//             }
//         }
//         return null;
//     }

//     private int determineBankTradeRate(Board board, Player player, ResourceType offeredResource) {
//         boolean hasGenericHarbor = false;
//         boolean hasSpecificForResource = false;
//         for (CityPoint cityPoint : board.cityPoints) {
//             if (cityPoint.getOwner() == player.color && cityPoint instanceof HarborPoint harborPoint) {
//                 if (harborPoint.isGeneric()) {
//                     hasGenericHarbor = true;
//                 } else if (harborPoint.getTradingResource() == offeredResource) {
//                     hasSpecificForResource = true;
//                 }
//             }
//         }
//         if (hasSpecificForResource) {
//             return Board.HARBOR_TRADE_VALUE;
//         }
//         if (hasGenericHarbor) {
//             return Board.GENERIC_TRADE_VALUE;
//         }
//         return Board.BANK_TRADE_VALUE;
//     }

//     private static class ScriptedDice extends Dice {
//         private final ArrayDeque<Integer> scriptedRolls = new ArrayDeque<>();

//         ScriptedDice() {
//             super(new Random(0));
//         }

//         void enqueueRoll(int rollValue) {
//             scriptedRolls.addLast(rollValue);
//         }

//         @Override
//         public int roll() {
//             if (scriptedRolls.isEmpty()) {
//                 return super.roll();
//             }
//             return scriptedRolls.removeFirst();
//         }
//     }

//     private static class RecordingGameWindowController extends GameWindowController {
//         int cityButtonCount = 0;
//         int roadButtonCount = 0;
//         int robberButtonCount = 0;
//         int lastRobberX = Integer.MIN_VALUE;
//         int lastRobberY = Integer.MIN_VALUE;
//         int showResourcesCallCount = 0;
//         int discardDialogShownCount = 0;
//         TurnStateData lastTurnState;
//         Map<ResourceType, Integer> lastShownResources = new HashMap<>();
//         ArrayList<String> placementsShown = new ArrayList<>();

//         RecordingGameWindowController() {
//             super(null);
//         }

//         @Override
//         public void placeCityButton(Board board, int x, int y) {
//             cityButtonCount++;
//         }

//         @Override
//         public void placeRoadButton(Board board, int x, int y) {
//             roadButtonCount++;
//         }

//         @Override
//         public void placeRobberButton(Board board, int x, int y) {
//             robberButtonCount++;
//         }

//         @Override
//         public void showInitialTurnState(TurnStateData turnData) {
//             this.lastTurnState = turnData;
//         }

//         @Override
//         public void showSettlement(Turn turn, int x, int y) {
//             placementsShown.add("settlement");
//         }

//         @Override
//         public void showRoad(Turn turn, int x, int y) {
//             placementsShown.add("road");
//         }

//         @Override
//         public void showCity(Turn turn, int x, int y) {
//             placementsShown.add("city");
//         }

//         @Override
//         public void showResourceCards(Board board, HashMap<ResourceType, Integer> resourceMap) {
//             this.lastShownResources = new HashMap<>(resourceMap);
//             showResourcesCallCount++;
//         }

//         @Override
//         public void showInitialRobberState(int x, int y) {
//             this.lastRobberX = x;
//             this.lastRobberY = y;
//         }

//         @Override
//         public void clearDevCards() {
//         }

//         @Override
//         public void showDevCards(Board board, ArrayList<DevelopmentCard> devCards) {
//         }

//         @Override
//         public void showDiceRoll(int diceRoll) {
//         }

//         @Override
//         public void showDiscardDialog(Board board) {
//             discardDialogShownCount++;
//         }

//         @Override
//         public void hideDiscardDialog() {
//         }

//         @Override
//         public void showStealDialog(Board board, Set<Player> players) {
//         }

//         @Override
//         public void removeStealDialog() {
//         }

//         @Override
//         public void showInvalidInputAndPass(String message) {
//         }
//     }
// }
