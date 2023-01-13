package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class AutoBalanceCommand extends CommandLoggerBase {
    private double m_pitch;
    private double m_roll;
    
    public AutoBalanceCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    private void updateYPR() {
        m_pitch = SwerveDriveSubsystem.getInstance().getPitch();
        m_roll = SwerveDriveSubsystem.getInstance().getRoll();
    }

    @Override
    public void execute() {
        updateYPR();

        // Level        = -2.5 <= x <= 2.5
        // Fully tilted = -15 <= x <= 15

        if (-15 <= m_pitch && m_pitch <= 15) {
            SwerveDriveSubsystem.getInstance().drive(Constants.DRIVE.CHARGE_STATION_BALANCE_CONTROLLER.calculate(m_pitch, 0), 0, 0);
        }

        if (-15 <= m_roll && m_roll <= 15) {
            SwerveDriveSubsystem.getInstance().drive(0, Constants.DRIVE.CHARGE_STATION_BALANCE_CONTROLLER.calculate(m_roll, 0), 0);
        }

    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().drive(new ChassisSpeeds(0, 0, 0));
    }

    @Override
    public boolean isFinished() {
        updateYPR();
        return (-2.5 <= m_roll && m_roll <= 2.5) && (-2.5 <= m_pitch && m_pitch <= 2.5);
    }
}
