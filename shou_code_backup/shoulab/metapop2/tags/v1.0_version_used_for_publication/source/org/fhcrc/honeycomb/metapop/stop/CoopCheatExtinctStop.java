package org.fhcrc.honeycomb.metapop.stop;

import org.fhcrc.honeycomb.metapop.World;

/** 
 * Stops if all cooperators or all cheaters go extinct.
 *
 * Created on 12 May, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class CoopCheatExtinctStop extends StopCondition {

    public boolean isMet() {
        boolean coops_extinct = false;
        boolean cheats_extinct = false;

        if ((world.getSizeById("anc_coop") == 0 &&
             world.getSizeById("evo_coop") == 0))
        {
            System.out.println("coops extinct at step " + world.getStep());
            coops_extinct = true;
        }

        if ((world.getSizeById("anc_cheat") == 0 &&
             world.getSizeById("evo_cheat") == 0))
        {
            System.out.println("cheats extinct at step " + world.getStep());
            cheats_extinct = true;
        }

        return (coops_extinct || cheats_extinct);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
