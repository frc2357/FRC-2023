package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToTargetCommand extends CommandBase {
    private int m_column;

    public TranslateToTargetCommand(int column) {
        m_column = column;
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().setClosedLoopEnabled(true);
        SwerveDriveSubsystem.getInstance().trackTarget(m_column);
    }

    @Override
    public boolean isFinished() {
        return SwerveDriveSubsystem.getInstance().isAtTarget();
    }

    @Override
    public void end(boolean isInterrupted) {
        SwerveDriveSubsystem.getInstance().setClosedLoopEnabled(false);
        SwerveDriveSubsystem.getInstance().stopTracking();
    }
}