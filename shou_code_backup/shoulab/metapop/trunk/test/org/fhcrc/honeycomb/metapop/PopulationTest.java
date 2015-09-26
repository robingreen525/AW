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
    protected List<Double> fitnesses = Arrays.asList(0.1, 0.2);
    protected List<Double> freqs = Arrays.asList(0.9, 0.1);
    protected Types types = new Types(fitnesses, freqs);

    // pop1
    protected int initial_size = 10000;
    protected double initial_cheat_freq = 0.7;
    protected double cheat_adv = 0.02;
    private double each_pop = initial_size * initial_cheat_freq / freqs.size();

    private int pop1_total_coops = (int) (initial_size*(1-initial_cheat_freq));
    private int pop1_coop1 = (int) (pop1_total_coops * freqs.get(0));
    private int pop1_coop2 = (int) (pop1_total_coops * freqs.get(1));

    private int pop1_total_cheats = (int) (initial_size*(initial_cheat_freq));
    private int pop1_cheat1 = (int) (pop1_total_cheats * freqs.get(0));
    private int pop1_cheat2 = (int) (pop1_total_cheats * freqs.get(1));

    // pop2
    private int pop2_coop1 = 30;
    private int pop2_coop2 = 3;
    private int pop2_cheat1 = 70;
    private int pop2_cheat2 = 7;
    private int pop2_total = pop2_coop1 + pop2_coop2 +
                             pop2_cheat1 + pop2_cheat2;
    private int pop2_total_coops = pop2_coop1 + pop2_coop2;
    private int pop2_total_cheats = pop2_cheat1 + pop2_cheat2;
    private List<Integer> coops = Arrays.asList(pop2_coop1, pop2_coop2);
    private List<Integer> cheats = Arrays.asList(pop2_cheat1, pop2_cheat2);

    // shared
    protected double no_u = 0;
    protected double u = 1e-4;

    protected long seed = 12345L;
    protected RandomNumberUser rng = new RandomNumberUser(seed);
    protected FitnessCalculator fc = new FitnessUnchanged();
    protected CheaterLoadCalculator clc;


    protected Population pop1;
    protected Population pop2;
    protected List<Population> pops;

    @Before
    public void setUp() {
        //System.out.println("pop1");
        pop1 = new Population(initial_size, initial_cheat_freq, cheat_adv, 
                              no_u, types, fc, rng);

        //System.out.println("pop2");
        pop2 = new Population(coops, cheats, fitnesses, cheat_adv, no_u, fc,
                              rng);

        pops = Arrays.asList(pop1, pop2);

    }

    @Test
    public void checkPop1Initialization() {
        assertEquals("pop1 size", initial_size, pop1.getSize(), 0.0);

        double coop2_sampling_error = Math.sqrt(pop1_coop2);
        double cheat2_sampling_error = Math.sqrt(pop1_cheat2);

        assertEquals("pop1 total coop size",
                     pop1_total_coops, pop1.getNCoops(),
                     coop2_sampling_error);

        assertEquals("pop1 type1 coop size", 
                     pop1_coop1, pop1.getCoops().get(0),
                     coop2_sampling_error);
        assertEquals("pop1 type2 coop size", 
                     pop1_coop2, pop1.getCoops().get(1),
                     coop2_sampling_error);

        assertEquals("pop1 total cheat size",
                     pop1_total_cheats, pop1.getNCheats(),
                     cheat2_sampling_error);
        assertEquals("pop1 type1 cheat size", 
                     pop1_cheat1, pop1.getCheats().get(0),
                     cheat2_sampling_error);
        assertEquals("pop1 type2 cheat size", 
                     pop1_cheat2, pop1.getCheats().get(1),
                     cheat2_sampling_error);
        checkShared(pop1);
    }

    @Test
    public void checkPop2Initialization() {
        double coop2_sampling_error = Math.sqrt(pop2_coop2);
        double cheat2_sampling_error = Math.sqrt(pop2_cheat2);

        assertEquals("pop2 total coop size",
                     pop2_total_coops, pop2.getNCoops(),
                     coop2_sampling_error);

        assertEquals("pop2 type1 coop size", 
                     pop2_coop1, pop2.getCoops().get(0), 
                     coop2_sampling_error);
        assertEquals("pop2 type2 coop size", 
                     pop2_coop2, pop2.getCoops().get(1), 
                     coop2_sampling_error);

        assertEquals("pop2 total cheat size",
                     pop2_total_cheats, pop2.getNCheats(),
                     cheat2_sampling_error);
        assertEquals("pop2 type1 cheat size", 
                     pop2_cheat1, pop2.getCheats().get(0),
                     cheat2_sampling_error);
        assertEquals("pop2 type2 cheat size", 
                     pop2_cheat2, pop2.getCheats().get(1),
                     cheat2_sampling_error);
        checkShared(pop2);
    }


    private void checkShared(Population pop) {
        assertSame("rng", rng, pop.getRNG());
        assertEquals("seed", seed, pop.getRNG().getSeed());
        assertThat("fitnesses", fitnesses, is(pop.getMaxFitnesses()));
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

        List<Integer> type1_coops = new ArrayList<Integer>();
        List<Integer> type1_cheats = new ArrayList<Integer>();
        Population one = test_pops.get(0);
        for (int i=0; i<test_pops.size(); i++) {
            Population pop = test_pops.get(i);

            if (i == 1) assertNotSame("pops are same", one, pop);

            type1_coops.add(pop.getCoops().get(0));
            type1_cheats.add(pop.getCheats().get(0));

            double cheat_freq = pop.getNCheats()/(double) pop.getSize();

            assertEquals("cheat freq", initial_cheat_freq, cheat_freq, 0.01);
        }
        Set<Integer> type1_coop_set = new HashSet<Integer>(type1_coops);
        Set<Integer> type1_cheat_set = new HashSet<Integer>(type1_cheats);

        assertTrue("type1 coops not different size",
                   type1_coop_set.size() > 1);
        assertTrue("type1 cheats not different size",
                   type1_cheat_set.size() > 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void initializeError() {
        List<Integer> coops2  = Arrays.asList(1, 2, 4, 8, 16);
        Population bad_pop = new Population(coops2, cheats, fitnesses,
                                            cheat_adv, no_u, fc, rng);
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
    public void mutation() {
        double u = 1.0;
        int[] before = {1000, 2000};
        Estimate expected = new Estimate(u*before[0]);

        Population pop = new Population(initial_size, initial_cheat_freq,
                                       cheat_adv, u, types, fc, rng);


        int[] after = pop.mutate(before);
        assertEquals("remaining coop size not correct",
                     before[0]-expected.getEstimate(), after[0], 0.0);
        assertEquals("remaining cheat size not correct",
                     before[1]+expected.getEstimate(), after[1], 0.0);

        int[] after2 = pop.mutate(before);
        assertEquals("remaining coop size not correct",
                     before[0]-expected.getEstimate(), after2[0], 0.0);
        assertEquals("remaining cheat size not correct",
                     before[1]+expected.getEstimate(), after2[1], 0.0);

        u = 0.5;
        expected = new Estimate(u*before[0]);
        pop = new Population(initial_size, initial_cheat_freq,
                             cheat_adv, u, types, fc, rng);
        after = pop.mutate(before);
        assertEquals("remaining coop size not correct",
                     before[0]-expected.getEstimate(), after[0],
                     expected.getError());
        assertEquals("remaining cheat size not correct",
                     before[1]+expected.getEstimate(), after[1],
                     expected.getError());

    }

    @Test
    public void growPopulation() {
        //System.out.println("pop1 before " + pop1);
        double pop1_coop1_before = pop1.getCoops().get(0);
        double pop1_coop2_before = pop1.getCoops().get(1);
        double pop1_cheat1_before = pop1.getCheats().get(0);
        double pop1_cheat2_before = pop1.getCheats().get(1);


        double pop1_coop1_expected = growthFormula(pop1_coop1_before,
                                                   fitnesses.get(0), 1);
        //System.out.println("pop1 coop1 expected " + pop1_coop1_expected);
        double pop1_coop1_error =
            StrictMath.sqrt(pop1_coop1_before*fitnesses.get(0));

        pop1.grow();
        //System.out.println("pop1 after " + pop1);

        assertEquals("pop1 coop1 wrong size", pop1_coop1_expected,
                     pop1.getCoops().get(0), pop1_coop1_error);

    }

    @Test
    public void shrinkPopulation() {
        List<Double> fitnesses2 = Arrays.asList(-0.1);
        List<Integer> large = Arrays.asList(30000);
        List<Integer> small = Arrays.asList(20);

        pop2 = new Population(large, small, fitnesses2, cheat_adv, no_u, fc,
                              rng);

        Estimate large_expected =
            new Estimate(large.get(0) * -fitnesses2.get(0));
        Estimate small_expected =
            new Estimate(small.get(0) * -fitnesses2.get(0));
        pop2.grow();

        assertEquals("large shrunk wrong size",
                     large.get(0)-large_expected.getEstimate(),
                     pop2.getNCoops(),
                     large_expected.getError(2));

        assertEquals("small shrunk wrong size",
                     small.get(0)-small_expected.getEstimate(),
                     pop2.getNCheats(),
                     small_expected.getError(2));
    }

    @Test
    public void dilutePopulation() {
        double dilution_fraction = 0.1;
        List<Integer> coops = pop1.getCoops();
        List<Integer> cheats = pop1.getCheats();
        Estimate coop1  = new Estimate(dilution_fraction * coops.get(0));
        Estimate coop2  = new Estimate(dilution_fraction * coops.get(1));
        Estimate cheat1 = new Estimate(dilution_fraction * cheats.get(0));
        Estimate cheat2 = new Estimate(dilution_fraction * cheats.get(1));

        //System.out.println("before dilution: " + pop1);
        Population migrants = pop1.dilute(dilution_fraction);
        //System.out.println("after dilution: " + pop1);

        List<Integer> mig_coops  = migrants.getCoops();
        List<Integer> mig_cheats = migrants.getCheats();
        assertEquals("coop1 wrong size",
                     coop1.getEstimate(), mig_coops.get(0),
                     coop1.getError(2));
        assertEquals("coop2 wrong size",
                     coop2.getEstimate(), mig_coops.get(1),
                     coop2.getError(2));
        assertEquals("cheat1 wrong size",
                     cheat1.getEstimate(), mig_cheats.get(0),
                     cheat1.getError(2));
        assertEquals("cheat2 wrong size",
                     cheat2.getEstimate(), mig_cheats.get(1),
                     cheat2.getError(2));

    }

    private double growthFormula(double A, double r, double dt) {
        return A*Math.exp(r*dt);
    }

    @Test
    public void mixPopulations() {
        List<Integer> coops  = Arrays.asList(1, 2, 4, 8, 16);
        List<Integer> cheats = Arrays.asList(12, 10, 8, 6, 4);


        int coop_sum = 0;
        int cheat_sum = 0;
        for (int co:coops)  coop_sum  += co;
        for (int ch:cheats) cheat_sum += ch;

        List<Double> fitnesses = Arrays.asList(Math.log(2), Math.log(2),
                                               Math.log(2), Math.log(2),
                                               Math.log(2));
        double cheat_adv = 0.02;

        Population pop = new Population(coops, cheats, fitnesses, cheat_adv,
                                        no_u, fc, rng);

        pop.mix(pop);

        assertNotSame(coops, pop.getCoops());
        assertNotSame(cheats, pop.getCheats());

        assertEquals("nCoops updated", coop_sum*2, pop.getNCoops());
        assertEquals("nCheats updated", cheat_sum*2, pop.getNCheats());

        //System.out.println("\n\nNew population");
        //System.out.println(pop);
        for (int i=0; i<coops.size(); i++) {
            int test_coop = pop.getCoops().get(i).intValue(); 
            int test_cheat = pop.getCheats().get(i).intValue(); 
            assertEquals("post-mix coops", coops.get(i)*2, test_coop);
            assertEquals("post-mix cheats", cheats.get(i)*2, test_cheat);
        }
    }

    @Test
    public void mixLargePopulations() {
        List<Integer> coops  = Arrays.asList(1000000);
        List<Integer> cheats = Arrays.asList(2000000);

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

    @Test(expected=IllegalArgumentException.class)
    public void badShift() {
        List<Double> new_freqs = Arrays.asList(0.1);
        pop1.shiftFitness(new_freqs);
    }

    @Test
    public void shiftFitness() {
        List<Double> new_freqs = Arrays.asList(0.9, 0.1);
        double coop2_total = pop1.getCoops().get(1);
        double cheat2_total = pop1.getCheats().get(1);
        Estimate coop2  = new Estimate(new_freqs.get(1) * coop2_total);
        Estimate cheat2 = new Estimate(new_freqs.get(1) * cheat2_total);

        //System.out.println("before shift : " + pop1);
        pop1.shiftFitness(new_freqs);
        //System.out.println("after shift : " + pop1);

        assertEquals("coop sum not correct",
                     coop2_total, pop1.getNCoops(), 0.0);
        assertEquals("cheat sum not correct",
                     cheat2_total, pop1.getNCheats(), 0.0);

        assertEquals("coop2 number not correct",
                     coop2.getEstimate(), pop1.getCoops().get(1),
                     coop2.getError(2));
        assertEquals("cheat2 number not correct",
                     cheat2.getEstimate(), pop1.getCheats().get(1),
                     cheat2.getError(2));
    }
}
