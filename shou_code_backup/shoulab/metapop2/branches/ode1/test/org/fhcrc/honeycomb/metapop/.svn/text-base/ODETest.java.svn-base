package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.MonodCalculator;

import org.fhcrc.honeycomb.metapop.ode.ConsumptionODE;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.AdamsMoultonIntegrator;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.ode.events.EventHandler;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*;

public class ODETest {
    private static final int TIMESTEP_SCALE = 100000;

    private double minStep = 1.0e-20;
    private double maxStep = 1.0;
    private double scalAbsoluteTolerance = 1.0e-10;
    private double scalRelativeTolerance = 1.0e-6;
    private FirstOrderIntegrator dp583 = 
        new DormandPrince853Integrator(minStep, maxStep,
                                       scalAbsoluteTolerance,
                                       scalRelativeTolerance);

    private RandomNumberUser population_rng = new RandomNumberUser(1);
    private double amount_for_new_cell = 5.5;
    private double coop_release_rate = 2.4;
    private double cheat_release_rate = 0.0;

    private double base_growth_rate = (StrictMath.log(2)/2);
    private double base_death_rate = 0.1;
    private double base_km = 10;
    
    // Cheater advantage.
    private double alpha = 1.2;

    // Evolved Km advantage.
    private double delta1 = 10;

    // Evolved death advantage
    private double delta2 = 10;

    // Evolved tradeoff.
    private double theta = 0.7;
    

    // Ancestor cooperator.
    private final String anc_coop_id = "anc_coop";
    private double co_vmax = base_growth_rate;
    private double co_km   = base_km;
    private double co_d    = base_death_rate;
    private FitnessCalculator anc_coop_fc = 
        new MonodCalculator(co_vmax, co_km, co_d);

    // Ancestor cheater.
    private final String anc_cheat_id = "anc_cheat";
    private double ch_vmax = alpha*co_vmax;
    private double ch_km = co_km;
    private double ch_d = co_d/alpha;
    private FitnessCalculator anc_cheat_fc = 
        new MonodCalculator(ch_vmax, ch_km, ch_d);

    // Evolved cooperator.
    private final String evo_coop_id = "evo_coop";
    private double eco_vmax = theta*co_vmax;
    private double eco_km   = co_km/delta1;
    private double eco_d    = co_d/delta2;
    private FitnessCalculator evo_coop_fc = 
        new MonodCalculator(eco_vmax, eco_km, eco_d);

    // Evolved cheater.
    private final String evo_cheat_id = "evo_cheat";
    private double ech_vmax = theta*ch_vmax;
    private double ech_km   = eco_km;
    private double ech_d    = eco_d/alpha;
    private FitnessCalculator evo_cheat_fc = 
        new MonodCalculator(ech_vmax, ech_km, ech_d);

    private Subpopulation anc_coop;
    private Subpopulation anc_cheat;
    private Subpopulation evo_coop;
    private Subpopulation evo_cheat;
    private List<Subpopulation> initial_subpopulations;
    private Population test_pop;
    
    private int initial_anc_coops = 0;
    private int initial_anc_cheats = 1; //1000; //(int) 1e5;
    private int initial_evo_coops = 0; //(int) 5e4;
    private int initial_evo_cheats = 0;
    private double initial_resource = 10000000;

    private StepHandler stepHandler = new StepHandler() {
        public void init(double t0, double[] y0, double t) {}

        public void handleStep(StepInterpolator interpolator, boolean isLast) {
            double t = interpolator.getCurrentTime();
            double[] y = interpolator.getInterpolatedState();

            for (int i=0; i<y.length; i++) {
                System.out.println("step: " + t + ": y[" + i + "] = " + y[i]);
            }
        }
    };

    private EventHandler eventHandler = new EventHandler() {
        public void init(double t0, double[] y0, double t) {}

        public double g(double t, double[] y) {
            return y[4] - 0.0;
        }

        public EventHandler.Action eventOccurred(double t, double[] y,
                                                 boolean increasing)
        {
            return EventHandler.Action.STOP;
        }

        public void resetState(double t, double[] y) {}
    };

    @Before
    public void setUp() {
         anc_coop = new Subpopulation(initial_anc_coops,
                                      amount_for_new_cell,
                                      coop_release_rate,
                                      anc_coop_fc,
                                      anc_coop_id,
                                      population_rng);

         anc_cheat = new Subpopulation(initial_anc_cheats,
                                       amount_for_new_cell,
                                       cheat_release_rate,
                                       anc_cheat_fc,
                                       anc_cheat_id,
                                       population_rng);

         evo_coop = new Subpopulation(initial_evo_coops,
                                      amount_for_new_cell,
                                      coop_release_rate,
                                      evo_coop_fc,
                                      evo_coop_id,
                                      population_rng);

         evo_cheat = new Subpopulation(initial_evo_cheats,
                                       amount_for_new_cell,
                                       cheat_release_rate,
                                       evo_cheat_fc,
                                       evo_cheat_id,
                                       population_rng);

         initial_subpopulations = new ArrayList<Subpopulation>(
                    Arrays.asList(anc_coop, anc_cheat, evo_coop, evo_cheat));

         test_pop = new Population(initial_subpopulations, 
                                   new Coordinate(1,1), initial_resource,
                                   population_rng);
    }

    @Test
    public void nutrientTest() {
        ConsumptionODE consumption = new ConsumptionODE(test_pop);

        consumption.integrate(1.0/TIMESTEP_SCALE);

        double ave_resource = consumption.getAverageResource();
        System.out.println("ave resource: " + ave_resource);

        for (Subpopulation subpop:test_pop.getSubpopulations()) {
            if (subpop.getSize()==0) continue;
            String id = subpop.getId();
            System.out.println(id);
            double gr = subpop.getGrowthRate(ave_resource);
            //System.out.println("  growth rate by consumption: " + gr);

            double gr2 = consumption.calculateGrowthRate(id);
            //System.out.println("  growth rate direct: " + gr2);

            if (!Double.isNaN(gr) && !Double.isNaN(gr2)) {
                assertEquals("growth rate methods disagree",
                             gr, gr2, 1e-4);
            }
        }
    }
}
