package org.fhcrc.honeycomb.metapop.migration;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.OccupiedLocations;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;

import java.util.List;
import java.util.ArrayList;

/** 
 * Propagule migration.
 *
 * Created on 30 May, 2013
 *
 * @author Adam Waite
 * @version $Id: PropaguleMigration.java 2105 2013-06-03 20:13:01Z ajwaite $
 *
 */
public class PropaguleMigration extends MigrationRule {

    public PropaguleMigration(double rate, CoordinatePicker picker) {
        super(rate, picker);
    }

    @Override
    public void migrate(OccupiedLocations ols) {
        // iterate over *copy* of occupied locations.
        for (Population pop:ols.copyList()) {
            setCoordinate(pop.getCoordinate());

            Population migrating_pop = pop.collectMigrants(getRate());
            if (migrating_pop.getSize() > 0) {
                migrating_pop.setCoordinate(getPicker().pick());
                migrating_pop.setResource(0.0);

                // alter *actual* populations
                ols.addOrMix(migrating_pop);
            }
        }
    }
}
