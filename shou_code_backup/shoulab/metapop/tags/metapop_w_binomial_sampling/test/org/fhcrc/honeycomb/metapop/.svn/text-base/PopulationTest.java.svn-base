// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class PopulationTest {
  List<Double> fitnesses;
  List<Double> freqs;
  int initial_size;
  double initial_coop_freq;
  double cheat_adv;
  long seed;

  List<Double> coops;
  List<Double> cheats;

  double each_pop;
  Types types;

  Population pop1;
  Population pop2;
  List<Population> pops;

  @Before
  public void setUp() {
    fitnesses = Arrays.asList(Math.log(2), Math.log(2)*1.1);
    freqs = Arrays.asList(0.5, 0.5);
    initial_size = 4;
    initial_coop_freq = 0.5;
    cheat_adv = 0.02;
    seed = 12345L;

    coops  = Arrays.asList(1.0,1.0);
    cheats = Arrays.asList(1.0,1.0);

    each_pop = initial_size * initial_coop_freq / freqs.size();

    types = new Types(fitnesses, freqs);

    pop1 = new Population(initial_size, initial_coop_freq, cheat_adv, types,
                          seed);
    pop2 = new Population(coops, cheats, fitnesses, cheat_adv, seed);

    pops = Arrays.asList(pop1, pop2);

  }

  @Test(expected=IllegalArgumentException.class)
  public void initializeError() {
    List<Double> coops2  = Arrays.asList(1.0, 2.0, 4.0, 8.0, 16.0);
    Population bad_pop = new Population(coops2,cheats,fitnesses,cheat_adv);
  }

  @Test
  public void checkInitialization() {
    for (Population pop:pops) {
      assertEquals(pop.size(),4.0,0.0);
      assertEquals(initial_coop_freq, pop.getCoopFrequency(),0.0);
      assertEquals(initial_size*initial_coop_freq, pop.getNCoops(), 0.0);
      assertEquals(initial_size*initial_coop_freq, pop.getNCheats(), 0.0);
      assertEquals(cheat_adv, pop.getCheatAdvantage(), 0.0);
      assertEquals(seed, pop.getSeed());

      for (int i=0; i<pop.getCoopFrequencies().size(); i++) {
        assertEquals(freqs.get(i),pop.getCoopFrequencies().get(i), 0.0);
        assertEquals(freqs.get(i),pop.getCheatFrequencies().get(i), 0.0);
      }
      for (int i=0; i<pop.getFitnesses().size(); i++) {
        assertEquals(fitnesses.get(i),pop.getFitnesses().get(i), 0.0);
      }
    }
  }

  @Test
  public void copyPopulation() {
    Population pop_copy = new Population(pop1);
    assertNotSame("population", pop1, pop_copy);
    assertNotSame("coop array", pop1.getCoops(), pop_copy.getCoops());
    assertNotSame("cheat array", pop1.getCheats(), pop_copy.getCheats());
    assertNotSame("coop freq array",
                  pop1.getCoopFrequencies(), pop_copy.getCoopFrequencies());
    assertNotSame("cheat freq array",
                  pop1.getCheatFrequencies(), pop_copy.getCheatFrequencies());
    assertNotSame("fitness array",
                  pop1.getFitnesses(), pop_copy.getFitnesses());
  }

  @Test
  public void growPopulation() {
    List<Double> fitnesses = Arrays.asList(Math.log(2), Math.log(2)*1.1);
    List<Double> freqs = Arrays.asList(0.5, 0.5);
    int initial_size = 4;
    double initial_coop_freq = 0.5;
    double cheat_adv = 0.02;

    Types types = new Types(fitnesses, freqs);
    Population pop = 
      new Population(initial_size, initial_coop_freq, cheat_adv, types);

    double each_pop = initial_size * initial_coop_freq / freqs.size();

    double type1_coop_expected = growthFormula(each_pop,fitnesses.get(0),1);
    double type2_coop_expected = growthFormula(each_pop,fitnesses.get(1),1);
    double type1_cheat_expected =
      growthFormula(each_pop, fitnesses.get(0)*(1+cheat_adv), 1); 
    double type2_cheat_expected =
      growthFormula(each_pop, fitnesses.get(1)*(1+cheat_adv), 1); 

    double nCoops  = type1_coop_expected  + type2_coop_expected;
    double nCheats = type1_cheat_expected + type2_cheat_expected;
    double expected_coop_freq = nCoops/(nCoops+nCheats);

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
    assertEquals("New coop freq", expected_coop_freq, 
                 pop.getCoopFrequency(), 0);
    //System.out.println();

  }

  @Test
  public void dilutePopulation() {
    double large_pop = 1e8;
    double small_pop = 1e1;
    List<Double> coops  = Arrays.asList(large_pop, small_pop);
    List<Double> cheats = Arrays.asList(large_pop, small_pop);
    List<Double> fitnesses = Arrays.asList(Math.log(2),Math.log(2));
    double cheat_advantage = 0.02;
    double dilution = 0.1;

    Population pop = new Population(coops,cheats,fitnesses,cheat_advantage);

    Population diluted = pop.dilute(dilution);
    //System.out.println(diluted);

    assertEquals(large_pop*dilution, diluted.getCoops().get(0),0);
    assertEquals(large_pop*dilution, diluted.getCheats().get(0),0);
  }

  private double growthFormula(double A, double r, double dt) {
    return A*Math.exp(r*dt);
  }

  @Test
  public void mixPopulations() {
    List<Double> coops_orig  = Arrays.asList(1.0, 2.0, 4.0, 8.0, 16.0);
    List<Double> coops  = Arrays.asList(1.0, 2.0, 4.0, 8.0, 16.0);
    List<Double> cheats_orig = Arrays.asList(12.0, 10.0, 8.0, 6.0, 4.0);
    List<Double> cheats = Arrays.asList(12.0, 10.0, 8.0, 6.0, 4.0);

    List<Double> fitnesses = Arrays.asList(Math.log(2),Math.log(2),
                                           Math.log(2),Math.log(2),
                                           Math.log(2));
    double cheat_adv = 0.02;

    Population pop = new Population(coops,cheats,fitnesses,cheat_adv);

    pop.mix(pop);

    //System.out.println("\n\nNew population");
    //System.out.println(pop);
    for (int i=0; i<coops.size(); i++) {
      assertEquals(coops_orig.get(i)*2, pop.getCoops().get(i), 0.0);
      assertEquals(cheats_orig.get(i)*2, pop.getCheats().get(i), 0.0);
    }
  }

  @Test
  public void mixLargePopulations() {
    List<Double> coops  = Arrays.asList(1e9);
    List<Double> cheats = Arrays.asList(2e9);

    List<Double> fitnesses = Arrays.asList(Math.log(2));
    double cheat_adv = 0.02;

    Population pop = new Population(coops,cheats,fitnesses,cheat_adv);

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
}
