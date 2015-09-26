package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/** 
 * A helper class to manage groups of {@code Population}s.  Only locations that
 * at one point contained a Population of non-zero size are tracked.  However,
 * if the size of a Population falls to zero, it is still tracked, as it might
 * contain resource.
 *
 * Created on 25 Apr, 2013
 * @author Adam Waite
 * @version $Id: OccupiedLocations.java 2144 2013-06-19 20:37:58Z ajwaite $
 *
 *
 *
 */
public class OccupiedLocations {
    private List<Population> list;
    private Map<Coordinate, Population> map;

    /** Constructor */
    public OccupiedLocations(List<Population> pops, int max_size) {
        list = new ArrayList<Population>(max_size);
        map = new HashMap<Coordinate, Population>(max_size);
        list = Population.copyPopulations(pops);
        for (Population pop:list) {
            map.put(pop.getCoordinate(), pop);
        }
    }

    /**
     * gets a List representation of the OccupiedLocations.
     *
     * @return a list of Populations.
     */
    public List<Population> getList() { return list; }

    /**
     * gets the number of populations currently tracked; these can have
     * zero population sizes.
     *
     * @return the size.
     */
    public int getSize() { return list.size(); }

    /**
     * gets a Map representation of the OccupiedLocations that indicates where
     * each Population is.
     *
     * @return a Map of Populations.
     */
    public Map<Coordinate, Population> getMap() { return map; }

    /**
     * checks whether the passed Coordinate is currently being tracked.
     *
     * @param coordinate the coordinate to check.
     * @return whether the coordinate is being tracked.
     */
    public boolean isOccupied(Coordinate coordinate) {
        return (map.get(coordinate) == null) ? false : true;
    }

    /**
     * gets the Population at the indicated Coordinate.
     *
     * @param coordinate the Coordinate to retrieve from.
     * @return the Population at this Coordinate.
     */
    public Population getPopulationAt(Coordinate coordinate) {
        return map.get(coordinate);
    }

    /**
     * makes a copy of the List representation and returns it.
     *
     * @return a copy of the List representation.
     */
    public List<Population> copyList() { 
        List<Population> copy = new ArrayList<Population>(list.size());
        for (Population pop:list) {
            copy.add(new Population(pop));
        }
        return copy;
    }

    /**
     * Mixes an incoming population with the resident Population, if there is
     * one, otherwise starts tracking this Population.
     *
     * @param incoming_pop the population to mix or add.
     */
    public void addOrMix(Population incoming_pop) {
        Population pop = map.get(incoming_pop.getCoordinate());
        if (pop == null) {
            add(incoming_pop);
        } else {
            pop.mix(incoming_pop);
        }
    }

    /**
     * Adds a Population.
     *
     * @param pop the Population to add.
     */
    public void add(Population pop) {
        list.add(pop);
        map.put(pop.getCoordinate(), pop);
    }

    @Override
    public String toString() {
        return map.toString();
    }
}

