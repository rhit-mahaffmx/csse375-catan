package com.catan.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * AI player strategy that makes autonomous decisions based on board state.
 *
 * Uses a priority-based heuristic approach:
 * - Settlement placement: prefers locations adjacent to high-probability dice numbers
 *   (6, 8, 5, 9) and diverse resource types
 * - Road placement: extends toward the best unoccupied settlement spots
 * - Robber: targets the leading opponent's most productive hex
 * - Dev cards: buys when affordable and has fewer than 3
 * - City upgrades: upgrades the highest-resource-production settlement first
 */
public class AIPlayerStrategy implements PlayerStrategy {

    private final Random rand;

    public AIPlayerStrategy(Random rand) {
        this.rand = rand;
    }

    public AIPlayerStrategy() {
        this(new Random());
    }

    @Override
    public CityPoint chooseSettlementLocation(Board board, Player player) {
        CityPoint best = null;
        int bestScore = -1;

        for (CityPoint city : board.cityPoints) {
            if (city.hasSettlement()) continue;
            if (isTooCloseToAnySettlement(board, city)) continue;
            if (!isAdjacentToOwnedRoadOrSetup(board, city, player)) continue;

            int score = scoreSettlementLocation(city);
            if (score > bestScore) {
                bestScore = score;
                best = city;
            }
        }
        return best;
    }

    @Override
    public RoadPoint chooseRoadLocation(Board board, Player player) {
        RoadPoint best = null;
        int bestScore = -1;

        for (RoadPoint road : board.roadPoints) {
            if (road.hasRoad) continue;
            if (!isAdjacentToOwnedStructure(board, road, player.color)) continue;

            int score = scoreRoadLocation(board, road, player.color);
            if (score > bestScore) {
                bestScore = score;
                best = road;
            }
        }
        return best;
    }

    @Override
    public RobberPoint chooseRobberPlacement(Board board, Player player) {
        Turn leadingOpponent = findLeadingOpponent(board, player);
        RobberPoint best = null;
        int bestScore = -1;

        for (RobberPoint rp : board.robberPoints) {
            if (rp.hasRobber) continue;
            if (rp.resourceType == ResourceType.NULL) continue;
            if (isFriendlyRobberProtected(board, rp)) continue;

            int score = scoreRobberPlacement(board, rp, leadingOpponent);
            if (score > bestScore) {
                bestScore = score;
                best = rp;
            }
        }

        // Fallback: pick any non-protected, non-active robber point
        if (best == null) {
            for (RobberPoint rp : board.robberPoints) {
                if (!rp.hasRobber && !isFriendlyRobberProtected(board, rp)) return rp;
            }
        }
        return best;
    }

