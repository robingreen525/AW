package org.fhcrc.honeycomb.metapop.mutation;

import org.fhcrc.honeycomb.metapop.Population;

import java.util.List;

/** 
 * No mutation.
 *
 * Created on 29 May, 2013
 *
 * @author Adam Waite
 * @version $Id: NoMutation.java 2092 2013-05-31 00:01:30Z ajwaite $
 *
 */
public class NoMutation implements MutationRule {
    public void mutate(List<Population> pop) {}
}
