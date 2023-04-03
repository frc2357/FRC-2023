package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.commands.controller.RumbleCommand;
import com.team2357.frc2023.controls.SwerveDriveControls;
import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.util.Utility;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;

public class DriverAutoScoreCommand extends CommandBase {
    private Command m_scoreCommand;

    public DriverAutoScoreCommand() {
        m_scoreCommand = null;
    }

    @Override
    public void initialize() {
        int targetCol = Buttonboard.getInstance().getColValue();
        int targetRow = Buttonboard.getInstance().getRowValue();
        Buttonboard.Gamepiece targetGamepiece = Buttonboard.getInstance().getGamepieceValue();

        m_scoreCommand = Utility.getScoreCommand(targetRow, targetCol, targetGamepiece);
        m_scoreCommand.schedule();

        new WaitCommand(Constants.CONTROLLER.RUMBLE_TIME_TO_START_AFTER_SCORE)
                .andThen(RumbleCommand.createRumbleCommand(SwerveDriveControls.getInstance(),
                        Constants.CONTROLLER.RUMBLE_TIMEOUT_SECONDS_ON_TELEOP_AUTO)).schedule();
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
