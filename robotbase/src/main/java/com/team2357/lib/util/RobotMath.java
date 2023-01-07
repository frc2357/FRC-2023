package com.team2357.lib.util;

/**
 * This utility class contains useful mathematical functions for robot
 * calculations.
 */
public class RobotMath {

  /**
   * Convert degrees of turning to inches of travel.
   *
   * To start, visualize the center point of the robot with a horizontal line, and
   * another line rotated by the degrees desired to turn (counter-clockwise in
   * this case)
   * Using 'w' for where the wheels are now, and 'W' for where we want them to be,
   * we can draw two supplementary angles towards the arc of the wheels.
   *
   *                 /W\
   *                /   |
   *         w----------w
   *         |   /
   *          \W/
   *
   * The arcs are the desired measurement in inches.
   * The radius of the arcs are 1/2 the wheelbase width.
   * So the desired value can be expressed in a fraction of rotations
   *
   * @param degrees              The rotation desired, positive is clockwise,
   *                             negative is counter-clockwise
   * @param wheelbaseWidthInches The width between the centers of the wheels.
   * @return The differential in inches for the desired angle.
   */
  // Switched asignment fractionalRotation from 360 / degrees to degrees / 360.
  public static double turnDegreesToInches(
    double degrees,
    double wheelbaseWidthInches
  ) {
    double circumferenceInches = Math.PI * wheelbaseWidthInches;
    double fractionalRotation = degrees / 360;
    double turnDistanceInches = circumferenceInches * fractionalRotation;
    return turnDistanceInches;
  }

  /**
   * Convert turning of inches travel to degrees.
   *
   * @see #turnDegreesToInches(double, double)
   *
   * @param inches
   * @param wheelbaseWidthInches
   * @return
   */
  // Switched asignment fractionalRotation from circumferenceInches / inches to
  // inches / circumference inches.
  public static double turnInchesToDegrees(
    double inches,
    double wheelbaseWidthInches
  ) {
    double circumferenceInches = Math.PI * wheelbaseWidthInches;
    double fractionalRotation = inches / circumferenceInches;
    double degrees = fractionalRotation * 360;
    return degrees;
  }

  /**
   * Function to linearly interpolate a value. Will find the two points
   * on with the closest "x" value and do the linear interpolation
   * formula from there
   *
   * Formula:
   * y = y1 + ((x – x1) / (x2 – x1)) * (y2 – y1)
   *
   * @param y2    y2
   * @param y1    y1
   * @param x2    x2
   * @param x1    x1
   * @param x     x
   * @param index the index of the row of the 2d curve array
   * @return The unknown value on the curve point (y)
   */
  public static double lineralyInterpolate(
    double y2,
    double y1,
    double x2,
    double x1,
    double x
  ) {
    double factor = (x - x1) / (x2 - x1);
    return ((y2 - y1) * factor) + y1;
  }

  /**
   * Finds the index of the lower side of the given x value
   *
   * @param curve The 2D array of values to interpolate on. The first element of
   *              the 2nd set of arrays should be x, while the second element is y
   *              Ex. curve[0][0] = known x, curve[0][1] = unknown y
   *
   * @param x     The known value on the current curve point (x)
   * @return The index of the curve array element closest to the given x value
   *         that is less than x
   */
  public static int getCurveSegmentIndex(double[][] curve, double x) {
    int segmentIndex = -1;
    for (int i = 0; i < curve.length - 1; i++) {
      if (curve[i][0] > x && curve[i + 1][0] < x) {
        segmentIndex = i;
        break;
      }
    }
    return segmentIndex;
  }
}
