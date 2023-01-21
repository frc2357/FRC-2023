package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoBalanceCommand extends CommandLoggerBase {
    private double m_error, m_angle, m_power, m_direction;
    
    public AutoBalanceCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().zero();
    }

    @Override
    public void execute() {

        m_direction = Math.copySign(1, m_direction);

        m_angle = SwerveDriveSubsystem.getInstance().getRoll();

        m_error = Math.copySign(Constants.DRIVE.BALANCE_LEVEL_DEGREES + Math.abs(m_angle), m_angle);
        m_power = Math.min(Constants.DRIVE.BALANCE_KP * m_error, 1);

        if (Math.abs(m_power) > Constants.DRIVE.BALANCE_MAX_POWER) {
            m_power = Math.copySign(Constants.DRIVE.BALANCE_MAX_POWER, m_power);
        }

        SwerveDriveSubsystem.getInstance().drive(m_power, 0, 0);

        System.out.println("Angle: " + m_angle);
        System.out.println("Error: " + m_error);
        System.out.println("Power: " + m_power);


    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(new ChassisSpeeds(0, 0, 0));
    }

    @Override
    public boolean isFinished() {
        return SwerveDriveSubsystem.getInstance().isBalanced();
    }
}
