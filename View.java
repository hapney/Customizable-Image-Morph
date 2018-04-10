/*
 * Author: Sydney Norman
 * Date: December 7, 2017
 * Project: Image Morph
 *
 * This Program allows a user to specify control points on a starting and ending
 * image and illustrate the morphing between the two images.
 *
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * The View for the Program.
 */
public class View extends JFrame {

    // Color Constant
    private static final Color SELECTED_COLOR = Color.ORANGE;

    // Program Components
    private Controller controller;
    private ImageView startImageView, endImageView, morphImageView;
    private ConsoleView consoleView;
    private int controlPointRow, controlPointColumn;

    // Control Point Movement Variables
    private boolean isDragging = false;
    private ControlPoint curControlPoint;
    private ControlPoint correspondingControlPoint;
    private Point previousPoint;
    private boolean controlPointsEnabled = true;

    // Group Move Variables
    private boolean inGroupBox = false;
    private boolean inGroupMove = false;
    private Point groupMoveBoxStart;
    private Point groupMoveLastPoint;
    private ControlPoint containedControlPoints[];

    // Frames for the Morph
    private BufferedImage startImageFrames[];
    private BufferedImage endImageFrames[];

    /*
     * Constructor for the View Class.
     *
     * @param   controller      The Controller for the Program
     */
    public View(Controller controller) {
        super("JMorph");

        this.controller = controller;

        controlPointRow = 10;
        controlPointColumn = 10;

        // Initialize Views
        startImageView = new ImageView(readImage("res/Barack.jpg"), controlPointRow, controlPointColumn);
        endImageView = new ImageView(readImage("res/Michelle.jpg"), controlPointRow, controlPointColumn);
        consoleView = new ConsoleView(controller);

        addListeners();

        JMenuBar menuBar = new MenuBar(controller);
        this.setJMenuBar(menuBar);

        // Set Up Container
        Container c = getContentPane();
        c.setLayout(new FlowLayout());

        // Set Up Views
        c.add(startImageView);
        c.add(endImageView);
        c.add(consoleView);

        // Create Window
        setSize(1250, 800);
        setResizable(false);
        setVisible(true);
    }

