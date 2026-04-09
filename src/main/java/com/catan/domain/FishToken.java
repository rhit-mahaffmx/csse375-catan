package com.catan.domain;

public class FishToken {
    private final int fishCount;
    private final boolean isOldShoe;

    public FishToken(int fishCount) {
        this.fishCount = fishCount;
        this.isOldShoe = false;
    }

    private FishToken(boolean oldShoe) {
        this.fishCount = 0;
        this.isOldShoe = true;
    }

    public static FishToken oldShoe() {
        return new FishToken(true);
    }

    public int getFishCount() {
        return fishCount;
    }

    public boolean isOldShoe() {
        return isOldShoe;
    }
}
