// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import org.junit.*;
import static org.junit.Assert.*;
import org.junit.rules.*;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math.stat.StatUtils;

public class SamplerTest {
  @Rule
  public ExpectedException thrown=ExpectedException.none();

  private static final long SEED    = 12345L;
  private static final double ALPHA = 0.01;
  private static final Sampler samp = new Sampler(ALPHA, SEED);

  @Test
  public void sampleTest() {
    List<Double> pops = Arrays.asList(1e1, 1e6);
    double amount = 0.5;

    //System.out.println("Setting up first Sampler");

    for (int i=0; i<10; i++) {

      HashMap<String, List<Double>> sampled = 
        samp.sampleBinomial(pops, amount);

      //System.out.println(sampled);
      assertEquals("large sample, sampled",
                   pops.get(1)*amount, sampled.get("sampled").get(1), 0.0);

      assertEquals("large sample, remaining",
                   pops.get(1)*amount, 
                   sampled.get("remaining").get(1), 0.0);

      assertEquals("small sample", 
                   pops.get(0),
                   sampled.get("sampled").get(0)+
                   sampled.get("remaining").get(0),
                   0.0);
    }
  }

  @Test
  public void binomialDistribution() throws IOException {
    // Writes out a bunch of random numbers, so we can check the 
    // distribution.
    int nSamples = 100000;

    double small_pop = 1e1;
    double amount = 0.5;
    List<Double> pops = Arrays.asList(small_pop);

    double expected_mean = small_pop * amount;
    double expected_var  = small_pop * amount * (1-amount);
    double tolerance = 0.005;

    double[] samples = new double[nSamples];

    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter("random_binomial.txt"));
      for (int i=0; i<nSamples; i++) {
        HashMap<String, List<Double>> sampled =
          samp.sampleBinomial(pops, amount);
        double sample = sampled.get("sampled").get(0);
        writer.write(sample + " ");
        samples[i] = sample;
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) writer.close();
    }

    assertEquals(expected_mean, StatUtils.mean(samples), tolerance);
    assertEquals(expected_var, StatUtils.variance(samples), tolerance);
  }

  @Test
  public void populationTooBig() {
    thrown.expect(IllegalArgumentException.class);
    double alpha = 0.0;
    double amount = 0.1;
    double too_big = Integer.MAX_VALUE * 10.0;
    List<Double> pop = Arrays.asList(too_big);
    Sampler sam = new Sampler(alpha, SEED);
    sam.sampleBinomial(pop, amount);
  }
}
