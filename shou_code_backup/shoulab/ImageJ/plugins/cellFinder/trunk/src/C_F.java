import java.awt.*;
import java.awt.event.*;
import java.io.*;
import ij.plugin.*;
import ij.plugin.filter.*;
import ij.plugin.filter.ParticleAnalyzer;
import ij.io.*;
import ij.measure.ResultsTable;
import ij.plugin.frame.RoiManager;
import ij.measure.*;

import ij.*;
import ij.process.*;
import ij.gui.*;



/*
 * Run this plugin on an open stack of bright field images.  The plugin
 * creates a 'selections' folder, containing the selections for each
 * slice and a tiff stack of the segmented images called 'segmented.tif'.
 * To be compatible with the YeastTracker plugin, 'X' and 'Y' columns
 * are printed first, followed by other measurements.  The results are
 * written to 'results.txt'.
 */


public class C_F implements PlugIn {
	// Some class constants
	private final int IMAGE_WIDTH = 696;
	private final int START_PIXEL = 145;
	private final int END_PIXEL = 440;
	private final int FILL_COLOR = 4450;
	
	// Private fields
	private ImagePlus imp;
	// Stack of ImagePlus objects that need to be processed.
	private String path;
	private ImageStack stack;
	private String FS;
	private NFPath nfPath;

	/*
	 * This method is part of the PlugInFilter interface
	 */
	public void run(String arg) {
		// Make sure that args is an actual filepath
		if(arg.length() > 1){
			nfPath = new NFPath(new File(arg));
		} else{
      nfPath = new NFPath(new File(IJ.getDirectory()));
		}

		// Fill the stack
		fillStack();
		
		// Store the file separator (System dependent) 
		FS = File.separator;

		// TESTING FLAG, to make sure that the process is running
		IJ.beep();
		
		// Store the originalID of the ImagePlus object
		int orig_id = imp.getID();
		IJ.selectWindow(orig_id);
						
		// Run the Set Measurements command through the IJ class
		// This could hypothetically be done through the Analyzer
		// class as well
		IJ.run("Set Measurements...", " redirect=None decimal=5");
				
		// Store the path to the image directory
		path = nfPath.projectPath().toString() + FS;
		
		// Store the path to the file results.txt
		String results = path + "results.txt";
		
		// Store the path to the file segmented.tif
		String segmented = path + "segmented.tif";
		
		// Make a file object to the stated directory
		File selection_path = new File(path + "selections" + FS);
		
		// Make the directory stated above, if it does not already exist
		if(!selection_path.mkdir()){/*Do nothing*/}
		
		/******** DEBUGGING CODE**************/
		//IJ.showMessage("Path is " + path + '\n' + "Results is " + results + '\n' + "Original ID is " + orig_id);
		/******** DEBUGGING CODE**************/
		
		// This sets the threshold for "Find Maxima..." and is probably the
   	// most important number in determining how well this works.
		int threshold = 50;
		preprocess_bf(orig_id);

		/******** DEBUGGING CODE**************/		
		//IJ.showMessage("Attempting to preprocess (blur) original image");
		/******** DEBUGGING CODE**************/
				
		IJ.selectWindow(orig_id);
		
		/******** DEBUGGING CODE**************/
		//IJ.showMessage("Finished preprocessing!! Proc_id is " + proc_id);
		/******** DEBUGGING CODE**************/
		
		segmentStack(threshold);
		
		IJ.selectWindow(orig_id);
		try{
			RoiManager m = RoiManager.getInstance();
			ResultsTable t = new ResultsTable();
			PrintStream output = new PrintStream(new File(results));
			output.println("X" + '\t' + "Y" + '\t' + "Area" + '\t' + "Major" + '\t' + "Minor" + '\t' + "Round" + '\t' + "Slice");
			int start_index = 0;
			for(int i = 1; i <= stack.getSize(); i++){ // max is stack_size	
				imp.setSlice(i);
				m.runCommand("Open", (path+ "selections" +FS + i+".zip"));
				m.runCommand("Deselect");
				m.runCommand("Measure");
				t = ResultsTable.getResultsTable();
				int nRoi = m.getCount();
				for(int j = start_index; j < start_index + nRoi; j++){
					if(t.getValueAsDouble(ResultsTable.STD_DEV, j) > 100){
						output.print(t.getValueAsDouble(ResultsTable.X_CENTROID, j));
						output.print('\t');
						output.print(t.getValueAsDouble(ResultsTable.Y_CENTROID, j));
						output.print('\t');
						output.print(t.getValueAsDouble(ResultsTable.AREA, j));
						output.print('\t');
						output.print(t.getValueAsDouble(ResultsTable.MAJOR, j));
						output.print('\t');
						output.print(t.getValueAsDouble(ResultsTable.MINOR, j ));
						output.print('\t');
						output.print(t.getValueAsDouble(ResultsTable.ROUNDNESS, j));
						output.print('\t');
						output.print((int) t.getValueAsDouble(ResultsTable.SLICE, j));
						output.println();
					}	
				}
				m.runCommand("Select All");
				m.runCommand("Delete");
				start_index += nRoi;
			}
		} catch (FileNotFoundException f){}
		
	}
		
