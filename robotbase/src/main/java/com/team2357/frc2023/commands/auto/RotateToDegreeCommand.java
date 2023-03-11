package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateToDegreeCommand extends CommandBase {

    public SwerveDriveSubsystem m_swerve = SwerveDriveSubsystem.getInstance();
    public double m_targetDegrees;
    public PIDController m_pidController;

    public RotateToDegreeCommand(double targetDegrees) {
        m_targetDegrees = targetDegrees;
        m_pidController = Constants.DRIVE.ROTATE_TO_TARGET_CONTROLLER;

        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_pidController.reset();
        double currentAngle = m_swerve.getGyroscopeRotation().getDegrees() % 360;

        while (currentAngle < 0) {
            currentAngle += 360;
        }

        double distance = m_targetDegrees - currentAngle;
        while (distance < -180) {
            distance += 360;
        }
        while (distance > 180) {
            distance -= 360;
        }

        m_pidController.setSetpoint(m_swerve.getGyroscopeRotation().getDegrees() + distance);
        m_pidController.setTolerance(0.05);
    }

    @Override
    public void execute() {
        double errorAngle = m_swerve.getGyroscopeRotation().getDegrees();
        double newSpeed = m_pidController.calculate(errorAngle);

        newSpeed += Math.copySign(Constants.DRIVE.ROTATE_TO_TARGET_FEEDFORWARD, newSpeed);

        newSpeed = MathUtil.clamp(newSpeed, -Constants.DRIVE.ROTATE_MAXSPEED_RADIANS_PER_SECOND, Constants.DRIVE.ROTATE_MAXSPEED_RADIANS_PER_SECOND);
        m_swerve.drive(ChassisSpeeds.fromFieldRelativeSpeeds(0, 0, newSpeed, m_swerve.getGyroscopeRotation()));
        
        System.out.print("Speed: " + newSpeed);
        System.out.println("Setpoint: " + m_pidController.getSetpoint());
        System.out.println("Error: " + m_pidController.getPositionError());
    }

    @Override
    public boolean isFinished() {
        return m_pidController.atSetpoint();
    }

    @Override
    public void end(boolean isInterrupted) {
        m_swerve.drive(new ChassisSpeeds());
    }
}
