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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/*
 * Controller for the Program.
 */
public class Controller
    implements ChangeListener, ItemListener, ActionListener {

    private View view;

    // Frame Info
    private static int frameCount;

    // Timer
    private Timer timer;
    private int time;

    private boolean isMorphRunning = false;

    /*
     * Constructor for the Controller Class.
     */
    public Controller() {

        // Initialize the Game Time
        time = 0;

        // Initialize Frame Info
        frameCount = 0;

        // Set Up the Timer
        timer = new Timer(1, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Every 1000 is a second

                int frames = view.getConsoleView().getFramesSlider().getValue();
                int seconds = view.getConsoleView().getSecondSlider().getValue();

                // Set Frame
                if (time % (1000 / frames) == 0) {
                    frameCount++;

                    if (isMorphRunning) {
                        //view.setFrame(frameCount, frames * seconds, isMorphRunning);
                        view.showMorphFrame(frameCount, frames * seconds);
                        //view.setFrame(frameCount, frames * seconds, true);
                    }
                    else {
                        view.setPreviewFrame(frameCount, frames * seconds);

                    }
                }

                // Check if Preview is Completed
                if (frameCount == frames * seconds) {
                    view.getConsoleView().endedPreviewButtons();
                    timer.stop();
                }

                // Increase the game time.
                time++;

            }
        });

    }

    /*
     * Passes the View to the Controller.
     *
     * @param   view        The Program's View
     */
    public void setView(View view) {
        this.view = view;
    }

    /*
     * Resets the preview and control point colors and location
     */
    private void reset() {

        // Reset Control Point Colors and Locations
        view.getStartImage().resetControlPoints();
        view.getEndImage().resetControlPoints();

        // Reset Preview
        resetPreview();
    }

    /*
     * Start the Preview.
     */
    private void startPreview() {

        // Reset the Control Point Colors
        view.getStartImage().resetControlPointsColor();
        view.getEndImage().resetControlPointsColor();

        // Start the Timer
        timer.start();

        // Disable Components
        view.getConsoleView().disableComponents();

        // Disable Start Button
        view.getConsoleView().startPreviewButtons();

        // Disable the Control Points
        view.disableControlPoints();
    }

    /*
     * Stop the Preview.
     */
    private void stopPreview() {
        timer.stop();

        // Enable Start Button
        view.getConsoleView().stopPreviewButtons();

    }

    /*
     * Reset the Preview.
     */
    private void resetPreview() {

        // Reset the Timer
        timer.restart();
        timer.stop();
        time = 0;

        // Reset the frame count
        frameCount = 0;

        // Reset the Start Image View Control Points
        view.resetStartImageView();

        // Enable Components
        view.getConsoleView().enableComponents();

        // Enable Start Button
        view.getConsoleView().resetPreviewButtons();

        // Enable the Control Points
        view.enableControlPoints();
    }

    /*
     * Responds to the console button presses.
     *
     * @param   event   The event occur which called the function
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource().equals(view.getConsoleView().getStartStopPreviewButton())) {
            if (view.getConsoleView().getStartStopPreviewButton().getText().equals("Start Preview")) {
                startPreview();
            }
            else {
                stopPreview();
            }
        }
        else if (event.getSource().equals(view.getConsoleView().getResetPreviewButton())) {
            resetPreview();
        }
        else if (event.getSource().equals(view.getConsoleView().getGroupMoveButton())) {
            view.startGroupMove();
        }
        else if (event.getSource().equals(view.getConsoleView().getGenerateMorphButton())) {
            isMorphRunning = true;
            timer.start();
            view.generateMorph();
        }
        else {

            JMenuItem curItem = (JMenuItem) event.getSource();

            if (curItem.getText().equals("Open Start Image")) {
                view.getStartImage().fileOpen();
                resetPreview();
            }
            else if (curItem.getText().equals("Open End Image")) {
                view.getEndImage().fileOpen();
                resetPreview();
            }
            else if (curItem.getText().equals("Reset Control Points")) {

                // Ask if user would like to reset
                int reply = view.resetMessage();

                // Reset Points If User Selected Yes
                if (reply == JOptionPane.YES_OPTION) {
                    reset();
                }
            }
            else if (curItem.getText().equals("Save Images")) {
                view.saveImages();
            }
            else if (curItem.getText().equals("Exit")) {
                System.exit(0);
            }
        }
    }

    /*
     * Responds to state changes for both the generation and solution
     * simulation boxes.
     *
     * @param   event       The event which triggered the function call
     */
    public void itemStateChanged(ItemEvent event) {
        Color curColor = getColor(event.getItem().toString());

        view.repaintControlPoints(curColor);
    }

    /*
    * Responds to the state change from the sliders.
    *
    * @param   event       The change event occur which called the function
    */
    public void stateChanged(ChangeEvent event) {

        if (event.getSource().equals(view.getConsoleView().getFramesSlider())) {

            int frames = ((JSlider) event.getSource()).getValue();

            // Update the Slider Label
            view.getConsoleView().changeFrameSliderLabel(frames);

            // Reset Preview
            resetPreview();
        }
        else if (event.getSource().equals(view.getConsoleView().getSecondSlider())) {

            int seconds = ((JSlider) event.getSource()).getValue();

            // Update the Slider Label
            view.getConsoleView().changeSecondSliderLabel(seconds);

            // Reset Preview
            resetPreview();
        }
        else if (event.getSource().equals(view.getConsoleView().getStartIntensitySlider())) {
            view.getStartImage().changeIntensity((float) (((JSlider) event.getSource()).getValue() / 100.0));
        }
        else if (event.getSource().equals(view.getConsoleView().getEndIntensitySlider())) {
            view.getEndImage().changeIntensity((float) (((JSlider) event.getSource()).getValue() / 100.0));
        }
        else if (event.getSource().equals(view.getConsoleView().getRowBox())) {
            view.changeRow((int) ((JSpinner) event.getSource()).getValue());
        }
        else if (event.getSource().equals(view.getConsoleView().getColumnBox())) {
            view.changeColumn((int) ((JSpinner) event.getSource()).getValue());
        }
    }

    /*
     * Returns the Color Object that Matches the String.
     * Defaults to Black.
     *
     * @param   colorString     The color string
     */
    private Color getColor(String colorString) {
        Color color;
        switch(colorString) {
            case "Black":   color = Color.BLACK;
                            break;
            case "White":   color = Color.WHITE;
                            break;
            case "Red":     color = Color.RED;
                            break;
            case "Green":   color = Color.GREEN;
                            break;
            default:        color = Color.BLACK;
                            break;
        }
        return color;
    }

    /*
     * Stops the Morph.
     */
    public void stopMorph() {
        isMorphRunning = false;
        timer.stop();
        time = 0;
        resetPreview();
    }

}
