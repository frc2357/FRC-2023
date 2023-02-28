package com.team2357.frc2023.util;

import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class Utility {
    /**
     * 
     * @param column The column from 0-8 with 0 being closest to opposing alliance
     *               loading station
     * @return The id of the april tag on the grid
     */
    public static int gridColumnToAprilTagID(int column) {
        switch (column / 2) {
            case 0:
                return DriverStationAllianceGetter.getAlliance() == Alliance.Blue ? 6 : 3;
            case 1:
                return DriverStationAllianceGetter.getAlliance() == Alliance.Blue ? 7 : 2;
            case 2:
                return DriverStationAllianceGetter.getAlliance() == Alliance.Blue ? 8 : 1;
            default:
                return -1;
        }
    }
}
