package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.MonodCalculator;

import org.fhcrc.honeycomb.metapop.ode.ConsumptionODE;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*;

public class ODETest {
    private static final int TIMESTEP_SCALE = 100;

    private RandomNumberUser population_rng = new RandomNumberUser(1);
    private double amount_for_new_cell = 5.5;
    private double coop_release_rate = 2.4/TIMESTEP_SCALE;
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
        new MonodCalculator(co_vmax, co_km, co_d, TIMESTEP_SCALE);

    // Ancestor cheater.
    private final String anc_cheat_id = "anc_cheat";
    private double ch_vmax = alpha*co_vmax;
    private double ch_km = co_km;
    private double ch_d = co_d/alpha;
    private FitnessCalculator anc_cheat_fc = 
        new MonodCalculator(ch_vmax, ch_km, ch_d, TIMESTEP_SCALE);

    // Evolved cooperator.
    private final String evo_coop_id = "evo_coop";
    private double eco_vmax = theta*co_vmax;
    private double eco_km   = co_km/delta1;
    private double eco_d    = co_d/delta2;
    private FitnessCalculator evo_coop_fc = 
        new MonodCalculator(eco_vmax, eco_km, eco_d, TIMESTEP_SCALE);

    // Evolved cheater.
    private final String evo_cheat_id = "evo_cheat";
    private double ech_vmax = theta*ch_vmax;
    private double ech_km   = eco_km;
    private double ech_d    = eco_d/alpha;
    private FitnessCalculator evo_cheat_fc = 
        new MonodCalculator(ech_vmax, ech_km, ech_d, TIMESTEP_SCALE);

    private Subpopulation anc_coop;
    private Subpopulation anc_cheat;
    private Subpopulation evo_coop;
    private Subpopulation evo_cheat;
    private List<Subpopulation> initial_subpopulations;
    private Population test_pop;
    
    private int initial_anc_coops = 0;
    private int initial_anc_cheats = 0;
    private int initial_evo_coops = 0;
    private int initial_evo_cheats = 0;
    private double initial_resource = 0;

    public void makePops() {
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
    public void resourceExcess() {
        initial_anc_cheats = 1;
        initial_resource = 1e6;
        makePops();
        ConsumptionODE ode = new ConsumptionODE(test_pop);
        ode.integrate(test_pop);

        //System.out.println("\nresourceExcess()");
        compareGrowthRates(ode);
    }

    @Test
    public void resourceLimited() {
        initial_anc_cheats = (int) 1e7;
        initial_resource = 0.001;
        makePops();
        ConsumptionODE ode = new ConsumptionODE(test_pop);
        ode.integrate(test_pop);

        //System.out.println("\nresourceLimited()");
        compareGrowthRates(ode);
    }

    @Test
    public void singleCoop() {
        initial_anc_coops = 100;
        initial_resource = 0;
        makePops();
        ConsumptionODE ode = new ConsumptionODE(test_pop);

        ode.integrate(test_pop);
        System.out.println("\nsingleCoop()");
        compareGrowthRates(ode);

        ode.integrate(test_pop);
        System.out.println("\nsingleCoop()");
        compareGrowthRates(ode);

    }

    @Test
    public void negativeResource() {
        //System.out.println("\nnegativeResource()");

        // large negative resource values occur when DormandPrince54Integrator
        // has the following settings:
        // scalAbsoluteTolerance = 1.0e-6;
        // scalRelativeTolerance = 1.0e-6;
        //
        //double BAD_VAL_FULL = 0.0271466666032164662125669707393171847797930240631103515625;
        
        // These exact values cause the error. Changing any of them gives
        // expected result.
        double BAD_VAL_MIN = 0.027146666603;
        initial_anc_coops = 0;
        initial_anc_cheats = 0;
        initial_evo_coops = 5985;
        initial_evo_cheats = 242832;
        initial_resource = BAD_VAL_MIN;
        makePops();

        ConsumptionODE ode = new ConsumptionODE(test_pop);
        ode.integrate(test_pop);
        double resource = ode.integrateResource();
        assertTrue("resource < 0: " + resource, resource > 0.0);
    }

    @Test
    public void negativeResource2() {
        System.out.println("\nnegativeResource2()");
        double BAD_VAL_MIN = 1926.66439844370825085206888616085052490234375;
        initial_anc_coops = 0;
        initial_anc_cheats = 0;
        initial_evo_coops = 584758;
        initial_evo_cheats = 23685;
        initial_resource = BAD_VAL_MIN;
        makePops();

        ConsumptionODE ode = new ConsumptionODE(test_pop);
        ode.integrate(test_pop);
        double resource = ode.integrateResource();
        System.out.println("resource: " + resource);
        assertTrue("resource < 0: " + resource, resource > 0.0);
    }

    private void compareGrowthRates(ConsumptionODE ode) {
        for (Subpopulation subpop:test_pop.getSubpopulations()) {
            if (subpop.getSize()==0) continue;
            String id = subpop.getId();
            System.out.println(id);
            ode.reportResults();

            double resource = ode.integrateResource();
            resource = ode.integrateResource();
            System.out.println("  integrated resource: " + resource);

            double gr = subpop.getGrowthRate(resource);
            System.out.println("  growth rate by consumption: " + gr);

            subpop.setSize(subpop.getSize() + subpop.getBirths(resource));

            double gr2 = ode.calculateGrowthRate(id);
            System.out.println("  growth rate direct: " + gr2);

            //System.out.println("resource array: ");
            //ode.printResourceArray();

            if (!Double.isNaN(gr) && !Double.isNaN(gr2)) {
                assertEquals("growth rate methods disagree",
                             gr, gr2, 1e-3);
            }
        }
    }
}
