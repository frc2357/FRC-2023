package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoBalanceCommand extends CommandLoggerBase {

    private double m_error, m_angle, m_power, m_yaw, m_direction;
    
    public AutoBalanceCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().zero();
    }

    @Override
    public void execute() {

        m_yaw = Math.abs(SwerveDriveSubsystem.getInstance().getYaw() % 360);

        if ((0 <= m_yaw && m_yaw < 45) || (315 <= m_yaw && m_yaw <= 360)) {
            m_direction = 1;
            m_angle = SwerveDriveSubsystem.getInstance().getRoll();
        } else if (45 <= m_yaw && m_yaw < 135) {
            m_direction = 1;
            m_angle = SwerveDriveSubsystem.getInstance().getPitch();
        } else if (135 <= m_yaw && m_yaw < 225) {
            m_direction = -1;
            m_angle = SwerveDriveSubsystem.getInstance().getRoll();
        } else if (225 <= m_yaw && m_yaw < 315) {
            m_direction = -1;
            m_angle = SwerveDriveSubsystem.getInstance().getPitch();
        }

        m_error = Math.copySign(Constants.DRIVE.BALANCE_LEVEL_DEGREES + Math.abs(m_angle), m_angle);
        m_power = Math.min(Math.abs(Constants.DRIVE.BALANCE_KP * m_error), Constants.DRIVE.BALANCE_MAX_POWER);
        m_power = Math.copySign(m_power, m_error);

        m_power *= m_direction;

        SwerveDriveSubsystem.getInstance().drive(m_power, 0, 0);

        // System.out.println("Yaw: " + m_yaw);
        // System.out.println("Angle: " + m_angle);
        // System.out.println("Error: " + m_error);
        // System.out.println("Power: " + m_power);
        // System.out.println("Direction: " + m_direction);

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
