package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

import edu.wpi.first.wpilibj.DriverStation;

public class ZeroDriveCommand extends CommandLoggerBase {
    private double m_startMillis;
    
    public ZeroDriveCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        m_startMillis = System.currentTimeMillis();
    }

    @Override
    public boolean isFinished() {
        return System.currentTimeMillis() - m_startMillis >= Constants.DRIVE.WAIT_FOR_ZERO_TIME_MILLIS;
    }

    @Override
    public void end(boolean interrupted) {
        if (!SwerveDriveSubsystem.getInstance().checkEncodersSynced()) {
            DriverStation.reportWarning("Swerve drive appears to not be zeroed", false);
        }
    }
}
