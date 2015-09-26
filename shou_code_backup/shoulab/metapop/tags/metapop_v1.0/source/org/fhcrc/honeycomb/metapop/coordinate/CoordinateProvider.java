package org.fhcrc.honeycomb.metapop.coordinate;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

/** 
 * Created on 10 Apr, 2013.
 * @author Adam Waite
 * @version $Rev: 1925 $, $Date: 2013-04-12 11:26:27 -0700 (Fri, 12 Apr 2013) $
 *
 */
public interface CoordinateProvider {
    int getMaxRow();
    int getMaxCol();
    Coordinate getCoordinate();
}
