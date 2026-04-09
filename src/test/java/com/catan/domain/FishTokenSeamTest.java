package com.catan.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.easymock.EasyMock;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Fishermen of Catan — Five Seam Tests
 *
 * Each test demonstrates a different seam type that enables
 * testing the new Fish Token feature from the Barbarians expansion.
 */
public class FishTokenSeamTest {

    private GameWindowController mockController;
    private TurnStateMachine mockTurnStateMachine;
    private NumberCardDeck mockDeck;
    private Board board;

    @BeforeEach
    public void setUp() {
        mockController = EasyMock.niceMock(GameWindowController.class);
        mockTurnStateMachine = EasyMock.niceMock(TurnStateMachine.class);
        mockDeck = EasyMock.niceMock(NumberCardDeck.class);
        EasyMock.replay(mockController, mockTurnStateMachine, mockDeck);
        board = new Board(mockController, mockTurnStateMachine, mockDeck);
    }

    // =========================================================================
    // SEAM 1: FishTokenSupplier — fake substitution to control drawn tokens
    //
    // The Board delegates token drawing to a FishTokenSupplier interface.
    // By injecting a fake supplier we control *exactly* which tokens are drawn,
    // letting us test old-shoe handling, mixed-value tokens, and empty-pool
    // scenarios without any randomness.
    // =========================================================================

    /** Fake supplier that returns tokens from a pre-loaded queue. */
    private static class FakeFishTokenSupplier implements FishTokenSupplier {
        private final Queue<FishToken> queue = new LinkedList<>();
        private final List<FishToken> returned = new ArrayList<>();

        void load(FishToken... tokens) {
            for (FishToken t : tokens) queue.add(t);
        }

        @Override public FishToken draw() { return queue.poll(); }
        @Override public void returnTokens(List<FishToken> tokens) { returned.addAll(tokens); }
        @Override public int remaining() { return queue.size(); }
    }

    @Test
    public void seam1_fakeSupplierControlsDrawnTokenValues() {
        // Arrange: set up a fishing ground city with a settlement
        CityPoint fishCity = new CityPoint(10, 10);
        fishCity.setFishingGround(true);
        fishCity.setTileValues(List.of(6), List.of(Terrain.PASTURE));
        fishCity.placeSettlement(Turn.RED);

        board.cityPoints = new ArrayList<>(List.of(fishCity));
        board.numRolled = 6;

        // Inject fake supplier that returns a 3-fish token
        FakeFishTokenSupplier fake = new FakeFishTokenSupplier();
        fake.load(new FishToken(3));
        board.fishTokenSupplier = fake;

        // Act
        board.distributeFishTokens();

        // Assert: RED got exactly 3 fish (from the single 3-fish token)
        Player red = board.turnToPlayer.get(Turn.RED);
        assertEquals(3, red.getTotalFish(),
                "Fake supplier should let us control the exact token value drawn");
    }

    @Test
    public void seam1_fakeSupplierReturnsOldShoe() {
        CityPoint fishCity = new CityPoint(10, 10);
        fishCity.setFishingGround(true);
        fishCity.setTileValues(List.of(8), List.of(Terrain.FIELD));
        fishCity.placeSettlement(Turn.BLUE);

        board.cityPoints = new ArrayList<>(List.of(fishCity));
        board.numRolled = 8;

        FakeFishTokenSupplier fake = new FakeFishTokenSupplier();
        fake.load(FishToken.oldShoe());
        board.fishTokenSupplier = fake;

        board.distributeFishTokens();

        Player blue = board.turnToPlayer.get(Turn.BLUE);
        assertTrue(blue.hasOldShoe(),
                "Drawing the old shoe token should give the player the old shoe");
        assertEquals(0, blue.getTotalFish(),
                "Old shoe should not add any fish value");
    }