    /*
     * Adds Mouse Listeners to the Image Views.
     */
    private void addListeners() {

        // Create Mouse Listener for Control Points
        MouseListener ml = new MouseListener(){
            public void mouseExited(MouseEvent e){}
            public void mouseEntered(MouseEvent e){}
            public void mouseReleased(MouseEvent e){
                if (controlPointsEnabled) {
                    isDragging = false;
                    curControlPoint = null;
                }
                if (inGroupMove) {
                    inGroupMove = false;
                    startImageView.deleteGroupMoveBox();
                    endImageView.deleteGroupMoveBox();
                    consoleView.enableGroupMoveButton();
                }
                if (inGroupBox) {
                    if (containedControlPoints[0] == null) {
                        inGroupMove = false;
                        consoleView.enableGroupMoveButton();
                    }
                    inGroupBox = false;
                    startImageView.eraseGroupMoveBox();
                    endImageView.eraseGroupMoveBox();
                }
            }
            public void mousePressed(MouseEvent e){

                ImageView curImageView = startImageView;
                ImageView correspondingImageView = endImageView;

                if (e.getSource() == endImageView) {
                    curImageView = endImageView;
                    correspondingImageView = startImageView;
                }

                if (curImageView.getGroupMoveBox() != null && curImageView.getGroupMoveBox().contains(e.getPoint())) {
                    inGroupMove = true;
                }

                if (inGroupBox) {
                    groupMoveBoxStart = e.getPoint();
                }
                else if (inGroupMove) {
                    groupMoveLastPoint = e.getPoint();
                }
                else if (controlPointsEnabled) {

                    // Check if User Clicked On a Control Point
                    ControlPoint tempControlPoint = curImageView.whichControlPoint(e.getPoint());
                    if (tempControlPoint != null) {

                        // Reset Selected Control Points If New Point Pair Selected
                        if (!(tempControlPoint == curControlPoint || tempControlPoint == correspondingControlPoint)) {
                            curImageView.resetControlPointsColor();
                            correspondingImageView.resetControlPointsColor();
                        }
                        curControlPoint = tempControlPoint;

                        // Set Selected Control Point Pair Color
                        correspondingControlPoint = correspondingImageView.getControlPoint(curControlPoint.getX(), curControlPoint.getY());
                        curControlPoint.changeColor(SELECTED_COLOR);
                        correspondingControlPoint.changeColor(SELECTED_COLOR);
                        curImageView.repaint();
                        correspondingImageView.repaint();

                        isDragging = true;
                    }
                }
            }
            public void mouseClicked(MouseEvent e){}
        };

        // Create Mouse Motion Listener for Control Points
        MouseMotionListener mml = new MouseMotionListener(){
            public void mouseDragged(MouseEvent e) {

                ImageView sourceImageView = (ImageView) e.getSource();
                ImageView correspondingImageView = startImageView;

                if (sourceImageView == startImageView) {
                    correspondingImageView = endImageView;
                }

                if (inGroupBox) {
                    sourceImageView.drawGroupMoveBox(groupMoveBoxStart, e.getPoint());

                    containedControlPoints = sourceImageView.selectControlPointsInBox();
                    correspondingImageView.colorGroupControlPoints(containedControlPoints);

                }
                else if (inGroupMove) {

                    Point constrainedPoints[] = sourceImageView.constrainPoints(containedControlPoints, (e.getX() - groupMoveLastPoint.getX()), (e.getY() - groupMoveLastPoint.getY()));

                    if (constrainedPoints != null) {
                        int i = 0;
                        while (i < containedControlPoints.length && containedControlPoints[i] != null) {
                            ControlPoint cp = containedControlPoints[i];

                            cp.changePoint(constrainedPoints[i].getX(), constrainedPoints[i].getY());
                            i++;
                        }
                        groupMoveLastPoint = e.getPoint();
                    }

                    sourceImageView.repaint();
                }
                else if (controlPointsEnabled && isDragging) {

                    Point constrainedPoint = sourceImageView.constrainPoint(curControlPoint, e.getPoint());

                    if (constrainedPoint == null) {
                        constrainedPoint = previousPoint;
                    }
                    else {
                        previousPoint = constrainedPoint;
                    }

                    curControlPoint.changePoint(constrainedPoint.getX(), constrainedPoint.getY());
                    sourceImageView.repaint();
                }
            }
            public void mouseMoved(MouseEvent e) {}
        };

        // Add Mouse Listener to Image Views
        startImageView.addMouseListener(ml);
        endImageView.addMouseListener(ml);

        // Add Mouse Motion Listener to Image Views
        startImageView.addMouseMotionListener(mml);
        endImageView.addMouseMotionListener(mml);
    }

    /*
     * Enables the Control Points.
     */
    public void enableControlPoints() {
        controlPointsEnabled = true;
    }

    /*
     * Disables the Control Points.
     */
    public void disableControlPoints() {
        controlPointsEnabled = false;
    }

    /*
     * Sets the Preview Frame.
     *
     * @param   frameCount      The current frame count
     * @param   totalFrames     The total number of frames
     */
    public void setPreviewFrame(int frameCount, int totalFrames) {

        // Find the New Point Locations
        for (int xi = 1; xi < (controlPointColumn + 1); xi++) {
            for (int yi = 1; yi < (controlPointRow + 1); yi++) {

                // Get Coordinates
                double x1 = startImageView.getControlPoints()[xi][yi].getPreviewStartXCoordinate();
                double y1 = startImageView.getControlPoints()[xi][yi].getPreviewStartYCoordinate();
                double x2 = endImageView.getControlPoints()[xi][yi].getXCoordinate();
                double y2 = endImageView.getControlPoints()[xi][yi].getYCoordinate();

                // Calculate Coordinate
                double x = (frameCount * ((x2 - x1) / totalFrames)) + x1;
                double y = (frameCount * ((y2 - y1) / totalFrames)) + y1;

                startImageView.getControlPoints()[xi][yi].changePreviewPoint(x, y);
            }
        }
        startImageView.repaint();
    }

    /*
     * Shows the Current Frame in the Morph.
     *
     * @param   frameCount      The current frame count
     * @param   totalFrames     The total number of frames
     */
    public void showMorphFrame(int frameCount, int totalFrames) {


        BufferedImage startFrame = startImageFrames[frameCount - 1];
        BufferedImage endFrame = endImageFrames[totalFrames - frameCount];
        BufferedImage combinedImage = new BufferedImage(startFrame.getWidth(), startFrame.getHeight(), BufferedImage.TYPE_INT_RGB);

        Graphics g = combinedImage.getGraphics();
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setComposite(AlphaComposite.SrcOver.derive(1 - ((float)frameCount / totalFrames)));
        g2d.drawImage(startFrame, 0, 0, this);

        g2d.setComposite(AlphaComposite.SrcOver.derive((float) frameCount / totalFrames));
        g2d.drawImage(endFrame, 0, 0, this);

        File outputFile = new File("Frame" + frameCount + ".jpg");
        try {
            ImageIO.write(combinedImage, "jpg", outputFile);
        } catch (IOException e) {
            System.out.println("Error Saving Morph Frame");
        }

        morphImageView.setImage(combinedImage);
    }

