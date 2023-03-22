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

public class DriveToPoseCommand extends CommandBase {
  private final SwerveDriveSubsystem m_swerve;
  private final Supplier<Pose2d> m_poseSupplier;

  private boolean m_running = false;

  private final ProfiledPIDController m_driveController =
      new ProfiledPIDController(
          0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(0.0, 0.0));
  private final ProfiledPIDController m_thetaController =
      new ProfiledPIDController(
          0.0, 0.0, 0.0, new TrapezoidProfile.Constraints(0.0, 0.0));

  /** Drives to the specified pose under full software control. */
  public DriveToPoseCommand(Pose2d targetPose) {
    this(() -> targetPose);
  }

  /** Drives to the specified pose under full software control. */
  public DriveToPoseCommand(Supplier<Pose2d> targetPoseSupplier) {
    m_swerve = SwerveDriveSubsystem.getInstance();
    m_poseSupplier = targetPoseSupplier;
    addRequirements(m_swerve);
    m_thetaController.enableContinuousInput(-Math.PI, Math.PI);
  }

  @Override
  public void initialize() {
    // Reset all controllers
    var currentPose = m_swerve.getPose();
    m_driveController.reset(
        currentPose.getTranslation().getDistance(m_poseSupplier.get().getTranslation()));
    m_thetaController.reset(currentPose.getRotation().getRadians());
  }

  @Override
  public void execute() {
    m_running = true;

    // Get current and target pose
    var currentPose = m_swerve.getPose();
    var targetPose = m_poseSupplier.get();

    // Calculate drive speed
    double currentDistance =
        currentPose.getTranslation().getDistance(m_poseSupplier.get().getTranslation());
    double driveErrorAbs = currentDistance;
    double driveVelocityScalar = m_driveController.calculate(driveErrorAbs, 0.0);
    if (m_driveController.atGoal()) driveVelocityScalar = 0.0;

    // Calculate theta speed
    double thetaVelocity =
        m_thetaController.calculate(
            currentPose.getRotation().getRadians(), targetPose.getRotation().getRadians());
    if (m_thetaController.atGoal()) thetaVelocity = 0.0;

    // Command speeds
    var driveVelocity =
        new Pose2d(
                new Translation2d(),
                currentPose.getTranslation().minus(targetPose.getTranslation()).getAngle())
            .transformBy(Utility.translationToTransform(driveVelocityScalar, 0.0))
            .getTranslation();
    m_swerve.drive(
        ChassisSpeeds.fromFieldRelativeSpeeds(
            driveVelocity.getX(), driveVelocity.getY(), thetaVelocity, m_swerve.getGyroscopeRotation()));
  }

  @Override
  public void end(boolean interrupted) {
    m_running = false;
    m_swerve.drive(new ChassisSpeeds());;
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
