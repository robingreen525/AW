package org.fhcrc.honeycomb.metapop.lookuptable;

import java.util.Map;
import java.util.HashMap;

/** 
 * Makes keys out of current resource and population sizes.
 *
 * Created on 05 Aug, 2013
 * @author Adam Waite
 * @version $Rev: 2300 $, $Date: 2013-08-16 12:21:32 -0700 (Fri, 16 Aug 2013) $, $Author: ajwaite $
 *
 */
public class ConcatLookupTable implements LookupTable {
    private Map<String, Double> map;

    public ConcatLookupTable() { this(100); }

    public ConcatLookupTable(int initial_capacity) {
        map = new HashMap<String, Double>(initial_capacity);
    }

    public double getValue(double ... keys) { 
        String key = concat(keys);
        Double val = map.get(key);
        if (val == null) {
            return -1.0;
        } else {
            return val;
        }
    }

    public void setValue(double val, double ... keys) {
        String key = concat(keys);
        map.put(key, val);
    }

    private String concat(double ... args) {
        StringBuilder x = new StringBuilder();
        for (double arg:args) { x.append("-").append(Double.toString(arg)); }
        return x.toString();
    }

    public void print() {
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "\t" + entry.getValue());
        }
    }
}