    /*
     * Sets the Given Morph Frame.
     *
     * @param   frameCount          The Current Frame
     * @param   totalFrames         The Total Number of Frames
     * @param   isForwardRunning    Whether or not the instance is of start to end image
     */
    private void setFrame(int frameCount, int totalFrames, boolean isForwardRunning) {

        ImageView startIV = startImageView;
        ImageView endIV = endImageView;

        if (!isForwardRunning) {
            startIV = endImageView;
            endIV = startImageView;
        }

        // Find the New Point Locations
        for (int xi = 1; xi < (controlPointColumn + 1); xi++) {
            for (int yi = 1; yi < (controlPointRow + 1); yi++) {

                // Get Coordinates
                double x1 = morphImageView.getControlPoints()[xi][yi].getPreviewStartXCoordinate();
                double y1 = morphImageView.getControlPoints()[xi][yi].getPreviewStartYCoordinate();
                double x2 = endIV.getControlPoints()[xi][yi].getXCoordinate();
                double y2 = endIV.getControlPoints()[xi][yi].getYCoordinate();

                // Calculate Coordinate
                double x = (frameCount * ((x2 - x1) / totalFrames)) + x1;
                double y = (frameCount * ((y2 - y1) / totalFrames)) + y1;

                morphImageView.getControlPoints()[xi][yi].changePreviewPoint(x, y);
            }
        }

        for (int xi = 0; xi < (controlPointColumn + 1); xi++) {
            for (int yi = 0; yi < (controlPointRow + 1); yi++) {

                // Original Triangle
                double sx1 = startIV.getControlPoints()[xi][yi].getPreviewStartXCoordinate();
                double sy1 = startIV.getControlPoints()[xi][yi].getPreviewStartYCoordinate();
                double sx2 = startIV.getControlPoints()[xi + 1][yi].getPreviewStartXCoordinate();
                double sy2 = startIV.getControlPoints()[xi + 1][yi].getPreviewStartYCoordinate();
                double sx3 = startIV.getControlPoints()[xi + 1][yi + 1].getPreviewStartXCoordinate();
                double sy3 = startIV.getControlPoints()[xi + 1][yi + 1].getPreviewStartYCoordinate();
                double sx4 = startIV.getControlPoints()[xi][yi + 1].getPreviewStartXCoordinate();
                double sy4 = startIV.getControlPoints()[xi][yi + 1].getPreviewStartYCoordinate();

                // New Triangle
                double dx1 = morphImageView.getControlPoints()[xi][yi].getXCoordinate();
                double dy1 = morphImageView.getControlPoints()[xi][yi].getYCoordinate();
                double dx2 = morphImageView.getControlPoints()[xi + 1][yi].getXCoordinate();
                double dy2 = morphImageView.getControlPoints()[xi + 1][yi].getYCoordinate();
                double dx3 = morphImageView.getControlPoints()[xi + 1][yi + 1].getXCoordinate();
                double dy3 = morphImageView.getControlPoints()[xi + 1][yi + 1].getYCoordinate();
                double dx4 = morphImageView.getControlPoints()[xi][yi + 1].getXCoordinate();
                double dy4 = morphImageView.getControlPoints()[xi][yi + 1].getYCoordinate();

                // Check for the Top Right and Bottom Left
                if (((xi == controlPointColumn) && (yi == 1))
                        || ((xi == 1) && (yi == controlPointRow))) {

                    Triangle S = new Triangle(sx1, sy1, sx2, sy2, sx4, sy4);
                    Triangle D = new Triangle(dx1, dy1, dx2, dy2, dx4, dy4);
                    setWarpFrame(startIV.getImage(), morphImageView.getImage(), S, D, null, null);

                    S = new Triangle(sx2, sy2, sx3, sy3, sx4, sy4);
                    D = new Triangle(dx2, dy2, dx3, dy3, dx4, dy4);
                    setWarpFrame(startIV.getImage(), morphImageView.getImage(), S, D, null, null);

                }
                else {

                    Triangle S = new Triangle(sx1, sy1, sx2, sy2, sx3, sy3);
                    Triangle D = new Triangle(dx1, dy1, dx2, dy2, dx3, dy3);
                    setWarpFrame(startIV.getImage(), morphImageView.getImage(), S, D, null, null);

                    S = new Triangle(sx3, sy3, sx4, sy4, sx1, sy1);
                    D = new Triangle(dx3, dy3, dx4, dy4, dx1, dy1);
                    setWarpFrame(startIV.getImage(), morphImageView.getImage(), S, D, null, null);
                }
            }
        }
        morphImageView.repaint();
    }

