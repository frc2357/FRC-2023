package com.team2357.frc2023.commands.auto;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import java.util.function.Supplier;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.util.Utility;

/**
 * Command to auto align the robot to a target pose based on the distance
 * between the target pose and the robot.
 * Uses odometry to determine target velocities for the robot to move to.
 * 
 * Heavily based on: https://github.com/Mechanical-Advantage/RobotCode2023/blob/main/src/main/java/org/littletonrobotics/frc2023/commands/DriveToPose.java
 */
public class DriveToPoseCommand extends CommandBase {
    private final SwerveDriveSubsystem m_swerve;
    private final Pose2d m_targetPose;
    private final Supplier<Pose2d> m_currentPoseSupplier;

    private boolean m_running = false;

    private final ProfiledPIDController m_driveController;
    private final ProfiledPIDController m_thetaController;

    /**
     * 
     * @param currentPoseSupplier A supplier to get the current pose relative to a
     *                            target ex: campose from limelight camera
     * @param targetPose          The pose to reach relative to the target ex: The
     *                            pose to be at to score on the middle row relative
     *                            to the april-tag in fron of it
     */
    public DriveToPoseCommand(Supplier<Pose2d> currentPoseSupplier, Pose2d targetPose) {
        m_swerve = SwerveDriveSubsystem.getInstance();
        m_targetPose = targetPose;
        addRequirements(m_swerve);

        m_driveController = m_swerve.getAutoAlignDriveController();

        m_thetaController = m_swerve.getAutoAlignThetaController();
        m_thetaController.enableContinuousInput(-Math.PI, Math.PI);

        m_currentPoseSupplier = currentPoseSupplier;
    }

    @Override
    public void initialize() {
        // Reset all controllers
        var currentPose = m_swerve.getPose();
        m_driveController.reset(
                currentPose.getTranslation().getDistance(m_targetPose.getTranslation()));
        m_thetaController.reset(currentPose.getRotation().getRadians());
    }

    @Override
    public void execute() {
        m_running = true;

        // Get current and target pose
        Pose2d currentPose = m_currentPoseSupplier.get();

        // Calculate drive speed
        double currentDistance = currentPose.getTranslation().getDistance(m_targetPose.getTranslation());
        double driveErrorAbs = currentDistance;
        double driveVelocityScalar = m_driveController.calculate(driveErrorAbs, 0.0);
        if (m_driveController.atGoal())
            driveVelocityScalar = 0.0;

        // Calculate theta speed
        double thetaVelocity = m_thetaController.calculate(
                currentPose.getRotation().getRadians(), m_targetPose.getRotation().getRadians());
        if (m_thetaController.atGoal())
            thetaVelocity = 0.0;

        // Command speeds
        var driveVelocity = new Pose2d(
                new Translation2d(),
                currentPose.getTranslation().minus(m_targetPose.getTranslation()).getAngle())
                .transformBy(Utility.translationToTransform(driveVelocityScalar, 0.0))
                .getTranslation();

        // m_swerve.drive(
        // ChassisSpeeds.fromFieldRelativeSpeeds(
        // driveVelocity.getX(), driveVelocity.getY(), thetaVelocity,
        // m_swerve.getGyroscopeRotation()));
    }

    @Override
    public void end(boolean interrupted) {
        m_running = false;
        m_swerve.drive(new ChassisSpeeds());
    }

    /** Checks if the robot is stopped at the final pose. */
    public boolean atGoal() {
        return m_running && m_driveController.atGoal() && m_thetaController.atGoal();
    }

    /** Returns whether the command is actively running. */
    public boolean isM_running() {
        return m_running;
    }
}
