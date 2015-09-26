// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;
import java.util.Set;
import java.util.Random;

/** InitialLayout defines the initial layout of a <class>World</class>.
 * Created on 26 Jan, 2012
@author Adam Waite
@version $Rev: 1201 $, $Date: 2012-02-03 14:33:54 -0800 (Fri, 03 Feb 2012) $
 */
abstract public class InitialLayout {
  protected final int rows;
  protected final int cols;
  protected final double initial_fraction;
  protected final int initial_number;
  protected Set<Location> initial_locations;

  public InitialLayout(int rows, int cols, double frac) {
    this.rows = rows;
    this.cols = cols;
    initial_fraction = frac;
    initial_number = (int) Math.round(frac * rows * cols);
  }

  abstract public Set<Location> generateLocations();

  public int getRows() { return rows; }

  public int getCols() { return cols; }

  public int getInitialNumber() { return initial_number; }

  public double getInitialFraction() { return initial_fraction; }

  public Set<Location> getInitialLocations() { 
    if (initial_locations == null) {
      throw new NullPointerException("Initial locations " +
                                     " have not been generated.");
    }
    return Location.deepCopy(initial_locations);
  }

  public void saveToFile(String f) {
  }

}
