package com.team2357.frc2023.util;

import com.team2357.frc2023.networktables.Buttonboard;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class Utility {
    /**
     * 
     * @param column The column from 0-8 with 0 being closest to opposing alliance
     * @return The id of the april tag on the grid
     */
    public static int gridColumnToAprilTagID(int column) {
        switch (column / 3) {
            case 0:
                return Buttonboard.getInstance().getAlliance() == Alliance.Blue ? 8 : 1;
            case 1:
                return Buttonboard.getInstance().getAlliance() == Alliance.Blue ? 7 : 2;
            case 2:
                return Buttonboard.getInstance().getAlliance() == Alliance.Blue ? 6 : 3;
            default:
                return -1;
        }
    }

    
  /**
   * From: https://github.com/Mechanical-Advantage/RobotCode2023/blob/main/src/main/java/org/littletonrobotics/frc2023/util/GeomUtil.java
   * Creates a pure translating transform
   *
   * @param x The x componenet of the translation
   * @param y The y componenet of the translation
   * @return The resulting transform
   */
  public static Transform2d translationToTransform(double x, double y) {
    return new Transform2d(new Translation2d(x, y), new Rotation2d());
  }
}
