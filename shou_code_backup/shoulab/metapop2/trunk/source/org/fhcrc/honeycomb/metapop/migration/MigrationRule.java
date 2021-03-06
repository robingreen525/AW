package org.fhcrc.honeycomb.metapop.migration;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.OccupiedLocations;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinateProvider;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;

import java.util.List;

/** 
 * Controls migration
 *
 * Created on 30 May, 2013
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public abstract class MigrationRule implements CoordinateProvider {
    private double rate;
    private CoordinatePicker picker;
    private Coordinate coordinate;

    protected MigrationRule() {}

    public MigrationRule(double rate, CoordinatePicker picker) {
        this.rate = rate;
        this.picker = picker;
        picker.setProvider(this);
    }

    public abstract void migrate(OccupiedLocations ols);

    public double getRate() { return rate; }
    public CoordinatePicker getPicker() { return picker; }
    public void setCoordinate(Coordinate coord) {
        this.coordinate = coord;
    }


    @Override
    public Coordinate getCoordinate() { return coordinate; }

    @Override
    public String toString() {
        return String.format("%s, rate=%s, picker=%s",
                             this.getClass().getSimpleName(), rate, picker);
    }
}
