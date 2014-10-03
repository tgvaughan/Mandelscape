/*
 * Copyright (C) 2014 Tim Vaughan <tgvaughan@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mandelscape;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

/**
 * Model for the Mandelbrot set.  Contains the computed escape iteration
 * counts for a chosen region of the set, and allows zooming of this region.
 * Provides methods for obtaining a rendered image of this region under
 * a chosen colour model.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class MandelModel {
    private int maxIter;
    private final List<MandelModelChangeListener> listeners =
        new ArrayList<MandelModelChangeListener>();

    final private double cr0Min = -2.5;
    final private double cr0Max = 0.5;
    final private double ci0Min = -1.25;
    final private double ci0Max = 1.25;

    private double crMin, crMax, ciMin, ciMax;

    private int [] iters;
    private int width, height;

    private final List<SwingWorker> workers = new ArrayList<SwingWorker>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);
    private int workerGridSide;
    

    /**
     * Create a new MandelModel with the specified initial maximum iteration
     * count, width and height.
     * 
     * @param maxIter
     * @param width
     * @param height 
     * @param workerGridSide Controls number of SwingWorkers to use in calculation.
     */
    public MandelModel(int maxIter, int width, int height, int workerGridSide) {
        this.maxIter = maxIter;

        this.width = width;
        this.height = height;
        this.iters = new int[width*height];

        this.workerGridSide = workerGridSide;

        this.crMin = cr0Min;
        this.crMax = cr0Max;
        this.ciMin = ci0Min;
        this.ciMax = ci0Max;
    }

    /**
     * Add a listener for changes in the MandelModel.
     * 
     * @param listener 
     */
    public void addChangeListener(MandelModelChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a model change listener.
     * 
     * @param listener 
     */
    public void removeChangeListener(MandelModelChangeListener listener) {
        listeners.remove(listener);
    }
    /**
     * Let any listeners know that the model has changed.
     */
    private void fireModelChangedEvent() {
        for (MandelModelChangeListener listener : listeners)
            listener.modelHasChanged();
    }

    /**
     * Set the maximum number of iterations to perform when estimating
     * boundary escape rate.
     * 
     * @param newMaxIter 
     */
    public void setMaxIter(int newMaxIter) {
        maxIter = newMaxIter;

        update();
    }

    /**
     * Reset zoom to default.
     */
    public void resetZoom() {
        crMin = cr0Min;
        crMax = cr0Max;
        ciMin = ci0Min;
        ciMax = ci0Max;

        update();
    }

    /**
     * Zoom in/out, keeping the complex number associated with the pixel
     * specified by centrex and centrey the same.
     *
     * @param centrex
     * @param centrey
     * @param factor zoom factor: &lt;1 zooms out, &gt;1 zooms in.
     */
    public void zoom(int centrex, int centrey, double factor) {
        zoom(getPoint(centrex, centrey), factor);
    }

    /**
     * Zoom in/out, keeping the specified complex number "centre" at the same
     * relative distance to the region boundaries.
     * 
     * @param centre
     * @param factor zoom factor: &lt;1 zooms out, &gt;1 zooms in.
     */
    public void zoom(CDouble centre, double factor) {
        double crMinPrime = crMin/factor - centre.real*(1.0/factor-1);
        double crMaxPrime = crMinPrime + (crMax-crMin)/factor;
        double ciMinPrime = ciMin/factor - centre.imag*(1.0/factor-1);
        double ciMaxPrime = ciMinPrime + (ciMax-ciMin)/factor;

        crMin = crMinPrime;
        crMax = crMaxPrime;
        ciMin = ciMinPrime;
        ciMax = ciMaxPrime;

        update();
    }

    /**
     * Shift view by chosen offset.
     * 
     * @param dx horizontal pixel offset
     * @param dy vertical pixel offset
     */
    public void pan(int dx, int dy) {
        double crMinPrime = crMin - dx*(crMax-crMin)/width;
        double crMaxPrime = crMax - dx*(crMax-crMin)/width;
        double ciMinPrime = ciMin - dy*(ciMax-ciMin)/height;
        double ciMaxPrime = ciMax - dy*(ciMax-ciMin)/height;

        crMin = crMinPrime;
        crMax = crMaxPrime;
        ciMin = ciMinPrime;
        ciMax = ciMaxPrime;

        update();
    }


    /**
     * Set the dimension of the pixel grid.
     * 
     * @param width
     * @param height 
     */
    public void setDimension(int width, int height) {

        // Cancel workers and wait for them to finish up:
        for (SwingWorker worker : workers) {
            worker.cancel(true);
        }

        while (workersActive>0) {
            // Do nothing;
        }

        this.width = width;
        this.height = height;
        iters = new int[width*height];

        update();
    }

    public void setWorkerGridSide(int workerGridSide) {
        this.workerGridSide = workerGridSide;

        update();
    }

    /**
     * Iterate z_{n+1} = z_{n}^2 + c to determine whether complex number c
     * is in the Mandelbrot set or not.  (Values of c which remain bounded
     * after an infinite number of iterations are in the set, all others
     * are not.  We use a finite number of iterations to approximately
     * determine membership.)
     * 
     * This function returns the number of iterations taken to escape the
     * boundary |z|=1, or -1 if z remained bounded for maxIter iterations.
     * 
     * @param cr
     * @param ci
     * @return 
     */
    private int getEscapeIters(CDouble c) {

        CDouble z = CDouble.ZERO;

        for (int i=0; i<maxIter; i++) {

            // Update z:
            z = z.squared().add(c);

            // Check for boundary escape
            if (z.abs2() > 10.0)
                return i;
        }

        // No boundary escape within chosen number of iterations.
        return -1;
    }

    /**
     * Get complex number associated with pixel grid coordinates (x,y).
     * 
     * @param x
     * @param y
     * @return complex number
     */
    public CDouble getPoint(int x, int y) {
        return new CDouble(crMin + x*(crMax-crMin)/width,
            ciMin + y*(ciMax-ciMin)/height);
    }

    /**
     * Get complex number associated with pixel grid coordinates (x,y), but
     * with a random jitter to avoid aliasing effects.
     * 
     * @param x
     * @param y
     * @param mag magnitude of jitter
     * @return complex number
     */
    public CDouble getPointJittered(int x, int y, double mag) {
        double dcr = (crMax-crMin)/((double)width);
        double dci = (ciMax-ciMin)/((double)height);

        return new CDouble(crMin + dcr*(x + mag*(Math.random()-0.5)),
            ciMin + dci*(y + mag*(Math.random()-0.5)));
    }

    /**
     * Construct BufferedImage view of Mandelbrot set using chosen colour model.
     *
     * @param colourModel
     * @return Image for painting on screen or writing to disk.
     */
    public BufferedImage getImage(MandelColourModel colourModel) {
        BufferedImage image = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_RGB);

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {
                image.setRGB(x, y, colourModel.iterToColor(iters[x*height + y]).getRGB());
            }
        }

        return image;
    }

    int workersActive;
    public synchronized void decrementActiveWorkerCount() {
        workersActive -= 1;
    }

    /**
     * Compute boundary escape iteration counts for each pixel in region.
     */
    public void update() {

        for (SwingWorker worker : workers) {
            worker.cancel(true);
        }

        while (workersActive>0) {
            // Do nothing;
        }

        workers.clear();

        for (int i=0; i<workerGridSide; i++) {
            for (int j=0; j<workerGridSide; j++) {

                final int xmin = i*width/workerGridSide;
                final int xmax = (i+1)*width/workerGridSide;
                final int ymin = j*height/workerGridSide;
                final int ymax = (j+1)*height/workerGridSide;

                workers.add(new SwingWorker<Void,Void>() {
                    
                    @Override
                    protected Void doInBackground() throws Exception {
                        for (int bsize=64; bsize>0; bsize/=2) {
                            for (int x=xmin; x<xmax; x+=bsize) {
                                for (int y=ymin; y<ymax; y+=bsize) {
                                    
                                    if (isCancelled())
                                        return null;
                                    
                                    CDouble c = getPointJittered(x, y, 0.1);
                                    int escapeIters;
                                    if (bsize<64 && x % (bsize*2) == 0 && y % (bsize*2) == 0)
                                        escapeIters = iters[x*height + y];
                                    else {
                                        escapeIters = getEscapeIters(c);
                                        iters[x*height + y] = escapeIters;
                                    }
                                    
                                    for (int xb=x; xb<x+bsize && xb<width; xb++) {
                                        for (int yb=y; yb<y+bsize && yb<height; yb++) {
                                            if (xb == x && yb==y)
                                                continue;
                                            
                                            iters[xb*height + yb] = escapeIters;
                                        }
                                    }
                                }
                            }
                            
                            publish((Void) null);
                        }
                        
                        return null;
                    }
                    
                    @Override
                    protected void process(List<Void> chunks) {
                        fireModelChangedEvent();
                    }
                    
                    @Override
                    protected void done() {
                        fireModelChangedEvent();
                        decrementActiveWorkerCount();
                    }
                });

                threadPool.submit(workers.get(workers.size()-1));
            }
        }
    }
}