	public void segmentStack(int threshold){
		// Dimensions is of the form: WIDTH, HEIGHT, nCHANNELS, nSLICES, nFRAMES
		int [] dimensions = imp.getDimensions();
		ImageStack p_seg = new ImageStack(dimensions[0], dimensions[1]);
				
		int stack_size = stack.getSize();
		RoiManager m = new RoiManager();
		ResultsTable r = new ResultsTable();
		Analyzer a = new Analyzer();
		MaximumFinder mf = new MaximumFinder();
		a.setMeasurements(Measurements.AREA+Measurements.CENTROID+Measurements.ELLIPSE+Measurements.STD_DEV+Measurements.SHAPE_DESCRIPTORS+Measurements.SLICE);
		for(int i = 1; i <= stack_size; i ++){ // max is stack_size
			imp.setSlice(i);
			ByteProcessor b = mf.findMaxima(imp.getProcessor(), threshold, threshold, MaximumFinder.SEGMENTED, false, false);			
			b.invert();
			analyzeParticles(i, b, m ,r);
			p_seg.addSlice(i+"", b);		
		}
		
		ImagePlus segmented = new ImagePlus("segmented", p_seg);
		segmented.show();
		IJ.selectWindow(segmented.getID());
		// Save the segmented tif files
		IJ.save(path + "segmented.tif");
	}
	
	// This method preprocess each of the bright field images
	public void preprocess_bf(int id){
		GaussianBlur gaussian_blur = new GaussianBlur();
		// Perform the Gaussian blur on all of the images in the stack
		for(int i = 1; i <= stack.getSize(); i ++){
			gaussian_blur.blur(stack.getProcessor(i), 5.0);
		}
	}
	
	public void  analyzeParticles(int i, ByteProcessor b, RoiManager m, ResultsTable r ){
		ImagePlus frame = new ImagePlus("blank", b);		
		ParticleAnalyzer p = new ParticleAnalyzer(ParticleAnalyzer.SHOW_NONE | ParticleAnalyzer.EXCLUDE_EDGE_PARTICLES | ParticleAnalyzer.INCLUDE_HOLES | ParticleAnalyzer.CLEAR_WORKSHEET | ParticleAnalyzer.ADD_TO_MANAGER, 0, r , 0, 500, .5, 1.00);
		p.analyze(frame);
		// Save the zip file containing the rois
		m.runCommand("Save", path+ "selections" +FS + i+".zip");
		m.runCommand("Select All");
		m.runCommand("Delete");
	}
	
	// This method fills up an ImageStack with the pictures of cells and then store them as an image
	// (or rather as an image that consists of several slices/photos)
	public void fillStack(){
		// Store the images in a File[]
		File[] images = nfPath.getFilesWavelength("WL1");
		
		// Get a single bright field image open so that we can look at its dimensions
		imp = new ImagePlus(images[0].toString());
		
		// Initialize the stack properly
		stack = new ImageStack(imp.getWidth(), imp.getHeight());
		
		// Fill the stack with ImagePlus objects
		for(int i = 0; i < images.length; i ++){
			ImagePlus j = new ImagePlus(images[i].toString());
			j.getProcessor().setRoi(0, START_PIXEL, IMAGE_WIDTH, END_PIXEL-START_PIXEL);
			ImageProcessor b = j.getProcessor().crop();
			b.setValue(FILL_COLOR);
			b.fillOval(30,262, 70, 70);
			b.fillOval(260,250, 65, 70);
			b.fillOval(495,250, 65, 70);
			stack.addSlice(i+"", b);
		}
		
		StackProcessor s = new StackProcessor(stack, null);
		stack = s.crop(0, START_PIXEL, IMAGE_WIDTH, END_PIXEL-START_PIXEL);
				
		// Put the stack into a image to be shown
		imp = new ImagePlus("inputStack", stack);
		imp.show();
				
		// We can save the image at this point,
		// if necessary.
	}
}
