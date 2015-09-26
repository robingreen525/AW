package org.fhcrc.honeycomb.metapop.coordinate;

import org.fhcrc.honeycomb.metapop.World;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;

import java.util.List;

/** 
 * Generates <code>Coordinate</code>s for <code>Location</code>s.
 *
 * Created on 10 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 * @see Coordinate
 *
 */
public abstract class CoordinatePicker {
    protected int max_row;
    protected int max_col;
    protected boolean exclude_current;
    protected CoordinateProvider provider;
    protected RandomNumberUser rng;

    protected CoordinatePicker() {}

    protected CoordinatePicker(int max_row, int max_col,
                               boolean exclude_current,
                               CoordinateProvider provider,
                               RandomNumberUser rng)
    {
        this.max_row = max_row;
        this.max_col = max_col;
        this.exclude_current = exclude_current;
        this.rng = rng;

        if (provider != null) setProvider(provider);
    }

    public CoordinatePicker(int max_row, int max_col,
                            boolean exclude_current,
                            RandomNumberUser rng)
    {
        this(max_row, max_col, exclude_current, null, rng);
    }

    public CoordinatePicker(boolean exclude_current, RandomNumberUser rng)
    {
        this(0, 0, exclude_current, null, rng);
    }

    public CoordinatePicker(boolean exclude_current,
                            CoordinateProvider provider,
                            RandomNumberUser rng)
    {
        this(0, 0, exclude_current, provider, rng);
    }

    public void setProvider(CoordinateProvider provider) {
        this.provider = provider;
        this.max_row = provider.getMaxRow();
        this.max_col = provider.getMaxCol();
    }

    public CoordinateProvider getProvider() { return provider; }

    public RandomNumberUser getRNG() { return rng; }

    public abstract Coordinate pick();
    public abstract List<Coordinate> pick(int n);

    public abstract String getType();
}
