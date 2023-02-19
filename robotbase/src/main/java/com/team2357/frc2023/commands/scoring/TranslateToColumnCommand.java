package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.auto.TranslateToTargetCommand;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToColumnCommand extends CommandBase {
    private SwerveDriveSubsystem.ColumnSetpoints m_column;
    
    public TranslateToColumnCommand(SwerveDriveSubsystem.ColumnSetpoints column) {
        m_column = column;
    }

    @Override
    public void initialize() {
        new TranslateToTargetCommand(m_column.setpoint).schedule();
    }
}