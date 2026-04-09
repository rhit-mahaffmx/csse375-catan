package com.catan.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class StandardFishTokenSupplier implements FishTokenSupplier {
    private final List<FishToken> faceDown = new ArrayList<>();
    private final List<FishToken> faceUp = new ArrayList<>();
    private final Random random;

    public StandardFishTokenSupplier(Random random) {
        this.random = random;
        for (int i = 0; i < 11; i++) faceDown.add(new FishToken(1));
        for (int i = 0; i < 10; i++) faceDown.add(new FishToken(2));
        for (int i = 0; i < 8; i++) faceDown.add(new FishToken(3));
        faceDown.add(FishToken.oldShoe());
        Collections.shuffle(faceDown, random);
    }

    @Override
    public FishToken draw() {
        if (faceDown.isEmpty()) {
            faceDown.addAll(faceUp);
            faceUp.clear();
            Collections.shuffle(faceDown, random);
        }
        if (faceDown.isEmpty()) {
            return null;
        }
        return faceDown.remove(faceDown.size() - 1);
    }

    @Override
    public void returnTokens(List<FishToken> tokens) {
        faceUp.addAll(tokens);
    }

    @Override
    public int remaining() {
        return faceDown.size();
    }
}
