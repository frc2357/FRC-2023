package com.team2357.frc2023.commands.auto;

import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToTargetCommand extends CommandBase {
    private SwerveDriveSubsystem.COLUMN_TARGET m_targetColumn;

    private int m_aprilTagId;

    public TranslateToTargetCommand(SwerveDriveSubsystem.COLUMN_TARGET targetColumn) {
        this(targetColumn, -1);
    }

    public TranslateToTargetCommand(SwerveDriveSubsystem.COLUMN_TARGET targetColumn, int aprilTagId) {
        m_targetColumn = targetColumn;
        m_aprilTagId = -1;
        addRequirements(SwerveDriveSubsystem.getInstance());
    }

    @Override
    public void initialize() {
        SwerveDriveSubsystem.getInstance().trackTarget(m_targetColumn, m_aprilTagId);
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