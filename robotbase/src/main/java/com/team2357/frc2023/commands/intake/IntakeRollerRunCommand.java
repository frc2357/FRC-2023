package com.team2357.frc2023.commands.intake;

import com.team2357.frc2023.subsystems.IntakeRollerSubsystem;
import com.team2357.lib.commands.CommandLoggerBase;

public class IntakeRollerRunCommand extends CommandLoggerBase {
    private double m_percentOutput;

    public IntakeRollerRunCommand() {
        this(0);
    }

    public IntakeRollerRunCommand(double percentOutput) {
        m_percentOutput = percentOutput;
        addRequirements(IntakeRollerSubsystem.getInstance());
    }

    @Override
    public void execute() {
        if (m_percentOutput == 0) {
            IntakeRollerSubsystem.getInstance().runIntake(false);
        } else {
            IntakeRollerSubsystem.getInstance().runIntake(m_percentOutput);
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end(boolean interrupted) {
        IntakeRollerSubsystem.getInstance().stopIntake();
    }
}
