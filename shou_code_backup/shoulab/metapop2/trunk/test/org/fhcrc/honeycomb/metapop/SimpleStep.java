package org.fhcrc.honeycomb.metapop;

/**
 * A simple implementation of a {@code StepProvider} for testing.
 * Created on 26 Apr, 2013
 *
 * @version $Id: SimpleStep.java 2006 2013-04-30 02:19:59Z ajwaite $
 *
 */
public class SimpleStep implements StepProvider {
    private int step = 0;

    SimpleStep() {}

    @Override
    public int getStep() { return step; }

    @Override
    public int incrementStep() { return step++; }
}

