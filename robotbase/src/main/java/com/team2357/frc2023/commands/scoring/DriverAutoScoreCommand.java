package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DriverAutoScoreCommand extends CommandBase {
    private Command m_scoreCommand;
    
    public DriverAutoScoreCommand() {
        m_scoreCommand = null;
    }

    @Override
    public void initialize() {
        int targetCol = Buttonboard.getInstance().getColValue();
        int targetRow = Buttonboard.getInstance().getRowValue();

        m_scoreCommand = Utility.getScoreCommand(targetRow, targetCol);
        m_scoreCommand.schedule();
    }

    @Override
    public boolean isFinished() {
        return m_scoreCommand.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
        m_scoreCommand.cancel();
    }
}
