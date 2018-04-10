/*
 * Author: Sydney Norman
 * Date: December 7, 2017
 * Project: Image Morph
 *
 * This Program allows a user to specify control points on a starting and ending
 * image and illustrate the morphing between the two images.
 *
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Handles the Menu Bar at the top of the screen.
 */
public class MenuBar extends JMenuBar {

    private final static String OPEN_START = "Open Start Image";
    private final static String OPEN_END = "Open End Image";
    private final static String EXIT = "Exit";
    private final static String OPTIONS = "Options";
    private final static String PROJECT = "Project";
    private final static String SAVE_IMAGES = "Save Images";
    private final static String RESET_CONTROL_POINTS = "Reset Control Points";

    private final static String HELP = "Help";
    private final static String HELP_TEXT = "************ HOW TO USE ************"
            + "\n\n"
            + "** Control Points **"
            + "\n" + "- Click and drag a control point to move it."
            + "\n" + "- As you click on a control point, the corresponding control point is highlighted."
            + "\n" + "- Match the control points to similar features to get a good morph."
            + "\n\n"
            + "** Group Control Point Move **"
            + "\n" + "- To move multiple control points at the same time, click on the \"Control Point"
            + "\n" + "      Group Move Button\" and draw a rectangle on the screen."
            + "\n" + "- Once your rectangle is drawn, click on one of the highlighted control points"
            + "\n" + "    to move the entire group."
            + "\n\n"
            + "** Changing the Images **"
            + "\n" + "- Change Image Intensity by using the slider below the image."
            + "\n" + "- Change the Start and End images by selecting Options on the Menu Bar at the top."
            + "\n\n"
            + "** Change Control Points **"
            + "\n" + "- Change the Control Points' color by selecting a color option between the two sliders."
            + "\n" + "- Change the row/column numbers using the boxes at the bottom left of the screen."
            + "\n" + "- Reset the control points by selecting Options > Reset Control Points."
            + "\n\n"
            + "** Morph **"
            + "\n" + "- Change the speed and preview time using the sliders at the bottom right of the screen."
            + "\n" + "- To preview the morph, select the \"Start Preview\" button."
            + "\n" + "- When you are ready to view the morph, select the \"Generate Morph\" button."
            + "\n" + "- The resulting images will be saved in your current working directory."
            + "\n\n"
            + "** Save Images **"
            + "\n" + "- To save the images you are working with, select Project > Save Images.";

    /*
     * Constructor for the MenuBar Class.
     *
     * @param       AL          The Action Listener for the Menu Bar Options
     */
    MenuBar(ActionListener AL) {

        // Build the Sub Menus
        JMenu projectMenu = new JMenu(PROJECT);
        JMenu optionsMenu = new JMenu(OPTIONS);
        JMenu helpMenu = new JMenu(HELP);

        // Add Save Project Menu Item
        JMenuItem saveProjectMenuItem = new JMenuItem(SAVE_IMAGES);
        saveProjectMenuItem.addActionListener(AL);
        projectMenu.add(saveProjectMenuItem);

        // Add Separator
        projectMenu.addSeparator();

        // Add Exit Menu Item
        JMenuItem exitMenuItem = new JMenuItem(EXIT);
        exitMenuItem.addActionListener(AL);
        projectMenu.add(exitMenuItem);

        // Add Open Start Menu Item
        JMenuItem openStartMenuItem = new JMenuItem(OPEN_START);
        openStartMenuItem.addActionListener(AL);
        optionsMenu.add(openStartMenuItem);

        // Add Open End Menu Item
        JMenuItem openEndMenuItem = new JMenuItem(OPEN_END);
        openEndMenuItem.addActionListener(AL);
        optionsMenu.add(openEndMenuItem);

        // Add Reset Control Points Menu Item
        JMenuItem resetControlPointsMenuItem = new JMenuItem(RESET_CONTROL_POINTS);
        resetControlPointsMenuItem.addActionListener(AL);
        optionsMenu.add(resetControlPointsMenuItem);

        JMenuItem helpMenuItem = new JMenuItem(HELP);
        helpMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuBar.this, HELP_TEXT);
            }
        });
        helpMenu.add(helpMenuItem);

        add(projectMenu);
        add(optionsMenu);
        add(helpMenu);
    }

}
