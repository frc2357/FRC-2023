package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.util.Utility;
import com.team2357.frc2023.Constants;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateToDegree extends CommandBase {

    public SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    public double m_targetDegrees;
    public double m_currentAngle;
    public PIDController m_pidController;

    public RotateToDegree(double targetDegrees) {
        m_targetDegrees = targetDegrees;
        m_pidController = Constants.DRIVE.ROTATE_TO_TARGET_CONTROLLER;
        m_pidController.enableContinuousInput(0, 360);
        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_pidController.reset();

        double currentAngle = Math.abs(m_swerve.getGyroscopeRotation().getDegrees() % 360);
        double distance = currentAngle-m_targetDegrees;
        while (distance < -180) {
            distance +=360;
        }
        while(distance >180){
            distance-=360;
        }
        m_pidController.setSetpoint(m_swerve.getGyroscopeRotation().getDegrees()+distance);
    }

    @Override
    public void execute() {
        double newSpeed = m_pidController.calculate(m_swerve.getGyroscopeRotation().getDegrees());
        m_swerve.drive(0, 0, newSpeed);
        System.out.println("Target angle: " + m_targetDegrees);
        System.out.println("Current angle: " + Math.abs(m_swerve.getGyroscopeRotation().getDegrees() % 360));
        System.out.println("New speed: " + newSpeed);
    }

    @Override
    public boolean isFinished() {
        return Utility.isWithinTolerance(m_swerve.getGyroscopeRotation().getDegrees() % 360, m_targetDegrees, 1);
    }

    @Override
    public void end(boolean isInterrupted) {
        m_swerve.drive(0, 0, 0);
    }
}
