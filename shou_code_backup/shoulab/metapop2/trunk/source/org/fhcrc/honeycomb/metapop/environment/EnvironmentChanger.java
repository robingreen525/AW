package org.fhcrc.honeycomb.metapop.environment;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** Determines when the environment changes.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
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
