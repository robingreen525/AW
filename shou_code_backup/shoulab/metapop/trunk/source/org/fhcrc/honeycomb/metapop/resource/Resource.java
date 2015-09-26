package org.fhcrc.honeycomb.metapop.resource;

import org.fhcrc.honeycomb.metapop.Population;

/** 
 * A representation of the common good.
 *
 * Created on 10 Apr, 2013
 *
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public abstract class Resource {
    private double amount;

    public Resource(double amount) {
        this.amount = amount;
    }

    public Resource() { this(0); }

    public abstract void update(Population pop);

    public double getAmount() { return amount; }
}
