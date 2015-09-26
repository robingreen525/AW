// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import java.util.List;
import java.util.ArrayList;

/** Useful functions that don't belong to a particular class.
 * Created on 27 Jan, 2012
@author Adam Waite
@version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 */
public class Utilities {
  public static List<Double> copyDoubles(List<Double> items) {
    List<Double> clone = new ArrayList<Double>(items.size());
    for (Double d:items) { clone.add(new Double(d)); }
    return clone;
  }

  public static List<Double> copyDoubles(double[] items) {
    List<Double> clone = new ArrayList<Double>(items.length);
    for (Double d:items) { clone.add(d); }
    return clone;
  }

  public static List<Integer> copyInts(List<Integer> items) {
    List<Integer> clone = new ArrayList<Integer>(items.size());
    for (Integer d:items) { clone.add(new Integer(d)); }
    return clone;
  }
}
