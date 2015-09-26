package org.fhcrc.honeycomb.metapop;

/**
 * Returns a number that can be interpreted as a step.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 */
public interface StepProvider {
    int getStep();
    int incrementStep();
}
