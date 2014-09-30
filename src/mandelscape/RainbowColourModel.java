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
 * Simple colour model which gives a "rainbow" effect.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class RainbowColourModel extends MandelColourModel {

    public RainbowColourModel() {
        this(500, 0);
    }

    public RainbowColourModel(int period, int offset) {
        this.period = period;
        this.offset = offset;
    }


    @Override
    public String toString() {
        return "Rainbow";
    }

    @Override
    public Color iterToColor(int iter) {

        // Colour the mandelbrot set black
        if (iter<0)
            return Color.BLACK;

        double phase = (iter + offset)/(double)period  % 1.0;

        return Color.getHSBColor((float)phase, 1, 1);
    }

    
}