    /*
     * Resets the Start Image View.
     */
    public void resetStartImageView() {
        startImageView.resetPreviewControlPoints();
    }

    /*
     * Retrieves the Console View.
     *
     * @return      The Console View
     */
    public ConsoleView getConsoleView() {
        return consoleView;
    }

    /*
     * Retrieves the Start Image View.
     *
     * @return      The Start Image View
     */
    public ImageView getStartImage() {
        return startImageView;
    }

    /*
     * Retrieves the End Image View.
     *
     * @return      The End Image View
     */
    public ImageView getEndImage() {
        return endImageView;
    }

    /*
     * Reads an Image from a File.
     * ** Code from Dr. Seales
     *
     * @param   file        The file to read
     */
    private BufferedImage readImage (String file) {
        Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(file));
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = new BufferedImage
                (image.getWidth(this), image.getHeight(this),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bim.createGraphics();
        big.drawImage (image, 0, 0, this);
        return bim;
    }

    /*
     * Sets the Control Point Colors for Both Image Views.
     *
     * @param   color       The New Color to Set
     */
    public void repaintControlPoints(Color color) {
        startImageView.setControlPointsColor(color);
        endImageView.setControlPointsColor(color);
    }

    /*
     * Checks if the user would like to reset all their control points.
     *
     * @return  The user response
     */
    public int resetMessage() {
        int reply = JOptionPane.showConfirmDialog(View.this, "Are you sure you would like to reset your control points?", "Reset", JOptionPane.YES_NO_OPTION);
        return reply;
    }

    /*
     * Changes the number of control points rows.
     *
     * @param   newRow      The new number of rows
     */
    public void changeRow(int newRow) {

        controlPointRow = newRow;

        startImageView.changeControlPointLayout(controlPointRow, controlPointColumn);
        endImageView.changeControlPointLayout(controlPointRow, controlPointColumn);
    }

    /*
     * Changes the number of control points columns.
     *
     * @param   newColumn   The new number of columns
     */
    public void changeColumn(int newColumn) {

        controlPointColumn = newColumn;

        startImageView.changeControlPointLayout(controlPointRow, controlPointColumn);
        endImageView.changeControlPointLayout(controlPointRow, controlPointColumn);
    }

    /*
     * Starts a Group Move Sequence.
     */
    public void startGroupMove() {
        inGroupBox = true;
        consoleView.disableGroupMoveButton();
    }

    /*
     * Opens a File Explorer and Allows User to Save Working Images.
     */
    public void saveImages() {
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showSaveDialog(View.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println(fc.getSelectedFile().getName());
            String s = fc.getSelectedFile().getAbsolutePath();
            System.out.println(s);

            File folder = new File(s);
            boolean folderCreated = folder.mkdirs();

            if (folderCreated) {
                File startImage = new File(s + "/startImage.jpg");
                File endImage = new File(s + "/endImage.jpg");

                try {
                    ImageIO.write(startImageView.getImage(), "jpg", startImage);
                    ImageIO.write(endImageView.getImage(), "jpg", endImage);
                } catch (IOException e) {
                    System.out.println("Error: Couldn't save project.");
                }
            }
        }
    }

    /*
     * Solves a series of matrix equations.
     * ** Code from Dr. Seales
     */
    private static void solve(int n, double[][] a, int[] l,
                              double[] b, double[] x){
        /*********************************************************
         a and l have previously been passed to Gauss() b is the product of
         a and x. x is the 1x3 matrix of coefficients to solve for
         *************************************************************/
        int i, k;
        double sum;
        for(k=0; k < (n-1); ++k){
            for( i=k+1; i<n; ++i){
                b[l[i]] -= a[l[i]][k] * b[l[k]];
            }
        }
        x[n-1] = b[l[n-1]] / a[l[n-1]][n-1];

        for( i=n-2; i>= 0; --i){
            sum = b[l[i]];
            for(int j=i+1; j<n; ++j){
                sum = sum - a[l[i]][j] * x[j];
            }
            x[i] = sum / a[l[i]][i];
        }
    }

