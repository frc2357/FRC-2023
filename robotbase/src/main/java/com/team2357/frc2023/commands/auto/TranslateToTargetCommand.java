package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToTargetCommand extends CommandBase {
    public TranslateToTargetCommand() {
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().trackTarget();
        SwerveDriveSubsystem.getInstance().setClosedLoopEnabled(true);
    }

    @Override
    public boolean isFinished() {
        return SwerveDriveSubsystem.getInstance().isAtTarget();
    }

    @Override
    public void end(boolean isInterrupted) {
        SwerveDriveSubsystem.getInstance().stopTracking();
    }
}