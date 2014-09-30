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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Very basic Mandelbrot set exploration application.
 *
 * @author Tim Vaughan <tgvaughan@gmail.com>
 */
public class MandelscapeApp extends JFrame {

    private final MandelPanel mandelPanel;

    public MandelscapeApp() {
        setTitle("MandelView - Mandelbrot Set Viewer");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container cp = getContentPane();

        // Set up main viewer model and panel:

        final MandelModel model = new MandelModel(500, 800, 800);
        Object[] colourModels = {new RainbowColourModel(), new IceColourModel() };
        MandelColourModel colourModel = (MandelColourModel)colourModels[0];

        mandelPanel = new MandelPanel(model, colourModel);
        cp.add(mandelPanel, BorderLayout.CENTER);


        // Set up components along bottom:

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(new JLabel("Col. model:"));
        JComboBox colourModelComboBox = new JComboBox(colourModels);
        colourModelComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                mandelPanel.setColourModel((MandelColourModel)cb.getSelectedItem());
            }
        });
        bottomPanel.add(colourModelComboBox);

        bottomPanel.add(new JLabel("Max iter: "));
        JSpinner iterSpinner = new JSpinner(new SpinnerNumberModel(500, 100, 10000, 100));
        iterSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner spinnerObj = (JSpinner)e.getSource();
                model.setMaxIter((Integer)spinnerObj.getValue());
            }
        });
        bottomPanel.add(iterSpinner);

        JButton zoomResetButton = new JButton("Reset Zoom");
        zoomResetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.resetZoom();
            }
        });
        bottomPanel.add(zoomResetButton);

        cp.add(bottomPanel, BorderLayout.SOUTH);


        // Set up menu bar:

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenuItem fileSaveMenuItem = new JMenuItem("Save image", KeyEvent.VK_S);
        fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        fileSaveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                fc.setFileFilter(new FileNameExtensionFilter("PNG image file", "png"));
                fc.setSelectedFile(new File("mandel.png"));
                if (fc.showSaveDialog(mandelPanel) == JFileChooser.APPROVE_OPTION) {
                    try {
                        BufferedImage image = mandelPanel.getImage();
                        File output = fc.getSelectedFile();
                        ImageIO.write(image, "png", output);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(getParent(),
                            "Error writing file.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        fileMenu.add(fileSaveMenuItem);

        fileMenu.addSeparator();

        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        fileExitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(mandelPanel,
                    "Really exit from Mandelscape?",
                    "Please confirm",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

                    System.exit(0);
                }
            }
        });
        fileMenu.add(fileExitMenuItem);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(helpMenu);

        JMenuItem helpControlsMenuItem = new JMenuItem("Controls...", KeyEvent.VK_C);
        helpControlsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mandelPanel,
                    "<html>"
                        + "<table style='font-weight: normal; text-align: left;'>"
                        + "<tr style='border-bottom: 1px solid black; font-weight: bold;'>"
                        + "<th><b>Mouse Action</b></th><th><b>Result</b></th></tr>"
                        + "<tr><td>single left click</td><td>zoom in</td></tr>"
                        + "<tr>single right click</td><td>zoom out</td></tr>"
                        + "<tr>scroll wheel</td><td>zoom in/out</td></tr>"
                        + "<tr>left click and drag</td><td>pan view</td></tr>"
                        + "<tr>double left click</td><td>reset zoom</td></tr>"
                        + "</table>"
                        + "</html>",
                    "Mandelscape Controls", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(helpControlsMenuItem);

        JMenuItem helpAboutMenuItem = new JMenuItem("About", KeyEvent.VK_A);
        helpAboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mandelPanel,
                    "<html>"
                        + "<body style='font-weight:normal;'"
                        + "<h2>Mandelscape 1.0</h2>"
                        + "Very simple application that can be used to explore <br>"
                        + "the Mandelbrot set</a>.<br>"
                        + "<br>"
                        + "Distributed under version 3.0 of the GNU General Public License."
                        + "</body>"
                        + "</html>",
                    "About Mandelscape", JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon(getClass().getClassLoader().getResource("mandelscape/resources/logo.png")));
                
            }
        });
        helpMenu.add(helpAboutMenuItem);


        // Set initial dimension and pack:

        setPreferredSize(new Dimension(800, 800));
        pack();
    }

    public static void main(String[] args) {

        new MandelscapeApp().setVisible(true);

    }
    
}
