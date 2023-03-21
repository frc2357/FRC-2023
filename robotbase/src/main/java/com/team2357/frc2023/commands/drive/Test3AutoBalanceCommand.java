package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.shuffleboard.ShuffleboardPIDTuner;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Test3AutoBalanceCommand extends CommandBase {

    // Use actual pid controller with d gain
    
    private ShuffleboardPIDTuner m_pidTuner;
    
    private SwerveDriveSubsystem m_swerve;
     
    private double prevAngle = Double.NaN;
    private double yaw, direction, angle, error, power;
    private PIDController m_pidController;
    
    public Test3AutoBalanceCommand() {
        m_pidController = Constants.DRIVE.BALANCE_PID_CONTROLLER;
        m_pidTuner = new ShuffleboardPIDTuner("Auto Balance", 0.1, 0.001, 0.0001, Constants.DRIVE.BALANCE_KP, Constants.DRIVE.BALANCE_KP, Constants.DRIVE.BALANCE_KP);
        m_swerve = SwerveDriveSubsystem.getInstance();
        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_swerve.zero();
        m_pidController.setSetpoint(Constants.DRIVE.BALANCE_LEVEL_DEGREES);
        prevAngle = m_swerve.getYaw(); 
    }

    @Override
    public void execute() {
        yaw = Math.abs(m_swerve.getYaw() % 360);
        
        angle = m_swerve.getTilt(yaw);
        direction = m_swerve.getDirection(yaw);

        if (angle <= Constants.DRIVE.BALANCE_FULL_TILT_DEGREES) {

            m_swerve.drive(-m_pidController.calculate(angle), 0, 0);

        }

        prevAngle = angle;

        if (m_pidTuner.arePIDsUpdated()) {
            updatePIDs();
        }
    }

    @Override
    public boolean isFinished() {
        return Math.abs(m_swerve.getTilt(m_swerve.getYaw())) < Constants.DRIVE.BALANCE_LEVEL_DEGREES;
    }
    
    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(0, 0, 0);
    }

    private void updatePIDs() {
        m_pidController.setP(m_pidTuner.getPValue());
        m_pidController.setI(m_pidTuner.getIValue());
        m_pidController.setD(m_pidTuner.getDValue());
    }
}
