package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.RandomNumberUser;
import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.OccupiedLocations;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.picker.CoordinatePicker;
import org.fhcrc.honeycomb.metapop.coordinate.picker.SpecifiedPicker;

import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.fitness.SpecifiedCalculator;

import org.fhcrc.honeycomb.metapop.migration.MigrationRule;
import org.fhcrc.honeycomb.metapop.migration.IndividualMigration;
import org.fhcrc.honeycomb.metapop.migration.PropaguleMigration;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

/** 
 * Tests MigrationRules.
 *
 * Created on 30 May, 2013
 * @author Adam Waite
 * @version $Id: MigrationTest.java 2106 2013-06-03 20:17:42Z ajwaite $
 *
 */
public class MigrationTest {
    private double conf = 0.95;
    private int rows = 20;
    private int cols = 20;
    private int initial_size = 100;
    private double death_rate = 0.1;

    private Subpopulation sub1;
    private FitnessCalculator fc = new SpecifiedCalculator(0.0, death_rate);

    private Population pop;
    private List<Population> pops = new ArrayList<Population>();

    private OccupiedLocations ols;

    private RandomNumberUser rng = new RandomNumberUser(1);
    private Coordinate coord     = new Coordinate(1,2);
    private List<Coordinate> new_coords =
                Arrays.asList(new Coordinate(1,1), new Coordinate(2,2),
                              new Coordinate(3,3), new Coordinate(4,4),
                              new Coordinate(5,5), new Coordinate(6,6),
                              new Coordinate(7,7), new Coordinate(8,8),
                              new Coordinate(9,9), new Coordinate(10,10));
    private CoordinatePicker cp  = new SpecifiedPicker(new_coords);

    private double migration_rate = 0.1;
    private CoordinatePicker picker = new SpecifiedPicker(new_coords);
    private MigrationRule individual_migration = 
        new IndividualMigration(migration_rate, picker);
    private MigrationRule propagule_migration = 
        new PropaguleMigration(migration_rate, picker);

    private int size_before;

    private Estimate mig_size;
    private double[] ci;

    @Before
    public void setUp() {
        sub1 = new Subpopulation(initial_size, fc, "subpop", rng);
        pop = new Population(Arrays.asList(sub1), coord, 0.0, rng);
        pops.add(pop);
        ols = new OccupiedLocations(pops, 10);

        size_before = ols.getSize();

        Estimate mig_size = 
            IntegerEstimate.binomialEstimate(pop.getSize(),
                                             migration_rate, conf);
        ci = mig_size.getCI();
    }

    @Test
    public void propaguleMigration() {
        double resource_before = pops.get(0).getResource();
        //System.out.println("before: " + ols);
        propagule_migration.migrate(ols);
        //System.out.println("after: " + ols);

        assertEquals("wrong number of migs",
                     size_before+1, ols.getSize(), 0.0);

        assertEquals("brought resource",
                     ols.getPopulationAt(coord).getResource(), 0.0, 0.0);
    }

    @Test
    public void individualMigration() {
        //System.out.println("before: " + ols);
        individual_migration.migrate(ols);
        //System.out.println("after: " + ols);
        int size_after = ols.getSize();

        assertTrue("wrong number of migs",
                   size_after > size_before + ci[0] &&
                   size_after < size_before + ci[1]);

        for (Population pop:ols.getList()) {
            if (!pop.getCoordinate().equals(coord)) {
                assertEquals("size not 1", 1, pop.getSize());
                assertEquals("brought resource", pop.getResource(), 0, 0.0);
            }
        }
    }
}
