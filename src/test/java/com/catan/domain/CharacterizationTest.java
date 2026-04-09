package com.catan.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Characterization Tests (Ch 13) with Effect Analysis (Ch 11)
 *
 * These tests document ACTUAL current behavior that is NOT covered
 * by the existing test suite. They serve as a safety net for:
 *
 *   1. Catan for Two Players w/ "Commercial Tokens"
 *   2. Computer to Computer gameplay
 *   3. The Fishermen of Catan Scenario (fishing ground tiles,
 *      fish resources, "Old Boot" -1 VP, fish-based actions)
 *
 * Each section includes an effect sketch comment showing which
 * variables/methods are affected by potential changes.
 */
public class CharacterizationTest {

    private GameWindowController mockController;
    private TurnStateMachine mockTSM;
    private NumberCardDeck mockDeck;
    private Board board;

    @BeforeEach
    public void setUp() {
        mockController = EasyMock.niceMock(GameWindowController.class);
        mockTSM = EasyMock.niceMock(TurnStateMachine.class);
        mockDeck = EasyMock.niceMock(NumberCardDeck.class);
        EasyMock.replay(mockController, mockTSM, mockDeck);
        board = new Board(mockController, mockTSM, mockDeck);
    }

    /** Fake supplier that returns tokens from a pre-loaded queue. */
    private static class FakeFishTokenSupplier implements FishTokenSupplier {
        private final Queue<FishToken> queue = new LinkedList<>();
        void load(FishToken... tokens) { for (FishToken t : tokens) queue.add(t); }
        @Override public FishToken draw() { return queue.poll(); }
        @Override public void returnTokens(List<FishToken> tokens) { }
        @Override public int remaining() { return queue.size(); }
    }

    // =========================================================================
    // SECTION 1: Board constructor — pinning 4-player initialization
    //
    // Effect Sketch:
    //   Board() --> turnToPlayer (4 players + BANK), longestRoad,
    //               harborSettlements
    //
    // Why: "Two Players" feature will reduce players from 4 to 2.
    //       These pin the current state so we detect breakage.
    // =========================================================================

    @Test
    public void characterize_board_initializesFourPlayersAndBank() {
        // Ch 13 algorithm: assert something we know will tell us the actual state.
        // The board hardcodes 5 entries: RED, BLUE, ORANGE, WHITE, BANK.
        assertNotNull(board.turnToPlayer.get(Turn.RED));
        assertNotNull(board.turnToPlayer.get(Turn.BLUE));
        assertNotNull(board.turnToPlayer.get(Turn.ORANGE));
        assertNotNull(board.turnToPlayer.get(Turn.WHITE));
        assertNotNull(board.turnToPlayer.get(Turn.BANK));
        assertEquals(5, board.turnToPlayer.size());
    }

    @Test
    public void characterize_board_initialLongestRoadZeroForAllPlayers() {
        // No existing test pins longestRoad init values.
        assertEquals(0, board.longestRoad.get(Turn.RED));
        assertEquals(0, board.longestRoad.get(Turn.BLUE));
        assertEquals(0, board.longestRoad.get(Turn.ORANGE));
        assertEquals(0, board.longestRoad.get(Turn.WHITE));
        assertEquals(4, board.longestRoad.size()); // no BANK entry
    }

    @Test
    public void characterize_board_initialHarborSettlementsZero() {
        // Pins harbor settlement counts for harbormaster bonus.
        assertEquals(0, board.harborSettlements.get(Turn.RED));
        assertEquals(0, board.harborSettlements.get(Turn.BLUE));
        assertEquals(0, board.harborSettlements.get(Turn.ORANGE));
        assertEquals(0, board.harborSettlements.get(Turn.WHITE));
    }

    // =========================================================================
    // SECTION 2: Player fish token behavior — gaps in existing coverage
    //
    // Effect Sketch:
    //   addFishToken() --> fishTokens list, hasOldShoe
    //   fishTokens --> getTotalFish(), spendFishTokens()
    //   addFishTokens(int) --> fishTokens (N individual 1-fish tokens)
    //
    // Why: Fishermen scenario extends fish mechanics. Existing FishTokenSeamTest
    //       tests seam types but doesn't cover these specific behaviors.
    // =========================================================================

