package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.subsystems.LimelightSubsystem;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToTargetYCommand extends CommandBase {
    public PIDController m_pidController;
    public SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    public LimelightSubsystem m_limeLight = LimelightSubsystem.getInstance();
    public SwerveDriveSubsystem.Configuration m_swerveConfig = Constants.DRIVE.GET_SWERVE_DRIVE_CONFIG();

    public TranslateToTargetYCommand() {
        m_pidController = m_swerveConfig.m_translateYController;
        m_pidController.setTolerance(0.4);

        addRequirements(m_swerve, m_limeLight);
    }

    @Override
    public void initialize() {
        m_pidController.reset();
        m_pidController.setSetpoint(0);
    }

    @Override
    public void execute() {
        double newSpeed = m_pidController.calculate(m_limeLight.getTX());
        newSpeed = newSpeed * m_swerveConfig.m_translateYMaxSpeedMeters;
        m_swerve.drive(0, newSpeed, 0);
    }

    @Override
    public boolean isFinished() {
        return m_pidController.atSetpoint() && (0 < m_limeLight.getTV());
    }

    @Override
    public void end(boolean isInterrupted) {
        m_swerve.drive(0, 0, 0);
    }
}