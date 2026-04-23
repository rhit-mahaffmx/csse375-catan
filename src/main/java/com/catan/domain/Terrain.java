package com.catan.domain;

public enum Terrain {
    DESERT(ResourceType.NULL),
    LAKE(ResourceType.NULL),
    FISHING_GROUND(ResourceType.NULL),
    FIELD(ResourceType.WHEAT),
    FOREST(ResourceType.WOOD),
    HILL(ResourceType.BRICK),
    MOUNTAIN(ResourceType.ORE),
    PASTURE(ResourceType.SHEEP);

    private final ResourceType resourceType;

    Terrain(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }
}