    @Test
    public void characterize_player_initialFishStateIsZero() {
        Player p = new Player(Turn.RED);
        assertEquals(0, p.getFishTokens());
        assertEquals(0, p.getTotalFish());
        assertFalse(p.hasOldShoe());
        assertTrue(p.getFishTokenList().isEmpty());
    }

    @Test
    public void characterize_player_addMultipleFishTokensAccumulates() {
        // Existing tests only add tokens via Board distribution.
        // This pins direct Player accumulation across multiple adds.
        Player p = new Player(Turn.RED);
        p.addFishToken(new FishToken(1));
        p.addFishToken(new FishToken(3));
        p.addFishToken(new FishToken(2));
        assertEquals(6, p.getTotalFish());
        assertEquals(3, p.getFishTokenList().size());
    }

    @Test
    public void characterize_player_addFishTokensConvenienceMethod() {
        // addFishTokens(int) is a legacy convenience method — NOT tested anywhere.
        // It adds N individual 1-fish tokens, not a single N-value token.
        Player p = new Player(Turn.RED);
        p.addFishTokens(3);
        assertEquals(3, p.getTotalFish());
        assertEquals(3, p.getFishTokenList().size());
        for (FishToken t : p.getFishTokenList()) {
            assertEquals(1, t.getFishCount());
        }
    }

    @Test
    public void characterize_player_oldShoeNotAddedToTokenList() {
        // FishTokenSeamTest checks hasOldShoe(), but not that old shoe
        // stays OUT of the fishTokens list (important for spending logic).
        Player p = new Player(Turn.RED);
        p.addFishToken(FishToken.oldShoe());
        p.addFishToken(new FishToken(2));
        assertTrue(p.hasOldShoe());
        assertEquals(2, p.getTotalFish()); // old shoe contributes 0
        assertEquals(1, p.getFishTokenList().size()); // only the 2-fish token
    }

    // =========================================================================
    // SECTION 3: StandardFishTokenSupplier — entirely untested class
    //
    // Effect Sketch:
    //   constructor --> faceDown (30 tokens shuffled)
    //   draw() --> removes from faceDown; reshuffles faceUp if empty
    //   returnTokens() --> adds to faceUp pile
    //   remaining() --> faceDown.size()
    //
    // Why: Fishermen scenario depends on correct token pool composition and
    //       reshuffle behavior. No existing tests cover this class at all.
    // =========================================================================

    @Test
    public void characterize_standardSupplier_initialPoolSize() {
        // 11x1-fish + 10x2-fish + 8x3-fish + 1 old shoe = 30
        StandardFishTokenSupplier supplier = new StandardFishTokenSupplier(new Random(42));
        assertEquals(30, supplier.remaining());
    }

    @Test
    public void characterize_standardSupplier_drawAllTokensComposition() {
        // Ch 13: draw all 30 and let the actual values tell us the composition.
        StandardFishTokenSupplier supplier = new StandardFishTokenSupplier(new Random(42));
        int ones = 0, twos = 0, threes = 0, shoes = 0;
        for (int i = 0; i < 30; i++) {
            FishToken t = supplier.draw();
            assertNotNull(t);
            if (t.isOldShoe()) shoes++;
            else if (t.getFishCount() == 1) ones++;
            else if (t.getFishCount() == 2) twos++;
            else if (t.getFishCount() == 3) threes++;
        }
        assertEquals(11, ones);
        assertEquals(10, twos);
        assertEquals(8, threes);
        assertEquals(1, shoes);
        assertEquals(0, supplier.remaining());
    }

