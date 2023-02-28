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
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AvailableTeleopTrajectories {

    private enum CONVERGENCE_POINT {
        NEAR(0), // Point closest to the opposing alliance load station
        MIDDLE(1), // Point in middle of the field, close to the charge station
        FAR(2); // Point farthest from the opposing alliance load station

        int index;

        CONVERGENCE_POINT(int index) {
            this.index = index;
        }
    }

    /*
     * key: y-value of start point
     * value: command from start point to convergence point
     */
    private static HashMap<Double, Command> startToConvergenceTrajMap = new HashMap<Double, Command>();

    /*
     * key: y-value of start point
     * value: x-value of start point 
     */
    private static HashMap<Double, Double> xMap = new HashMap<Double, Double>();

    /*
     * key: y-value of start point
     * value: convergence point
     */
    private static HashMap<Double, CONVERGENCE_POINT> yToConvergenceMap = new HashMap<Double, CONVERGENCE_POINT>();

    private static int numberColumns = 9;
    /*
     * row: convergence point index
     * col: corresponding auto to get to grid column
     */
    private static Command[][] convergenceToColumnTraj;

    // Array of starting y values of start point
    private static Double[] yVals;

    public static void generateTrajectories() {
        /*
         * Add all teleop start point trajectories, can be in any order. 
         * To add another start to convergence trajectory simply do
         * addStartTrajectory(fileName, CONVERGENCE_POINT)
        */ 
        addStartTrajectory("enterCommunity 4.5", CONVERGENCE_POINT.NEAR);
        addStartTrajectory("enterCommunity 4.65", CONVERGENCE_POINT.NEAR);
        addStartTrajectory("enterCommunity 4.85", CONVERGENCE_POINT.NEAR);

        // Add all convergence to column trajectories
        convergenceToColumnTraj = new Command[CONVERGENCE_POINT.values().length][numberColumns];

        addConvergenceToColumnTrajectory("from NEAR to pos0", CONVERGENCE_POINT.NEAR, 0);
        addConvergenceToColumnTrajectory("from NEAR to pos1", CONVERGENCE_POINT.NEAR, 1);
        addConvergenceToColumnTrajectory("from NEAR to pos2", CONVERGENCE_POINT.NEAR, 2);
        addConvergenceToColumnTrajectory("from NEAR to pos3", CONVERGENCE_POINT.NEAR, 3);
        addConvergenceToColumnTrajectory("from NEAR to pos4", CONVERGENCE_POINT.NEAR, 4);
        addConvergenceToColumnTrajectory("from NEAR to pos5", CONVERGENCE_POINT.NEAR, 5);
        addConvergenceToColumnTrajectory("from NEAR to pos6", CONVERGENCE_POINT.NEAR, 6);
        addConvergenceToColumnTrajectory("from NEAR to pos7", CONVERGENCE_POINT.NEAR, 7);
        addConvergenceToColumnTrajectory("from NEAR to pos8", CONVERGENCE_POINT.NEAR, 8);

        // Load hashmap keys into sorted array for efficient searching
        yVals = new Double[startToConvergenceTrajMap.keySet().size()];
        startToConvergenceTrajMap.keySet().toArray(yVals);
        Arrays.sort(yVals);
    }

    private static void addStartTrajectory(String fileName, CONVERGENCE_POINT toConvergencePoint) {
        PathPlannerTrajectory trajectory = TrajectoryUtil.createPathPlannerTrajectory(fileName);
        Command TrajCmd = TrajectoryUtil.createDrivePathCommand(trajectory, false);
        PathPlannerState initialState = PathPlannerTrajectory.transformStateForAlliance(trajectory.getInitialState(),
                DriverStationAllianceGetter.getAlliance());
        double yVal = initialState.poseMeters.getY();

        startToConvergenceTrajMap.put(yVal, TrajCmd);
        xMap.put(yVal, initialState.poseMeters.getX());
        yToConvergenceMap.put(yVal, toConvergencePoint);
    }

    private static void addConvergenceToColumnTrajectory(String fileName, CONVERGENCE_POINT fromConvergencePoint, int col) {
        Command trajectoryCmd = TrajectoryUtil.createTrajectoryPathCommand(fileName, false);
       
        convergenceToColumnTraj[fromConvergencePoint.index][col] = trajectoryCmd;
    }

    /**
     * 
     * @param col Column from 0-8 on the grid
     * @param pose Current robot pose
     * @return A command to run to get to column
     */
    public static Command BuildTrajectory(int col, Pose2d pose) {
        double startKey = getTrajectoryKey(pose);
        Command startCommand = startToConvergenceTrajMap.get(startKey);

        CONVERGENCE_POINT convergencePoint = yToConvergenceMap.get(startKey);
        Command toColCommand = convergenceToColumnTraj[convergencePoint.index][col];

        return new SequentialCommandGroup(startCommand, toColCommand);
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

        // Binary search to find two closest keys
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