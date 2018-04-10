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
 * View for the Console Buttons.
 */
public class ConsoleView extends JPanel {

    private final static String COLORS[] = {"Black", "White", "Red", "Green"};
    private final static int DEFAULT_FRAMES_PER_SECOND = 30;
    private final static int DEFAULT_SECONDS = 2;

    // Button Panel Objects
    private JSlider startIntensitySlider;
    private JButton groupMoveButton;
    private JSlider endIntensitySlider;

    private JSpinner rowBox, columnBox;

    // Component Panel Objects
    private JLabel framesSliderLabel;
    private JLabel secondSliderLabel;
    private JSlider framesSlider;
    private JSlider secondSlider;
    private JComboBox<String> colorChoiceBox;

    // Preview Panel Objects
    private JButton startStopPreviewButton;
    private JButton resetPreviewButton;
    private JButton generateMorphButton;

    /*
     * Constructor for the Console View.
     *
     * @param   controller      The Program's Controller to serve as various listeners
     */
    public ConsoleView(Controller controller) {
        super();

        setLayout(new FlowLayout());
        setPreferredSize(new Dimension(1250, 150));

        // Build Sub Components
        buildButtons(controller);
        buildComponents(controller);
        buildPreview(controller);
    }

    /*
     * Builds the Button SubPanel.
     *
     * @param   controller      The Program's Controller to serve as an Action Listener
     */
    private void buildButtons(Controller controller) {

        // Setup SubPanel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 3));
        topPanel.setPreferredSize(new Dimension(1250, 65));

        // Create Start Intensity Panel
        JPanel startIntensityPanel = new JPanel();
        startIntensityPanel.setLayout(new GridLayout(2, 1));
        startIntensityPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // Create Color Choice Panel
        JPanel colorChoicePanel = new JPanel();
        colorChoicePanel.setLayout(new GridLayout(1, 2));

        // Create Middle Console Panel
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayout(2, 1));
        middlePanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // Create End Intensity Panel
        JPanel endIntensityPanel = new JPanel();
        endIntensityPanel.setLayout(new GridLayout(2, 1));
        endIntensityPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // Create Start Intensity Label and Slider
        JLabel startIntensityLabel = new JLabel("Start Intensity");
        startIntensityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        startIntensitySlider = new JSlider(JSlider.HORIZONTAL, 1, 200, 100);
        startIntensitySlider.addChangeListener(controller);

        // Create End Intensity Label and Slider
        JLabel endIntensityLabel = new JLabel("End Intensity");
        endIntensityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        endIntensitySlider = new JSlider(JSlider.HORIZONTAL, 1, 200, 100);
        endIntensitySlider.addChangeListener(controller);

        // Create and Setup Reset Button
        groupMoveButton = new JButton("Control Point Group Move");
        groupMoveButton.addActionListener(controller);

        // Create Color Choice Components
        JLabel colorChoiceLabel = new JLabel("Control Point Color");
        colorChoiceBox = new JComboBox<>(COLORS);

        // Setup Color Choice Components
        colorChoiceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        colorChoiceBox.addItemListener(controller);

        // Add Start Intensity Elements to Panel
        startIntensityPanel.add(startIntensityLabel);
        startIntensityPanel.add(startIntensitySlider);

        // Add Color Choice Elements to Panel
        colorChoicePanel.add(colorChoiceLabel);
        colorChoicePanel.add(colorChoiceBox);

        // Add Elements to Middle Panel
        middlePanel.add(groupMoveButton);
        middlePanel.add(colorChoicePanel);

        // Add End Intensity Elements to Panel
        endIntensityPanel.add(endIntensityLabel);
        endIntensityPanel.add(endIntensitySlider);

        // Add Buttons to Panel
        topPanel.add(startIntensityPanel);
        topPanel.add(middlePanel);
        topPanel.add(endIntensityPanel);

