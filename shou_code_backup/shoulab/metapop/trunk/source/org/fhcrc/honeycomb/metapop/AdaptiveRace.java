package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.World;
import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.dilutionrule.DilutionRule;
import org.fhcrc.honeycomb.metapop.environmentchanger.EnvironmentChanger;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinatePicker;
import org.fhcrc.honeycomb.metapop.coordinate.RandomPicker;
import org.fhcrc.honeycomb.metapop.coordinate.UniqueRandomPicker;
import org.fhcrc.honeycomb.metapop.coordinate.RandomNeighborPicker;
import org.fhcrc.honeycomb.metapop.resource.Resource;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

/** 
 * Runs the adaptive race.
 * Created on 12 Feb, 2013.
 *
 * @author Adam Waite
 * @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 *
 */
public abstract class AdaptiveRace {
    public static final double MIN_PER_HR = 60.0;
    private static final String DATE_FORMAT = "yyMMddHHmmss";

    // Seeds
    private long population_seed;
    private long location_seed;
    private long migration_seed;
    private long env_change_seed;

    // RNGs
    private RandomNumberUser population_rng;
    private RandomNumberUser location_rng;
    private RandomNumberUser migration_rng;
    private RandomNumberUser env_change_rng;

    // Population params
    private double frac_occupied;
    private final int initial_pop_size = 100000;
    private final double initial_coop_freq = 0.5;
    private double cheat_adv;
    private double u_per_hr;
    private List<Population> initial_populations;
    private FitnessCalculator fc;
    private double doubling_time;
    private double base_growth_rate_per_hr = StrictMath.log(2);
    private Types types;

    // World params
    private CoordinatePicker location_picker;
    private int rows;
    private int cols;
    private DilutionRule dil_rule;
    private EnvironmentChanger env_changer;
    private CoordinatePicker migration_picker;
    private Resource resource;
    private File data_path;
    private double env_change_prob_per_hr;

    // World iteration params
    private int iterations;
    private int save_every;
    private double migration_rate_per_min;
    private final double max_pop = 1e7;

    // From command line
    private String migration_type;
    private double mutant_freq;
    private double migration_rate_per_hr;
    private double fractional_death_rate;
    private int hours;
    private double save_every_hrs;

    // Depend on command line args
    private String output = ".";
    private String migration_string;

    /** The constructor handles command line arguments and constructs an
     * <code>AdaptiveRace</code> object, which is used to initialize
     * the populations.
     *
     */
    public AdaptiveRace(String args[]) {
        parseArgs(args);
        setRNGs();
    }

    // Override these methods to define how the simulation runs.
    protected abstract DilutionRule       makeDilutionRule();
    protected abstract EnvironmentChanger makeEnvChanger();
    protected abstract FitnessCalculator  makeFitnessCalculator();
    protected abstract Resource           makeResource();

    // Public methods
    public void run() {
        this.dil_rule    = makeDilutionRule();
        this.env_changer = makeEnvChanger();
        this.fc          = makeFitnessCalculator();
        this.resource    = makeResource();

        checkNulls();
        setDataPath();
        generatePopulations();
        startWorld();
    }

    private void setRNGs() {
        population_rng = new RandomNumberUser(population_seed);
        location_rng   = new RandomNumberUser(location_seed);
        migration_rng  = new RandomNumberUser(migration_seed);
        env_change_rng = new RandomNumberUser(env_change_seed);

        location_picker = new UniqueRandomPicker(location_rng);
        pickMigration();

        double env_change_prob_per_min = envChangeProbPerHr()/MIN_PER_HR;
    }


    public void setFitnessCalculator(FitnessCalculator fc) {
        this.fc = fc;
    }

    public void setDilutionRule(DilutionRule dil_rule) {
        this.dil_rule = dil_rule;
    }

    public void setEnvironmentChanger(EnvironmentChanger ec) {
        this.env_changer = ec;
    }


    // Private methods
    private void generatePopulations() {
        double base_growth_rate_per_min = 
            this.base_growth_rate_per_hr/this.doubling_time/MIN_PER_HR;
        List<Double> freqs = 
            Arrays.asList(1-this.mutant_freq, this.mutant_freq);
        List<Double> fitnesses =
            Arrays.asList(base_growth_rate_per_min,
                          base_growth_rate_per_min*2.5);

        int initial_n_pops =
            (int) Math.round(this.frac_occupied * this.rows * this.cols);
        this.types = new Types(fitnesses, freqs);
        this.initial_populations = 
            Population.generate(initial_n_pops,
                                this.initial_pop_size,
                                this.initial_coop_freq,
                                this.cheat_adv,
                                this.u_per_hr/MIN_PER_HR,
                                this.types,
                                this.fc,
                                this.population_rng);
    }

