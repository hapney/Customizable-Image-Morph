/*
 * Author: Sydney Norman
 * Date: December 7, 2017
 * Project: Image Morph
 *
 * This Program allows a user to specify control points on a starting and ending
 * image and illustrate the morphing between the two images.
 *
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
 * The JMorph Class.
 */
public class JMorph {

    // Model, View, and Controller
    private static Controller controller;
    private static View view;

    /*
     * Creates the JMorph UI.
     */
    public static void main(String arg[]) {

        // Initialize the Controller and View
        controller = new Controller();
        view = new View(controller);

        // Pass the View and Model to the Controller
        controller.setView(view);

        view.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }



}
