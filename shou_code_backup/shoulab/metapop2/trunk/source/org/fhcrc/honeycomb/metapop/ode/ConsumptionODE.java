package org.fhcrc.honeycomb.metapop.ode;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;
import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.ode.sampling.StepNormalizerMode;
import org.apache.commons.math3.ode.sampling.StepNormalizerBounds;
import org.apache.commons.math3.ode.events.EventHandler;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/** 
 * Uses ODEs to accurately calculate growth rates.
 *
 * Created on 2 Aug, 2013
 * @author Adam Waite
 * @version $Rev: 2354 $, $Date: 2013-10-06 12:53:49 -0700 (Sun, 06 Oct 2013) $, $Author: ajwaite $
 *
 */
public class ConsumptionODE implements FirstOrderDifferentialEquations {
    private Population pop;
    private List<Subpopulation> subpops = new ArrayList<Subpopulation>();
    private int n_subpops;
    private int n_states;
    private double capacity;

    // Integrator
    private double minStep = 1.0e-12;
    private double maxStep = 1;
    private double scalAbsoluteTolerance = 1.0e-10;
    private double scalRelativeTolerance = 1.0e-7;

    private FirstOrderIntegrator integrator = 
        new DormandPrince54Integrator(minStep, maxStep,
                                      scalAbsoluteTolerance,
                                      scalRelativeTolerance);

    private double initial_time = 0.0;
    private double timestep_length;
    private double[] init;
    private double[] result;
    private Map<String, Double> result_map = new HashMap<String, Double>();
    private Map<String, Double> init_map = new HashMap<String, Double>();

    // For integration of resource
    private int steps = 1000;
    private double step_size = 1.0/steps;
    private int step_idx = 0;
    private double[] resource_array = new double[steps+1];


    private FixedStepHandler fixedHandler;
    private StepHandler normalizer;

    public ConsumptionODE() {}

    public ConsumptionODE(Population pop) {
        this(pop, 1);
    }

    public ConsumptionODE(Population pop, double timestep_length) {
        this.pop = pop;
        this.capacity = pop.getCapacity();
        this.subpops = pop.getSubpopulations();
        this.n_subpops = this.subpops.size();
        this.n_states = n_subpops + 1;
        this.timestep_length = timestep_length;

        init = new double[n_states];

        fixedHandler = new FixedStepHandler() {
            @Override
            public void init(double t0, double[] y0, double t) {
                step_idx = 0;
            }

            @Override
            public void handleStep(double t, double[] y, double[] yDot,
                                   boolean isLast)
            {
                resource_array[step_idx++] = y[n_subpops];
            }
        };
        //System.out.println("step size: " + timestep_length/steps);
        normalizer = new StepNormalizer(timestep_length * step_size,
                                        fixedHandler,
                                        StepNormalizerBounds.BOTH);

        integrator.addStepHandler(normalizer);
    }

    @Override
    public int getDimension() { return n_states; }

    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) {
        double total_size = 0.0;
        for (int i=0; i<n_subpops; i++) { total_size += y[i]; }

        double S = y[y.length-1];
        double S_new = 0.0;
        double gr = 0.0;
        double release = 0.0;
        double consumption = 0.0;
        double scale = 1-(total_size/capacity);
        for (int i=0; i<n_subpops; i++) {
            Subpopulation subpop = subpops.get(i);
            gr = subpop.getFitnessCalculator().calculateGrowthRate(S);
            yDot[i] = y[i] * gr;
            S_new += y[i]*(subpop.getReleaseRate()*scale-gr*subpop.getGamma());
        }
        yDot[yDot.length-1] = S_new;
    }

    public void integrate(Population pop) {
        this.pop = pop;

        for (int i=0; i<n_subpops; i++) { 
            Subpopulation subpop = pop.getSubpopulations().get(i);
            init[i] = subpop.getSize();
        }
        init[n_subpops] = pop.getResource();
        result = Arrays.copyOf(init, init.length);

        integrator.integrate(this, initial_time, result, timestep_length,
                             result); 
    }

    public void makeResults() {
        init_map = new HashMap<String, Double>();
        result_map = new HashMap<String, Double>();
        for (int i=0; i<n_subpops; i++) {
            init_map.put(subpops.get(i).getId(), init[i]);
            result_map.put(subpops.get(i).getId(), result[i]);
        }
        init_map.put("resource", init[n_subpops]);
        result_map.put("resource", result[n_subpops]);
    }

    public void reportResults() {
        makeResults();
        System.out.println("init: " + init_map.toString());
        System.out.println("final: " + result_map.toString());
        System.out.println("timestep length: " + timestep_length);
    }

    public double calculateGrowthRate(String id) {
        // dN/dt = rN
        if (result_map.size() == 0) makeResults();
        double N = result_map.get(id);
        double dN_dt = (N-init_map.get(id))/timestep_length;
        return dN_dt/N;
    }

    public double integrateResource() {
        double sum = 0.0;
        for (int i=1; i<steps; i++) { 
            sum += resource_array[i];
        }
        sum += 0.5*(resource_array[0]+resource_array[steps]);
        return sum*step_size;
    }

    public double getFinalResource() {
        return result[n_subpops];
    }

    public double getInitialResource() {
        return init[n_subpops];
    }

    public void printResourceArray() {
        for (int i=0; i<steps; i++) {
            System.out.println("resource_array[" + i + "]: " + 
                               resource_array[i]);
        }
    }

    @Override
    public String toString() {
        return String.format("integrator=%s, minStep=%.2e, maxStep=%.2e, " +
                             "AbsTol=%.2e, RelTol=%.2e",
                             integrator.getClass().getSimpleName(),
                             minStep, maxStep, scalAbsoluteTolerance,
                             scalRelativeTolerance);
    }
}
