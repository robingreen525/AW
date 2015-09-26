package org.fhcrc.honeycomb.metapop.lookuptable;

/** 
 * Defines a lookup table for storing results of the ode.
 *
 * Created on 05 Aug, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public interface LookupTable {
    void setValue(double val, double ... keys);
    double getValue(double ... keys);
    void print();
}
