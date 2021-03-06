package org.fhcrc.honeycomb.metapop.coordinate;

import java.util.Queue;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/** 
 * Picks from a list of specified locations.
 *
 * Created on 11 Apr, 2013.
 * @author Adam Waite
 * @version $Rev: 1936 $, $Date: 2013-04-16 18:26:45 -0700 (Tue, 16 Apr 2013) $
 *
 * @see Coordinate
 *
 */
public class SpecifiedCoordinatePicker extends CoordinatePicker {
    private Queue<Coordinate> coords;

    public SpecifiedCoordinatePicker(final List<Coordinate> coords) {
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
    public String getType() { return new String("specified"); }

    @Override
    public String toString() { return new String("SpecifiedPicker"); }
}