    /*
     * Solves the Gaussian Elimination.
     * ** Code from Dr. Seales
     */
    private static void Gauss( int n, double[][] a, int[] l){
        /****************************************************
         a is a n x n matrix and l is an int array of length n
         l is used as an index array that will determine the order of
         elimination of coefficients
         All array indexes are assumed to start at 0
         ******************************************************/
        double [] s = new double[n];   //scaling factor
        int i, j=0, k;
        double r, rmax, smax, xmult;
        for(i=0; i<n; ++i){
            l[i] = i;
            smax = 0;
            for(j=0; j<n; ++j)
                smax = Math.max(smax, Math.abs(a[i][j]));
            s[i] = smax;
        }

        i=n-1;
        for(k=0; k<(n-1); ++k){
            --j;
            rmax = 0;
            for(i=k; i<n; ++i){
                r = Math.abs(a[l[i]][k] / s[l[i]]);
                if( r > rmax){
                    rmax = r;
                    j = i;
                }
            }
            int temp = l[j];
            l[j] = l[k];
            l[k] = temp;
            for( i = k+1; i< n; ++i){
                xmult = a[l[i]][k] / a[l[k]][k];
                a[l[i]][k] = xmult;
                for(j = k+1; j<n; ++j){
                    a[l[i]][j] = a[l[i]][j] - xmult * a[l[k]][j];
                }
            }
        }
    }

    /*
     * Sets the Current Warp Frame.
     * ** Code from Dr. Seales
     */
    private static void setWarpFrame (BufferedImage src, BufferedImage dest,
             Triangle S, Triangle D,
             Object ALIASING, Object INTERPOLATION) {

        if( ALIASING == null )
            ALIASING = RenderingHints.VALUE_ANTIALIAS_ON;
        if( INTERPOLATION == null )
            INTERPOLATION = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        double [][] a = new double [3][3];
        for( int i= 0; i<3; ++i){
            a[i][0] = S.getX(i);
            a[i][1] = S.getY(i);
            a[i][2] = 1.0;
        }

        int l[] = new int[3];
        Gauss(3,a,l);

        double[] b = new double[3];
        for( int i= 0; i<3; ++i){
            b[i] = D.getX(i);
        }

        double[] x = new double[3];
        solve(3, a, l, b, x);

        double [] by = new double[3];
        for(int i = 0; i<3; ++i){
            by[i] = D.getY(i);
        }

        double[] y = new double[3];
        solve(3, a, l, by, y);

        AffineTransform af = new AffineTransform(x[0], y[0], x[1], y[1],
                x[2], y[2]);
        GeneralPath destPath = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

        destPath.moveTo((float)D.getX(0), (float)D.getY(0));
        destPath.lineTo((float)D.getX(1), (float)D.getY(1));
        destPath.lineTo((float)D.getX(2), (float)D.getY(2) );
        destPath.lineTo((float)D.getX(0), (float)D.getY(0) );
        Graphics2D g2 = dest.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                ALIASING);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                INTERPOLATION);
        g2.clip(destPath);
        g2.setTransform(af);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();


    }

    /*
     * Generates the Actual Morph Sequence.
     */
    public void generateMorph() {

        int frameCount = consoleView.getFramesSlider().getValue () * consoleView.getSecondSlider().getValue();
        startImageFrames = new BufferedImage[frameCount];
        endImageFrames = new BufferedImage[frameCount];

        // Create the End Frames
        morphImageView = createMorphView(endImageView);

        for (int i = 0; i < endImageFrames.length; i++) {
            setFrame(i, frameCount, false);
            endImageFrames[i] = ImageView.deepCopy(morphImageView.getImage());
        }
         morphImageView.resetPreviewControlPoints();

         // Create the Start Frames
        morphImageView = createMorphView(startImageView);
        for (int i = 0; i < startImageFrames.length; i++) {
            setFrame(i, frameCount, true);
            startImageFrames[i] = ImageView.deepCopy(morphImageView.getImage());
        }

        MorphView morphView = new MorphView(morphImageView);
        morphView.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                controller.stopMorph();
            }
        });

    }

    /*
     * Creates a Duplicate Morph View.
     *
     * @param   curImageView    The ImageView to duplicate
     */
    private ImageView createMorphView(ImageView curImageView) {
        return new ImageView(curImageView.getImage(), controlPointRow, controlPointColumn, curImageView.getControlPoints());
    }
}
