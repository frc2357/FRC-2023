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
        addStartTrajectory("4.5Y to NEAR", CONVERGENCE_POINT.NEAR);
        addStartTrajectory("4.65Y to NEAR", CONVERGENCE_POINT.NEAR);
        addStartTrajectory("4.85Y to NEAR", CONVERGENCE_POINT.NEAR);

        addStartTrajectory("1.99Y to MIDDLE", CONVERGENCE_POINT.MIDDLE);
        addStartTrajectory("2.8Y to MIDDLE", CONVERGENCE_POINT.MIDDLE);
        addStartTrajectory("2.24Y to MIDDLE", CONVERGENCE_POINT.MIDDLE);
        addStartTrajectory("3.23Y to MIDDLE", CONVERGENCE_POINT.MIDDLE);
        addStartTrajectory("3.44Y to MIDDLE", CONVERGENCE_POINT.MIDDLE);

        addStartTrajectory("1.00Y to FAR", CONVERGENCE_POINT.FAR);
        addStartTrajectory("0.75Y to FAR", CONVERGENCE_POINT.FAR);
        addStartTrajectory("0.50Y to FAR", CONVERGENCE_POINT.FAR);

        // Add all convergence to column trajectories
        convergenceToColumnTraj = new Command[CONVERGENCE_POINT.values().length][numberColumns];

        addConvergenceToColumnTrajectory("NEAR to node 0", CONVERGENCE_POINT.NEAR, 0);
        addConvergenceToColumnTrajectory("NEAR to node 1", CONVERGENCE_POINT.NEAR, 1);
        addConvergenceToColumnTrajectory("NEAR to node 2", CONVERGENCE_POINT.NEAR, 2);
        addConvergenceToColumnTrajectory("NEAR to node 3", CONVERGENCE_POINT.NEAR, 3);
        addConvergenceToColumnTrajectory("NEAR to node 4", CONVERGENCE_POINT.NEAR, 4);
        addConvergenceToColumnTrajectory("NEAR to node 5", CONVERGENCE_POINT.NEAR, 5);
        addConvergenceToColumnTrajectory("NEAR to node 6", CONVERGENCE_POINT.NEAR, 6);
        addConvergenceToColumnTrajectory("NEAR to node 7", CONVERGENCE_POINT.NEAR, 7);
        addConvergenceToColumnTrajectory("NEAR to node 8", CONVERGENCE_POINT.NEAR, 8);

        addConvergenceToColumnTrajectory("MIDDLE to node 0", CONVERGENCE_POINT.MIDDLE, 0);
        addConvergenceToColumnTrajectory("MIDDLE to node 1", CONVERGENCE_POINT.MIDDLE, 1);
        addConvergenceToColumnTrajectory("MIDDLE to node 2", CONVERGENCE_POINT.MIDDLE, 2);
        addConvergenceToColumnTrajectory("MIDDLE to node 3", CONVERGENCE_POINT.MIDDLE, 3);
        addConvergenceToColumnTrajectory("MIDDLE to node 4", CONVERGENCE_POINT.MIDDLE, 4);
        addConvergenceToColumnTrajectory("MIDDLE to node 5", CONVERGENCE_POINT.MIDDLE, 5);
        addConvergenceToColumnTrajectory("MIDDLE to node 6", CONVERGENCE_POINT.MIDDLE, 6);
        addConvergenceToColumnTrajectory("MIDDLE to node 7", CONVERGENCE_POINT.MIDDLE, 7);
        addConvergenceToColumnTrajectory("MIDDLE to node 8", CONVERGENCE_POINT.MIDDLE, 8);

        addConvergenceToColumnTrajectory("FAR to node 0", CONVERGENCE_POINT.MIDDLE, 0);
        addConvergenceToColumnTrajectory("FAR to node 1", CONVERGENCE_POINT.MIDDLE, 1);
        addConvergenceToColumnTrajectory("FAR to node 2", CONVERGENCE_POINT.MIDDLE, 2);
        addConvergenceToColumnTrajectory("FAR to node 3", CONVERGENCE_POINT.MIDDLE, 3);
        addConvergenceToColumnTrajectory("FAR to node 4", CONVERGENCE_POINT.MIDDLE, 4);
        addConvergenceToColumnTrajectory("FAR to node 5", CONVERGENCE_POINT.MIDDLE, 5);
        addConvergenceToColumnTrajectory("FAR to node 6", CONVERGENCE_POINT.MIDDLE, 6);
        addConvergenceToColumnTrajectory("FAR to node 7", CONVERGENCE_POINT.MIDDLE, 7);
        addConvergenceToColumnTrajectory("FAR to node 8", CONVERGENCE_POINT.MIDDLE, 8);

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
        
        if(startKey == -1) {
            return null;
        }
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
