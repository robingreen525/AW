package org.fhcrc.honeycomb.metapop.environmentchanger;

import org.fhcrc.honeycomb.metapop.World;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;

import java.util.Queue;
import java.util.List;
import java.util.LinkedList;

/** 
 * Changes the environment at the specified intervals.
 *
 * Created on 23 Feb, 2013
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 */
public class SpecifiedEnvironmentChanger implements EnvironmentChanger {
    private World world;
    private Queue<Integer> change_at;

    public SpecifiedEnvironmentChanger(World world, List<Integer> change_at)
    {
        if (change_at == null || change_at.size() == 0) {
            throw new IllegalArgumentException("List cannot be empty.");
        }
        this.world = world;
        this.change_at = new LinkedList<Integer>(change_at);
    }

    public SpecifiedEnvironmentChanger(List<Integer> change_at)
    {
        this(null, change_at);
    }

    public boolean environmentChanged() {
        if (world == null) {
            throw new UnsupportedOperationException("need a World");
        }
        return checkEnvironment(world.getStep());
    }

    public boolean checkEnvironment(int step) {
        if (change_at.isEmpty()) {
            return false;
        } else if (step == change_at.peek()) {
            change_at.remove();
            return true;
        }
        return false;
    }

    /**
     * The environment will change, so return 1.
     */
    public double getProb() { return 1.0; }

    public RandomNumberUser getRNG() { return null; }

    @Override
    public String toString() {
        return new String("SpecifiecEnvironmentChanger, " +
                          "change at=" + change_at);
    }
}
