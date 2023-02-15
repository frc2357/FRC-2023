package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class ZeroDifferentialDriveCommand extends CommandLoggerBase {
    private double m_startMillis;
    
    public ZeroDifferentialDriveCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().zeroDifferentialDrive();
        m_startMillis = System.currentTimeMillis();
    }

    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() - m_startMillis >= Constants.DRIVE.WAIT_FOR_DIFFERENTIAL_ZERO_TIME_MILLIS;
    }
}
