package com.team2357.frc2023.util;

import java.util.Arrays;
import java.util.HashMap;

import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPlannerTrajectory.PathPlannerState;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;

public class AvailableTeleopTrajectories {
    
    private static HashMap<Double, Command> trajMap = new HashMap<Double, Command>();
    private static Double[] yVals;

    public static void generateTrajectories() {
        // Add all teleop start point trajectories
        addTrajectory("enterCommunity 4.5");
        addTrajectory("enterCommunity 4.65");
        addTrajectory("enterCommunity 4.85");

        // Load hashmap keys into sorted array for efficient searching
        yVals = (Double[]) trajMap.keySet().toArray();
        Arrays.sort(yVals);
    }

    private static void addTrajectory(String fileName) {
        PathPlannerTrajectory trajectory = TrajectoryUtil.createPathPlannerTrajectory(fileName);
        Command TrajCmd = TrajectoryUtil.createDrivePathCommand(trajectory, false);

        PathPlannerState initialState= PathPlannerTrajectory.transformStateForAlliance(trajectory.getInitialState(), DriverStationAllianceGetter.getAlliance());
        double yVal = initialState.poseMeters.getY();
        
        trajMap.put(yVal, TrajCmd);
    }

    /**
     * 
     * @param pose The robot's current position
     * @return The start trajectory for teleop autos
     */
    public static Command getStartTrajectory(Pose2d pose) {
     
        double matchYVal = pose.getY();
        int high = yVals.length;
        int low = 0;
        int key = -1;
        while (low <= high) {
            int mid = low  + ((high - low) / 2);
            if (yVals[mid] < matchYVal) {
                low = mid + 1;
            } else if (yVals[mid] > matchYVal) {
                high = mid - 1;
            } else if (yVals[mid] == matchYVal) {
                key = mid;
                break;
            }

        }
        
        if (key == -1) {
            DriverStation.reportWarning("Trajectory not found", null);
        }
    }
}
