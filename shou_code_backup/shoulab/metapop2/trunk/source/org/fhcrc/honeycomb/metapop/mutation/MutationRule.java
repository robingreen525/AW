package org.fhcrc.honeycomb.metapop.mutation;

import org.fhcrc.honeycomb.metapop.Population;

import java.util.List;

/** 
 * Mutates one {@link org.fhcrc.honeycomb.metapop.Subpopulation} to another.
 *
 * Created on 27 May, 2013
 *
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public interface MutationRule {
    public void mutate(List<Population> pop);
}
