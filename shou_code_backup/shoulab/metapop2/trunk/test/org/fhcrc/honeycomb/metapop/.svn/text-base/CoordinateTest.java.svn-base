package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;

public class CoordinateTest {
    Coordinate coord1 = new Coordinate(1,2);
    Coordinate coord2 = new Coordinate(1,2);
    Coordinate coord3 = new Coordinate(2,1);
    Set<Coordinate> coord_set = new HashSet<Coordinate>(3);

    @Test
    public void equality() {
        assertTrue(coord1.equals(coord2));
        assertFalse(coord1.equals(coord3));
    }

    @Test
    public void hashCodeEquality() {
        assertTrue(coord1.hashCode() == coord2.hashCode());
        assertFalse(coord1.hashCode() == coord3.hashCode());
    }

    @Test
    public void copy() {
        Coordinate coord_copy = new Coordinate(coord1);
        Coordinate bad_copy = coord1;

        assertSame(coord1, bad_copy);
        assertNotSame(coord1, coord_copy);
    }
}
