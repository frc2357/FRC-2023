package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.commands.auto.TranslateToTargetCommand;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToColumnCommand extends CommandBase {
    private int m_column;
    
    /**
     * @param column Column to translate to (0 = left, 1 = middle, 2 = right)
     */
    public TranslateToColumnCommand(int column) {
        m_column = column;
    }

    @Override
    public void initialize() {
        new TranslateToTargetCommand(SwerveDriveSubsystem.getInstance().getXSetPoint(m_column)).schedule();
    }
}
