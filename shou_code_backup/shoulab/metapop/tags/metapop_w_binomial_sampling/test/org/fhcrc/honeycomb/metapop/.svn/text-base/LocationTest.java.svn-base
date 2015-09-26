// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;

public class LocationTest {
  Location loc1 = new Location(1,2);
  Location loc2 = new Location(1,2);
  Location loc3 = new Location(2,1);
  Set<Location> loc_set = new HashSet<Location>(3);

  @Before public void setUp() {
    loc_set.add(loc1);
    loc_set.add(loc2);
    loc_set.add(loc3);
  }

  @Test
  public void equality() {
    assertTrue(loc1.equals(loc2));
    assertFalse(loc1.equals(loc3));
  }

  @Test
  public void hashCodeEquality() {
    assertTrue(loc1.hashCode() == loc2.hashCode());
    assertFalse(loc1.hashCode() == loc3.hashCode());
  }

  @Test
  public void immutability() {
    Location loc_copy = new Location(loc1);
    Location bad_copy = loc1;

    Location loc_copy2 = loc1.deepCopy();

    Set<Location> loc_set_copy = Location.deepCopy(loc_set);

    assertSame(loc1, bad_copy);
    assertNotSame(loc1, loc_copy);
    assertNotSame(loc1, loc_copy2);
    assertNotSame(loc_set, loc_set_copy);
  }
}
