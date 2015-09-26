package org.fhcrc.honeycomb.metapop.mutation;

import org.fhcrc.honeycomb.metapop.Population;

import java.util.List;

/** 
 * Mutates one {@link org.fhcrc.honeycomb.metapop.Subpopulation} to another.
 *
 * Created on 27 May, 2013
 *
 * @author Adam Waite
 * @version $Id: MutationRule.java 2145 2013-06-19 21:15:54Z ajwaite $
 *
 */
public interface MutationRule {
    public void mutate(List<Population> pop);
}