    @Test
    public void characterize_standardSupplier_drawFromEmptyReshufflesFaceUp() {
        StandardFishTokenSupplier supplier = new StandardFishTokenSupplier(new Random(42));
        for (int i = 0; i < 30; i++) supplier.draw();
        assertEquals(0, supplier.remaining());

        // Return tokens face-up, then draw — triggers reshuffle of face-up pile
        supplier.returnTokens(List.of(new FishToken(1), new FishToken(2)));
        FishToken drawn = supplier.draw();
        assertNotNull(drawn);
        assertEquals(1, supplier.remaining()); // one left after drawing 1 of 2
    }

    @Test
    public void characterize_standardSupplier_drawFromCompletelyEmptyReturnsNull() {
        // Important: returns null, does NOT throw an exception.
        StandardFishTokenSupplier supplier = new StandardFishTokenSupplier(new Random(42));
        for (int i = 0; i < 30; i++) supplier.draw();
        FishToken result = supplier.draw();
        assertNull(result);
    }

    // =========================================================================
    // SECTION 4: CityPoint.gatherResources — surprising special cases
    //
    // Effect Sketch:
    //   gatherResources() reads: tileValueToTerrain
    //   SURPRISING: FOREST terrain with tileValue 9 produces SHEEP, not WOOD
    //   SURPRISING: DESERT returns ResourceCard(NULL), not empty list
    //
    // Why: Fishing ground tiles will add new terrain types. Must pin
    //       these edge cases so the special logic isn't broken.
    // =========================================================================

    @Test
    public void characterize_cityPoint_gatherResourcesForest9ProducesSheep() {
        // SURPRISING BEHAVIOR: Forest at tile value 9 override produces SHEEP.
        // This is a hardcoded special case in gatherResources().
        CityPoint cp = new CityPoint(10, 10);
        cp.setTileValues(List.of(9), List.of(Terrain.FOREST));

        ArrayList<ResourceCard> resources = cp.gatherResources();
        assertEquals(1, resources.size());
        assertEquals(ResourceType.SHEEP, resources.get(0).getResourceType());
    }

    @Test
    public void characterize_cityPoint_gatherResourcesForestNon9ProducesWood() {
        // Contrast: Forest at any other tile value produces WOOD normally.
        CityPoint cp = new CityPoint(10, 10);
        cp.setTileValues(List.of(5), List.of(Terrain.FOREST));

        ArrayList<ResourceCard> resources = cp.gatherResources();
        assertEquals(1, resources.size());
        assertEquals(ResourceType.WOOD, resources.get(0).getResourceType());
    }

    @Test
    public void characterize_cityPoint_desertProducesNullResourceCard() {
        // SURPRISING: DESERT doesn't return an empty list — it returns
        // a ResourceCard with ResourceType.NULL.
        CityPoint cp = new CityPoint(10, 10);
        cp.setTileValues(List.of(7), List.of(Terrain.DESERT));

        ArrayList<ResourceCard> resources = cp.gatherResources();
        assertEquals(1, resources.size());
        assertEquals(ResourceType.NULL, resources.get(0).getResourceType());
    }

    @Test
    public void characterize_cityPoint_gatherResourcesNormalTerrain() {
        CityPoint cp = new CityPoint(10, 10);
        cp.setTileValues(List.of(6, 8), List.of(Terrain.HILL, Terrain.FIELD));

        ArrayList<ResourceCard> resources = cp.gatherResources();
        assertEquals(2, resources.size());
        boolean hasBrick = false, hasWheat = false;
        for (ResourceCard rc : resources) {
            if (rc.getResourceType() == ResourceType.BRICK) hasBrick = true;
            if (rc.getResourceType() == ResourceType.WHEAT) hasWheat = true;
        }
        assertTrue(hasBrick);
        assertTrue(hasWheat);
    }

    @Test
    public void characterize_cityPoint_fishingGroundDefaultsFalse() {
        CityPoint cp = new CityPoint(10, 10);
        assertFalse(cp.isFishingGround());
    }

    // =========================================================================
    // SECTION 5: Board resource distribution — untested edge cases
    //
    // Effect Sketch:
    //   giveResourcesToBorderingSettlements() reads:
    //     numRolled, robberNumber, robberResource, epidemicActive
    //   --> player.resources
    //
    // Why: Computer gameplay needs deterministic distribution. Fishermen
    //       adds fishing ground tiles alongside normal distribution.
    // =========================================================================

