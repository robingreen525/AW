package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinateProvider;
import org.fhcrc.honeycomb.metapop.coordinate.picker.*;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

class Provider implements CoordinateProvider {
    int rows;
    int cols;
    Coordinate current;

    public Provider(int max_row, int max_col, Coordinate current) {
        this.rows = max_row;
        this.cols = max_col;
        this.current = current;
    }

    public Provider(int max_row, int max_col) {
        this(max_row, max_col, null);
    }

    @Override
    public Coordinate getCoordinate() { return new Coordinate(current); }
}

public class CoordinatePickerTest {
    private boolean exclude = true;
    private boolean include = false;
    private long seed = 12345L;
    private RandomNumberUser rng = new RandomNumberUser(seed);

    private int rows = 2;
    private int cols = 2;
    private CoordinateProvider prov;

    @Before
    public void setUp() {
        prov = new Provider(rows, cols, new Coordinate(1,2));
    }

    @Test
    public void specified() {
        List<Coordinate> initial_locations = Arrays.asList(
                                                new Coordinate(1,1),
                                                new Coordinate(2,2),
                                                new Coordinate(3,3),
                                                new Coordinate(4,4),
                                                new Coordinate(5,5),
                                                new Coordinate(6,6),
                                                new Coordinate(7,7),
                                                new Coordinate(8,8),
                                                new Coordinate(9,9),
                                                new Coordinate(10,10)
                                             );
       CoordinatePicker spec = new SpecifiedPicker(initial_locations);
       for (int i=0; i<initial_locations.size(); i++) {
           assertEquals("failed - wrong pick",
                        initial_locations.get(i), spec.pick());
       }
    }

    @Test(expected = IllegalArgumentException.class)
    public void specifiedError() {
       CoordinatePicker spec = new SpecifiedPicker(null);
       spec.pick();
    }

    @Test(expected = NoSuchElementException.class)
    public void specifiedError2() {
       CoordinatePicker spec =
           new SpecifiedPicker(Arrays.asList(new Coordinate(1,1)));
       spec.pick(2);
    }

    @Test(expected = NullPointerException.class)
    public void currentCoordNotSet() {
        CoordinateProvider prov = new Provider(rows, cols);
        boolean exclude_current = true;

        CoordinatePicker random_picker = 
            new RandomPicker(rows, cols, exclude, prov, rng);
        random_picker.pick();
    }

    @Test(expected = IllegalArgumentException.class)
    public void cantBeUnique() {
        CoordinatePicker uni = 
            new UniqueRandomPicker(rows, cols, include, prov, rng);
        uni.pick(10);
    }

    @Test
    public void randomPickerExclude() {

        CoordinatePicker random_picker = 
            new RandomPicker(rows, cols, exclude, prov, rng);

        for (int i=0; i<100; i++) {
            assertNotEquals("not equals",
                            prov.getCoordinate(), random_picker.pick());
        }

        random_picker = new RandomPicker(rows, cols, include, prov, rng);
        boolean same = false;
        for (int i=0; i<100; i++) {
            if (prov.getCoordinate().equals(random_picker.pick())) {
                same = true;
                break;
            }
        }
        assertTrue("at least one equals", same);
    }

    @Test
    public void checkSizes() {
        int max_distance = 1;
        int samples = 10;
        rows = 10;
        cols = 10;
        prov = new Provider(rows, cols, new Coordinate(3, 3));

        CoordinatePicker rand  = 
            new RandomPicker(rows, cols, include, prov, rng);
        CoordinatePicker uni   = 
            new UniqueRandomPicker(rows, cols, include, prov, rng);
        CoordinatePicker neigh = 
            new RandomNeighborPicker(rows, cols, exclude, prov, max_distance,
                                     rng);

        assertEquals("RandomPicker", samples, rand.pick(samples).size());
        assertEquals("NeighborPicker", samples, neigh.pick(samples).size());
        assertEquals("UniquePicker", samples, uni.pick(samples).size());
    }

    @Test
    public void unique() {
        int samples = 100;
        rows = 10;
        cols = 10;
        prov = new Provider(rows, cols, new Coordinate(3, 3));
        CoordinatePicker uni =
            new UniqueRandomPicker(rows, cols, include, prov, rng);

        testUnique(uni.pick(samples));

        List<Coordinate> test = new ArrayList<Coordinate>(samples);
        for (int i=0; i<samples; i++) test.add(uni.pick());
        testUnique(test);
    }

    private void testUnique(List<Coordinate> test_list) {
        Set<Coordinate> seen = new HashSet<Coordinate>(test_list.size());
        for (Coordinate t : test_list) {
            assertFalse("failed - coordinate is not unique", seen.contains(t));
            seen.add(t);
        }
    }

    @Test
    public void testNeighborhood() {
        int max_distance = 1;
        int samples = 1000;
        rows = 5;
        cols = 5;

        Coordinate current = new Coordinate(3, 3);
        prov = new Provider(rows, cols, current);
        List<Coordinate> allowed = Arrays.asList(new Coordinate(2,2),
                                                 new Coordinate(2,3),
                                                 new Coordinate(2,4),
                                                 new Coordinate(3,2),
                                                 new Coordinate(3,4),
                                                 new Coordinate(4,2),
                                                 new Coordinate(4,3),
                                                 new Coordinate(4,4));

        CoordinatePicker neigh = new RandomNeighborPicker(rows, cols, exclude,
                                                          prov, max_distance,
                                                          rng);
        List<Coordinate> test = neigh.pick(samples);
        testRange(test, current, allowed);

        current = new Coordinate(1, 1);
        prov = new Provider(rows, cols, current);
        allowed = Arrays.asList(new Coordinate(1,2),
                                new Coordinate(1,5),
                                new Coordinate(2,1),
                                new Coordinate(2,2),
                                new Coordinate(2,5),
                                new Coordinate(5,1),
                                new Coordinate(5,2),
                                new Coordinate(5,5));

        neigh = new RandomNeighborPicker(rows, cols, exclude, prov,
                                         max_distance, rng);
        test = neigh.pick(samples);
        testRange(test, current, allowed);
    }

    private void testRange(List<Coordinate> test, Coordinate excluded,
                           List<Coordinate> allowed)

    {
        assertFalse("failed - contains excluded coordinate",
                    test.contains(excluded));
        assertTrue("failed - not all possible coordinates are present",
                   test.containsAll(allowed));
        assertTrue("failed - wrong coordinates are present",
                   allowed.containsAll(test));
    }
}
