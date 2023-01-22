package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.util.Utility;
import com.team2357.frc2023.Constants;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateToDegree extends CommandBase {

    public SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    public double m_targetDegrees;
    public PIDController m_pidController;

    public RotateToDegree(double targetDegrees) {
        m_targetDegrees = targetDegrees;
        m_pidController = Constants.DRIVE.ROTATE_TO_TARGET_CONTROLLER;
        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_pidController.reset();
        m_pidController.setSetpoint(m_targetDegrees);
    }

    @Override
    public void execute() {
        double newSpeed = m_pidController
                .calculate(m_swerve.getGyroscopeRotation().getDegrees() * Constants.DRIVE.ROTATE_MAX_SPEED);
        m_swerve.drive(0, 0, newSpeed);
    }

    @Override
    public boolean isFinished() {
        System.out.println(m_swerve.getGyroscopeRotation().getDegrees() % 360);
        System.out.println(m_targetDegrees);
        return Utility.isWithinTolerance(m_swerve.getGyroscopeRotation().getDegrees() % 360, m_targetDegrees, 2);
    }

    @Override
    public void end(boolean isInterrupted) {
        m_swerve.drive(0, 0, 0);
    }
}
