package org.fhcrc.honeycomb.metapop.dilution;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.StepProvider;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

import java.util.List;
import java.util.Map;

/** 
 * Dilutes a world.
 *
 * Created on 26 Apr, 2013
 *
 * @author Adam Waite
 * @version $Id: DilutionRule.java 2170 2013-06-26 01:55:29Z ajwaite $
 *
 */
public interface DilutionRule {
    /**
     * Given a list of {@code Population}s, returns a map describing 
     * the amount by which each {@code Coordinate} should be diluted.
     *
     * @param pops the list of {@link Population}s to be diluted.
     *
     * @return a map specifying which {@link Coordinate}s should be diluted.
     */
    public Map<Coordinate, Double> generate(List<Population> pops);

    public void setStepProvider(StepProvider step_provider);
}
