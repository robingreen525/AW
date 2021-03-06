package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.dilutionrule.*;
import org.fhcrc.honeycomb.metapop.environmentchanger.*;
import org.fhcrc.honeycomb.metapop.resource.*;
import org.fhcrc.honeycomb.metapop.coordinate.*;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

/** 
 * A <code>World</code>.
 *
 * Created on 26 Jan, 2012.
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public class World implements CoordinateProvider {
    private static final File DEFAULT_DATA_PATH = new File(".");
    private final File data_path;

    private final int rows;
    private final int cols;
    private final EnvironmentChanger env_changer;
    private final Resource resource;

    private double migration_rate;
    private DilutionRule dilution_rule;
    private double max_pop;

    // State variables.
    private boolean environment_changed = false;
    private boolean dilute = false;
    private boolean coops_extinct = false;
    private boolean cheats_extinct = false;
    private boolean all_extinct = false;


    private Coordinate current_coordinate;

    private final StateSaver ss;
    private CoordinatePicker coordinate_picker;
    private CoordinatePicker migration_picker;

    private final List<Location> initial_setup = new ArrayList<Location>();
    private List<Location> occupied_locations = new ArrayList<Location>();

    private int step = 0;

    /** 
     * Constructs a new <code>World</code>.
     *
     * @param rows                the number of rows.
     * @param cols                the number of columns.
     * @param env_changer         the <code>EnvironmentChanger</code>.
     * @param initial_populations a <code>List</code> of
     *                            <code>Population</code>s to initialize the
     *                            <code>World</code> with.
     * @param coordinate_picker   the <code>CoordinatePicker</code> used to 
     *                            place the <code>initial_populations</code>.
     * @param migration_picker    the <code>CoordinatePicker</code> used to 
     *                            place the migrating <code>Populations</code> 
     *                            at each iteration.
     * @param resource            the <code>Resource</code>
     * @param migration_rate      the portion of the <code>Population</code>
     *                            that migrates every iteration.
     * @param dilution_rule       the rule by which <code>Population</code>s
     *                            are diluted.
     * @param max_pop             the maximum population size supported by a
     *                            <code>Location</code>.
     * @param data_path           the path for saving data about this
     *                            <code>World</code>.
     *
     *
     * @see EnvironmentChanger
     * @see Location
     * @see Population
     * @see RandomNumberUser
     *
     */
    public World(int rows, int cols,
                 Resource resource,
                 EnvironmentChanger env_changer,
                 List<Population> initial_populations,
                 CoordinatePicker coordinate_picker,
                 CoordinatePicker migration_picker,
                 double migration_rate, DilutionRule dilution_rule, 
                 double max_pop, File data_path)
    {
        this.rows = rows;
        this.cols = cols;
        this.resource = resource;
        this.env_changer = env_changer;
        this.coordinate_picker = coordinate_picker;
        this.migration_picker = migration_picker;
        this.migration_rate = migration_rate;
        this.dilution_rule = dilution_rule;
        this.max_pop = max_pop;
        this.data_path = data_path;

        this.coordinate_picker.setProvider(this);

        if (this.migration_picker != null) {
            this.migration_picker.setProvider(this);
        }

        for (Population pop:initial_populations) {
            Location l = new Location(this.coordinate_picker.pick(),
                                      resource, pop);

            occupied_locations.add(l);
            initial_setup.add(new Location(l));
        }

        this.dilution_rule.setWorld(this);
        this.ss = new StateSaver(this);
    }

    /**
     * Constructor using default data path.
     *
     */
    public World(int rows, int cols,
                 Resource resource,
                 EnvironmentChanger env_changer,
                 List<Population> initial_populations,
                 CoordinatePicker coordinate_picker,
                 CoordinatePicker migration_picker,
                 double migration_rate,
                 DilutionRule dilution_rule, double max_pop)
    {
        this(rows, cols, resource, env_changer, initial_populations,
             coordinate_picker, migration_picker, migration_rate, 
             dilution_rule, max_pop, DEFAULT_DATA_PATH);
    }

    /**
     * returns the current step.
     *
     * @return the current step.
     */
    public int getStep() { return step; }

    /** increments the step. */
    public int incrementStep() { return ++step; }

    /** 
     * returns the number of rows in this <clas>World</class>.
     *
     * @return the number of rows.
     */
    public int getNRows() { return rows; }
    public int getMaxRow() { return rows; }

    /**
     * returns the number of columns in this <clas>World</class>.
     *
     *  @return the number of columns.
     */
    public int getNCols() { return cols; }
    public int getMaxCol() { return rows; }
    /**
     * returns the path to the simulation data.
     *
     * @return the data path. 
     */
    public File getDataPath() { return data_path; }

    /**
     * returns the locations that have populations, and their populations.
     *
     * @return the currently occupied locations and their populations.
     */
    public List<Location> getOccupiedLocations() {
        return occupied_locations;
    }

    public List<Coordinate> getOccupiedCoordinates() {
        List<Coordinate> occ_coords = 
            new ArrayList<Coordinate>(occupied_locations.size());
        for (Location oc : occupied_locations) {
            occ_coords.add(oc.getCoordinate());
        }
        return occ_coords;
    }

    /**
     * Search for a location based on its coordinates.  Returns 
     * <code>null</code> if the location is currently uninitialized.
     *
     * @param coord the <code>Coordinate</code> to look up.
     * @return      the <code>Location</code> if occupied or <code>null</code>
     *              if uninitialized.
     */
    public Location getLocationAt(Coordinate co) {
        for (Location loc : occupied_locations) {
            if (loc.getCoordinate().equals(co)) {
                return loc;
            }
        }
        return null;
    }

    /**
     * returns the uninitialized coordinates.
     *
     * @return the currently unoccupied coordinates.
     */
    public List<Coordinate> getUnoccupiedLocations() {
        int n_unoccupied = getNRows()*getNCols() - occupied_locations.size();
        List<Coordinate> unoccupied = new ArrayList<Coordinate>(n_unoccupied);

        for (int i=1; i<=getNRows(); i++) {
            for (int j=1; j<=getNCols(); j++) {
                Coordinate test_coord = new Coordinate(i, j);
                if (getLocationAt(test_coord) == null) {
                    unoccupied.add(test_coord);
                }
            }
        }
        return unoccupied;
    }

    public List<Location> getInitialLocations() { 
        List<Location> locs = new ArrayList<Location>(initial_setup.size());

        for (Location l:initial_setup) {
            locs.add(new Location(l));
        }
        return locs;
    }


    /**
     * returns the <code>EnvironmentChanger</code>.
     *
     * @return the EnvironmentChanger.
     */
    public EnvironmentChanger getEnvChanger() { return env_changer; }

    /**
     * returns whether the environment has changed at this step.
     *
     * @return whether the environment changed.
     */
    public boolean envChanged() { return environment_changed; }

    /**
     * returns whether all populations are extinct
     *
     * @return whether all populations are extinct.
     */
    public boolean allExtinct() { return all_extinct; }

    /**
     * returns whether the cooperators are extinct.
     *
     * @return whether the cooperators are extinct.
     */
    public boolean coopsExtinct() { return coops_extinct; }

    /**
     * returns whether the cheaters are extinct.
     *
     * @return whether the cheaters are extinct.
     */
    public boolean cheatsExtinct() { return cheats_extinct; }

    /**
     * returns the migration rate per iteration.
     *
     * @return the migration rate.
     */
    public double getMigrationRate() { return migration_rate; }

    /**
     * returns the dilution amount. A value between 0 and 1.
     *
     * @return the dilution amount.
     */
    public double getDilutionFraction() {
        return dilution_rule.getDilutionFraction();
    }

    /**
     * returns the maximum population.
     *
     * @return the maximum population.
     */
    public double getMaxPop() { return max_pop; }

    /**
     * returns the <code>Coordinate</code> of the <code>Location</code>
     * active in the iteration.
     *
     * @return the current coordinate.
     */
    public Coordinate getCoordinate() { return current_coordinate; }

    /**
     * returns the <code>CoordinatePicker</code> used for migration.
     *
     * @return the <code>CoordinatePicker</code> used for migration.
     *
     * @see CoordinatePicker
     */
    public CoordinatePicker getMigrationPicker() { return migration_picker; }

    /**
     * returns the <code>CoordinatePicker</code> used to pick initial 
     * locations.
     *
     * @return the <code>CoordinatePicker</code> used to pick initial
     * locations.
     *
     * @see CoordinatePicker
     */
    public CoordinatePicker getCoordinatePicker() { return coordinate_picker; }

    /**
     * returns the <code>DilutionRule</code>.
     *
     * @return the <code>DilutionRule</code>.
     *
     * @see DilutionRule
     */
    public DilutionRule getDilutionRule() { return dilution_rule; }

    /**
     * sets the <code>CoordinatePicker</code> used to pick migration locations.
     *
     * @see CoordinatePicker
     */
    public void setMigrationPicker(CoordinatePicker migration_picker) { 
        this.migration_picker = migration_picker;
        this.migration_picker.setProvider(this);
    }

    /**
     * sets the <code>DilutionRule</code>.
     *
     * @see DilutionRule
     */
    public void setDilutionRule(DilutionRule dilution_rule) {
        this.dilution_rule = dilution_rule;
        this.dilution_rule.setWorld(this);
    }

    /** 
     * distributes <code>Population</code>s.
     *
     * @param locations a <code>List</code> of <code>Location</code>s
     *                  that contain migrants to distribute.
     */
    public void distribute(final List<Location> migrants) {
        for (Location migrant : migrants) {
            if (migrant.populationSize() > 0) {
                Location new_location = getLocationAt(migrant.getCoordinate());
                if (new_location == null) {
                    occupied_locations.add(new Location(migrant));
                } else {
                    new_location.mix(migrant.getPopulation());
                }
                //System.out.println("now " + occupied_locations);
            }
        }
    }

    public void iterate(List<Double> new_freqs, int iterations, int save_every)
    {
        if (new_freqs == null && env_changer.getProb() > 0) {
            throw new IllegalArgumentException(
                "[World.iterate] Need new frequencies if " +
                "environment changes.");
        }

        if (getStep() == 0) saveState();

        while(incrementStep() <= iterations) {
            printStep(iterations);

            environment_changed = env_changer.environmentChanged();

            int n_occupied = occupied_locations.size();

            List<Location> new_locations = new ArrayList<Location>(n_occupied);

            Map<Location, Boolean> dilution_map = dilution_rule.generate();
            for (Location ol:occupied_locations) {
                current_coordinate = ol.getCoordinate();

                ol.updateResource();

                Population migrants = ol.retrieveMigrants(migration_rate);

                if (migrants.getSize() > 0) {
                    Coordinate new_coord = migration_picker.pick();
                    new_locations.add(new Location(new_coord, resource,
                                                   migrants));
                }

                if (environment_changed) {
                    ol.shiftFitness(new_freqs);
                    System.out.println(
                            "[World.iterate] Environment changed at step " +
                            getStep());
                }

                if (dilution_map.get(ol) && ol.populationSize() > 0) {
                    ol.dilute(dilution_rule.getDilutionFraction());
                }

                // if anything is > 0 and < 1, it is removed.
                if (ol.populationSize() < max_pop) {
                    ol.growPopulation();
                }
            }
            distribute(new_locations);

            checkExtinct(occupied_locations);
            if (coops_extinct || cheats_extinct) {
                saveState();
                break;
            }

            if (getStep() % save_every == 0 || envChanged()) saveState();
            resetState();
        }
        System.out.println("Done!");
    }

    public void iterate(int iterations, int save_every)
    {
        iterate(null, iterations, save_every);
    }


    private void printStep(int iters) {
        if (getStep() % 100000 == 0) {
            System.out.println("[World.iterate] Step " + getStep() +
                               " of " + iters);
        }
    }

    private void checkExtinct(List<Location> ols) {
        double remaining_coops = 0;
        double remaining_cheats = 0;

        for (Location ol:ols) {
            Population pop = ol.getPopulation();

            double coop_size  = pop.getNCoops();
            double cheat_size = pop.getNCheats();

            remaining_coops += coop_size;
            remaining_cheats += cheat_size;
        }

        coops_extinct  = (remaining_coops == 0) ? true : false;
        cheats_extinct = (remaining_cheats == 0) ? true : false;
        if (coops_extinct && cheats_extinct) { 
            System.out.println("All extinct at step " + getStep());
            all_extinct = true;
        }

        if (coops_extinct ^ cheats_extinct) {
            if (coops_extinct) {
                System.out.println("Coops extinct at step " + getStep());
            }
            if (cheats_extinct) {
                System.out.println("Cheats extinct at step " + getStep());
            }
        }
    }

    private void resetState() {
        dilute = false;
        environment_changed = false;
    }

    private void saveState() {
        try {
            ss.saveState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return new String("A World with " + rows + " rows, " +
                          cols + " columns, and " + 
                          occupied_locations.size() +
                          " occupied locations:\n" + occupied_locations);
    }
}