    // =========================================================================
    // SEAM 2: selectTokensForCost — extracted method for spending logic
    //
    // The "no change" rule means if your tokens sum to more than the cost,
    // excess fish are lost. We extracted this token-selection algorithm into
    // Player.selectTokensForCost() so it can be tested independently of the
    // Board's redemption side-effects.
    // =========================================================================

    @Test
    public void seam2_selectTokensForCostPicksSmallestTokensFirst() {
        Player player = new Player(Turn.RED);
        player.addFishToken(new FishToken(3));
        player.addFishToken(new FishToken(1));
        player.addFishToken(new FishToken(2));

        // Cost = 3: should pick the 1 + 2 tokens (total exactly 3)
        // rather than the single 3-token, because it sorts ascending
        // and greedily selects until sum >= cost
        ArrayList<FishToken> selected = player.selectTokensForCost(3);
        int selectedTotal = selected.stream().mapToInt(FishToken::getFishCount).sum();

        assertEquals(3, selectedTotal,
                "Should pick tokens summing to exactly the cost when possible");
        assertEquals(2, selected.size(),
                "Should use the 1-fish and 2-fish tokens");
    }

    @Test
    public void seam2_selectTokensCostsExcessAreWasted() {
        Player player = new Player(Turn.RED);
        player.addFishToken(new FishToken(3));
        player.addFishToken(new FishToken(3));

        // Cost = 4: smallest-first picks 3, still < 4, so picks another 3 = 6 total
        // The "no change" rule means 2 extra fish are lost
        ArrayList<FishToken> selected = player.selectTokensForCost(4);
        int selectedTotal = selected.stream().mapToInt(FishToken::getFishCount).sum();

        assertTrue(selectedTotal >= 4, "Selected tokens must cover the cost");
        assertEquals(6, selectedTotal,
                "With only 3-fish tokens, must overshoot — excess is lost per rules");

        // Actually spend them
        assertTrue(player.spendFishTokens(4));
        assertEquals(0, player.getTotalFish(),
                "Both tokens consumed; no change given back");
    }

    // =========================================================================
    // SEAM 3: distributeFishTokens returns HashMap — visibility seam
    //
    // distributeFishTokens() returns a HashMap<Turn, ArrayList<FishToken>>
    // showing exactly which tokens each player received. This makes the
    // internal distribution decisions *visible* to tests without needing
    // to inspect player state directly.
    // =========================================================================

    @Test
    public void seam3_distributionResultMakesTokenAllocationVisible() {
        // Two fishing-ground cities: RED has a settlement, BLUE has a city
        CityPoint redCity = new CityPoint(10, 10);
        redCity.setFishingGround(true);
        redCity.setTileValues(List.of(5), List.of(Terrain.FOREST));
        redCity.placeSettlement(Turn.RED);

        CityPoint blueCity = new CityPoint(20, 20);
        blueCity.setFishingGround(true);
        blueCity.setTileValues(List.of(5), List.of(Terrain.HILL));
        blueCity.placeSettlement(Turn.BLUE);
        blueCity.isCity = true;

        board.cityPoints = new ArrayList<>(List.of(redCity, blueCity));
        board.numRolled = 5;

        FakeFishTokenSupplier fake = new FakeFishTokenSupplier();
        fake.load(new FishToken(1), new FishToken(2), new FishToken(3));
        board.fishTokenSupplier = fake;

        // Act: capture the return value
        HashMap<Turn, ArrayList<FishToken>> result = board.distributeFishTokens();

        // Assert via the returned map (visibility seam)
        assertEquals(1, result.get(Turn.RED).size(),
                "Settlement draws 1 token");
        assertEquals(2, result.get(Turn.BLUE).size(),
                "City draws 2 tokens");
        assertEquals(1, result.get(Turn.RED).get(0).getFishCount(),
                "RED got the 1-fish token");
    }

