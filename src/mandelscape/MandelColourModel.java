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
import java.util.ArrayList;
import java.util.List;

/**
 * Class of objects that describe a mapping from iteration counts to colours.
 * These objects are parameterized by two numbers: a "period" and an "offset".
 * The period is the number of iterations required for the iteration to colour
 * mapping to return to the same colour. The offset adjust the particular colour
 * that a given iteration count will map to.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public abstract class MandelColourModel {

    int period, offset;

    private final List<ColourModelChangeListener> listeners =
        new ArrayList<ColourModelChangeListener>();

    @Override
    public abstract String toString();

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;

        fireModelChangedEvent();
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;

        fireModelChangedEvent();
    }

    public abstract Color iterToColor(int iter);
    
    /**
     * Add a listener for changes in the MandelModel.
     * 
     * @param listener 
     */
    public void addChangeListener(ColourModelChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener.
     * 
     * @param listener 
     */
    public void removeChangeListener(ColourModelChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Let any listeners know that the model has changed.
     */
    private void fireModelChangedEvent() {
        for (ColourModelChangeListener listener : listeners)
            listener.modelHasChanged();
    }
}
