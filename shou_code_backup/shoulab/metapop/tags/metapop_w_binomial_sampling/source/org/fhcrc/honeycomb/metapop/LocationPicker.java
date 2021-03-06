// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

/** Generates {@link Location}s.
 *
 * Created on 31 Jan, 2012
@author Adam Waite
@version $Rev: 1208 $, $Date: 2012-02-03 20:33:57 -0800 (Fri, 03 Feb 2012) $
 */
public class LocationPicker {
  private static final long DEFAULT_SEED = System.nanoTime();
  private static Random rng;

  private final int row_limit;
  private final int column_limit;
  private final long seed;

  public LocationPicker(final int row_limit, final int column_limit, final long seed) {
    this.row_limit = row_limit;
    this.column_limit = column_limit;
    this.seed = seed;
    this.rng = new Random(seed);
  }

  public LocationPicker(final int row_limit, final int column_limit) {
    this(row_limit, column_limit, DEFAULT_SEED);
  }

  public Location pickRandom() {
    return new Location(rng.nextInt(row_limit), rng.nextInt(column_limit));
  }

  public List<Location> pickRandom(final int n) {
    List<Location> locations = new ArrayList<Location>(n);
    while (locations.size() < n) locations.add(pickRandom());
    return locations;
  }

  public Set<Location> pickUniqueRandom(final int n) {
    int max_n = row_limit*column_limit;
    if (n > max_n) {
      throw new IllegalArgumentException("Requested number of locations ("+
          n + ") is larger than the number of locations (" + max_n + ")");
    }

    Set<Location> locations = new HashSet<Location>(n);
    while (locations.size() < n) locations.add(pickRandom());
    return locations;
  }

  public List<Location> pickNonIdRandom(int n, Location current_location) {
    List<Location> locations = new ArrayList<Location>(n);
    while (locations.size() < n) {
      Location loc = pickRandom();
      if (!current_location.equals(loc)) locations.add(loc);
    }
    return locations;
  }

  public Location pickNonIdRandom(Location current_location) {
    return pickNonIdRandom(1, current_location).get(0);
  }

  public List<Location> pickNeighborRandom(int n, 
                                           Location current_location,
                                           int max_distance)
  {
    List<Location> locations = new ArrayList<Location>(n);
    int current_row = current_location.getRow();
    int current_col = current_location.getCol();
    int new_row, new_col;
    while (locations.size() < n) {
      do {
        new_row = getNewSpot(max_distance, current_row, row_limit);
        new_col = getNewSpot(max_distance, current_col, column_limit);
      } while (new_row == 0 && new_col == 0);
      locations.add(new Location(new_row, new_col));
    }
    return locations;
  }

  public Location pickNeighborRandom(Location current_location,
                                           int max_distance)
  {
    return pickNeighborRandom(1,current_location,max_distance).get(0);
  }

  int getNewSpot(int dist, int current, int limit) {
    int move = rng.nextInt(2*dist)-dist;
    int new_spot = move+current;
    if (new_spot > limit) {
      new_spot = new_spot-limit;
    } else if (new_spot < 1) {
      new_spot = limit+new_spot;
    }
    return new_spot;
  }
}
