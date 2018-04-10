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
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

/*
 * The Image View Class which handles the combination of an image and its control points.
 */
public class ImageView extends JLabel {

    // Constants
    private final static int MAX_ROWS = 30;
    private final static int MAX_COLUMNS = 50;
    private final static int IMAGE_WIDTH = 600;

    private final static int BOUNDARY_POINT_X[] = {0, -1, -1, 0, 1, 1};
    private final static int BOUNDARY_POINT_Y[] = {-1, -1,  0, 1, 1, 0};

    private final static int BOUNDARY_POINT_X_TOP_RIGHT[] = {0, -1, -1, 0, 1, 1, 1};
    private final static int BOUNDARY_POINT_Y_TOP_RIGHT[] = {-1, -1, 0, 1, 1, 0, -1};

    private final static int BOUNDARY_POINT_X_BOTTOM_LEFT[] = {0, -1, -1, -1, 0, 1, 1};
    private final static int BOUNDARY_POINT_Y_BOTTOM_LEFT[] = {-1, -1, 0, 1, 1, 1, 0};

    // Instance Variables to Hold the Buffered Image and Control Points
    private BufferedImage bim = null;
    private BufferedImage originalBim = null;
    private ControlPoint controlPoints[][] = new ControlPoint[MAX_COLUMNS + 2][MAX_ROWS + 2];
    private int rows, columns;

    // The Primary Color for the Control Points
    private static Color controlPointsColor = Color.BLACK;

    // Group Move Variables
    private Polygon groupMoveBox = null;
    private boolean drawGroupMoveBox = false;
    private boolean drawControlPoints = true;

