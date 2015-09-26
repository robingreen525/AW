package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.fitness.cheaterloadcalculator.*;
import org.fhcrc.honeycomb.metapop.fitness.fitnesscalculator.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Arrays;

public class FitnessCalculatorTest {
    private List<Double> max_fitnesses = 
        Arrays.asList(Math.log(2), Math.log(2)*1.1);
    private double cheat_adv = 0.02;
    private List<Double> coops  = Arrays.asList(1.0,1.0);
    private List<Double> cheats = Arrays.asList(1.0,1.0);

    private RandomNumberUser rng = new RandomNumberUser(12345L);

    private Population pop;
    private double max_death_rate = -max_fitnesses.get(0);

    private double no_u = 0;

    @Test
    public void fitnessUnchanged() {
        FitnessCalculator fc = new FitnessUnchanged();
        pop = new Population(coops, cheats, max_fitnesses, cheat_adv, no_u,
                             fc, rng);

        for (int i=0; i<max_fitnesses.size(); i++) {
            List<Double> fitnesses = fc.calculate(pop, max_fitnesses.get(i));

            assertEquals("FitnessUnchanged",
                         fitnesses.get(0), max_fitnesses.get(i), 0.0);

            assertEquals("FitnessUnchanged",
                         fitnesses.get(1),
                         max_fitnesses.get(i) * (1+cheat_adv), 0.0);
        }
    }

    @Test
    public void fitnessAfter() {
        CheaterLoadCalculator clc = new LinearCheaterLoad(max_death_rate);
        FitnessCalculator fc = new FitnessAfterLoad(clc);
        pop = new Population(coops, cheats, max_fitnesses, cheat_adv, no_u, 
                             fc, rng);

        double type1_expected =
            clc.calculate(pop.getCheatFrequency(), max_fitnesses.get(0));

        List<Double> fitnesses = fc.calculate(pop, max_fitnesses.get(0));
        assertEquals("FitnessAfterLoad coop1",
                     type1_expected, fitnesses.get(0), 0.0);
        assertEquals("FitnessAfterLoad cheat1",
                     type1_expected, fitnesses.get(1), 0.0);

        double type2_expected =
            clc.calculate(pop.getCheatFrequency(), max_fitnesses.get(1));

        fitnesses = fc.calculate(pop, max_fitnesses.get(1));
        assertEquals("FitnessAfterLoad coop2",
                     type2_expected, fitnesses.get(0), 0.0);
        assertEquals("FitnessAfterLoad cheat2",
                     type2_expected * (1+cheat_adv), fitnesses.get(1), 0.0);
    }

    @Test
    public void fitnessBefore() {
        CheaterLoadCalculator clc = new LinearCheaterLoad(max_death_rate);
        FitnessCalculator fc = new FitnessBeforeLoad(clc);
        pop = new Population(coops, cheats, max_fitnesses, cheat_adv, no_u, 
                             fc, rng);

        double type1_coop_expected =
            clc.calculate(pop.getCheatFrequency(), max_fitnesses.get(0));
        double type1_cheat_expected =
            clc.calculate(pop.getCheatFrequency(),
                          max_fitnesses.get(0) * (1+pop.getCheatAdvantage()));

        List<Double> fitnesses = fc.calculate(pop, max_fitnesses.get(0));
        assertEquals("FitnessAfterLoad coop1",
                     type1_coop_expected, fitnesses.get(0), 0.0);
        assertEquals("FitnessAfterLoad cheat1",
                     type1_cheat_expected, fitnesses.get(1), 0.0);


        double type2_coop_expected =
            clc.calculate(pop.getCheatFrequency(), max_fitnesses.get(1));
        double type2_cheat_expected =
            clc.calculate(pop.getCheatFrequency(),
                          max_fitnesses.get(1) * (1+pop.getCheatAdvantage()));

        fitnesses = fc.calculate(pop, max_fitnesses.get(1));
        assertEquals("FitnessAfterLoad coop2",
                     type2_coop_expected, fitnesses.get(0), 0.0);
        assertEquals("FitnessAfterLoad cheat2",
                     type2_cheat_expected, fitnesses.get(1), 0.0);
    }
}