    @Test
    public void characterize_giveResources_cityGetsOneResourceDuringEpidemic() {
        // Normally a city gets 2 resources. During an epidemic, only 1.
        CityPoint cp = new CityPoint(10, 10);
        cp.setTileValues(List.of(6), List.of(Terrain.HILL));
        cp.placeSettlement(Turn.RED);
        cp.isCity = true;

        board.cityPoints = new ArrayList<>(List.of(cp));
        board.numRolled = 6;
        board.epidemicActive = true;

        board.giveResourcesToBorderingSettlements();

        assertEquals(1, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void characterize_giveResources_robberBlocksOnlyMatchingResourceAndNumber() {
        // Robber blocks ONLY when both robberNumber AND robberResource match.
        CityPoint cp = new CityPoint(10, 10);
        cp.setTileValues(List.of(6), List.of(Terrain.HILL)); // BRICK on 6

        cp.placeSettlement(Turn.RED);
        board.cityPoints = new ArrayList<>(List.of(cp));
        board.numRolled = 6;

        // Robber matches number but WRONG resource — should NOT block
        board.robberNumber = 6;
        board.robberResource = ResourceType.WOOD; // not BRICK
        board.giveResourcesToBorderingSettlements();
        assertEquals(1, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.BRICK));
    }

    @Test
    public void characterize_giveResources_forest9ProducesSheepNotWood() {
        // Same Forest-9→SHEEP override applies during dice-roll distribution.
        CityPoint cp = new CityPoint(10, 10);
        cp.setTileValues(List.of(9), List.of(Terrain.FOREST));
        cp.placeSettlement(Turn.RED);

        board.cityPoints = new ArrayList<>(List.of(cp));
        board.numRolled = 9;

        board.giveResourcesToBorderingSettlements();

        assertEquals(0, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.WOOD));
        assertEquals(1, board.turnToPlayer.get(Turn.RED).getResource(ResourceType.SHEEP));
    }

    // =========================================================================
    // SECTION 6: Fish distribution edge cases — not in FishTokenSeamTest
    //
    // Effect Sketch:
    //   distributeFishTokens() reads: cityPoints, numRolled, fishTokenSupplier
    //   SURPRISING: all-or-nothing when supply < needed
    // =========================================================================

    @Test
    public void characterize_distributeFish_noFishingGroundCitiesReturnsEmpty() {
        CityPoint normalCity = new CityPoint(10, 10);
        normalCity.setTileValues(List.of(6), List.of(Terrain.PASTURE));
        normalCity.placeSettlement(Turn.RED);
        // isFishingGround defaults to false

        board.cityPoints = new ArrayList<>(List.of(normalCity));
        board.numRolled = 6;

        HashMap<Turn, ArrayList<FishToken>> result = board.distributeFishTokens();
        assertTrue(result.isEmpty());
    }

    @Test
    public void characterize_distributeFish_wrongDiceRollGivesNothing() {
        CityPoint fishCity = new CityPoint(10, 10);
        fishCity.setFishingGround(true);
        fishCity.setTileValues(List.of(6), List.of(Terrain.PASTURE));
        fishCity.placeSettlement(Turn.RED);

        board.cityPoints = new ArrayList<>(List.of(fishCity));
        board.numRolled = 8; // doesn't match tile value 6

        HashMap<Turn, ArrayList<FishToken>> result = board.distributeFishTokens();
        assertTrue(result.isEmpty());
    }

