package org.fhcrc.honeycomb.metapop;

import java.io.File;
import java.util.Map;
import java.util.List;

/**
 * Saves the state of {@code Saveable} objects.
 * Created on 28 Apr, 2013.
 * @author Adam Waite
 * @version $Id: Saveable.java 2145 2013-06-19 21:15:54Z ajwaite $
 */
public interface Saveable {

    /** the save path */
    public File getDataPath();

    /** the filename */
    public String getFilename();

    /** the headers.  The order is how the data should be written.*/
    public String getHeaders();

    /** 
     * how this {@code Saveable} was set up. Should have entry for 'filename'
     * and one for 'info'.
     * */
    public Map<String, String> getInitializationData();

    /** data information. */
    public String getData();
}
