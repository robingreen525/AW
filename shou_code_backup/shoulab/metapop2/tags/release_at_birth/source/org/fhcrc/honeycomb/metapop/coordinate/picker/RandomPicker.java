package org.fhcrc.honeycomb.metapop.coordinate.picker;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinateProvider;

import java.util.List;
import java.util.ArrayList;

/** 
 * Generates a random <code>Coordinate</code>.
 *
 * Created on 8 Feb, 2012
 * @author Adam Waite
 * @version $Id: RandomPicker.java 2179 2013-06-26 22:09:58Z ajwaite $
 *
 */
public class RandomPicker extends CoordinatePicker {

    public RandomPicker(int max_row, int max_col,
                        boolean exclude_current, 
                        CoordinateProvider provider, RandomNumberUser rng) 
    {
        super(max_row, max_col, exclude_current, provider, rng);
    }

    public RandomPicker(int max_row, int max_col, 
                        boolean exclude_current,
                        RandomNumberUser rng) 
    {
        this(max_row, max_col, exclude_current, null, rng);
    }


    public RandomPicker(int max_row, int max_col, RandomNumberUser rng) {
        this(max_row, max_col, false, rng);
    }


    @Override
    public Coordinate pick() {
        Coordinate coord = new Coordinate(rng.getNextInt(1, max_row),
                                          rng.getNextInt(1, max_col));
        if (exclude_current == true) {
            if (provider.getCoordinate() == null) {
                throw new NullPointerException(
                        "Can't exclude: current coordinate is null!");
            }

            Coordinate current = provider.getCoordinate();
            while (coord.equals(current)) {
                coord = new Coordinate(rng.getNextInt(1, max_row),
                                       rng.getNextInt(1, max_col));
            }
        }
        return coord;
    }

    @Override
    public List<Coordinate> pick(final int n) {
        List<Coordinate> coordinates = new ArrayList<Coordinate>(n);
        while (coordinates.size() < n) coordinates.add(pick());
        return coordinates;
    }

    @Override
    public String getType() {
        return new String("global");
    }
}
