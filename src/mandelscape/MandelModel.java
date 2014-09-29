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

    public void update() {

    }
}
