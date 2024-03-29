package com.team2357.frc2023.trajectoryutil;

import java.util.ArrayList;

import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.PathPlannerTrajectory.PathPlannerState;
import com.pathplanner.lib.PathPoint;
import com.pathplanner.lib.commands.PPSwerveControllerCommand;
import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class TrajectoryUtil {

	public static SequentialCommandGroup createTrajectoryPathCommand(String trajectoryFileName,
			final boolean resetOdometry) {
		return createDrivePathCommand(createPathPlannerTrajectory(trajectoryFileName), resetOdometry);
	}

	public static PathPlannerTrajectory createPathPlannerTrajectory(String trajectoryFileName) {
		return PathPlanner.loadPath(trajectoryFileName,
				Constants.DRIVE.DEFAULT_PATH_CONSTRAINTS);
	}

	public static SequentialCommandGroup createTrajectoryPathCommand(String trajectoryFileName,
			PathConstraints constraints,
			final boolean resetOdometry) {
		return createDrivePathCommand(createPathPlannerTrajectory(trajectoryFileName, constraints), resetOdometry);
	}

	public static PathPlannerTrajectory createPathPlannerTrajectory(String trajectoryFileName,
			PathConstraints constraints) {
		return PathPlanner.loadPath(trajectoryFileName, constraints);
	}

	public static SequentialCommandGroup CreateTwoPointTrajectoryPathCommand(Pose2d startPose, Pose2d endPose,
			final boolean resetOdometry) {
		ArrayList<PathPoint> points = new ArrayList<PathPoint>();

		PathPoint startPoint = new PathPoint(startPose.getTranslation(), startPose.getRotation(),
				startPose.getRotation());
		points.add(startPoint);

		PathPoint endPoint = new PathPoint(endPose.getTranslation(), endPose.getRotation(),
				endPose.getRotation());
		points.add(endPoint);

		PathPlannerTrajectory trajectory = PathPlanner.generatePath(
				Constants.DRIVE.DEFAULT_PATH_CONSTRAINTS, false, points);

		return createDrivePathCommand(trajectory, resetOdometry);
	}

	public static SequentialCommandGroup createDrivePathCommand(
			PathPlannerTrajectory trajectory, final boolean resetOdometry) {

		SwerveDriveSubsystem swerveDrive = SwerveDriveSubsystem.getInstance();

		SequentialCommandGroup pathCommand = new SequentialCommandGroup();
		pathCommand.addRequirements(swerveDrive);

		pathCommand.addCommands(new InstantCommand(() -> {
			if (resetOdometry) {
				PathPlannerState initialState = trajectory.getInitialState();
				initialState = PathPlannerTrajectory.transformStateForAlliance(initialState,
						DriverStation.getAlliance());
				Pose2d initialPose = new Pose2d(initialState.poseMeters.getTranslation(),
						initialState.holonomicRotation);
				swerveDrive.resetPoseEstimator(initialPose);
			}
			swerveDrive.getXController().reset();
			swerveDrive.getYController().reset();
			swerveDrive.setCurrentTrajectory(trajectory);
		}));

		pathCommand.addCommands(new PPSwerveControllerCommand(
				trajectory,
				() -> swerveDrive.getPose(),
				swerveDrive.getKinematics(),
				swerveDrive.getXController(),
				swerveDrive.getYController(),
				swerveDrive.getThetaController(),
				(SwerveModuleState[] moduleStates) -> {
					swerveDrive.drive(SwerveDriveSubsystem.getInstance().getKinematics().toChassisSpeeds(moduleStates));
				}, true));

		pathCommand.addCommands(new InstantCommand(() -> {
			swerveDrive.drive(new ChassisSpeeds());
			swerveDrive.endTrajectory();
		}));

		return pathCommand;
	}

	// Prints path to stdout
	public static void samplePath(PathPlannerTrajectory trajectory) {
		double Seconds = 0.0;
		System.out.println("===== Begin Sampling path =====");
		while (trajectory.getTotalTimeSeconds() > Seconds) {
			PathPlannerState state = (PathPlannerState) trajectory.sample(Seconds);
			System.out.println(
					"time: " + Seconds
							+ ", x: " + state.poseMeters.getX()
							+ ", y: " + state.poseMeters.getY()
							+ ", angle: " + state.poseMeters.getRotation().getDegrees()
							+ ", holo: " + state.holonomicRotation.getDegrees());
			Seconds += 0.1;
		}
		System.out.println("===== End Sampling Path =====");
	}

}
