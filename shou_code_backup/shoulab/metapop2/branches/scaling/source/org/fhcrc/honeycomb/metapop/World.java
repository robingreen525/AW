package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.dilution.DilutionRule;
import org.fhcrc.honeycomb.metapop.environment.EnvironmentChanger;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinateProvider;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;

import org.fhcrc.honeycomb.metapop.stop.StopCondition;
import org.fhcrc.honeycomb.metapop.mutation.MutationRule;
import org.fhcrc.honeycomb.metapop.migration.MigrationRule;

import java.io.File;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

/** 
 * Manages {@code Population}s.
 *
 * Created on 25 Apr, 2013
 * @author Adam Waite
 * @version $Id: World.java 2221 2013-07-19 00:21:07Z ajwaite $
 *
 */
public class World implements StepProvider, Saveable {
    protected static final File DEFAULT_DATA_PATH = new File(".");

    // Initialization parameters.
    protected final File data_path;
    protected final int rows;
    protected final int cols;
    protected final int timestep_scale;
    protected final List<String> const_headers = 
                  new ArrayList<String>(Arrays.asList("timestep",
                                                      "env.changed",
                                                      "row", "col",
                                                      "row.col",
                                                      "resource.before",
                                                      "consumed",
                                                      "released",
                                                      "resource.after"));
    protected String headers;

    protected final Map<String, String> initialization_data =
                        new HashMap<String, String>();
    protected List<Population> initial_populations = 
                        new ArrayList<Population>();
    protected List<String> subpop_ids = new ArrayList<String>();

    // Behavior.
    protected final EnvironmentChanger env_changer;
    protected final DilutionRule dilution_rule;
    protected final MutationRule mutation_rule;
    protected final MigrationRule migration_rule;
    protected final StopCondition stop_condition;

    protected final StateSaver ss;

    // State
    protected int step = 0;
    protected Coordinate current_coordinate;
    protected OccupiedLocations occupied_locations; 
    protected boolean environment_changed = false;
    protected int total_size = 0;
    protected boolean all_extinct = false;
    protected boolean env_changed = false;

    /** 
     * Constructs a new <code>World</code>.
     */
    public World(int rows, int cols, 
                 List<Population> initial_populations,
                 EnvironmentChanger env_changer,
                 DilutionRule dilution_rule,
                 MutationRule mutation_rule,
                 MigrationRule migration_rule,
                 StopCondition stop_condition,
                 File data_path,
                 int timestep_scale)
    {
        this.rows = rows;
        this.cols = cols;

        this.env_changer = env_changer;
        this.dilution_rule = dilution_rule;
        this.mutation_rule = mutation_rule;
        this.migration_rule = migration_rule;
        this.stop_condition = stop_condition;
        this.data_path = data_path;
        this.timestep_scale = timestep_scale;

        this.occupied_locations = 
            new OccupiedLocations(initial_populations, this.rows*this.cols);

        this.initial_populations = 
            Population.copyPopulations(initial_populations);

        this.stop_condition.setWorld(this);
        this.dilution_rule.setStepProvider(this);

        updateWorldSize();
        makeHeaders();
        makeInitializationData();
        this.ss = new StateSaver(this);
        saveState();
    }

    public World(int rows, int cols, 
                 List<Population> initial_populations,
                 EnvironmentChanger env_changer,
                 DilutionRule dilution_rule,
                 MutationRule mutation_rule,
                 MigrationRule migration_rule,
                 StopCondition stop_condition,
                 File data_path)
    {
        this(rows, cols, initial_populations, env_changer,
             dilution_rule, mutation_rule, migration_rule, stop_condition,
             data_path, 1);
    }


    // Implemented methods
    
    // StepProvider
    @Override
    public int getStep() { return step; }

    @Override
    public int incrementStep() { return ++step; }

    // Saveable
    @Override
    public File getDataPath() { return data_path; }

    @Override
    public String getFilename() { 
        return Integer.toString(getStep()) + ".tab";
    }

    @Override
    public Map<String, String> getInitializationData() {
        return initialization_data;
    }

    @Override
    public String getHeaders() { return headers; }

    @Override
    public String getData() {
        StringBuffer data = new StringBuffer(1000);
        for (Population pop:occupied_locations.getList()) {
            String row = Integer.toString(pop.getCoordinate().getRow());
            String col = Integer.toString(pop.getCoordinate().getCol());
            data.append(Integer.toString(getStep())).append("\t");
            data.append((env_changed) ? 1 : 0).append("\t");
            data.append(row).append("\t");
            data.append(col).append("\t");
            data.append(row + "_" + col).append("\t");
            data.append(
                    Double.toString(pop.getAvailableResource())).append("\t");
            data.append(Double.toString(pop.getConsumed())).append("\t");
            data.append(Double.toString(pop.getReleased())).append("\t");
            data.append(Double.toString(pop.getResource())).append("\t");

            for (String subpop_id:subpop_ids) {
                data.append(pop.getSizeById(subpop_id)).append("\t");
            }
            data.deleteCharAt(data.lastIndexOf("\t"));
            data.append("\n");
        }
        return data.toString();
    }

