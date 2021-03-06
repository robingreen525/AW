package org.fhcrc.honeycomb.metapop;

import java.util.Map;
import java.util.List;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Saves the state of {@code Saveable} objects.
 * Created on 28 Apr, 2013.
 * @author Adam Waite
 * @version $Id: StateSaver.java 2007 2013-04-30 23:24:16Z ajwaite $
 */
public class StateSaver {
    private final Saveable saveable;
    private final File data_path;
    private final Map<String, String> initialization_data;
    private final String headers;

    public StateSaver(Saveable saveable) {
        this.saveable = saveable;
        this.data_path = saveable.getDataPath();
        this.headers = saveable.getHeaders();
        this.initialization_data = saveable.getInitializationData();

        try {
            writeInitializationData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeInitializationData() throws IOException {
        if (!data_path.exists()) data_path.mkdirs();
        String filename = initialization_data.get("filename");

        File write_to = new File(data_path, filename);
        BufferedWriter writer = null; 
        try {
            write_to.createNewFile();
            writer = new BufferedWriter(new FileWriter(write_to));
            writer.write(initialization_data.get("info"));
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

    public void saveState() throws IOException 
    {
        if (!data_path.exists()) data_path.mkdirs();

        String file_name = saveable.getFilename();
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

    public String report() {
        StringBuilder report = new StringBuilder();
        report.append(headers).append("\n").append(saveable.getData());
        return report.toString();
    }
}
