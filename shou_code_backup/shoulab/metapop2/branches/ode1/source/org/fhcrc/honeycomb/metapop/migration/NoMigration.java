package org.fhcrc.honeycomb.metapop.migration;

import org.fhcrc.honeycomb.metapop.OccupiedLocations;

import java.util.List;

/** 
 * No migration.
 *
 * Created on 30 May, 2013
 *
 * @author Adam Waite
 * @version $Id: NoMigration.java 2105 2013-06-03 20:13:01Z ajwaite $
 *
 */
public class NoMigration extends MigrationRule {
    public NoMigration() { super(); }

    @Override
    public void migrate(OccupiedLocations ols) { }
}