    private void makeHeaders() {
        StringBuilder tmp = new StringBuilder();
        List<Subpopulation> subpops = 
            initial_populations.get(0).getSubpopulations();

        for (String header:const_headers) {
            tmp.append(header).append("\t");
        }

        for (Subpopulation sub:subpops) {
            String id = sub.getId();
            subpop_ids.add(id);
            tmp.append(sub.getId()).append("\t");
        }
        tmp.deleteCharAt(tmp.lastIndexOf("\t"));
        headers = tmp.toString();
    }


    // Getters
    public EnvironmentChanger getEnvChanger() { return env_changer; }
    public MutationRule getMutationRule() { return mutation_rule; }
    public MigrationRule getMigrationRule() { return migration_rule; }
    public DilutionRule getDilutionRule() { return dilution_rule; }
    public int getSize() { return total_size; }
    public int getTimestepScale() { return timestep_scale; }
    public int getMaxRow() { return rows; }
    public int getMaxCol() { return cols; }

    public OccupiedLocations getOccupiedLocations() {
        return occupied_locations;
    }

    /** returns the total population size for a given subpopulation id. */
    public int getSizeById(String id) {
        int total = 0;
        for (Population pop:occupied_locations.getList()) {
            total += pop.getSizeById(id);
        }
        return total;
    }


    /** returns a copy of the original populations */
    public List<Population> getInitialPopulations() { 
        List<Population> pops = 
            new ArrayList<Population>(initial_populations.size());

        for (Population pop:initial_populations) {
            pops.add(new Population(pop));
        }
        return pops;
    }


    public void iterate(int iterations, int save_every) {
        iterate(null, iterations, save_every);
    }

    public void iterate(List<Double> new_freqs, int iterations, int save_every)
    {
        if (new_freqs == null && env_changer.getProb() > 0) {
            throw new IllegalArgumentException(
                "[World.iterate] Need new frequencies if " +
                "environment changes.");
        }

        while(incrementStep() <= iterations) {
            //System.out.println("step: " + step);
            printStep(iterations);
            environment_changed = env_changer.environmentChanged();
            int n_occupied = occupied_locations.getSize();

            dilute();
            grow();
            mutate();
            migrate();

            updateWorldSize();
            if (all_extinct || stop_condition.isMet()) {
                saveState();
                break;
            }

            if (getStep() % save_every == 0) saveState();
            env_changed = false;
        }
    }

    public void migrate() {
        migration_rule.migrate(occupied_locations);
    }

    public void grow() {
        for (Population pop:occupied_locations.getList()) {
            pop.grow();
        }
    }

    public void mutate() {
        mutation_rule.mutate(occupied_locations.getList());
    }

    private void dilute() {
        List<Population> pops = occupied_locations.getList();
        Map<Coordinate, Double> dilution_map = dilution_rule.generate(pops);
        for (Map.Entry<Coordinate, Double> entry:dilution_map.entrySet()) {
            occupied_locations.
                getPopulationAt(entry.getKey()).dilute(entry.getValue());
        }
    }

    private void updateWorldSize() {
        total_size = 0;
        for (Population pop:occupied_locations.getList()) {
            total_size += pop.getSize();
        }
        if (total_size == 0) {
            all_extinct = true;
            System.out.println("All extinct at step " + getStep());
        }
    }

    protected void saveState() {
        try {
            ss.saveState();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Private methods.
    protected void printStep(int iters) {
        if (getStep() % 100000 == 0) {
            System.out.println("[World.iterate] Step " + getStep() +
                               " of " + iters);
        }
    }

    private void makeInitializationData() {
        String filename = "world_info.txt";
        Population pop = initial_populations.get(0);
        String info =
            String.format("<world>\n" +
                          "\t<timestep_scale>%d</timestep_scale>\n" + 
                          "\t<rows>%d</rows>\n" +
                          "\t<cols>%d</cols>\n" +
                          "\t<initial_populations>%d</initial_populations>\n" +
                          "\t<initial_population_structure>\n\t%s" + 
                          "\t</initial_population_structure>\n" +
                          "\t<env_changer>%s</env_changer>\n" +
                          "\t<dilution_rule>%s</dilution_rule>\n" +
                          "\t<mutation_rule>%s</mutation_rule>\n" +
                          "\t<migration_rule>%s</migration_rule>\n" +
                          "</world>\n"
                          ,
                          getTimestepScale(),
                          getMaxCol(),
                          getMaxRow(),
                          occupied_locations.getNOccupied(),
                          pop,
                          getEnvChanger(),
                          getDilutionRule(),
                          getMutationRule(),
                          getMigrationRule()
                        );
        initialization_data.put("filename", filename);
        initialization_data.put("info", info);
    }

    @Override
    public String toString() {
        StringBuilder world_data = 
            new StringBuilder(initialization_data.get("info") + "\n");
        for (Population pop:occupied_locations.getList()) {
            world_data.append(pop).append("\n");
        }
        return world_data.toString();
    }
}
