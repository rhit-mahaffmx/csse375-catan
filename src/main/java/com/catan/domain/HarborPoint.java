package com.catan.domain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HarborPoint extends CityPoint {
    private final ResourceType tradingResource;

    public HarborPoint(int x, int y, ResourceType tradingResource) {
        super(x, y);
        this.tradingResource = tradingResource;
    }

    public ResourceType getTradingResource() {
        return this.tradingResource;
    }

    public boolean isGeneric(){
        return tradingResource == ResourceType.NULL;
    }
}