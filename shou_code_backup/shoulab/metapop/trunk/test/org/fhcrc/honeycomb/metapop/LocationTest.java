package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Location;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

import org.fhcrc.honeycomb.metapop.resource.Resource;
import org.fhcrc.honeycomb.metapop.resource.NullResource;

public class LocationTest {
    private Coordinate a = new Coordinate(1, 2);
    private Coordinate b = new Coordinate(1, 2);
    private Coordinate c = new Coordinate(2, 2);
    private Coordinate n = null;
    private Resource null_r = new NullResource();

    private Location loc_a = new Location(a, null_r);
    private Location loc_b = new Location(b, null_r);
    private Location loc_c = new Location(c, null_r);

    @Test
    public void eqality() {
        assertEquals("failed - not identical to itself", loc_a, loc_a);
        assertNotEquals("failed - should not be equal", loc_a, loc_b);
        assertNotSame("failed - should not be same", loc_a, loc_b);
        assertNotEquals("failed - should not be equal", loc_a, loc_c);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidComparison() {
        loc_a.equals(a);
    }
}
