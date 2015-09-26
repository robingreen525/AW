package org.fhcrc.honeycomb.metapop;

import static org.junit.Assert.*;

/** 
 * Reports result of tests with random variable elements.
 *
 * Created on 25 Apr, 2013
 * @author Adam Waite
 * @version $Id: FailReport.java 2019 2013-05-08 17:40:29Z ajwaite $
 *
 */
public class FailReport {
    public static void report(String tag, int failed, int tests,
                              Estimate expected)
    {
        double allowed = expected.getCI()[1];
        String fail_fraction = failed + "/" + tests;
        System.out.println(tag + ": " + fail_fraction + " failed");
        assertTrue("too many failures (" + fail_fraction +
                   ", " + allowed + " allowed)", failed <= allowed);
    }
}
