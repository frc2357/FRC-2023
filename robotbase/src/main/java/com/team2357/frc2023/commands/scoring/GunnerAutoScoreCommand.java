package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.controller.RumbleCommand;
import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class GunnerAutoScoreCommand extends CommandBase {
    private Command m_scoreCommand;
    private Command m_rumbleCommand;

    public GunnerAutoScoreCommand() {
        m_scoreCommand = null;
        m_rumbleCommand = new RumbleCommand(SwerveDriveControls.getInstance(),
                Constants.CONTROLLER.RUMBLE_TIMEOUT_SECONDS_ON_TELEOP_AUTO);
    }

    @Override
    public void initialize() {
        int targetCol = Buttonboard.getInstance().getColValue();
        int targetRow = Buttonboard.getInstance().getRowValue();
        Buttonboard.Gamepiece targetGamepiece = Buttonboard.getInstance().getGamepieceValue();

        m_scoreCommand = Utility.getScoreCommand(targetRow, targetCol);
        m_scoreCommand = new WaitCommand(0);
        m_scoreCommand.schedule();

        m_rumbleCommand.schedule();

        System.out.println("Auto line col: " + targetCol + " row: " + targetRow + " game: " + targetGamepiece);
    }

    @Override
    public boolean isFinished() {
        return m_scoreCommand.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
        m_scoreCommand.cancel();
        m_rumbleCommand.cancel();
    }
}
