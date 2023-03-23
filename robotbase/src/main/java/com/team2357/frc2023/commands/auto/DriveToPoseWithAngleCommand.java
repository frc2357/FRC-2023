package com.team2357.frc2023.commands.auto;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;
import java.util.function.Supplier;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.util.Utility;

/**
 * Command to auto align the robot to a target pose based on the distance
 * between the target pose and the robot.
 * Uses odometry to determine target velocities for the robot to move to.
 * 
 * Heavily based on:
 * https://github.com/Mechanical-Advantage/RobotCode2023/blob/main/src/main/java/org/littletonrobotics/frc2023/commands/DriveToPose.java
 */
public class DriveToPoseWithAngleCommand extends CommandBase {
    private final SwerveDriveSubsystem m_swerve;
    private final double m_tXTarget;
    private final double m_tYTarget;
    private final Supplier<Double> m_tXCurrentSupplier;
    private final Supplier<Double> m_tYCurrentSupplier;

    private boolean m_running = false;

    private final ProfiledPIDController m_xDriveController;
    private final ProfiledPIDController m_yDriveController;
    private final ProfiledPIDController m_thetaController;

    /**
     * 
     * @param currentPoseSupplier A supplier to get the current pose relative to a
     *                            target ex: campose from limelight camera
     * @param targetPose          The pose to reach relative to the target ex: The
     *                            pose to be at to score on the middle row relative
     *                            to the april-tag in fron of it
     */
    public DriveToPoseWithAngleCommand(Supplier<Double> tXCurrentSupplier, Supplier<Double> tYCurrentSupplier,
            double tXTarget, double tYTarget) {
        m_swerve = SwerveDriveSubsystem.getInstance();

        m_tXTarget = tXTarget;
        m_tXCurrentSupplier = tXCurrentSupplier;
        m_tYTarget = tYTarget;
        m_tYCurrentSupplier = tYCurrentSupplier;

        // m_driveController = m_swerve.getAutoAlignDriveController();

        // m_thetaController = m_swerve.getAutoAlignThetaController();

        m_xDriveController = new ProfiledPIDController(
                0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(0.0, 0.0));

        m_yDriveController = new ProfiledPIDController(
                0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(0.0, 0.0));

        m_thetaController = new ProfiledPIDController(
                0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(0.0, 0.0));

        m_thetaController.enableContinuousInput(-Math.PI, Math.PI);

        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        // Reset all controllers
        m_xDriveController.reset(m_tYCurrentSupplier.get());
        m_yDriveController.reset(m_tXCurrentSupplier.get());
        m_thetaController.reset(m_swerve.getYaw());
    }

    @Override
    public void execute() {
        m_running = true;

        // Get current and target pose
        double tXCurrent = m_tXCurrentSupplier.get();
        double tYCurrent = m_tYCurrentSupplier.get();

        // Calculate drive speed
        double tYError = tYCurrent - m_tYTarget;
        double xVelMetersPerSecond = m_xDriveController.calculate(tYError, 0.0);
        if (m_xDriveController.atGoal())
            xVelMetersPerSecond = 0.0;

        double tXError = tXCurrent - m_tXTarget;
        double yVelMetersPerSecond = m_yDriveController.calculate(tXError, 0.0);
        if (m_xDriveController.atGoal())
            yVelMetersPerSecond = 0.0;
        
        // Calculate theta speed
        double thetaVelocity = m_thetaController.calculate(m_swerve.getYaw(), 0);
        if (m_thetaController.atGoal())
            thetaVelocity = 0.0;

        m_swerve.drive(
        ChassisSpeeds.fromFieldRelativeSpeeds(
            xVelMetersPerSecond, yVelMetersPerSecond, thetaVelocity,
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
        return m_yDriveController.atGoal() && m_xDriveController.atGoal() && m_thetaController.atGoal();
    }

    /** Returns whether the command is actively running. */
    public boolean isM_running() {
        return m_running;
    }
}
