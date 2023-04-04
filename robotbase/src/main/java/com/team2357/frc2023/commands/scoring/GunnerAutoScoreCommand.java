package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.controller.RumbleCommand;
import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.networktables.Buttonboard;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SelectCommand;

public class GunnerAutoScoreCommand extends CommandBase {
    private SelectCommand m_scoreCommand;
    private Command m_rumbleCommand;

    int m_targetCol;
    int m_targetRow;

    public GunnerAutoScoreCommand() {
        m_scoreCommand = new AutoScoreSelector(() -> {return m_targetRow;}, () -> {return m_targetCol;}).getSelectCommand();
        m_rumbleCommand = new RumbleCommand(SwerveDriveControls.getInstance(),
                Constants.CONTROLLER.RUMBLE_TIMEOUT_SECONDS_ON_TELEOP_AUTO);
    }

    @Override
    public void initialize() {
        m_targetCol = Buttonboard.getInstance().getColValue();
        m_targetRow = Buttonboard.getInstance().getRowValue();

        m_scoreCommand.schedule();

        m_rumbleCommand.schedule();

        System.out.println("Auto line col: " + m_targetCol + " row: " + m_targetRow);
    }

    @Override
    public boolean isFinished() {
        return m_scoreCommand.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
        if(m_scoreCommand.isFinished()) {
            m_scoreCommand.cancel();
        }

        if(m_rumbleCommand.isFinished()) {
            m_rumbleCommand.cancel();
        }
    }
}
