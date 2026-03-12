package com.catan.domain;

public class ResourceCard {
    private final ResourceType resourceType;

    public ResourceCard(ResourceType resourceType) {

        this.resourceType = resourceType;
    }

    public ResourceType  getResourceType() {
        return this.resourceType;
    }
}