        add(topPanel);

    }

    /*
     * Builds the Component SubPanel.
     *
     * @param   controller      The Program's Controller to serve as a Change Listener and Item Listener
     */
    private void buildComponents(Controller controller) {

        // Setup SubPanel
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2, 2));
        gridPanel.setPreferredSize(new Dimension(600, 75));
        gridPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        SpinnerNumberModel rowModel = new SpinnerNumberModel(10, 0, 30, 1);
        SpinnerNumberModel columnModel = new SpinnerNumberModel(10, 0, 30, 1);

        // Create Second Slider Components
        JLabel rowLabel = new JLabel("Rows");
        rowBox = new JSpinner(rowModel);

        // Create Frame Slider Components
        JLabel columnLabel = new JLabel("Columns");
        columnBox = new JSpinner(columnModel);

        // Setup Second Slider Components
        rowLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rowBox.addChangeListener(controller);

        // Setup Frame Slider Components
        columnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        columnBox.addChangeListener(controller);

        // Add Components to Panel
        gridPanel.add(rowLabel);
        gridPanel.add(rowBox);
        gridPanel.add(columnLabel);
        gridPanel.add(columnBox);

        add(gridPanel);

    }

    /*
     * Builds the Preview SubPanel.
     *
     * @param   controller      The Program's Controller to serve as an Action Listener
     */
    private void buildPreview(Controller controller) {

        // Setup SubPanel
        JPanel previewPanel = new JPanel();
        previewPanel.setLayout(new GridLayout(3, 2));
        previewPanel.setPreferredSize(new Dimension(600, 75));
        previewPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // Create Second Slider Components
        secondSliderLabel = new JLabel("Seconds: " + DEFAULT_SECONDS);
        secondSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, DEFAULT_SECONDS);

        // Create Frame Slider Components
        framesSliderLabel = new JLabel("Frames Per Second: " + DEFAULT_FRAMES_PER_SECOND);
        framesSlider = new JSlider(JSlider.HORIZONTAL, 1, 60, DEFAULT_FRAMES_PER_SECOND);

        // Setup Second Slider Components
        secondSliderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        secondSlider.addChangeListener(controller);

        // Setup Frame Slider Components
        framesSliderLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        framesSlider.addChangeListener(controller);

        // Create Buttons
        startStopPreviewButton = new JButton("Start Preview");
        resetPreviewButton = new JButton("Reset Preview");
        generateMorphButton = new JButton("Generate Morph");

        // Setup Buttons
        startStopPreviewButton.addActionListener(controller);
        resetPreviewButton.addActionListener(controller);
        generateMorphButton.addActionListener(controller);

        resetPreviewButton.setEnabled(false);

        // Add Buttons to Panel
        previewPanel.add(secondSliderLabel);
        previewPanel.add(secondSlider);
        previewPanel.add(framesSliderLabel);
        previewPanel.add(framesSlider);

        JPanel subpanel = new JPanel();
        subpanel.setLayout(new GridLayout(1, 2));
        subpanel.add(startStopPreviewButton);
        subpanel.add(resetPreviewButton);

        previewPanel.add(subpanel);
        previewPanel.add(generateMorphButton);


        add(previewPanel);
    }

    /*
     * Disables the Buttons and Components.
     */
    public void disableComponents() {

        // Disable Buttons
        startIntensitySlider.setEnabled(false);
        groupMoveButton.setEnabled(false);
        endIntensitySlider.setEnabled(false);

        // Disable Components
        colorChoiceBox.setEnabled(false);
        secondSlider.setEnabled(false);
        framesSlider.setEnabled(false);
    }

    /*
     * Enables the Buttons and Components.
     */
    public void enableComponents() {

        // Enable Buttons
        startIntensitySlider.setEnabled(true);
        groupMoveButton.setEnabled(true);
        endIntensitySlider.setEnabled(true);

        // Enable Components
        colorChoiceBox.setEnabled(true);
        secondSlider.setEnabled(true);
        framesSlider.setEnabled(true);
    }

    /*
     * Enables and Disables the Appropriate Buttons for a Start Preview.
     */
    public void startPreviewButtons() {

        startStopPreviewButton.setText("Stop Preview");

        resetPreviewButton.setEnabled(true);
    }

    /*
     * Enables and Disables the Appropriate Buttons for a Stop Preview.
     */
    public void stopPreviewButtons() {

        startStopPreviewButton.setText("Start Preview");

        resetPreviewButton.setEnabled(true);
    }

    /*
     * Enables and Disables the Appropriate Buttons for a Reset Preview.
     */
    public void resetPreviewButtons() {

        startStopPreviewButton.setText("Start Preview");

        startStopPreviewButton.setEnabled(true);
        resetPreviewButton.setEnabled(false);
    }

    /*
     * Enables and Disables the Appropriate Buttons for an End Preview.
     */
    public void endedPreviewButtons() {

        startStopPreviewButton.setEnabled(false);
        resetPreviewButton.setEnabled(true);
    }

    /*
     * Updates the Frame Slider Label.
     *
     * @param   frames      The number of frames to update the label to
     */
    public void changeFrameSliderLabel(int frames) {
        framesSliderLabel.setText("Frames Per Second: " + frames);
    }

    /*
     * Updates the Second Slider Label.
     *
     * @param   seconds     The number of seconds to update the label to
     */
    public void changeSecondSliderLabel(int seconds) {
        secondSliderLabel.setText("Seconds: " + seconds);
    }

    /*
     * Disables the Group Move Button.
     */
    public void disableGroupMoveButton() {
        groupMoveButton.setEnabled(false);
    }

    /*
     * Enables the Group Move Button.
     */
    public void enableGroupMoveButton() {
        groupMoveButton.setEnabled(true);
    }

    // Get Functions

    /*
     * Retrieves the Reset Button.
     *
     * @return      The Reset Button
     */
    public JButton getResetButton() {
        return groupMoveButton;
    }

    /*
     * Retrieves the Frames Slider.
     *
     * @return      The Frames Slider
     */
    public JSlider getFramesSlider() {
        return framesSlider;
    }

    /*
     * Retrieves the Seconds Slider.
     *
     * @return      The Seconds Slider
     */
    public JSlider getSecondSlider() {
        return secondSlider;
    }

    /*
     * Retrieves the Start Preview Button.
     *
     * @return      The Start Preview Button
     */
    public JButton getStartStopPreviewButton() {
        return startStopPreviewButton;
    }

    /*
     * Gets the Generate Morph Button.
     *
     * @return      The Generate Morph Button
     */
    public JButton getGenerateMorphButton() {
        return generateMorphButton;
    }

    /*
     * Retrieves the Reset Preview Button
     *
     * @return      The Reset Preview Button
     */
    public JButton getResetPreviewButton() {
        return resetPreviewButton;
    }

    /*
     * Gets the Start Intensity Slider.
     *
     * @return      The Start Intensity Slider
     */
    public JSlider getStartIntensitySlider() {
        return startIntensitySlider;
    }

    /*
     * Gets the End Intensity Slider.
     *
     * @return      The End Intensity Slider
     */
    public JSlider getEndIntensitySlider() {
        return endIntensitySlider;
    }

    /*
     * Gets the Change Control Point Row Box.
     *
     * @return      The Control Point Row Box
     */
    public JSpinner getRowBox() {
        return rowBox;
    }

    /*
     * Gets the Change Control Point Column Box.
     *
     * @return      The Control Point Column Box
     */
    public JSpinner getColumnBox() {
        return columnBox;
    }

    /*
     * Gets the Group Move Button.
     *
     * @return      The Group Move Button
     */
    public JButton getGroupMoveButton() {
        return groupMoveButton;
    }

}
