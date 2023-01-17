package com.team2357.frc2023.util;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPlannerTrajectory.PathPlannerState;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TrajectoryUtil {

    public static SequentialCommandGroup createTrajectoryPathCommand(final boolean shouldResetOdometry, String trajectoryFileName) {

		// final Trajectory trajectory = generateTrajectory(waypoints);
		final PathPlannerTrajectory trajectory = PathPlanner.loadPath(trajectoryFileName, 2, 3);
		// double Seconds = 0.0;
		// System.out.println("===== Begin Sampling path =====");
		// while(trajectory.getTotalTimeSeconds() > Seconds) {
		// PathPlannerState state = (PathPlannerState) trajectory.sample(Seconds);
		// System.out.println(
		// "time: " + Seconds
		// + ", x: " + state.poseMeters.getX()
		// + ", y: " + state.poseMeters.getY()
		// + ", angle: " + state.poseMeters.getRotation().getDegrees()
		// + ", holo: " + state.holonomicRotation.getDegrees()
		// );
		// Seconds += 0.25;
		// }
		// System.out.println("===== End Sampling Path =====");
		return new InstantCommand(() -> {
			if (shouldResetOdometry) {
				PathPlannerState initialSample = (PathPlannerState) trajectory.sample(0);
				Pose2d initialPose = new Pose2d(initialSample.poseMeters.getTranslation(),
						initialSample.holonomicRotation);
				SwerveDriveSubsystem.getInstance().resetOdometry(initialPose);
			}
            SwerveDriveSubsystem.getInstance().getXController().reset();
            SwerveDriveSubsystem.getInstance().getYController().reset();
            }).andThen(new PPSwerveControllerCommand(
				trajectory,
				() -> SwerveDriveSubsystem.getInstance().getPose(),
				SwerveDriveSubsystem.getInstance().getKinematics(),
				SwerveDriveSubsystem.getInstance().getXController(),
				SwerveDriveSubsystem.getInstance().getYController(),
				SwerveDriveSubsystem.getInstance().getThetaController(),
				(SwerveModuleState[] moduleStates) -> {
					SwerveDriveSubsystem.getInstance().drive(SwerveDriveSubsystem.getInstance().getKinematics().toChassisSpeeds(moduleStates));
				},
				SwerveDriveSubsystem.getInstance())).andThen(() -> SwerveDriveSubsystem.getInstance().drive(new ChassisSpeeds()), SwerveDriveSubsystem.getInstance());
	}
}
