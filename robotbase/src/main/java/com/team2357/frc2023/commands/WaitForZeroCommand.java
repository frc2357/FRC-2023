package com.team2357.frc2023.commands;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.wpilibj.AddressableLED;

public class WaitForZeroCommand extends CommandLoggerBase {
    private double m_startMillis;
    
    public WaitForZeroCommand() {
        m_startMillis = System.currentTimeMillis();
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() - m_startMillis >= Constants.DRIVE.WAIT_FOR_ZERO_TIME_MILLIS;
    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().checkEncodersSynced();
    }
}
