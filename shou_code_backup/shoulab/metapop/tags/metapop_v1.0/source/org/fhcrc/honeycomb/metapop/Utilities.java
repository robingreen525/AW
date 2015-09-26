// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import java.util.List;
import java.util.ArrayList;

/** Useful functions that don't belong to a particular class.
 * Created on 27 Jan, 2012
@author Adam Waite
@version $Rev: 1847 $, $Date: 2013-04-03 17:02:18 -0700 (Wed, 03 Apr 2013) $
 */
public class Utilities {
  public static List<Double> copyDoubles(List<Double> items) {
    List<Double> clone = new ArrayList<Double>(items.size());
    for (Double d:items) { clone.add(new Double(d)); }
    return clone;
  }
}
