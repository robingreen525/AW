package org.fhcrc.honeycomb.metapop.environmentchanger;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

/** Determines when the environment changes.
 *
 * Created on 23 Feb, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
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
