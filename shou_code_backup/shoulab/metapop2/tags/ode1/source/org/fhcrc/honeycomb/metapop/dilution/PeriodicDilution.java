package org.fhcrc.honeycomb.metapop.dilution;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.StepProvider;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/** Dilution occurs every set number of timesteps.
 *
 * Created on 26 Apr, 2013
 * @author Adam Waite
 * @version $Id: PeriodicDilution.java 2177 2013-06-26 22:05:32Z ajwaite $
 *
 */
public class PeriodicDilution implements DilutionRule {
    private double fraction;
    private int every;
    private StepProvider step_provider;

    public PeriodicDilution(double fraction, int every,
                            StepProvider step_provider)
    {
        this.fraction = fraction;
        this.every = every;
        this.step_provider = step_provider;
    }

    public PeriodicDilution(double fraction, int every)
    {
        this(fraction, every, null);
    }

    @Override
    public Map<Coordinate, Double> generate(List<Population> pops) {
        Map<Coordinate, Double> dilution_map = 
            new HashMap<Coordinate, Double>(pops.size());

        int step = step_provider.getStep();

        if (step > 0 && step % every == 0) {
            for (Population pop:pops) {
                dilution_map.put(pop.getCoordinate(), fraction);
            }
        }
        return dilution_map;
    }

    @Override
    public void setStepProvider(StepProvider sp) {
        this.step_provider = sp;
    }

    @Override
    public String toString() {
        return new String(super.toString() + ", fraction=" + fraction + 
                          ", every=" + every);
                          
    }
}