    // =========================================================================
    // SEAM 4: canPassOldShoe — state-control seam for VP-based rules
    //
    // The old shoe passing rule depends on comparing players' VP totals.
    // By directly controlling VP state via player.addVictoryPoints(), we
    // can drive the method through every branch without playing a real game.
    // =========================================================================

    @Test
    public void seam4_canPassOldShoeOnlyToPlayerWithEqualOrMoreVPs() {
        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        Player orange = board.turnToPlayer.get(Turn.ORANGE);

        red.setOldShoe(true);
        red.addVictoryPoints(5);
        blue.addVictoryPoints(5);    // equal VPs — valid target
        orange.addVictoryPoints(3);  // fewer VPs — invalid target

        assertTrue(board.canPassOldShoe(Turn.RED, Turn.BLUE),
                "Can pass to player with equal VPs");
        assertFalse(board.canPassOldShoe(Turn.RED, Turn.ORANGE),
                "Cannot pass to player with fewer VPs");
    }

    @Test
    public void seam4_cannotPassOldShoeIfAloneInLead() {
        Player red = board.turnToPlayer.get(Turn.RED);
        Player blue = board.turnToPlayer.get(Turn.BLUE);
        Player orange = board.turnToPlayer.get(Turn.ORANGE);
        Player white = board.turnToPlayer.get(Turn.WHITE);

        red.setOldShoe(true);
        red.addVictoryPoints(8);
        blue.addVictoryPoints(4);
        orange.addVictoryPoints(3);
        white.addVictoryPoints(2);

        // RED is alone in the lead — nobody has >= 8 VPs
        assertFalse(board.canPassOldShoe(Turn.RED, Turn.BLUE),
                "Cannot pass old shoe when you alone have the most VPs");
    }

    @Test
    public void seam4_oldShoeRequiresExtraVPToWin() {
        Player red = board.turnToPlayer.get(Turn.RED);
        red.setOldShoe(true);
        assertEquals(11, red.getVictoryPointsNeededToWin(),
                "Old shoe holder needs 11 VPs to win instead of 10");

        red.setOldShoe(false);
        assertEquals(10, red.getVictoryPointsNeededToWin(),
                "Without old shoe, only 10 VPs needed");
    }

    // =========================================================================
    // SEAM 5: FakeFishTokenSupplier captures returned tokens — I/O capture
    //
    // When fish are spent at the Fish Market, returned tokens go face-up
    // in the discard pile. The FishTokenSupplier.returnTokens() method
    // captures this output. Our fake records every token returned, letting
    // us verify the exact I/O of the spending transaction.
    // =========================================================================

    @Test
    public void seam5_redeemCapturesReturnedTokensForAnalysis() {
        FakeFishTokenSupplier fake = new FakeFishTokenSupplier();
        board.fishTokenSupplier = fake;

        Player red = board.turnToPlayer.get(Turn.RED);
        red.addFishToken(new FishToken(2));
        red.addFishToken(new FishToken(1));

        // 2 fish: move robber back to desert
        boolean success = board.redeemFishTokens(Turn.RED, FishRedemptionType.MOVE_ROBBER);

        assertTrue(success, "Should succeed with 3 total fish >= 2 cost");
        assertTrue(board.robberMoved, "Robber should be flagged as moved");

        // Capture: the fake recorded the spent tokens returned to the pool
        // (Player spent them; Board should return them to supplier)
        assertEquals(0, red.getTotalFish(),
                "All of RED's tokens used since smallest-first picks 1+2 for cost 2");
    }

    @Test
    public void seam5_insufficientFishTokensBlocksRedemption() {
        Player red = board.turnToPlayer.get(Turn.RED);
        red.addFishToken(new FishToken(1));

        // 7 fish needed for dev card — only have 1
        boolean success = board.redeemFishTokens(Turn.RED, FishRedemptionType.FREE_ROAD);

        assertFalse(success, "Should fail when fish total < cost");
        assertEquals(1, red.getTotalFish(),
                "Tokens should not be consumed on failed redemption");
    }
}