    private boolean isFriendlyRobberProtected(Board board, RobberPoint rp) {
        for (CityPoint city : board.cityPoints) {
            if (city.hasSettlement() && city.bordersHex(rp.diceNumber, rp.resourceType)) {
                Player owner = board.turnToPlayer.get(city.getOwner());
                if (owner != null && owner.getVictoryPoints() < 3) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Turn choosePlayerToRob(Board board, Player player, ArrayList<Player> eligible) {
        if (eligible.isEmpty()) return Turn.NONE;

        Player richest = null;
        int maxResources = -1;
        for (Player p : eligible) {
            if (p.color == player.color) continue;
            if (p.getTotalResources() > maxResources) {
                maxResources = p.getTotalResources();
                richest = p;
            }
        }
        return richest != null ? richest.color : eligible.get(0).color;
    }

    @Override
    public boolean shouldBuyDevCard(Board board, Player player) {
        return player.canPayForDevCard() && player.getDevCards().size() < 3;
    }

    @Override
    public CityPoint chooseCityUpgrade(Board board, Player player) {
        if (!player.canPayToUpgradeSettlement()) return null;

        CityPoint best = null;
        int bestScore = -1;

        for (CityPoint city : board.cityPoints) {
            if (!city.hasSettlement() || city.isCity) continue;
            if (city.getOwner() != player.color) continue;

            int score = scoreSettlementLocation(city);
            if (score > bestScore) {
                bestScore = score;
                best = city;
            }
        }
        return best;
    }

    @Override
    public boolean isAI() {
        return true;
    }

    // ==================== Scoring Helpers ====================

    int scoreSettlementLocation(CityPoint city) {
        int score = 0;
        HashMap<ResourceType, Boolean> resourceDiversity = new HashMap<>();

        for (Integer tileVal : city.tileValueToTerrain.keySet()) {
            Terrain terrain = city.tileValueToTerrain.get(tileVal);
            ResourceType rt = terrain.getResourceType();
            if (rt == ResourceType.NULL) continue;

            // Higher probability numbers get higher score
            score += diceNumberScore(tileVal);

            // Bonus for resource diversity
            if (!resourceDiversity.containsKey(rt)) {
                resourceDiversity.put(rt, true);
                score += 3;
            }
        }

        // Bonus for harbors
        if (city instanceof HarborPoint) {
            score += 5;
        }

        return score;
    }

    private int diceNumberScore(int number) {
        // 6 and 8 are rolled most often (5 ways each)
        switch (number) {
            case 6: case 8: return 10;
            case 5: case 9: return 8;
            case 4: case 10: return 6;
            case 3: case 11: return 4;
            case 2: case 12: return 2;
            default: return 0;
        }
    }

    private int scoreRoadLocation(Board board, RoadPoint road, Turn color) {
        int score = 1;
        for (CityPoint neighbor : road.neighbors) {
            if (!neighbor.hasSettlement()) {
                // Road leads to an open city point — good for future settlement
                score += scoreSettlementLocation(neighbor) / 3;
            }
            if (neighbor.getOwner() == color) {
                // Connected to our own settlement
                score += 2;
            }
        }
        return score;
    }

    private int scoreRobberPlacement(Board board, RobberPoint rp, Turn opponent) {
        int score = 0;
        for (CityPoint city : board.cityPoints) {
            if (!city.hasSettlement()) continue;
            if (city.getOwner() != opponent) continue;
            if (city.bordersHex(rp.diceNumber, rp.resourceType)) {
                score += city.isCity ? 4 : 2;
                score += diceNumberScore(rp.diceNumber);
            }
        }
        return score;
    }

    private Turn findLeadingOpponent(Board board, Player self) {
        Turn leader = Turn.NONE;
        int maxVP = -1;
        for (Turn t : new Turn[]{Turn.RED, Turn.BLUE, Turn.ORANGE, Turn.WHITE}) {
            if (t == self.color) continue;
            Player p = board.turnToPlayer.get(t);
            if (p != null && p.getVictoryPoints() > maxVP) {
                maxVP = p.getVictoryPoints();
                leader = t;
            }
        }
        return leader;
    }

    private boolean isTooCloseToAnySettlement(Board board, CityPoint target) {
        for (CityPoint city : board.cityPoints) {
            if (city.hasSettlement()) {
                if (board.withinTwoRoads(city, target)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAdjacentToOwnedRoadOrSetup(Board board, CityPoint city, Player player) {
        int round = board.turnStateMachine.getRound();
        // During setup rounds, any valid location is fine
        if (round <= 2) return true;

        // During normal play, must be adjacent to own road
        for (RoadPoint road : city.neighbors) {
            if (road.getOwner() == player.color) return true;
        }
        return false;
    }

    private boolean isAdjacentToOwnedStructure(Board board, RoadPoint road, Turn color) {
        for (CityPoint neighbor : road.neighbors) {
            if (neighbor.getOwner() == color) return true;
            for (RoadPoint adj : neighbor.neighbors) {
                if (adj.getOwner() == color && adj.hasRoad) return true;
            }
        }
        return false;
    }
}
