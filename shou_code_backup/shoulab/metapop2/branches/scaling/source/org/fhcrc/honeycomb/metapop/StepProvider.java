package org.fhcrc.honeycomb.metapop;

/**
 * Returns a number that can be interpreted as a step.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Id: StepProvider.java 2003 2013-04-30 02:18:33Z ajwaite $
 */
public interface StepProvider {
    int getStep();
    int incrementStep();
}
