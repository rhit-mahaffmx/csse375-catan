package com.catan.domain;

import java.util.HashMap;
import java.util.Map;

public class TitleBonus {
    private final int minimumThreshold;
    private final int vpBonus;
    private Turn currentHolder;
    private int currentHolderCount;

    public TitleBonus(int minimumThreshold, int vpBonus) {
        this.minimumThreshold = minimumThreshold;
        this.vpBonus = vpBonus;
        this.currentHolder = Turn.BANK;
        this.currentHolderCount = 0;
    }

    public Turn getCurrentHolder() {
        return currentHolder;
    }

    public int getCurrentHolderCount() {
        return currentHolderCount;
    }

    public void evaluate(HashMap<Turn, Integer> counts, HashMap<Turn, Player> turnToPlayer) {
        if (currentHolder != Turn.BANK) {
            int holderCount = counts.getOrDefault(currentHolder, 0);
            if (holderCount < minimumThreshold) {
                turnToPlayer.get(currentHolder).addVictoryPoints(-vpBonus);
                currentHolder = Turn.BANK;
                currentHolderCount = 0;
            }
        }

        for (Map.Entry<Turn, Integer> entry : counts.entrySet()) {
            Turn turn = entry.getKey();
            if (turn == Turn.BANK) continue;
            int count = entry.getValue();

            if (count >= minimumThreshold && count > currentHolderCount && turn != currentHolder) {
                if (currentHolder != Turn.BANK) {
                    turnToPlayer.get(currentHolder).addVictoryPoints(-vpBonus);
                }
                turnToPlayer.get(turn).addVictoryPoints(vpBonus);
                currentHolder = turn;
                currentHolderCount = count;
            }
        }
    }
}
