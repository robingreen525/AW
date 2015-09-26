// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import java.util.Set;
import java.util.HashSet;

/** A <class>Location</class> represents a set of row/column coordinates.
 * Created on 27 Jan, 2012
 * @author Adam Waite
 * @version $Rev: 1201 $, $Date: 2012-02-03 14:33:54 -0800 (Fri, 03 Feb 2012) $
 */
public class Location {
  private final int row;
  private final int col;

  public Location(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public Location(Location l) {
    this(l.getRow(), l.getCol());
  }

  public int getRow() { return row; }
  public int getCol() { return col; }

  public static Set<Location> deepCopy(Set<Location> ls) {
    Set<Location> new_locs = new HashSet<Location>(ls.size());
    for (Location l:ls) new_locs.add(new Location(l));
    return new_locs;
  }

  public Location deepCopy() { return new Location(this); }


  @Override
  public String toString() { 
    return new String("\nrow: "+row+" col: "+col);
  }

  @Override
  public boolean equals(Object obj) { 
    if (this == obj) return true;
    if (!(obj instanceof Location)) return false;

    Location loc = (Location) obj;
    return loc.getRow() == row && loc.getCol() == col;
  }

  @Override
  public int hashCode() {
    return (Integer.toString(row) + Integer.toString(col)).hashCode();
  }
}
