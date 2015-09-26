package org.fhcrc.honeycomb.metapop.coordinate;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

/** 
 * Created on 10 Apr, 2013.
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public interface CoordinateProvider {
    Coordinate getCoordinate();
}
