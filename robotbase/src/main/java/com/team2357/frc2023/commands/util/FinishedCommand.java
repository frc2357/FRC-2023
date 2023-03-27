package com.team2357.frc2023.commands.util;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Runs the given command only if interrupted
 */
public class FinishedCommand extends CommandBase {
    private Command m_finishedCommand;
    private Command m_interruptCommand;

    public FinishedCommand(Command finishedCommand, Command interruptCommand) {
        m_finishedCommand = finishedCommand;
        m_interruptCommand = interruptCommand;
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            if (m_interruptCommand != null) {
                m_interruptCommand.schedule();
            }
        } else {
            if (m_finishedCommand != null) {
                m_finishedCommand.schedule();
            }
        }
    }
}
