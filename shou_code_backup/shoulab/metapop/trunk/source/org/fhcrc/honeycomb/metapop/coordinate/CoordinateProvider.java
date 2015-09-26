package org.fhcrc.honeycomb.metapop.coordinate;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

/** 
 * Created on 10 Apr, 2013.
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public interface CoordinateProvider {
    int getMaxRow();
    int getMaxCol();
    Coordinate getCoordinate();
}
