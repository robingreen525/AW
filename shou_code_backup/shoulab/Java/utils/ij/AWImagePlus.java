package org.fhcrc.honeycomb.utils.ij;

import ij.*;
import ij.io.*;
import ij.IJ;
import ij.process.*;

import java.util.*;

import org.fhcrc.honeycomb.mc.MCImage;

public class AWImagePlus extends ImagePlus {
  /**
   * Makes a stack out of a <code>List</code> of <code>MCImage</code>s.
   */
  public AWImagePlus(List<MCImage> list) {
    Opener opener = new Opener();
    int size = list.size();
    ImageStack stack = new ImageStack(list.get(0).getXDim(), 
                                      list.get(0).getYDim(),size);

    for (int i=0; i<size; i++) {
      MCImage mci = list.get(i);
      stack.setPixels(opener.openImage(mci.getImagePath().toString()).
                                           getProcessor().getPixels(),i+1);
    }
    setStack("stack",stack);
  }

  /**
   * Shows an image for the specified number of msec.  The image is updated
   * before display so that any recent changes will be visualized.
   * @param img the <code>ImagePlus</code> to show.
   * @param time the number of milliseconds to display the image.
   */
  public void show(int time) {
    updateAndDraw();
    show();
    IJ.wait(time);
    hide();
  }
}
