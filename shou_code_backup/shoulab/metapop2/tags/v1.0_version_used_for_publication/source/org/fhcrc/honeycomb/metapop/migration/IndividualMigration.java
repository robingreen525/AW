package org.fhcrc.honeycomb.metapop.migration;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.OccupiedLocations;

import org.fhcrc.honeycomb.metapop.coordinate.CoordinateProvider;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/** 
 * Individual migration.
 *
 * Created on 30 May, 2013
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class IndividualMigration extends MigrationRule {

    public IndividualMigration(double rate, CoordinatePicker picker) {
        super(rate, picker);
    }

    @Override
    public void migrate(OccupiedLocations ols) {

        // Loop over *copy* of occupied locations.
        for (Population pop:ols.copyList()) {
            if (pop.getSize() == 0) continue; 

            setCoordinate(pop.getCoordinate());
            RandomNumberUser rng = pop.getRNG();

            Population migrating_pop = pop.collectMigrants(getRate());
            if (migrating_pop.getSize() == 0) continue;

            for (Subpopulation subpop:migrating_pop.getSubpopulations()) {
                String id = subpop.getId();
                for (int i=0; i<subpop.getSize(); i++) {
                    Coordinate new_coord = getPicker().pick();
                    Subpopulation subpop_copy = new Subpopulation(subpop);
                    subpop_copy.setSize(1);
                    Population new_pop = new Population(
                            Arrays.asList(subpop_copy),
                            new_coord, 0.0, rng);

                    // alter *actual* occupied locations.
                    ols.addOrMix(new_pop);
                }
            }
        }
    }
}
