// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.List;

public class LocationPickerTest {
    int rows = 10;
    int cols = 10;
    int distance = 3;
    int samples = 10;
    int max_n = rows*cols;
    long seed = 12345L;
    Location current = new Location(rows-1,cols-1);
    LocationPicker loc;

  @Before 
  public void setUp() {
    loc = new LocationPicker(rows, cols, seed);
  }

  @Test(expected=IllegalArgumentException.class)
  public void uniqueRandomMaxNError() {
    loc.pickUniqueRandom(max_n+1);
  }

  @Test
  public void correctSize() {
    assertEquals("random", samples, 
                 loc.pickRandom(samples).size());

    assertEquals("unique random", samples, 
                 loc.pickUniqueRandom(samples).size());

    assertEquals("nonID random", samples, 
                 loc.pickNonIdRandom(samples, current).size());

    assertEquals("neighbor random", samples, 
                 loc.pickNeighborRandom(samples, current, distance).size());
  }

  @Test
  public void ensureNonID() {
    int more_samples = (int) 1e3;

    List<Location> locs = loc.pickNonIdRandom(more_samples, current);
    for (Location l:locs) {
      if (l.equals(current)) fail("Picked current location");
    }
  }

  @Test
  public void neighborBoundary() {
    int dist    = 5;
    int current = 3;
    int limit   = 5;
    
    //System.out.println("\n\n**bound**");
    //for (int i=0; i<20; i++) loc.getNewSpot(dist, current, limit);
    // rand number is 1, move is -4, new spot is 4.
    int new_spot = loc.getNewSpot(dist, current, limit);
    assertEquals(4, new_spot);

    // rand number is 0, move is -5, new spot is 3.
    new_spot = loc.getNewSpot(dist, current, limit);
    assertEquals(3, new_spot);

    // rand number is 1.
    new_spot = loc.getNewSpot(dist, current, limit);

    // rand number is 8, move is 3, new spot is 1.
    new_spot = loc.getNewSpot(dist, current, limit);
    assertEquals(1, new_spot);

    // rand 5, 4
    for (int i=0; i<3; i++) loc.getNewSpot(dist, current, limit);

    // rand = 2, move = -3, new spot = 5
    new_spot = loc.getNewSpot(dist, current, limit);
    assertEquals(5, new_spot);


  }
}
