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

/**
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class RainbowColourModel implements MandelColourModel {

    @Override
    public String toString() {
        return "Rainbow";
    }

    @Override
    public Color iterToColor(int iter, int maxIter) {

        // Colour the mandelbrot set black
        if (iter<0)
            return Color.BLACK;

        float relIter = iter/(float)maxIter;

        return Color.getHSBColor((float)(0.5 + (float)Math.pow(relIter,0.5) % 1.0), 1, 1-relIter);
    }
    
}
