package com.team2357.frc2023.util;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AvailableTrajectories {
    public static SequentialCommandGroup figure8Trajectory;
    public static SequentialCommandGroup lineTrajectory;

    public static void generateTrajectories() {
        figure8Trajectory = createFigure8Trajectory();
        lineTrajectory = createLineTrajectory();
    }

    public static SequentialCommandGroup createFigure8Trajectory() {
        return TrajectoryUtil.createTrajectoryPathCommand("figure8", true);
    }

    public static SequentialCommandGroup createLineTrajectory() {
        return TrajectoryUtil.createTrajectoryPathCommand("Line", true);
    }
}
