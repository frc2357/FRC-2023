package com.team2357.frc2023.commands.util;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Runs the given command only if interrupted
 */
public class InterruptCommand extends CommandBase {
    private Command m_interruptCommand;

    public InterruptCommand(Command interruptCommand) {
        m_interruptCommand = interruptCommand;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            if (m_interruptCommand != null) {
                m_interruptCommand.schedule();
            }
        }
    }
}
