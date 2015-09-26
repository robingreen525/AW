package org.fhcrc.honeycomb.metapop;

import java.util.List;
import java.util.ArrayList;

import org.junit.*;
import static org.junit.Assert.*;


public class RandomNumberUserTest {
    private RandomNumberUser rng1;
    private RandomNumberUser rng2;
    private long new_seed = 1L;

    @Before
    public void setUp() {
        rng1 = new RandomNumberUser();
        rng2 = new RandomNumberUser();
    }

    @Test
    public void initialization() {
        assertNotEquals("using different default seed",
                        rng1.getSeed(), rng2.getSeed());

        rng1.reSeed(new_seed);

        assertEquals("change seed", new_seed, rng1.getSeed());
        assertNotEquals("new seed change is local",
                        rng1.getSeed(), rng2.getSeed());
    }

    @Test
    public void stream() {
        List<Integer> rng1_stream = new ArrayList<Integer>();
        List<Integer> rng2_stream = new ArrayList<Integer>();

        rng1.reSeed(new_seed);
        for (int i=0; i<5; i++) {
            rng1_stream.add(rng1.getNextInt(0,10));
        }

        rng2.reSeed(new_seed);
        for (int i=0; i<5; i++) {
            rng2_stream.add(rng2.getNextInt(0,10));
        }

        assertEquals("numbers equal after reseed", rng1_stream, rng2_stream);
    }
}