    @Test
    public void characterize_distributeFish_insufficientTokensSkipsAll() {
        // SURPRISING: If supplier can't cover total needed, NOTHING is distributed.
        // All-or-nothing behavior, not partial distribution.
        CityPoint fishCity = new CityPoint(10, 10);
        fishCity.setFishingGround(true);
        fishCity.setTileValues(List.of(6), List.of(Terrain.PASTURE));
        fishCity.placeSettlement(Turn.RED);
        fishCity.isCity = true; // needs 2 tokens

        board.cityPoints = new ArrayList<>(List.of(fishCity));
        board.numRolled = 6;

        FakeFishTokenSupplier fake = new FakeFishTokenSupplier();
        fake.load(new FishToken(1)); // only 1 available, need 2
        board.fishTokenSupplier = fake;

        HashMap<Turn, ArrayList<FishToken>> result = board.distributeFishTokens();
        assertTrue(result.isEmpty());
        assertEquals(0, board.turnToPlayer.get(Turn.RED).getTotalFish());
    }

    // =========================================================================
    // SECTION 7: Fish redemption — specific reward types
    //
    // Effect Sketch:
    //   redeemFishTokens(DEVELOPMENT_CARD) --> adds KNIGHT specifically
    //   redeemFishTokens(FREE_ROAD) --> adds 1 free road
    //   getFishRedemptionCost() --> cost constants per type
    // =========================================================================

    @Test
    public void characterize_fishRedemptionCosts() {
        // Pin the exact costs — not documented in any existing test.
        assertEquals(2, board.getFishRedemptionCost(FishRedemptionType.MOVE_ROBBER));
        assertEquals(3, board.getFishRedemptionCost(FishRedemptionType.STEAL_RESOURCE));
        assertEquals(4, board.getFishRedemptionCost(FishRedemptionType.FREE_RESOURCE));
        assertEquals(5, board.getFishRedemptionCost(FishRedemptionType.DEVELOPMENT_CARD));
        assertEquals(7, board.getFishRedemptionCost(FishRedemptionType.FREE_ROAD));
    }

    @Test
    public void characterize_redeemFish_devCardAddsKnight() {
        // SURPRISING: Spending 5 fish for a dev card always gives a KNIGHT,
        // not a random card like buyDevCard() does.
        Player red = board.turnToPlayer.get(Turn.RED);
        red.addFishToken(new FishToken(3));
        red.addFishToken(new FishToken(2));

        assertTrue(board.redeemFishTokens(Turn.RED, FishRedemptionType.DEVELOPMENT_CARD));
        assertEquals(1, red.getDevCards().size());
        assertEquals(DevCards.KNIGHT, red.getDevCards().get(0).getType());
    }

    @Test
    public void characterize_redeemFish_freeRoadAddsFreeRoad() {
        Player red = board.turnToPlayer.get(Turn.RED);
        red.addFishToken(new FishToken(3));
        red.addFishToken(new FishToken(3));
        red.addFishToken(new FishToken(1));

        assertTrue(board.redeemFishTokens(Turn.RED, FishRedemptionType.FREE_ROAD));
        assertEquals(1, red.getFreeRoads());
    }

    // =========================================================================
    // SECTION 8: Old Shoe passing — untested edge cases
    //
    // Effect Sketch:
    //   canPassOldShoe() reads: giver.hasOldShoe, from==to, receiver.VPs
    //   passOldShoe() --> giver.hasOldShoe=false, receiver.hasOldShoe=true
    //
    // FishTokenSeamTest covers VP-comparison logic but NOT these cases.
    // =========================================================================

    @Test
    public void characterize_canPassOldShoe_giverMustHaveShoe() {
        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        red.addVictoryPoints(5);
        blue.addVictoryPoints(5);
        // RED doesn't have old shoe
        assertFalse(board.canPassOldShoe(Turn.RED, Turn.BLUE));
    }

    @Test
    public void characterize_canPassOldShoe_cannotPassToSelf() {
        Player red = board.turnToPlayer.get(Turn.RED);
        red.setOldShoe(true);
        red.addVictoryPoints(5);
        assertFalse(board.canPassOldShoe(Turn.RED, Turn.RED));
    }

    @Test
    public void characterize_passOldShoe_transfersOwnership() {
        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        red.setOldShoe(true);
        red.addVictoryPoints(5);
        blue.addVictoryPoints(5);

        board.passOldShoe(Turn.RED, Turn.BLUE);
        assertFalse(red.hasOldShoe());
        assertTrue(blue.hasOldShoe());
    }

