package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;
import org.fhcrc.honeycomb.metapop.ode.ConsumptionODE;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.math.BigDecimal;

/** 
 * Operates on {@link Subpopulation}s.
 *
 * Created on 24 Apr, 2013
 * @author Adam Waite
 * @version $Rev: 2301 $, $Date: 2013-08-16 17:26:24 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class Population {
    private static final double RESOURCE_TOL = -1e-6;
    private static final double MIN_RESOURCE = 1e-6;
    private static final double DEFAULT_CAPACITY = 1e6;

    private static ConsumptionODE ode;

    private List<Subpopulation> subpopulations;
    private double capacity;
    private double resource;
    private double prev_resource;
    private Coordinate coordinate;
    private int n_subpopulations;
    private HashMap<String, Integer> size_by_id;
    private RandomNumberUser rng;

    /** Constructor. */
    public Population(final List<Subpopulation> subpopulations,
                      final Coordinate coordinate,
                      final double resource,
                      final double capacity,
                      final RandomNumberUser rng)
    {
        this.subpopulations = copySubpopulations(subpopulations);
        if (coordinate != null) {
            this.coordinate = new Coordinate(coordinate);
        }
        this.n_subpopulations = this.subpopulations.size();
        this.resource = resource;
        this.prev_resource = resource;
        this.capacity = capacity;
        this.size_by_id = new HashMap<String, Integer>(n_subpopulations);
        this.rng = rng;

        updateSizeById();
        if (ode == null) {
            this.ode = new ConsumptionODE(this);
        }
    }
    public Population(final List<Subpopulation> subpopulations,
                      final Coordinate coordinate,
                      final double resource,
                      final RandomNumberUser rng)
    {
        this(subpopulations, coordinate, resource, DEFAULT_CAPACITY, rng);
    }

    /**
     * Generates this {@code Population} by randomizing the initial numbers
     * of each {@code Subpopulation} and its {@code Coordinate}.
     *
     * @param subpops the {@link Subpopulation}s 
     * @param resource the amount of resource.
     * @param cp a {@link CoordinatePicker} to pick the initial locations of
     * the {@code Populations}.
     * @param rng a {@link RandomNumberUser}.
     *
     */
    public Population(final List<Subpopulation> subpops,
                      final double resource,
                      final CoordinatePicker cp,
                      final RandomNumberUser rng)
    {
        this(subpops, null, resource, rng);

        for (Subpopulation subpop:subpopulations) {
            subpop.setSize((int) rng.getNextPoisson(subpop.getSize()));
        }

        this.coordinate = cp.pick();
        updateSizeById();
    }

    /**
     * generates {@code Population}s.
     *
     * @param n the number of {@code Population}s to make
     * @param subpops the {@link Subpopulation}s 
     * @param resource the amount of resource.
     * @param cp a {@link CoordinatePicker} to pick the initial locations of
     * the {@code Populations}.
     * @param rng a {@link RandomNumberUser}.
     *
     * @return the {@code Population}s
     *
     */
    public static List<Population> generate(final int n,
                                            final List<Subpopulation> subpops,
                                            final double resource,
                                            final CoordinatePicker cp,
                                            final RandomNumberUser rng)
    {
        List<Population> populations = new ArrayList<Population>(n);

        for (int i=0; i<n; i++) {
            populations.add(new Population(subpops, resource, cp, rng));
        }
        return populations;
    }

    /** Copy constructor. */
    public Population(Population pop) {
        this(pop.getSubpopulations(), pop.getCoordinate(), pop.getResource(),
             pop.getRNG());
    }

    // Getters

    /**
     * Extracts a copy of the {@link Subpopulation} <code>List</code> from the
     * population.
     *
     * @return a copy of the Subpopulations.
     */
    public List<Subpopulation> getSubpopulations() {
        return subpopulations;
    }

    /**
     * returns the number of {@link Subpopulation}s.
     *
     * @return the number of Subpopulations.
     */
    public int getNSubpopulations() { return n_subpopulations; }

    public static List<Population> copyPopulations(List<Population> pops) {
        List<Population> copy = new ArrayList<Population>(pops.size());
        for (Population pop:pops) {
            copy.add(new Population(pop));
        }
        return copy;
    }

    public List<Subpopulation> copySubpopulations(List<Subpopulation> subs) {
        List<Subpopulation> copy = new ArrayList<Subpopulation>(subs.size());
        for (Subpopulation sub:subs) copy.add(new Subpopulation(sub));
        return copy;
    }

    /**
     * returns the maximum size.
     * @return the maximum size.
     */
    public double getCapacity() { return capacity; }

    /**
     * returns the {@link RandomNumberUser}.
     * @return the {@code RandomNumberUser}.
     */
    public RandomNumberUser getRNG() { return rng; }

    /**
     * returns the amount of resource.
     * @return the amount of resource.
     */
    public double getResource() { return resource; }

    /**
     * sets the amount of resource.
     */
    public void setResource(double new_resource) {
        this.resource = new_resource;
    }

    /**
     * sets the carrying capacity.
     */
    public void setCapacity(double new_capacity) {
        this.capacity = new_capacity;
    }

    /**
     * returns a copy of the {@link Coordinate}.
     * @return a copy of the Coordinate.
     */
    public Coordinate getCoordinate() { return new Coordinate(coordinate); }

    /**
     * returns the total population size.
     * @return the total population size.
     */
    public int getSize() { return calculateSize(); }

    /** calculates the total population size.  */
    private int calculateSize() {
        int size = 0;
        for (Subpopulation sub:subpopulations) {
            size += sub.getSize();
        }
        return size;
    }

    public void grow() { grow(1.0); }

    /** 
     * Updates subpopulation sizes.  Uses {@link ConsumptionODE} to determine
     * the average amount of resource available during the time step, then
     * uses this to calculate the (whole) number of births and deaths.
     */
    public void grow(double timestep_length) {
        if (getSize() == 0) return;

        prev_resource = resource;

        //ConsumptionODE ode = new ConsumptionODE(this);
        ode.integrate(this);
        resource = ode.integrateResource();

        if (resource < RESOURCE_TOL) {
            System.out.println("resource < RESOURCE_TOL: " +
                               new BigDecimal(resource));
            System.out.println("second attempt...");
            System.out.println("resource was " + 
                               new BigDecimal(prev_resource));
            resource = prev_resource + MIN_RESOURCE;
            System.out.println("resource set to " +
                               new BigDecimal(resource));
            ode.integrate(this);
            resource = ode.integrateResource();
            System.out.println("resource now: "  + resource);

                if (resource < RESOURCE_TOL) {
                    String fail = 
                            "resource < RESOURCE_TOL: " +
                            new BigDecimal(resource) + "\n" +
                            "prev resource: " + new BigDecimal(prev_resource) +
                            "\npop:\n" + this +
                            "\node:\n" + ode.toString();
                    throw new RuntimeException(fail);
                }
        } else if (resource < 0) {
            removeResidualResource();
        }
        for (Subpopulation subpop:subpopulations) {
            int births = subpop.getBirths(resource);
            int deaths = subpop.getDeaths(resource);

            subpop.setSize(subpop.getSize() + births - deaths);
        }
    }

    /**
     * dilutes population by calling {@link #collectMigrants}, and reducing
     * the amount of resource.
     *
     * @param fraction the fraction to dilute the {@code Population}.
     */
    public void dilute(double fraction) {
        collectMigrants(fraction);
        resource *= (1-fraction);
        removeResidualResource();
    }

    private void removeResidualResource() {
        if (resource < MIN_RESOURCE) resource = 0.0;
    }

    /**
     * collects the specified fraction of each {@link Subpopulation} and
     * returns a new Population containing them.  The Population is otherwise
     * identical to the one the migrants came from.
     *
     * @param migration_rate the fraction of migrants to retrieve from each
     *                       Subpopulation.
     * @return               a Population containing the migrants which is
     *                       otherwise identical to the original population.
     */
    public Population collectMigrants(double migration_rate) {
        double no_resource = 0.0;
        List<Subpopulation> migrants = 
            new ArrayList<Subpopulation>(n_subpopulations);
        for (Subpopulation sub:subpopulations) {
            migrants.add(sub.retrieveMigrants(migration_rate));
        }
        Population migrant_pop = new Population(migrants, coordinate,
                                                no_resource, getRNG());
        return migrant_pop;
    }

    /**
     * Mixes this {@code Population} with an incoming migrant
     * {@code Population}.
     *
     * @param incoming the incoming {@code Population}.
     */
    public void mix(final Population incoming) {
        for (Subpopulation in_sub:incoming.getSubpopulations()) {
            String in_sub_id = in_sub.getId();
            Subpopulation res_sub = getSubpopById(in_sub_id);
            if (res_sub == null) {
                subpopulations.add(new Subpopulation(in_sub));
                n_subpopulations += 1;
            } else {
                res_sub.setSize(res_sub.getSize() + in_sub.getSize());
            }
        }
    }

    /** 
     * sets the Coordinate.
     *
     * @param coord the desired Coordinate.
     */
    public void setCoordinate(Coordinate coord) {
        this.coordinate = coord;
    }

    /** 
     * gets a Subpopulation based on its id.  Returns {@code null} if that
     * id does not exist in this Population.
     *
     * @param id the id to get.
     *
     */
    public Subpopulation getSubpopById(String id) {
        for (Subpopulation subpop:subpopulations) {
            if (subpop.getId().equals(id)) {
                return subpop;
            }
        }
        return null;
    }

    /**
     * returns the size of all {@link Subpopulation}(s) with the given Id.
     * Returns zero if the id is not found.
     *
     * @param id the Id of the Subpopulation(s).
     * @return   the size of the Subpopulation(s) with this Id.
     */
    public int getSizeById(String id) {
        Integer size = updateSizeById().get(id);
        return (size == null) ? 0 : size;
    }

    /**
     * Currently identical to {@link #updateSizeById}, but kept in case
     * getting should be separate from updating.
     *
     * @return the updated Map.
     */
    public Map<String, Integer> getSizeById() {
        return updateSizeById();
    }

    /**
     * update the data structure relating the size of each Subpopulation id.
     *
     * @return the updated Map.
     */
    public Map<String, Integer> updateSizeById() { 
        size_by_id.clear();
        for (Subpopulation sub:subpopulations) {
            String id = sub.getId();
            if (size_by_id.containsKey(id)) {
                size_by_id.put(id, size_by_id.get(id) + sub.getSize());
            } else {
                size_by_id.put(id, sub.getSize());
            }
        }
        return size_by_id;
    }

    @Override
    public String toString() {
        String spacer = "   ";
        StringBuilder report = 
            new StringBuilder(
                    String.format("coordinate=%s, size=%d, resource=%.3e, " +
                                  "capacity=%.0f, " + "subpopulations=%d:%n",
                                  getCoordinate(), getSize(), getResource(),
                                  getCapacity(), getNSubpopulations()));

        report.append(spacer).append((new ConsumptionODE(this)).toString());
        for (Subpopulation sub:subpopulations) {
            report.append(spacer).append(sub).append("\n");
        }
        return report.toString();
    }
}
