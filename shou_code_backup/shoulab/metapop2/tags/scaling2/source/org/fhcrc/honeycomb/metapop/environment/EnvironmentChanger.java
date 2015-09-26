package org.fhcrc.honeycomb.metapop.environment;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** Determines when the environment changes.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Id: EnvironmentChanger.java 1990 2013-04-26 21:39:23Z ajwaite $
 */
public interface EnvironmentChanger {
    /**
     * Checks whether the environment changed.
     *
     * @return whether the environment changed.
     */
    boolean environmentChanged();

    /**
     * the probability that the environment will change.
     *
     * @return the probability that the environment will change.
     */
    double getProb();

    RandomNumberUser getRNG();
}
