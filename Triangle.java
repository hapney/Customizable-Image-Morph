/*
 * Author: Sydney Norman
 * Date: December 7, 2017
 * Project: Image Morph
 *
 * This Program allows a user to specify control points on a starting and ending
 * image and illustrate the morphing between the two images.
 *
 */

import java.awt.geom.*;

/*
 * Triangle Class.
 *
 * ** Code from Dr. Seales
 */
public class Triangle {

    private Point2D.Double tri[];

    /*
     * The Constructor for the Triangle Class.
     *
     * @param   x1      The x-coordinate of the first point
     * @param   y1      The y-coordinate of the first point
     * @param   x2      The x-coordinate of the second point
     * @param   y2      The y-coordinate of the second point
     * @param   x3      The x-coordinate of the third point
     * @param   y3      The y-coordinate of the third point
     */
    public Triangle (double x1, double y1, double x2, double y2, double x3, double y3) {
        tri = new Point2D.Double[3];
        tri[0] = new Point2D.Double (x1, y1);
        tri[1] = new Point2D.Double (x2, y2);
        tri[2] = new Point2D.Double (x3, y3);
    }

    /*
     * Gets the X Coordinate at a given triangle index.
     *
     * @param   index       The index to retrieve from
     * @return              The x-coordinate
     */
    public double getX (int index) {
        if ((index >= 0) && (index < 6))
            return (tri[index].getX());
        System.out.println("Index out of bounds in getX()");
        return(0.0);
    }

    /*
     * Gets the Y Coordinate at a given triangle index.
     *
     * @param   index       The index to retrieve from
     * @return              The y-coordinate
     */
    public double getY (int index) {
        if ((index >= 0) && (index < 6))
            return (tri[index].getY());
        System.out.println("Index out of bounds in getY()");
        return(0.0);
    }

}
