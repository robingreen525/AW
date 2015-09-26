// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

/** This class generates random locations. 
 * Created on 2 Feb, 2012
@author Adam Waite
@version $Rev: 1201 $, $Date: 2012-02-03 14:33:54 -0800 (Fri, 03 Feb 2012) $
 */
public class RandomLayout extends InitialLayout {
  private static final long DEFAULT_SEED = System.currentTimeMillis();
  private final long seed;
  private final LocationPicker picker;

  public RandomLayout(int rows, int cols, double frac) {
    this(rows, cols, frac, DEFAULT_SEED);
  }

  public RandomLayout(int rows, int cols, double frac, long seed) {
    super(rows, cols, frac);
    this.seed = seed;
    picker = new LocationPicker(rows, cols, seed);
  }

  public Set<Location> generateLocations() {
    this.initial_locations = picker.pickUniqueRandom(initial_number);
    return Location.deepCopy(initial_locations);
  }
}
