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

import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;

/**
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class MandelPanel extends JPanel {

    private final MandelModel model;
    private final MandelColourModel colourModel;

    public MandelPanel(final MandelModel model, MandelColourModel colourModel) {
        this.model = model; 
        this.colourModel = colourModel;
        model.addChangeListener(new MandelModelChangeListener() {
            @Override
            public void modelHasChanged() {
                repaint();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                model.setDimension(getWidth(), getHeight());
            }
        });

    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(model.getImage(colourModel), 0, 0, null);
    }
}
