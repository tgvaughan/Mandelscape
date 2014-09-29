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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.JPanel;

/**
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class MandelPanel extends JPanel {

    private int maxIter;
    private double crMin, crMax, ciMin, ciMax;

    public MandelPanel(int maxIter,
        double crMin, double crMax, double ciMin, double ciMax) {
        this.crMin = crMin;
        this.crMax = crMax;
        this.ciMin = ciMin;
        this.ciMax = ciMax;
        this.maxIter = maxIter;
    }

    /**
     * Set maximum number of iterations to different value.
     * 
     * @param newMaxIter 
     */
    public void setMaxIter(int newMaxIter) {
        maxIter = newMaxIter;
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
    private int getEscapeIters(double cr, double ci) {

        double zr = 0.0;
        double zi = 0.0;

        for (int i=0; i<maxIter; i++) {

            // Update z:
            double zrPrime = zr*zr - zi*zi + cr;
            double ziPrime = 2.0*zr*zi + ci;
            zr = zrPrime;
            zi = ziPrime;

            // Check for boundary escape
            if (zr*zr + zi*zi > 10.0)
                return i;
        }

        // No boundary escape within chosen number of iterations.
        return -1;
    }

    /**
     * Method to convert escape iterations to a colour for painting.
     * @param iter
     * @return colour to be painted on panel.
     */
    private Color iterToColor(int iter) {

        // Colour the mandelbrot set black
        if (iter<0)
            return Color.BLACK;

        float relIter = iter/(float)maxIter;

        return Color.getHSBColor((float)(0.5 + (float)Math.pow(relIter,0.5) % 1.0), 1, 1-relIter);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Random random = new Random();

        double dcr = (crMax-crMin)/((double)getWidth());
        double dci = (ciMax-ciMin)/((double)getHeight());

        for (int x=0; x<getWidth(); x++) {
            for (int y=0; y<getHeight(); y++) {

               double cr = crMin + dcr*x + 0.1*dcr*(random.nextDouble()-0.5);
               double ci = ciMin + dci*y + 0.1*dcr*(random.nextDouble()-0.5);

                int iter = getEscapeIters(cr, ci);

                g.setColor(iterToColor(iter));
                g.drawLine(x, y, x, y); // hack to draw a single pixel
            }
        }
    }
}
