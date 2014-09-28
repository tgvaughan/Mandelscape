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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.HeadlessException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class MandelscapeApp extends JFrame {

    public MandelscapeApp() throws HeadlessException {
        setTitle("MandelView - Mandelbrot Set Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();

        MandelPanel mandelPanel = new MandelPanel(5000, -2, 0.5, -1.25 , 1.25);
        cp.add(mandelPanel, BorderLayout.CENTER);
        mandelPanel.repaint();

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Maximum number of iterations: "));
        bottomPanel.add(new JSpinner(new SpinnerNumberModel(500, 100, 10000, 100)));
        bottomPanel.add(new JButton("Update"));
        cp.add(bottomPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(800, 800));
        pack();
    }

    public static void main(String[] args) {

        new MandelscapeApp().setVisible(true);

    }
    
}
