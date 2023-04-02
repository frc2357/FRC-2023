package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.Constants;
import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeStallCommand extends CommandBase {
    private Command m_onStallCommand;
    private Command m_onInterruptCommand;
    private long m_stallStart;

    public IntakeStallCommand() {
        this(null, null);
    }

    public IntakeStallCommand(Command onStallCommand, Command onInterruptCommand) {
        m_onStallCommand = onStallCommand;
        m_onInterruptCommand = onInterruptCommand;
    }

    @Override
    public void initialize() {
        m_stallStart = 0;
    }

    @Override
    public boolean isFinished() {
        boolean isStalled =  IntakeRollerSubsystem.getInstance().isStalled(Constants.INTAKE_ROLLER.AUTO_INTAKE_CURRENT_LIMIT);

        if (m_stallStart == 0) {
            if (isStalled) {
                m_stallStart = System.currentTimeMillis();
            }
        } else {
            boolean confirmationTimePassed = m_stallStart + Constants.INTAKE_ROLLER.AUTO_INTAKE_CONFIRMATION_MILLIS < System.currentTimeMillis();

            if (isStalled && confirmationTimePassed) {
                return true;
            }

            if (!isStalled) {
                m_stallStart = 0;
            }
        }

        return false;
    }

    @Override
    public void end(boolean interrupted) {
        if (!interrupted) {
            if (m_onStallCommand != null) {
                m_onStallCommand.schedule();
            }
        } else {
            if (m_onInterruptCommand != null) {
                m_onInterruptCommand.schedule();
            }
        }
    }
}
