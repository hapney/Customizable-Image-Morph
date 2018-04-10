/*
 * Author: Sydney Norman
 * Date: December 7, 2017
 * Project: Image Morph
 *
 * This Program allows a user to specify control points on a starting and ending
 * image and illustrate the morphing between the two images.
 *
 */

import java.awt.*;

/*
 * Handles Everything for Individual Control Points
 */
public class ControlPoint {

    private static final int CONTROL_POINT_WIDTH = 8;

    // Position of Control Point In Grid
    private int xPosition;
    private int yPosition;

    // Default Center Point Coordinate Location
    private double defaultXCoordinate;
    private double defaultYCoordinate;

    // Control Center Point Coordinate
    private double xCenterCoordinate;
    private double yCenterCoordinate;

    // Control Center Point Preview Start Location
    private double previewStartXCoordinate;
    private double previewStartYCoordinate;

    // Surrounding Box Coordinate
    // Top Left, Top Right, Bottom Right, Bottom Left
    private int xBoxCoordinates[] = new int[4];
    private int yBoxCoordinates[] = new int[4];

    private Color color;

    // Control Point Handle
    private Polygon square;

    private boolean isBorder;
    private int imageWidth;
    private int numberOfColumns, numberOfRows;

    /*
     * Constructor for the Control Point Class.
     */
    public ControlPoint(int x, int y, int imageWidth, int numberOfColumns, int numberOfRows, boolean isBorder) {

        // Update isBorder Value
        this.isBorder = isBorder;

        this.imageWidth = imageWidth;
        this.numberOfColumns = numberOfColumns;
        this.numberOfRows = numberOfRows;

        // Update the Position
        xPosition = x;
        yPosition = y;

        // Update the Control Point Coordinate
        xCenterCoordinate = x * ((imageWidth - 1) / (numberOfColumns + 1.0));
        yCenterCoordinate = y * ((imageWidth - 1) / (numberOfRows + 1.0));

        previewStartXCoordinate = xCenterCoordinate;
        previewStartYCoordinate = yCenterCoordinate;

        // Update the Default Control Point Coordinate
        defaultXCoordinate = xCenterCoordinate;
        defaultYCoordinate = yCenterCoordinate;

        if (!isBorder) {

            // Update Control Point Square
            setSquare();
        }
    }

    /*
     * Creates the Control Point Square for the Object.
     */
    private void setSquare() {
        // Set Top Left Box Coordinate
        xBoxCoordinates[0] = (int)(xCenterCoordinate - (CONTROL_POINT_WIDTH / 2));
        yBoxCoordinates[0] = (int)(yCenterCoordinate - (CONTROL_POINT_WIDTH / 2));

        // Set Top Right Box Coordinate
        xBoxCoordinates[1] = (int)(xCenterCoordinate + (CONTROL_POINT_WIDTH / 2));
        yBoxCoordinates[1] = (int)(yCenterCoordinate - (CONTROL_POINT_WIDTH / 2));

        // Set Bottom Right Box Coordinate
        xBoxCoordinates[2] = (int)(xCenterCoordinate + (CONTROL_POINT_WIDTH / 2));
        yBoxCoordinates[2] = (int)(yCenterCoordinate + (CONTROL_POINT_WIDTH / 2));

        // Set Bottom Left Box Coordinate
        xBoxCoordinates[3] = (int)(xCenterCoordinate - (CONTROL_POINT_WIDTH / 2));
        yBoxCoordinates[3] = (int)(yCenterCoordinate + (CONTROL_POINT_WIDTH / 2));

        // Create the Control Point Square
        square = new Polygon(xBoxCoordinates, yBoxCoordinates, xBoxCoordinates.length);
    }

    /*
     * Changes the Control Point Location.
     *
     * @param   x       The X-Coordinate of the Control Point
     * @param   y       The Y-Coordinate of the Control Point
     */
    public void changePoint(double x, double y) {

        if (!isBorder) {

            // Update Center Point
            xCenterCoordinate = x;
            yCenterCoordinate = y;

            previewStartXCoordinate = x;
            previewStartYCoordinate = y;

            // Update Control Point Square
            setSquare();
        }
    }

    /*
     * Changes the Control Point Location During the Preview.
     *
     * @param   x       The X-Coordinate of the Control Point
     * @param   y       The Y-Coordinate of the Control Point
     */
    public void changePreviewPoint(double x, double y) {

        // Update Center Point
        xCenterCoordinate = x;
        yCenterCoordinate = y;

        // Update Control Point Square
        setSquare();
    }

    /*
     * Change the Color of the Control Point.
     *
     * @param   c       The New Color of the Control Point
     */
    public void changeColor(Color c) {
        color = c;
    }

    /*
     * Retrieves the X Position of the Control Point in the Grid.
     *
     * @return      The X Position in the Grid
     */
    public int getX() {
        return xPosition;
    }

    /*
     * Retrieves the Y Position of the Control Point in the Grid.
     *
     * @return      The Y Position in the Grid
     */
    public int getY() {
        return yPosition;
    }

    /*
     * Retrieves the X Coordinate of the Control Point in the Grid.
     *
     * @return      The X Coordinate of the Control Point
     */
    public double getXCoordinate() {
        return xCenterCoordinate;
    }

    /*
     * Retrieves the Y Coordinate of the Control Point in the Grid.
     *
     * @return      The Y Coordinate of the Control Point
     */
    public double getYCoordinate() {
        return yCenterCoordinate;
    }

    /*
     * Resets the Location of the Control Point to the Default Location.
     */
    public void resetDefaultLocation() {
        changePoint(defaultXCoordinate, defaultYCoordinate);
    }

    /*
     * Resets the Location of the Control Point to Before the Preview.
     */
    public void resetPreviewLocation() {
        changePoint(previewStartXCoordinate, previewStartYCoordinate);
    }

    /*
     * Gets the X-Coordinate from Before the Preview.
     *
     * @return      The X-Coordinate
     */
    public double getPreviewStartXCoordinate() {
        return previewStartXCoordinate;
    }

    /*
     * Gets the Y-Coordinate from Before the Preview.
     *
     * @return      The Y-Coordinate
     */
    public double getPreviewStartYCoordinate() {
        return previewStartYCoordinate;
    }

    /*
     * Returns the Control Point Square.
     *
     * @return      The Control Point Polygon Square
     */
    public Polygon getControlPointSquare() {
        return square;
    }

    /*
     * Draws the Control Point and the Connecting Lines.
     *
     * @return      The Graphics
     */
    public void drawControlPoint(Graphics g) {

        g.setColor(color);

        if (!isBorder) {
            // Draw the Control Point Itself
            g.drawPolygon(square);
            g.fillPolygon(square);
        }
    }

    /*
     * Duplicates the Control Point.
     *
     * @return      The duplicated control point
     */
    public ControlPoint duplicate() {
        ControlPoint controlPoint = new ControlPoint(xPosition, yPosition, imageWidth, numberOfColumns, numberOfRows, isBorder);
        controlPoint.changePoint(xCenterCoordinate, yCenterCoordinate);
        return controlPoint;
    }
}
