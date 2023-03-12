package com.team2357.frc2023.trajectoryutil;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AvailableTrajectories {
    public static SequentialCommandGroup figure8Trajectory;
    public static SequentialCommandGroup lineTrajectory;
    public static SequentialCommandGroup exampleTwoPointTrajectory;

    public static void generateTrajectories() {
        figure8Trajectory = createFigure8Trajectory();
        lineTrajectory = createLineTrajectory();
        exampleTwoPointTrajectory = createExampleTwoPointTrajectory();

    }
  
    public static SequentialCommandGroup createFigure8Trajectory() {
        return TrajectoryUtil.createTrajectoryPathCommand("figure8", true);
    }

    public static SequentialCommandGroup createLineTrajectory() {
        return TrajectoryUtil.createTrajectoryPathCommand("Normal line", true);
    }

    public static SequentialCommandGroup createExampleTwoPointTrajectory() {
        return TrajectoryUtil.CreateTwoPointTrajectoryPathCommand(new Pose2d(1, 1, Rotation2d.fromDegrees(0)),
                new Pose2d(2, 1, Rotation2d.fromDegrees(90)), true);
    }    
}
