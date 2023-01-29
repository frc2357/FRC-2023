package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.subsystems.LimelightSubsystem;
import com.team2357.lib.util.Utility;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToTargetXCommand extends CommandBase {
    public PIDController m_pidController;
    public SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    public LimelightSubsystem m_limeLight = LimelightSubsystem.getInstance();

    public TranslateToTargetXCommand() {
        m_pidController = Constants.DRIVE.TRANSLATE_TO_APRILTAG_X_CONTROLLER;
        addRequirements(m_swerve);
        addRequirements(m_limeLight);
    }

    @Override
    public void initialize() {
        m_pidController.reset();
        m_pidController.setSetpoint(-15);
        m_swerve.enableOpenLoopRamp();
    }

    @Override
    public void execute() {
        double newSpeed = m_pidController.calculate(m_limeLight.getTY());
        newSpeed = newSpeed * Constants.DRIVE.TRANSLATE_TO_TARGET_X_MAXSPEED;
        newSpeed = newSpeed*-1;
        m_swerve.drive(newSpeed, 0, 0);
    }

    @Override
    public boolean isFinished() {
        return Utility.isWithinTolerance(m_limeLight.getTY(), 0, 0.5);
    }

    @Override
    public void end(boolean isInterrupted) {
        m_swerve.disableOpenLoopRamp();
        m_swerve.drive(0, 0, 0);
    }
}