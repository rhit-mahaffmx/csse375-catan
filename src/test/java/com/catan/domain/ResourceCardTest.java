package com.catan.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ResourceCardTest {
    @Test
    public void testResourceCardUsesEnum() {
        ResourceCard testResourceCard = new ResourceCard(ResourceType.SHEEP);
        assertEquals(ResourceType.SHEEP, testResourceCard.getResourceType());
    }

}
