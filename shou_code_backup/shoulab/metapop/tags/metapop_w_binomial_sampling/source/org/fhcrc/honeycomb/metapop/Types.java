// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import java.util.List;

/** defines the different types present in a <class>Population</class>.
 * Created on 27 Jan, 2012
 * @author Adam Waite
 * @version $Rev: 1180 $, $Date: 2012-01-31 13:23:13 -0800 (Tue, 31 Jan 2012) $
 */
public class Types {
  private final int size;
  private final List<Double> fitnesses;
  private final List<Double> frequencies;

  public Types(List<Double> fitnesses, List<Double> freqs) {
    if (fitnesses.size() != freqs.size()) {
      throw new RuntimeException("fitnesses array length not equal to "+
                                 " frequency array length!");
    }

    checkFreqs(freqs);
    this.fitnesses   = fitnesses;
    this.frequencies = freqs;
    size = freqs.size();
  }

  public int size() { return size; }

  public double getFitness(int i)   { return fitnesses.get(i); }
  public double getFrequency(int i) { return frequencies.get(i); }

  public List<Double> getFitnesses() { return fitnesses; }
  public List<Double> getFrequencies() { return frequencies; }

  private void checkFreqs(List<Double> freqs) {
    double sum = 0.0;
    for (double freq:freqs) { sum += freq; }
    if (sum != 1) 
      throw new RuntimeException("Frequencies sum to " + sum);
  }
}