    private void startWorld() {
        System.out.println(
            String.format("\n\nInitiating world with %s migration and " +
                          "mutant frequency of %.2e",
                          this.migration_type,
                          this.mutant_freq));
        System.out.println("\nSaving to " + this.data_path + "\n");


        World world = new World(this.rows,
                                this.cols, 
                                this.resource,
                                this.env_changer,
                                initial_populations,
                                this.location_picker, 
                                this.migration_picker,
                                this.migration_rate_per_hr/MIN_PER_HR,
                                this.dil_rule,
                                this.max_pop,
                                this.data_path);

        //System.out.println(world);
        world.iterate(this.types.getFrequencies(),
                      this.iterations, this.save_every);
    }

    private void checkNulls() {
        if (this.dil_rule == null) {
            throw new RuntimeException("DilutionRule is null!\n");
        }

        if (this.fc == null) {
            throw new RuntimeException("FitnessCalculator is null!\n");
        }

        if (this.env_changer == null) {
            throw new RuntimeException("EnvironmentChanger is null!\n");
        }

        if (this.migration_picker == null) {
            throw new RuntimeException("Migration piker is null!\n");
        }

        if (this.location_picker == null) {
            throw new RuntimeException("Location piker is null!\n");
        }

        if (this.resource == null) {
            throw new RuntimeException("Resource is null!\n");
        }
    }

    private void pickMigration() {
        boolean exclude_current = true;
        int max_distance = 1;

        if (this.migration_type.equals("global")) {
            this.migration_picker = new RandomPicker(exclude_current, 
                                                     this.migration_rng);
        } else if (this.migration_type.equals("local")) {
            this.migration_picker =
                new RandomNeighborPicker(exclude_current, max_distance,
                                         this.migration_rng);
        } else {
            throw new IllegalArgumentException(
                "Don't recognize that migration type!");
        }
    }

    private void setDataPath() {
        String folder_name = 
            String.format("ar_%s_u=%.2e_mutr=%.2e_dil=%.2e_mr=%.2e_" +
                          "dr=%.2e_max-pop=%.2e_env=%.2e_hrs=%d_" +
                          "occ=%.2e_%drowsX%dcols",
                          this.migration_picker.getType(),
                          this.u_per_hr,
                          this.mutant_freq,
                          this.dil_rule.getDilutionFraction(),
                          this.migration_rate_per_hr,
                          maxDeathRatePerHr(), 
                          this.max_pop,
                          this.env_change_prob_per_hr,
                          this.hours,
                          this.frac_occupied,
                          this.rows,
                          this.cols);

        this.data_path = new File(new File(output, folder_name),
                                  UUID.randomUUID().toString());
    }

    public double maxDeathRatePerHr() {
        return this.fractional_death_rate * -this.base_growth_rate_per_hr;
    }

    public double envChangeProbPerHr() {
        return this.env_change_prob_per_hr;
    }

    private void parseArgs(String args[]) {
        int expected_length = 16;

        if (args.length < expected_length) {
            throw new IllegalArgumentException(
            "usage: AdaptiveRaceBuilder " +
            "[migration type] " +
            "[cheat advantage] " +
            "[coop to cheat] " +
            "[mutant freq] [anc doubling time]" +
            "[row/col size] [frac occupied] [migration rate] " +
            "[fractional death rate] " +
            "[env change prob] " +
            "[population seed] " +
            "[location seed] " +
            "[migration seed]" +
            "[env change seed] " +
            "[hours to simulate] [save frequency (hrs)] [output]\n\n" +
            "all time units in hours");
        }

        this.migration_type = args[0];
        this.cheat_adv = Double.parseDouble(args[1]);
        this.u_per_hr = Double.parseDouble(args[2]);
        this.mutant_freq = Double.parseDouble(args[3]);
        this.doubling_time = Double.parseDouble(args[4]);
        this.rows = Integer.parseInt(args[5]);
        this.cols = Integer.parseInt(args[5]);
        this.frac_occupied = Double.parseDouble(args[6]);
        this.migration_rate_per_hr = Double.parseDouble(args[7]);
        this.fractional_death_rate = Double.parseDouble(args[8]);
        this.env_change_prob_per_hr = Double.parseDouble(args[9]);
        this.population_seed = Long.parseLong(args[10]);
        this.location_seed = Long.parseLong(args[11]);
        this.migration_seed = Long.parseLong(args[12]);
        this.env_change_seed = Long.parseLong(args[13]);
        this.hours = Integer.parseInt(args[14]);
        this.save_every_hrs = Double.parseDouble(args[15]);

        if (args.length == expected_length+1) 
            output = args[expected_length];

        this.iterations = (int) Math.round(this.hours*MIN_PER_HR);
        this.save_every = (int) Math.round(this.save_every_hrs*MIN_PER_HR);
    }
}
