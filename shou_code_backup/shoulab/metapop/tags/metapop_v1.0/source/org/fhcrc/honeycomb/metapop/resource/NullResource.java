package org.fhcrc.honeycomb.metapop.resource;

import org.fhcrc.honeycomb.metapop.Population;

/** 
 * A resource that is not modelled.
 *
 * Created on 11 Apr, 2013
 *
 * @author Adam Waite
 * @version $Rev: 1925 $, $Date: 2013-04-10 19:24:28 -0700 (Wed, 10 Apr 2013) $
 *
 */
public class NullResource extends Resource {

    public NullResource(double amount) { super(amount); }

    public NullResource() { this(0); }

    @Override
    public void update(Population pop) { }
}
