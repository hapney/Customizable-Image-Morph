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
import java.awt.*;

/*
 * Creates the Morph View Popup.
 */
public class MorphView extends JFrame {

    /*
     * Constructor for the Morph View.
     */
    public MorphView(ImageView imageView) {
        super("Morph");

        // Set Up Container
        Container c = getContentPane();
        c.setLayout(new FlowLayout());

        this.add(imageView);

        // Create Window
        setSize(650, 650);
        setResizable(false);
        setVisible(true);
    }
}
