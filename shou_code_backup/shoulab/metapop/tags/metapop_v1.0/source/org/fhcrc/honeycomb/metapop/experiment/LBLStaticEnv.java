package org.fhcrc.honeycomb.metapop.experiment;

import org.fhcrc.honeycomb.metapop.environmentchanger.EnvironmentChanger;
import org.fhcrc.honeycomb.metapop.environmentchanger.StaticEnvironment;
import org.fhcrc.honeycomb.metapop.resource.Resource;
import org.fhcrc.honeycomb.metapop.resource.NullResource;

/** 
 * Implements a static environment.
 *
 * Created on 8 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 1931 $, $Date: 2013-04-12 11:26:27 -0700 (Fri, 12 Apr 2013) $
 *
 */
public abstract class LBLStaticEnv extends LinearBeforeLoad {

    public LBLStaticEnv(String args[]) { super(args); }

    @Override
    public EnvironmentChanger makeEnvChanger() {
        return new StaticEnvironment();
    }

    @Override
    public Resource makeResource() {
        return new NullResource();
    }
}
