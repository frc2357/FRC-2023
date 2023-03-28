package com.team2357.frc2023.commands.util;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;

/**
 * Runs the given command only if interrupted
 */
public class ParallelInterruptCommandGroup extends CommandBase {
    private Command m_interruptCommand;
    private ParallelCommandGroup m_parallelCommand;

    public ParallelInterruptCommandGroup(Command interruptCommand, Command ... commands) {
        m_interruptCommand = interruptCommand;
        m_parallelCommand = new ParallelCommandGroup(commands);
    }

    @Override
    public void initialize() {
        m_parallelCommand.schedule();
    }

    @Override
    public boolean isFinished() {
        return m_parallelCommand.isFinished();
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            m_parallelCommand.cancel();
            if (m_interruptCommand != null) {
                m_interruptCommand.schedule();
            }
        }
    }
}
