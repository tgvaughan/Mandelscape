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
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Component which paints the Mandelbrot set as defined by the
 * chosen MandelModel.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class MandelPanel extends JPanel {

    private final MandelModel model;
    private MandelColourModel colourModel;
    private ColourModelChangeListener colourChangeListener;

    public MandelPanel(final MandelModel model, MandelColourModel colourModel) {
        this.model = model; 
        model.addChangeListener(new MandelModelChangeListener() {
            @Override
            public void modelHasChanged() {
                repaint();
            }
        });

        this.colourModel = colourModel;
        colourChangeListener = new ColourModelChangeListener() {
            @Override
            public void modelHasChanged() {
                repaint();
            }
        };
        this.colourModel.addChangeListener(colourChangeListener);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                model.setDimension(getWidth(), getHeight());
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation()==0)
                    return;

                Point point = e.getPoint();

                if (e.getWheelRotation()>0) {
                    // Scroll down (zoom out)

                    model.zoom(point.x, point.y, 0.8);
                } else {
                    // Scroll up (zoom in)

                    model.zoom(point.x, point.y, 1.2);
                }
            }
        });

        addMouseListener(new MouseAdapter() {

            boolean dragging = false;
            Point lastPoint; 

            @Override
            public void mouseClicked(MouseEvent e) {
                switch(e.getButton()) {
                    case MouseEvent.BUTTON1:
                        if (e.getClickCount()==1) {
                            // Single left click: Zoom in
                            Point point = e.getPoint();
                            model.zoom(point.x, point.y, 1.2);
                        } else {
                            // Double right click: Reset zoom
                            model.resetZoom();
                        }
                        break;

                    case MouseEvent.BUTTON3:
                        if (e.getClickCount() == 1) {
                            // Single left click: Zoom in
                            Point point = e.getPoint();
                            model.zoom(point.x, point.y, 0.8);
                        }
                        break;

                    default:
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            Point lastPoint;

            @Override
            public void mouseDragged(MouseEvent e) {
                Point thisPoint = e.getPoint();

                if (lastPoint != null)
                    model.pan(thisPoint.x - lastPoint.x,
                        thisPoint.y - lastPoint.y);

                lastPoint = thisPoint;
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                lastPoint = e.getPoint();
            }
            
        });
    }

    public void setColourModel(MandelColourModel colourModel) {
        this.colourModel.removeChangeListener(colourChangeListener);
        this.colourModel = colourModel;
        this.colourModel.addChangeListener(colourChangeListener);
        repaint();
    }

    public MandelColourModel getColourModel() {
        return this.colourModel;
    }

    public BufferedImage getImage() {
        return model.getImage(colourModel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(model.getImage(colourModel), 0, 0, null);
    }
}
