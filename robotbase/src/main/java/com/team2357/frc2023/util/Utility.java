package com.team2357.frc2023.util;

import com.team2357.frc2023.networktables.Buttonboard;

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
}
