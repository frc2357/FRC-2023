package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Test2AutoBalanceCommand extends CommandBase {

    // Stop driving if angle is changing too quickly
    
    private SwerveDriveSubsystem m_swerve;
     
    private double prevAngle = Double.NaN;
    private double yaw, direction, angle, error, power;
    
    public Test2AutoBalanceCommand() {
        m_swerve = SwerveDriveSubsystem.getInstance();
        addRequirements(m_swerve);
    }

    @Override
    public void initialize() {
        m_swerve.zero();
        prevAngle = m_swerve.getYaw(); 
    }

    @Override
    public void execute() {
        yaw = Math.abs(m_swerve.getYaw() % 360);
        
        angle = m_swerve.getTilt(yaw);
        direction = m_swerve.getDirection(yaw);

        if (angle <= Constants.DRIVE.BALANCE_FULL_TILT_DEGREES) {
            // original:
			// error = Math.copySign(Constants.DRIVE.BALANCE_LEVEL_DEGREES + Math.abs(angle), angle);
            error = Math.copySign(Math.abs(angle), angle);
            power = Math.min(Math.abs(Constants.DRIVE.BALANCE_KP * error), Constants.DRIVE.BALANCE_MAX_POWER);
            power = Math.copySign(power, error) * direction;

            if (Math.abs(prevAngle - angle) > Constants.DRIVE.STOP_DRIVING_TILT_DIFFERENCE) {
                m_swerve.drive(power, 0, 0);
            }

        }

        prevAngle = angle;
    }

    @Override
    public boolean isFinished() {
        return Math.abs(m_swerve.getTilt(m_swerve.getYaw())) < Constants.DRIVE.BALANCE_LEVEL_DEGREES;
    }
    
    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(0, 0, 0);
    }
}