    @Test
    public void characterize_passOldShoe_blockedByInvalidTargetIsNoOp() {
        // passOldShoe silently does nothing when canPassOldShoe is false.
        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        red.setOldShoe(true);
        red.addVictoryPoints(8);
        blue.addVictoryPoints(2);

        board.passOldShoe(Turn.RED, Turn.BLUE);
        assertTrue(red.hasOldShoe()); // unchanged
        assertFalse(blue.hasOldShoe());
    }

    // =========================================================================
    // SECTION 9: NumberCardDeck — entirely untested class
    //
    // Effect Sketch:
    //   NumberCardDeck() --> fullDeck (36 cards), drawPile (shuffled copy)
    //   drawCard() --> drawPile shrinks; auto-reshuffles if empty
    //   EventCard.diceNumber, EventCard.eventType
    //
    // Why: All features interact with dice/events. Computer gameplay
    //       needs deterministic event handling.
    // =========================================================================

    @Test
    public void characterize_numberCardDeck_totalCards() {
        NumberCardDeck deck = new NumberCardDeck(new Random(42));
        assertEquals(36, deck.deckSize());
        assertEquals(36, deck.cardsRemaining());
    }

    @Test
    public void characterize_numberCardDeck_drawReducesRemaining() {
        NumberCardDeck deck = new NumberCardDeck(new Random(42));
        deck.drawCard();
        assertEquals(35, deck.cardsRemaining());
    }

    @Test
    public void characterize_numberCardDeck_drawAllThenAutoReshuffle() {
        NumberCardDeck deck = new NumberCardDeck(new Random(42));
        for (int i = 0; i < 36; i++) {
            EventCard card = deck.drawCard();
            assertNotNull(card);
            assertTrue(card.getDiceNumber() >= 2 && card.getDiceNumber() <= 12);
        }
        assertEquals(0, deck.cardsRemaining());

        // Drawing again auto-reshuffles
        EventCard card = deck.drawCard();
        assertNotNull(card);
        assertEquals(35, deck.cardsRemaining());
    }

    @Test
    public void characterize_numberCardDeck_allSevensAreRobberAttack() {
        // Every 7-card in the deck is a ROBBER_ATTACK event (6 total).
        NumberCardDeck deck = new NumberCardDeck(new Random(42));
        int robberAttackCount = 0;
        for (int i = 0; i < 36; i++) {
            EventCard card = deck.drawCard();
            if (card.getDiceNumber() == 7) {
                assertEquals(EventType.ROBBER_ATTACK, card.getEventType());
                robberAttackCount++;
            }
        }
        assertEquals(6, robberAttackCount);
    }

    // =========================================================================
    // SECTION 10: Bank trading rates — untested
    //
    // Effect Sketch:
    //   onBankSubmitClick() reads: cityPoints (harbor ownership)
    //   Rate logic: 4:1 default, 3:1 generic harbor, 2:1 specific harbor
    //
    // Why: "Commercial Tokens" modify trade mechanics for 2-player mode.
    // =========================================================================

    @Test
    public void characterize_bankTrade_defaultRate4to1() {
        EasyMock.reset(mockTSM);
        EasyMock.expect(mockTSM.getTurn()).andReturn(Turn.RED);
        EasyMock.replay(mockTSM);

        board.cityPoints = new ArrayList<>(); // no harbors
        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 4);

        TradeInfo playerTrade = new TradeInfo();
        playerTrade.setResources(ResourceType.WOOD, 4);
        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        board.onBankSubmitClick(playerTrade, bankTrade);

