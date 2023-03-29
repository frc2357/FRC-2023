package com.team2357.frc2023.commands.scoring;

import com.team2357.frc2023.networktables.Buttonboard;
import com.team2357.frc2023.subsystems.SwerveDriveSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class HeartlandAutoScoreCommand extends CommandBase {
    private Command m_teleopCommand;

    private boolean m_hasScored;

    @Override
    public void initialize() {
        m_hasScored = false;

        int col = Buttonboard.getInstance().getColValue();
        int row = Buttonboard.getInstance().getRowValue();

        /*
        m_teleopCommand = SwerveDriveSubsystem.getAutoScoreCommands(row,col).andThen(() -> {
            m_hasScored = true;
        });
        */

        m_teleopCommand.schedule();
    }

    @Override
    public boolean isFinished() {
        return m_hasScored;
    }

    @Override
    public void end(boolean interrupted) {
        m_teleopCommand.cancel();
    }
}
