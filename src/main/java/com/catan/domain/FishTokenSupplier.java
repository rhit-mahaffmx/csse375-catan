package com.catan.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface FishTokenSupplier {

    FishToken draw();

    void returnTokens(List<FishToken> tokens);

    int remaining();
}
