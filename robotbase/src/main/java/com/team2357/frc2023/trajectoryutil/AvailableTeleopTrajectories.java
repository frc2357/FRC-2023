package com.team2357.frc2023.trajectoryutil;

import java.util.Arrays;
import java.util.HashMap;

import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPlannerTrajectory.PathPlannerState;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.util.DriverStationAllianceGetter;
import com.team2357.lib.util.Utility;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class AvailableTeleopTrajectories {

    private static HashMap<Double, Command> trajMap = new HashMap<Double, Command>();
    private static HashMap<Double, Double> xMap = new HashMap<Double, Double>();
    private static Double[] yVals;

    public static void generateTrajectories() {
        // Add all teleop start point trajectories
        addTrajectory("enterCommunity 4.5");
        addTrajectory("enterCommunity 4.65");
        addTrajectory("enterCommunity 4.85");

        // Load hashmap keys into sorted array for efficient searching
        yVals = new Double[trajMap.keySet().size()];
        trajMap.keySet().toArray(yVals);
        Arrays.sort(yVals);
    }

    private static void addTrajectory(String fileName) {
        PathPlannerTrajectory trajectory = TrajectoryUtil.createPathPlannerTrajectory(fileName);
        Command TrajCmd = TrajectoryUtil.createDrivePathCommand(trajectory, false);
        PathPlannerState initialState = PathPlannerTrajectory.transformStateForAlliance(trajectory.getInitialState(),
                DriverStationAllianceGetter.getAlliance());
        double yVal = initialState.poseMeters.getY();

        trajMap.put(yVal, TrajCmd);
        xMap.put(yVal, initialState.poseMeters.getX());
    }

    /**
     * 
     * @param col Column from 0-8 on the grid
     * @param pose Current robot pose
     * @return A command to run to get to column
     */
    public static Command BuildTrajectory(int col, Pose2d pose) {
        double startKey = getTrajectoryKey(pose);
        Command startCommand = trajMap.get(startKey);
        return new WaitCommand(0);
    }

    /**
     * 
     * @param pose The robot's current position
     * @return The key for the start trajectory
     */
    public static double getTrajectoryKey(Pose2d pose) {

        // Robot pose values we are trying to match to.
        double robotYPos = pose.getY();
        double robotXPos = pose.getX();

        // Binary search setup
        int high = yVals.length-1;
        int low = 0, mid = 0;
        boolean isRobotGreaterThan = false;

        // Resulting key for hashmap
        double key = -1.0;

        while (low <= high) {
            mid = low + ((high - low) / 2);

            if (yVals[mid] < robotYPos) {
                low = mid + 1;
                isRobotGreaterThan = true;
            } else if (yVals[mid] > robotYPos) {
                high = mid - 1;
                isRobotGreaterThan = false;
            } else if (yVals[mid] == robotYPos) {
                key = yVals[mid];
                break;
            }
        }

        try {
            double nextClosest = yVals[mid + (isRobotGreaterThan ? 1 : -1)];

            if (Math.abs(nextClosest - robotYPos) < Math.abs(yVals[mid] - robotYPos)) {
                key = nextClosest;
            } else {
                key = yVals[mid];
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            // Means next closest element does not exist, use current mid value
            key = yVals[mid];
        }

        // If too far away, return -1
        if (!Utility.isWithinTolerance(robotYPos, key, Constants.DRIVE.TRAJECTORY_MAP_TOLERANCE_METERS)
                || !Utility.isWithinTolerance(robotXPos, xMap.get(key), Constants.DRIVE.TRAJECTORY_MAP_TOLERANCE_METERS)) {
            return -1.0;
        }

        return key;
    }
}
