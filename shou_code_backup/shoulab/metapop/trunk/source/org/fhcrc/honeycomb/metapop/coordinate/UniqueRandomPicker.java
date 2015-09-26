package org.fhcrc.honeycomb.metapop.coordinate;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/** 
 * Generates unique, random {@link Coordinate}s.
 *
 * Created on 8 Feb, 2012
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public class UniqueRandomPicker extends RandomPicker {
    private Set<Coordinate> picked = new HashSet<Coordinate>();

    public UniqueRandomPicker(int max_rows, int max_cols, 
                              boolean exclude_current,
                              RandomNumberUser rng)
    {
        super(max_rows, max_cols, exclude_current, rng);
    }

    public UniqueRandomPicker(boolean exclude_current, RandomNumberUser rng)
    {
        super(exclude_current, rng);
    }

    public UniqueRandomPicker(RandomNumberUser rng) { super(rng); }

    public UniqueRandomPicker(boolean exclude_current, 
                              CoordinateProvider provider,
                              RandomNumberUser rng) 
    {
        super(exclude_current, provider, rng);
    }

    @Override
    public Coordinate pick() { 
        Coordinate coord;
        do {
            coord = super.pick();
        } while (picked.contains(coord));
        picked.add(coord);
        return coord;
    }

    @Override
    public List<Coordinate> pick(final int n) {
        int max_n = max_row * max_col;

        if (n > max_n) {
            throw new IllegalArgumentException(
                "Requested number of coordinates ("+ n +
                ") is larger than the number of coordinates (" + max_n + ")");
        }

        Set<Coordinate> coordinates = new HashSet<Coordinate>(n);
        while (coordinates.size() < n) coordinates.add(super.pick());
        return new ArrayList<Coordinate>(coordinates);
    }

    @Override
    public String getType() {
        return new String("global");
    }

    @Override
    public String toString() {
        return new String("UniqueRandomPicker, seed=" + rng.getSeed());
    }
}
