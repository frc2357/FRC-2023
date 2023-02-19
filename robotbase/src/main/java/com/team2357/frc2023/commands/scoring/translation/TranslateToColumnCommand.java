package com.team2357.frc2023.commands.scoring.translation;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToColumnCommand extends CommandBase {
    private int m_col;
    
    public TranslateToColumnCommand(int col) {
        m_col = col;
    }

    @Override
    public void initialize() {

    }
}
