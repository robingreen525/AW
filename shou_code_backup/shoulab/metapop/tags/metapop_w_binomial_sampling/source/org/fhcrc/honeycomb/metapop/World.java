// vim: set filetype=java tabstop=2 shiftwidth=2 expandtab :
package org.fhcrc.honeycomb.metapop;

import java.util.Set;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/** A <code>World</code>.
 * Created on 26 Jan, 2012.
@author Adam Waite
@version $Rev: 1219 $, $Date: 2012-02-06 18:10:32 -0800 (Mon, 06 Feb 2012) $
 */
public class World {
  private final InitialLayout initial_layout;
  private final Population initial_pop_structure;
  private final Set<Location> initial_locations;
  private long step = 0;


  private static final long DEFAULT_SEED = System.currentTimeMillis();

  private Map<Location,Population> occupied_locations = 
    new HashMap<Location,Population>();

  /** Creates a <code>World</code>.
   *
  @param initial_layout an {@link InitialLayout} defining the number and
  initial positions of the {@link Population}s.
  @param initial_pop the initial population structure used to start the
  world.
  */
  public World(InitialLayout initial_layout, Population initial_pop)
  {
    this.initial_layout = initial_layout;
    initial_pop_structure = new Population(initial_pop);

    initial_locations = initial_layout.generateLocations();

    for (Location loc:initial_locations) {
      occupied_locations.put(new Location(loc),
                             new Population(initial_pop, 
                                            System.nanoTime()));
    }
  }

  /** distributes <code>Population</code>s.
   *
  @param migrants a <code>Population</code> of migrants to distribute
  @param new_location the <code>Location</code> to place the migrants
  */
  public void distribute(final List<Population> migrants, 
                         final List<Location> locations) 
  {
    if (locations.size() != migrants.size()) {
      throw 
        new IllegalArgumentException("location and population array must be same size");
    }

    for (int i=0; i<locations.size(); i++) {
      Location loc = locations.get(i);
      Population migrant = migrants.get(i);

      if (occupied_locations.containsKey(loc)) {
        occupied_locations.get(loc).mix(migrant);
      } else {
        occupied_locations.put(new Location(loc), new Population(migrant));
      }
      //System.out.println("now " + occupied_locations);
    }
  }

  /** returns the current step.
  @return the current step.
  */
  public long getStep() { return step; }

  /** increments the step. */
  public long incrementStep() { return ++step; }

  /** returns the number of rows in this <clas>World</class>.
  @return the number of rows.
   */
  public int getRows() { return initial_layout.getRows(); }

  /** returns the number of columns in this <clas>World</class>.
  @return the number of columns.
   */
  public int getCols() { return initial_layout.getCols(); }

  /** returns the locations that have populations, and their populations.
  @return the currently occupied locations and their populations.
   */
  public Map<Location,Population> getOccupiedLocations() {
    return occupied_locations;
  }

  public Population getInitialPopulations() { return initial_pop_structure; }

  public void saveState(String file) throws IOException { saveState(new File(file)); }

  public void saveState(File file) throws IOException {
    boolean append = true;
    boolean withHeaders = true;
    boolean withoutHeaders = false;
    BufferedWriter writer = null; 
    try {
      if (!file.exists()) {
        if (file.getParentFile() != null) {
          file.getParentFile().mkdirs();
        }
        file.createNewFile();
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(this.report(withHeaders));
      } else {
        writer = new BufferedWriter(new FileWriter(file, append));
        writer.write(this.report(withoutHeaders));
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      writer.close();
    }

  }

  /** writes a report about the current state of each 
   * <code>Population</code>.
   *
  @return a string with a newline between each location, and tabs between 
          each piece of data
  */
  public String report(boolean headers) {
    StringBuilder header_names =
      new StringBuilder("step\trow\tcol\trow.col\t"+
                        "coops\tcheats\n");
    StringBuilder data = new StringBuilder();
    for (Map.Entry<Location,Population> os:occupied_locations.entrySet()) {
      Location loc = os.getKey();
      Population pop = os.getValue();

      int row = loc.getRow(); int col = loc.getCol();
      data.append(step + "\t" + 
                  row + "\t" +
                  col + "\t" +
                  row + "_" + col + "\t" +
                  pop.getNCoops() + "\t" + 
                  pop.getNCheats());
      data.append("\n");
    }
    if (headers) data = header_names.append(data);
    return data.toString();
  }

  @Override
  public String toString() {
    return new String("A World with " + getRows() + " rows, " +
                      getCols() + " columns, and " + 
                      occupied_locations.size() + " Populations "+
                      "at positions\n"+ occupied_locations.keySet());
  }
}
