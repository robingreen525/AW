package org.fhcrc.honeycomb.metapop.ode;

import org.fhcrc.honeycomb.metapop.Population;
import org.fhcrc.honeycomb.metapop.Subpopulation;
import org.fhcrc.honeycomb.metapop.fitness.FitnessCalculator;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;
import org.apache.commons.math3.ode.nonstiff.HighamHall54Integrator;
import org.apache.commons.math3.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.apache.commons.math3.ode.ContinuousOutputModel;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.ode.events.EventHandler;

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
 * @version $Id: ConsumptionODE.java 2245 2013-08-04 23:51:50Z ajwaite $
 *
 */
public class ConsumptionODE implements FirstOrderDifferentialEquations {
    private Population pop;
    private List<Subpopulation> subpops = new ArrayList<Subpopulation>();
    private int n_subpops;
    private int n_states;

    // Integrator
    private double minStep = 1.0e-20;
    private double maxStep = 1.0;
    private double scalAbsoluteTolerance = 1.0e-6;
    private double scalRelativeTolerance = 1.0e-3;

    // Event handler
    private double maxCheckInterval = 1e-5;
    private double convergence = 1e-8;
    private int maxIterationCount = 1000;

    private ContinuousOutputModel com = new ContinuousOutputModel();
    private double initial_time = 0.0;
    private double timestep_length; // full timestep length
    private double integration_end; // at what time integration ended.
    private double[] init;
    private double[] result;
    private Map<String, Double> result_map;
    private Map<String, Double> init_map;

    private FirstOrderIntegrator integrator = 
        //new GraggBulirschStoerIntegrator(
        new DormandPrince54Integrator(
        //new HighamHall54Integrator(
        //new DormandPrince853Integrator(
                                       minStep, maxStep,
                                       scalAbsoluteTolerance,
                                       scalRelativeTolerance);

    private StepHandler stepHandler = new StepHandler() {
        public void init(double t0, double[] y0, double t) {}

        public void handleStep(StepInterpolator interpolator, boolean isLast) {
            double t = interpolator.getCurrentTime();
            double[] y = interpolator.getInterpolatedState();

            for (int i=0; i<y.length; i++) {
                System.out.println("step: " + t + ": y[" + i + "] = " + y[i]);
            }
        }
    };

    private EventHandler eventHandler = new EventHandler() {
        public void init(double t0, double[] y0, double t) {}

        public double g(double t, double[] y) {
            return y[n_states-1] - 0.0;
        }

        public EventHandler.Action eventOccurred(double t, double[] y,
                                                 boolean increasing)
        {
            return EventHandler.Action.STOP;
        }

        public void resetState(double t, double[] y) {}
    };

    public ConsumptionODE() {}

    public ConsumptionODE(Population pop) {
        this.pop = pop;
        this.subpops = pop.getSubpopulations();
        this.n_subpops = this.subpops.size();
        this.n_states = n_subpops + 1;
    }

    public int getDimension() {
        return n_states;
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) {
        double S = y[y.length-1];

        double S_new = 0.0;
        for (int i=0; i<n_subpops; i++) {
            Subpopulation subpop = subpops.get(i);
            FitnessCalculator fc = subpop.getFitnessCalculator();
            double gr = fc.calculateGrowthRate(S);
            double S_net = subpop.getReleaseRate() - gr*subpop.getGamma();

            yDot[i] = y[i] * gr;
            S_new += y[i] * S_net;
        }
        yDot[yDot.length-1] = S_new;
    }

    public void integrate(double timestep_length) {
        this.timestep_length = timestep_length;

        initialize();

        integrator.addStepHandler(com);
        //integrator.addStepHandler(stepHandler);
        integrator.addEventHandler(eventHandler, maxCheckInterval, convergence,
                                   maxIterationCount);

        integrator.integrate(this, initial_time, result, timestep_length,
                             result); 
        integration_end = com.getFinalTime();
        make_results();

        //System.out.println("init: " + init_map.toString());
        //System.out.println("final: " + result_map.toString());
        //System.out.println("integration ended: " + integration_end);
    }

    private void initialize() {
        double initial_resource = pop.getResource();
        init = new double[n_states];
        init_map = new HashMap<String, Double>();
        for (int i=0; i<n_subpops; i++) { 
            Subpopulation subpop = subpops.get(i);
            double size = subpop.getSize();
            init_map.put(subpop.getId(), size);
            init[i] = size;
        }
        init_map.put("resource", initial_resource);
        init[n_subpops] = initial_resource;

        result = Arrays.copyOf(init, init.length);
    }

    private void make_results() {
        result_map = new HashMap<String, Double>();
        for (int i=0; i<n_subpops; i++) {
            result_map.put(subpops.get(i).getId(), result[i]);
        }
        result_map.put("resource", result[result.length-1]);
    }

    public double calculateGrowthRate(String id) {
        // dN/dt = rN
        double dN_dt = (result_map.get(id) - init_map.get(id))/
                       (integration_end - initial_time);
        return dN_dt / result_map.get(id);
    }

    public double getAverageResource() {
        double S_ave = 0.5 * (init_map.get("resource") +
                              result_map.get("resource"));
        return Math.abs(S_ave * (integration_end-initial_time) /
                                (timestep_length-initial_time));
    }

    public Map<String, Double> getResult() {
        return result_map;
    }

    @Override
    public String toString() {
        return String.format("integrator=%s, minStep=%.2e, maxStep=%.2e, " +
                             "AbsTol=%.2e, RelTol=%.2e, maxCheck=%.2e, " +
                             "convergence=%.2e, maxIter=%d\n",
                             integrator.getClass().getSimpleName(),
                             minStep, maxStep, scalAbsoluteTolerance,
                             scalRelativeTolerance,
                             maxCheckInterval, convergence,
                             maxIterationCount);
    }
}