        assertEquals(0, red.getResource(ResourceType.WOOD));
        assertEquals(1, red.getResource(ResourceType.BRICK));
    }

    @Test
    public void characterize_bankTrade_genericHarbor3to1() {
        EasyMock.reset(mockTSM);
        EasyMock.expect(mockTSM.getTurn()).andReturn(Turn.RED);
        EasyMock.replay(mockTSM);

        HarborPoint genericHarbor = new HarborPoint(10, 10, ResourceType.NULL);
        genericHarbor.placeSettlement(Turn.RED);
        board.cityPoints = new ArrayList<>(List.of(genericHarbor));

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 3);

        TradeInfo playerTrade = new TradeInfo();
        playerTrade.setResources(ResourceType.WOOD, 3);
        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        board.onBankSubmitClick(playerTrade, bankTrade);

        assertEquals(0, red.getResource(ResourceType.WOOD));
        assertEquals(1, red.getResource(ResourceType.BRICK));
    }

    @Test
    public void characterize_bankTrade_specificHarbor2to1() {
        EasyMock.reset(mockTSM);
        EasyMock.expect(mockTSM.getTurn()).andReturn(Turn.RED);
        EasyMock.replay(mockTSM);

        HarborPoint woodHarbor = new HarborPoint(10, 10, ResourceType.WOOD);
        woodHarbor.placeSettlement(Turn.RED);
        board.cityPoints = new ArrayList<>(List.of(woodHarbor));

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addResources(ResourceType.WOOD, 2);

        TradeInfo playerTrade = new TradeInfo();
        playerTrade.setResources(ResourceType.WOOD, 2);
        TradeInfo bankTrade = new TradeInfo();
        bankTrade.setPlayer(Turn.BANK);
        bankTrade.setResources(ResourceType.BRICK, 1);

        board.onBankSubmitClick(playerTrade, bankTrade);

        assertEquals(0, red.getResource(ResourceType.WOOD));
        assertEquals(1, red.getResource(ResourceType.BRICK));
    }

    // =========================================================================
    // SECTION 11: Event handling — entirely untested
    //
    // Effect Sketch:
    //   handleEpidemicEvent() --> epidemicActive = true
    //   handleRobberAttackEvent() --> robberMoved = false
    //   handleEarthquakeEvent() --> road.isSideways, longestRoad recalculated
    //
    // Why: Computer player needs automated event responses.
    // =========================================================================

    @Test
    public void characterize_epidemicEvent_setsFlag() {
        assertFalse(board.epidemicActive);
        board.handleEpidemicEvent();
        assertTrue(board.epidemicActive);
    }

    @Test
    public void characterize_robberAttackEvent_setsRobberNotMoved() {
        board.robberMoved = true;
        board.handleRobberAttackEvent();
        assertFalse(board.robberMoved);
    }

    @Test
    public void characterize_earthquakeEvent_makesOneRoadSideways() {
        RoadPoint r1 = new RoadPoint(10, 10);
        r1.placeRoad(Turn.RED);
        RoadPoint r2 = new RoadPoint(20, 20);
        r2.placeRoad(Turn.RED);

        board.roadPoints = new ArrayList<>(List.of(r1, r2));
        board.longestRoad = new HashMap<>();
        board.longestRoad.put(Turn.RED, 0);
        board.longestRoad.put(Turn.BLUE, 0);
        board.longestRoad.put(Turn.ORANGE, 0);
        board.longestRoad.put(Turn.WHITE, 0);
        board.longestRoad.put(Turn.BANK, 0);

        board.rand = new Random(42);
        board.handleEarthquakeEvent();

        int sidewaysCount = 0;
        if (r1.isSideways()) sidewaysCount++;
        if (r2.isSideways()) sidewaysCount++;
        assertEquals(1, sidewaysCount);
    }

    // =========================================================================
    // SECTION 12: VP edge case — negative VPs possible
    //
    // Effect Sketch:
    //   TitleBonus.evaluate() calls addVictoryPoints(-vpBonus)
    //   --> player VPs can go below zero
    //
    // Why: Old Shoe (-1 VP effectively) interacts with VP calculations.
    // =========================================================================

    @Test
    public void characterize_player_vpCanBeNegative() {
        // TitleBonus.evaluate can subtract VPs — VPs aren't clamped to 0.
        Player p = new Player(Turn.RED);
        p.addVictoryPoints(-1);
        assertEquals(-1, p.getVictoryPoints());
    }
}
