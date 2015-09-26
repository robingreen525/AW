// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Set;
public class RandomLayoutTest {
  private final int rows = 10;
  private final int cols = 10;
  private final double initial_fraction_occupied = 0.1;
  private final long location_seed   = 1L;
  private final int expected_nOccupied = 
    (int) Math.round(rows*cols*initial_fraction_occupied);

  @Test
  public void initializeTest() {
    InitialLayout initial_layout = 
      new RandomLayout(rows,cols,initial_fraction_occupied,location_seed);
    assertEquals(expected_nOccupied, initial_layout.getInitialNumber());
  }

  @Test
  public void immutabilityOfInitialLocations() {
    InitialLayout initial_layout = 
      new RandomLayout(rows,cols,initial_fraction_occupied,location_seed);

    Set<Location> locs = initial_layout.generateLocations();
    locs.add(new Location(-1,-1));

    Set<Location> initial_locs = initial_layout.getInitialLocations();

    assertNotSame(locs, initial_locs);
    assertNotSame(initial_locs, initial_layout.getInitialLocations());
  }
}
