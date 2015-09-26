package org.fhcrc.honeycomb.metapop.migration;

import org.fhcrc.honeycomb.metapop.OccupiedLocations;

import java.util.List;

/** 
 * No migration.
 *
 * Created on 30 May, 2013
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class NoMigration extends MigrationRule {
    public NoMigration() { super(); }

    @Override
    public void migrate(OccupiedLocations ols) { }
}
