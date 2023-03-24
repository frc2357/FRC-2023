package com.team2357.frc2023.commands.auto;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj2.command.CommandBase;

import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem.LIMELIGHT;
import com.team2357.lib.subsystems.LimelightSubsystem;

/**
 * Command to auto align the robot to a target x, and y from a limelight
 * The units used are in degrees relative to the limelight. Ex: target is x degrees and velocity is x degrees per second
 *
 */
public class DriveToPoseWithAngleCommand extends CommandBase {
    private final SwerveDriveSubsystem m_swerve;
    private final double m_tXTarget;
    private final double m_tYTarget;
    
    private boolean m_running = false;

    private final ProfiledPIDController m_xDriveController;
    private final ProfiledPIDController m_yDriveController;
    private final ProfiledPIDController m_thetaController;

    private final LimelightSubsystem m_limelight;

    /**
     * 
     * @param currentPoseSupplier A supplier to get the current pose relative to a
     *                            target ex: campose from limelight camera
     * @param targetPose          The pose to reach relative to the target ex: The
     *                            pose to be at to score on the middle row relative
     *                            to the april-tag in fron of it
     */
    public DriveToPoseWithAngleCommand(
            double tXTarget, double tYTarget) {
        m_swerve = SwerveDriveSubsystem.getInstance();

        m_tXTarget = tXTarget;
        m_tYTarget = tYTarget;

        // m_driveController = m_swerve.getAutoAlignDriveController();

        // m_thetaController = m_swerve.getAutoAlignThetaController();

        m_xDriveController = new ProfiledPIDController(
                0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(1, 0.5));
        m_xDriveController.setTolerance(0.5);

        m_yDriveController = new ProfiledPIDController(
                0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(1, 0.5));
        m_yDriveController.setTolerance(0.5);

        m_thetaController = new ProfiledPIDController(
                0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(1, 0.5));
        m_thetaController.setTolerance(0.5);

        m_limelight = DualLimelightManagerSubsystem.getInstance().getLimelight(LIMELIGHT.RIGHT);

        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        // Reset all controllers
        DualLimelightManagerSubsystem.getInstance().setAprilTagPipelineActive();
        m_xDriveController.reset(m_limelight.getTY());
        m_yDriveController.reset(m_limelight.getTX());
       
        m_thetaController.reset(m_swerve.getYaw());

        double currentAngle = m_swerve.getYaw() % 360;

        while (currentAngle < 0) {
            currentAngle += 360;
        }

        double distance = 180 - currentAngle; // target - current
        while (distance < -180) {
            distance += 360;
        }
        while (distance > 180) {
            distance -= 360;
        }

        m_thetaController.setGoal(m_swerve.getGyroscopeRotation().getDegrees() + distance);    
    }

    @Override
    public void execute() {
        m_running = true;

        // Get current and target pose
        double tXCurrent = m_limelight.getTX();
        double tYCurrent = m_limelight.getTY();

        // Calculate drive speed
        double tYError = tYCurrent - m_tYTarget;
        double xDegreesPerSecond = m_xDriveController.calculate(tYError, 0.0);
        if (m_xDriveController.atGoal())
            xDegreesPerSecond = 0.0;

        double tXError = tXCurrent - m_tXTarget;
        double yDegreesPerSecond = m_yDriveController.calculate(tXError, 0.0);
        if (m_yDriveController.atGoal())
            yDegreesPerSecond = 0.0;
        
        // Calculate theta speed
        double thetaDegreesPerSecond = m_thetaController.calculate(m_swerve.getYaw());
        if (m_thetaController.atGoal())
            thetaDegreesPerSecond = 0.0;

        System.out.println("X degrees per second: " + xDegreesPerSecond);

        double xMetersPerSecond = xDegreesPerSecond * 0; // Factor to go from degrees per second to meters per second
        double yMetersPerSecond = yDegreesPerSecond * 0; // Factor to go from degrees per second to meters per second
        double thetaRadiansPerSecond =  Math.toRadians(thetaDegreesPerSecond); // Factor to go from degrees per second to radians per second
        // m_swerve.drive(
        // ChassisSpeeds.fromFieldRelativeSpeeds(
        //     xMetersPerSecond, yMetersPerSecond, thetaVelocity,
        // m_swerve.getGyroscopeRotation()));
    }

    @Override
    public void end(boolean interrupted) {
        m_running = false;
        m_swerve.drive(new ChassisSpeeds());
    }

    @Override
    public boolean isFinished() {
        return atGoal() || !m_limelight.validTargetExists();
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
