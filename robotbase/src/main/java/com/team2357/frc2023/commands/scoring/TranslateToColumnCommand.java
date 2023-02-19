package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.auto.TranslateToTargetCommandGroup;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToColumnCommand extends CommandBase {
    private SwerveDriveSubsystem.ColumnSetpoints m_column;
    
    public TranslateToColumnCommand(SwerveDriveSubsystem.ColumnSetpoints column) {
        m_column = column;
    }

    @Override
    public void initialize() {
        new TranslateToTargetCommandGroup(m_column.setpoint).schedule();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}