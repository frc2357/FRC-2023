package com.team2357.frc2023.commands.drive;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class SetCoastOnDisableCommand extends CommandBase{

    Timer m_timer;

    @Override
    public void initialize() {
        m_timer = new Timer();
        m_timer.start();
    }

    @Override
    public boolean isFinished() {
        return m_timer.hasElapsed(Constants.DRIVE.TIME_TO_COAST_SECONDS);
    }

    @Override
    public void end(boolean interrupted) {
        SwerveDriveSubsystem.getInstance().setCoastMode();
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }
}
