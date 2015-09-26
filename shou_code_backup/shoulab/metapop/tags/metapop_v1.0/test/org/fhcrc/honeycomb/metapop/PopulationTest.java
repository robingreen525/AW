package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.*;
import org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator.*;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.*;
import org.fhcrc.honeycomb.metapop.environmentchanger.*;
import org.fhcrc.honeycomb.metapop.coordinate.*;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.core.Is.is;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class PopulationTest {
    private List<Double> fitnesses =
        Arrays.asList(StrictMath.log(2), StrictMath.log(2)*1.1);
    private List<Double> freqs = Arrays.asList(0.5, 0.5);
    private int initial_size = 4;
    private double initial_cheat_freq = 0.7;
    private double cheat_adv = 0.02;

    private List<Double> coops = Arrays.asList(30.0,3.0);
    private List<Double> cheats = Arrays.asList(70.0,7.0);
    private double no_u = 0;
    private double u = 1e-4;

    private long seed = 12345L;
    private RandomNumberUser rng = new RandomNumberUser(seed);
    private FitnessCalculator fc;
    private CheaterLoadCalculator clc;

    private double each_pop;
    private Types types;

    private Population pop1;
    private Population pop2;
    private List<Population> pops;


    @Before
    public void setUp() {
        rng = new RandomNumberUser(seed);
        fc  = new FitnessUnchanged();

        each_pop = initial_size * initial_cheat_freq / freqs.size();

        types = new Types(fitnesses, freqs);

        pop1 = new Population(initial_size, initial_cheat_freq, cheat_adv, 
                              no_u, types, fc, rng);
        pop2 = new Population(coops, cheats, fitnesses, cheat_adv, no_u, fc,
                              rng);

        pops = Arrays.asList(pop1, pop2);

    }

    @Test
    public void mutation() {
        double coops = 1e7;
        double cheats = 2e6;
        double small_coops = 1;
        double small_cheats = 10;
        double expected_coops = coops * (1 - u);
        double expected_cheats = cheats + u*coops;

        List<Double> mutants = pop1.mutate(coops,cheats);
        assertEquals("coops", coops, mutants.get(0), 0.0);
        assertEquals("cheats", cheats, mutants.get(1), 0.0);

        mutants = pop1.mutate(small_coops, small_cheats);
        assertEquals("small coops", small_coops, mutants.get(0), 0.0);
        assertEquals("small cheats", small_cheats, mutants.get(1), 0.0);

        Population pop =
            new Population(initial_size, initial_cheat_freq, cheat_adv, u,
                           types, fc, rng);
        mutants = pop.mutate(coops, cheats);
        assertEquals("coops", expected_coops, mutants.get(0), 0.0);
        assertEquals("cheats", expected_cheats, mutants.get(1), 0.0);

        for (int i=0; i<100; i++) {
            mutants = pop.mutate(small_coops, small_cheats);
            double mutant_coops = mutants.get(0);
            double mutant_cheats = mutants.get(1);
            assertTrue("coop mutate one",
                       mutant_coops == 1.0 || mutant_coops == 0.0);
            assertTrue("cheat mutate ten", 
                       mutant_cheats == 11.0 || mutant_cheats == 10.0);
        }
    }

    @Test
    public void generatePops() {
        initial_size = 1000;
        List<Population> test_pops = Population.generate(10,
                                                         initial_size,
                                                         initial_cheat_freq,
                                                         cheat_adv,
                                                         no_u,
                                                         types,
                                                         fc,
                                                         rng);

        List<Double> type1_coops = new ArrayList<Double>();
        List<Double> type1_cheats = new ArrayList<Double>();
        for (Population pop : test_pops) {
            type1_coops.add(pop.getCoops().get(0));
            type1_cheats.add(pop.getCheats().get(0));

            assertEquals("cheat freq", 
                         initial_cheat_freq,
                         pop.getNCheats()/(pop.getNCoops()+pop.getNCheats()),
                         0.01);
        }
        Set<Double> type1_coop_set = new HashSet<Double>(type1_coops);
        Set<Double> type1_cheat_set = new HashSet<Double>(type1_cheats);

        assertTrue("type1 coop", type1_coop_set.size() > 1);
        assertTrue("type1 cheat", type1_cheat_set.size() > 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void initializeError() {
        List<Double> coops2  = Arrays.asList(1.0, 2.0, 4.0, 8.0, 16.0);
        Population bad_pop = new Population(coops2, cheats, fitnesses,
                                            cheat_adv, no_u, fc, rng);
    }

    @Test
    public void checkInitialization() {
        assertEquals("pop1 size", initial_size, pop1.getSize(), 0.0);
        assertEquals("pop2 size", 110, pop2.getSize(), 0.0);

        assertEquals("pop1 coop size",
                initial_size*(1-initial_cheat_freq),
                pop1.getNCoops(), 0.0);
        assertEquals("pop2 coop size", 33, pop2.getNCoops(), 0.0);

        assertEquals("pop1 cheat size",
                initial_size*initial_cheat_freq,
                pop1.getNCheats(), 0.0);
        assertEquals("pop2 cheat size", 77, pop2.getNCheats(), 0.0);

        assertEquals("pop1 cheat freq",
                initial_cheat_freq, pop1.getCheatFrequency(), 0.0);
        assertEquals("pop2 cheat freq",
                initial_cheat_freq, pop2.getCheatFrequency(), 0.0);


        assertEquals("pop1 cheat advantage",
                cheat_adv, pop1.getCheatAdvantage(), 0.0);
        assertEquals("pop2 cheat advantage",
                cheat_adv, pop2.getCheatAdvantage(), 0.0);

        for (Population pop : pops) {
            assertSame("rng", rng, pop.getRNG());
            assertEquals("seed", seed, pop.getRNG().getSeed());
            assertThat("fitnesses", fitnesses, is(pop.getMaxFitnesses()));
        }
    }

    @Test
    public void copyPopulation() {
        Population pop_copy = new Population(pop1);

        assertNotSame("population", pop1, pop_copy);

        assertNotSame("coop array", pop1.getCoops(), pop_copy.getCoops());
        assertThat("coop array", pop1.getCoops(), is(pop_copy.getCoops()));

        assertNotSame("cheat array", pop1.getCheats(), pop_copy.getCheats());
        assertThat("cheat array", pop1.getCheats(), is(pop_copy.getCheats()));

        assertNotSame("coop freq array",
                      pop1.getCoopFrequencies(),
                      pop_copy.getCoopFrequencies());
        assertThat("coop freq array",
                   pop1.getCoopFrequencies(),
                   is(pop_copy.getCoopFrequencies()));

        assertNotSame("cheat freq array",
                      pop1.getCheatFrequencies(),
                      pop_copy.getCheatFrequencies());
        assertThat("cheat freq array",
                   pop1.getCheatFrequencies(),
                   is(pop_copy.getCheatFrequencies()));

        assertNotSame("fitness array",
                      pop1.getMaxFitnesses(), pop_copy.getMaxFitnesses());
        assertThat("fitness array",
                   pop1.getMaxFitnesses(), is(pop_copy.getMaxFitnesses()));

        assertSame("RNG", pop_copy.getRNG(), pop1.getRNG());
        assertEquals("seeds", pop_copy.getRNG().getSeed(),
                     pop1.getRNG().getSeed());
    }

    @Test
    public void persistenceGivesUniqueNumbers() {
        List<Double> pop1_res     = new ArrayList<Double>(5);
        List<Double> pop_copy_res = new ArrayList<Double>(5);
        int size = 100;

        Population pop_copy = new Population(pop1);

        for (int i=0; i<size; i++) pop1_res.add(pop1.persistence(0.5));
        for (int i=0; i<size; i++) pop_copy_res.add(pop_copy.persistence(0.5));

        int same = 0;
        for (int i=0; i<size; i++) {
            if (pop1_res.get(i).equals(pop_copy_res.get(i))) ++same;
        }
        assertNotEquals("samples different", pop1_res, pop_copy_res);
    }

    @Test
    public void growPopulation() {
        List<Double> fitnesses = Arrays.asList(Math.log(2), Math.log(2)*1.1);
        List<Double> freqs = Arrays.asList(0.5, 0.5);
        int initial_size = 4;
        double initial_cheat_freq = 0.5;
        double cheat_adv = 0.02;

        Types types = new Types(fitnesses, freqs);
        Population pop = 
            new Population(initial_size, initial_cheat_freq, cheat_adv, no_u,
                           types, fc, rng);

        double each_pop = initial_size * initial_cheat_freq / freqs.size();

        double type1_coop_expected = 2.0;
        double type2_coop_expected = growthFormula(each_pop,
                                                   fitnesses.get(1), 1);
        double type1_cheat_expected =
            growthFormula(each_pop, fitnesses.get(0)*(1+cheat_adv), 1); 
        double type2_cheat_expected = 
            growthFormula(each_pop, fitnesses.get(1)*(1+cheat_adv), 1); 

        double nCoops  = type1_coop_expected  + type2_coop_expected;
        double nCheats = type1_cheat_expected + type2_cheat_expected;
        double expected_cheat_freq = nCheats/(nCoops+nCheats);

        //System.out.println("Growing...");
        pop.grow();

        //System.out.println("Coops: "  + pop.getCoops());
        //System.out.println("Cheats: " + pop.getCheats());
        //System.out.println("New coop freq: " + pop.getCoopFrequency());

        assertEquals("New type1 coop pop", 
                type1_coop_expected,
                pop.getCoops().get(0), 0);

        assertEquals("New type2 coop pop", 
                type2_coop_expected,
                pop.getCoops().get(1), 0);

        assertEquals("New type1 cheat pop", 
                type1_cheat_expected,
                pop.getCheats().get(0), 0);

        assertEquals("New type2 cheat pop", 
                type2_cheat_expected,
                pop.getCheats().get(1), 0);

        assertEquals("New coop pop", 
                type1_coop_expected+type2_coop_expected,
                pop.getNCoops(), 0);
        assertEquals("New cheat pop", 
                type1_cheat_expected+type2_cheat_expected,
                pop.getNCheats(), 0);

        assertEquals("New nCoops", nCoops, pop.getNCoops(), 0);
        assertEquals("New nCheats", nCheats, pop.getNCheats(), 0);
        assertEquals("New cheat freq", expected_cheat_freq, 
                     pop.getCheatFrequency(), 0);
        //System.out.println();

    }

    @Test
    public void dilutePopulation() {
        double large_pop = 1e8;
        double small_pop = 9.0;
        double lt1_pop = 0.9;
        List<Double> coops  = Arrays.asList(large_pop, small_pop, lt1_pop);
        List<Double> cheats = Arrays.asList(large_pop, small_pop, lt1_pop);
        List<Double> fitnesses = Arrays.asList(Math.log(2),
                                               Math.log(2),
                                               Math.log(2));
        double cheat_advantage = 0.02;
        double dilution_amount = 0.1;

        Population pop = new Population(coops, cheats, fitnesses,
                                        cheat_advantage, no_u, fc, rng);

        //System.out.println("\n\ndilutePopulation()");
        Population diluted = pop.dilute(dilution_amount);

        //System.out.println("Diluted");
        //System.out.println(diluted);

        //System.out.println("Remaining");
        //System.out.println("coops " + pop.getCoops());
        //System.out.println("cheats " + pop.getCheats());

        assertEquals("remaining coops",
                     large_pop*(1-dilution_amount), pop.getCoops().get(0),0);
        assertEquals("remaining cheats",
                     large_pop*(1-dilution_amount), pop.getCheats().get(0),0);

        assertEquals("diluted coops",
                     large_pop*dilution_amount, diluted.getCoops().get(0),0);
        assertEquals("diluted cheats",
                     large_pop*dilution_amount, diluted.getCheats().get(0),0);


        // getNextBinomal currently returns two 1s in a row.
        assertEquals(8.0, pop.getCoops().get(1),0);
        assertEquals(8.0, pop.getCheats().get(1),0);
        assertEquals(1.0, diluted.getCoops().get(1),0);
        assertEquals(1.0, diluted.getCheats().get(1),0);

        pop = new Population(coops, cheats, fitnesses, cheat_advantage, no_u,
                             fc, rng);
        while(pop.getCoops().get(2) > 0) {
            assertEquals("correct amount remains",
                         lt1_pop, pop.getCoops().get(2), 0.0);
            assertEquals("nothing in diluted",
                         0.0, diluted.getCoops().get(2), 0.0);

            //System.out.println("\n\ndilutePopulation()");
            diluted = pop.dilute(dilution_amount);

            //System.out.println("Diluted");
            //System.out.println(diluted);

            //System.out.println("Remaining");
            //System.out.println("coops " + pop.getCoops());
            //System.out.println("cheats " + pop.getCheats());
        }

        assertEquals("no coops remain", 0.0, pop.getCoops().get(2), 0.0);
        assertEquals("nothing is migrated",
                     0.0, diluted.getCoops().get(2), 0.0);
    }

    private double growthFormula(double A, double r, double dt) {
        return A*Math.exp(r*dt);
    }

    @Test
    public void mixPopulations() {
        List<Double> coops  = Arrays.asList(1.0, 2.0, 4.0, 8.0, 16.0);
        List<Double> cheats = Arrays.asList(12.0, 10.0, 8.0, 6.0, 4.0);


        double coop_sum = 0;
        double cheat_sum = 0;
        for (double d:coops)  coop_sum  += d;
        for (double d:cheats) cheat_sum += d;

        List<Double> fitnesses = Arrays.asList(Math.log(2), Math.log(2),
                                               Math.log(2), Math.log(2),
                                               Math.log(2));
        double cheat_adv = 0.02;

        Population pop = new Population(coops, cheats, fitnesses, cheat_adv,
                                        no_u, fc, rng);

        pop.mix(pop);

        assertNotSame(coops, pop.getCoops());
        assertNotSame(cheats, pop.getCheats());

        assertEquals("nCoops updated", coop_sum*2, pop.getNCoops(), 0.0);
        assertEquals("nCheats updated", cheat_sum*2, pop.getNCheats(), 0.0);

        //System.out.println("\n\nNew population");
        //System.out.println(pop);
        for (int i=0; i<coops.size(); i++) {
            assertEquals(coops.get(i)*2, pop.getCoops().get(i), 0.0);
            assertEquals(cheats.get(i)*2, pop.getCheats().get(i), 0.0);
        }
    }

    @Test
    public void mixLargePopulations() {
        List<Double> coops  = Arrays.asList(1e9);
        List<Double> cheats = Arrays.asList(2e9);

        List<Double> fitnesses = Arrays.asList(Math.log(2));
        double cheat_adv = 0.02;

        Population pop = new Population(coops, cheats, fitnesses, cheat_adv,
                                        no_u, fc, rng);

        //System.out.println("\n\nOriginal population");
        //System.out.println(pop);
        pop.mix(pop);

        //System.out.println("\n\nNew population");
        //System.out.println(pop);
        for (int i=0; i<coops.size(); i++) {
            assertEquals(coops.get(i)*2, pop.getCoops().get(i), 0.0);
            assertEquals(cheats.get(i)*2, pop.getCheats().get(i), 0.0);
        }
    }

    @Test
    public void shiftFitness() {
        List<Double> coops  = Arrays.asList(100.0, 1e6);
        List<Double> cheats = Arrays.asList(200.0, 2e7);

        Population pop = new Population(coops, cheats, fitnesses, cheat_adv,
                                        no_u, fc, rng);
        double mutant_freq = 1e-4;
        List<Double> new_freqs = Arrays.asList(1-mutant_freq, mutant_freq);

        pop.shiftFitness(new_freqs);

        assertTrue("more type 0 than type 1 coops",
                   pop.getCoops().get(0) > pop.getCoops().get(1));
        assertTrue("more type 0 than type 1 cheats",
                   pop.getCheats().get(0) > pop.getCheats().get(1));

        assertEquals("total coop amount correct",
                     coops.get(1),
                     pop.getCoops().get(0) + pop.getCoops().get(1), 0.0);

        assertEquals("total cheat amount correct",
                     cheats.get(1),
                     pop.getCheats().get(0) + pop.getCheats().get(1), 0.0);

        double coop_expect = mutant_freq * coops.get(1);
        double coop_samp_error = 2*StrictMath.sqrt(mutant_freq*coops.get(1));
        assertEquals("new coops within sampling error",
                     coop_expect, pop.getCoops().get(1), coop_samp_error);

        double cheat_expect = mutant_freq * cheats.get(1);
        double cheat_samp_error = 2*StrictMath.sqrt(mutant_freq*cheats.get(1));
        assertEquals("new cheats within sampling error",
                     cheat_expect, pop.getCheats().get(1), cheat_samp_error);

        double coop1 = pop.getCoops().get(0);
        double coop2 = pop.getCoops().get(1);
        double cheat1 = pop.getCheats().get(0);
        double cheat2 = pop.getCheats().get(1);

        double coop1_expected = coop1 * StrictMath.exp(fitnesses.get(0));
        double coop2_expected = coop2 * StrictMath.exp(fitnesses.get(1));
        double cheat1_expected = 
            cheat1 * StrictMath.exp(fitnesses.get(0)*(1+cheat_adv));
        double cheat2_expected = 
            cheat2 * StrictMath.exp(fitnesses.get(1)*(1+cheat_adv));

        pop.grow();

        assertEquals("type 1 coop shifted growth",
                     coop1_expected, pop.getCoops().get(0), 0.0);
        assertEquals("type 2 coop shifted growth",
                     coop2_expected, pop.getCoops().get(1), 0.0);

        assertEquals("type 1 cheat shifted growth",
                     cheat1_expected, pop.getCheats().get(0), 0.0);
        assertEquals("type 2 cheat shifted growth",
                     cheat2_expected, pop.getCheats().get(1), 0.0);


    }
}
