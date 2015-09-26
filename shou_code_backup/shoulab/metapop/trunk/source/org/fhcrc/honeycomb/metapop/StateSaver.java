package org.fhcrc.honeycomb.metapop;

import org.fhcrc.honeycomb.metapop.coordinate.Coordinate;
import org.fhcrc.honeycomb.metapop.coordinate.CoordinatePicker;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/** Saves the state of <code>World</code> objects.
 * Created on 28 Feb, 2012.
 @author Adam Waite
 @version $Rev: 1961 $, $Date: 2013-04-23 10:11:40 -0700 (Tue, 23 Apr 2013) $
 */
public class StateSaver {
    private final World world;
    private final File data_path;

    public StateSaver(World world) {
        this.world = world;
        this.data_path = world.getDataPath();
    }

    public void saveState() throws IOException 
    {
        if (!data_path.exists()) data_path.mkdirs();

        String file_name = Integer.toString(world.getStep()) + ".tab";
        File write_to = new File(data_path, file_name);
        BufferedWriter writer = null; 
        try {
            write_to.createNewFile();
            writer = new BufferedWriter(new FileWriter(write_to));
            writer.write(report());
        } catch (IOException e) {
            System.out.println("Couldn't write to file " + 
                               write_to.toString());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    /** writes a report about the current state of each 
     * <code>Population</code>. Assumes all populations are using the same
     * <code>FitnessCalculator</code>!
     *
     @return a string with a newline between each location, and tabs between 
     each piece of data
     */
    public String report() {
        List<Location> occupied_locations = world.getOccupiedLocations();

        StringBuilder data = new StringBuilder(1000);
        for (Location ol:occupied_locations) {
            Coordinate co  = ol.getCoordinate();
            Population pop = ol.getPopulation();

            String step = Long.toString(world.getStep());
            String env_changed = Integer.toString(world.envChanged() ? 1 : 0);
            String coops_extinct = 
                Integer.toString(world.coopsExtinct() ? 1 : 0);
            String cheats_extinct = 
                Integer.toString(world.cheatsExtinct() ? 1 : 0);
            String all_extinct = 
                Integer.toString(world.allExtinct() ? 1 : 0);
            String row = Integer.toString(co.getRow());
            String col = Integer.toString(co.getCol());

            String row_col =
                new StringBuilder(row).append("_").append(col).toString();

            data.append(step).append("\t").
                 append(env_changed).append("\t").
                 append(coops_extinct).append("\t").
                 append(cheats_extinct).append("\t").
                 append(all_extinct).append("\t").
                 append(row).append("\t").
                 append(col).append("\t").
                 append(row_col).append("\t");

            for (int type=0; type<pop.getNTypes(); type++) {
                data.append(pop.getCoops().get(type)).append("\t").
                     append(pop.getCheats().get(type)).append("\t");
            }
            data.append(pop.getNCoops()).append("\t").
                 append(pop.getNCheats());
            data.append("\n");
        }

        // ASSUMES ALL POPULATIONS ARE USING THE SAME FITNESS CALCULATOR!
        List<Location> initial_locs = world.getInitialLocations();
        Population pop = initial_locs.get(0).getPopulation();

        String mutant_freq =
            (pop.getCoopFrequencies().size() > 1) ?
                Double.toString(pop.getCoopFrequencies().get(1)) : "0";

        String info_line =
            String.format("#rows: %d; " +
                          "cols: %d; " +
                          "initial populations: %d; " +
                          "population seed: %d; " +
                          "cheat adv: %.2e; " +
                          "u: %.2e; " +
                          "migration rate: %.2e; " +
                          "mutation freq: %s; " +
                          "dilution frac: %.2e; " +
                          "max pop size: %.2e; " +
                          "fitness calc: %s; " +
                          "location picker: %s; " +
                          "migration picker: %s; " +
                          "env changer: %s " +
                          "\n"
                          ,
                          world.getNRows(),
                          world.getNCols(),
                          initial_locs.size(),
                          pop.getRNG().getSeed(),
                          pop.getCheatAdvantage(),
                          pop.getU(),
                          world.getMigrationRate(),
                          mutant_freq,
                          world.getDilutionFraction(),
                          world.getMaxPop(),
                          pop.getFitnessCalculator(),
                          world.getCoordinatePicker(),
                          world.getMigrationPicker(),
                          world.getEnvChanger()
                        );
                                        
        StringBuilder world_info = new StringBuilder(info_line);
        /*
        StringBuilder world_info = new StringBuilder(
                String.format(
                    "#rows: %d, cols: %d, initial populations: %d, " +
                    "population seed: %d, " +
                    "cheat adv: %.2e " +
                    "u: %.2e " +
                    "migration rate: %.2e, mutation freq: %.2e, " +
                    "dilution rate: %.2e, " + 
                    "environment change prob: %.2e, " +
                    "environment change seed: %d, " +
                    "max pop size: %.2e, fitness calc: %s, " +
                    "location picker: %s, migration picker: %s\n",
                    world.getNRows(), world.getNCols(),
                    initial_pops.size(),
                    pop.getRNG().getSeed(),
                    pop.getCheatAdvantage(),
                    pop.getU(),
                    world.getMigrationRate(),
                    pop.getCoopFrequencies().get(1),
                    world.getDilutionFraction(),
                    world.getEnvChanger().getProb(),
                    world.getEnvChanger().getRNG().getSeed(),
                    world.getMaxPop(),
                    pop.getFitnessCalculator(), world.getLocationPicker(), 
                    world.getMigrationPicker()));

                    */
        StringBuilder header_names =
            new StringBuilder("timestep\tenv.changed\tcoops.extinct\t" + 
                              "cheats.extinct\tall.extinct\t" +
                              "row\tcol\trow.col\t");
        for (int type=0; type<pop.getNTypes(); type++) {
            header_names.append("coop.type").append(type+1).append("\t").
                         append("cheat.type").append(type+1).append("\t");
        }
        header_names.append("coops\tcheats");
        header_names.append("\n");
        data = world_info.append(header_names).append(data);

        return data.toString();
    }
}
