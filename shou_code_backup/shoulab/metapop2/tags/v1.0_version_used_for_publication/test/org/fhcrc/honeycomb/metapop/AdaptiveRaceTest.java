package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.experiment.GlobalDilutionAR;
import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

public class AdaptiveRaceTest {

    @Test
    public void adaptiveRace() {
        System.out.println("Running AdaptiveRaceTest\n");
        GlobalDilutionAR.main(
            new String[] {"local", "indv", "1.00e+05", "5.00e-01", "2.60e+00", 
                          "5.50e+00", "5.00e-01", "1.00e+01", "1.20e+00",
                          "1.00e+01", "2.00e+01", "7.00e-01", "0.00e+00", "1.00e+01",
                          "5.00e-01", "1.00e-04", "0.0", "0.0", "False",
                          "1370969005", "1370969006", "1370969007",
                          "1370969008", "1370969009", "1.00e+00", "1.00e+00",
                          "output_test/adaptive_race_test/"});
    }
}
