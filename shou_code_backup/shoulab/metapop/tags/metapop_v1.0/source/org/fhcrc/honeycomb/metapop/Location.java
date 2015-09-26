package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.resource.Resource;

import java.util.List;

/** 
 * A space that mediates the interaction between <code>Population</code>s and
 * <code>Resource</code>s.
 *
 * Created on 27 Jan, 2012
 *
 * @author Adam Waite
 * @version $Rev$, $Date: 2013-04-12 13:36:10 -0700 (Fri, 12 Apr 2013) $
 *
 */
public class Location {
    private Coordinate coordinate;
    private Resource resource;
    private Population population;

    public Location(Coordinate coordinate, Resource resource,
                    Population population)
    {
        this.coordinate = coordinate;
        this.resource = resource;
        this.population = population;
    }

    public Location(Coordinate coordinate, Resource resource) {
        this(coordinate, resource, null);
    }

    public Location(Coordinate coordinate, Population population) {
        this(coordinate, null, population);
    }

    public Location(Coordinate coordinate) {
        this(coordinate, null, null);
    }

    public Location(Location l) {
        this(l.getCoordinate(), l.getResource(), l.getPopulation());
    }

    public void mix(Location loc) {
        population.mix(loc.getPopulation());
    }

    public void mix(Population pop) {
        population.mix(pop);
    }

    public Population retrieveMigrants(double rate) {
        return population.retrieveMigrants(rate);
    }

    public void shiftFitness(List<Double> new_freqs) {
        population.shiftFitness(new_freqs);
    }

    public void dilute(double fraction) {
        population.dilute(fraction);
    }

    public double populationSize() {
        return population.getSize();
    }

    public void growPopulation() {
        population.grow(resource);
    }

    public void updateResource() {
        resource.update(population);
    }

    public int getRow() { return coordinate.getRow(); }
    public int getCol() { return coordinate.getCol(); }
    public Coordinate getCoordinate() { return coordinate; }
    public Resource getResource() { return resource; }
    public Population getPopulation() { return population; }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) {
            throw new 
                IllegalArgumentException("Location cannot be compared to " +
                                         "another type of object.");
        }
        if (this == obj) return true;
        return false;
    }

    @Override
    public String toString() {
        return new String("A location at " + getCoordinate() +
                          " with " + resource.getAmount() + " resource.\n" +
                          " Contents " + getPopulation());
    }

}
