package com.team2357.frc2023.commands.scoring;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class TranslateToColumnCommand extends CommandBase {
    private int m_col;
    
    /**
     * @param col Column to translate to (0 = left, 1 = middle, 2 = right)
     */
    public TranslateToColumnCommand(int col) {
        m_col = col;
    }

    @Override
    public void initialize() {

    }
}
