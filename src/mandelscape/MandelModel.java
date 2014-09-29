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

import java.util.Random;

/**
 * Model for Mandelbrot set.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class MandelModel {
    private int maxIter;

    final private double cr0Min = -2.5;
    final private double cr0Max = 0.5;
    final private double ci0Min = -1.25;
    final private double ci0Max = 1.25;

    private double crMin, crMax, ciMin, ciMax;

    private int [] iters;
    private int width, height;

    public MandelModel(int maxIter, int width, int height) {
        this.maxIter = maxIter;

        this.width = width;
        this.height = height;
        this.iters = new int[width*height];

        this.crMin = cr0Min;
        this.crMax = cr0Max;
        this.ciMin = ci0Min;
        this.ciMax = ci0Max;
    }

    public void setMaxIter(int newMaxIter) {
        maxIter = newMaxIter;
    }

    public final void reset() {
        crMin = cr0Min;
        crMax = cr0Max;
        ciMin = ci0Min;
        ciMax = ci0Max;
    }

    public void setDimension(int width, int height) {
        this.width = width;
        this.height = height;
        iters = new int[width*height];
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

    public CDouble getPoint(int x, int y) {
        return new CDouble(crMin + (crMax-crMin)*x/((double)width),
            ciMin + (ciMax-ciMin)*y/((double)height));
    }

    public CDouble getPointJittered(int x, int y, double mag, Random random) {
        double dcr = (crMax-crMin)/((double)width);
        double dci = (ciMax-ciMin)/((double)height);

        return new CDouble(crMin + dcr*(x + mag*(random.nextDouble()-0.5)),
            ciMin + dci*(y + mag*(random.nextDouble()-0.5)));
    }

    public void update() {
        Random random = new Random();

        for (int x=0; x<width; x++) {
            for (int y=0; y<height; y++) {

                CDouble c = getPointJittered(x, y, 0.1, random);
                iters[x*height + y] = getEscapeIters(c);
            }
        }
    }
}
