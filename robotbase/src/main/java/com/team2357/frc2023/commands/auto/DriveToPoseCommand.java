package com.team2357.frc2023.commands.auto;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;
import java.util.function.Supplier;

import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
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
    private final Pose2d m_initialPose;

    private boolean m_running = false;

    private final ProfiledPIDController m_driveController;
    private final ProfiledPIDController m_thetaController;

    /**
     * @param targetPose          The pose to reach relative to the target ex: The
     *                            pose to be at to score on the middle row relative
     *                            to the april-tag in fron of it
     */
    public DriveToPoseCommand(Pose2d initialPose, Pose2d targetPose) {
        m_swerve = SwerveDriveSubsystem.getInstance();
        m_targetPose = targetPose;
        m_initialPose = initialPose;
        addRequirements(m_swerve);

        m_driveController = m_swerve.getAutoAlignDriveController();

        m_thetaController = m_swerve.getAutoAlignThetaController();
        m_thetaController.enableContinuousInput(-Math.PI, Math.PI);
    }

    @Override
    public void initialize() {
        // Reset all controllers

        m_swerve.resetPoseEstimator(new Pose2d(m_initialPose.getX(), m_initialPose.getY(), m_swerve.getGyroscopeRotation()));

        var currentPose = m_swerve.getPose();
        m_driveController.reset(
                new TrapezoidProfile.State(
                    currentPose.getTranslation().getDistance(m_targetPose.getTranslation()),
                    -new Translation2d(m_swerve.getFieldVelocity().dx, m_swerve.getFieldVelocity().dy)
                        .rotateBy(
                            m_targetPose
                                .getTranslation()
                                .minus(m_swerve.getPose().getTranslation())
                                .getAngle()
                                .unaryMinus())
                        .getX()));
            
        m_thetaController.reset(currentPose.getRotation().getRadians());
    }

    @Override
    public void execute() {
        m_running = true;

        // Get current and target pose
        Pose2d currentPose = m_swerve.getPose();

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

        System.out.println(" X: " + driveVelocity.getX() + " Y: " + driveVelocity.getY() + " rot: " + thetaVelocity);
        m_swerve.drive(
        ChassisSpeeds.fromFieldRelativeSpeeds(
        driveVelocity.getX(), driveVelocity.getY(), thetaVelocity,
        m_swerve.getGyroscopeRotation()));
    }

    @Override
    public void end(boolean interrupted) {
        m_running = false;
        m_swerve.drive(new ChassisSpeeds());
    }

    @Override
    public boolean isFinished() {
        return atGoal();
    }

    /** Checks if the robot is stopped at the final pose. */
    public boolean atGoal() {
        return m_running && m_driveController.atGoal() && m_thetaController.atGoal();
    }

    /** Returns whether the command is actively running. */
    public boolean isRunning() {
        return m_running;
    }
}
