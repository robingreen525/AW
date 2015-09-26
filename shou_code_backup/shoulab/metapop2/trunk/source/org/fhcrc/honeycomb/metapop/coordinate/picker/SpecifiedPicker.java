package org.fhcrc.honeycomb.metapop.coordinate.picker;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/** 
 * Picks from a list of specified locations.
 *
 * Created on 11 Apr, 2013.
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 * @see Coordinate
 *
 */
public class SpecifiedPicker extends CoordinatePicker {
    private Queue<Coordinate> coords;

    public SpecifiedPicker(final List<Coordinate> coords) {
        super();
        if (coords == null) {
            throw new IllegalArgumentException("Coordinate list is null!");
        }

        this.coords = new LinkedList<Coordinate>();
        for (Coordinate coord : coords) {
            this.coords.add(new Coordinate(coord));
        }
    }

    @Override
    public Coordinate pick() {
        return coords.remove();
    }

    @Override
    public List<Coordinate> pick(int n) {
        List<Coordinate> ret = new ArrayList<Coordinate>(n);
        for (int i=0; i<n; i++) ret.add(coords.remove());
        return ret;
    }

    @Override
    public String getType() { return "specified"; }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
