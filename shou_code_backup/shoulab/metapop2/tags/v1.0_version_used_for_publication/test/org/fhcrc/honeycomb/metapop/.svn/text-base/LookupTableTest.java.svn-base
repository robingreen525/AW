package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.lookuptable.LookupTable;
import org.fhcrc.honeycomb.metapop.lookuptable.ConcatLookupTable;

import org.junit.*;
import static org.junit.Assert.*;

public class LookupTableTest {
    private LookupTable lookup = new ConcatLookupTable();
    private int a;
    private int b;
    private int c;
    private double val;
    private double not_present = -1.0;

    @Before
    public void setUp() {
        a = 1;
        b = 2;
        c = 3;
        val = 10.3;
    }

    @Test
    public void simpleTest() {
        lookup.setValue(val, a, b, c);
        assertEquals("wrong val", val, lookup.getValue(a,b,c), 0.0);
        //System.out.println(lookup.getValue(a,b,c));
    }

    @Test
    public void valAbsent() {
        assertEquals("wrong return", not_present,
                     lookup.getValue(a-1, b, c), 0.0);
    }
}
