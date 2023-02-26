package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.auto.TranslateToTargetCommandGroup;
import com.team2357.frc2023.subsystems.DualLimelightManagerSubsystem;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToColumnCommand extends CommandBase {
    private SwerveDriveSubsystem.COLUMN_SETPOINT m_column;
    
    public TranslateToColumnCommand(SwerveDriveSubsystem.COLUMN_SETPOINT column) {
        m_column = column;
    }

    @Override
    public void initialize() {
        DualLimelightManagerSubsystem.getInstance().setPrimary(m_column.primaryLimelight);
        new TranslateToTargetCommandGroup(m_column.setpoint).schedule();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}