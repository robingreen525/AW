package org.fhcrc.honeycomb.metapop.coordinate;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

import java.util.List;
import java.util.ArrayList;

/** 
 * Generates a random <code>Coordinate</code>.
 *
 * Created on 8 Feb, 2012
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public class RandomPicker extends CoordinatePicker {

    public RandomPicker(int max_row, int max_col, 
                        boolean exclude_current,
                        RandomNumberUser rng) 
    {
        super(max_row, max_col, exclude_current, rng);
    }

    public RandomPicker(boolean exclude_current, RandomNumberUser rng) 
    {
        super(exclude_current, rng);
    }

    public RandomPicker(RandomNumberUser rng) { this(false, rng); }

    public RandomPicker(boolean exclude_current, 
                        CoordinateProvider provider, RandomNumberUser rng) 
    {
        super(exclude_current, provider, rng);
    }

    @Override
    public Coordinate pick() {
        if (max_row == 0 || max_col == 0) {
            throw new IllegalStateException(
                    "max_row or max_col has not been set");
        }

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

    @Override
    public String toString() {
        return new String("RandomPicker, seed=" + rng.getSeed());
    }
}