    /*
     * The Constructor for the ImageView Class.
     *
     * @param   img     The Image
     * @param   rows    The number of control point rows
     * @param   columns The number of control point columns
     */
    public ImageView(BufferedImage img, int rows, int columns) {
        super();

        this.rows = rows;
        this.columns = columns;

        setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_WIDTH));
        bim = deepCopy(img);
        originalBim = img;

        // Setup the Image View Control Points
        setupControlPoints();

        // Repaint the View
        this.repaint();
    }

    /*
     * The Constructor for the Morph ImageView Class.
     *
     * @param   img             The Image
     * @param   rows            The number of control point rows
     * @param   columns         The number of control point columns
     * @param   controlPoints   The control points for the image
     */
    public ImageView(BufferedImage img, int rows, int columns, ControlPoint controlPoints[][]) {
        super();

        this.rows = rows;
        this.columns = columns;

        setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_WIDTH));
        bim = deepCopy(img);
        originalBim = img;

        for (int xi = 0; xi < columns + 2; xi++) {
            for (int yi = 0; yi < rows + 2; yi++) {
                this.controlPoints[xi][yi] = controlPoints[xi][yi].duplicate();
            }
        }
        drawControlPoints = false;
        this.repaint();
    }

    /*
     * Setup the Control Points for the Image View.
     */
    private void setupControlPoints() {
        for (int xi = 0; xi < (columns + 2); xi++) {
            for (int yi = 0; yi < (rows + 2); yi++) {

                boolean isBorder = false;
                if (xi == 0 || xi == (columns + 1)
                        || yi == 0 || yi == (rows + 1)) {
                    isBorder = true;
                }

                // Create Control Points
                controlPoints[xi][yi] = new ControlPoint(xi, yi, IMAGE_WIDTH, columns, rows, isBorder);
                controlPoints[xi][yi].changeColor(controlPointsColor);
            }
        }

        this.repaint();
    }

    /*
     * Sets the Image for the ImageView.
     * ** Code from Dr. Seales
     *
     * @param   img     The Image to Set
     */
    public void setImage(BufferedImage img) {

        // Error Checking
        if (img == null) return;

        bim = deepCopy(img);

        // Repaint the View
        this.repaint();
    }

    /*
     * Sets the Original Image for the ImageView
     *
     * @param   img     The Image to Set
     */
    private void setOriginalImage(BufferedImage img) {

        // Error Checking
        if (img == null) return;

        originalBim = img;
        bim = deepCopy(img);

        // Repaint the View
        this.repaint();
    }

    /*
     * Opens the File Chooser.
     * ** Code from Dr. Seales
     */
    public void fileOpen() {
        final JFileChooser fc = new JFileChooser(".");
        int returnVal = fc.showOpenDialog(ImageView.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                bim = ImageIO.read(file);
            } catch (IOException e1) {
            }

            setOriginalImage(bim);
            showImage();
        }
    }

    /*
     * Accessor to Get a Handle to the BufferedImage Object Stored Here
     * ** Code from Dr. Seales
     */
    public BufferedImage getImage() {
        return bim;
    }

    /*
     * Shows the Image.
     */
    private void showImage() {
        if (bim == null) return;
        this.repaint();
    }

    /*
     * Determines which Control Point the Point is in.
     *
     * @param   p       The point clicked in
     */
    public ControlPoint whichControlPoint(Point p) {
        for (int xi = 1; xi < (columns + 1); xi++) {
            for (int yi = 1; yi < (rows + 1); yi++) {
                if (controlPoints[xi][yi].getControlPointSquare().contains(p)) {
                    return controlPoints[xi][yi];
                }
            }
        }
        return null;
    }

    /*
     * Retrieves the Control Points.
     *
     * @return      The Control Points
     */
    public ControlPoint[][] getControlPoints() {
        return controlPoints;
    }

    /*
     * Retrieves the Control Points at a Certain Grid Position.
     *
     * @param   x       The X Position in the Grid
     * @param   y       The Y Position in the Grid
     */
    public ControlPoint getControlPoint(int x, int y) {
        return controlPoints[x][y];
    }

    /*
     * Sets the Control Points Color.
     *
     * @param   color       The New Color to Set the Control Points
     */
    public void setControlPointsColor(Color color) {
        controlPointsColor = color;
        for (int xi = 1; xi <= columns; xi++) {
            for (int yi = 1; yi <= rows; yi++) {
                controlPoints[xi][yi].changeColor(color);
            }
        }
        repaint();
    }

    /*
     * Changes the intensity of the given image.
     *
     * @param   percentage      The percentage to change
     */
    public void changeIntensity(float percentage) {

        if (originalBim == null) {
            return;
        }

        int[] pixel = { 0, 0, 0, 0 };
        float[] hsbvals = { 0, 0, 0 };

        /*RescaleOp op = new RescaleOp(percentage, 0, null);
        BufferedImage bufferedImage = op.filter(originalBim, originalBim); */

        // https://stackoverflow.com/questions/46797579/how-can-i-control-the-brightness-of-an-image
        for ( int i = 0; i < originalBim.getHeight(); i++ ) {
            for ( int j = 0; j < originalBim.getWidth(); j++ ) {

                // get the pixel data
                originalBim.getRaster().getPixel( j, i, pixel );

                // converts its data to hsb to change brightness
                Color.RGBtoHSB( pixel[0], pixel[1], pixel[2], hsbvals );

                // calculates the brightness component.
                float newBrightness = hsbvals[2] * percentage;
                if ( newBrightness > 1f ) {
                    newBrightness = 1f;
                }

                // create a new color with the new brightness
                Color c = new Color( Color.HSBtoRGB( hsbvals[0], hsbvals[1], newBrightness ) );

                // set the new pixel
                bim.getRaster().setPixel( j, i, new int[]{ c.getRed(), c.getGreen(), c.getBlue(), pixel[3] } );

            }

        }

        setImage(bim);
    }

    /*
     * Changes the control point layout.
     *
     * @param   row     The new number of rows
     * @param   column  The new number of columns
     */
    public void changeControlPointLayout(int row, int column) {
        this.rows = row;
        this.columns = column;

        setupControlPoints();
    }

    /*
     * Constrains the Given Point.
     *
     * @param   controlPoint        The given control point
     * @param   p                   The new point location
     * @return                      The constrained point
     */
    public Point constrainPoint(ControlPoint controlPoint, Point p) {

        if (insideBounds(controlPoint, p)) {
            return p;
        }

        return null;
    }

    /*
     * Constrains a Given List of Points.
     *
     * @param   controlPoints       The given control point list
     * @param   changeInX           The change in X location
     * @param   changeInY           The change in Y location
     * @return                      The new list of control point locations
     */
    public Point[] constrainPoints(ControlPoint controlPoints[], double changeInX, double changeInY) {

        Point newPoints[] = new Point[controlPoints.length];

        int i = 0;
        while (i < controlPoints.length && controlPoints[i] != null) {
            Point newPoint = new Point((int) (controlPoints[i].getXCoordinate() + changeInX), (int) (controlPoints[i].getYCoordinate() + changeInY));
            newPoints[i] = newPoint;

            if (!insideBounds(controlPoints[i], newPoint)) {
                return null;
            }
            i++;
        }

        return newPoints;
    }

    /*
     * Checks if a control point is inside its bounds
     *
     * @param   controlPoint        The given control point
     * @param   p                   The point to check
     * @return                      Whether or not the point is inside its bounds
     */
    private boolean insideBounds(ControlPoint controlPoint, Point p) {

        // Determine Where in Grid Control Point Is
        int x = controlPoint.getX();
        int y = controlPoint.getY();

        // Create Polygon
        int boundaryPointCount = BOUNDARY_POINT_X_TOP_RIGHT.length;
        int xBoundaryPointCoordinates[] = new int[boundaryPointCount];
        int yBoundaryPointCoordinates[] = new int[boundaryPointCount];

        // Check If Control Point is Top Right or Bottom Left
        if (((x == columns) && (y == 1))
                || ((x == 1) && (y == rows))) {

            if (x == columns) {

                // Add the Boundary Points for Top Right Control Point
                for (int i = 0; i < BOUNDARY_POINT_X_TOP_RIGHT.length; i++) {
                    int x1 = x + BOUNDARY_POINT_X_TOP_RIGHT[i];
                    int y1 = y + BOUNDARY_POINT_Y_TOP_RIGHT[i];
                    xBoundaryPointCoordinates[i] = (int) controlPoints[x1][y1].getXCoordinate();
                    yBoundaryPointCoordinates[i] = (int) controlPoints[x1][y1].getYCoordinate();
                }

            }
            else {

                // Add the Boundary Points for Bottom Left Control Point
                for (int i = 0; i < BOUNDARY_POINT_X_BOTTOM_LEFT.length; i++) {
                    int x1 = x + BOUNDARY_POINT_X_BOTTOM_LEFT[i];
                    int y1 = y + BOUNDARY_POINT_Y_BOTTOM_LEFT[i];
                    xBoundaryPointCoordinates[i] = (int) controlPoints[x1][y1].getXCoordinate();
                    yBoundaryPointCoordinates[i] = (int) controlPoints[x1][y1].getYCoordinate();
                }
            }
        }
        else {

            boundaryPointCount = BOUNDARY_POINT_X.length;

            // Add the Boundary Points
            for (int i = 0; i < BOUNDARY_POINT_X.length; i++) {
                int x1 = x + BOUNDARY_POINT_X[i];
                int y1 = y + BOUNDARY_POINT_Y[i];
                xBoundaryPointCoordinates[i] = (int) controlPoints[x1][y1].getXCoordinate();
                yBoundaryPointCoordinates[i] = (int) controlPoints[x1][y1].getYCoordinate();
            }
        }

        Polygon boundingBox = new Polygon(xBoundaryPointCoordinates, yBoundaryPointCoordinates, boundaryPointCount);
        return boundingBox.contains(p);
    }

    /*
     * Resets the Control Points Color.
     */
    public void resetControlPointsColor() {
        setControlPointsColor(controlPointsColor);
    }

    /*
     * Resets the Control Points Color and Location
     */
    public void resetControlPoints() {

        // Reset the Control Points Color
        resetControlPointsColor();

        // Reset the Control Points Location
        for (int xi = 1; xi < (columns + 1); xi++) {
            for (int yi = 1; yi < (rows + 1); yi++) {
                controlPoints[xi][yi].resetDefaultLocation();
            }
        }

        repaint();
    }

    /*
     * Resets the Control Points to Before the Preview.
     */
    public void resetPreviewControlPoints() {

        // Reset the Control Points Location to Before Preview
        for (int xi = 1; xi < (columns + 1); xi++) {
            for (int yi = 1; yi < (rows + 1); yi++) {
                controlPoints[xi][yi].resetPreviewLocation();
            }
        }

        repaint();
    }

    /*
     * Gets the Group Move Box.
     *
     * @return      The group move box
     */
    public Polygon getGroupMoveBox() {
        return groupMoveBox;
    }

    /*
     * Draws the Group Move Box.
     *
     * @param   start       The starting point of the box
     * @param   end         The ending point of the box
     */
    public void drawGroupMoveBox(Point start, Point end) {

        int x1 = (int) start.getX();
        int y1 = (int) start.getY();
        int x2 = (int) end.getX();
        int y2 = (int) end.getY();

        int x[] = {x1, x1, x2, x2};
        int y[] = {y1, y2, y2, y1};

        groupMoveBox = new Polygon(x, y, x.length);
        drawGroupMoveBox = true;

        repaint();
    }

    /*
     * Selects the Control Points in the Group Move Box.
     */
    public ControlPoint[] selectControlPointsInBox() {

        ControlPoint[] returnList = new ControlPoint[rows * columns];
        int i = 0;

        for (int xi = 1; xi < columns + 1; xi++) {
            for (int yi = 1; yi < rows + 1; yi++) {
                if (groupMoveBox.contains(controlPoints[xi][yi].getXCoordinate(), controlPoints[xi][yi].getYCoordinate())) {
                    controlPoints[xi][yi].changeColor(Color.ORANGE);
                    returnList[i] = controlPoints[xi][yi];
                    i++;
                }
            }
        }

        return returnList;
    }

    /*
     * Colors the Group Move Control Points
     *
     * @param   cp          The control points in the group move box
     */
    public void colorGroupControlPoints(ControlPoint cp[]) {
        int i = 0;
        while (i < cp.length && cp[i] != null) {
            controlPoints[cp[i].getX()][cp[i].getY()].changeColor(Color.ORANGE);
            i++;
        }
        repaint();
    }

    /*
     * Erases the Group Move Box.
     */
    public void eraseGroupMoveBox() {
        drawGroupMoveBox = false;
        repaint();
    }

    /*
     * Deletes the Group Move Box.
     */
    public void deleteGroupMoveBox() {
        groupMoveBox = null;
        drawGroupMoveBox = false;
        repaint();
    }

    /*
     * Draws the Grid Lines for the Image View.
     *
     * @param       g       The Graphics
     */
    private void drawConnectingLines(Graphics g) {

        for (int xi = 0; xi < (columns + 2); xi++) {

            for (int yi = 0; yi < (rows + 2); yi++) {
                int x = (int) controlPoints[xi][yi].getXCoordinate();
                int y = (int) controlPoints[xi][yi].getYCoordinate();

                // Draw Horizontal Connecting Lines
                if (xi < (columns + 1)) {
                    int xRight = (int) controlPoints[xi + 1][yi].getXCoordinate();
                    int yRight = (int) controlPoints[xi + 1][yi].getYCoordinate();

                    g.drawLine(x, y, xRight, yRight);
                }

                // Draw Vertical Connecting Lines
                if (yi < (rows  + 1)) {
                    int xDown = (int) controlPoints[xi][yi + 1].getXCoordinate();
                    int yDown = (int) controlPoints[xi][yi + 1].getYCoordinate();

                    g.drawLine(x, y, xDown, yDown);
                }

                // Draw Diagonal Connecting Lines
                if ((xi < (columns + 1)) && (yi < (rows + 1))) {

                    if (((xi == columns) && (yi == 0))
                        || ((xi == 0) && (yi == rows))) {
                        int x1 = (int) controlPoints[xi + 1][yi].getXCoordinate();
                        int y1 = (int) controlPoints[xi + 1][yi].getYCoordinate();

                        int x2 = (int) controlPoints[xi][yi + 1].getXCoordinate();
                        int y2 = (int) controlPoints[xi][yi + 1].getYCoordinate();

                        g.drawLine(x1, y1, x2, y2);

                    }
                    else {
                        int xDiagonal = (int) controlPoints[xi + 1][yi + 1].getXCoordinate();
                        int yDiagonal = (int) controlPoints[xi + 1][yi + 1].getYCoordinate();

                        g.drawLine(x, y, xDiagonal, yDiagonal);
                    }
                }
            }
        }
    }

    /*
     * Draws the Image View.
     *
     * @param       g       The Graphics
     */
    public void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        // Update the Color
        g.setColor(controlPointsColor);

        g2d.drawImage(bim, 0, 0, this);

        // Draw the Group Move Box if Created
        if (drawGroupMoveBox) {
            g.drawPolygon(groupMoveBox);
        }

        if (drawControlPoints) {
            // Draw the Grid Lines
            drawConnectingLines(g);

            // Draw the Control Points
            for (int xi = 1; xi < (columns + 1); xi++) {
                for (int yi = 1; yi < (rows + 1); yi++) {
                    controlPoints[xi][yi].drawControlPoint(g);
                }
            }
        }

    }

    /*
     * Creates a Deep Copy of a Buffered Image.
     *
     * @param   bi      The BufferedImage to copy
     * @return          The copy
     */
    static public BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

}
